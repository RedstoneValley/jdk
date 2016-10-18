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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.peer.ButtonPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;

/**
 * This class creates a labeled button. The application can cause
 * some action to happen when the button is pushed. This image
 * depicts three views of a "{@code Quit}" button as it appears
 * under the Solaris operating system:
 * <p>
 * <img src="doc-files/Button-1.gif" alt="The following context describes the graphic"
 * style="float:center; margin: 7px 10px;">
 * <p>
 * The first view shows the button as it appears normally.
 * The second view shows the button
 * when it has input focus. Its outline is darkened to let the
 * user know that it is an active object. The third view shows the
 * button when the user clicks the mouse over the button, and thus
 * requests that an action be performed.
 * <p>
 * The gesture of clicking on a button with the mouse
 * is associated with one instance of {@code ActionEvent},
 * which is sent out when the mouse is both pressed and released
 * over the button. If an application is interested in knowing
 * when the button has been pressed but not released, as a separate
 * gesture, it can specialize {@code processMouseEvent},
 * or it can register itself as a listener for mouse events by
 * calling {@code addMouseListener}. Both of these methods are
 * defined by {@code Component}, the abstract superclass of
 * all components.
 * <p>
 * When a button is pressed and released, AWT sends an instance
 * of {@code ActionEvent} to the button, by calling
 * {@code processEvent} on the button. The button's
 * {@code processEvent} method receives all events
 * for the button; it passes an action event along by
 * calling its own {@code processActionEvent} method.
 * The latter method passes the action event on to any action
 * listeners that have registered an interest in action
 * events generated by this button.
 * <p>
 * If an application wants to perform some action based on
 * a button being pressed and released, it should implement
 * {@code ActionListener} and register the new listener
 * to receive events from this button, by calling the button's
 * {@code addActionListener} method. The application can
 * make use of the button's action command as a messaging protocol.
 *
 * @author Sami Shaio
 * @see ActionEvent
 * @see ActionListener
 * @see Component#processMouseEvent
 * @see Component#addMouseListener
 * @since JDK1.0
 */
public class Button extends Component {

  public static final String base = "button";
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = -8774683716313001058L;
  private static int nameCounter;
  /*
   * Button Serial Data Version.
   * @serial
   */
  private final int buttonSerializedDataVersion = 1;
  /**
   * The button's label.  This value may be null.
   *
   * @serial
   * @see #getLabel()
   * @see #setLabel(String)
   */
  String label;
  /**
   * The action to be performed once a button has been
   * pressed.  This value may be null.
   *
   * @serial
   * @see #getActionCommand()
   * @see #setActionCommand(String)
   */
  String actionCommand;
  transient ActionListener actionListener;

  /**
   * Constructs a button with an empty string for its label.
   *
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true
   * @see GraphicsEnvironment#isHeadless
   */
  public Button() throws HeadlessException {
    this("");
  }

  /**
   * Constructs a button with the specified label.
   *
   * @param label a string label for the button, or
   *              {@code null} for no label
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true
   * @see GraphicsEnvironment#isHeadless
   */
  public Button(String label) throws HeadlessException {
    super(android.widget.Button.class);
    this.label = label;
    SkinJobButtonPeer peer = new SkinJobButtonPeer(this);
    this.peer = peer;
    peer.setLabel(label);
  }

  /**
   * Construct a name for this component.  Called by getName() when the
   * name is null.
   */
  @Override
  String constructComponentName() {
    synchronized (Button.class) {
      String result = base + nameCounter;
      nameCounter++;
      return result;
    }
  }

  // REMIND: remove when filtering is done at lower level
  @Override
  boolean eventEnabled(AWTEvent e) {
    if (e.id == ActionEvent.ACTION_PERFORMED) {
      return (eventMask & AWTEvent.ACTION_EVENT_MASK) != 0 || actionListener != null;
    }
    return super.eventEnabled(e);
  }

  /**
   * Returns an array of all the objects currently registered
   * as <code><em>Foo</em>Listener</code>s
   * upon this {@code Button}.
   * <code><em>Foo</em>Listener</code>s are registered using the
   * <code>add<em>Foo</em>Listener</code> method.
   * <p>
   * <p>
   * You can specify the {@code listenerType} argument
   * with a class literal, such as
   * <code><em>Foo</em>Listener.class</code>.
   * For example, you can query a
   * {@code Button} {@code b}
   * for its action listeners with the following code:
   * <p>
   * <pre>ActionListener[] als = (ActionListener[])(b.getListeners(ActionListener.class));</pre>
   *
   * If no such listeners exist, this method returns an empty array.
   *
   * @param listenerType the type of listeners requested; this parameter
   *                     should specify an interface that descends from
   *                     {@code java.util.EventListener}
   * @return an array of all objects registered as
   * <code><em>Foo</em>Listener</code>s on this button,
   * or an empty array if no such
   * listeners have been added
   * @throws ClassCastException if {@code listenerType}
   *                            doesn't specify a class or interface that implements
   *                            {@code java.util.EventListener}
   * @see #getActionListeners
   * @since 1.3
   */
  @Override
  public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
    EventListener l;
    if (listenerType == ActionListener.class) {
      l = actionListener;
    } else {
      return super.getListeners(listenerType);
    }
    return AWTEventMulticaster.getListeners(l, listenerType);
  }

  /**
   * Processes events on this button. If an event is
   * an instance of {@code ActionEvent}, this method invokes
   * the {@code processActionEvent} method. Otherwise,
   * it invokes {@code processEvent} on the superclass.
   * <p>Note that if the event parameter is {@code null}
   * the behavior is unspecified and may result in an
   * exception.
   *
   * @param e the event
   * @see ActionEvent
   * @see Button#processActionEvent
   * @since JDK1.1
   */
  @Override
  protected void processEvent(AWTEvent e) {
    if (e instanceof ActionEvent) {
      processActionEvent((ActionEvent) e);
      return;
    }
    super.processEvent(e);
  }

  /**
   * Creates the peer of the button.  The button's peer allows the
   * application to change the look of the button without changing
   * its functionality.
   *
   * @see Toolkit#createButton(Button)
   * @see Component#getToolkit()
   */
  @Override
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (peer == null) {
        peer = getToolkit().createButton(this);
      }
      super.addNotify();
    }
  }

  /**
   * Returns a string representing the state of this {@code Button}.
   * This method is intended to be used only for debugging purposes, and the
   * content and format of the returned string may vary between
   * implementations. The returned string may be empty but may not be
   * {@code null}.
   *
   * @return the parameter string of this button
   */
  @Override
  protected String paramString() {
    return super.paramString() + ",label=" + label;
  }

  /**
   * Gets the label of this button.
   *
   * @return the button's label, or {@code null}
   * if the button has no label.
   * @see Button#setLabel
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the button's label to be the specified string.
   *
   * @param label the new label, or {@code null}
   *              if the button has no label.
   * @see Button#getLabel
   */
  public void setLabel(String label) {
    boolean testvalid = false;

    synchronized (this) {
      if (label != this.label && (this.label == null || !this.label.equals(label))) {
        this.label = label;
        updateLabel();
        testvalid = true;
      }
    }

    // This could change the preferred size of the Component.
    if (testvalid) {
      invalidateIfValid();
    }
  }

  /**
   * Update the {@link ButtonPeer}'s label with the current {@link #label}.
   */
  protected void updateLabel() {
    ButtonPeer peer = (ButtonPeer) this.peer;
    if (peer != null) {
      peer.setLabel(label);
    }
  }

  /**
   * Returns the command name of the action event fired by this button.
   * If the command name is {@code null} (default) then this method
   * returns the label of the button.
   */
  public String getActionCommand() {
    return actionCommand == null ? label : actionCommand;
  }

  /**
   * Sets the command name for the action event fired
   * by this button. By default this action command is
   * set to match the label of the button.
   *
   * @param command a string used to set the button's
   *                action command.
   *                If the string is {@code null} then the action command
   *                is set to match the label of the button.
   * @see ActionEvent
   * @since JDK1.1
   */
  public void setActionCommand(String command) {
    actionCommand = command;
  }

  /**
   * Adds the specified action listener to receive action events from
   * this button. Action events occur when a user presses or releases
   * the mouse over this button.
   * If l is null, no exception is thrown and no action is performed.
   * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
   * >AWT Threading Issues</a> for details on AWT's threading model.
   *
   * @param l the action listener
   * @see #removeActionListener
   * @see #getActionListeners
   * @see ActionListener
   * @since JDK1.1
   */
  public synchronized void addActionListener(ActionListener l) {
    if (l == null) {
      return;
    }
    actionListener = AWTEventMulticaster.add(actionListener, l);
    newEventsOnly = true;
  }

  /**
   * Removes the specified action listener so that it no longer
   * receives action events from this button. Action events occur
   * when a user presses or releases the mouse over this button.
   * If l is null, no exception is thrown and no action is performed.
   * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
   * >AWT Threading Issues</a> for details on AWT's threading model.
   *
   * @param l the action listener
   * @see #addActionListener
   * @see #getActionListeners
   * @see ActionListener
   * @since JDK1.1
   */
  public synchronized void removeActionListener(ActionListener l) {
    if (l == null) {
      return;
    }
    actionListener = AWTEventMulticaster.remove(actionListener, l);
  }

  /**
   * Returns an array of all the action listeners
   * registered on this button.
   *
   * @return all of this button's {@code ActionListener}s
   * or an empty array if no action
   * listeners are currently registered
   * @see #addActionListener
   * @see #removeActionListener
   * @see ActionListener
   * @since 1.4
   */
  public synchronized ActionListener[] getActionListeners() {
    return getListeners(ActionListener.class);
  }


    /* Serialization support.
     */

  /**
   * Processes action events occurring on this button
   * by dispatching them to any registered
   * {@code ActionListener} objects.
   * <p>
   * This method is not called unless action events are
   * enabled for this button. Action events are enabled
   * when one of the following occurs:
   * <ul>
   * <li>An {@code ActionListener} object is registered
   * via {@code addActionListener}.
   * <li>Action events are enabled via {@code enableEvents}.
   * </ul>
   * <p>Note that if the event parameter is {@code null}
   * the behavior is unspecified and may result in an
   * exception.
   *
   * @param e the action event
   * @see ActionListener
   * @see Button#addActionListener
   * @see Component#enableEvents
   * @since JDK1.1
   */
  protected void processActionEvent(ActionEvent e) {
    ActionListener listener = actionListener;
    if (listener != null) {
      listener.actionPerformed(e);
    }
  }

  /**
   * Writes default serializable fields to stream.  Writes
   * a list of serializable {@code ActionListeners}
   * as optional data.  The non-serializable
   * {@code ActionListeners} are detected and
   * no attempt is made to serialize them.
   *
   * @param s the {@code ObjectOutputStream} to write
   * @serialData {@code null} terminated sequence of 0 or
   * more pairs: the pair consists of a {@code String}
   * and an {@code Object}; the {@code String}
   * indicates the type of object and is one of the following:
   * {@code actionListenerK} indicating an
   * {@code ActionListener} object
   * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
   * @see Component#actionListenerK
   * @see #readObject(ObjectInputStream)
   */
  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();

    AWTEventMulticaster.save(s, actionListenerK, actionListener);
    s.writeObject(null);
  }

  /**
   * Reads the {@code ObjectInputStream} and if
   * it isn't {@code null} adds a listener to
   * receive action events fired by the button.
   * Unrecognized keys or values will be ignored.
   *
   * @param s the {@code ObjectInputStream} to read
   * @throws HeadlessException if
   *                           {@code GraphicsEnvironment.isHeadless} returns
   *                           {@code true}
   * @serial
   * @see #removeActionListener(ActionListener)
   * @see #addActionListener(ActionListener)
   * @see GraphicsEnvironment#isHeadless
   * @see #writeObject(ObjectOutputStream)
   */
  private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException {
    s.defaultReadObject();

    Object keyOrNull;
    while (null != (keyOrNull = s.readObject())) {
      String key = ((String) keyOrNull).intern();

      if (actionListenerK == key) {
        addActionListener((ActionListener) s.readObject());
      } else // skip value for unrecognized key
      {
        s.readObject();
      }
    }
    updateLabel();
  }
}
