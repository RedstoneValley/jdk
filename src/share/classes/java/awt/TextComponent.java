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

import android.widget.EditText;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextComponentPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.EventListener;
import sun.awt.InputMethodSupport;

/**
 * The {@code TextComponent} class is the superclass of
 * any component that allows the editing of some text.
 * <p>
 * A text component embodies a string of text.  The
 * {@code TextComponent} class defines a set of methods
 * that determine whether or not this text is editable. If the
 * component is editable, it defines another set of methods
 * that supports a text insertion caret.
 * <p>
 * In addition, the class defines methods that are used
 * to maintain a current <em>selection</em> from the text.
 * The text selection, a substring of the component's text,
 * is the target of editing operations. It is also referred
 * to as the <em>selected text</em>.
 *
 * @author Sami Shaio
 * @author Arthur van Hoff
 * @since JDK1.0
 */
public class TextComponent extends Component {

  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = -2214773872412987419L;
  /**
   * The textComponent SerializedDataVersion.
   *
   * @serial
   */
  private final int textComponentSerializedDataVersion = 1;
  protected transient TextListener textListener;
  /**
   * The value of the text.
   * A {@code null} value is the same as "".
   *
   * @serial
   * @see #setText(String)
   * @see #getText()
   */
  String text;
  /**
   * A boolean indicating whether or not this
   * {@code TextComponent} is editable.
   * It will be {@code true} if the text component
   * is editable and {@code false} if not.
   *
   * @serial
   * @see #isEditable()
   */
  boolean editable = true;
  /**
   * The selection refers to the selected text, and the
   * {@code selectionStart} is the start position
   * of the selected text.
   *
   * @serial
   * @see #getSelectionStart()
   * @see #setSelectionStart(int)
   */
  int selectionStart;
  /**
   * The selection refers to the selected text, and the
   * {@code selectionEnd}
   * is the end position of the selected text.
   *
   * @serial
   * @see #getSelectionEnd()
   * @see #setSelectionEnd(int)
   */
  int selectionEnd;
  // A flag used to tell whether the background has been set by
  // developer code (as opposed to AWT code).  Used to determine
  // the background color of non-editable TextComponents.
  boolean backgroundSetByClientCode;
  private boolean checkForEnableIM = true;

  /**
   * Constructs a new text component initialized with the
   * specified text. Sets the value of the cursor to
   * {@code Cursor.TEXT_CURSOR}.
   *
   * @param text the text to be displayed; if
   *             {@code text} is {@code null}, the empty
   *             string {@code ""} will be displayed
   * @throws HeadlessException if
   *                           {@code GraphicsEnvironment.isHeadless}
   *                           returns true
   * @see GraphicsEnvironment#isHeadless
   * @see Cursor
   */
  TextComponent(String text) throws HeadlessException {
    super(EditText.class);
    this.text = text != null ? text : "";
    setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
  }

  private void enableInputMethodsIfNecessary() {
    if (checkForEnableIM) {
      checkForEnableIM = false;
      try {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        boolean shouldEnable = false;
        if (toolkit instanceof InputMethodSupport) {
          shouldEnable = ((InputMethodSupport) toolkit).enableInputMethodsForTextComponent();
        }
        enableInputMethods(shouldEnable);
      } catch (Exception e) {
        // if something bad happens, just don't enable input methods
      }
    }
  }

  /**
   * Enables or disables input method support for this text component. If input
   * method support is enabled and the text component also processes key events,
   * incoming events are offered to the current input method and will only be
   * processed by the component or dispatched to its listeners if the input method
   * does not consume them. Whether and how input method support for this text
   * component is enabled or disabled by default is implementation dependent.
   *
   * @param enable true to enable, false to disable
   * @see #processKeyEvent
   * @since 1.2
   */
  @Override
  public void enableInputMethods(boolean enable) {
    checkForEnableIM = false;
    super.enableInputMethods(enable);
  }

  /**
   * Gets the background color of this text component.
   * <p>
   * By default, non-editable text components have a background color
   * of SystemColor.control.  This default can be overridden by
   * calling setBackground.
   *
   * @return This text component's background color.
   * If this text component does not have a background color,
   * the background color of its parent is returned.
   * @see #setBackground(Color)
   * @since JDK1.0
   */
  @Override
  public Color getBackground() {
    if (!editable && !backgroundSetByClientCode) {
      return SystemColor.control;
    }

    return super.getBackground();
  }

  /**
   * Sets the background color of this text component.
   *
   * @param c The color to become this text component's color.
   *          If this parameter is null then this text component
   *          will inherit the background color of its parent.
   * @see #getBackground()
   * @since JDK1.0
   */
  @Override
  public void setBackground(Color c) {
    backgroundSetByClientCode = true;
    super.setBackground(c);
  }

  @Override
  boolean areInputMethodsEnabled() {
    // moved from the constructor above to here and addNotify below,
    // this call will initialize the toolkit if not already initialized.
    if (checkForEnableIM) {
      enableInputMethodsIfNecessary();
    }

    // TextComponent handles key events without touching the eventMask or
    // having a key listener, so just check whether the flag is set
    return (eventMask & AWTEvent.INPUT_METHODS_ENABLED_MASK) != 0;
  }

  // REMIND: remove when filtering is done at lower level
  @Override
  boolean eventEnabled(AWTEvent e) {
    if (e.id == TextEvent.TEXT_VALUE_CHANGED) {
      return (eventMask & AWTEvent.TEXT_EVENT_MASK) != 0 || textListener != null;
    }
    return super.eventEnabled(e);
  }

  /**
   * Returns an array of all the objects currently registered
   * as <code><em>Foo</em>Listener</code>s
   * upon this {@code TextComponent}.
   * <code><em>Foo</em>Listener</code>s are registered using the
   * <code>add<em>Foo</em>Listener</code> method.
   * <p>
   * <p>
   * You can specify the {@code listenerType} argument
   * with a class literal, such as
   * <code><em>Foo</em>Listener.class</code>.
   * For example, you can query a
   * {@code TextComponent} {@code t}
   * for its text listeners with the following code:
   * <p>
   * <pre>TextListener[] tls = (TextListener[])(t.getListeners(TextListener.class));</pre>
   *
   * If no such listeners exist, this method returns an empty array.
   *
   * @param listenerType the type of listeners requested; this parameter
   *                     should specify an interface that descends from
   *                     {@code java.util.EventListener}
   * @return an array of all objects registered as
   * <code><em>Foo</em>Listener</code>s on this text component,
   * or an empty array if no such
   * listeners have been added
   * @throws ClassCastException if {@code listenerType}
   *                            doesn't specify a class or interface that implements
   *                            {@code java.util.EventListener}
   * @see #getTextListeners
   * @since 1.3
   */
  @Override
  public <T extends EventListener> T[] getListeners(Class<T> listenerType) {
    EventListener l;
    if (listenerType == TextListener.class) {
      l = textListener;
    } else {
      return super.getListeners(listenerType);
    }
    return AWTEventMulticaster.getListeners(l, listenerType);
  }

  @Override
  public InputMethodRequests getInputMethodRequests() {
    TextComponentPeer peer = (TextComponentPeer) this.peer;
    return peer != null ? peer.getInputMethodRequests() : null;
  }

  /**
   * Processes events on this text component. If the event is a
   * {@code TextEvent}, it invokes the {@code processTextEvent}
   * method else it invokes its superclass's {@code processEvent}.
   * <p>Note that if the event parameter is {@code null}
   * the behavior is unspecified and may result in an
   * exception.
   *
   * @param e the event
   */
  @Override
  protected void processEvent(AWTEvent e) {
    if (e instanceof TextEvent) {
      processTextEvent((TextEvent) e);
      return;
    }
    super.processEvent(e);
  }

  /**
   * Makes this Component displayable by connecting it to a
   * native screen resource.
   * This method is called internally by the toolkit and should
   * not be called directly by programs.
   *
   * @see TextComponent#removeNotify
   */
  @Override
  public void addNotify() {
    super.addNotify();
    enableInputMethodsIfNecessary();
  }

  /**
   * Removes the {@code TextComponent}'s peer.
   * The peer allows us to modify the appearance of the
   * {@code TextComponent} without changing its
   * functionality.
   */
  @Override
  public void removeNotify() {
    synchronized (getTreeLock()) {
      TextComponentPeer peer = (TextComponentPeer) this.peer;
      if (peer != null) {
        text = peer.getText();
        selectionStart = peer.getSelectionStart();
        selectionEnd = peer.getSelectionEnd();
      }
      super.removeNotify();
    }
  }

  /**
   * Returns a string representing the state of this
   * {@code TextComponent}. This
   * method is intended to be used only for debugging purposes, and the
   * content and format of the returned string may vary between
   * implementations. The returned string may be empty but may not be
   * {@code null}.
   *
   * @return the parameter string of this text component
   */
  @Override
  protected String paramString() {
    String str = super.paramString() + ",text=" + getText();
    if (editable) {
      str += ",editable";
    }
    return str + ",selection=" + getSelectionStart() + "-" + getSelectionEnd();
  }

  /**
   * Returns the text that is presented by this text component.
   * By default, this is an empty string.
   *
   * @return the value of this {@code TextComponent}
   * @see TextComponent#setText
   */
  public synchronized String getText() {
    TextComponentPeer peer = (TextComponentPeer) this.peer;
    if (peer != null) {
      text = peer.getText();
    }
    return text;
  }

  /**
   * Sets the text that is presented by this
   * text component to be the specified text.
   *
   * @param t the new text;
   *          if this parameter is {@code null} then
   *          the text is set to the empty string ""
   * @see TextComponent#getText
   */
  public synchronized void setText(String t) {
    boolean skipTextEvent = (text == null || text.isEmpty()) && (t == null || t.isEmpty());
    text = t != null ? t : "";
    TextComponentPeer peer = (TextComponentPeer) this.peer;
    // Please note that we do not want to post an event
    // if TextArea.setText() or TextField.setText() replaces an empty text
    // by an empty text, that is, if component's text remains unchanged.
    if (peer != null && !skipTextEvent) {
      peer.setText(text);
    }
  }

  /**
   * Returns the selected text from the text that is
   * presented by this text component.
   *
   * @return the selected text of this text component
   * @see TextComponent#select
   */
  public synchronized String getSelectedText() {
    return getText().substring(getSelectionStart(), getSelectionEnd());
  }

  /**
   * Indicates whether or not this text component is editable.
   *
   * @return {@code true} if this text component is
   * editable; {@code false} otherwise.
   * @see TextComponent#setEditable
   * @since JDK1.0
   */
  public boolean isEditable() {
    return editable;
  }

  /**
   * Sets the flag that determines whether or not this
   * text component is editable.
   * <p>
   * If the flag is set to {@code true}, this text component
   * becomes user editable. If the flag is set to {@code false},
   * the user cannot change the text of this text component.
   * By default, non-editable text components have a background color
   * of SystemColor.control.  This default can be overridden by
   * calling setBackground.
   *
   * @param b a flag indicating whether this text component
   *          is user editable.
   * @see TextComponent#isEditable
   * @since JDK1.0
   */
  public synchronized void setEditable(boolean b) {
    if (editable == b) {
      return;
    }

    editable = b;
    TextComponentPeer peer = (TextComponentPeer) this.peer;
    if (peer != null) {
      peer.setEditable(b);
    }
  }

  /**
   * Gets the start position of the selected text in
   * this text component.
   *
   * @return the start position of the selected text
   * @see TextComponent#setSelectionStart
   * @see TextComponent#getSelectionEnd
   */
  public synchronized int getSelectionStart() {
    TextComponentPeer peer = (TextComponentPeer) this.peer;
    if (peer != null) {
      selectionStart = peer.getSelectionStart();
    }
    return selectionStart;
  }

  /**
   * Sets the selection start for this text component to
   * the specified position. The new start point is constrained
   * to be at or before the current selection end. It also
   * cannot be set to less than zero, the beginning of the
   * component's text.
   * If the caller supplies a value for {@code selectionStart}
   * that is out of bounds, the method enforces these constraints
   * silently, and without failure.
   *
   * @param selectionStart the start position of the
   *                       selected text
   * @see TextComponent#getSelectionStart
   * @see TextComponent#setSelectionEnd
   * @since JDK1.1
   */
  public synchronized void setSelectionStart(int selectionStart) {
        /* Route through select method to enforce consistent policy
         * between selectionStart and selectionEnd.
         */
    select(selectionStart, getSelectionEnd());
  }

  /**
   * Gets the end position of the selected text in
   * this text component.
   *
   * @return the end position of the selected text
   * @see TextComponent#setSelectionEnd
   * @see TextComponent#getSelectionStart
   */
  public synchronized int getSelectionEnd() {
    TextComponentPeer peer = (TextComponentPeer) this.peer;
    if (peer != null) {
      selectionEnd = peer.getSelectionEnd();
    }
    return selectionEnd;
  }

  /**
   * Sets the selection end for this text component to
   * the specified position. The new end point is constrained
   * to be at or after the current selection start. It also
   * cannot be set beyond the end of the component's text.
   * If the caller supplies a value for {@code selectionEnd}
   * that is out of bounds, the method enforces these constraints
   * silently, and without failure.
   *
   * @param selectionEnd the end position of the
   *                     selected text
   * @see TextComponent#getSelectionEnd
   * @see TextComponent#setSelectionStart
   * @since JDK1.1
   */
  public synchronized void setSelectionEnd(int selectionEnd) {
        /* Route through select method to enforce consistent policy
         * between selectionStart and selectionEnd.
         */
    select(getSelectionStart(), selectionEnd);
  }

  /**
   * Selects the text between the specified start and end positions.
   * <p>
   * This method sets the start and end positions of the
   * selected text, enforcing the restriction that the start position
   * must be greater than or equal to zero.  The end position must be
   * greater than or equal to the start position, and less than or
   * equal to the length of the text component's text.  The
   * character positions are indexed starting with zero.
   * The length of the selection is
   * {@code endPosition} - {@code startPosition}, so the
   * character at {@code endPosition} is not selected.
   * If the start and end positions of the selected text are equal,
   * all text is deselected.
   * <p>
   * If the caller supplies values that are inconsistent or out of
   * bounds, the method enforces these constraints silently, and
   * without failure. Specifically, if the start position or end
   * position is greater than the length of the text, it is reset to
   * equal the text length. If the start position is less than zero,
   * it is reset to zero, and if the end position is less than the
   * start position, it is reset to the start position.
   *
   * @param selectionStart the zero-based index of the first
   *                       character ({@code char} value) to be selected
   * @param selectionEnd   the zero-based end position of the
   *                       text to be selected; the character ({@code char} value) at
   *                       {@code selectionEnd} is not selected
   * @see TextComponent#setSelectionStart
   * @see TextComponent#setSelectionEnd
   * @see TextComponent#selectAll
   */
  public synchronized void select(int selectionStart, int selectionEnd) {
    String text = getText();
    if (selectionStart < 0) {
      selectionStart = 0;
    }
    if (selectionStart > text.length()) {
      selectionStart = text.length();
    }
    if (selectionEnd > text.length()) {
      selectionEnd = text.length();
    }
    if (selectionEnd < selectionStart) {
      selectionEnd = selectionStart;
    }

    this.selectionStart = selectionStart;
    this.selectionEnd = selectionEnd;

    TextComponentPeer peer = (TextComponentPeer) this.peer;
    if (peer != null) {
      peer.select(selectionStart, selectionEnd);
    }
  }

  /**
   * Selects all the text in this text component.
   *
   * @see TextComponent#select
   */
  public synchronized void selectAll() {
    selectionStart = 0;
    selectionEnd = getText().length();

    TextComponentPeer peer = (TextComponentPeer) this.peer;
    if (peer != null) {
      peer.select(selectionStart, selectionEnd);
    }
  }

  /**
   * Returns the position of the text insertion caret.
   * The caret position is constrained to be between 0
   * and the last character of the text, inclusive.
   * If the text or caret have not been set, the default
   * caret position is 0.
   *
   * @return the position of the text insertion caret
   * @see #setCaretPosition(int)
   * @since JDK1.1
   */
  public synchronized int getCaretPosition() {
    TextComponentPeer peer = (TextComponentPeer) this.peer;
    int position;

    position = peer != null ? peer.getCaretPosition() : selectionStart;
    int maxposition = getText().length();
    if (position > maxposition) {
      position = maxposition;
    }
    return position;
  }

  /**
   * Sets the position of the text insertion caret.
   * The caret position is constrained to be between 0
   * and the last character of the text, inclusive.
   * If the passed-in value is greater than this range,
   * the value is set to the last character (or 0 if
   * the {@code TextComponent} contains no text)
   * and no error is returned.  If the passed-in value is
   * less than 0, an {@code IllegalArgumentException}
   * is thrown.
   *
   * @param position the position of the text insertion caret
   * @throws IllegalArgumentException if {@code position}
   *                                  is less than zero
   * @since JDK1.1
   */
  public synchronized void setCaretPosition(int position) {
    if (position < 0) {
      throw new IllegalArgumentException("position less than zero.");
    }

    int maxposition = getText().length();
    if (position > maxposition) {
      position = maxposition;
    }

    TextComponentPeer peer = (TextComponentPeer) this.peer;
    if (peer != null) {
      peer.setCaretPosition(position);
    } else {
      select(position, position);
    }
  }

  /**
   * Adds the specified text event listener to receive text events
   * from this text component.
   * If {@code l} is {@code null}, no exception is
   * thrown and no action is performed.
   * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
   * >AWT Threading Issues</a> for details on AWT's threading model.
   *
   * @param l the text event listener
   * @see #removeTextListener
   * @see #getTextListeners
   * @see TextListener
   */
  public synchronized void addTextListener(TextListener l) {
    if (l == null) {
      return;
    }
    textListener = AWTEventMulticaster.add(textListener, l);
    newEventsOnly = true;
  }

  /**
   * Removes the specified text event listener so that it no longer
   * receives text events from this text component
   * If {@code l} is {@code null}, no exception is
   * thrown and no action is performed.
   * <p>Refer to <a href="doc-files/AWTThreadIssues.html#ListenersThreads"
   * >AWT Threading Issues</a> for details on AWT's threading model.
   *
   * @param l the text listener
   * @see #addTextListener
   * @see #getTextListeners
   * @see TextListener
   * @since JDK1.1
   */
  public synchronized void removeTextListener(TextListener l) {
    if (l == null) {
      return;
    }
    textListener = AWTEventMulticaster.remove(textListener, l);
  }

    /*
     * Serialization support.
     */

  /**
   * Returns an array of all the text listeners
   * registered on this text component.
   *
   * @return all of this text component's {@code TextListener}s
   * or an empty array if no text
   * listeners are currently registered
   * @see #addTextListener
   * @see #removeTextListener
   * @since 1.4
   */
  public synchronized TextListener[] getTextListeners() {
    return getListeners(TextListener.class);
  }

  /**
   * Processes text events occurring on this text component by
   * dispatching them to any registered {@code TextListener} objects.
   * <p>
   * NOTE: This method will not be called unless text events
   * are enabled for this component. This happens when one of the
   * following occurs:
   * <ul>
   * <li>A {@code TextListener} object is registered
   * via {@code addTextListener}
   * <li>Text events are enabled via {@code enableEvents}
   * </ul>
   * <p>Note that if the event parameter is {@code null}
   * the behavior is unspecified and may result in an
   * exception.
   *
   * @param e the text event
   * @see Component#enableEvents
   */
  protected void processTextEvent(TextEvent e) {
    TextListener listener = textListener;
    if (listener != null) {
      int id = e.getID();
      switch (id) {
        case TextEvent.TEXT_VALUE_CHANGED:
          listener.textValueChanged(e);
          break;
      }
    }
  }

  /**
   * Writes default serializable fields to stream.  Writes
   * a list of serializable TextListener(s) as optional data.
   * The non-serializable TextListener(s) are detected and
   * no attempt is made to serialize them.
   *
   * @serialData Null terminated sequence of zero or more pairs.
   * A pair consists of a String and Object.
   * The String indicates the type of object and
   * is one of the following :
   * textListenerK indicating and TextListener object.
   * @see AWTEventMulticaster#save(ObjectOutputStream, String, EventListener)
   * @see Component#textListenerK
   */
  private void writeObject(ObjectOutputStream s) throws IOException {
    // Serialization support.  Since the value of the fields
    // selectionStart, selectionEnd, and text aren't necessarily
    // up to date, we sync them up with the peer before serializing.
    TextComponentPeer peer = (TextComponentPeer) this.peer;
    if (peer != null) {
      text = peer.getText();
      selectionStart = peer.getSelectionStart();
      selectionEnd = peer.getSelectionEnd();
    }

    s.defaultWriteObject();

    AWTEventMulticaster.save(s, textListenerK, textListener);
    s.writeObject(null);
  }

  /**
   * Read the ObjectInputStream, and if it isn't null,
   * add a listener to receive text events fired by the
   * TextComponent.  Unrecognized keys or values will be
   * ignored.
   *
   * @throws HeadlessException if
   *                           {@code GraphicsEnvironment.isHeadless()} returns
   *                           {@code true}
   * @see #removeTextListener
   * @see #addTextListener
   * @see GraphicsEnvironment#isHeadless
   */
  private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException {
    s.defaultReadObject();

    // Make sure the state we just read in for text,
    // selectionStart and selectionEnd has legal values
    text = text != null ? text : "";
    select(selectionStart, selectionEnd);

    Object keyOrNull;
    while (null != (keyOrNull = s.readObject())) {
      String key = ((String) keyOrNull).intern();

      if (textListenerK == key) {
        addTextListener((TextListener) s.readObject());
      } else {
        // skip value for unrecognized key
        s.readObject();
      }
    }
    enableInputMethodsIfNecessary();
  }
}
