/*
 * Copyright (c) 1997, 2012, Oracle and/or its affiliates. All rights reserved.
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

package sun.awt.im;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.im.spi.InputMethod;

/**
 * An input method adapter interfaces with the native input methods on a host platform. In general,
 * it looks to the input method framework like a Java input method (that may support a few more
 * locales than a typical Java input method). However, since it often has to work in a slightly
 * hostile environment that's not designed for easy integration into the Java input method
 * framework, it gets some special treatment that's not available to Java input methods.
 * <p>
 * Licensees are free to modify this class as necessary to implement their host input method
 * adapters.
 *
 * @author JavaSoft International
 */

public abstract class InputMethodAdapter implements InputMethod {

  /**
   * Informs the input method adapter about the component that has the AWT focus if it's using the
   * input context owning this adapter instance.
   */
  protected void setAWTFocussedComponent(Component component) {
    // ignore - adapters can override if needed
  }

  /**
   * Returns whether host input methods can support below-the-spot input. Returns false by default.
   */
  protected boolean supportsBelowTheSpot() {
    return false;
  }

  /**
   * Informs the input method adapter not to listen to the native events.
   */
  protected void stopListening() {
    // ignore - adapters can override if needed
  }

  /**
   * Starts reconvertion. An implementing host adapter has to override this method if it can support
   * reconvert().
   *
   * @throws UnsupportedOperationException when the adapter does not override the method.
   */
  @Override
  public void reconvert() {
    throw new UnsupportedOperationException();
  }

  /**
   * Notifies client Window location or status changes
   */
  @Override
  public void notifyClientWindowChange(Rectangle location) {
  }

  /**
   * Returns a string with information about the native input method, or null.
   */
  public abstract String getNativeInputMethodInfo();
}
