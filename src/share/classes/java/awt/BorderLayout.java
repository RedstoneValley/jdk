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

import java.io.Serializable;

/**
 * A border layout lays out a container, arranging and resizing
 * its components to fit in five regions:
 * north, south, east, west, and center.
 * Each region may contain no more than one component, and
 * is identified by a corresponding constant:
 * {@code NORTH}, {@code SOUTH}, {@code EAST},
 * {@code WEST}, and {@code CENTER}.  When adding a
 * component to a container with a border layout, use one of these
 * five constants, for example:
 * <pre>
 *    Panel p = new Panel();
 *    p.setLayout(new BorderLayout());
 *    p.add(new Button("Okay"), BorderLayout.SOUTH);
 * </pre>
 * As a convenience, {@code BorderLayout} interprets the
 * absence of a string specification the same as the constant
 * {@code CENTER}:
 * <pre>
 *    Panel p2 = new Panel();
 *    p2.setLayout(new BorderLayout());
 *    p2.add(new TextArea());  // Same as p.add(new TextArea(), BorderLayout.CENTER);
 * </pre>
 * <p>
 * In addition, {@code BorderLayout} supports the relative
 * positioning constants, {@code PAGE_START}, {@code PAGE_END},
 * {@code LINE_START}, and {@code LINE_END}.
 * In a container whose {@code ComponentOrientation} is set to
 * {@code ComponentOrientation.LEFT_TO_RIGHT}, these constants map to
 * {@code NORTH}, {@code SOUTH}, {@code WEST}, and
 * {@code EAST}, respectively.
 * <p>
 * For compatibility with previous releases, {@code BorderLayout}
 * also includes the relative positioning constants {@code BEFORE_FIRST_LINE},
 * {@code AFTER_LAST_LINE}, {@code BEFORE_LINE_BEGINS} and
 * {@code AFTER_LINE_ENDS}.  These are equivalent to
 * {@code PAGE_START}, {@code PAGE_END}, {@code LINE_START}
 * and {@code LINE_END} respectively.  For
 * consistency with the relative positioning constants used by other
 * components, the latter constants are preferred.
 * <p>
 * Mixing both absolute and relative positioning constants can lead to
 * unpredictable results.  If
 * you use both types, the relative constants will take precedence.
 * For example, if you add components using both the {@code NORTH}
 * and {@code PAGE_START} constants in a container whose
 * orientation is {@code LEFT_TO_RIGHT}, only the
 * {@code PAGE_START} will be layed out.
 * <p>
 * NOTE: Currently (in the Java 2 platform v1.2),
 * {@code BorderLayout} does not support vertical
 * orientations.  The {@code isVertical} setting on the container's
 * {@code ComponentOrientation} is not respected.
 * <p>
 * The components are laid out according to their
 * preferred sizes and the constraints of the container's size.
 * The {@code NORTH} and {@code SOUTH} components may
 * be stretched horizontally; the {@code EAST} and
 * {@code WEST} components may be stretched vertically;
 * the {@code CENTER} component may stretch both horizontally
 * and vertically to fill any space left over.
 * <p>
 * Here is an example of five buttons in an applet laid out using
 * the {@code BorderLayout} layout manager:
 * <p>
 * <img src="doc-files/BorderLayout-1.gif"
 * alt="Diagram of an applet demonstrating BorderLayout.
 * Each section of the BorderLayout contains a Button corresponding to its position in the
 * layout, one of:
 * North, West, Center, East, or South."
 * style="float:center; margin: 7px 10px;">
 * <p>
 * The code for this applet is as follows:
 * <p>
 * <hr><blockquote><pre>
 * import java.awt.*;
 * import java.applet.Applet;
 * <p>
 * public class buttonDir extends Applet {
 *   public void init() {
 *     setLayout(new BorderLayout());
 *     add(new Button("North"), BorderLayout.NORTH);
 *     add(new Button("South"), BorderLayout.SOUTH);
 *     add(new Button("East"), BorderLayout.EAST);
 *     add(new Button("West"), BorderLayout.WEST);
 *     add(new Button("Center"), BorderLayout.CENTER);
 *   }
 * }
 * </pre></blockquote><hr>
 * <p>
 *
 * @author Arthur van Hoff
 * @see Container#add(String, Component)
 * @see ComponentOrientation
 * @since JDK1.0
 */
public class BorderLayout implements LayoutManager2, Serializable {
  /**
   * The north layout constraint (top of container).
   */
  public static final String NORTH = "North";
  /**
   * The south layout constraint (bottom of container).
   */
  public static final String SOUTH = "South";
  /**
   * The east layout constraint (right side of container).
   */
  public static final String EAST = "East";
  /**
   * The west layout constraint (left side of container).
   */
  public static final String WEST = "West";
  /**
   * The center layout constraint (middle of container).
   */
  public static final String CENTER = "Center";
  /**
   * Synonym for PAGE_START.  Exists for compatibility with previous
   * versions.  PAGE_START is preferred.
   *
   * @see #PAGE_START
   * @since 1.2
   */
  public static final String BEFORE_FIRST_LINE = "First";
  /**
   * Synonym for PAGE_END.  Exists for compatibility with previous
   * versions.  PAGE_END is preferred.
   *
   * @see #PAGE_END
   * @since 1.2
   */
  public static final String AFTER_LAST_LINE = "Last";
  /**
   * Synonym for LINE_START.  Exists for compatibility with previous
   * versions.  LINE_START is preferred.
   *
   * @see #LINE_START
   * @since 1.2
   */
  public static final String BEFORE_LINE_BEGINS = "Before";
  /**
   * Synonym for LINE_END.  Exists for compatibility with previous
   * versions.  LINE_END is preferred.
   *
   * @see #LINE_END
   * @since 1.2
   */
  public static final String AFTER_LINE_ENDS = "After";
  /**
   * The component comes before the first line of the layout's content.
   * For Western, left-to-right and top-to-bottom orientations, this is
   * equivalent to NORTH.
   *
   * @see Component#getComponentOrientation
   * @since 1.4
   */
  public static final String PAGE_START = BEFORE_FIRST_LINE;
  /**
   * The component comes after the last line of the layout's content.
   * For Western, left-to-right and top-to-bottom orientations, this is
   * equivalent to SOUTH.
   *
   * @see Component#getComponentOrientation
   * @since 1.4
   */
  public static final String PAGE_END = AFTER_LAST_LINE;
  /**
   * The component goes at the beginning of the line direction for the
   * layout. For Western, left-to-right and top-to-bottom orientations,
   * this is equivalent to WEST.
   *
   * @see Component#getComponentOrientation
   * @since 1.4
   */
  public static final String LINE_START = BEFORE_LINE_BEGINS;
  /**
   * The component goes at the end of the line direction for the
   * layout. For Western, left-to-right and top-to-bottom orientations,
   * this is equivalent to EAST.
   *
   * @see Component#getComponentOrientation
   * @since 1.4
   */
  public static final String LINE_END = AFTER_LINE_ENDS;
  protected static final float ALIGN_CENTER = 0.5f;
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = -8658291919501921765L;
  /**
   * Constructs a border layout with the horizontal gaps
   * between components.
   * The horizontal gap is specified by {@code hgap}.
   *
   * @serial
   * @see #getHgap()
   * @see #setHgap(int)
   */
  int hgap;
  /**
   * Constructs a border layout with the vertical gaps
   * between components.
   * The vertical gap is specified by {@code vgap}.
   *
   * @serial
   * @see #getVgap()
   * @see #setVgap(int)
   */
  int vgap;
  /**
   * Constant to specify components location to be the
   * north portion of the border layout.
   *
   * @serial
   * @see #getChild(String, boolean)
   * @see #addLayoutComponent
   * @see #getLayoutAlignmentX
   * @see #getLayoutAlignmentY
   * @see #removeLayoutComponent
   */
  Component north;
  /**
   * Constant to specify components location to be the
   * west portion of the border layout.
   *
   * @serial
   * @see #getChild(String, boolean)
   * @see #addLayoutComponent
   * @see #getLayoutAlignmentX
   * @see #getLayoutAlignmentY
   * @see #removeLayoutComponent
   */
  Component west;
  /**
   * Constant to specify components location to be the
   * east portion of the border layout.
   *
   * @serial
   * @see #getChild(String, boolean)
   * @see #addLayoutComponent
   * @see #getLayoutAlignmentX
   * @see #getLayoutAlignmentY
   * @see #removeLayoutComponent
   */
  Component east;
  /**
   * Constant to specify components location to be the
   * south portion of the border layout.
   *
   * @serial
   * @see #getChild(String, boolean)
   * @see #addLayoutComponent
   * @see #getLayoutAlignmentX
   * @see #getLayoutAlignmentY
   * @see #removeLayoutComponent
   */
  Component south;
  /**
   * Constant to specify components location to be the
   * center portion of the border layout.
   *
   * @serial
   * @see #getChild(String, boolean)
   * @see #addLayoutComponent
   * @see #getLayoutAlignmentX
   * @see #getLayoutAlignmentY
   * @see #removeLayoutComponent
   */
  Component center;
  /**
   * A relative positioning constant, that can be used instead of
   * north, south, east, west or center.
   * mixing the two types of constants can lead to unpredictable results.  If
   * you use both types, the relative constants will take precedence.
   * For example, if you add components using both the {@code NORTH}
   * and {@code BEFORE_FIRST_LINE} constants in a container whose
   * orientation is {@code LEFT_TO_RIGHT}, only the
   * {@code BEFORE_FIRST_LINE} will be layed out.
   * This will be the same for lastLine, firstItem, lastItem.
   *
   * @serial
   */
  Component firstLine;
  /**
   * A relative positioning constant, that can be used instead of
   * north, south, east, west or center.
   * Please read Description for firstLine.
   *
   * @serial
   */
  Component lastLine;
  /**
   * A relative positioning constant, that can be used instead of
   * north, south, east, west or center.
   * Please read Description for firstLine.
   *
   * @serial
   */
  Component firstItem;
  /**
   * A relative positioning constant, that can be used instead of
   * north, south, east, west or center.
   * Please read Description for firstLine.
   *
   * @serial
   */
  Component lastItem;

  /**
   * Constructs a new border layout with
   * no gaps between components.
   */
  public BorderLayout() {
    this(0, 0);
  }

  /**
   * Constructs a border layout with the specified gaps
   * between components.
   * The horizontal gap is specified by {@code hgap}
   * and the vertical gap is specified by {@code vgap}.
   *
   * @param hgap the horizontal gap.
   * @param vgap the vertical gap.
   */
  public BorderLayout(int hgap, int vgap) {
    this.hgap = hgap;
    this.vgap = vgap;
  }

  /**
   * Returns the horizontal gap between components.
   *
   * @since JDK1.1
   */
  public int getHgap() {
    return hgap;
  }

  /**
   * Sets the horizontal gap between components.
   *
   * @param hgap the horizontal gap between components
   * @since JDK1.1
   */
  public void setHgap(int hgap) {
    this.hgap = hgap;
  }

  /**
   * Returns the vertical gap between components.
   *
   * @since JDK1.1
   */
  public int getVgap() {
    return vgap;
  }

  /**
   * Sets the vertical gap between components.
   *
   * @param vgap the vertical gap between components
   * @since JDK1.1
   */
  public void setVgap(int vgap) {
    this.vgap = vgap;
  }

  /**
   * Adds the specified component to the layout, using the specified
   * constraint object.  For border layouts, the constraint must be
   * one of the following constants:  {@code NORTH},
   * {@code SOUTH}, {@code EAST},
   * {@code WEST}, or {@code CENTER}.
   * <p>
   * Most applications do not call this method directly. This method
   * is called when a component is added to a container using the
   * {@code Container.add} method with the same argument types.
   *
   * @param comp        the component to be added.
   * @param constraints an object that specifies how and where
   *                    the component is added to the layout.
   * @throws IllegalArgumentException if the constraint object is not
   *                                  a string, or if it not one of the five specified
   *                                  constants.
   * @see Container#add(Component, Object)
   * @since JDK1.1
   */
  @Override
  public void addLayoutComponent(Component comp, Object constraints) {
    synchronized (comp.getTreeLock()) {
      if (constraints == null || constraints instanceof String) {
        addLayoutComponent((String) constraints, comp);
      } else {
        throw new IllegalArgumentException(
            "cannot add to layout: constraint must be a string (or null)");
      }
    }
  }

  /**
   * Returns the maximum dimensions for this layout given the components
   * in the specified target container.
   *
   * @param target the component which needs to be laid out
   * @see Container
   * @see #minimumLayoutSize
   * @see #preferredLayoutSize
   */
  @Override
  public Dimension maximumLayoutSize(Container target) {
    return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
  }

  /**
   * Returns the alignment along the x axis.  This specifies how
   * the component would like to be aligned relative to other
   * components.  The value should be a number between 0 and 1
   * where 0 represents alignment along the origin, 1 is aligned
   * the furthest away from the origin, 0.5 is centered, etc.
   */
  @Override
  public float getLayoutAlignmentX(Container parent) {
    return ALIGN_CENTER;
  }

  /**
   * Returns the alignment along the y axis.  This specifies how
   * the component would like to be aligned relative to other
   * components.  The value should be a number between 0 and 1
   * where 0 represents alignment along the origin, 1 is aligned
   * the furthest away from the origin, 0.5 is centered, etc.
   */
  @Override
  public float getLayoutAlignmentY(Container parent) {
    return ALIGN_CENTER;
  }

  /**
   * Invalidates the layout, indicating that if the layout manager
   * has cached information it should be discarded.
   */
  @Override
  public void invalidateLayout(Container target) {
  }

  /**
   * @deprecated replaced by {@code addLayoutComponent(Component, Object)}.
   */
  @Override
  @Deprecated
  public void addLayoutComponent(String name, Component comp) {
    synchronized (comp.getTreeLock()) {
        /* Special case:  treat null the same as "Center". */
      if (name == null) {
        name = CENTER;
      }

        /* Assign the component to one of the known regions of the layout.
         */
      if (CENTER.equals(name)) {
        center = comp;
      } else if (NORTH.equals(name)) {
        north = comp;
      } else if (SOUTH.equals(name)) {
        south = comp;
      } else if ("East".equals(name)) {
        east = comp;
      } else if ("West".equals(name)) {
        west = comp;
      } else if (BEFORE_FIRST_LINE.equals(name)) {
        firstLine = comp;
      } else if (AFTER_LAST_LINE.equals(name)) {
        lastLine = comp;
      } else if (BEFORE_LINE_BEGINS.equals(name)) {
        firstItem = comp;
      } else if (AFTER_LINE_ENDS.equals(name)) {
        lastItem = comp;
      } else {
        throw new IllegalArgumentException("cannot add to layout: unknown constraint: " + name);
      }
    }
  }

  /**
   * Removes the specified component from this border layout. This
   * method is called when a container calls its {@code remove} or
   * {@code removeAll} methods. Most applications do not call this
   * method directly.
   *
   * @param comp the component to be removed.
   * @see Container#remove(Component)
   * @see Container#removeAll()
   */
  @Override
  public void removeLayoutComponent(Component comp) {
    synchronized (comp.getTreeLock()) {
      if (comp == center) {
        center = null;
      } else if (comp == north) {
        north = null;
      } else if (comp == south) {
        south = null;
      } else if (comp == east) {
        east = null;
      } else if (comp == west) {
        west = null;
      }
      if (comp == firstLine) {
        firstLine = null;
      } else if (comp == lastLine) {
        lastLine = null;
      } else if (comp == firstItem) {
        firstItem = null;
      } else if (comp == lastItem) {
        lastItem = null;
      }
    }
  }

  /**
   * Determines the preferred size of the {@code target}
   * container using this layout manager, based on the components
   * in the container.
   * <p>
   * Most applications do not call this method directly. This method
   * is called when a container calls its {@code getPreferredSize}
   * method.
   *
   * @param target the container in which to do the layout.
   * @return the preferred dimensions to lay out the subcomponents
   * of the specified container.
   * @see Container
   * @see BorderLayout#minimumLayoutSize
   * @see Container#getPreferredSize()
   */
  @Override
  public Dimension preferredLayoutSize(Container target) {
    synchronized (target.getTreeLock()) {
      Dimension dim = new Dimension(0, 0);

      boolean ltr = target.getComponentOrientation().isLeftToRight();
      Component c;

      if ((c = getChild(EAST, ltr)) != null) {
        Dimension d = c.getPreferredSize();
        dim.width += d.width + hgap;
        dim.height = Math.max(d.height, dim.height);
      }
      if ((c = getChild(WEST, ltr)) != null) {
        Dimension d = c.getPreferredSize();
        dim.width += d.width + hgap;
        dim.height = Math.max(d.height, dim.height);
      }
      if ((c = getChild(CENTER, ltr)) != null) {
        Dimension d = c.getPreferredSize();
        dim.width += d.width;
        dim.height = Math.max(d.height, dim.height);
      }
      if ((c = getChild(NORTH, ltr)) != null) {
        Dimension d = c.getPreferredSize();
        dim.width = Math.max(d.width, dim.width);
        dim.height += d.height + vgap;
      }
      if ((c = getChild(SOUTH, ltr)) != null) {
        Dimension d = c.getPreferredSize();
        dim.width = Math.max(d.width, dim.width);
        dim.height += d.height + vgap;
      }

      Insets insets = target.getInsets();
      dim.width += insets.left + insets.right;
      dim.height += insets.top + insets.bottom;

      return dim;
    }
  }

  /**
   * Determines the minimum size of the {@code target} container
   * using this layout manager.
   * <p>
   * This method is called when a container calls its
   * {@code getMinimumSize} method. Most applications do not call
   * this method directly.
   *
   * @param target the container in which to do the layout.
   * @return the minimum dimensions needed to lay out the subcomponents
   * of the specified container.
   * @see Container
   * @see BorderLayout#preferredLayoutSize
   * @see Container#getMinimumSize()
   */
  @Override
  public Dimension minimumLayoutSize(Container target) {
    synchronized (target.getTreeLock()) {
      Dimension dim = new Dimension(0, 0);

      boolean ltr = target.getComponentOrientation().isLeftToRight();
      Component c;

      if ((c = getChild(EAST, ltr)) != null) {
        Dimension d = c.getMinimumSize();
        dim.width += d.width + hgap;
        dim.height = Math.max(d.height, dim.height);
      }
      if ((c = getChild(WEST, ltr)) != null) {
        Dimension d = c.getMinimumSize();
        dim.width += d.width + hgap;
        dim.height = Math.max(d.height, dim.height);
      }
      if ((c = getChild(CENTER, ltr)) != null) {
        Dimension d = c.getMinimumSize();
        dim.width += d.width;
        dim.height = Math.max(d.height, dim.height);
      }
      if ((c = getChild(NORTH, ltr)) != null) {
        Dimension d = c.getMinimumSize();
        dim.width = Math.max(d.width, dim.width);
        dim.height += d.height + vgap;
      }
      if ((c = getChild(SOUTH, ltr)) != null) {
        Dimension d = c.getMinimumSize();
        dim.width = Math.max(d.width, dim.width);
        dim.height += d.height + vgap;
      }

      Insets insets = target.getInsets();
      dim.width += insets.left + insets.right;
      dim.height += insets.top + insets.bottom;

      return dim;
    }
  }

  /**
   * Lays out the container argument using this border layout.
   * <p>
   * This method actually reshapes the components in the specified
   * container in order to satisfy the constraints of this
   * {@code BorderLayout} object. The {@code NORTH}
   * and {@code SOUTH} components, if any, are placed at
   * the top and bottom of the container, respectively. The
   * {@code WEST} and {@code EAST} components are
   * then placed on the left and right, respectively. Finally,
   * the {@code CENTER} object is placed in any remaining
   * space in the middle.
   * <p>
   * Most applications do not call this method directly. This method
   * is called when a container calls its {@code doLayout} method.
   *
   * @param target the container in which to do the layout.
   * @see Container
   * @see Container#doLayout()
   */
  @Override
  public void layoutContainer(Container target) {
    synchronized (target.getTreeLock()) {
      Insets insets = target.getInsets();
      int top = insets.top;
      int bottom = target.height - insets.bottom;
      int left = insets.left;
      int right = target.width - insets.right;

      boolean ltr = target.getComponentOrientation().isLeftToRight();
      Component c;

      if ((c = getChild(NORTH, ltr)) != null) {
        c.setSize(right - left, c.height);
        Dimension d = c.getPreferredSize();
        c.setBounds(left, top, right - left, d.height);
        top += d.height + vgap;
      }
      if ((c = getChild(SOUTH, ltr)) != null) {
        c.setSize(right - left, c.height);
        Dimension d = c.getPreferredSize();
        c.setBounds(left, bottom - d.height, right - left, d.height);
        bottom -= d.height + vgap;
      }
      if ((c = getChild(EAST, ltr)) != null) {
        c.setSize(c.width, bottom - top);
        Dimension d = c.getPreferredSize();
        c.setBounds(right - d.width, top, d.width, bottom - top);
        right -= d.width + hgap;
      }
      if ((c = getChild(WEST, ltr)) != null) {
        c.setSize(c.width, bottom - top);
        Dimension d = c.getPreferredSize();
        c.setBounds(left, top, d.width, bottom - top);
        left += d.width + hgap;
      }
      if ((c = getChild(CENTER, ltr)) != null) {
        c.setBounds(left, top, right - left, bottom - top);
      }
    }
  }

  /**
   * Gets the component that was added using the given constraint
   *
   * @param constraints the desired constraint, one of {@code CENTER},
   *                    {@code NORTH}, {@code SOUTH},
   *                    {@code WEST}, {@code EAST},
   *                    {@code PAGE_START}, {@code PAGE_END},
   *                    {@code LINE_START}, {@code LINE_END}
   * @return the component at the given location, or {@code null} if
   * the location is empty
   * @throws IllegalArgumentException if the constraint object is
   *                                  not one of the nine specified constants
   * @see #addLayoutComponent(Component, Object)
   * @since 1.5
   */
  public Component getLayoutComponent(Object constraints) {
    if (CENTER.equals(constraints)) {
      return center;
    } else if (NORTH.equals(constraints)) {
      return north;
    } else if (SOUTH.equals(constraints)) {
      return south;
    } else if (WEST.equals(constraints)) {
      return west;
    } else if (EAST.equals(constraints)) {
      return east;
    } else if (PAGE_START.equals(constraints)) {
      return firstLine;
    } else if (PAGE_END.equals(constraints)) {
      return lastLine;
    } else if (LINE_START.equals(constraints)) {
      return firstItem;
    } else if (LINE_END.equals(constraints)) {
      return lastItem;
    } else {
      throw new IllegalArgumentException(
          "cannot get component: unknown constraint: " + constraints);
    }
  }

  /**
   * Returns the component that corresponds to the given constraint location
   * based on the target {@code Container}'s component orientation.
   * Components added with the relative constraints {@code PAGE_START},
   * {@code PAGE_END}, {@code LINE_START}, and {@code LINE_END}
   * take precedence over components added with the explicit constraints
   * {@code NORTH}, {@code SOUTH}, {@code WEST}, and {@code EAST}.
   * The {@code Container}'s component orientation is used to determine the location of
   * components
   * added with {@code LINE_START} and {@code LINE_END}.
   *
   * @param constraints the desired absolute position, one of {@code CENTER},
   *                    {@code NORTH}, {@code SOUTH},
   *                    {@code EAST}, {@code WEST}
   * @param target      the {@code Container} used to obtain
   *                    the constraint location based on the target
   *                    {@code Container}'s component orientation.
   * @return the component at the given location, or {@code null} if
   * the location is empty
   * @throws IllegalArgumentException if the constraint object is
   *                                  not one of the five specified constants
   * @throws NullPointerException     if the target parameter is null
   * @see #addLayoutComponent(Component, Object)
   * @since 1.5
   */
  public Component getLayoutComponent(Container target, Object constraints) {
    boolean ltr = target.getComponentOrientation().isLeftToRight();
    Component result;

    if (NORTH.equals(constraints)) {
      result = firstLine != null ? firstLine : north;
    } else if (SOUTH.equals(constraints)) {
      result = lastLine != null ? lastLine : south;
    } else if (WEST.equals(constraints)) {
      result = ltr ? firstItem : lastItem;
      if (result == null) {
        result = west;
      }
    } else if (EAST.equals(constraints)) {
      result = ltr ? lastItem : firstItem;
      if (result == null) {
        result = east;
      }
    } else if (CENTER.equals(constraints)) {
      result = center;
    } else {
      throw new IllegalArgumentException(
          "cannot get component: invalid constraint: " + constraints);
    }

    return result;
  }

  /**
   * Gets the constraints for the specified component
   *
   * @param comp the component to be queried
   * @return the constraint for the specified component,
   * or null if component is null or is not present
   * in this layout
   * @see #addLayoutComponent(Component, Object)
   * @since 1.5
   */
  public Object getConstraints(Component comp) {
    //fix for 6242148 : API method java.awt.BorderLayout.getConstraints(null) should return null
    if (comp == null) {
      return null;
    }
    if (comp == center) {
      return CENTER;
    }
    if (comp == north) {
      return NORTH;
    }
    if (comp == south) {
      return SOUTH;
    }
    if (comp == west) {
      return WEST;
    }
    if (comp == east) {
      return EAST;
    }
    if (comp == firstLine) {
      return PAGE_START;
    }
    if (comp == lastLine) {
      return PAGE_END;
    }
    if (comp == firstItem) {
      return LINE_START;
    }
    if (comp == lastItem) {
      return LINE_END;
    }
    return null;
  }

  /**
   * Get the component that corresponds to the given constraint location
   *
   * @param key The desired absolute position,
   *            either NORTH, SOUTH, EAST, or WEST.
   * @param ltr Is the component line direction left-to-right?
   */
  private Component getChild(String key, boolean ltr) {
    Component result;
    switch (key) {
      case NORTH:
        result = firstLine != null ? firstLine : north;
        break;
      case SOUTH:
        result = lastLine != null ? lastLine : south;
        break;
      case WEST:
        result = ltr ? firstItem : lastItem;
        if (result == null) {
          result = west;
        }
        break;
      case EAST:
        result = ltr ? lastItem : firstItem;
        if (result == null) {
          result = east;
        }
        break;
      case CENTER:
        result = center;
        break;
      default:
        result = null;
    }
    if (result != null && !result.visible) {
      result = null;
    }
    return result;
  }

  /**
   * Returns a string representation of the state of this border layout.
   *
   * @return a string representation of this border layout.
   */
  public String toString() {
    return getClass().getName() + "[hgap=" + hgap + ",vgap=" + vgap + "]";
  }
}
