/*
 * Copyright (c) 2002, Oracle and/or its affiliates. All rights reserved.
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
  test
  @bug 4664415
  @summary REGRESSION: double click jframe titlebar generating mouse events in panel
  @author Andrei Dmitriev: area=awt.mouse
  @run applet TitleBarDoubleClick.html
*/
import java.awt.*;
import java.awt.event.*;

public class TitleBarDoubleClick extends Applet implements MouseListener,
 WindowListener
{
    //Declare things used in the test, like buttons and labels here
    private static final Rectangle BOUNDS = new Rectangle(300, 300, 300, 300);
    private static final int TITLE_BAR_OFFSET = 10;

    Frame frame;
    Robot robot;

    public void init()
    {
        setLayout(new BorderLayout ());

    }//End  init()

    public void start ()
    {
        //Get things going.  Request focus, set size, et cetera
        setSize (200,200);
        setVisible(true);
        validate();

        //What would normally go into main() will probably go here.
        //Use System.out.println for diagnostic messages that you want
        //to read after the test is done.
        //Use Sysout.println for messages you want the tester to read.

        robot = Util.createRobot();
        robot.setAutoDelay(100);
        robot.mouseMove(BOUNDS.x + BOUNDS.width / 2,
                            BOUNDS.y + BOUNDS.height/ 2);

        frame = new Frame("TitleBarDoubleClick");
        frame.setBounds(BOUNDS);
        frame.addMouseListener(this);
        frame.addWindowListener(this);
        frame.setVisible(true);
            Util.waitForIdle(robot);
    }// start()

    // Move the mouse into the title bar and double click to maximize the
    // Frame
    static boolean hasRun;

    private void doTest() {
        if (hasRun) {
            return;
        }
        hasRun = true;

        System.out.println("doing test");
        robot.mouseMove(BOUNDS.x + BOUNDS.width / 2,
                            BOUNDS.y + TITLE_BAR_OFFSET);
        robot.delay(50);
            // Util.waitForIdle(robot) seem always hangs here.
            // Need to use it instead robot.delay() when the bug become fixed.
            System.out.println("1st press:   currentTimeMillis: " + System.currentTimeMillis());
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(50);
            System.out.println("1st release: currentTimeMillis: " + System.currentTimeMillis());
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(50);
            System.out.println("2nd press:   currentTimeMillis: " + System.currentTimeMillis());
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(50);
            System.out.println("2nd release: currentTimeMillis: " + System.currentTimeMillis());
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
            System.out.println("done:        currentTimeMillis: " + System.currentTimeMillis());
    }

    private void fail() {
        throw new AWTError("Test failed");
    }

    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {
        fail();}
    @Override
    public void mouseReleased(MouseEvent e) {
        fail();}
    @Override
    public void mouseClicked(MouseEvent e) {
        fail();}

    @Override
    public void windowActivated(WindowEvent  e) {
        doTest();}
    @Override
    public void windowClosed(WindowEvent  e) {}
    @Override
    public void windowClosing(WindowEvent  e) {}
    @Override
    public void windowDeactivated(WindowEvent  e) {}
    @Override
    public void windowDeiconified(WindowEvent  e) {}
    @Override
    public void windowIconified(WindowEvent  e) {}
    @Override
    public void windowOpened(WindowEvent  e) {}

}// class TitleBarDoubleClick
