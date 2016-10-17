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


import sun.awt.SunToolkit;

import java.awt.*;

/**
 * @test
 * @bug 7090424
 * @author Sergey Bylokhov
 * @run main ExposeOnEDT
 */
public final class ExposeOnEDT {

    private static final Button buttonStub = new Button() {
        private static final long serialVersionUID = -4743986035967015033L;

        @Override
        public void paint(Graphics g) {
            buttonPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Canvas canvasStub = new Canvas() {
        private static final long serialVersionUID = 5732646970257682437L;

        @Override
        public void paint(Graphics g) {
            canvasPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Checkbox checkboxStub = new Checkbox() {
        private static final long serialVersionUID = -41582990038994293L;

        @Override
        public void paint(Graphics g) {
            checkboxPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Choice choiceStub = new Choice() {
        private static final long serialVersionUID = -8759485038495261077L;

        @Override
        public void paint(Graphics g) {
            choicePainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Component lwComponentStub = new Component() {
        private static final long serialVersionUID = 7282716688496605431L;

        @Override
        public void paint(Graphics g) {
            lwPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Container containerStub = new Container() {
        private static final long serialVersionUID = 2200997310761766160L;

        @Override
        public void paint(Graphics g) {
            containerPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Frame frame = new Frame() {
        private static final long serialVersionUID = -6327440091782737735L;

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            framePainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Label labelStub = new Label() {
        private static final long serialVersionUID = 7310712807759787263L;

        @Override
        public void paint(Graphics g) {
            labelPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final List listStub = new List() {
        private static final long serialVersionUID = 1359117189150937098L;

        @Override
        public void paint(Graphics g) {
            listPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Panel panelStub = new Panel() {
        private static final long serialVersionUID = -1583370072912567353L;

        @Override
        public void paint(Graphics g) {
            panelPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final Scrollbar scrollbarStub = new Scrollbar() {
        private static final long serialVersionUID = -4089405787449653471L;

        @Override
        public void paint(Graphics g) {
            scrollbarPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final ScrollPane scrollPaneStub = new ScrollPane() {
        private static final long serialVersionUID = 287842786390684915L;

        @Override
        public void paint(Graphics g) {
            scrollPanePainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final TextArea textAreaStub = new TextArea() {
        private static final long serialVersionUID = -3354190101437831523L;

        @Override
        public void paint(Graphics g) {
            textAreaPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    private static final TextField textFieldStub = new TextField() {
        private static final long serialVersionUID = -725466496110884248L;

        @Override
        public void paint(Graphics g) {
            textFieldPainted = true;
            if (!EventQueue.isDispatchThread()) {
                throw new RuntimeException("Wrong thread");
            }
        }
    };
    static volatile boolean lwPainted;
    static volatile boolean buttonPainted;
    static volatile boolean canvasPainted;
    static volatile boolean checkboxPainted;
    static volatile boolean choicePainted;
    static volatile boolean containerPainted;
    static volatile boolean framePainted;
    static volatile boolean labelPainted;
    static volatile boolean listPainted;
    static volatile boolean panelPainted;
    static volatile boolean scrollbarPainted;
    static volatile boolean scrollPanePainted;
    static volatile boolean textAreaPainted;
    static volatile boolean textFieldPainted;

    private ExposeOnEDT() {
    }

    public static void main(String[] args) throws Exception {
        //Frame initialisation
        frame.setLayout(new GridLayout());
        frame.setSize(new Dimension(200, 200));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        sleep();

        frame.add(buttonStub);
        frame.add(canvasStub);
        frame.add(checkboxStub);
        frame.add(choiceStub);
        frame.add(lwComponentStub);
        frame.add(containerStub);
        frame.add(labelStub);
        frame.add(listStub);
        frame.add(panelStub);
        frame.add(scrollbarStub);
        frame.add(scrollPaneStub);
        frame.add(textAreaStub);
        frame.add(textFieldStub);
        frame.validate();
        sleep();

        // Force expose event from the native system.
        initPaintedFlags();
        frame.setSize(300, 300);
        frame.validate();
        sleep();

        //Check results.
        validation();

        cleanup();
    }

    private static void initPaintedFlags() {
        lwPainted = false;
        buttonPainted = false;
        canvasPainted = false;
        checkboxPainted = false;
        choicePainted = false;
        containerPainted = false;
        framePainted = false;
        labelPainted = false;
        listPainted = false;
        panelPainted = false;
        scrollbarPainted = false;
        scrollPanePainted = false;
        textAreaPainted = false;
        textFieldPainted = false;
    }

    private static void validation() {
        if (!buttonPainted) {
            fail("Paint is not called a Button ");
        }
        if (!canvasPainted) {
            fail("Paint is not called a Canvas ");
        }
        if (!checkboxPainted) {
            fail("Paint is not called a Checkbox ");
        }
        if (!choicePainted) {
            fail("Paint is not called a Choice ");
        }
        if (!lwPainted) {
            fail("Paint is not called on a lightweight");
        }
        if (!containerPainted) {
            fail("Paint is not called on a Container");
        }
        if (!labelPainted) {
            fail("Paint is not called on a Label");
        }
        if (!listPainted) {
            fail("Paint is not called on a List");
        }
        if (!panelPainted) {
            fail("Paint is not called on a Panel");
        }
        if (!scrollbarPainted) {
            fail("Paint is not called on a Scrollbar");
        }
        if (!scrollPanePainted) {
            fail("Paint is not called on a ScrollPane");
        }
        if (!textAreaPainted) {
            fail("Paint is not called on a TextArea");
        }
        if (!textFieldPainted) {
            fail("Paint is not called on a TextField");
        }
        if (!framePainted) {
            fail("Paint is not called on a Frame when paintAll()");
        }
    }

    private static void sleep() {
        ((SunToolkit) Toolkit.getDefaultToolkit()).realSync();
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException ignored) {
        }
    }

    private static void fail(String message) {
        cleanup();
        throw new RuntimeException(message);
    }

    private static void cleanup() {
        frame.dispose();
    }
}
