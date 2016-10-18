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

import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VRGB;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.DefaultKeyboardFocusManager;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.KeyboardFocusManager;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuComponent;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.SkinJob;
import java.awt.SystemTray;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.KeyboardFocusManagerPeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.MouseInfoPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.RobotPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.SystemTrayPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.TrayIconPeer;
import java.awt.peer.WindowPeer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import sun.awt.AWTAccessor.SequencedEventAccessor;
import sun.awt.im.InputContext;
import sun.awt.im.SimpleInputMethodWindow;
import sun.awt.image.ByteArrayImageSource;
import sun.awt.image.FileImageSource;
import sun.awt.image.ImageRepresentation;
import sun.awt.image.MultiResolutionToolkitImage;
import sun.awt.image.ToolkitImage;
import sun.awt.image.URLImageSource;
import sun.font.FontDesignMetrics;

public abstract class SunToolkit extends Toolkit
    implements WindowClosingSupport, WindowClosingListener, ComponentFactory, InputMethodSupport,
    KeyboardFocusManagerPeerProvider {

  // 8014718: logging has been removed from SunToolkit

  /**
   * Special mask for the UngrabEvent events, in addition to the
   * public masks defined in AWTEvent.  Should be used as the mask
   * value for Toolkit.addAWTEventListener.
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
  static final SoftCache imgCache = new SoftCache<URL, Image>();
  /* The key to put()/get() the PostEventQueue into/from the AppContext.
   */
  private static final String POST_EVENT_QUEUE_KEY = "PostEventQueue";
  /**
   * The AWT lock is typically only used on Unix platforms to synchronize
   * access to Xlib, OpenGL, etc.  However, these methods are implemented
   * in SunToolkit so that they can be called from shared code (e.g.
   * from the OGL pipeline) or from the X11 pipeline regardless of whether
   * XToolkit or MToolkit is currently in use.  There are native macros
   * (such as AWT_LOCK) defined in awt.h, so if the implementation of these
   * methods is changed, make sure it is compatible with the native macros.
   * <p>
   * Note: The following methods (awtLock(), awtUnlock(), etc) should be
   * used in place of:
   * synchronized (getAWTLock()) {
   * ...
   * }
   * <p>
   * By factoring these methods out specially, we are able to change the
   * implementation of these methods (e.g. use more advanced locking
   * mechanisms) without impacting calling code.
   * <p>
   * Sample usage:
   * private void doStuffWithXlib() {
   * assert !SunToolkit.isAWTLockHeldByCurrentThread();
   * SunToolkit.awtLock();
   * try {
   * ...
   * XlibWrapper.XDoStuff();
   * } finally {
   * SunToolkit.awtUnlock();
   * }
   * }
   */

  public static final ReentrantLock AWT_LOCK = new ReentrantLock();
  private static final Condition AWT_LOCK_COND = AWT_LOCK.newCondition();
  // Maps from non-Component/MenuComponent to AppContext.
  // WeakHashMap<Component,AppContext>
  private static final Map<Object, AppContext> appContextMap
      = Collections.synchronizedMap(new WeakHashMap<Object, AppContext>());
  private static final int MAX_ITERS = 20;
  private static final int MIN_ITERS = 0;
  private static final int MINIMAL_EDELAY = 0;
  private static final Object DEACTIVATION_TIMES_MAP_KEY = new Object();
  /**
   * Number of buttons.
   * By default it's taken from the system. If system value does not
   * fit into int type range, use our own MAX_BUTTONS_SUPPORT value.
   */
  protected static int numberOfButtons;
  private static Locale startupLocale;
  private static DefaultMouseInfoPeer mPeer;
  private static ModalExclusionType DEFAULT_MODAL_EXCLUSION_TYPE;
  private static boolean checkedSystemAAFontSettings;
  private static boolean useSystemAAFontSettings;
  private static boolean lastExtraCondition = true;
  private static RenderingHints desktopFontHints;
  private static Boolean sunAwtDisableMixing;

  /* Load debug settings for native code */
  static {
    if (Boolean.valueOf(System.getProperty("sun.awt.nativedebug"))) {
      DebugSettings.init();
    }
  }

  final Object waitLock = "Wait Lock";
  private final ModalityListenerList modalityListeners = new ModalityListenerList();
  // Support for window closing event notifications
  private transient WindowClosingListener windowClosingListener;
  boolean eventDispatched;
  boolean queueEmpty;

  public SunToolkit() {
  }

  /**
   * Creates and initializes EventQueue instance for the specified
   * AppContext.
   * Note that event queue must be created from createNewAppContext()
   * only in order to ensure that EventQueue constructor obtains
   * the correct AppContext.
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
   * Sets the appContext field of target. If target is not a Component or
   * MenuComponent, this returns false.
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
   * Returns the appContext field for target. If target is not a
   * Component or MenuComponent this returns null.
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

  private static FocusTraversalPolicy createLayoutPolicy() {
    FocusTraversalPolicy policy = null;
    try {
      Class<?> layoutPolicyClass = Class.forName("javax.swing.LayoutFocusTraversalPolicy");
      policy = (FocusTraversalPolicy) layoutPolicyClass.newInstance();
    } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
      assert false;
    }

    return policy;
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

    SequencedEventAccessor sea = AWTAccessor.getSequencedEventAccessor();
    if (sea != null && sea.isSequencedEvent(event)) {
      AWTEvent nested = sea.getNested(event);
      if (nested.getID() == WindowEvent.WINDOW_LOST_FOCUS && nested instanceof TimedWindowEvent) {
        TimedWindowEvent twe = (TimedWindowEvent) nested;
        ((SunToolkit) Toolkit.getDefaultToolkit()).
            setWindowDeactivationTime((Window) twe.getSource(), twe.getWhen());
      }
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

  static Image getImageFromHash(Toolkit tk, URL url) {
    synchronized (imgCache) {
      Image img = (Image) imgCache.get(url);
      if (img == null) {
        try {
          img = tk.createImage(new URLImageSource(url));
          imgCache.put(url, img);
        } catch (Exception e) {
        }
      }
      return img;
    }
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

  /**
   * Scans {@code imageList} for best-looking image of specified dimensions.
   * Image can be scaled and/or padded with transparency.
   */
  public static BufferedImage getScaledIconImage(
      java.util.List<Image> imageList, int width, int height) {
    if (width == 0 || height == 0) {
      return null;
    }
    Image bestImage = null;
    int bestWidth = 0;
    int bestHeight = 0;
    double bestSimilarity = 3; //Impossibly high value
    double bestScaleFactor = 0;
    for (Image im : imageList) {
      //Iterate imageList looking for best matching image.
      //'Similarity' measure is defined as good scale factor and small insets.
      //best possible similarity is 0 (no scale, no insets).
      //It's found while the experiments that good-looking result is achieved
      //with scale factors x1, x3/4, x2/3, xN, x1/N.
      if (im == null) {
        continue;
      }
      if (im instanceof ToolkitImage) {
        ImageRepresentation ir = ((ToolkitImage) im).getImageRep();
        ir.reconstruct(ImageObserver.ALLBITS);
      }
      int iw;
      int ih;
      try {
        iw = im.getWidth(null);
        ih = im.getHeight(null);
      } catch (Exception e) {
        continue;
      }
      if (iw > 0 && ih > 0) {
        //Calc scale factor
        double scaleFactor = Math.min((double) width / (double) iw, (double) height / (double) ih);
        //Calculate scaled image dimensions
        //adjusting scale factor to nearest "good" value
        int adjw;
        int adjh;
        double scaleMeasure; //0 - best (no) scale, 1 - impossibly bad
        if (scaleFactor >= 2) {
          //Need to enlarge image more than twice
          //Round down scale factor to multiply by integer value
          scaleFactor = Math.floor(scaleFactor);
          adjw = iw * (int) scaleFactor;
          adjh = ih * (int) scaleFactor;
          scaleMeasure = 1.0 - 0.5 / scaleFactor;
        } else if (scaleFactor >= 1) {
          //Don't scale
          scaleFactor = 1.0;
          adjw = iw;
          adjh = ih;
          scaleMeasure = 0;
        } else if (scaleFactor >= 0.75) {
          //Multiply by 3/4
          scaleFactor = 0.75;
          adjw = iw * 3 / 4;
          adjh = ih * 3 / 4;
          scaleMeasure = 0.3;
        } else if (scaleFactor >= 0.6666) {
          //Multiply by 2/3
          scaleFactor = 0.6666;
          adjw = (iw << 1) / 3;
          adjh = (ih << 1) / 3;
          scaleMeasure = 0.33;
        } else {
          //Multiply size by 1/scaleDivider
          //where scaleDivider is minimum possible integer
          //larger than 1/scaleFactor
          double scaleDivider = Math.ceil(1.0 / scaleFactor);
          scaleFactor = 1.0 / scaleDivider;
          adjw = (int) Math.round((double) iw / scaleDivider);
          adjh = (int) Math.round((double) ih / scaleDivider);
          scaleMeasure = 1.0 - 1.0 / scaleDivider;
        }
        double similarity = ((double) width - (double) adjw) / (double) width +
            ((double) height - (double) adjh) / (double) height + //Large padding is bad
            scaleMeasure; //Large rescale is bad
        if (similarity < bestSimilarity) {
          bestSimilarity = similarity;
          bestImage = im;
          bestWidth = adjw;
          bestHeight = adjh;
        }
        if (similarity == 0) {
          break;
        }
      }
    }
    if (bestImage == null) {
      //No images were found, possibly all are broken
      return null;
    }
    BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g = bimage.createGraphics();
    g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    try {
      int x = (width - bestWidth) / 2;
      int y = (height - bestHeight) / 2;
      g.drawImage(bestImage, x, y, bestWidth, bestHeight, null);
    } finally {
      g.dispose();
    }
    return bimage;
  }

  public static DataBufferInt getScaledIconData(
      java.util.List<Image> imageList, int width, int height) {
    BufferedImage bimage = getScaledIconImage(imageList, width, height);
    if (bimage == null) {
      return null;
    }
    Raster raster = bimage.getRaster();
    DataBuffer buffer = raster.getDataBuffer();
    return (DataBufferInt) buffer;
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
   * Locates the splash screen library in a platform dependent way and closes
   * the splash screen. Should be invoked on first top-level frame display.
   *
   * @see java.awt.SplashScreen
   * @since 1.6
   */
  public static void closeSplashScreen() {
    // TODO: Native in OpenJDK AWT
  }

  /* Since Swing is the reason for this "extra condition" logic its
   * worth documenting it in some detail.
   * First, a goal is for Swing and applications to both retrieve and
   * use the same desktop property value so that there is complete
   * consistency between the settings used by JDK's Swing implementation
   * and 3rd party custom Swing components, custom L&Fs and any general
   * text rendering that wants to be consistent with these.
   * But by default on Solaris & Linux Swing will not use AA text over
   * remote X11 display (unless Xrender can be used which is TBD and may not
   * always be available anyway) as that is a noticeable performance hit.
   * So there needs to be a way to express that extra condition so that
   * it is seen by all clients of the desktop property API.
   * If this were the only condition it could be handled here as it would
   * be the same for any L&F and could reasonably be considered to be
   * a static behaviour of those systems.
   * But GTK currently has an additional test based on locale which is
   * not applied by Metal. So mixing GTK in a few locales with Metal
   * would mean the last one wins.
   * This could be stored per-app context which would work
   * for different applets, but wouldn't help for a single application
   * using GTK and some other L&F concurrently.
   * But it is expected this will be addressed within GTK and the font
   * system so is a temporary and somewhat unlikely harmless corner case.
   */
  public static void setAAFontSettingsCondition(boolean extraCondition) {
    if (extraCondition != lastExtraCondition) {
      lastExtraCondition = extraCondition;
      if (checkedSystemAAFontSettings) {
                /* Someone already asked for this info, under a different
                 * condition.
                 * We'll force re-evaluation instead of replicating the
                 * logic, then notify any listeners of any change.
                 */
        checkedSystemAAFontSettings = false;
        Toolkit tk = Toolkit.getDefaultToolkit();
        if (tk instanceof SunToolkit) {
          ((SunToolkit) tk).fireDesktopFontPropertyChanges();
        }
      }
    }
  }

  /* "false", "off", ""default" aren't explicitly tested, they
   * just fall through to produce a null return which all are equated to
   * "false".
   */
  private static RenderingHints getDesktopAAHintsByName(String hintname) {
    Object aaHint = null;
    hintname = hintname.toLowerCase(Locale.ENGLISH);
    if ("on".equals(hintname)) {
      aaHint = VALUE_TEXT_ANTIALIAS_ON;
    } else if ("gasp".equals(hintname)) {
      aaHint = VALUE_TEXT_ANTIALIAS_GASP;
    } else if ("lcd".equals(hintname) || "lcd_hrgb".equals(hintname)) {
      aaHint = VALUE_TEXT_ANTIALIAS_LCD_HRGB;
    } else if ("lcd_hbgr".equals(hintname)) {
      aaHint = VALUE_TEXT_ANTIALIAS_LCD_HBGR;
    } else if ("lcd_vrgb".equals(hintname)) {
      aaHint = VALUE_TEXT_ANTIALIAS_LCD_VRGB;
    } else if ("lcd_vbgr".equals(hintname)) {
      aaHint = VALUE_TEXT_ANTIALIAS_LCD_VBGR;
    }
    if (aaHint != null) {
      RenderingHints map = new RenderingHints(null);
      map.put(KEY_TEXT_ANTIALIASING, aaHint);
      return map;
    } else {
      return null;
    }
  }

  /* This method determines whether to use the system font settings,
   * or ignore them if a L&F has specified they should be ignored, or
   * to override both of these with a system property specified value.
   * If the toolkit isn't a SunToolkit, (eg may be headless) then that
   * system property isn't applied as desktop properties are considered
   * to be inapplicable in that case. In that headless case although
   * this method will return "true" the toolkit will return a null map.
   */
  private static boolean useSystemAAFontSettings() {
    if (!checkedSystemAAFontSettings) {
      useSystemAAFontSettings = true; /* initially set this true */
      String systemAAFonts = null;
      Toolkit tk = Toolkit.getDefaultToolkit();
      if (tk instanceof SunToolkit) {
        systemAAFonts = System.getProperty("awt.useSystemAAFontSettings");
      }
      if (systemAAFonts != null) {
        useSystemAAFontSettings = Boolean.valueOf(systemAAFonts);
                /* If it is anything other than "true", then it may be
                 * a hint name , or it may be "off, "default", etc.
                 */
        if (!useSystemAAFontSettings) {
          desktopFontHints = getDesktopAAHintsByName(systemAAFonts);
        }
      }
            /* If its still true, apply the extra condition */
      if (useSystemAAFontSettings) {
        useSystemAAFontSettings = lastExtraCondition;
      }
      checkedSystemAAFontSettings = true;
    }
    return useSystemAAFontSettings;
  }

  /* Subclass desktop property loading methods call this which
   * in turn calls the appropriate subclass implementation of
   * getDesktopAAHints() when system settings are being used.
   * Its public rather than protected because subclasses may delegate
   * to a helper class.
   */
  public static RenderingHints getDesktopFontHints() {
    if (useSystemAAFontSettings()) {
      Toolkit tk = Toolkit.getDefaultToolkit();
      if (tk instanceof SunToolkit) {
        Object map = ((SunToolkit) tk).getDesktopAAHints();
        return (RenderingHints) map;
      } else { /* Headless Toolkit */
        return null;
      }
    } else if (desktopFontHints != null) {
            /* cloning not necessary as the return value is cloned later, but
             * its harmless.
             */
      return (RenderingHints) desktopFontHints.clone();
    } else {
      return null;
    }
  }

  /*
   * consumeNextKeyTyped() method is not currently used,
   * however Swing could use it in the future.
   */
  public static synchronized void consumeNextKeyTyped(KeyEvent keyEvent) {
    try {
      AWTAccessor
          .getDefaultKeyboardFocusManagerAccessor()
          .consumeNextKeyTyped((DefaultKeyboardFocusManager) KeyboardFocusManager.
              getCurrentKeyboardFocusManager(), keyEvent);
    } catch (ClassCastException cce) {
      cce.printStackTrace();
    }
  }

  /**
   * Returns the {@code Window} ancestor of the component {@code comp}.
   *
   * @return Window ancestor of the component or component by itself if it is Window;
   * null, if component is not a part of window hierarchy
   */
  public static Window getContainingWindow(Component comp) {
    while (comp != null && !(comp instanceof Window)) {
      comp = comp.getParent();
    }
    return (Window) comp;
  }

  /**
   * Returns the value of "sun.awt.disableMixing" property. Default
   * value is {@code false}.
   */
  public static synchronized boolean getSunAwtDisableMixing() {
    if (sunAwtDisableMixing == null) {
      sunAwtDisableMixing = Boolean.valueOf(System.getProperty("sun.awt.disableMixing"));
    }
    return sunAwtDisableMixing;
  }

  /**
   * Returns whether or not a containing top level window for the passed
   * component is
   * {@link WindowTranslucency#PERPIXEL_TRANSLUCENT PERPIXEL_TRANSLUCENT}.
   *
   * @param c a Component which toplevel's to check
   * @return {@code true}  if the passed component is not null and has a
   * containing toplevel window which is opaque (so per-pixel translucency
   * is not enabled), {@code false} otherwise
   * @see WindowTranslucency#PERPIXEL_TRANSLUCENT
   */
  public static boolean isContainingTopLevelOpaque(Component c) {
    Window w = getContainingWindow(c);
    return w != null && w.isOpaque();
  }

  /**
   * Returns whether or not a containing top level window for the passed
   * component is
   * {@link WindowTranslucency#TRANSLUCENT TRANSLUCENT}.
   *
   * @param c a Component which toplevel's to check
   * @return {@code true} if the passed component is not null and has a
   * containing toplevel window which has opacity less than
   * 1.0f (which means that it is translucent), {@code false} otherwise
   * @see WindowTranslucency#TRANSLUCENT
   */
  public static boolean isContainingTopLevelTranslucent(Component c) {
    Window w = getContainingWindow(c);
    return w != null && w.getOpacity() < 1.0f;
  }

  /**
   * Checks that the given object implements/extends the given
   * interface/class.
   * <p>
   * Note that using the instanceof operator causes a class to be loaded.
   * Using this method doesn't load a class and it can be used instead of
   * the instanceof operator for performance reasons.
   *
   * @param obj  Object to be checked
   * @param type The name of the interface/class. Must be
   *             fully-qualified interface/class name.
   * @return true, if this object implements/extends the given
   * interface/class, false, otherwise, or if obj or type is null
   */
  public static boolean isInstanceOf(Object obj, String type) {
    if (obj == null) {
      return false;
    }
    if (type == null) {
      return false;
    }

    return isInstanceOf(obj.getClass(), type);
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

  protected static LightweightFrame getLightweightFrame(Component c) {
    for (; c != null; c = c.getParent()) {
      if (c instanceof LightweightFrame) {
        return (LightweightFrame) c;
      }
      if (c instanceof Window) {
        // Don't traverse owner windows
        return null;
      }
    }
    return null;
  }

  public static void setSystemGenerated(AWTEvent e) {
    AWTAccessor.getAWTEventAccessor().setSystemGenerated(e);
  }

  public boolean useBufferPerWindow() {
    return false;
  }

  public abstract FramePeer createLightweightFrame(LightweightFrame target)
      throws HeadlessException;

  public abstract TrayIconPeer createTrayIcon(TrayIcon target)
      throws HeadlessException, AWTException;

  public abstract SystemTrayPeer createSystemTray(SystemTray target);

  public abstract boolean isTraySupported();

  @Override
  public abstract RobotPeer createRobot(Robot target, GraphicsDevice screen) throws AWTException;

  @Override
  public abstract KeyboardFocusManagerPeer getKeyboardFocusManagerPeer() throws HeadlessException;

  protected abstract int getScreenWidth();

  protected abstract int getScreenHeight();

  private int checkResolutionVariant(Image img, int w, int h, ImageObserver o) {
    ToolkitImage rvImage = getResolutionVariant(img);
    int rvw = getRVSize(w);
    int rvh = getRVSize(h);
    // Ignore the resolution variant in case of error
    return rvImage == null || rvImage.hasError() ? 0xFFFF
        : checkImage(rvImage, rvw, rvh, MultiResolutionToolkitImage.
            getResolutionVariantObserver(img, o, true));
  }

  private boolean prepareResolutionVariant(Image img, int w, int h, ImageObserver o) {

    ToolkitImage rvImage = getResolutionVariant(img);
    int rvw = getRVSize(w);
    int rvh = getRVSize(h);
    // Ignore the resolution variant in case of error
    return rvImage == null || rvImage.hasError() || prepareImage(rvImage,
        rvw,
        rvh, MultiResolutionToolkitImage.getResolutionVariantObserver(img, o, true));
  }

  /**
   * Returns a new input method window, with behavior as specified in
   * {@link java.awt.im.spi.InputMethodContext#createInputMethodWindow}.
   * If the inputContext is not null, the window should return it from its
   * getInputContext() method. The window needs to implement
   * sun.awt.im.InputMethodWindow.
   * <p>
   * SunToolkit subclasses can override this method to return better input
   * method windows.
   */
  @Override
  public Window createInputMethodWindow(String title, InputContext context) {
    return new SimpleInputMethodWindow(title, context);
  }

  /**
   * Returns whether enableInputMethods should be set to true for peered
   * TextComponent instances on this platform. False by default.
   */
  @Override
  public boolean enableInputMethodsForTextComponent() {
    return false;
  }

  /**
   * Returns the default keyboard locale of the underlying operating system
   */
  @Override
  public Locale getDefaultKeyboardLocale() {
    return getStartupLocale();
  }

  @Override
  public WindowClosingListener getWindowClosingListener() {
    return windowClosingListener;
  }

  @Override
  public void setWindowClosingListener(WindowClosingListener wcl) {
    windowClosingListener = wcl;
  }

  @Override
  public RuntimeException windowClosingNotify(WindowEvent event) {
    return windowClosingListener != null ? windowClosingListener.windowClosingNotify(event) : null;
  }

  @Override
  public RuntimeException windowClosingDelivered(WindowEvent event) {
    return windowClosingListener != null ? windowClosingListener.windowClosingDelivered(event)
        : null;
  }

  /**
   * Returns whether the XEmbed server feature is requested by
   * developer.  If true, Toolkit should return an
   * XEmbed-server-enabled CanvasPeer instead of the ordinary CanvasPeer.
   */
  protected final boolean isXEmbedServerRequested() {
    return Boolean.valueOf(System.getProperty("sun.awt.xembedserver"));
  }

  /*
   * Default implementation for isModalExcludedSupportedImpl(), returns false.
   *
   * @see sun.awt.windows.WToolkit#isModalExcludeSupportedImpl
   * @see sun.awt.X11.XToolkit#isModalExcludeSupportedImpl
   *
   * @since 1.5
   */
  protected boolean isModalExcludedSupportedImpl() {
    return false;
  }

  public void addModalityListener(ModalityListener listener) {
    modalityListeners.add(listener);
  }

  public void removeModalityListener(ModalityListener listener) {
    modalityListeners.remove(listener);
  }

  public void notifyModalityPushed(Dialog dialog) {
    notifyModalityChange(ModalityEvent.MODALITY_PUSHED, dialog);
  }

  public void notifyModalityPopped(Dialog dialog) {
    notifyModalityChange(ModalityEvent.MODALITY_POPPED, dialog);
  }

  final void notifyModalityChange(int id, Dialog source) {
    ModalityEvent ev = new ModalityEvent(source, modalityListeners, id);
    ev.dispatch();
  }

  ///////////////////////////////////////////////////////////////////////////
  //
  // The following is used by the Java Plug-in to coordinate dialog modality
  // between containing applications (browsers, ActiveX containers etc) and
  // the AWT.
  //
  ///////////////////////////////////////////////////////////////////////////

  protected Context getAndroidContext() {
    return SkinJob.getAndroidApplicationContext();
  }

  protected void launchIntent(File file, String action) throws IOException {
    Intent intentToOpen = new Intent(action);
    Uri fileUri = Uri.fromFile(file);
    FileInputStream fileInputStream = new FileInputStream(file);
    try {
      String mime = URLConnection.guessContentTypeFromStream(fileInputStream);
      if (mime == null) {
        mime = URLConnection.guessContentTypeFromName(file.getName());
      }
      intentToOpen.setDataAndType(fileUri, mime);
      getAndroidContext().startActivity(intentToOpen);
    } finally {
      fileInputStream.close();
    }
  }

  @Override
  protected DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
    return new DesktopPeerImpl();
  }

  public abstract ButtonPeer createButton(Button target) throws HeadlessException;

  public abstract TextFieldPeer createTextField(TextField target) throws HeadlessException;

  public abstract LabelPeer createLabel(Label target) throws HeadlessException;

  public abstract ListPeer createList(List target) throws HeadlessException;

  public abstract CheckboxPeer createCheckbox(Checkbox target) throws HeadlessException;

  public abstract ScrollbarPeer createScrollbar(Scrollbar target) throws HeadlessException;

  public abstract ScrollPanePeer createScrollPane(ScrollPane target) throws HeadlessException;

  ///////////////////////////////////////////////////////////////////////////
  // End Plug-in code
  ///////////////////////////////////////////////////////////////////////////

  public abstract TextAreaPeer createTextArea(TextArea target) throws HeadlessException;

  public abstract ChoicePeer createChoice(Choice target) throws HeadlessException;

  public abstract FramePeer createFrame(Frame target) throws HeadlessException;

  public CanvasPeer createCanvas(Canvas target) {
    return (CanvasPeer) createComponent(target);
  }

  public PanelPeer createPanel(Panel target) {
    return (PanelPeer) createComponent(target);
  }

  public abstract WindowPeer createWindow(Window target) throws HeadlessException;

  public abstract DialogPeer createDialog(Dialog target) throws HeadlessException;

  public abstract MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException;

  public abstract MenuPeer createMenu(Menu target) throws HeadlessException;

  public abstract PopupMenuPeer createPopupMenu(PopupMenu target) throws HeadlessException;

  public abstract MenuItemPeer createMenuItem(MenuItem target) throws HeadlessException;

  public abstract FileDialogPeer createFileDialog(FileDialog target) throws HeadlessException;

  public abstract CheckboxMenuItemPeer createCheckboxMenuItem(
      CheckboxMenuItem target) throws HeadlessException;

  @Override
  protected synchronized MouseInfoPeer getMouseInfoPeer() {
    if (mPeer == null) {
      mPeer = new DefaultMouseInfoPeer();
    }
    return mPeer;
  }

  @Override
  @SuppressWarnings("deprecation")
  public abstract FontPeer getFontPeer(String name, int style);

  @Override
  public Dimension getScreenSize() {
    return new Dimension(getScreenWidth(), getScreenHeight());
  }

  @Override
  @SuppressWarnings("deprecation")
  public String[] getFontList() {
    return new String[]{
        Font.DIALOG, Font.SANS_SERIF, Font.SERIF, Font.MONOSPACED, Font.DIALOG_INPUT

        // -- Obsolete font names from 1.0.2.  It was decided that
        // -- getFontList should not return these old names:
        //    "Helvetica", "TimesRoman", "Courier", "ZapfDingbats"
    };
  }

  @Override
  @SuppressWarnings("deprecation")
  public FontMetrics getFontMetrics(Font font) {
    return FontDesignMetrics.getMetrics(font);
  }

  @Override
  public Image getImage(String filename) {
    return getImageFromHash(this, filename);
  }

    /* The following methods and variables are to support retrieving
     * desktop text anti-aliasing settings
     */

  @Override
  public Image getImage(URL url) {
    return getImageFromHash(this, url);
  }

  @Override
  public Image createImage(String filename) {
    checkPermissions(filename);
    return createImage(new FileImageSource(filename));
  }

  @Override
  public Image createImage(URL url) {
    return createImage(new URLImageSource(url));
  }

  @Override
  public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
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

  @Override
  public int checkImage(Image img, int w, int h, ImageObserver o) {
    if (!(img instanceof ToolkitImage)) {
      return ImageObserver.ALLBITS;
    }

    ToolkitImage tkimg = (ToolkitImage) img;
    int repbits;
    repbits = w == 0 || h == 0 ? ImageObserver.ALLBITS : tkimg.getImageRep().check(o);
    return (tkimg.check(o) | repbits) & checkResolutionVariant(img, w, h, o);
  }

  @Override
  public Image createImage(ImageProducer producer) {
    return new ToolkitImage(producer);
  }

  @Override
  public Image createImage(byte[] data, int offset, int length) {
    return createImage(new ByteArrayImageSource(data, offset, length));
  }

  @Override
  protected EventQueue getSystemEventQueueImpl() {
    return getSystemEventQueueImplPP();
  }

  public abstract DragSourceContextPeer createDragSourceContextPeer(
      DragGestureEvent dge) throws InvalidDnDOperationException;

  /**
   * Overridden in XToolkit and WToolkit
   */
  @Override
  public boolean isModalityTypeSupported(ModalityType modalityType) {
    return modalityType == ModalityType.MODELESS || modalityType == ModalityType.APPLICATION_MODAL;
  }

  /**
   * Overridden in XToolkit and WToolkit
   */
  @Override
  public boolean isModalExclusionTypeSupported(ModalExclusionType exclusionType) {
    return exclusionType == ModalExclusionType.NO_EXCLUDE;
  }

  /**
   * Parameterless version of realsync which uses default timout (see DEFAUL_WAIT_TIME).
   */
  public void realSync() throws OperationTimedOut, InfiniteLoop {
    realSync(DEFAULT_WAIT_TIME);
  }

  /**
   * Forces toolkit to synchronize with the native windowing
   * sub-system, flushing all pending work and waiting for all the
   * events to be processed.  This method guarantees that after
   * return no additional Java events will be generated, unless
   * cause by user. Obviously, the method cannot be used on the
   * event dispatch thread (EDT). In case it nevertheless gets
   * invoked on this thread, the method throws the
   * IllegalThreadException runtime exception.
   * <p>
   * <p> This method allows to write tests without explicit timeouts
   * or wait for some event.  Example:
   * {@code
   * Frame f = ...;
   * f.setVisible(true);
   * ((SunToolkit)Toolkit.getDefaultToolkit()).realSync();
   * }
   * <p>
   * <p> After realSync, {@code f} will be completely visible
   * on the screen, its getLocationOnScreen will be returning the
   * right result and it will be the focus owner.
   * <p>
   * <p> Another example:
   * {@code
   * b.requestFocus();
   * ((SunToolkit)Toolkit.getDefaultToolkit()).realSync();
   * }
   * <p>
   * <p> After realSync, {@code b} will be focus owner.
   * <p>
   * <p> Notice that realSync isn't guaranteed to work if recurring
   * actions occur, such as if during processing of some event
   * another request which may generate some events occurs.  By
   * default, sync tries to perform as much as {@value MAX_ITERS}
   * cycles of event processing, allowing for roughly {@value
   * MAX_ITERS} additional requests.
   * <p>
   * <p> For example, requestFocus() generates native request, which
   * generates one or two Java focus events, which then generate a
   * serie of paint events, a serie of Java focus events, which then
   * generate a serie of paint events which then are processed -
   * three cycles, minimum.
   *
   * @param timeout the maximum time to wait in milliseconds, negative means "forever".
   */
  public void realSync(long timeout) throws OperationTimedOut, InfiniteLoop {
    if (EventQueue.isDispatchThread()) {
      throw new IllegalThreadException(
          "The SunToolkit.realSync() method cannot be used on the event dispatch thread (EDT).");
    }
    int bigLoop = 0;
    do {
      // Let's do sync first
      sync();

      // During the wait process, when we were processing incoming
      // events, we could have made some new request, which can
      // generate new events.  Example: MapNotify/XSetInputFocus.
      // Therefore, we dispatch them as long as there is something
      // to dispatch.
      int iters = 0;
      while (syncNativeQueue(timeout) && iters < MAX_ITERS) {
        iters++;
      }
      if (iters >= MAX_ITERS) {
        throw new InfiniteLoop();
      }

      // native requests were dispatched by X/Window Manager or Windows
      // Moreover, we processed them all on Toolkit thread
      // Now wait while EDT processes them.
      //
      // During processing of some events (focus, for example),
      // some other events could have been generated.  So, after
      // waitForIdle, we may end up with full EventQueue
      iters = 0;
      while (waitForIdle(timeout) && iters < MAX_ITERS) {
        iters++;
      }
      if (iters >= MAX_ITERS) {
        throw new InfiniteLoop();
      }

      bigLoop++;
      // Again, for Java events, it was simple to check for new Java
      // events by checking event queue, but what if Java events
      // resulted in native requests?  Therefor, check native events again.
    } while ((syncNativeQueue(timeout) || waitForIdle(timeout)) && bigLoop < MAX_ITERS);
  }

  /**
   * Platform toolkits need to implement this method to perform the
   * sync of the native queue.  The method should wait until native
   * requests are processed, all native events are processed and
   * corresponding Java events are generated.  Should return
   * {@code true} if some events were processed,
   * {@code false} otherwise.
   */
  protected abstract boolean syncNativeQueue(long timeout);

  boolean isEQEmpty() {
    EventQueue queue = getSystemEventQueueImpl();
    return AWTAccessor.getEventQueueAccessor().noEvents(queue);
  }

  /**
   * Waits for the Java event queue to empty.  Ensures that all
   * events are processed (including paint events), and that if
   * recursive events were generated, they are also processed.
   * Should return {@code true} if more processing is
   * necessary, {@code false} otherwise.
   */
  @SuppressWarnings("serial")
  protected final boolean waitForIdle(long timeout) {
    flushPendingEvents();
    boolean queueWasEmpty = isEQEmpty();
    queueEmpty = false;
    eventDispatched = false;
    synchronized (waitLock) {
      postEvent(AppContext.getAppContext(),
          new PeerEvent(getSystemEventQueueImpl(), null, PeerEvent.LOW_PRIORITY_EVENT) {
            @Override
            public void dispatch() {
              // Here we block EDT.  It could have some
              // events, it should have dispatched them by
              // now.  So native requests could have been
              // generated.  First, dispatch them.  Then,
              // flush Java events again.
              int iters = 0;
              while (syncNativeQueue(timeout) && iters < MAX_ITERS) {
                iters++;
              }
              flushPendingEvents();

              synchronized (waitLock) {
                queueEmpty = isEQEmpty();
                eventDispatched = true;
                waitLock.notifyAll();
              }
            }
          });
      try {
        while (!eventDispatched) {
          waitLock.wait();
        }
      } catch (InterruptedException ie) {
        return false;
      }
    }

    try {
      Thread.sleep(MINIMAL_EDELAY);
    } catch (InterruptedException ie) {
      throw new RuntimeException("Interrupted");
    }

    flushPendingEvents();

    // Lock to force write-cache flush for queueEmpty.
    synchronized (waitLock) {
      return !(queueEmpty && isEQEmpty() && queueWasEmpty);
    }
  }

  /**
   * Grabs the mouse input for the given window.  The window must be
   * visible.  The window or its children do not receive any
   * additional mouse events besides those targeted to them.  All
   * other events will be dispatched as before - to the respective
   * targets.  This Window will receive UngrabEvent when automatic
   * ungrab is about to happen.  The event can be listened to by
   * installing AWTEventListener with WINDOW_EVENT_MASK.  See
   * UngrabEvent class for the list of conditions when ungrab is
   * about to happen.
   *
   * @see UngrabEvent
   */
  public abstract void grab(Window w);

  /**
   * Forces ungrab.  No event will be sent.
   */
  public abstract void ungrab(Window w);

  /* Need an instance method because setDesktopProperty(..) is protected. */
  private void fireDesktopFontPropertyChanges() {
    setDesktopProperty(DESKTOPFONTHINTS, getDesktopFontHints());
  }

  /* Overridden by subclasses to return platform/desktop specific values */
  protected RenderingHints getDesktopAAHints() {
    return null;
  }

  public abstract boolean isDesktopSupported();

  /**
   * Returns true if the native GTK libraries are available.  The
   * default implementation returns false, but UNIXToolkit overrides this
   * method to provide a more specific answer.
   */
  public boolean isNativeGTKAvailable() {
    return false;
  }

  public synchronized void setWindowDeactivationTime(Window w, long time) {
    AppContext ctx = getAppContext(w);
    WeakHashMap<Window, Long> map = (WeakHashMap<Window, Long>) ctx.get(DEACTIVATION_TIMES_MAP_KEY);
    if (map == null) {
      map = new WeakHashMap<>();
      ctx.put(DEACTIVATION_TIMES_MAP_KEY, map);
    }
    map.put(w, time);
  }

  public synchronized long getWindowDeactivationTime(Window w) {
    AppContext ctx = getAppContext(w);
    WeakHashMap<Window, Long> map = (WeakHashMap<Window, Long>) ctx.get(DEACTIVATION_TIMES_MAP_KEY);
    if (map == null) {
      return -1;
    }
    Long time = map.get(w);
    return time == null ? -1 : time;
  }

  // Cosntant alpha
  public boolean isWindowOpacitySupported() {
    return false;
  }

  // Shaping
  public boolean isWindowShapingSupported() {
    return false;
  }

  // Per-pixel alpha
  public boolean isWindowTranslucencySupported() {
    return false;
  }

  public boolean isTranslucencyCapable(GraphicsConfiguration gc) {
    return false;
  }

  /**
   * Returns true if swing backbuffer should be translucent.
   */
  public boolean isSwingBackbufferTranslucencySupported() {
    return false;
  }

  /**
   * Returns whether the native system requires using the peer.updateWindow()
   * method to update the contents of a non-opaque window, or if usual
   * painting procedures are sufficient. The default return value covers
   * the X11 systems. On MS Windows this method is overriden in WToolkit
   * to return true.
   */
  public boolean needUpdateWindow() {
    return false;
  }

  /**
   * Descendants of the SunToolkit should override and put their own logic here.
   */
  public int getNumberOfButtons() {
    return 3;
  }

  static class ModalityListenerList implements ModalityListener {

    final Vector<ModalityListener> listeners = new Vector<>();

    void add(ModalityListener listener) {
      listeners.addElement(listener);
    }

    void remove(ModalityListener listener) {
      listeners.removeElement(listener);
    }

    @Override
    public void modalityPushed(ModalityEvent ev) {
      for (ModalityListener listener : listeners) {
        listener.modalityPushed(ev);
      }
    }

    @Override
    public void modalityPopped(ModalityEvent ev) {
      for (ModalityListener listener : listeners) {
        listener.modalityPopped(ev);
      }
    }
  } // end of class ModalityListenerList

  @SuppressWarnings("serial")
  public static class OperationTimedOut extends RuntimeException {
    public OperationTimedOut(String msg) {
      super(msg);
    }

    public OperationTimedOut() {
    }
  }

  ///////////////////////////////////////////////////////////////////////////
  //
  // The following methods help set and identify whether a particular
  // AWTEvent object was produced by the system or by user code. As of this
  // writing the only consumer is the Java Plug-In, although this information
  // could be useful to more clients and probably should be formalized in
  // the public API.
  //
  ///////////////////////////////////////////////////////////////////////////

  @SuppressWarnings("serial")
  public static class InfiniteLoop extends RuntimeException {
  }

  @SuppressWarnings("serial")
  public static class IllegalThreadException extends RuntimeException {
    public IllegalThreadException(String msg) {
      super(msg);
    }

    public IllegalThreadException() {
    }
  }

  private class DesktopPeerImpl implements DesktopPeer {

    DesktopPeerImpl() {
    }

    @Override
    public boolean isSupported(Action action) {
      return action != Action.PRINT;
    }

    @Override
    public void open(File file) throws IOException {
      launchIntent(file, Intent.ACTION_VIEW);
    }

    @Override
    public void edit(File file) throws IOException {
      launchIntent(file, Intent.ACTION_EDIT);
    }

    @Override
    public void print(File file) throws IOException {
      throw new UnsupportedOperationException("TODO: Uncomment once support.v4 JAR is installed");
      /*
       if (ContextCompat.checkSelfPermission(androidContext,
       Manifest.permission.READ_CONTACTS)
       != PackageManager.PERMISSION_GRANTED) {

       // Should we show an explanation?
       if (ActivityCompat.shouldShowRequestPermissionRationale(androidContext,
       Manifest.permission.READ_CONTACTS)) {

       // Show an expanation to the user *asynchronously* -- don't block
       // this thread waiting for the user's response! After the user
       // sees the explanation, try again to request the permission.

       } else {

       // No explanation needed, we can request the permission.

       ActivityCompat.requestPermissions(thisActivity,
       new String[]{Manifest.permission.READ_CONTACTS},
       MY_PERMISSIONS_REQUEST_READ_CONTACTS);

       // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
       // app-defined int constant. The callback method gets the
       // result of the request.
       }
       }
       launchIntent(file, Intent.ACTION_PR);
       */
    }

    @Override
    public void mail(URI mailtoURL) throws IOException {
      Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
      mailIntent.setData(Uri.parse(mailtoURL.toString()));
      getAndroidContext().startActivity(mailIntent);
    }

    @Override
    public void browse(URI uri) throws IOException {
      Intent browseIntent = new Intent(Intent.ACTION_VIEW);
      browseIntent.setData(Uri.parse(uri.toString()));
      getAndroidContext().startActivity(browseIntent);
    }
  }
} // class SunToolkit

/*
 * PostEventQueue is a Thread that runs in the same AppContext as the
 * Java EventQueue.  It is a queue of AWTEvents to be posted to the
 * Java EventQueue.  The toolkit Thread (AWT-Windows/AWT-Motif) posts
 * events to this queue, which then calls EventQueue.postEvent().
 *
 * We do this because EventQueue.postEvent() may be overridden by client
 * code, and we mustn't ever call client code from the toolkit thread.
 */
class PostEventQueue {
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
