package java.awt;

import android.util.Log;
import java.awt.event.AWTEventListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import sun.awt.AppContext;
import sun.awt.SunToolkit;
import sun.awt.dnd.SunDropTargetEvent;

/**
 * Class to manage the dispatching of MouseEvents to the lightweight descendants
 * and SunDropTargetEvents to both lightweight and heavyweight descendants
 * contained by a native container.
 * <p>
 * NOTE: the class name is not appropriate anymore, but we cannot change it
 * because we must keep serialization compatibility.
 *
 * @author Timothy Prinzing
 */
class LightweightDispatcher implements java.io.Serializable, AWTEventListener {

  /*
     * JDK 1.1 serialVersionUID
     */
  private static final long serialVersionUID = 5184291520170872969L;
  /*
     * Our own mouse event for when we're dragged over from another hw
     * container
     */
  private static final int LWD_MOUSE_DRAGGED_OVER = 1500;

  private static final String TAG = "LightweightDispatcher";
  /**
   * The kind of events routed to lightweight components from windowed
   * hosts.
   */
  private static final long PROXY_EVENT_MASK = AWTEvent.FOCUS_EVENT_MASK |
      AWTEvent.KEY_EVENT_MASK |
      AWTEvent.MOUSE_EVENT_MASK |
      AWTEvent.MOUSE_MOTION_EVENT_MASK |
      AWTEvent.MOUSE_WHEEL_EVENT_MASK;
  private static final long MOUSE_MASK = AWTEvent.MOUSE_EVENT_MASK |
      AWTEvent.MOUSE_MOTION_EVENT_MASK |
      AWTEvent.MOUSE_WHEEL_EVENT_MASK;
  /**
   * The windowed container that might be hosting events for
   * subcomponents.
   */
  private final Container nativeContainer;
  /**
   * This variable is not used, but kept for serialization compatibility
   */
  private Component focus;
  /**
   * The current subcomponent being hosted by this windowed
   * component that has events being forwarded to it.  If this
   * is null, there are currently no events being forwarded to
   * a subcomponent.
   */
  private transient Component mouseEventTarget;
  /**
   * The last component entered by the {@code MouseEvent}.
   */
  private transient Component targetLastEntered;
  /**
   * The last component entered by the {@code SunDropTargetEvent}.
   */
  private transient Component targetLastEnteredDT;
  /**
   * Indicates whether {@code mouseEventTarget} was removed and nulled
   */
  private transient boolean isCleaned;
  /**
   * Is the mouse over the native container.
   */
  private transient boolean isMouseInNativeContainer = false;
  /**
   * Is DnD over the native container.
   */
  private transient boolean isMouseDTInNativeContainer = false;
  /**
   * This variable is not used, but kept for serialization compatibility
   */
  private Cursor nativeCursor;
  /**
   * The event mask for contained lightweight components.  Lightweight
   * components need a windowed container to host window-related
   * events.  This separate mask indicates events that have been
   * requested by contained lightweight components without effecting
   * the mask of the windowed component itself.
   */
  private long eventMask;

  LightweightDispatcher(Container nativeContainer) {
    this.nativeContainer = nativeContainer;
    mouseEventTarget = null;
    eventMask = 0;
  }

  /*
     * Clean up any resources allocated when dispatcher was created;
     * should be called from Container.removeNotify
     */
  void dispose() {
    //System.out.println("Disposing lw dispatcher");
    stopListeningForOtherDrags();
    mouseEventTarget = null;
    targetLastEntered = null;
    targetLastEnteredDT = null;
  }

  // --- member variables -------------------------------

  /**
   * Enables events to subcomponents.
   */
  void enableEvents(long events) {
    eventMask |= events;
  }

  /**
   * Dispatches an event to a sub-component if necessary, and
   * returns whether or not the event was forwarded to a
   * sub-component.
   *
   * @param e the event
   */
  boolean dispatchEvent(AWTEvent e) {
    boolean ret = false;

        /*
         * Fix for BugTraq Id 4389284.
         * Dispatch SunDropTargetEvents regardless of eventMask value.
         * Do not update cursor on dispatching SunDropTargetEvents.
         */
    if (e instanceof SunDropTargetEvent) {

      SunDropTargetEvent sdde = (SunDropTargetEvent) e;
      ret = processDropTargetEvent(sdde);
    } else {
      if (e instanceof MouseEvent && (eventMask & MOUSE_MASK) != 0) {
        MouseEvent me = (MouseEvent) e;
        ret = processMouseEvent(me);
      }

      if (e.getID() == MouseEvent.MOUSE_MOVED) {
        nativeContainer.updateCursorImmediately();
      }
    }

    return ret;
  }

  /* This method effectively returns whether or not a mouse button was down
     * just BEFORE the event happened.  A better method name might be
     * wasAMouseButtonDownBeforeThisEvent().
     */
  private boolean isMouseGrab(MouseEvent e) {
    int modifiers = e.getModifiersEx();

    if (e.getID() == MouseEvent.MOUSE_PRESSED || e.getID() == MouseEvent.MOUSE_RELEASED) {
      switch (e.getButton()) {
        case MouseEvent.BUTTON1:
          modifiers ^= InputEvent.BUTTON1_DOWN_MASK;
          break;
        case MouseEvent.BUTTON2:
          modifiers ^= InputEvent.BUTTON2_DOWN_MASK;
          break;
        case MouseEvent.BUTTON3:
          modifiers ^= InputEvent.BUTTON3_DOWN_MASK;
          break;
      }
    }
        /* modifiers now as just before event */
    return ((modifiers & (InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK
                              | InputEvent.BUTTON3_DOWN_MASK)) != 0);
  }

  /**
   * This method attempts to distribute a mouse event to a lightweight
   * component.  It tries to avoid doing any unnecessary probes down
   * into the component tree to minimize the overhead of determining
   * where to route the event, since mouse movement events tend to
   * come in large and frequent amounts.
   */
  private boolean processMouseEvent(MouseEvent e) {
    int id = e.getID();
    Component mouseOver =   // sensitive to mouse events
        nativeContainer.getMouseEventTarget(e.getX(), e.getY(), Container.INCLUDE_SELF);

    trackMouseEnterExit(mouseOver, e);

    // 4508327 : MOUSE_CLICKED should only go to the recipient of
    // the accompanying MOUSE_PRESSED, so don't reset mouseEventTarget on a
    // MOUSE_CLICKED.
    if (!isMouseGrab(e) && id != MouseEvent.MOUSE_CLICKED) {
      mouseEventTarget = (mouseOver != nativeContainer) ? mouseOver : null;
      isCleaned = false;
    }

    if (mouseEventTarget != null) {
      switch (id) {
        case MouseEvent.MOUSE_ENTERED:
        case MouseEvent.MOUSE_EXITED:
          break;
        case MouseEvent.MOUSE_PRESSED:
          retargetMouseEvent(mouseEventTarget, id, e);
          break;
        case MouseEvent.MOUSE_RELEASED:
          retargetMouseEvent(mouseEventTarget, id, e);
          break;
        case MouseEvent.MOUSE_CLICKED:
          // 4508327: MOUSE_CLICKED should never be dispatched to a Component
          // other than that which received the MOUSE_PRESSED event.  If the
          // mouse is now over a different Component, don't dispatch the event.
          // The previous fix for a similar problem was associated with bug
          // 4155217.
          if (mouseOver == mouseEventTarget) {
            retargetMouseEvent(mouseOver, id, e);
          }
          break;
        case MouseEvent.MOUSE_MOVED:
          retargetMouseEvent(mouseEventTarget, id, e);
          break;
        case MouseEvent.MOUSE_DRAGGED:
          if (isMouseGrab(e)) {
            retargetMouseEvent(mouseEventTarget, id, e);
          }
          break;
        case MouseEvent.MOUSE_WHEEL:
          // This may send it somewhere that doesn't have MouseWheelEvents
          // enabled.  In this case, Component.dispatchEventImpl() will
          // retarget the event to a parent that DOES have the events enabled.
          Log.v(TAG, "retargeting mouse wheel to " +
              mouseOver.getName() + ", " +
              mouseOver.getClass());
          retargetMouseEvent(mouseOver, id, e);
          break;
      }
      //Consuming of wheel events is implemented in "retargetMouseEvent".
      if (id != MouseEvent.MOUSE_WHEEL) {
        e.consume();
      }
    } else if (isCleaned && id != MouseEvent.MOUSE_WHEEL) {
      //After mouseEventTarget was removed and cleaned should consume all events
      //until new mouseEventTarget is found
      e.consume();
    }
    return e.isConsumed();
  }

  private boolean processDropTargetEvent(SunDropTargetEvent e) {
    int id = e.getID();
    int x = e.getX();
    int y = e.getY();

        /*
         * Fix for BugTraq ID 4395290.
         * It is possible that SunDropTargetEvent's Point is outside of the
         * native container bounds. In this case we truncate coordinates.
         */
    if (!nativeContainer.contains(x, y)) {
      final Dimension d = nativeContainer.getSize();
      if (d.width <= x) {
        x = d.width - 1;
      } else if (x < 0) {
        x = 0;
      }
      if (d.height <= y) {
        y = d.height - 1;
      } else if (y < 0) {
        y = 0;
      }
    }
    Component mouseOver =   // not necessarily sensitive to mouse events
        nativeContainer.getDropTargetEventTarget(x, y, Container.INCLUDE_SELF);
    trackMouseEnterExit(mouseOver, e);

    if (mouseOver != nativeContainer && mouseOver != null) {
      switch (id) {
        case SunDropTargetEvent.MOUSE_ENTERED:
        case SunDropTargetEvent.MOUSE_EXITED:
          break;
        default:
          retargetMouseEvent(mouseOver, id, e);
          e.consume();
          break;
      }
    }
    return e.isConsumed();
  }

  /*
     * Generates dnd enter/exit events as mouse moves over lw components
     * @param targetOver       Target mouse is over (including native container)
     * @param e                SunDropTarget mouse event in native container
     */
  private void trackDropTargetEnterExit(Component targetOver, MouseEvent e) {
    int id = e.getID();
    if (id == MouseEvent.MOUSE_ENTERED && isMouseDTInNativeContainer) {
      // This can happen if a lightweight component which initiated the
      // drag has an associated drop target. MOUSE_ENTERED comes when the
      // mouse is in the native container already. To propagate this event
      // properly we should null out targetLastEntered.
      targetLastEnteredDT = null;
    } else if (id == MouseEvent.MOUSE_ENTERED) {
      isMouseDTInNativeContainer = true;
    } else if (id == MouseEvent.MOUSE_EXITED) {
      isMouseDTInNativeContainer = false;
    }
    targetLastEnteredDT = retargetMouseEnterExit(targetOver,
        e,
        targetLastEnteredDT,
        isMouseDTInNativeContainer);
  }

  /*
     * Generates enter/exit events as mouse moves over lw components
     * @param targetOver        Target mouse is over (including native container)
     * @param e                 Mouse event in native container
     */
  private void trackMouseEnterExit(Component targetOver, MouseEvent e) {
    if (e instanceof SunDropTargetEvent) {
      trackDropTargetEnterExit(targetOver, e);
      return;
    }
    int id = e.getID();

    if (id != MouseEvent.MOUSE_EXITED &&
        id != MouseEvent.MOUSE_DRAGGED &&
        id != LWD_MOUSE_DRAGGED_OVER &&
        !isMouseInNativeContainer) {
      // any event but an exit or drag means we're in the native container
      isMouseInNativeContainer = true;
      startListeningForOtherDrags();
    } else if (id == MouseEvent.MOUSE_EXITED) {
      isMouseInNativeContainer = false;
      stopListeningForOtherDrags();
    }
    targetLastEntered = retargetMouseEnterExit(targetOver,
        e,
        targetLastEntered,
        isMouseInNativeContainer);
  }

  private Component retargetMouseEnterExit(
      Component targetOver, MouseEvent e, Component lastEntered, boolean inNativeContainer) {
    int id = e.getID();
    Component targetEnter = inNativeContainer ? targetOver : null;

    if (lastEntered != targetEnter) {
      if (lastEntered != null) {
        retargetMouseEvent(lastEntered, MouseEvent.MOUSE_EXITED, e);
      }
      if (id == MouseEvent.MOUSE_EXITED) {
        // consume native exit event if we generate one
        e.consume();
      }

      if (targetEnter != null) {
        retargetMouseEvent(targetEnter, MouseEvent.MOUSE_ENTERED, e);
      }
      if (id == MouseEvent.MOUSE_ENTERED) {
        // consume native enter event if we generate one
        e.consume();
      }
    }
    return targetEnter;
  }

  /*
     * Listens to global mouse drag events so even drags originating
     * from other heavyweight containers will generate enter/exit
     * events in this container
     */
  private void startListeningForOtherDrags() {
    //System.out.println("Adding AWTEventListener");
    java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Object>() {
      public Object run() {
        nativeContainer.getToolkit().addAWTEventListener(LightweightDispatcher.this,
            AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
        return null;
      }
    });
  }

  private void stopListeningForOtherDrags() {
    //System.out.println("Removing AWTEventListener");
    java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Object>() {
      public Object run() {
        nativeContainer.getToolkit().removeAWTEventListener(LightweightDispatcher.this);
        return null;
      }
    });
  }

  /*
     * (Implementation of AWTEventListener)
     * Listen for drag events posted in other hw components so we can
     * track enter/exit regardless of where a drag originated
     */
  public void eventDispatched(AWTEvent e) {
    boolean isForeignDrag = (e instanceof MouseEvent) &&
        !(e instanceof SunDropTargetEvent) &&
        (e.id == MouseEvent.MOUSE_DRAGGED) &&
        (e.getSource() != nativeContainer);

    if (!isForeignDrag) {
      // only interested in drags from other hw components
      return;
    }

    MouseEvent srcEvent = (MouseEvent) e;
    MouseEvent me;

    synchronized (nativeContainer.getTreeLock()) {
      Component srcComponent = srcEvent.getComponent();

      // component may have disappeared since drag event posted
      // (i.e. Swing hierarchical menus)
      if (!srcComponent.isShowing()) {
        return;
      }

      // see 5083555
      // check if srcComponent is in any modal blocked window
      Component c = nativeContainer;
      while ((c != null) && !(c instanceof Window)) {
        c = c.getParent_NoClientCode();
      }
      if ((c == null) || ((Window) c).isModalBlocked()) {
        return;
      }

      //
      // create an internal 'dragged-over' event indicating
      // we are being dragged over from another hw component
      //
      me = new MouseEvent(nativeContainer,
          LWD_MOUSE_DRAGGED_OVER,
          srcEvent.getWhen(),
          srcEvent.getModifiersEx() | srcEvent.getModifiers(),
          srcEvent.getX(),
          srcEvent.getY(),
          srcEvent.getXOnScreen(),
          srcEvent.getYOnScreen(),
          srcEvent.getClickCount(),
          srcEvent.isPopupTrigger(),
          srcEvent.getButton());
      ((AWTEvent) srcEvent).copyPrivateDataInto(me);
      // translate coordinates to this native container
      final Point ptSrcOrigin = srcComponent.getLocationOnScreen();

      if (AppContext.getAppContext() != nativeContainer.appContext) {
        final MouseEvent mouseEvent = me;
        Runnable r = new Runnable() {
          public void run() {
            if (!nativeContainer.isShowing()) {
              return;
            }

            Point ptDstOrigin = nativeContainer.getLocationOnScreen();
            mouseEvent.translatePoint(ptSrcOrigin.x - ptDstOrigin.x, ptSrcOrigin.y - ptDstOrigin.y);
            Component targetOver = nativeContainer.getMouseEventTarget(mouseEvent.getX(),
                mouseEvent.getY(),
                Container.INCLUDE_SELF);
            trackMouseEnterExit(targetOver, mouseEvent);
          }
        };
        SunToolkit.executeOnEventHandlerThread(nativeContainer, r);
        return;
      } else {
        if (!nativeContainer.isShowing()) {
          return;
        }

        Point ptDstOrigin = nativeContainer.getLocationOnScreen();
        me.translatePoint(ptSrcOrigin.x - ptDstOrigin.x, ptSrcOrigin.y - ptDstOrigin.y);
      }
    }
    //System.out.println("Track event: " + me);
    // feed the 'dragged-over' event directly to the enter/exit
    // code (not a real event so don't pass it to dispatchEvent)
    Component targetOver = nativeContainer.getMouseEventTarget(me.getX(),
        me.getY(),
        Container.INCLUDE_SELF);
    trackMouseEnterExit(targetOver, me);
  }

  /**
   * Sends a mouse event to the current mouse event recipient using
   * the given event (sent to the windowed host) as a srcEvent.  If
   * the mouse event target is still in the component tree, the
   * coordinates of the event are translated to those of the target.
   * If the target has been removed, we don't bother to send the
   * message.
   */
  void retargetMouseEvent(Component target, int id, MouseEvent e) {
    if (target == null) {
      return; // mouse is over another hw component or target is disabled
    }

    int x = e.getX(), y = e.getY();
    Component component;

    for (component = target; component != null && component != nativeContainer;
        component = component.getParent()) {
      x -= component.x;
      y -= component.y;
    }
    MouseEvent retargeted;
    if (component != null) {
      if (e instanceof SunDropTargetEvent) {
        retargeted = new SunDropTargetEvent(target,
            id,
            x,
            y,
            ((SunDropTargetEvent) e).getDispatcher());
      } else if (id == MouseEvent.MOUSE_WHEEL) {
        retargeted = new MouseWheelEvent(target,
            id,
            e.getWhen(),
            e.getModifiersEx() | e.getModifiers(),
            x,
            y,
            e.getXOnScreen(),
            e.getYOnScreen(),
            e.getClickCount(),
            e.isPopupTrigger(),
            ((MouseWheelEvent) e).getScrollType(),
            ((MouseWheelEvent) e).getScrollAmount(),
            ((MouseWheelEvent) e).getWheelRotation(),
            ((MouseWheelEvent) e).getPreciseWheelRotation());
      } else {
        retargeted = new MouseEvent(target,
            id,
            e.getWhen(),
            e.getModifiersEx() | e.getModifiers(),
            x,
            y,
            e.getXOnScreen(),
            e.getYOnScreen(),
            e.getClickCount(),
            e.isPopupTrigger(),
            e.getButton());
      }

      ((AWTEvent) e).copyPrivateDataInto(retargeted);

      if (target == nativeContainer) {
        // avoid recursively calling LightweightDispatcher...
        ((Container) target).dispatchEventToSelf(retargeted);
      } else {
        assert AppContext.getAppContext() == target.appContext;

        if (nativeContainer.modalComp != null) {
          if (((Container) nativeContainer.modalComp).isAncestorOf(target)) {
            target.dispatchEvent(retargeted);
          } else {
            e.consume();
          }
        } else {
          target.dispatchEvent(retargeted);
        }
      }
      if (id == MouseEvent.MOUSE_WHEEL && retargeted.isConsumed()) {
        //An exception for wheel bubbling to the native system.
        //In "processMouseEvent" total event consuming for wheel events is skipped.
        //Protection from bubbling of Java-accepted wheel events.
        e.consume();
      }
    }
  }

  void removeReferences(Component removedComponent) {
    if (mouseEventTarget == removedComponent) {
      isCleaned = true;
      mouseEventTarget = null;
    }
    if (targetLastEntered == removedComponent) {
      targetLastEntered = null;
    }
    if (targetLastEnteredDT == removedComponent) {
      targetLastEnteredDT = null;
    }
  }
}
