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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * @test
 * @bug 7190349
 * @summary Verifies that we get correct direction, when draw rotated string.
 * @author Sergey Bylokhov
 * @run main/othervm DrawRotatedString
 */
public final class DrawRotatedString {

    private static final int SIZE = 500;

  private DrawRotatedString() {
  }

  public static void main(String[] args) {
        BufferedImage bi = createBufferedImage(true);
        verify(bi);
        bi = createBufferedImage(false);
        verify(bi);
        System.out.println("Passed");
    }

    private static void verify(BufferedImage bi) {
        for (int i = 0; i < SIZE; ++i) {
            for (int j = 0; j < 99; ++j) {
                //Text should not appear before 100
                if (bi.getRGB(i, j) != Color.RED.getRGB()) {
                    ImageIO.write(bi, "png", new File(MultiResolutionImageTest.IMAGE_NAME_1X));
                    throw new RuntimeException("Failed: wrong text location");
                }
            }
        }
    }

    private static BufferedImage createBufferedImage(boolean  aa) {
        BufferedImage bi = new BufferedImage(SIZE, SIZE,
                                                   BufferedImage.TYPE_INT_RGB);
        Graphics2D bg = bi.createGraphics();
        bg.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            aa ? RenderingHints.VALUE_ANTIALIAS_ON
                               : RenderingHints.VALUE_ANTIALIAS_OFF);
        bg.setColor(Color.RED);
        bg.fillRect(0, 0, SIZE, SIZE);
        bg.translate(100, 100);
        bg.rotate(Math.toRadians(90));
        bg.setColor(Color.BLACK);
        bg.setFont(bg.getFont().deriveFont(20.0f));
        bg.drawString("MMMMMMMMMMMMMMMM", 0, 0);
        bg.dispose();
        return bi;
    }
}
