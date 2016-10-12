/*
 * Copyright (c) 1995, 2007, Oracle and/or its affiliates. All rights reserved.
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

/**
 * <code>Panel</code> is the simplest container class. A panel
 * provides space in which an application can attach any other
 * component, including other panels.
 * <p>
 * The default layout manager for a panel is the
 * <code>FlowLayout</code> layout manager.
 *
 * @author Sami Shaio
 * @see java.awt.FlowLayout
 * @since JDK1.0
 */
public class Panel extends Container {
  private static final String base = "panel";
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = -2728009084054400034L;
  private static int nameCounter = 0;

  /**
   * Creates a new panel using the default layout manager.
   * The default layout manager for all panels is the
   * <code>FlowLayout</code> class.
   */
  public Panel() {
    this(new FlowLayout());
  }

  /**
   * Creates a new panel with the specified layout manager.
   *
   * @param layout the layout manager for this panel.
   * @since JDK1.1
   */
  public Panel(LayoutManager layout) {
    setLayout(layout);
  }

  /**
   * Construct a name for this component.  Called by getName() when the
   * name is null.
   */
  String constructComponentName() {
    synchronized (Panel.class) {
      return base + nameCounter++;
    }
  }

  /**
   * Creates the Panel's peer.  The peer allows you to modify the
   * appearance of the panel without changing its functionality.
   */

  public void addNotify() {
    synchronized (getTreeLock()) {
      if (peer == null) {
        peer = getToolkit().createPanel(this);
      }
      super.addNotify();
    }
  }
}
