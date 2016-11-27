/*
 * Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved.
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
import java.util.Locale;

/**
 * {@code InputMethodManager} is an abstract class that manages the input
 * method environment of JVM. There is only one {@code InputMethodManager}
 * instance in JVM that is executed under a separate daemon thread.
 * {@code InputMethodManager} performs the following:
 * <UL>
 * <LI>
 * Keeps track of the current input context.</LI>
 * <p>
 * <LI>
 * Provides a user interface to switch input methods and notifies the current
 * input context about changes made from the user interface.</LI>
 * </UL>
 * <p>
 * The mechanism for supporting input method switch is as follows. (Note that
 * this may change in future releases.)
 * <p>
 * <UL>
 * <LI>
 * One way is to use platform-dependent window manager's menu (known as the <I>Window
 * menu </I>in Motif and the <I>System menu</I> or <I>Control menu</I> in
 * Win32) on each window which is popped up by clicking the left top box of
 * a window (known as <I>Window menu button</I> in Motif and <I>System menu
 * button</I> in Win32). This happens to be common in both Motif and Win32.</LI>
 * <p>
 * <LI>
 * When more than one input method descriptor can be found or the only input
 * method descriptor found supports multiple locales, a menu item
 * is added to the window (manager) menu. This item label is obtained invoking
 * {@code getTriggerMenuString()}. If null is returned by this method, it
 * means that there is only input method or none in the environment. Frame and Dialog
 * invoke this method.</LI>
 * <p>
 * <LI>
 * This menu item means a trigger switch to the user to pop up a selection
 * menu.</LI>
 * <p>
 * <LI>
 * When the menu item of the window (manager) menu has been selected by the
 * user, Frame/Dialog invokes {@code notifyChangeRequest()} to notify
 * {@code InputMethodManager} that the user wants to switch input methods.</LI>
 * <p>
 * <LI>
 * {@code InputMethodManager} displays a pop-up menu to choose an input method.</LI>
 * <p>
 * <LI>
 * {@code InputMethodManager} notifies the current {@code InputContext} of
 * the selected {@code InputMethod}.</LI>
 * </UL>
 * <p>
 * <UL>
 * <LI>
 * The other way is to use user-defined hot key combination to show the pop-up menu to
 * choose an input method.  This is useful for the platforms which do not provide a
 * way to add a menu item in the window (manager) menu.</LI>
 * <p>
 * <LI>
 * When the hot key combination is typed by the user, the component which has the input
 * focus invokes {@code notifyChangeRequestByHotKey()} to notify
 * {@code InputMethodManager} that the user wants to switch input methods.</LI>
 * <p>
 * <LI>
 * This results in a popup menu and notification to the current input context,
 * as above.</LI>
 * </UL>
 *
 * @author JavaSoft International
 * @see java.awt.im.spi.InputMethod
 * @see InputContext
 * @see InputMethodAdapter
 */

public abstract class InputMethodManager {

  /**
   * InputMethodManager thread name
   */
  private static final String threadName = "AWT-InputMethodManager";

  /**
   * Object for global locking
   */
  private static final Object LOCK = new Object();

  /**
   * The InputMethodManager instance
   */
  private static InputMethodManager inputMethodManager;

  /**
   * Returns the instance of InputMethodManager. This method creates
   * the instance that is unique in the Java VM if it has not been
   * created yet.
   *
   * @return the InputMethodManager instance
   */
  public static final InputMethodManager getInstance() {
    if (inputMethodManager != null) {
      return inputMethodManager;
    }
    synchronized (LOCK) {
      if (inputMethodManager == null) {
        ExecutableInputMethodManager imm = new ExecutableInputMethodManager();

        // Initialize the input method manager and start a
        // daemon thread if the user has multiple input methods
        // to choose from. Otherwise, just keep the instance.
        if (imm.hasMultipleInputMethods()) {
          imm.initialize();
          Thread immThread = new Thread(imm, threadName);
          immThread.setDaemon(true);
          immThread.setPriority(Thread.NORM_PRIORITY + 1);
          immThread.start();
        }
        inputMethodManager = imm;
      }
    }
    return inputMethodManager;
  }

  /**
   * Notifies InputMethodManager that input method change has been
   * requested by the user. This notification triggers a popup menu
   * for user selection.
   *
   * @param comp Component that has accepted the change
   *             request. This component has to be a Frame or Dialog.
   */
  public abstract void notifyChangeRequest(Component comp);

  /**
   * Notifies InputMethodManager that input method change has been
   * requested by the user using the hot key combination. This
   * notification triggers a popup menu for user selection.
   *
   * @param comp Component that has accepted the change
   *             request. This component has the input focus.
   */
  public abstract void notifyChangeRequestByHotKey(Component comp);

  /**
   * Sets the current input context so that it will be notified
   * of input method changes initiated from the user interface.
   * Set to real input context when activating; to null when
   * deactivating.
   */
  abstract void setInputContext(InputContext inputContext);

  /**
   * Tries to find an input method locator for the given locale.
   * Returns null if no available input method locator supports
   * the locale.
   */
  abstract InputMethodLocator findInputMethod(Locale forLocale);

  /**
   * Gets the default keyboard locale of the underlying operating system.
   */
  abstract Locale getDefaultKeyboardLocale();

  /**
   * Returns whether multiple input methods are available or not
   */
  abstract boolean hasMultipleInputMethods();
}
