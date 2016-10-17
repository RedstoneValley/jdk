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


import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("MagicNumber")
public class AddPopupAfterShowTest extends Applet {

    private static Image createTrayIconImage() {
        /*
          Create a small image of a red circle to use as the icon for the tray icon
         */
        int trayIconImageSize = 32;
        BufferedImage trayImage = new BufferedImage(trayIconImageSize, trayIconImageSize, BufferedImage.TYPE_INT_ARGB);
        Graphics2D trayImageGraphics = (Graphics2D) trayImage.getGraphics();

        trayImageGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        trayImageGraphics.setColor(new Color(255, 255, 255, 0));
        trayImageGraphics.fillRect(0, 0, trayImage.getWidth(), trayImage.getHeight());

        trayImageGraphics.setColor(Color.red);

        int trayIconImageInset = 4;
        trayImageGraphics.fillOval(trayIconImageInset,
                trayIconImageInset,
                trayImage.getWidth() - 2 * trayIconImageInset,
                trayImage.getHeight() - 2 * trayIconImageInset);

        trayImageGraphics.setColor(Color.darkGray);

        trayImageGraphics.drawOval(trayIconImageInset,
                trayIconImageInset,
                trayImage.getWidth() - 2 * trayIconImageInset,
                trayImage.getHeight() - 2 * trayIconImageInset);

        return trayImage;
    }

    private static PopupMenu createTrayIconPopupMenu() {
        PopupMenu trayIconPopupMenu = new PopupMenu();
        MenuItem popupMenuItem = new MenuItem("TEST PASSED!");
        trayIconPopupMenu.add(popupMenuItem);
        return trayIconPopupMenu;
    }
}
