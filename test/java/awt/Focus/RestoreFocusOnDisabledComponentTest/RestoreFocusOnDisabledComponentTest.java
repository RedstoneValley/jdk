/*
 * Copyright (c) 2007, 2008, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

/*
  @test      %W% %E%
  @bug       6598089
  @summary   Tests restoring focus on a single disabled coponent
  @author    Anton Tarasov: area=awt-focus
  @library   ../../regtesthelpers
  @build     Util
  @run       main RestoreFocusOnDisabledComponentTest
*/

import java.awt.*;
import java.awt.event.*;

/*
 * The bug is not reproducible on Windows.
 */
public class RestoreFocusOnDisabledComponentTest extends Applet {
    final Frame frame = new Frame("Frame") {
        private static final long serialVersionUID = 8315022443987169807L;

        public String toString() {return "FRAME";}};
    final Button b0 = new Button("button0") {
        private static final long serialVersionUID = -159716841733806064L;

        public String toString() {return "B-0";}};
    final Button b1 = new Button("button1") {
        private static final long serialVersionUID = 5550450948413255674L;

        public String toString() {return "B-1";}};
    volatile int nFocused;
    Robot robot;

    public static void main(String[] args) {
        RestoreFocusOnDisabledComponentTest app = new RestoreFocusOnDisabledComponentTest();
        app.init();
        app.start();
    }

    public void init() {
        robot = Util.createRobot();
    }

    public void start() {
        frame.add(b0);
        frame.add(b1);
        frame.setLayout(new FlowLayout());
        frame.pack();

        frame.setVisible(true);

        Util.waitForIdle(robot);
        KeyboardFocusManager.setCurrentKeyboardFocusManager(new DefaultKeyboardFocusManager() {
            @Override
            public boolean dispatchEvent(AWTEvent e) {
                if (e.getID() == FocusEvent.FOCUS_GAINED) {
                    // Trying to emulate timings. b1 should be disabled just by the time it gets
                    // FOCUS_GAINED event. The latter is a result of disabling b0 that initiates
                    // focus auto transfer.
                    if (e.getSource() == b1) {
                        b1.setEnabled(false);

                    } else if (e.getSource() == b0) {
                        ++nFocused;
                        if (nFocused > 10) {
                            nFocused = -1;
                            throw new TestFailedException("Focus went into busy loop!");
                        }
                    }
                }
                return super.dispatchEvent(e);
            }
        });
        // Initiating focus auto transfer.
        // Focus will be requested to b1. When FOCUS_GAINED is being dispatched to b1, it will
        // be disabled. This will trigger focus restoring. Focus will be requested to b0 (the
        // last opposite component). When FOCUS_GAINED is being dispatched to b0, it will
        // also be disabled. However, the last opposite component (and the most recent focus owner)
        // will still be b0. When DKFM initiates focus restoring it should detect restoring
        // on the same component and break.
        b0.setEnabled(false);

        Util.waitForIdle(robot);
        if (nFocused != -1) {
            System.out.println("Test passed.");
        }
    }
}

class TestFailedException extends RuntimeException {
    private static final long serialVersionUID = -6211481026000527924L;

    TestFailedException(String msg) {
        super("Test failed: " + msg);
    }
}
