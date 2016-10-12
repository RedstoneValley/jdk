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


import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.event.PaintEvent;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;

import sun.awt.CausedFocusEvent;


/**
 * @test
 * @bug 7172833
 * @summary java.awt.Toolkit methods is/setDynamicLayout() should be consistent.
 * @author Sergey Bylokhov
 */
public final class bug7172833 {

    public static void main(final String[] args) throws Exception {
        final StubbedToolkit t = new StubbedToolkit();

        t.setDynamicLayout(true);
        if(!t.isDynamicLayoutSet()){
            throw new RuntimeException("'true' expected but 'false' returned");
        }

        t.setDynamicLayout(false);
        if(t.isDynamicLayoutSet()){
            throw new RuntimeException("'false' expected but 'true' returned");
        }
    }

    static final class StubbedToolkit extends Toolkit {

        @Override
        protected boolean isDynamicLayoutSet() throws HeadlessException {
            return super.isDynamicLayoutSet();
        }

        protected DesktopPeer createDesktopPeer(final Desktop target)
                throws HeadlessException {
            return null;
        }

        protected ButtonPeer createButton(final Button target)
                throws HeadlessException {
            return null;
        }

        protected TextFieldPeer createTextField(final TextField target)
                throws HeadlessException {
            return null;
        }

        protected LabelPeer createLabel(final Label target) throws HeadlessException {
            return null;
        }

        protected ListPeer createList(final List target) throws HeadlessException {
            return null;
        }

        @Override
        protected CheckboxPeer createCheckbox(final Checkbox target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected ScrollbarPeer createScrollbar(final Scrollbar target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected ScrollPanePeer createScrollPane(final ScrollPane target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected TextAreaPeer createTextArea(final TextArea target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected ChoicePeer createChoice(final Choice target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected FramePeer createFrame(final Frame target) throws HeadlessException {
            return null;
        }

        @Override
        protected CanvasPeer createCanvas(final Canvas target) {
            return null;
        }

        @Override
        protected PanelPeer createPanel(final Panel target) {
            return null;
        }

        @Override
        protected WindowPeer createWindow(final Window target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected DialogPeer createDialog(final Dialog target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected MenuBarPeer createMenuBar(final MenuBar target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected MenuPeer createMenu(final Menu target) throws HeadlessException {
            return null;
        }

        @Override
        protected PopupMenuPeer createPopupMenu(final PopupMenu target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected MenuItemPeer createMenuItem(final MenuItem target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected FileDialogPeer createFileDialog(final FileDialog target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected CheckboxMenuItemPeer createCheckboxMenuItem(
                final CheckboxMenuItem target) throws HeadlessException {
            return null;
        }

        @Override
        protected FontPeer getFontPeer(final String name, final int style) {
            return null;
        }

        @Override
        public Dimension getScreenSize() throws HeadlessException {
            return null;
        }

        @Override
        public int getScreenResolution() throws HeadlessException {
            return 0;
        }

        @Override
        public ColorModel getColorModel() throws HeadlessException {
            return null;
        }

        @Override
        public String[] getFontList() {
            return new String[0];
        }

        @Override
        public FontMetrics getFontMetrics(final Font font) {
            return null;
        }

        @Override
        public void sync() {

        }

        @Override
        public Image getImage(final String filename) {
            return null;
        }

        @Override
        public Image getImage(final URL url) {
            return null;
        }

        @Override
        public Image createImage(final String filename) {
            return null;
        }

        @Override
        public Image createImage(final URL url) {
            return null;
        }

        @Override
        public boolean prepareImage(
                final Image image, final int width, final int height,
                                    final ImageObserver observer) {
            return false;
        }

        @Override
        public int checkImage(final Image image, final int width, final int height,
                              final ImageObserver observer) {
            return 0;
        }

        @Override
        public Image createImage(final ImageProducer producer) {
            return null;
        }

        @Override
        public Image createImage(final byte[] imagedata, final int imageoffset,
                                 final int imagelength) {
            return null;
        }

        @Override
        public PrintJob getPrintJob(final Frame frame, final String jobtitle,
                                    final Properties props) {
            return null;
        }

        @Override
        public void beep() {

        }

        @Override
        public Clipboard getSystemClipboard() throws HeadlessException {
            return null;
        }

        @Override
        protected EventQueue getSystemEventQueueImpl() {
            return null;
        }

        @Override
        public DragSourceContextPeer createDragSourceContextPeer(
                final DragGestureEvent dge) throws InvalidDnDOperationException {
            return null;
        }

        @Override
        public boolean isModalityTypeSupported(
                final Dialog.ModalityType modalityType) {
            return false;
        }

        @Override
        public boolean isModalExclusionTypeSupported(
                final Dialog.ModalExclusionType modalExclusionType) {
            return false;
        }

        @Override
        public Map<TextAttribute, ?> mapInputMethodHighlight(
                final InputMethodHighlight highlight) throws HeadlessException {
            return null;
        }

        protected Context getAndroidContext() {
            return SkinJob.getAndroidApplicationContext();
        }

        protected void launchIntent(File file, String action) throws IOException {
            Intent intentToOpen = new Intent(action);
            Uri fileUri = Uri.fromFile(file);
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                String mime = URLConnection.guessContentTypeFromStream();
                if (mime == null) mime = URLConnection.guessContentTypeFromName(file.getName());
                intentToOpen.setDataAndType(fileUri, mime);
                androidContext.startActivity(intentToOpen);
            } finally {
                fileInputStream.close();
            }
        }

        private static class ButtonPeerImpl implements ButtonPeer {
            public ButtonPeerImpl(Button target) {

            }

            @Override
            public void setLabel(String label) {

            }

            @Override
            public boolean isObscured() {
                return false;
            }

            @Override
            public boolean canDetermineObscurity() {
                return false;
            }

            @Override
            public void setVisible(boolean v) {

            }

            @Override
            public void setEnabled(boolean e) {

            }

            @Override
            public void paint(Graphics g) {

            }

            @Override
            public void print(Graphics g) {

            }

            @Override
            public void setBounds(int x, int y, int width, int height, int op) {

            }

            @Override
            public void handleEvent(AWTEvent e) {

            }

            @Override
            public void coalescePaintEvent(PaintEvent e) {

            }

            @Override
            public Point getLocationOnScreen() {
                return null;
            }

            @Override
            public Dimension getPreferredSize() {
                return null;
            }

            @Override
            public Dimension getMinimumSize() {
                return null;
            }

            @Override
            public ColorModel getColorModel() {
                return null;
            }

            @Override
            public Graphics getGraphics() {
                return null;
            }

            @Override
            public FontMetrics getFontMetrics(Font font) {
                return null;
            }

            @Override
            public void dispose() {

            }

            @Override
            public void setForeground(Color c) {

            }

            @Override
            public void setBackground(Color c) {

            }

            @Override
            public void setFont(Font f) {

            }

            @Override
            public void updateCursorImmediately() {

            }

            @Override
            public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
                return false;
            }

            @Override
            public boolean isFocusable() {
                return false;
            }

            @Override
            public Image createImage(ImageProducer producer) {
                return null;
            }

            @Override
            public Image createImage(int width, int height) {
                return null;
            }

            @Override
            public VolatileImage createVolatileImage(int width, int height) {
                return null;
            }

            @Override
            public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
                return false;
            }

            @Override
            public int checkImage(Image img, int w, int h, ImageObserver o) {
                return 0;
            }

            @Override
            public GraphicsConfiguration getGraphicsConfiguration() {
                return null;
            }

            @Override
            public boolean handlesWheelScrolling() {
                return false;
            }

            @Override
            public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

            }

            @Override
            public Image getBackBuffer() {
                return null;
            }

            @Override
            public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {

            }

            @Override
            public void destroyBuffers() {

            }

            @Override
            public void reparent(ContainerPeer newContainer) {

            }

            @Override
            public boolean isReparentSupported() {
                return false;
            }

            @Override
            public void layout() {

            }

            @Override
            public void applyShape(Region shape) {

            }

            @Override
            public void setZOrder(ComponentPeer above) {

            }

            @Override
            public boolean updateGraphicsData(GraphicsConfiguration gc) {
                return false;
            }
        }

        private static class TextFieldPeerImpl implements TextFieldPeer {
            public TextFieldPeerImpl(TextField target) {

            }

            @Override
            public void setEchoChar(char echoChar) {

            }

            @Override
            public Dimension getPreferredSize(int columns) {
                return null;
            }

            @Override
            public Dimension getMinimumSize(int columns) {
                return null;
            }

            @Override
            public void setEditable(boolean editable) {

            }

            @Override
            public String getText() {
                return null;
            }

            @Override
            public void setText(String text) {

            }

            @Override
            public int getSelectionStart() {
                return 0;
            }

            @Override
            public int getSelectionEnd() {
                return 0;
            }

            @Override
            public void select(int selStart, int selEnd) {

            }

            @Override
            public void setCaretPosition(int pos) {

            }

            @Override
            public int getCaretPosition() {
                return 0;
            }

            @Override
            public InputMethodRequests getInputMethodRequests() {
                return null;
            }

            @Override
            public boolean isObscured() {
                return false;
            }

            @Override
            public boolean canDetermineObscurity() {
                return false;
            }

            @Override
            public void setVisible(boolean v) {

            }

            @Override
            public void setEnabled(boolean e) {

            }

            @Override
            public void paint(Graphics g) {

            }

            @Override
            public void print(Graphics g) {

            }

            @Override
            public void setBounds(int x, int y, int width, int height, int op) {

            }

            @Override
            public void handleEvent(AWTEvent e) {

            }

            @Override
            public void coalescePaintEvent(PaintEvent e) {

            }

            @Override
            public Point getLocationOnScreen() {
                return null;
            }

            @Override
            public Dimension getPreferredSize() {
                return null;
            }

            @Override
            public Dimension getMinimumSize() {
                return null;
            }

            @Override
            public ColorModel getColorModel() {
                return null;
            }

            @Override
            public Graphics getGraphics() {
                return null;
            }

            @Override
            public FontMetrics getFontMetrics(Font font) {
                return null;
            }

            @Override
            public void dispose() {

            }

            @Override
            public void setForeground(Color c) {

            }

            @Override
            public void setBackground(Color c) {

            }

            @Override
            public void setFont(Font f) {

            }

            @Override
            public void updateCursorImmediately() {

            }

            @Override
            public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
                return false;
            }

            @Override
            public boolean isFocusable() {
                return false;
            }

            @Override
            public Image createImage(ImageProducer producer) {
                return null;
            }

            @Override
            public Image createImage(int width, int height) {
                return null;
            }

            @Override
            public VolatileImage createVolatileImage(int width, int height) {
                return null;
            }

            @Override
            public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
                return false;
            }

            @Override
            public int checkImage(Image img, int w, int h, ImageObserver o) {
                return 0;
            }

            @Override
            public GraphicsConfiguration getGraphicsConfiguration() {
                return null;
            }

            @Override
            public boolean handlesWheelScrolling() {
                return false;
            }

            @Override
            public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

            }

            @Override
            public Image getBackBuffer() {
                return null;
            }

            @Override
            public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {

            }

            @Override
            public void destroyBuffers() {

            }

            @Override
            public void reparent(ContainerPeer newContainer) {

            }

            @Override
            public boolean isReparentSupported() {
                return false;
            }

            @Override
            public void layout() {

            }

            @Override
            public void applyShape(Region shape) {

            }

            @Override
            public void setZOrder(ComponentPeer above) {

            }

            @Override
            public boolean updateGraphicsData(GraphicsConfiguration gc) {
                return false;
            }
        }

        private static class LabelPeerImpl implements LabelPeer {
            @Override
            public void setText(String label) {

            }

            @Override
            public void setAlignment(int alignment) {

            }

            @Override
            public boolean isObscured() {
                return false;
            }

            @Override
            public boolean canDetermineObscurity() {
                return false;
            }

            @Override
            public void setVisible(boolean v) {

            }

            @Override
            public void setEnabled(boolean e) {

            }

            @Override
            public void paint(Graphics g) {

            }

            @Override
            public void print(Graphics g) {

            }

            @Override
            public void setBounds(int x, int y, int width, int height, int op) {

            }

            @Override
            public void handleEvent(AWTEvent e) {

            }

            @Override
            public void coalescePaintEvent(PaintEvent e) {

            }

            @Override
            public Point getLocationOnScreen() {
                return null;
            }

            @Override
            public Dimension getPreferredSize() {
                return null;
            }

            @Override
            public Dimension getMinimumSize() {
                return null;
            }

            @Override
            public ColorModel getColorModel() {
                return null;
            }

            @Override
            public Graphics getGraphics() {
                return null;
            }

            @Override
            public FontMetrics getFontMetrics(Font font) {
                return null;
            }

            @Override
            public void dispose() {

            }

            @Override
            public void setForeground(Color c) {

            }

            @Override
            public void setBackground(Color c) {

            }

            @Override
            public void setFont(Font f) {

            }

            @Override
            public void updateCursorImmediately() {

            }

            @Override
            public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
                return false;
            }

            @Override
            public boolean isFocusable() {
                return false;
            }

            @Override
            public Image createImage(ImageProducer producer) {
                return null;
            }

            @Override
            public Image createImage(int width, int height) {
                return null;
            }

            @Override
            public VolatileImage createVolatileImage(int width, int height) {
                return null;
            }

            @Override
            public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
                return false;
            }

            @Override
            public int checkImage(Image img, int w, int h, ImageObserver o) {
                return 0;
            }

            @Override
            public GraphicsConfiguration getGraphicsConfiguration() {
                return null;
            }

            @Override
            public boolean handlesWheelScrolling() {
                return false;
            }

            @Override
            public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

            }

            @Override
            public Image getBackBuffer() {
                return null;
            }

            @Override
            public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {

            }

            @Override
            public void destroyBuffers() {

            }

            @Override
            public void reparent(ContainerPeer newContainer) {

            }

            @Override
            public boolean isReparentSupported() {
                return false;
            }

            @Override
            public void layout() {

            }

            @Override
            public void applyShape(Region shape) {

            }

            @Override
            public void setZOrder(ComponentPeer above) {

            }

            @Override
            public boolean updateGraphicsData(GraphicsConfiguration gc) {
                return false;
            }
        }

        private static class ListPeerImpl implements ListPeer {
            public ListPeerImpl(List target) {

            }

            @Override
            public int[] getSelectedIndexes() {
                return new int[0];
            }

            @Override
            public void add(String item, int index) {

            }

            @Override
            public void delItems(int start, int end) {

            }

            @Override
            public void removeAll() {

            }

            @Override
            public void select(int index) {

            }

            @Override
            public void deselect(int index) {

            }

            @Override
            public void makeVisible(int index) {

            }

            @Override
            public void setMultipleMode(boolean m) {

            }

            @Override
            public Dimension getPreferredSize(int rows) {
                return null;
            }

            @Override
            public Dimension getMinimumSize(int rows) {
                return null;
            }

            @Override
            public boolean isObscured() {
                return false;
            }

            @Override
            public boolean canDetermineObscurity() {
                return false;
            }

            @Override
            public void setVisible(boolean v) {

            }

            @Override
            public void setEnabled(boolean e) {

            }

            @Override
            public void paint(Graphics g) {

            }

            @Override
            public void print(Graphics g) {

            }

            @Override
            public void setBounds(int x, int y, int width, int height, int op) {

            }

            @Override
            public void handleEvent(AWTEvent e) {

            }

            @Override
            public void coalescePaintEvent(PaintEvent e) {

            }

            @Override
            public Point getLocationOnScreen() {
                return null;
            }

            @Override
            public Dimension getPreferredSize() {
                return null;
            }

            @Override
            public Dimension getMinimumSize() {
                return null;
            }

            @Override
            public ColorModel getColorModel() {
                return null;
            }

            @Override
            public Graphics getGraphics() {
                return null;
            }

            @Override
            public FontMetrics getFontMetrics(Font font) {
                return null;
            }

            @Override
            public void dispose() {

            }

            @Override
            public void setForeground(Color c) {

            }

            @Override
            public void setBackground(Color c) {

            }

            @Override
            public void setFont(Font f) {

            }

            @Override
            public void updateCursorImmediately() {

            }

            @Override
            public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, CausedFocusEvent.Cause cause) {
                return false;
            }

            @Override
            public boolean isFocusable() {
                return false;
            }

            @Override
            public Image createImage(ImageProducer producer) {
                return null;
            }

            @Override
            public Image createImage(int width, int height) {
                return null;
            }

            @Override
            public VolatileImage createVolatileImage(int width, int height) {
                return null;
            }

            @Override
            public boolean prepareImage(Image img, int w, int h, ImageObserver o) {
                return false;
            }

            @Override
            public int checkImage(Image img, int w, int h, ImageObserver o) {
                return 0;
            }

            @Override
            public GraphicsConfiguration getGraphicsConfiguration() {
                return null;
            }

            @Override
            public boolean handlesWheelScrolling() {
                return false;
            }

            @Override
            public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException {

            }

            @Override
            public Image getBackBuffer() {
                return null;
            }

            @Override
            public void flip(int x1, int y1, int x2, int y2, BufferCapabilities.FlipContents flipAction) {

            }

            @Override
            public void destroyBuffers() {

            }

            @Override
            public void reparent(ContainerPeer newContainer) {

            }

            @Override
            public boolean isReparentSupported() {
                return false;
            }

            @Override
            public void layout() {

            }

            @Override
            public void applyShape(Region shape) {

            }

            @Override
            public void setZOrder(ComponentPeer above) {

            }

            @Override
            public boolean updateGraphicsData(GraphicsConfiguration gc) {
                return false;
            }
        }

        private class DesktopPeerImpl implements DesktopPeer {

            @Override
            public boolean isSupported(Desktop.Action action) {
                return action != Desktop.Action.PRINT;
            }

            @Override
            public void open(File file) throws IOException {
                launchIntent(file, Intent.ACTION_VIEW);
            }

            @Override
            public void edit(File file) throws IOException {
                launchIntent(file, Intent.ACTION_EDIT);
            }

            @Override
            public void print(File file) throws IOException {
                throw new
                    UnsupportedOperationException("TODO: Uncomment once support.v4 JAR is installed");
                /**
                if (ContextCompat.checkSelfPermission(androidContext,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(androidContext,
                            Manifest.permission.READ_CONTACTS)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(thisActivity,
                                new String[]{Manifest.permission.READ_CONTACTS},
                                MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                }
                launchIntent(file, Intent.ACTION_PR);
                 */
            }

            @Override
            public void mail(URI mailtoURL) throws IOException {
                Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
                mailIntent.setData(Uri.parse(mailtoURL.toString()));
                androidContext.startActivity(mailIntent);
            }

            @Override
            public void browse(URI uri) throws IOException {
                Intent browseIntent = new Intent(Intent.ACTION_VIEW);
                browseIntent.setData(Uri.parse(uri.toString()));
                androidContext.startActivity(browseIntent);
            }
        }
    }
}
