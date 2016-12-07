/*
 * Copyright (c) 1997, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.awt;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog.ModalExclusionType;
import java.awt.EventQueue;
import java.awt.FocusTraversalPolicy;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.MenuComponent;
import java.awt.SplashScreen;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.net.URL;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import sun.awt.image.ByteArrayImageSource;
import sun.awt.image.FileImageSource;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.ToolkitImage;
import sun.awt.image.URLImageSource;

/**
 * In SkinJob, only static methods of this class are used.
 */
public final class SunToolkit {

  // 8014718: logging has been removed from SunToolkit

  /**
   * Special mask for the UngrabEvent events, in addition to the public masks defined in AWTEvent.
   * Should be used as the mask value for Toolkit.addAWTEventListener.
   */
  public static final int GRAB_EVENT_MASK = 0x80000000;

  /* XFree standard mention 24 buttons as maximum:
   * http://www.xfree86.org/current/mouse.4.html
   * We workaround systems supporting more than 24 buttons.
   * Otherwise, we have to use long type values as masks
   * which leads to API change.
   * InputEvent.BUTTON_DOWN_MASK may contain only 21 masks due to
   * the 4-bytes limit for the int type. (CR 6799099)
   * One more bit is reserved for FIRST_HIGH_BIT.
   */
  public static final int MAX_BUTTONS_SUPPORTED = 20;
  public static final int DEFAULT_WAIT_TIME = 10000;
  /* A variable defined for the convenience of JDK code */
  public static final String DESKTOPFONTHINTS = "awt.font.desktophints";
  /**
   * The AWT lock is typically only used on Unix platforms to synchronize access to Xlib, OpenGL,
   * etc.  However, these methods are implemented in SunToolkit so that they can be called from
   * shared code (e.g. from the OGL pipeline) or from the X11 pipeline regardless of whether
   * XToolkit or MToolkit is currently in use.  There are native macros (such as AWT_LOCK) defined
   * in awt.h, so if the implementation of these methods is changed, make sure it is compatible with
   * the native macros.
   * <p>
   * Note: The following methods (awtLock(), awtUnlock(), etc) should be used in place of:
   * synchronized (getAWTLock()) { ... }
   * <p>
   * By factoring these methods out specially, we are able to change the implementation of these
   * methods (e.g. use more advanced locking mechanisms) without impacting calling code.
   * <p>
   * Sample usage: private void doStuffWithXlib() { assert !SunToolkit.isAWTLockHeldByCurrentThread();
   * SunToolkit.awtLock(); try { ... XlibWrapper.XDoStuff(); } finally { SunToolkit.awtUnlock(); }
   * }
   */
  public static final ReentrantLock AWT_LOCK = new ReentrantLock();
  static final SoftCache imgCache = new SoftCache<URL, Image>();
  /* The key to put()/get() the PostEventQueue into/from the AppContext.
   */
  private static final String POST_EVENT_QUEUE_KEY = "PostEventQueue";
  private static final Condition AWT_LOCK_COND = AWT_LOCK.newCondition();
  // Maps from non-Component/MenuComponent to AppContext.
  // WeakHashMap<Component,AppContext>
  private static final Map<Object, AppContext> appContextMap
      = Collections.synchronizedMap(new WeakHashMap<Object, AppContext>());
  private static Locale startupLocale;
  private static DefaultMouseInfoPeer mPeer;
  private static ModalExclusionType DEFAULT_MODAL_EXCLUSION_TYPE;
  private static Boolean sunAwtDisableMixing;

  /* Load debug settings for native code */
  static {
    if (Boolean.valueOf(System.getProperty("sun.awt.nativedebug"))) {
      DebugSettings.init();
    }
  }

  private SunToolkit() {
  }

  /**
   * Creates and initializes EventQueue instance for the specified AppContext. Note that event queue
   * must be created from createNewAppContext() only in order to ensure that EventQueue constructor
   * obtains the correct AppContext.
   *
   * @param appContext AppContext to associate with the event queue
   */
  private static void initEQ(AppContext appContext) {
    EventQueue eventQueue;

    String eqName = System.getProperty("AWT.EventQueueClass", "java.awt.EventQueue");

    try {
      eventQueue = (EventQueue) Class.forName(eqName).newInstance();
    } catch (Exception e) {
      e.printStackTrace();
      System.err.println("Failed loading " + eqName + ": " + e);
      eventQueue = new EventQueue();
    }
    appContext.put(AppContext.EVENT_QUEUE_KEY, eventQueue);

    PostEventQueue postEventQueue = new PostEventQueue(eventQueue);
    appContext.put(POST_EVENT_QUEUE_KEY, postEventQueue);
  }

  /*
   * Create a new AppContext, along with its EventQueue, for a
   * new ThreadGroup.  Browser code, for example, would use this
   * method to create an AppContext & EventQueue for an Applet.
   */
  public static AppContext createNewAppContext() {
    ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
    return createNewAppContext(threadGroup);
  }

  static final AppContext createNewAppContext(ThreadGroup threadGroup) {
    // Create appContext before initialization of EventQueue, so all
    // the calls to AppContext.getAppContext() from EventQueue ctor
    // return correct values
    AppContext appContext = new AppContext(threadGroup);
    initEQ(appContext);

    return appContext;
  }

  /**
   * Sets the appContext field of target. If target is not a Component or MenuComponent, this
   * returns false.
   */
  private static boolean setAppContext(Object target, AppContext context) {
    if (target instanceof Component) {
      AWTAccessor.getComponentAccessor().
          setAppContext((Component) target, context);
    } else if (target instanceof MenuComponent) {
      AWTAccessor.getMenuComponentAccessor().
          setAppContext((MenuComponent) target, context);
    } else {
      return false;
    }
    return true;
  }

  /**
   * Returns the appContext field for target. If target is not a Component or MenuComponent this
   * returns null.
   */
  private static AppContext getAppContext(Object target) {
    if (target instanceof Component) {
      return AWTAccessor.getComponentAccessor().
          getAppContext((Component) target);
    } else if (target instanceof MenuComponent) {
      return AWTAccessor.getMenuComponentAccessor().
          getAppContext((MenuComponent) target);
    } else {
      return null;
    }
  }

  /*
   * Fetch the AppContext associated with the given target.
   * This can be used to determine things like which EventQueue
   * to use for posting events to a Component.  If the target is
   * null or the target can't be found, a null with be returned.
   */
  public static AppContext targetToAppContext(Object target) {
    if (target == null) {
      return null;
    }
    AppContext context = getAppContext(target);
    if (context == null) {
      // target is not a Component/MenuComponent, try the
      // appContextMap.
      context = appContextMap.get(target);
    }
    return context;
  }

  public static void checkAndSetPolicy(Container cont) {
    FocusTraversalPolicy defaultPolicy = KeyboardFocusManager.
        getCurrentKeyboardFocusManager().
        getDefaultFocusTraversalPolicy();

    cont.setFocusTraversalPolicy(defaultPolicy);
  }

  /*
   * Insert a mapping from target to AppContext, for later retrieval
   * via targetToAppContext() above.
   */
  public static void insertTargetMapping(Object target, AppContext appContext) {
    if (!setAppContext(target, appContext)) {
      // Target is not a Component/MenuComponent, use the private Map
      // instead.
      appContextMap.put(target, appContext);
    }
  }

  /*
   * Post an AWTEvent to the Java EventQueue, using the PostEventQueue
   * to avoid possibly calling client code (EventQueueSubclass.postEvent())
   * on the toolkit (AWT-Windows/AWT-Motif) thread.  This function should
   * not be called under another lock since it locks the EventQueue.
   * See bugids 4632918, 4526597.
   */
  public static void postEvent(AppContext appContext, AWTEvent event) {
    if (event == null) {
      throw new NullPointerException();
    }

    // All events posted via this method are system-generated.
    // Placing the following call here reduces considerably the
    // number of places throughout the toolkit that would
    // otherwise have to be modified to precisely identify
    // system-generated events.
    setSystemGenerated(event);
    AppContext eventContext = targetToAppContext(event.getSource());
    if (eventContext != null && !eventContext.equals(appContext)) {
      throw new RuntimeException("Event posted on wrong app context : " + event);
    }
    PostEventQueue postEventQueue = (PostEventQueue) appContext.get(POST_EVENT_QUEUE_KEY);
    if (postEventQueue != null) {
      postEventQueue.postEvent(event);
    }
  }

  /*
   * Flush any pending events which haven't been posted to the AWT
   * EventQueue yet.
   */
  public static void flushPendingEvents() {
    AppContext appContext = AppContext.getAppContext();
    flushPendingEvents(appContext);
  }

  /*
   * Flush the PostEventQueue for the right AppContext.
   * The default flushPendingEvents only flushes the thread-local context,
   * which is not always correct, c.f. 3746956
   */
  public static void flushPendingEvents(AppContext appContext) {
    PostEventQueue postEventQueue = (PostEventQueue) appContext.get(POST_EVENT_QUEUE_KEY);
    if (postEventQueue != null) {
      postEventQueue.flush();
    }
  }

  /*
   * Execute a chunk of code on the Java event handler thread for the
   * given target.  Does not wait for the execution to occur before
   * returning to the caller.
   */
  public static void executeOnEventHandlerThread(Object target, Runnable runnable) {
    executeOnEventHandlerThread(new PeerEvent(target, runnable, PeerEvent.PRIORITY_EVENT));
  }

  /*
   * Execute a chunk of code on the Java event handler thread for the
   * given target.  Does not wait for the execution to occur before
   * returning to the caller.
   */
  public static void executeOnEventHandlerThread(PeerEvent peerEvent) {
    postEvent(targetToAppContext(peerEvent.getSource()), peerEvent);
  }

  static Image getImageFromHash(Toolkit tk, String filename) {
    checkPermissions(filename);
    synchronized (imgCache) {
      Image img = (Image) imgCache.get(filename);
      if (img == null) {
        try {
          img = tk.createImage(new FileImageSource(filename));
          imgCache.put(filename, img);
        } catch (Exception e) {
        }
      }
      return img;
    }
  }

  private static int getRVSize(int size) {
    return size == -1 ? -1 : 2 * size;
  }

  private static ToolkitImage getResolutionVariant(Image image) {
    if (image instanceof MultiResolutionToolkitImage) {
      Image resolutionVariant = ((MultiResolutionToolkitImage) image).
          getResolutionVariant();
      if (resolutionVariant instanceof ToolkitImage) {
        return (ToolkitImage) resolutionVariant;
      }
    }
    return null;
  }

  private static void checkPermissions(String filename) {
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      security.checkRead(filename);
    }
  }

  // Package private implementation
  static EventQueue getSystemEventQueueImplPP() {
    return getSystemEventQueueImplPP(AppContext.getAppContext());
  }

  public static EventQueue getSystemEventQueueImplPP(AppContext appContext) {
    return (EventQueue) appContext.get(AppContext.EVENT_QUEUE_KEY);
  }

  /**
   * Returns the locale in which the runtime was started.
   */
  public static Locale getStartupLocale() {
    if (startupLocale == null) {
      String language, region, country, variant;
      language = System.getProperty("user.language", "en");
      // for compatibility, check for old user.region property
      region = System.getProperty("user.region");
      if (region != null) {
        // region can be of form country, country_variant, or _variant
        int i = region.indexOf('_');
        if (i >= 0) {
          country = region.substring(0, i);
          variant = region.substring(i + 1);
        } else {
          country = region;
          variant = "";
        }
      } else {
        country = System.getProperty("user.country", "");
        variant = System.getProperty("user.variant", "");
      }
      startupLocale = new Locale(language, country, variant);
    }
    return startupLocale;
  }

  /*
   * Returns whether the specified window is blocked by modal dialogs.
   * If the modal exclusion API isn't supported by the current toolkit,
   * it returns false for all windows.
   *
   * @param window Window to test for modal exclusion
   *
   * @return true if the window is modal excluded, false otherwise. If
   * the modal exclusion isn't supported by the current Toolkit, false
   * is returned
   *
   * @see sun.awt.SunToolkit#isModalExcludedSupported
   * @see sun.awt.SunToolkit#setModalExcluded(java.awt.Window)
   *
   * @since 1.5
   */
  public static boolean isModalExcluded(Window window) {
    if (DEFAULT_MODAL_EXCLUSION_TYPE == null) {
      DEFAULT_MODAL_EXCLUSION_TYPE = ModalExclusionType.APPLICATION_EXCLUDE;
    }
    return window.getModalExclusionType().compareTo(DEFAULT_MODAL_EXCLUSION_TYPE) >= 0;
  }

  /**
   * Locates the splash screen library in a platform dependent way and closes the splash screen.
   * Should be invoked on first top-level frame display.
   *
   * @see SplashScreen
   * @since 1.6
   */
  public static void closeSplashScreen() {
    SplashScreen.getSplashScreen().close();
  }

  /**
   * Returns the {@code Window} ancestor of the component {@code comp}.
   *
   * @return Window ancestor of the component or component by itself if it is Window; null, if
   * component is not a part of window hierarchy
   */
  public static Window getContainingWindow(Component comp) {
    while (comp != null && !(comp instanceof Window)) {
      comp = comp.getParent();
    }
    return (Window) comp;
  }

  /**
   * Returns the value of "sun.awt.disableMixing" property. Default value is {@code false}.
   */
  public static synchronized boolean getSunAwtDisableMixing() {
    if (sunAwtDisableMixing == null) {
      sunAwtDisableMixing = Boolean.valueOf(System.getProperty("sun.awt.disableMixing"));
    }
    return sunAwtDisableMixing;
  }

  /**
   * Checks that the given object implements/extends the given interface/class.
   * <p>
   * Note that using the instanceof operator causes a class to be loaded. Using this method doesn't
   * load a class and it can be used instead of the instanceof operator for performance reasons.
   *
   * @param obj Object to be checked
   * @param type The name of the interface/class. Must be fully-qualified interface/class name.
   * @return true, if this object implements/extends the given interface/class, false, otherwise, or
   * if obj or type is null
   */
  public static boolean isInstanceOf(Object obj, String type) {
    if (obj == null) {
      return false;
    }
    return type != null && isInstanceOf(obj.getClass(), type);
  }

  private static boolean isInstanceOf(Class<?> cls, String type) {
    if (cls == null) {
      return false;
    }

    if (cls.getName().equals(type)) {
      return true;
    }

    for (Class<?> c : cls.getInterfaces()) {
      if (c.getName().equals(type)) {
        return true;
      }
    }
    return isInstanceOf(cls.getSuperclass(), type);
  }

  public static void setSystemGenerated(AWTEvent e) {
    AWTAccessor.getAWTEventAccessor().setSystemGenerated(e);
  }

  private static int checkResolutionVariant(Image img, int w, int h, ImageObserver o) {
    ToolkitImage rvImage = getResolutionVariant(img);
    int rvw = getRVSize(w);
    int rvh = getRVSize(h);
    // Ignore the resolution variant in case of error
    return rvImage == null || rvImage.hasError() ? 0xFFFF
        : checkImage(rvImage, rvw, rvh, MultiResolutionToolkitImage.
            getResolutionVariantObserver(img, o, true));
  }

  private static boolean prepareResolutionVariant(Image img, int w, int h, ImageObserver o) {

    ToolkitImage rvImage = getResolutionVariant(img);
    int rvw = getRVSize(w);
    int rvh = getRVSize(h);
    // Ignore the resolution variant in case of error
    return rvImage == null || rvImage.hasError() || prepareImage(rvImage,
        rvw, rvh, MultiResolutionToolkitImage.getResolutionVariantObserver(img, o, true));
  }

  public static Image getImage(String filename) {
    return getImageFromHash(Toolkit.getDefaultToolkit(), filename);
  }

  public static Image getImage(URL url) {
    return getImageFromHash(Toolkit.getDefaultToolkit(), url.toString());
  }

  public static Image createImage(String filename) {
    checkPermissions(filename);
    return createImage(new FileImageSource(filename));
  }

  public static Image createImage(URL url) {
    return createImage(new URLImageSource(url));
  }

  public static boolean prepareImage(Image img, int w, int h, ImageObserver o) {
    if (w == 0 || h == 0) {
      return true;
    }

    // Must be a ToolkitImage
    if (!(img instanceof ToolkitImage)) {
      return true;
    }

    ToolkitImage tkimg = (ToolkitImage) img;
    if (tkimg.hasError()) {
      if (o != null) {
        o.imageUpdate(img, ImageObserver.ERROR | ImageObserver.ABORT, -1, -1, -1, -1);
      }
      return false;
    }
    ImageRepresentation ir = tkimg.getImageRep();
    return ir.prepare(o) && prepareResolutionVariant(img, w, h, o);
  }

  public static int checkImage(Image img, int w, int h, ImageObserver o) {
    if (!(img instanceof ToolkitImage)) {
      return ImageObserver.ALLBITS;
    }

    ToolkitImage tkimg = (ToolkitImage) img;
    int repbits;
    repbits = w == 0 || h == 0 ? ImageObserver.ALLBITS : tkimg.getImageRep().check(o);
    return (tkimg.check(o) | repbits) & checkResolutionVariant(img, w, h, o);
  }

  public static Image createImage(ImageProducer producer) {
    return new ToolkitImage(producer);
  }

  public static Image createImage(byte[] data, int offset, int length) {
    return createImage(new ByteArrayImageSource(data, offset, length));
  }

  /**
   * PostEventQueue is a Thread that runs in the same AppContext as the Java EventQueue.  It is a
   * queue of AWTEvents to be posted to the Java EventQueue.  The toolkit Thread
   * (AWT-Windows/AWT-Motif) posts events to this queue, which then calls EventQueue.postEvent().
   * <p>
   * We do this because EventQueue.postEvent() may be overridden by client code, and we mustn't ever
   * call client code from the toolkit thread.
   */
  static class PostEventQueue {
    private final EventQueue eventQueue;
    private EventQueueItem queueHead;
    private EventQueueItem queueTail;
    private Thread flushThread;

    PostEventQueue(EventQueue eq) {
      eventQueue = eq;
    }

    static void wakeupEventQueue(EventQueue q, boolean isShutdown) {
      AWTAccessor.getEventQueueAccessor().wakeup(q, isShutdown);
    }

    /*
     * Continually post pending AWTEvents to the Java EventQueue. The method
     * is synchronized to ensure the flush is completed before a new event
     * can be posted to this queue.
     *
     * 7177040: The method couldn't be wholly synchronized because of calls
     * of EventQueue.postEvent() that uses pushPopLock, otherwise it could
     * potentially lead to deadlock
     */
    public void flush() {

      Thread newThread = Thread.currentThread();

      try {
        EventQueueItem tempQueue;
        synchronized (this) {
          // Avoid method recursion
          if (newThread == flushThread) {
            return;
          }
          // Wait for other threads' flushing
          while (flushThread != null) {
            wait();
          }
          // Skip everything if queue is empty
          if (queueHead == null) {
            return;
          }
          // Remember flushing thread
          flushThread = newThread;

          tempQueue = queueHead;
          queueHead = queueTail = null;
        }
        try {
          while (tempQueue != null) {
            eventQueue.postEvent(tempQueue.event);
            tempQueue = tempQueue.next;
          }
        } finally {
          // Only the flushing thread can get here
          synchronized (this) {
            // Forget flushing thread, inform other pending threads
            flushThread = null;
            notifyAll();
          }
        }
      } catch (InterruptedException e) {
        // Couldn't allow exception go up, so at least recover the flag
        newThread.interrupt();
      }
    }

    /*
     * Enqueue an AWTEvent to be posted to the Java EventQueue.
     */
    void postEvent(AWTEvent event) {
      EventQueueItem item = new EventQueueItem(event);

      synchronized (this) {
        if (queueHead == null) {
          queueHead = queueTail = item;
        } else {
          queueTail.next = item;
          queueTail = item;
        }
      }
      wakeupEventQueue(eventQueue, event.getSource() == AWTAutoShutdown.getInstance());
    }
  } // class PostEventQueue
} // class SunToolkit

