/*
 * Copyright (c) 2007, Oracle and/or its affiliates. All rights reserved.
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
  @test
  @bug 6451578
  @library ../../../regtesthelpers
  @build Sysout AbstractTest Util
  @summary A key event could have wrong time
  @author andrei dmitriev : area=awt.event
  @run main CorrectTime
*/

import java.awt.*;
import java.awt.event.*;

public class CorrectTime extends Frame implements KeyListener {
    //Maximum time the event should arrive to listener
    static final int REASONABLE_PATH_TIME = 5000;
    static final TextField tf = new TextField("press keys", 10);
    static final TextArea ta = new TextArea("press keys", 10, 10);
    static final Button bt = new Button("press button");
    static final List list = new List();
    static final Robot robot = Util.createRobot();
    private static final long serialVersionUID = -3689084737229729893L;

    public CorrectTime(){
        super("Check time of KeyEvents");

        setPreferredSize(new Dimension(400,400));
        setLayout(new FlowLayout());

        list.add("item1");
        list.add("item2");
        list.add("item3");

        add(bt);
        add(tf);
        add(ta);
        add(list);

        bt.addKeyListener(this);
        tf.addKeyListener(this);
        ta.addKeyListener(this);
        list.addKeyListener(this);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String []s) {
        Frame frame = new CorrectTime();

        Robot robot = Util.createRobot();
        Util.waitForIdle(robot);

        testComponent(tf);
        testComponent(ta);
        testComponent(bt);
        testComponent(list);

    }

    private static void testComponent(Component comp){
        Runnable action = new Runnable(){
                @Override
                public void run(){
                    Util.clickOnComp(comp, robot);
                    Util.waitForIdle(robot);
                }
            };

        if (! Util.trackFocusGained(comp, action, REASONABLE_PATH_TIME, true)){
            AbstractTest.fail("Focus didn't come to " + comp);
        }
        testKeys();
        Util.waitForIdle(robot);
    }

    private static void testKeys(){
        //set of keys is random
        typeKey(KeyEvent.VK_A);
        typeKey(KeyEvent.VK_B);
        typeKey(KeyEvent.VK_SPACE);
        typeKey(KeyEvent.VK_Z);
    }

    private static void typeKey(int keyChar){
        try {
            robot.keyPress(keyChar);
            robot.delay(5);
        } finally {
            robot.keyRelease(keyChar);
        }
        robot.delay(100);
    }

    private void traceKey(String k, KeyEvent e){
        long eventTime = e.getWhen();
        long currTime = System.currentTimeMillis();
        long diff = currTime - eventTime;
        Sysout.println(k + " diff is " + diff + ", event is "+ e);
        if (diff < 0 ||
            diff > REASONABLE_PATH_TIME)
        {
            AbstractTest.fail(k + " diff is " + diff + ", event = "+e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e){
        traceKey("keytyped",e);
    }
    @Override
    public void keyPressed(KeyEvent e){
        traceKey("keypress",e);
    }
    @Override
    public void keyReleased(KeyEvent e){
        traceKey("keyrelease",e);
    }
}
