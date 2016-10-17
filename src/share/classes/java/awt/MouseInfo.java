/*
 * Copyright (c) 2003, Oracle and/or its affiliates. All rights reserved.
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
 * {@code MouseInfo}  provides methods for getting information about the mouse,
 * such as mouse pointer location and the number of mouse buttons.
 *
 * @author Roman Poborchiy
 * @since 1.5
 */

public final class MouseInfo {

  /**
   * Private constructor to prevent instantiation.
   */
  private MouseInfo() {
  }

  /**
   * Returns a {@code PointerInfo} instance that represents the current
   * location of the mouse pointer.
   * The {@code GraphicsDevice} stored in this {@code PointerInfo}
   * contains the mouse pointer. The coordinate system used for the mouse position
   * depends on whether or not the {@code GraphicsDevice} is part of a virtual
   * screen device.
   * For virtual screen devices, the coordinates are given in the virtual
   * coordinate system, otherwise they are returned in the coordinate system
   * of the {@code GraphicsDevice}. See {@link GraphicsConfiguration}
   * for more information about the virtual screen devices.
   * On systems without a mouse, returns {@code null}.
   * <p>
   * If there is a security manager, its {@code checkPermission} method
   * is called with an {@code AWTPermission("watchMousePointer")}
   * permission before creating and returning a {@code PointerInfo}
   * object. This may result in a {@code SecurityException}.
   *
   * @return location of the mouse pointer
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true
   * @throws SecurityException if a security manager exists and its
   *                           {@code checkPermission} method doesn't allow the operation
   * @see GraphicsConfiguration
   * @see SecurityManager#checkPermission
   * @see AWTPermission
   * @since 1.5
   */
  public static PointerInfo getPointerInfo() throws HeadlessException {

    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      security.checkPermission(SecurityConstants.AWT.WATCH_MOUSE_PERMISSION);
    }

    Point point = new Point(0, 0);
    int deviceNum = Toolkit.getDefaultToolkit().getMouseInfoPeer().fillPointWithCoords(point);
    GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().
        getScreenDevices();
    PointerInfo retval = null;
    if (areScreenDevicesIndependent(gds)) {
      retval = new PointerInfo(gds[deviceNum], point);
    } else {
      for (GraphicsDevice gd : gds) {
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        Rectangle bounds = gc.getBounds();
        if (bounds.contains(point)) {
          retval = new PointerInfo(gd, point);
        }
      }
    }

    return retval;
  }

  private static boolean areScreenDevicesIndependent(GraphicsDevice[] gds) {
    for (GraphicsDevice gd : gds) {
      Rectangle bounds = gd.getDefaultConfiguration().getBounds();
      if (bounds.x != 0 || bounds.y != 0) {
        return false;
      }
    }
    return true;
  }

  /**
   * Returns the number of buttons on the mouse.
   * On systems without a mouse, returns {@code -1}.
   *
   * @return number of buttons on the mouse
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true
   * @since 1.5
   */
  public static int getNumberOfButtons() throws HeadlessException {
    Object prop = Toolkit.getDefaultToolkit().
        getDesktopProperty("awt.mouse.numButtons");
    if (prop instanceof Integer) {
      return (Integer) prop;
    }

    // This should never happen.
    assert false : "awt.mouse.numButtons is not an integer property";
    return 0;
  }
}
