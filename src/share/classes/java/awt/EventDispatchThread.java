/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.awt;

import android.util.Log;
import java.awt.EventFilter.FilterAction;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import sun.awt.EventQueueDelegate;
import sun.awt.EventQueueDelegate.Delegate;
import sun.awt.ModalExclude;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDragSourceContextPeer;

/**
 * EventDispatchThread is a package-private AWT class which takes
 * events off the EventQueue and dispatches them to the appropriate
 * AWT components.
 * <p>
 * The Thread starts a "permanent" event pump with a call to
 * pumpEvents(Conditional) in its run() method. Event handlers can choose to
 * block this event pump at any time, but should start a new pump (<b>not</b>
 * a new EventDispatchThread) by again calling pumpEvents(Conditional). This
 * secondary event pump will exit automatically as soon as the Condtional
 * evaluate()s to false and an additional Event is pumped and dispatched.
 *
 * @author Tom Ball
 * @author Amy Fowler
 * @author Fred Ecks
 * @author David Mendenhall
 * @since 1.1
 */
class EventDispatchThread extends Thread {
  private static final String TAG = "EventDispatchThread";

  private static final int ANY_EVENT = -1;
  private final ArrayList<EventFilter> eventFilters = new ArrayList<>();
  private EventQueue theQueue;
  private volatile boolean doDispatch = true;

  EventDispatchThread(ThreadGroup group, String name, EventQueue queue) {
    super(group, name);
    setEventQueue(queue);
  }

  /*
   * Must be called on EDT only, that's why no synchronization
   */
  public void stopDispatching() {
    doDispatch = false;
  }

  @Override
  public void run() {
    try {
      pumpEvents(new Conditional() {
        @Override
        public boolean evaluate() {
          return true;
        }
      });
    } finally {
      getEventQueue().detachDispatchThread(this);
    }
  }

  void pumpEvents(Conditional cond) {
    pumpEvents(ANY_EVENT, cond);
  }

  void pumpEventsForHierarchy(Conditional cond, Component modalComponent) {
    pumpEventsForHierarchy(ANY_EVENT, cond, modalComponent);
  }

  void pumpEvents(int id, Conditional cond) {
    pumpEventsForHierarchy(id, cond, null);
  }

  void pumpEventsForHierarchy(int id, Conditional cond, Component modalComponent) {
    pumpEventsForFilter(id, cond, new HierarchyEventFilter(modalComponent));
  }

  void pumpEventsForFilter(Conditional cond, EventFilter filter) {
    pumpEventsForFilter(ANY_EVENT, cond, filter);
  }

  void pumpEventsForFilter(int id, Conditional cond, EventFilter filter) {
    addEventFilter(filter);
    doDispatch = true;
    while (doDispatch && !isInterrupted() && cond.evaluate()) {
      pumpOneEventForFilters(id);
    }
    removeEventFilter(filter);
  }

  void addEventFilter(EventFilter filter) {
    Log.v(TAG, "adding the event filter: " + filter);
    synchronized (eventFilters) {
      if (!eventFilters.contains(filter)) {
        if (filter instanceof ModalEventFilter) {
          ModalEventFilter newFilter = (ModalEventFilter) filter;
          int k;
          for (k = 0; k < eventFilters.size(); k++) {
            EventFilter f = eventFilters.get(k);
            if (f instanceof ModalEventFilter) {
              ModalEventFilter cf = (ModalEventFilter) f;
              if (cf.compareTo(newFilter) > 0) {
                break;
              }
            }
          }
          eventFilters.add(k, filter);
        } else {
          eventFilters.add(filter);
        }
      }
    }
  }

  void removeEventFilter(EventFilter filter) {
    Log.v(TAG, "removing the event filter: " + filter);
    synchronized (eventFilters) {
      eventFilters.remove(filter);
    }
  }

  void pumpOneEventForFilters(int id) {
    AWTEvent event;
    boolean eventOK;
    try {
      EventQueue eq;
      Delegate delegate;
      do {
        // EventQueue may change during the dispatching
        eq = getEventQueue();
        delegate = EventQueueDelegate.getDelegate();

        if (delegate != null && id == ANY_EVENT) {
          event = delegate.getNextEvent(eq);
        } else {
          event = id == ANY_EVENT ? eq.getNextEvent() : eq.getNextEvent(id);
        }

        eventOK = true;
        synchronized (eventFilters) {
          for (int i = eventFilters.size() - 1; i >= 0; i--) {
            EventFilter f = eventFilters.get(i);
            FilterAction accept = f.acceptEvent(event);
            if (accept == FilterAction.REJECT) {
              eventOK = false;
              break;
            } else if (accept == FilterAction.ACCEPT_IMMEDIATELY) {
              break;
            }
          }
        }
        eventOK = eventOK && SunDragSourceContextPeer.checkEvent(event);
        if (!eventOK) {
          event.consume();
        }
      } while (!eventOK);

      Log.v(TAG, "Dispatching: " + event);

      Object handle = null;
      if (delegate != null) {
        handle = delegate.beforeDispatch(event);
      }
      eq.dispatchEvent(event);
      if (delegate != null) {
        delegate.afterDispatch(event, handle);
      }
    } catch (ThreadDeath death) {
      doDispatch = false;
      throw death;
    } catch (InterruptedException interruptedException) {
      doDispatch = false; // AppContext.dispose() interrupts all
      // Threads in the AppContext
    } catch (Throwable e) {
      processException(e);
    }
  }

  private void processException(Throwable e) {
    Log.d(TAG, "Processing exception: ", e);
    getUncaughtExceptionHandler().uncaughtException(this, e);
  }

  public synchronized EventQueue getEventQueue() {
    return theQueue;
  }

  public synchronized void setEventQueue(EventQueue eq) {
    theQueue = eq;
  }

  static class HierarchyEventFilter implements EventFilter {
    protected static final String JINTERNAL_FRAME_CLASS = "javax.swing.JInternalFrame";
    private final Component modalComponent;

    public HierarchyEventFilter(Component modalComponent) {
      this.modalComponent = modalComponent;
    }

    /**
     * Checks that the given object is instance of the given class.
     *
     * @param obj       Object to be checked
     * @param className The name of the class. Must be fully-qualified class name.
     * @return true, if this object is instanceof given class,
     * false, otherwise, or if obj or className is null
     */
    private static boolean isInstanceOf(Object obj, String className) {
      if (obj == null) {
        return false;
      }
      if (className == null) {
        return false;
      }

      Class<?> cls = obj.getClass();
      while (cls != null) {
        if (cls.getName().equals(className)) {
          return true;
        }
        cls = cls.getSuperclass();
      }
      return false;
    }

    @Override
    public FilterAction acceptEvent(AWTEvent event) {
      if (modalComponent != null) {
        int eventID = event.getID();
        boolean mouseEvent = eventID >= MouseEvent.MOUSE_FIRST && eventID <= MouseEvent.MOUSE_LAST;
        boolean actionEvent = eventID >= ActionEvent.ACTION_FIRST
            && eventID <= ActionEvent.ACTION_LAST;
        boolean windowClosingEvent = eventID == WindowEvent.WINDOW_CLOSING;
                /*
                 * filter out MouseEvent and ActionEvent that's outside
                 * the modalComponent hierarchy.
                 * KeyEvent is handled by using enqueueKeyEvent
                 * in Dialog.show
                 */
        if (isInstanceOf(modalComponent, JINTERNAL_FRAME_CLASS)) {
                    /*
                     * Modal internal frames are handled separately. If event is
                     * for some component from another heavyweight than modalComp,
                     * it is accepted. If heavyweight is the same - we still accept
                     * event and perform further filtering in LightweightDispatcher
                     */
          return windowClosingEvent ? FilterAction.REJECT : FilterAction.ACCEPT;
        }
        if (mouseEvent || actionEvent || windowClosingEvent) {
          Object o = event.getSource();
          if (o instanceof ModalExclude) {
            // Exclude this object from modality and
            // continue to pump it's events.
            return FilterAction.ACCEPT;
          } else if (o instanceof Component) {
            Component c = (Component) o;
            // 5.0u3 modal exclusion
            boolean modalExcluded = false;
            if (modalComponent instanceof Container) {
              while (c != modalComponent && c != null) {
                if (c instanceof Window && SunToolkit.isModalExcluded((Window) c)) {
                  // Exclude this window and all its children from
                  //  modality and continue to pump it's events.
                  modalExcluded = true;
                  break;
                }
                c = c.getParent();
              }
            }
            if (!modalExcluded && c != modalComponent) {
              return FilterAction.REJECT;
            }
          }
        }
      }
      return FilterAction.ACCEPT;
    }
  }
}
