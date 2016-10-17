/*
 * Copyright (c) 2014, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.VolatileImage;

import static java.awt.image.BufferedImage.*;

/*
 * @test
 * @bug 8029253 8059941
 * @summary Unmanaged images should be drawn fast.
 * @author Sergey Bylokhov
 */
public final class UnmanagedDrawImagePerformance {

    private static final int[] TYPES = {TYPE_INT_RGB, TYPE_INT_ARGB,
                                        TYPE_INT_ARGB_PRE, TYPE_INT_BGR,
                                        TYPE_3BYTE_BGR, TYPE_4BYTE_ABGR,
                                        TYPE_4BYTE_ABGR_PRE,
                                        TYPE_USHORT_565_RGB,
                                        TYPE_USHORT_555_RGB, TYPE_BYTE_GRAY,
                                        TYPE_USHORT_GRAY, TYPE_BYTE_BINARY,
                                        TYPE_BYTE_INDEXED};
    private static final int[] TRANSPARENCIES = {OPAQUE, BITMASK, TRANSLUCENT};
    private static final int SIZE = 1000;
    private static final AffineTransform[] TRANSFORMS = {
            AffineTransform.getScaleInstance(.5, .5),
            AffineTransform.getScaleInstance(1, 1),
            AffineTransform.getScaleInstance(2, 2),
            AffineTransform.getShearInstance(7, 11)};

    private UnmanagedDrawImagePerformance() {
    }

    public static void main(String[] args) {
        for (AffineTransform atfm : TRANSFORMS) {
            for (int viType : TRANSPARENCIES) {
                for (int biType : TYPES) {
                    BufferedImage bi = makeUnmanagedBI(biType);
                    VolatileImage vi = makeVI(viType);
                    long time = test(bi, vi, atfm) / 1000000000;
                    if (time > 1) {
                        throw new RuntimeException(String.format(
                                "drawImage is slow: %d seconds", time));
                    }
                }
            }
        }
    }

    private static long test(Image bi, Image vi, AffineTransform atfm) {
        Polygon p = new Polygon();
        p.addPoint(0, 0);
        p.addPoint(SIZE, 0);
        p.addPoint(0, SIZE);
        p.addPoint(SIZE, SIZE);
        p.addPoint(0, 0);
        Graphics2D g2d = (Graphics2D) vi.getGraphics();
        g2d.clip(p);
        g2d.transform(atfm);
        g2d.setComposite(AlphaComposite.SrcOver);
        long start = System.nanoTime();
        g2d.drawImage(bi, 0, 0, null);
        long time = System.nanoTime() - start;
        g2d.dispose();
        return time;
    }

    private static VolatileImage makeVI(int type) {
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice gd = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gd.getDefaultConfiguration();
        return gc.createCompatibleVolatileImage(SIZE, SIZE, type);
    }

    private static BufferedImage makeUnmanagedBI(int type) {
        BufferedImage img = new BufferedImage(SIZE, SIZE, type);
        DataBuffer db = img.getRaster().getDataBuffer();
        if (db instanceof DataBufferInt) {
            ((DataBufferInt) db).getData();
        } else if (db instanceof DataBufferShort) {
            ((DataBufferShort) db).getData();
        } else if (db instanceof DataBufferByte) {
            ((DataBufferByte) db).getData();
        } else {
            try {
                img.setAccelerationPriority(0.0f);
            } catch (Throwable ignored) {
            }
        }
        return img;
    }
}
