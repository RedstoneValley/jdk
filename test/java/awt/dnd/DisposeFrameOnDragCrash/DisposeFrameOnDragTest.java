/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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
  @test @summary JVM crash if the frame is disposed in DropTargetListener
 * @author Petr Pchelko
 * @library ../../regtesthelpers
 * @build Util
 * @compile DisposeFrameOnDragTest.java
 * @run main/othervm DisposeFrameOnDragTest
 */
import java.awt.AWTException;
import java.awt.Point;
import java.awt.Robot;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.InputEvent;
import java.util.TooManyListenersException;

public final class DisposeFrameOnDragTest {

    private static JTextArea textArea;

    private DisposeFrameOnDragTest() {
    }

    public static void main(String[] args) throws Throwable {

        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                constructTestUI();
            }
        });

        Util.waitForIdle(null);
        try {
            Point loc = textArea.getLocationOnScreen();
            Util.drag(new Robot(),
                    new Point(loc.x + 3, loc.y + 3),
                    new Point(loc.x + 40, loc.y + 40),
                    InputEvent.BUTTON1_MASK);
        } catch (AWTException ex) {
            throw new RuntimeException("Could not initiate a drag operation");
        }
        Util.waitForIdle(null);
    }

    static void constructTestUI() {
        JFrame frame = new JFrame("Test frame");
        textArea = new JTextArea("Drag Me!");
        try {
            textArea.getDropTarget().addDropTargetListener(new DropTargetAdapter() {
                @Override
                public void drop(DropTargetDropEvent dtde) {
                    //IGNORE
                }

                @Override
                public void dragOver(DropTargetDragEvent dtde) {
                    frame.dispose();
                }
            });
        } catch (TooManyListenersException ex) {
            throw new RuntimeException(ex);
        }
        textArea.setSize(100, 100);
        textArea.setDragEnabled(true);
        textArea.select(0, textArea.getText().length());
        frame.add(textArea);
        frame.setBounds(100, 100, 100, 100);
        frame.setVisible(true);
    }
}
