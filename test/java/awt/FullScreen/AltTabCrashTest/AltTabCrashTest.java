/*
 * Copyright (c) 2005, 2014 Oracle and/or its affiliates. All rights reserved.
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
 @bug 6275887 6429971 6459792
 @summary Test that we don't crash when alt+tabbing in and out of
         fullscreen app
 @author Dmitri.Trembovetski@sun.com: area=FullScreen
 @run main/othervm/timeout=100  AltTabCrashTest -auto -changedm
 @run main/othervm/timeout=100 -Dsun.java2d.d3d=True AltTabCrashTest -auto -changedm
 @run main/othervm/timeout=100 -Dsun.java2d.d3d=True AltTabCrashTest -auto -usebs -changedm
 @run main/othervm/timeout=100 -Dsun.java2d.opengl=True AltTabCrashTest -auto
*/

import java.awt.AWTException;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.Random;

/**
 * Note that the alt+tabbing in and out part will most likely only work
 * on Windows, and only if there are no interventions.
 */

@SuppressWarnings("MagicNumber")
public class AltTabCrashTest extends Frame {
    public static final int NUM_OF_BALLS = 70;
    static final Object lock = new Object();
    static final Random rnd = new Random();
    private static final long serialVersionUID = 5908506776273306913L;
    public static int width;
    public static int height;
    public static volatile boolean autoMode;
    public static boolean useBS;
    // number of times to alt+tab in and out of the app
    public static int altTabs = 5;
    private static boolean changeDM;
    private static SpriteType spriteType;
    final ArrayList<Ball> balls = new ArrayList<>();
    final GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
        .getDefaultScreenDevice();
    VolatileImage vimg;
    BufferStrategy bufferStrategy;
    volatile boolean timeToQuit;
    public AltTabCrashTest( ) {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    timeToQuit = true;
                }
            }
        });
        setIgnoreRepaint(true);
        addMouseListener(new MouseHandler());
        for (int i = 0; i < NUM_OF_BALLS; i++) {
            int x = 50 + rnd.nextInt(550), y = 50 + rnd.nextInt(400);

            balls.addElement(createRandomBall(y, x));
        }
        setUndecorated(true);
        gd.setFullScreenWindow(this);
        GraphicsDevice gd = getGraphicsConfiguration().getDevice();
        if (gd.isDisplayChangeSupported() && changeDM) {
            DisplayMode dm = findDisplayMode();
            if (dm != null) {
                try {
                    gd.setDisplayMode(dm);
                } catch (IllegalArgumentException iae) {
                    System.err.println("Error setting display mode");
                }
            }
        }
        if (useBS) {
            createBufferStrategy(2);
            bufferStrategy = getBufferStrategy();
        } else {
            Graphics2D g = (Graphics2D) getGraphics();
            render(g);
            g.dispose();
        }
        Thread t = new BallThread();
        t.start();
        if (autoMode) {
            Thread tt = new AltTabberThread();
            tt.start();
            synchronized (lock) {
                while (!timeToQuit) {
                    try {
                        lock.wait(200);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
            dispose();
        }
    }

    public static void main(String[] args) {
        for (String arg : args) {
            if ("-auto".equalsIgnoreCase(arg)) {
                autoMode = true;
                System.err.println("Running in automatic mode using Robot");
            } else if ("-usebs".equalsIgnoreCase(arg)) {
                useBS = true;
                System.err.println("Using BufferStrategy instead of VI");
            } else if ("-changedm".equalsIgnoreCase(arg)) {
                changeDM = true;
                System.err.println("The test will change display mode");
            } else if ("-vi".equalsIgnoreCase(arg)) {
                spriteType = SpriteType.VIMAGES;
            } else if ("-bi".equalsIgnoreCase(arg)) {
                spriteType = SpriteType.BIMAGES;
            } else if ("-ov".equalsIgnoreCase(arg)) {
                spriteType = SpriteType.OVALS;
            } else if ("-aaov".equalsIgnoreCase(arg)) {
                spriteType = SpriteType.AAOVALS;
            } else if ("-tx".equalsIgnoreCase(arg)) {
                spriteType = SpriteType.TEXT;
            } else {
                System.err.println("Usage: AltTabCrashTest [-usebs][-auto]"
                    + "[-changedm][-vi|-bi|-ov|-aaov|-tx]");
                System.err.println(" -usebs: use BufferStrategy instead of VI");
                System.err.println(
                    " -auto: automatically alt+tab in and out" + " of the application ");
                System.err.println(" -changedm: change display mode");
                System.err.println(
                    " -(vi|bi|ov|tx|aaov) : use only VI, BI, " + "text or [AA] [draw]Oval sprites");
                System.exit(0);
            }
        }
        if (spriteType != null) {
            System.err.println("The test will only use " + spriteType + " sprites.");
        }
        new AltTabCrashTest();
    }

    Ball createRandomBall(int y, int x) {
        Ball b;
        SpriteType type;

        if (spriteType == null) {
            int index = rnd.nextInt(SpriteType.values().length);
            type = SpriteType.values()[index];
        } else {
            type = spriteType;
        }
        switch (type) {
            case VIMAGES: b = new VISpriteBall(x, y); break;
            case AAOVALS: b = new AAOvalBall(x, y); break;
            case BIMAGES: b = new BISpriteBall(x, y); break;
            case TEXT: b = new TextBall(x,y, "Text Sprite!"); break;
            default: b = new Ball(x, y); break;
        }
        return b;
    }

    public void renderOffscreen() {
        Graphics2D g2d = (Graphics2D) vimg.getGraphics();
        synchronized (balls) {
            for (Ball b : balls) {
                b.paint(g2d, getBackground());
                b.move();
                b.paint(g2d, null);
            }
        }
        g2d.dispose();
    }

    public void renderToBS() {
        width = getWidth();
        height = getHeight();

        do {
            Graphics2D g2d = (Graphics2D) bufferStrategy.getDrawGraphics();

            g2d.clearRect(0, 0, width, height);
            synchronized (balls) {
                for (Ball b : balls) {
                    b.move();
                    b.paint(g2d, null);
                }
            }
            g2d.dispose();
        } while (bufferStrategy.contentsLost() || bufferStrategy.contentsRestored());
    }

    public void render(Graphics g) {
        do {
            height = getBounds().height;
            width = getBounds().width;
            if (vimg == null) {
                vimg = createVolatileImage(width, height);
                renderOffscreen();
            }
            int returnCode = vimg.validate(getGraphicsConfiguration());
            if (returnCode == VolatileImage.IMAGE_RESTORED) {
                renderOffscreen();
            } else if (returnCode == VolatileImage.IMAGE_INCOMPATIBLE) {
                vimg = getGraphicsConfiguration().
                    createCompatibleVolatileImage(width, height);
                renderOffscreen();
            } else if (returnCode == VolatileImage.IMAGE_OK) {
                renderOffscreen();
            }
            g.drawImage(vimg, 0, 0, this);
        } while (vimg.contentsLost());
    }

    private DisplayMode findDisplayMode() {
        GraphicsDevice gd = getGraphicsConfiguration().getDevice();
        DisplayMode[] dms = gd.getDisplayModes();
        DisplayMode currentDM = gd.getDisplayMode();
        for (DisplayMode dm : dms) {
            if (dm.getBitDepth() > 8 &&
                dm.getBitDepth() != DisplayMode.BIT_DEPTH_MULTI &&
                dm.getBitDepth() != currentDM.getBitDepth() &&
                dm.getWidth() == currentDM.getWidth() &&
                dm.getHeight() == currentDM.getHeight()) {
                // found a mode which has the same dimensions but different
                // depth
                return dm;
            }
            if (dm.getBitDepth() == DisplayMode.BIT_DEPTH_MULTI && (
                dm.getWidth() != currentDM.getWidth() || dm.getHeight() != currentDM.getHeight())) {
                // found a mode which has the same depth but different
                // dimensions
                return dm;
            }
        }

        return null;
    }

    enum SpriteType {
        OVALS, VIMAGES, BIMAGES, AAOVALS, TEXT
    }

    @SuppressWarnings("MagicNumber")
    static class Ball {

        final int diameter = 40;
        int x, y;     // current location
        int dx, dy;   // motion delta
        Color color = Color.red;

        public Ball() {
        }

        public Ball(int x, int y) {
            this.x = x;
            this.y = y;
            dx = x % 20 + 1;
            dy = y % 20 + 1;
            color = new Color(rnd.nextInt(0x00ffffff));
        }

        public void move() {
            if (x < 10 || x >= width - 20) {
                dx = -dx;
            }
            if (y < 10 || y > height - 20) {
                dy = -dy;
            }
            x += dx;
            y += dy;
        }

        public void paint(Graphics g, Color c) {
            if (c == null) {
                g.setColor(color);
            } else {
                g.setColor(c);
            }
            g.fillOval(x, y, diameter, diameter);
        }

    }

    static class TextBall extends Ball {
        final String text;
        public TextBall(int x, int y, String text) {
            super(x, y);
            this.text = text;
        }

        @Override
        public void paint(Graphics g, Color c) {
            if (c == null) {
                g.setColor(color);
            } else {
                g.setColor(c);
            }
            g.drawString(text, x, y);
        }
    }

    static class AAOvalBall extends Ball {
        public AAOvalBall(int x, int y) {
            super(x, y);
        }
        @Override
        public void paint(Graphics g, Color c) {
            if (c == null) {
                Graphics2D g2d = (Graphics2D)g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                     RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(color);
                g2d.fillOval(x, y, diameter, diameter);
            } else {
                g.setColor(c);
                g.fillOval(x -2, y -2, diameter +4, diameter +4);
            }
        }
    }

    abstract static class SpriteBall extends Ball {
        Image image;
        public SpriteBall(int x, int y) {
            super(x, y);
            image = createSprite();
            Graphics g = image.getGraphics();
            g.setColor(color);
            g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
        }

        public abstract Image createSprite();

        @Override
        public void paint(Graphics g, Color c) {
            if (c != null) {
                g.setColor(c);
                g.fillRect(x, y, image.getWidth(null), image.getHeight(null));
            } else {
                do {
                    validateSprite();
                    g.drawImage(image, x, y, null);
                } while (renderingIncomplete());
            }
        }

        public void validateSprite() {}

        public boolean renderingIncomplete() { return false; }

    }

    private class MouseHandler extends MouseAdapter {
        MouseHandler() {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            synchronized (balls) {
                balls.addElement(createRandomBall(e.getX(), e.getY()));
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    private class AltTabberThread extends Thread {
        Robot robot;

        AltTabberThread() {
        }

        void pressAltTab() {
            robot.keyPress(KeyEvent.VK_ALT);
            robot.keyPress(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_TAB);
            robot.keyRelease(KeyEvent.VK_ALT);
        }

        void pressShiftAltTab() {
            robot.keyPress(KeyEvent.VK_SHIFT);
            pressAltTab();
            robot.keyRelease(KeyEvent.VK_SHIFT);
        }

        @Override
        public void run() {
            try {
                robot = new Robot();
                robot.setAutoDelay(200);
            } catch (AWTException e) {
                throw new RuntimeException("Can't create robot");
            }
            boolean out = true;
            while (altTabs > 0 && !timeToQuit) {
                altTabs--;
                System.err.println("Alt+tabber Iteration: " + altTabs);
                try {
                    Thread.sleep(2500);
                } catch (InterruptedException ex) {
                }

                if (out) {
                    System.err.println("Issuing alt+tab");
                    pressAltTab();
                } else {
                    System.err.println("Issuing shift ");
                    pressShiftAltTab();
                }
                out = !out;
            }
            altTabs--;
            System.err.println("Alt+tabber finished.");
            synchronized (lock) {
                timeToQuit = true;
                lock.notify();
            }
        }
    }

    private class BallThread extends Thread {
        BallThread() {
        }

        @Override
        public void run() {
            while (!timeToQuit) {
                if (useBS) {
                    renderToBS();
                    bufferStrategy.show();
                } else {
                    Graphics g = getGraphics();
                    render(g);
                    g.dispose();
                }
            }
            gd.setFullScreenWindow(null);
            dispose();
        }
    }

    @SuppressWarnings("MagicNumber")
    class VISpriteBall extends SpriteBall {

        public VISpriteBall(int x, int y) {
            super(x, y);
        }

        @Override
        public Image createSprite() {
            return gd.getDefaultConfiguration().
                createCompatibleVolatileImage(20, 20);
        }

        @Override
        public boolean renderingIncomplete() {
            return ((VolatileImage) image).contentsLost();
        }

        @Override
        public void validateSprite() {
            int result =
                ((VolatileImage) image).validate(getGraphicsConfiguration());
            if (result == VolatileImage.IMAGE_INCOMPATIBLE) {
                image = createSprite();
                result = VolatileImage.IMAGE_RESTORED;
            }
            if (result == VolatileImage.IMAGE_RESTORED) {
                Graphics g = image.getGraphics();
                g.setColor(color);
                g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
            }
        }
    }

    @SuppressWarnings("MagicNumber")
    class BISpriteBall extends SpriteBall {
        public BISpriteBall(int x, int y) {
            super(x, y);
        }
        @Override
        public Image createSprite() {
            return new BufferedImage(20, 20, BufferedImage.TYPE_INT_RGB);
        }
    }
}
