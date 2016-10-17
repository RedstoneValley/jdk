/*
 * Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsDevice.WindowTranslucency;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Canvas;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.util.Random;
import java.awt.geom.Ellipse2D;

public final class TSFrame {

    static volatile boolean done;

    static final boolean useSwing = System.getProperty("useswing") != null;
    static final boolean useShape = System.getProperty("useshape") != null;
    static final boolean useTransl = System.getProperty("usetransl") != null;
    static final boolean useNonOpaque = System.getProperty("usenonop") != null;

    static final Random rnd = new Random();

    private TSFrame() {
    }

    static void render(Graphics g, int w, int h, boolean useNonOpaque) {
        if (useNonOpaque) {
            Graphics2D g2d = (Graphics2D)g;
            GradientPaint p =
                new GradientPaint(0.0f, 0.0f,
                                  new Color(rnd.nextInt(0xffffff)),
                                  w, h,
                                  new Color(rnd.nextInt(0xff),
                                            rnd.nextInt(0xff),
                                            rnd.nextInt(0xff), 0),
                                  true);
            g2d.setPaint(p);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                 RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.fillOval(0, 0, w, h);
        } else {
            g.setColor(new Color(rnd.nextInt(0xffffff)));
            g.fillRect(0, 0, w, h);
        }
    }

    private static class MyCanvas extends Canvas {
        private static final long serialVersionUID = -1495754634946682764L;

        MyCanvas() {
        }

        @Override
        public void paint(Graphics g) {
            render(g, getWidth(), getHeight(), false);
        }
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(200, 100);
        }
    }
    private static class NonOpaqueJFrame extends JFrame {
        NonOpaqueJFrame() {
            super("NonOpaque Swing JFrame");
            JPanel p = new JPanel() {
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    render(g, getWidth(), getHeight(), true);
                    g.setColor(Color.red);
                    g.drawString("Non-Opaque Swing JFrame", 10, 15);
                }
            };
            p.setDoubleBuffered(false);
            p.setOpaque(false);
            add(p);
            setUndecorated(true);
        }
    }
    private static class NonOpaqueJAppletFrame extends JFrame {
        final JPanel p;
        NonOpaqueJAppletFrame() {
            super("NonOpaque Swing JAppletFrame");
            JApplet ja = new JApplet() {
                public void paint(Graphics g) {
                    super.paint(g);
                    System.err.println("JAppletFrame paint called");
                }
            };
            p = new JPanel() {
                public void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    render(g, getWidth(), getHeight(), true);
                    g.setColor(Color.red);
                    g.drawString("Non-Opaque Swing JFrame", 10, 15);
                }
            };
            p.setDoubleBuffered(false);
            p.setOpaque(false);
            ja.add(p);
            add(ja);
            setUndecorated(true);
        }
    }
    private static class NonOpaqueFrame extends Frame {
        private static final long serialVersionUID = 5479381857444285839L;

        NonOpaqueFrame() {
            super("NonOpaque AWT Frame");
            // uncomment to test with hw child
//            setLayout(null);
//            Component c = new Panel() {
//                public void paint(Graphics g) {
//                    g.setColor(new Color(1.0f, 1.0f, 1.0f, 0.5f));
//                    g.fillRect(0, 0, getWidth(), getHeight());
//                }
//            };
//            c.setSize(100, 100);
//            c.setBackground(Color.red);
//            c.setForeground(Color.red);
//            add(c);
//            c.setLocation(130, 130);
        }
        @Override
        public void paint(Graphics g) {
            render(g, getWidth(), getHeight(), true);
            g.setColor(Color.red);
            g.drawString("Non-Opaque AWT Frame", 10, 15);
        }
    }

    private static class MyJPanel extends JPanel {
        MyJPanel() {
        }

        @Override
        public void paintComponent(Graphics g) {
            render(g, getWidth(), getHeight(), false);
        }
    }

    public static Frame createGui(
                                  boolean useSwing,
                                  boolean useShape,
                                  boolean useTransl,
                                  boolean useNonOpaque,
                                  float factor)
    {
        Frame frame;
        done = false;

        if (useNonOpaque) {
            frame = useSwing ? new NonOpaqueJFrame() : new NonOpaqueFrame();
            animateComponent(frame);
        } else if (useSwing) {
            frame = new JFrame("Swing Frame");
            JComponent p = new JButton("Swing!");
            p.setPreferredSize(new Dimension(200, 100));
            frame.add(BorderLayout.NORTH, p);
            p = new MyJPanel();
            animateComponent(p);
            frame.add(BorderLayout.CENTER, p);
        } else {
            frame = new Frame("AWT Frame") {
                private static final long serialVersionUID = 6044871841701872050L;

                @Override
                public void paint(Graphics g) {
                    g.setColor(Color.red);
                    g.fillRect(0, 0, 100, 100);
                }
            };
            frame.setLayout(new BorderLayout());
            Canvas c = new MyCanvas();
            frame.add(BorderLayout.NORTH, c);
            animateComponent(c);
            c = new MyCanvas();
            frame.add(BorderLayout.CENTER, c);
            animateComponent(c);
            c = new MyCanvas();
            frame.add(BorderLayout.SOUTH, c);
            animateComponent(c);
        }
        Frame finalFrame = frame;
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                finalFrame.dispose();
                done = true;
            }
        });
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                finalFrame.dispose();
                done = true;
            }
        });
        frame.setPreferredSize(new Dimension(800, 600));

        if (useShape) {
            frame.setUndecorated(true);
        }

        frame.setLocation(450, 10);
        frame.pack();

        GraphicsDevice gd = frame.getGraphicsConfiguration().getDevice();
        if (useShape) {
            if (gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSPARENT)) {
                System.out.println("applying PERPIXEL_TRANSPARENT");
                frame.setShape(new Ellipse2D.Double(0, 0, frame.getWidth(),
                                                    frame.getHeight()/3));
                frame.setTitle("PERPIXEL_TRANSPARENT");
            } else {
                System.out.println("Passed: PERPIXEL_TRANSPARENT unsupported");
            }
        }
        if (useTransl) {
            if (gd.isWindowTranslucencySupported(WindowTranslucency.TRANSLUCENT)) {
                System.out.println("applying TRANSLUCENT");
                frame.setOpacity(factor);
                frame.setTitle("TRANSLUCENT");
            } else {
                System.out.println("Passed: TRANSLUCENT unsupported");
            }
        }
        if (useNonOpaque) {
            if (gd.isWindowTranslucencySupported(WindowTranslucency.PERPIXEL_TRANSLUCENT)) {
                System.out.println("applying PERPIXEL_TRANSLUCENT");
                frame.setBackground(new Color(0, 0, 0, 0));
                frame.setTitle("PERPIXEL_TRANSLUCENT");
            } else {
                System.out.println("Passed: PERPIXEL_TRANSLUCENT unsupported");
            }
        }
        frame.setVisible(true);
        return frame;
    }

    private static void animateComponent(Component comp) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                do {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {}
                    comp.repaint();
                } while (!done);
            }
        });
        t.start();
    }

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createGui(useSwing,
                                  useShape,
                                  useTransl,
                                  useNonOpaque,
                                  0.7f);
            }
        });
    }
}
