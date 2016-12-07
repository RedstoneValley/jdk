/*
 * Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Component;
import java.awt.event.FocusEvent;

/**
 * This class represents FocusEvents with a known "cause" - reason why this event happened. It can
 * be mouse press, traversal, activation, and so on - all causes are described as Cause enum. The
 * event with the cause can be constructed in two ways - explicitly through constructor of
 * CausedFocusEvent class or implicitly, by calling appropriate requestFocusXXX method with "cause"
 * parameter. The default cause is UNKNOWN.
 */
@SuppressWarnings("serial")
public class CausedFocusEvent extends FocusEvent {
  private final Cause cause;

  public CausedFocusEvent(
      Component source, int id, boolean temporary, Component opposite, Cause cause) {
    super(source, id, temporary, opposite);
    if (cause == null) {
      cause = Cause.UNKNOWN;
    }
    this.cause = cause;
  }

  public Cause getCause() {
    return cause;
  }

  public String toString() {
    return "java.awt.FocusEvent[" + paramString() + ",cause=" + cause + "] on " + getSource();
  }

  public enum Cause {
    UNKNOWN,
    TRAVERSAL_UP,
    TRAVERSAL_DOWN,
    TRAVERSAL_FORWARD,
    TRAVERSAL_BACKWARD,
    ROLLBACK,
    NATIVE_SYSTEM,
    ACTIVATION,
    CLEAR_GLOBAL_FOCUS_OWNER,
    RETARGETED
  }
}
