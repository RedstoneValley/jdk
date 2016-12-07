/*
 * Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.MenuComponent;
import java.awt.MenuContainer;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.InvocationEvent;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.InvocationTargetException;

import sun.awt.CausedFocusEvent.Cause;

/**
 * The AWTAccessor utility class. The main purpose of this class is to enable accessing private and
 * package-private fields of classes from different classes/packages. See sun.misc.SharedSecretes
 * for another example.
 */
public final class AWTAccessor {

  /*
   * Accessor instances are initialized in the static initializers of
   * corresponding AWT classes by using setters defined below.
   */
  private static ComponentAccessor componentAccessor;
  private static AWTEventAccessor awtEventAccessor;
  private static MenuComponentAccessor menuComponentAccessor;
  private static EventQueueAccessor eventQueueAccessor;
  private static InvocationEventAccessor invocationEventAccessor;

  /*
   * We don't need any objects of this class.
   * It's rather a collection of static methods
   * and interfaces.
   */
  private AWTAccessor() {
  }

  /*
   * Retrieve the accessor object for the java.awt.Component class.
   */
  public static ComponentAccessor getComponentAccessor() {
    if (componentAccessor == null) {

    }

    return componentAccessor;
  }

  /*
   * Set an accessor object for the java.awt.Component class.
   */
  public static void setComponentAccessor(ComponentAccessor ca) {
    componentAccessor = ca;
  }

  /*
   * Retrieve the accessor object for the java.awt.AWTEvent class.
   */
  public static AWTEventAccessor getAWTEventAccessor() {
    if (awtEventAccessor == null) {

    }
    return awtEventAccessor;
  }

  /*
   * Set an accessor object for the java.awt.AWTEvent class.
   */
  public static void setAWTEventAccessor(AWTEventAccessor aea) {
    awtEventAccessor = aea;
  }

  /*
   * Retrieve the accessor object for the java.awt.MenuComponent class.
   */
  public static MenuComponentAccessor getMenuComponentAccessor() {
    if (menuComponentAccessor == null) {

    }
    return menuComponentAccessor;
  }

  /*
   * Set an accessor object for the java.awt.MenuComponent class.
   */
  public static void setMenuComponentAccessor(MenuComponentAccessor mca) {
    menuComponentAccessor = mca;
  }

  /*
   * Retrieve the accessor object for the java.awt.EventQueue class.
   */
  public static EventQueueAccessor getEventQueueAccessor() {
    if (eventQueueAccessor == null) {

    }
    return eventQueueAccessor;
  }

  /*
   * Set an accessor object for the java.awt.EventQueue class.
   */
  public static void setEventQueueAccessor(EventQueueAccessor eqa) {
    eventQueueAccessor = eqa;
  }

  /*
   * Set the accessor object for the java.awt.event.InvocationEvent class.
   */
  public static InvocationEventAccessor getInvocationEventAccessor() {
    return invocationEventAccessor;
  }

  /*
   * Get the accessor object for the java.awt.event.InvocationEvent class.
   */
  public static void setInvocationEventAccessor(InvocationEventAccessor invocationEventAccessor) {
    AWTAccessor.invocationEventAccessor = invocationEventAccessor;
  }

  /*
   * An interface of accessor for the java.awt.Component class.
   */
  public interface ComponentAccessor {

    /*
     *
     * Gets the bounds of this component in the form of a
     * <code>Rectangle</code> object. The bounds specify this
     * component's width, height, and location relative to
     * its parent.
     */
    Rectangle getBounds(Component comp);

    /*
     * Requests focus to the component.
     */
    boolean requestFocus(Component comp, Cause cause);

    /**
     * Returns whether the component is visible without invoking any client code.
     */
    boolean isVisible(Component comp);

    /**
     * Returns the appContext of the component.
     */
    AppContext getAppContext(Component comp);

    /**
     * Sets the appContext of the component.
     */
    void setAppContext(Component comp, AppContext appContext);

    /**
     * Returns the parent of the component.
     */
    Container getParent(Component comp);

    /**
     * Resizes the component to the specified width and height.
     */
    void setSize(Component comp, int width, int height);

    /**
     * Returns the location of the component.
     */
    Point getLocation(Component comp);

    /**
     * Moves the component to the new location.
     */
    void setLocation(Component comp, int x, int y);

    /**
     * Determines whether this component is enabled.
     */
    boolean isEnabled(Component comp);

    /**
     * Determines whether this component is displayable.
     */
    boolean isDisplayable(Component comp);

    /**
     * Returns the peer of the component.
     */
    ComponentPeer getPeer(Component comp);

    /**
     * Returns the width of the component.
     */
    int getWidth(Component comp);

    /**
     * Returns the height of the component.
     */
    int getHeight(Component comp);

    /**
     * Returns the x coordinate of the component.
     */
    int getX(Component comp);

    /**
     * Returns the y coordinate of the component.
     */
    int getY(Component comp);

    /**
     * Gets the foreground color of this component.
     */
    Color getForeground(Component comp);

    /**
     * Gets the background color of this component.
     */
    Color getBackground(Component comp);

    /**
     * Sets the background of this component to the specified color.
     */
    void setBackground(Component comp, Color background);

    /**
     * Gets the font of the component.
     */
    Font getFont(Component comp);

    /**
     * Processes events occurring on this component.
     */
    void processEvent(Component comp, AWTEvent e);
  }

  /**
   * An accessor for the AWTEvent class.
   */
  public interface AWTEventAccessor {
    /**
     * Marks the event as posted.
     */
    void setPosted(AWTEvent ev);

    /**
     * Sets the flag on this AWTEvent indicating that it was generated by the system.
     */
    void setSystemGenerated(AWTEvent ev);

    /**
     * Indicates whether this AWTEvent was generated by the system.
     */
    boolean isSystemGenerated(AWTEvent ev);
  }

  /**
   * An accessor for the MenuComponent class.
   */
  public interface MenuComponentAccessor {
    /**
     * Returns the appContext of the menu component.
     */
    AppContext getAppContext(MenuComponent menuComp);

    /**
     * Sets the appContext of the menu component.
     */
    void setAppContext(MenuComponent menuComp, AppContext appContext);

    /**
     * Returns the menu container of the menu component
     */
    MenuContainer getParent(MenuComponent menuComp);
  }

  /**
   * An accessor for the EventQueue class
   */
  public interface EventQueueAccessor {
    /**
     * Gets the event dispatch thread.
     */
    Thread getDispatchThread(EventQueue eventQueue);

    /**
     * Checks if the current thread is EDT for the given EQ.
     */
    boolean isDispatchThreadImpl(EventQueue eventQueue);

    /**
     * Called from PostEventQueue.postEvent to notify that a new event appeared.
     */
    void wakeup(EventQueue eventQueue, boolean isShutdown);

    /**
     * Static in EventQueue
     */
    void invokeAndWait(Object source, Runnable r)
        throws InterruptedException, InvocationTargetException;

    /**
     * Gets most recent event time in the EventQueue
     */
    long getMostRecentEventTime(EventQueue eventQueue);
  }

  /*
   * An accessor object for the InvocationEvent class
   */
  public interface InvocationEventAccessor {
    void dispose(InvocationEvent event);
  }
}
