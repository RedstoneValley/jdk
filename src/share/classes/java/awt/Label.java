/*
 * Copyright (c) 1995, 2014, Oracle and/or its affiliates. All rights reserved.
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

import android.widget.TextView;
import java.awt.peer.LabelPeer;
import java.io.IOException;
import java.io.ObjectInputStream;

/**
 * A {@code Label} object is a component for placing text in a
 * container. A label displays a single line of read-only text.
 * The text can be changed by the application, but a user cannot edit it
 * directly.
 * <p>
 * For example, the code&nbsp;.&nbsp;.&nbsp;.
 * <p>
 * <hr><blockquote><pre>
 * setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
 * add(new Label("Hi There!"));
 * add(new Label("Another Label"));
 * </pre></blockquote><hr>
 * <p>
 * produces the following labels:
 * <p>
 * <img src="doc-files/Label-1.gif" alt="Two labels: 'Hi There!' and 'Another label'"
 * style="float:center; margin: 7px 10px;">
 *
 * @author Sami Shaio
 * @since JDK1.0
 */
public class Label extends Component {

  /**
   * Indicates that the label should be left justified.
   */
  public static final int LEFT = 0;

  /**
   * Indicates that the label should be centered.
   */
  public static final int CENTER = 1;

  /**
   * Indicates that the label should be right justified.
   *
   * @since JDK1.0t.
   */
  public static final int RIGHT = 2;
  private static final String base = "label";
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = 3094126758329070636L;
  private static int nameCounter;
  /**
   * The text of this label.
   * This text can be modified by the program
   * but never by the user.
   *
   * @serial
   * @see #getText()
   * @see #setText(String)
   */
  String text;
  /**
   * The label's alignment.  The default alignment is set
   * to be left justified.
   *
   * @serial
   * @see #getAlignment()
   * @see #setAlignment(int)
   */
  int alignment = LEFT;

  /**
   * Constructs an empty label.
   * The text of the label is the empty string {@code ""}.
   *
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true.
   * @see GraphicsEnvironment#isHeadless
   */
  public Label() throws HeadlessException {
    this("", LEFT);
  }

  /**
   * Constructs a new label with the specified string of text,
   * left justified.
   *
   * @param text the string that the label presents.
   *             A {@code null} value
   *             will be accepted without causing a NullPointerException
   *             to be thrown.
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true.
   * @see GraphicsEnvironment#isHeadless
   */
  public Label(String text) throws HeadlessException {
    this(text, LEFT);
  }

  /**
   * Constructs a new label that presents the specified string of
   * text with the specified alignment.
   * Possible values for {@code alignment} are {@code Label.LEFT},
   * {@code Label.RIGHT}, and {@code Label.CENTER}.
   *
   * @param text      the string that the label presents.
   *                  A {@code null} value
   *                  will be accepted without causing a NullPointerException
   *                  to be thrown.
   * @param alignment the alignment value.
   * @throws HeadlessException if GraphicsEnvironment.isHeadless()
   *                           returns true.
   * @see GraphicsEnvironment#isHeadless
   */
  public Label(String text, int alignment) throws HeadlessException {
    super(TextView.class);
    peer = new SkinJobLabelPeer(this);
    setText(text);
    setAlignment(alignment);
  }

  /**
   * Read a label from an object input stream.
   *
   * @throws HeadlessException if
   *                           {@code GraphicsEnvironment.isHeadless()} returns
   *                           {@code true}
   * @serial
   * @see GraphicsEnvironment#isHeadless
   * @since 1.4
   */
  private void readObject(ObjectInputStream s)
      throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    s.defaultReadObject();
  }

  /**
   * Construct a name for this component.  Called by getName() when the
   * name is {@code null}.
   */
  @Override
  String constructComponentName() {
    synchronized (Label.class) {
      String result = base + nameCounter;
      nameCounter++;
      return result;
    }
  }

  /**
   * Creates the peer for this label.  The peer allows us to
   * modify the appearance of the label without changing its
   * functionality.
   */
  @Override
  public void addNotify() {
    synchronized (getTreeLock()) {
      if (peer == null) {
        peer = getToolkit().createLabel(this);
      }
      super.addNotify();
    }
  }

  /**
   * Returns a string representing the state of this {@code Label}.
   * This method is intended to be used only for debugging purposes, and the
   * content and format of the returned string may vary between
   * implementations. The returned string may be empty but may not be
   * {@code null}.
   *
   * @return the parameter string of this label
   */
  @Override
  protected String paramString() {
    String align = "";
    switch (alignment) {
      case LEFT:
        align = "left";
        break;
      case CENTER:
        align = "center";
        break;
      case RIGHT:
        align = "right";
        break;
    }
    return super.paramString() + ",align=" + align + ",text=" + text;
  }

  /**
   * Gets the current alignment of this label. Possible values are
   * {@code Label.LEFT}, {@code Label.RIGHT}, and
   * {@code Label.CENTER}.
   *
   * @see Label#setAlignment
   */
  public int getAlignment() {
    return alignment;
  }

  /**
   * Sets the alignment for this label to the specified alignment.
   * Possible values are {@code Label.LEFT},
   * {@code Label.RIGHT}, and {@code Label.CENTER}.
   *
   * @param alignment the alignment to be set.
   * @throws IllegalArgumentException if an improper value for
   *                                  {@code alignment} is given.
   * @see Label#getAlignment
   */
  public synchronized void setAlignment(int alignment) {
    switch (alignment) {
      case LEFT:
      case CENTER:
      case RIGHT:
        this.alignment = alignment;
        LabelPeer peer = (LabelPeer) this.peer;
        if (peer != null) {
          peer.setAlignment(alignment);
        }
        return;
    }
    throw new IllegalArgumentException("improper alignment: " + alignment);
  }

  /**
   * Gets the text of this label.
   *
   * @return the text of this label, or {@code null} if
   * the text has been set to {@code null}.
   * @see Label#setText
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text for this label to the specified text.
   *
   * @param text the text that this label displays. If
   *             {@code text} is {@code null}, it is
   *             treated for display purposes like an empty
   *             string {@code ""}.
   * @see Label#getText
   */
  public void setText(String text) {
    boolean testvalid = false;
    synchronized (this) {
      if (text != this.text && (this.text == null || !this.text.equals(text))) {
        this.text = text;
        LabelPeer peer = (LabelPeer) this.peer;
        if (peer != null) {
          peer.setText(text);
        }
        testvalid = true;
      }
    }

    // This could change the preferred size of the Component.
    if (testvalid) {
      invalidateIfValid();
    }
  }
}
