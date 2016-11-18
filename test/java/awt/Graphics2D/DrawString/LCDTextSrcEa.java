/*
 * Copyright (c) 2010, Oracle and/or its affiliates. All rights reserved.
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
 * @bug 6996867
 * @summary Render as LCD Text in SrcEa composite mode.
 */

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class LCDTextSrcEa extends Component {

    static final int SZ=150;
    private static final long serialVersionUID = 202912689081814962L;
    final BufferedImage target =
        new BufferedImage(SZ, SZ, BufferedImage.TYPE_INT_RGB);

    protected LCDTextSrcEa() {}

    public static void main(String[] args) {
        Frame f = new Frame("LCD Text SrcEa Test");
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        LCDTextSrcEa td = new LCDTextSrcEa();
        f.add(BorderLayout.CENTER, td);
        f.pack();
        f.setVisible(true);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(SZ,SZ);
    }

    @Override
    public void paint(Graphics gx) {

        Graphics2D g2d = (Graphics2D) target.getGraphics();
        g2d.setColor(Color.white);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 0.01f));
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_VBGR);
        g2d.setRenderingHint(
               RenderingHints.KEY_ANTIALIASING,
               RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.black);
        g2d.drawString("Some sample text.", 10, 20);
        gx.drawImage(target, 0, 0, null);
        boolean nongrey = false;
        //Test BI: should be some non-greyscale color
        for (int px=0;px<SZ;px++) {
            for (int py=0;py<SZ;py++) {
                int rgb = target.getRGB(px, py);
                int r = (rgb & 0xff0000) >> 16;
                int g = (rgb & 0x00ff00) >> 8;
                int b = rgb & 0x0000ff;
                if (r != g || r != b) {
                     nongrey=true;
                     break;
                }
            }
        }
        if (!nongrey) {
            throw new RuntimeException("No LCD text found");
        }
    }
}
