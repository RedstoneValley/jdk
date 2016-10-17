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
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.io.File;
import java.net.URI;
import java.util.stream.Collectors;
import java.util.stream.Stream;


class SourceFileListFrame extends Frame implements DragGestureListener {

    private static final int SOURCE_POINT_SHIFT = 3;
    private static final long serialVersionUID = 7093380057172507366L;

    private final List list = new List(URIListToFileListBetweenJVMsTest.VISIBLE_RAWS_IN_LIST);
    private File[] files;

    SourceFileListFrame() {
        super("Source File List Frame");
        extractFilesFromTheWorkingDirectory();
        initList();
        initGUI();
        new DragSource().createDefaultDragGestureRecognizer(
            list,
                DnDConstants.ACTION_COPY,this);
    }

    private void extractFilesFromTheWorkingDirectory() {
        files = new File(System.getProperty("java.home", "")).listFiles();
    }

    private void initList() {
        for (File currFile: files) {
            list.add(currFile.getName());
        }
    }

    private void initGUI() {
        addWindowListener(Util.getClosingWindowAdapter());
        setLocation(300,250);
        add(new Panel().add(list));
        pack();
        setVisible(true);
    }

    int getNextLocationX() {
        return getX()+ getWidth();
    }

    int getNextLocationY() {
        return getY();
    }

    int getDragSourcePointX() {
        return (int) list.getLocationOnScreen().getX()+ list.getWidth()/2;
    }

   int getDragSourcePointY() {
        return (int) list.getLocationOnScreen().getY()+ SOURCE_POINT_SHIFT;
    }

    int getSourceFilesNumber() {
        return files.length;
    }

    @Override
    public void dragGestureRecognized(DragGestureEvent dge) {
        java.util.List<URI> uriList = Stream.of(list.getItems())
                                                .map(File::new)
                                                .map(File::toURI)
                                                .collect(Collectors.toList());

        dge.startDrag(null, new URIListTransferable(uriList));
    }
}
