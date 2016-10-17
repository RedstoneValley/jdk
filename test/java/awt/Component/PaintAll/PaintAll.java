/*
 * Copyright (c) 2011, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

/*
  @test
  @bug 6596915
  @summary Test Component.paintAll() method
  @author sergey.bylokhov@oracle.com: area=awt.component
  @run main PaintAll
*/
public final class PaintAll {

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

    private static final Button buttonStub = new Button() {
        private static final long serialVersionUID = 2009712878760041628L;

        @Override
        public void paint(Graphics g) {
            buttonPainted = true;
        }
    };

    private static final Canvas canvasStub = new Canvas() {
        private static final long serialVersionUID = 3062230612623972902L;

        @Override
        public void paint(Graphics g) {
            canvasPainted = true;
        }
    };

    private static final Checkbox checkboxStub = new Checkbox() {
        private static final long serialVersionUID = -2555941337535991676L;

        @Override
        public void paint(Graphics g) {
            checkboxPainted = true;
        }
    };

    private static final Choice choiceStub = new Choice() {
        private static final long serialVersionUID = -8020671546564801184L;

        @Override
        public void paint(Graphics g) {
            choicePainted = true;
        }
    };

    private static final Component lwComponentStub = new Component() {
        private static final long serialVersionUID = 2952525056084924873L;

        @Override
        public void paint(Graphics g) {
            lwPainted = true;
        }
    };

    private static final Container containerStub = new Container() {
        private static final long serialVersionUID = 4209507396693712650L;

        @Override
        public void paint(Graphics g) {
            containerPainted = true;
        }
    };

    private static final Frame frame = new Frame() {
        private static final long serialVersionUID = -3692549915911700356L;

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            framePainted = true;
        }
    };

    private static final Label labelStub = new Label() {
        private static final long serialVersionUID = 5610635828194499959L;

        @Override
        public void paint(Graphics g) {
            labelPainted = true;
        }
    };

    private static final List listStub = new List() {
        private static final long serialVersionUID = 8229065321092893676L;

        @Override
        public void paint(Graphics g) {
            listPainted = true;
        }
    };

    private static final Panel panelStub = new Panel() {
        private static final long serialVersionUID = -5546183174813082101L;

        @Override
        public void paint(Graphics g) {
            panelPainted = true;
        }
    };

    private static final Scrollbar scrollbarStub = new Scrollbar() {
        private static final long serialVersionUID = 1597406021799175433L;

        @Override
        public void paint(Graphics g) {
            scrollbarPainted = true;
        }
    };

    private static final ScrollPane scrollPaneStub = new ScrollPane() {
        private static final long serialVersionUID = 9120061519583106371L;

        @Override
        public void paint(Graphics g) {
            scrollPanePainted = true;
        }
    };

    private static final TextArea textAreaStub = new TextArea() {
        private static final long serialVersionUID = 8207182200039517139L;

        @Override
        public void paint(Graphics g) {
            textAreaPainted = true;
        }
    };

    private static final TextField textFieldStub = new TextField() {
        private static final long serialVersionUID = 9151860287885547173L;

        @Override
        public void paint(Graphics g) {
            textFieldPainted = true;
        }
    };

    private PaintAll() {
    }

    public static void main(String[] args) throws Exception {
        //Frame initialisation
        BufferedImage graphicsProducer =
                new BufferedImage(BufferedImage.TYPE_INT_ARGB, 1, 1);

        Graphics g = graphicsProducer.getGraphics();

        frame.setLayout(new GridLayout());
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
        frame.setSize(new Dimension(500, 500));
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        sleep();

        //Check results.
        validation();

        //Reset all flags to 'false'.
        initPaintedFlags();

        //Tested method.
        frame.paintAll(g);
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
            fail("Paint is not called a Button "
                 + "when paintAll() invoked on a parent");
        }
        if (!canvasPainted) {
            fail("Paint is not called a Canvas "
                 + "when paintAll() invoked on a parent");
        }
        if (!checkboxPainted) {
            fail("Paint is not called a Checkbox "
                 + "when paintAll() invoked on a parent");
        }
        if (!choicePainted) {
            fail("Paint is not called a Choice "
                 + "when paintAll() invoked on a parent");
        }
        if (!lwPainted) {
            fail("Paint is not called on a lightweight"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!containerPainted) {
            fail("Paint is not called on a Container"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!labelPainted) {
            fail("Paint is not called on a Label"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!listPainted) {
            fail("Paint is not called on a List"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!panelPainted) {
            fail("Paint is not called on a Panel"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!scrollbarPainted) {
            fail("Paint is not called on a Scrollbar"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!scrollPanePainted) {
            fail("Paint is not called on a ScrollPane"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!textAreaPainted) {
            fail("Paint is not called on a TextArea"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!textFieldPainted) {
            fail("Paint is not called on a TextField"
                 + " subcomponent when paintAll() invoked on a parent");
        }
        if (!framePainted) {
            fail("Paint is not called on a Frame when paintAll()");
        }
    }

    private static void sleep() {
        ((SunToolkit) Toolkit.getDefaultToolkit()).realSync();
        try {
            Thread.sleep(500L);
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
