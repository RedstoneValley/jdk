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

package sun.awt;

import android.view.Display;
import android.view.InputDevice;
import android.view.MotionEvent;
import android.view.View;
import java.awt.Point;
import java.awt.Window;
import java.awt.peer.MouseInfoPeer;

public class DefaultMouseInfoPeer implements MouseInfoPeer {

  private final InputDevice androidInputDevice;
  private volatile float x = -1;
  private volatile float y = -1;
  private volatile int lastDisplay = -1;

  private final View.OnTouchListener androidListener = new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
      Display touchedDisplay = v.getDisplay();
      lastDisplay = touchedDisplay == null ? 0 : touchedDisplay.getDisplayId();
      x = event.getX() + v.getX();
      y = event.getY() + v.getY();
      return false; // do not "consume" this event, in case other code needs it
    }
  };

  /**
   * Package-private constructor to prevent instantiation.
   */
  public DefaultMouseInfoPeer() {
    for (int id : InputDevice.getDeviceIds()) {
      InputDevice device = InputDevice.getDevice(id);
      if (device.supportsSource(InputDevice.SOURCE_MOUSE)) {
        androidInputDevice = device;
        return;
      }
    }
    androidInputDevice = null;
  }

  public void sjMaybeWatchWidget(View widget) {
    if (sjHaveMouse()) {
      widget.setOnTouchListener(androidListener);
    }
  }

  public boolean sjHaveMouse() {
    return androidInputDevice != null;
  }

  @Override
  public int fillPointWithCoords(Point point) {
    point.setLocation(x, y);
    return lastDisplay;
  }

  @Override
  public boolean isWindowUnderMouse(Window w) {
    if (androidInputDevice == null || !w.isActive()) {
      return false;
    }
    Display display = w.sjAndroidWindow.getDecorView().getDisplay();
    return display != null && display.getDisplayId() == lastDisplay;
  }
}
