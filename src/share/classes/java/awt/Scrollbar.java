/*
 * Copyright (c) 1995, 2013, Oracle and/or its affiliates. All rights reserved.
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

import android.widget.ScrollView;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.peer.ScrollbarPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;

/**
 * The {@code Scrollbar} class embodies a scroll bar, a
 * familiar user-interface object. A scroll bar provides a
 * convenient means for allowing a user to select from a
 * range of values. The following three vertical
 * scroll bars could be used as slider controls to pick
 * the red, green, and blue components of a color:
 * <p>
 * <img src="doc-files/Scrollbar-1.gif" alt="Image shows 3 vertical sliders, side-by-side."
 * style="float:center; margin: 7px 10px;">
 * <p>
 * Each scroll bar in this example could be created with
 * code similar to the following:
 * <p>
 * <hr><blockquote><pre>
 * redSlider=new Scrollbar(Scrollbar.VERTICAL, 0, 1, 0, 255);
 * add(redSlider);
 * </pre></blockquote><hr>
 * <p>
 * Alternatively, a scroll bar can represent a range of values. For
 * example, if a scroll bar is used for scrolling through text, the
 * width of the "bubble" (also called the "thumb" or "scroll box")
 * can be used to represent the amount of text that is visible.
 * Here is an example of a scroll bar that represents a range:
 * <p>
 * <img src="doc-files/Scrollbar-2.gif"
 * alt="Image shows horizontal slider with starting range of 0 and ending range of 300. The
 * slider thumb is labeled 60."
 * style="float:center; margin: 7px 10px;">
 * <p>
 * The value range represented by the bubble in this example
 * is the <em>visible amount</em>. The horizontal scroll bar
 * in this example could be created with code like the following:
 * <p>
 * <hr><blockquote><pre>
 * ranger = new Scrollbar(Scrollbar.HORIZONTAL, 0, 60, 0, 300);
 * add(ranger);
 * </pre></blockquote><hr>
 * <p>
 * Note that the actual maximum value of the scroll bar is the
 * {@code maximum} minus the {@code visible amount}.
 * In the previous example, because the {@code maximum} is
 * 300 and the {@code visible amount} is 60, the actual maximum
 * value is 240.  The range of the scrollbar track is 0 - 300.
 * The left side of the bubble indicates the value of the
 * scroll bar.
 * <p>
 * Normally, the user changes the value of the scroll bar by
 * making a gesture with the mouse. For example, the user can
 * drag the scroll bar's bubble up and down, or click in the
 * scroll bar's unit increment or block increment areas. Keyboard
 * gestures can also be mapped to the scroll bar. By convention,
 * the <b>Page&nbsp;Up</b> and <b>Page&nbsp;Down</b>
 * keys are equivalent to clicking in the scroll bar's block
 * increment and block decrement areas.
 * <p>
 * When the user changes the value of the scroll bar, the scroll bar
 * receives an instance of {@code AdjustmentEvent}.
 * The scroll bar processes this event, passing it along to
 * any registered listeners.
 * <p>
 * Any object that wishes to be notified of changes to the
 * scroll bar's value should implement
 * {@code AdjustmentListener}, an interface defined in
 * the package {@code java.awt.event}.
 * Listeners can be added and removed dynamically by calling
 * the methods {@code addAdjustmentListener} and
 * {@code removeAdjustmentListener}.
 * <p>
 * The {@code AdjustmentEvent} class defines five types
 * of adjustment event, listed here:
 * <p>
 * <ul>
 * <li>{@code AdjustmentEvent.TRACK} is sent out when the
 * user drags the scroll bar's bubble.
 * <li>{@code AdjustmentEvent.UNIT_INCREMENT} is sent out
 * when the user clicks in the left arrow of a horizontal scroll
 * bar, or the top arrow of a vertical scroll bar, or makes the
 * equivalent gesture from the keyboard.
 * <li>{@code AdjustmentEvent.UNIT_DECREMENT} is sent out
 * when the user clicks in the right arrow of a horizontal scroll
 * bar, or the bottom arrow of a vertical scroll bar, or makes the
 * equivalent gesture from the keyboard.
 * <li>{@code AdjustmentEvent.BLOCK_INCREMENT} is sent out
 * when the user clicks in the track, to the left of the bubble
 * on a horizontal scroll bar, or above the bubble on a vertical
 * scroll bar. By convention, the <b>Page&nbsp;Up</b>
 * key is equivalent, if the user is using a keyboard that
 * defines a <b>Page&nbsp;Up</b> key.
 * <li>{@code AdjustmentEvent.BLOCK_DECREMENT} is sent out
 * when the user clicks in the track, to the right of the bubble
 * on a horizontal scroll bar, or below the bubble on a vertical
 * scroll bar. By convention, the <b>Page&nbsp;Down</b>
 * key is equivalent, if the user is using a keyboard that
 * defines a <b>Page&nbsp;Down</b> key.
 * </ul>
 * <p>
 * The JDK&nbsp;1.0 event system is supported for backwards
 * compatibility, but its use with newer versions of the platform is
 * discouraged. The five types of adjustment events introduced
 * with JDK&nbsp;1.1 correspond to the five event types
 * that are associated with scroll bars in previous platform versions.
 * The following list gives the adjustment event type,
 * and the corresponding JDK&nbsp;1.0 event type it replaces.
 * <p>
 * <ul>
 * <li>{@code AdjustmentEvent.TRACK} replaces
 * {@code Event.SCROLL_ABSOLUTE}
 * <li>{@code AdjustmentEvent.UNIT_INCREMENT} replaces
 * {@code Event.SCROLL_LINE_UP}
 * <li>{@code AdjustmentEvent.UNIT_DECREMENT} replaces
 * {@code Event.SCROLL_LINE_DOWN}
 * <li>{@code AdjustmentEvent.BLOCK_INCREMENT} replaces
 * {@code Event.SCROLL_PAGE_UP}
 * <li>{@code AdjustmentEvent.BLOCK_DECREMENT} replaces
 * {@code Event.SCROLL_PAGE_DOWN}
 * </ul>
 * <p>
 * <b>Note</b>: We recommend using a {@code Scrollbar}
 * for value selection only.  If you want to implement
 * a scrollable component inside a container, we recommend you use
 * a {@link ScrollPane ScrollPane}. If you use a
 * {@code Scrollbar} for this purpose, you are likely to
 * encounter issues with painting, key handling, sizing and
 * positioning.
 *
 * @author Sami Shaio
 * @see AdjustmentEvent
 * @see AdjustmentListener
 * @since JDK1.0
 */
public class Scrollbar extends Component implements Adjustable {

  /**
   * A constant that indicates a horizontal scroll bar.
   */
  public static final int HORIZONTAL = 0;

  /**
   * A constant that indicates a vertical scroll bar.
   */
  public static final int VERTICAL = 1;
  private static final String base = "scrollbar";
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = 8451667562882310543L;
  private static int nameCounter;

  static {
        /* ensure that the necessary native libraries are loaded */
    Toolkit.loadLibraries();
  }

  /**
   * The scroll bar's serialized Data Version.
   *
   * @serial
   */
  private final int scrollbarSerializedDataVersion = 1;
  /**
   * The value of the {@code Scrollbar}.
   * This property must be greater than or equal to {@code minimum}
   * and less than or equal to
   * {@code maximum - visibleAmount}
   *
   * @serial
   * @see #getValue
   * @see #setValue
   */
  int value;
  /**
   * The maximum value of the {@code Scrollbar}.
   * This value must be greater than the {@code minimum}
   * value.<br>
   *
   * @serial
   * @see #getMaximum
   * @see #setMaximum
   */
  int maximum;
  /**
   * The minimum value of the {@code Scrollbar}.
   * This value must be less than the {@code maximum}
   * value.<br>
   *
   * @serial
   * @see #getMinimum
   * @see #setMinimum
   */
  int minimum;
  /**
   * The size of the {@code Scrollbar}'s bubble.
   * When a scroll bar is used to select a range of values,
   * the visibleAmount represents the size of this range.
   * Depending on platform, this may be visually indicated
   * by the size of the bubble.
   *
   * @serial
   * @see #getVisibleAmount
   * @see #setVisibleAmount
   */
  int visibleAmount;
  /**
   * The {@code Scrollbar}'s orientation--being either horizontal
   * or vertical.
   * This value should be specified when the scrollbar is created.<BR>
   * orientation can be either : {@code VERTICAL} or
   * {@code HORIZONTAL} only.
   *
   * @serial
   * @see #getOrientation
   * @see #setOrientation
   */
  int orientation;
  /**
   * The amount by which the scrollbar value will change when going
   * up or down by a line.
   * This value must be greater than zero.
   *
   * @serial
   * @see #getLineIncrement
   * @see #setLineIncrement
   */
  int lineIncrement = 1;
  /**
   * The amount by which the scrollbar value will change when going
   * up or down by a page.
   * This value must be greater than zero.
   *
   * @serial
   * @see #getPageIncrement
   * @see #setPageIncrement
   */
  int pageIncrement = 10;
  /**
   * The adjusting status of the {@code Scrollbar}.
   * True if the value is in the process of changing as a result of
   * actions being taken by the user.
   *
   * @see #getValueIsAdjusting
   * @see #setValueIsAdjusting
   * @since 1.4
   */
  transient boolean isAdjusting;
  transient AdjustmentListener adjustmentListener;

  /**
   * Constructs a new vertical scroll bar.
   * The default properties of the scroll bar are listed in
   * the following table:
   * <p>
   * <table border=1 summary="Scrollbar default properties">
   * <tr>
   * <th>Property</th>
   * <th>Description</th>
   * <th>Default Value</th>
   * </tr>
   * <tr>
   * <td>orientation</td>
   * <td>indicates whether the scroll bar is vertical
   * <br>or horizontal</td>
   * <td>{@code Scrollbar.VERTICAL}</td>
   * </tr>
   * <tr>
   * <td>value</td>
   * <td>value which controls the location
   * <br>of the scroll bar's bubble</td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>visible amount</td>
   * <td>visible amount of the scroll bar's range,
   * <br>typically represented by the size of the
   * <br>scroll bar's bubble</td>
   * <td>10</td>
   * </tr>
   * <tr>
   * <td>minimum</td>
   * <td>minimum value of the scroll bar</td>
   * <td>0</td>
   * </tr>
   * <tr>
   * <td>maximum</td>
   * <td>maximum value of the scroll bar</td>
   * <td>100</td>
   * </tr>
   * <tr>
   * <td>unit increment</td>
   * <td>amount the value changes when the
   * <br>Line Up or Line Down key is pressed,
   * <br>or when the end arrows of the scrollbar
   * <br>are clicked </td>
   * <td>1</td>
   * </tr>
   * <tr>
   * <td>block increment</td>
   * <td>amount the value changes when the
   * <br>Page Up or Page Down key is pressed,
   * <br>or when the scrollbar track is clicked
   * <br>on either side of the bubble </td>
   * <td>10</td>
   * </tr>
   * </table>
   *
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true.
   * @see GraphicsEnvironment#isHeadless
   */
  public Scrollbar() throws HeadlessException {
    this(VERTICAL, 0, 10, 0, 100);
  }

  /**
   * Constructs a new scroll bar with the specified orientation.
   * <p>
   * The {@code orientation} argument must take one of the two
   * values {@code Scrollbar.HORIZONTAL},
   * or {@code Scrollbar.VERTICAL},
   * indicating a horizontal or vertical scroll bar, respectively.
   *
   * @param orientation indicates the orientation of the scroll bar
   * @throws IllegalArgumentException when an illegal value for
   *                                  the {@code orientation} argument is supplied
   * @throws HeadlessException        if GraphicsEnvironment.isHeadless()
   *                                  returns true.
   * @see GraphicsEnvironment#isHeadless
   */
  public Scrollbar(int orientation) throws HeadlessException {
    this(orientation, 0, 10, 0, 100);
  }

  /**
   * Constructs a new scroll bar with the specified orientation,
   * initial value, visible amount, and minimum and maximum values.
   * <p>
   * The {@code orientation} argument must take one of the two
   * values {@code Scrollbar.HORIZONTAL},
   * or {@code Scrollbar.VERTICAL},
   * indicating a horizontal or vertical scroll bar, respectively.
   * <p>
   * The parameters supplied to this constructor are subject to the
   * constraints described in {@link #setValues(int, int, int, int)}.
   *
   * @param orientation indicates the orientation of the scroll bar.
   * @param value       the initial value of the scroll bar
   * @param visible     the visible amount of the scroll bar, typically
   *                    represented by the size of the bubble
   * @param minimum     the minimum value of the scroll bar
   * @param maximum     the maximum value of the scroll bar
   * @throws IllegalArgumentException when an illegal value for
   *                                  the {@code orientation} argument is supplied
   * @throws HeadlessException        if GraphicsEnvironment.isHeadless()
   *                                  returns true.
   * @see #setValues
   * @see GraphicsEnvironment#isHeadless
   */
  public Scrollbar(
      int orientation, int value, int visible, int minimum, int maximum) throws HeadlessException {
    super(ScrollView.class);
    GraphicsEnvironment.checkHeadless();
    switch (orientation) {
      case HORIZONTAL:
      case VERTICAL:
        this.orientation = orientation;
        break;
      default:
        throw new IllegalArgumentException("illegal scrollbar orientation");
    }
    setValues(value, visible, minimum, maximum);
  }

  /**
   * Constructs a name for this component.  Called by {@code getName}
   * when the name is {@code null}.
   */
  @Override
  String constructComponentName() {
    synchronized (Scrollbar.class) {
      String result = base + nameCounter;
      nameCounter++;
      return result;
    }
  }

  // REMIND: remove when filtering is done at lower level
  @Override
  boolean eventEnabled(AWTEvent e) {
    if (e.id == AdjustmentEvent.ADJUSTMENT_VALUE_CHANGED) {
      return (eventMask & AWTEvent.ADJUSTMENT_EVENT_MASK) != 0 || adjustmentListener != null;
    }
    return super.eventEnabled(e);
  }

  /**
   * Returns an array of all the objects currently registered
   * as <code><em>Foo</em>Listener</code>s
   * upon this {@code Scrollbar}.
   * <code><em>Foo</em>Listener</code>s are registered using the
   * <code>add<em>Foo</em>Listener</code> method.
   * <p>
   * You can specify the {@code listenerType} argument
   * with a class literal,  such as
   * <code><em>Foo</em>Listener.class</code>.
   * For example, you can query a
   * {@code Scrollbar} {@code c}
   * for its mouse listeners with the following code:
   * <p>
   * <pre>MouseListener[] mls = (MouseListener[])(c.getListeners(MouseListener.class));</pre>
   *
   * If no such listeners exist, this method returns an empty array.
   *
   * @param listenerType the type of listeners requested; this parameter
   *                     should specify an interface that descends from
   *                     {@code java.util.EventListener}
   * @return an array of all objects registered as
   * <code><em>Foo</em>Listener</code>s on this component,
   * or an empty array if no such listeners have been added
   * @throws ClassCastException if {@code listenerType}
   *                            doesn't specify a class or interface that implements
   *                            {@code java.util.EventListener}
   * @since 1.3
   */
  @Override
  public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
    EventListener l;
    if (listenerType == AdjustmentListener.class) {
      l = adjustmentListener;
    } else {
      return super.getListeners(listenerType);
    }
    return AWTEventMulticaster.getListeners(l, listenerType);
  }

  /**
   * Processes events on this scroll bar. If the event is an
   * instance of {@code AdjustmentEvent}, it invokes the
   * {@code processAdjustmentEvent} method.
   * Otherwise, it invokes its superclass's
   * {@code processEvent} method.
   * <p>Note that if the event parameter is {@code null}
   * the behavior is unspecified and may result in an
   * exception.
   *
   * @param e the event
   * @see AdjustmentEvent
   * @see Scrollbar#processAdjustmentEvent
   * @since JDK1.1
   */
  @Override
  protected void processEvent(AWTEvent e) {
    if (e instanceof AdjustmentEvent) {
      processAdjustmentEvent((AdjustmentEvent) e);
      return;
    }
    super.processEvent(e);
  }

  /**
   * Creates the {@code Scrollbar}'s peer.  The peer allows you to modify
   * the appearance of the {@code Scrollbar} without changing any of its
   * functionality.
   */
  @Override
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (peer == null) {
        peer = getToolkit().createScrollbar(this);
      }
      super.addNotify();
    }
  }

  /**
   * Returns a string representing the state of this {@code Scrollbar}.
   * This method is intended to be used only for debugging purposes, and the
   * content and format of the returned string may vary between
   * implementations. The returned string may be empty but may not be
   * {@code null}.
   *
   * @return the parameter string of this scroll bar
   */
  @Override
  protected String paramString() {
    return super.paramString() +
        ",val=" + value +
        ",vis=" + visibleAmount +
        ",min=" + minimum +
        ",max=" + maximum +
        (orientation == VERTICAL ? ",vert" : ",horz") +
        ",isAdjusting=" + isAdjusting;
  }

  /**
   * Returns the orientation of this scroll bar.
   *
   * @return the orientation of this scroll bar, either
   * {@code Scrollbar.HORIZONTAL} or
   * {@code Scrollbar.VERTICAL}
   * @see Scrollbar#setOrientation
   */
  @Override
  public int getOrientation() {
    return orientation;
  }

  /**
   * Gets the minimum value of this scroll bar.
   *
   * @return the minimum value of this scroll bar
   * @see Scrollbar#getValue
   * @see Scrollbar#getMaximum
   */
  @Override
  public int getMinimum() {
    return minimum;
  }

  /**
   * Sets the minimum value of this scroll bar.
   * <p>
   * When {@code setMinimum} is called, the minimum value
   * is changed, and other values (including the maximum, the
   * visible amount, and the current scroll bar value)
   * are changed to be consistent with the new minimum.
   * <p>
   * Normally, a program should change a scroll bar's minimum
   * value only by calling {@code setValues}.
   * The {@code setValues} method simultaneously
   * and synchronously sets the minimum, maximum, visible amount,
   * and value properties of a scroll bar, so that they are
   * mutually consistent.
   * <p>
   * Note that setting the minimum value to {@code Integer.MAX_VALUE}
   * will result in the new minimum value being set to
   * {@code Integer.MAX_VALUE - 1}.
   *
   * @param newMinimum the new minimum value for this scroll bar
   * @see Scrollbar#setValues
   * @see Scrollbar#setMaximum
   * @since JDK1.1
   */
  @Override
  public void setMinimum(int newMinimum) {
    // No checks are necessary in this method since minimum is
    // the first variable checked in the setValues function.

    // Use setValues so that a consistent policy relating
    // minimum, maximum, visible amount, and value is enforced.
    setValues(value, visibleAmount, newMinimum, maximum);
  }

  /**
   * Gets the maximum value of this scroll bar.
   *
   * @return the maximum value of this scroll bar
   * @see Scrollbar#getValue
   * @see Scrollbar#getMinimum
   */
  @Override
  public int getMaximum() {
    return maximum;
  }

  /**
   * Sets the maximum value of this scroll bar.
   * <p>
   * When {@code setMaximum} is called, the maximum value
   * is changed, and other values (including the minimum, the
   * visible amount, and the current scroll bar value)
   * are changed to be consistent with the new maximum.
   * <p>
   * Normally, a program should change a scroll bar's maximum
   * value only by calling {@code setValues}.
   * The {@code setValues} method simultaneously
   * and synchronously sets the minimum, maximum, visible amount,
   * and value properties of a scroll bar, so that they are
   * mutually consistent.
   * <p>
   * Note that setting the maximum value to {@code Integer.MIN_VALUE}
   * will result in the new maximum value being set to
   * {@code Integer.MIN_VALUE + 1}.
   *
   * @param newMaximum the new maximum value
   *                   for this scroll bar
   * @see Scrollbar#setValues
   * @see Scrollbar#setMinimum
   * @since JDK1.1
   */
  @Override
  public void setMaximum(int newMaximum) {
    // minimum is checked first in setValues, so we need to
    // enforce minimum and maximum checks here.
    if (newMaximum == Integer.MIN_VALUE) {
      newMaximum = Integer.MIN_VALUE + 1;
    }

    if (minimum >= newMaximum) {
      minimum = newMaximum - 1;
    }

    // Use setValues so that a consistent policy relating
    // minimum, maximum, visible amount, and value is enforced.
    setValues(value, visibleAmount, minimum, newMaximum);
  }

  /**
   * Gets the unit increment for this scrollbar.
   * <p>
   * The unit increment is the value that is added or subtracted
   * when the user activates the unit increment area of the
   * scroll bar, generally through a mouse or keyboard gesture
   * that the scroll bar receives as an adjustment event.
   * The unit increment must be greater than zero.
   * <p>
   * In some operating systems, this property
   * can be ignored by the underlying controls.
   *
   * @return the unit increment of this scroll bar
   * @see Scrollbar#setUnitIncrement
   * @since JDK1.1
   */
  @Override
  public int getUnitIncrement() {
    return getLineIncrement();
  }

  /**
   * Sets the unit increment for this scroll bar.
   * <p>
   * The unit increment is the value that is added or subtracted
   * when the user activates the unit increment area of the
   * scroll bar, generally through a mouse or keyboard gesture
   * that the scroll bar receives as an adjustment event.
   * The unit increment must be greater than zero.
   * Attepts to set the unit increment to a value lower than 1
   * will result in a value of 1 being set.
   * <p>
   * In some operating systems, this property
   * can be ignored by the underlying controls.
   *
   * @param v the amount by which to increment or decrement
   *          the scroll bar's value
   * @see Scrollbar#getUnitIncrement
   * @since JDK1.1
   */
  @Override
  public void setUnitIncrement(int v) {
    setLineIncrement(v);
  }

  /**
   * Gets the block increment of this scroll bar.
   * <p>
   * The block increment is the value that is added or subtracted
   * when the user activates the block increment area of the
   * scroll bar, generally through a mouse or keyboard gesture
   * that the scroll bar receives as an adjustment event.
   * The block increment must be greater than zero.
   *
   * @return the block increment of this scroll bar
   * @see Scrollbar#setBlockIncrement
   * @since JDK1.1
   */
  @Override
  public int getBlockIncrement() {
    return getPageIncrement();
  }

  /**
   * Sets the block increment for this scroll bar.
   * <p>
   * The block increment is the value that is added or subtracted
   * when the user activates the block increment area of the
   * scroll bar, generally through a mouse or keyboard gesture
   * that the scroll bar receives as an adjustment event.
   * The block increment must be greater than zero.
   * Attepts to set the block increment to a value lower than 1
   * will result in a value of 1 being set.
   *
   * @param v the amount by which to increment or decrement
   *          the scroll bar's value
   * @see Scrollbar#getBlockIncrement
   * @since JDK1.1
   */
  @Override
  public void setBlockIncrement(int v) {
    setPageIncrement(v);
  }

  /**
   * Gets the visible amount of this scroll bar.
   * <p>
   * When a scroll bar is used to select a range of values,
   * the visible amount is used to represent the range of values
   * that are currently visible.  The size of the scroll bar's
   * bubble (also called a thumb or scroll box), usually gives a
   * visual representation of the relationship of the visible
   * amount to the range of the scroll bar.
   * Note that depending on platform, the value of the visible amount property
   * may not be visually indicated by the size of the bubble.
   * <p>
   * The scroll bar's bubble may not be displayed when it is not
   * moveable (e.g. when it takes up the entire length of the
   * scroll bar's track, or when the scroll bar is disabled).
   * Whether the bubble is displayed or not will not affect
   * the value returned by {@code getVisibleAmount}.
   *
   * @return the visible amount of this scroll bar
   * @see Scrollbar#setVisibleAmount
   * @since JDK1.1
   */
  @Override
  public int getVisibleAmount() {
    return getVisible();
  }

  /**
   * Sets the visible amount of this scroll bar.
   * <p>
   * When a scroll bar is used to select a range of values,
   * the visible amount is used to represent the range of values
   * that are currently visible.  The size of the scroll bar's
   * bubble (also called a thumb or scroll box), usually gives a
   * visual representation of the relationship of the visible
   * amount to the range of the scroll bar.
   * Note that depending on platform, the value of the visible amount property
   * may not be visually indicated by the size of the bubble.
   * <p>
   * The scroll bar's bubble may not be displayed when it is not
   * moveable (e.g. when it takes up the entire length of the
   * scroll bar's track, or when the scroll bar is disabled).
   * Whether the bubble is displayed or not will not affect
   * the value returned by {@code getVisibleAmount}.
   * <p>
   * If the visible amount supplied is less than {@code one}
   * or greater than the current {@code maximum - minimum},
   * then either {@code one} or {@code maximum - minimum}
   * is substituted, as appropriate.
   * <p>
   * Normally, a program should change a scroll bar's
   * value only by calling {@code setValues}.
   * The {@code setValues} method simultaneously
   * and synchronously sets the minimum, maximum, visible amount,
   * and value properties of a scroll bar, so that they are
   * mutually consistent.
   *
   * @param newAmount the new visible amount
   * @see Scrollbar#getVisibleAmount
   * @see Scrollbar#setValues
   * @since JDK1.1
   */
  @Override
  public void setVisibleAmount(int newAmount) {
    // Use setValues so that a consistent policy relating
    // minimum, maximum, visible amount, and value is enforced.
    setValues(value, newAmount, minimum, maximum);
  }

  /**
   * Gets the current value of this scroll bar.
   *
   * @return the current value of this scroll bar
   * @see Scrollbar#getMinimum
   * @see Scrollbar#getMaximum
   */
  @Override
  public int getValue() {
    return value;
  }

  /**
   * Sets the value of this scroll bar to the specified value.
   * <p>
   * If the value supplied is less than the current {@code minimum}
   * or greater than the current {@code maximum - visibleAmount},
   * then either {@code minimum} or {@code maximum - visibleAmount}
   * is substituted, as appropriate.
   * <p>
   * Normally, a program should change a scroll bar's
   * value only by calling {@code setValues}.
   * The {@code setValues} method simultaneously
   * and synchronously sets the minimum, maximum, visible amount,
   * and value properties of a scroll bar, so that they are
   * mutually consistent.
   * <p>
   * Calling this method does not fire an
   * {@code AdjustmentEvent}.
   *
   * @param newValue the new value of the scroll bar
   * @see Scrollbar#setValues
   * @see Scrollbar#getValue
   * @see Scrollbar#getMinimum
   * @see Scrollbar#getMaximum
   */
  @Override
  public void setValue(int newValue) {
    // Use setValues so that a consistent policy relating
    // minimum, maximum, visible amount, and value is enforced.
    setValues(newValue, visibleAmount, minimum, maximum);
  }

  /**
   * Adds the specified adjustment listener to receive instances of
   * {@code AdjustmentEvent} from this scroll bar.
   * If l is {@code null}, no exception is thrown and no
   * action is performed.
   * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
   * >AWT Threading Issues</a> for details on AWT's threading model.
   *
   * @param l the adjustment listener
   * @see #removeAdjustmentListener
   * @see #getAdjustmentListeners
   * @see AdjustmentEvent
   * @see AdjustmentListener
   * @since JDK1.1
   */
  @Override
  public synchronized void addAdjustmentListener(AdjustmentListener l) {
    if (l == null) {
      return;
    }
    adjustmentListener = AWTEventMulticaster.add(adjustmentListener, l);
    newEventsOnly = true;
  }

  /**
   * Removes the specified adjustment listener so that it no longer
   * receives instances of {@code AdjustmentEvent} from this scroll bar.
   * If l is {@code null}, no exception is thrown and no action
   * is performed.
   * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
   * >AWT Threading Issues</a> for details on AWT's threading model.
   *
   * @param l the adjustment listener
   * @see #addAdjustmentListener
   * @see #getAdjustmentListeners
   * @see AdjustmentEvent
   * @see AdjustmentListener
   * @since JDK1.1
   */
  @Override
  public synchronized void removeAdjustmentListener(AdjustmentListener l) {
    if (l == null) {
      return;
    }
    adjustmentListener = AWTEventMulticaster.remove(adjustmentListener, l);
  }

  /**
   * Sets the orientation for this scroll bar.
   *
   * @param orientation the orientation of this scroll bar, either
   *                    {@code Scrollbar.HORIZONTAL} or
   *                    {@code Scrollbar.VERTICAL}
   * @throws IllegalArgumentException if the value supplied
   *                                  for {@code orientation} is not a
   *                                  legal value
   * @see Scrollbar#getOrientation
   * @since JDK1.1
   */
  public void setOrientation(int orientation) {
    synchronized (getTreeLock()) {
      if (orientation == this.orientation) {
        return;
      }
      switch (orientation) {
        case HORIZONTAL:
        case VERTICAL:
          this.orientation = orientation;
          break;
        default:
          throw new IllegalArgumentException("illegal scrollbar orientation");
      }
            /* Create a new peer with the specified orientation. */
      if (peer != null) {
        removeNotify();
        addNotify();
        invalidate();
      }
    }
  }

  /**
   * @deprecated As of JDK version 1.1,
   * replaced by {@code getVisibleAmount()}.
   */
  @Deprecated
  public int getVisible() {
    return visibleAmount;
  }

  /**
   * @deprecated As of JDK version 1.1,
   * replaced by {@code getUnitIncrement()}.
   */
  @Deprecated
  public int getLineIncrement() {
    return lineIncrement;
  }

  /**
   * @deprecated As of JDK version 1.1,
   * replaced by {@code setUnitIncrement(int)}.
   */
  @Deprecated
  public synchronized void setLineIncrement(int v) {
    int tmp = v < 1 ? 1 : v;

    if (lineIncrement == tmp) {
      return;
    }
    lineIncrement = tmp;

    ScrollbarPeer peer = (ScrollbarPeer) this.peer;
    if (peer != null) {
      peer.setLineIncrement(lineIncrement);
    }
  }

  /**
   * @deprecated As of JDK version 1.1,
   * replaced by {@code getBlockIncrement()}.
   */
  @Deprecated
  public int getPageIncrement() {
    return pageIncrement;
  }

  /**
   * @deprecated As of JDK version 1.1,
   * replaced by {@code setBlockIncrement()}.
   */
  @Deprecated
  public synchronized void setPageIncrement(int v) {
    int tmp = v < 1 ? 1 : v;

    if (pageIncrement == tmp) {
      return;
    }
    pageIncrement = tmp;

    ScrollbarPeer peer = (ScrollbarPeer) this.peer;
    if (peer != null) {
      peer.setPageIncrement(pageIncrement);
    }
  }

  /**
   * Sets the values of four properties for this scroll bar:
   * {@code value}, {@code visibleAmount},
   * {@code minimum}, and {@code maximum}.
   * If the values supplied for these properties are inconsistent
   * or incorrect, they will be changed to ensure consistency.
   * <p>
   * This method simultaneously and synchronously sets the values
   * of four scroll bar properties, assuring that the values of
   * these properties are mutually consistent. It enforces the
   * following constraints:
   * {@code maximum} must be greater than {@code minimum},
   * {@code maximum - minimum} must not be greater
   * than {@code Integer.MAX_VALUE},
   * {@code visibleAmount} must be greater than zero.
   * {@code visibleAmount} must not be greater than
   * {@code maximum - minimum},
   * {@code value} must not be less than {@code minimum},
   * and {@code value} must not be greater than
   * {@code maximum - visibleAmount}
   * <p>
   * Calling this method does not fire an
   * {@code AdjustmentEvent}.
   *
   * @param value   is the position in the current window
   * @param visible is the visible amount of the scroll bar
   * @param minimum is the minimum value of the scroll bar
   * @param maximum is the maximum value of the scroll bar
   * @see #setMinimum
   * @see #setMaximum
   * @see #setVisibleAmount
   * @see #setValue
   */
  public void setValues(int value, int visible, int minimum, int maximum) {
    int oldValue;
    synchronized (this) {
      if (minimum == Integer.MAX_VALUE) {
        minimum = Integer.MAX_VALUE - 1;
      }
      if (maximum <= minimum) {
        maximum = minimum + 1;
      }

      long maxMinusMin = (long) maximum - (long) minimum;
      if (maxMinusMin > Integer.MAX_VALUE) {
        maxMinusMin = Integer.MAX_VALUE;
        maximum = minimum + (int) maxMinusMin;
      }
      if (visible > (int) maxMinusMin) {
        visible = (int) maxMinusMin;
      }
      if (visible < 1) {
        visible = 1;
      }

      if (value < minimum) {
        value = minimum;
      }
      if (value > maximum - visible) {
        value = maximum - visible;
      }

      this.value = value;
      visibleAmount = visible;
      this.minimum = minimum;
      this.maximum = maximum;
      ScrollbarPeer peer = (ScrollbarPeer) this.peer;
      if (peer != null) {
        peer.setValues(value, visibleAmount, minimum, maximum);
      }
    }
  }

  /**
   * Returns true if the value is in the process of changing as a
   * result of actions being taken by the user.
   *
   * @return the value of the {@code valueIsAdjusting} property
   * @see #setValueIsAdjusting
   * @since 1.4
   */
  public boolean getValueIsAdjusting() {
    return isAdjusting;
  }

  /**
   * Sets the {@code valueIsAdjusting} property.
   *
   * @param b new adjustment-in-progress status
   * @see #getValueIsAdjusting
   * @since 1.4
   */
  public void setValueIsAdjusting(boolean b) {
    boolean oldValue;

    synchronized (this) {
      isAdjusting = b;
    }
  }

  /**
   * Returns an array of all the adjustment listeners
   * registered on this scrollbar.
   *
   * @return all of this scrollbar's {@code AdjustmentListener}s
   * or an empty array if no adjustment
   * listeners are currently registered
   * @see #addAdjustmentListener
   * @see #removeAdjustmentListener
   * @see AdjustmentEvent
   * @see AdjustmentListener
   * @since 1.4
   */
  public synchronized AdjustmentListener[] getAdjustmentListeners() {
    return getListeners(AdjustmentListener.class);
  }

  /**
   * Processes adjustment events occurring on this
   * scrollbar by dispatching them to any registered
   * {@code AdjustmentListener} objects.
   * <p>
   * This method is not called unless adjustment events are
   * enabled for this component. Adjustment events are enabled
   * when one of the following occurs:
   * <ul>
   * <li>An {@code AdjustmentListener} object is registered
   * via {@code addAdjustmentListener}.
   * <li>Adjustment events are enabled via {@code enableEvents}.
   * </ul>
   * <p>Note that if the event parameter is {@code null}
   * the behavior is unspecified and may result in an
   * exception.
   *
   * @param e the adjustment event
   * @see AdjustmentEvent
   * @see AdjustmentListener
   * @see Scrollbar#addAdjustmentListener
   * @see Component#enableEvents
   * @since JDK1.1
   */
  protected void processAdjustmentEvent(AdjustmentEvent e) {
    AdjustmentListener listener = adjustmentListener;
    if (listener != null) {
      listener.adjustmentValueChanged(e);
    }
  }

  /**
   * Writes default serializable fields to stream.  Writes
   * a list of serializable {@code AdjustmentListeners}
   * as optional data. The non-serializable listeners are
   * detected and no attempt is made to serialize them.
   *
   * @param s the {@code ObjectOutputStream} to write
   * @serialData {@code null} terminated sequence of 0
   * or more pairs; the pair consists of a {@code String}
   * and an {@code Object}; the {@code String} indicates
   * the type of object and is one of the following:
   * {@code adjustmentListenerK} indicating an
   * {@code AdjustmentListener} object
   * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
   * @see Component#adjustmentListenerK
   * @see #readObject(ObjectInputStream)
   */
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();

    AWTEventMulticaster.save(s, adjustmentListenerK, adjustmentListener);
    s.writeObject(null);
  }

  /**
   * Reads the {@code ObjectInputStream} and if
   * it isn't {@code null} adds a listener to
   * receive adjustment events fired by the
   * {@code Scrollbar}.
   * Unrecognized keys or values will be ignored.
   *
   * @param s the {@code ObjectInputStream} to read
   * @throws HeadlessException if
   *                           {@code GraphicsEnvironment.isHeadless} returns
   *                           {@code true}
   * @see GraphicsEnvironment#isHeadless
   * @see #writeObject(ObjectOutputStream)
   */
  private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    s.defaultReadObject();

    Object keyOrNull;
    while (null != (keyOrNull = s.readObject())) {
      String key = ((String) keyOrNull).intern();

      if (adjustmentListenerK == key) {
        addAdjustmentListener((AdjustmentListener) s.readObject());
      } else // skip value for unrecognized key
      {
        s.readObject();
      }
    }
  }














    /* Serialization support.
     */
}
