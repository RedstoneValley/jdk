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

import android.widget.RadioGroup;

import java.io.Serializable;

import skinjob.SkinJobGlobals;

/**
 * The {@code CheckboxGroup} class is used to group together a set of {@code Checkbox} buttons.
 * <p>
 * Exactly one check box button in a {@code CheckboxGroup} can be in the "on" state at any given
 * time. Pushing any button sets its state to "on" and forces any other button that is in the "on"
 * state into the "off" state.
 * <p>
 * The following code example produces a new check box group, with three check boxes:
 * <p>
 * <hr><blockquote><pre>
 * setLayout(new GridLayout(3, 1));
 * CheckboxGroup cbg = new CheckboxGroup();
 * add(new Checkbox("one", cbg, true));
 * add(new Checkbox("two", cbg, false));
 * add(new Checkbox("three", cbg, false));
 * </pre></blockquote><hr>
 * <p>
 * This image depicts the check box group created by this example:
 * <p>
 * <img src="doc-files/CheckboxGroup-1.gif" alt="Shows three checkboxes, arranged vertically,
 * labeled one, two, and three. Checkbox one is in the on state." style="float:center; margin: 7px
 * 10px;">
 * <p>
 *
 * @author Sami Shaio
 * @see Checkbox
 * @since JDK1.0
 */
public class CheckboxGroup implements Serializable {
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = 3729780091441768983L;
  protected final RadioGroup androidGroup;
  /**
   * The current choice.
   *
   * @serial
   * @see #getCurrent()
   * @see #setCurrent(Checkbox)
   */
  Checkbox selectedCheckbox;

  /**
   * Creates a new instance of {@code CheckboxGroup}.
   */
  public CheckboxGroup() {
    androidGroup = new RadioGroup(SkinJobGlobals.getAndroidApplicationContext());
  }

  public RadioGroup getAndroidGroup() {
    return androidGroup;
  }

  /**
   * Gets the current choice from this check box group. The current choice is the check box in this
   * group that is currently in the "on" state, or {@code null} if all check boxes in the group are
   * off.
   *
   * @return the check box that is currently in the "on" state, or {@code null}.
   * @see Checkbox
   * @see CheckboxGroup#setSelectedCheckbox
   * @since JDK1.1
   */
  public Checkbox getSelectedCheckbox() {
    return getCurrent();
  }

  /**
   * Sets the currently selected check box in this group to be the specified check box. This method
   * sets the state of that check box to "on" and sets all other check boxes in the group to be
   * off.
   * <p>
   * If the check box argument is <tt>null</tt>, all check boxes in this check box group are
   * deselected. If the check box argument belongs to a different check box group, this method does
   * nothing.
   *
   * @param box the {@code Checkbox} to set as the current selection.
   * @see Checkbox
   * @see CheckboxGroup#getSelectedCheckbox
   * @since JDK1.1
   */
  public void setSelectedCheckbox(Checkbox box) {
    setCurrent(box);
  }

  /**
   * @deprecated As of JDK version 1.1, replaced by {@code getSelectedCheckbox()}.
   */
  @Deprecated
  public Checkbox getCurrent() {
    return selectedCheckbox;
  }

  /**
   * @deprecated As of JDK version 1.1, replaced by {@code setSelectedCheckbox(Checkbox)}.
   */
  @SuppressWarnings("ObjectEquality")
  @Deprecated
  public synchronized void setCurrent(Checkbox box) {
    if (box != null && box.group != this) {
      return;
    }
    Checkbox oldChoice = selectedCheckbox;
    selectedCheckbox = box;
    if (oldChoice != null && oldChoice != box && oldChoice.group == this) {
      oldChoice.setState(false);
    }
    if (box != null && oldChoice != box && !box.getState()) {
      box.setStateInternal(true);
    }
  }

  /**
   * Returns a string representation of this check box group, including the value of its current
   * selection.
   *
   * @return a string representation of this check box group.
   */
  public String toString() {
    return getClass().getName() + "[selectedCheckbox=" + selectedCheckbox + "]";
  }
}
