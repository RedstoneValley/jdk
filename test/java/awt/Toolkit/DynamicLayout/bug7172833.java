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
import java.awt.AWTEvent;
import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.Shape;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
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
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.DesktopPeer;
import java.awt.peer.DialogPeer;
import java.awt.peer.FileDialogPeer;
import java.awt.peer.FontPeer;
import java.awt.peer.FramePeer;
import java.awt.peer.LabelPeer;
import java.awt.peer.ListPeer;
import java.awt.peer.MenuBarPeer;
import java.awt.peer.MenuItemPeer;
import java.awt.peer.MenuPeer;
import java.awt.peer.PanelPeer;
import java.awt.peer.PopupMenuPeer;
import java.awt.peer.ScrollPanePeer;
import java.awt.peer.ScrollbarPeer;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;
import java.awt.peer.WindowPeer;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Properties;
import skinjob.SkinJobGlobals;
import sun.awt.CausedFocusEvent.Cause;

/**
 * @test
 * @bug 7172833
 * @summary java.awt.Toolkit methods is/setDynamicLayout() should be consistent.
 * @author Sergey Bylokhov
 */
public final class bug7172833 {

    public static void main(String[] args) throws Exception {
        StubbedToolkit t = new StubbedToolkit();

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
        protected DesktopPeer createDesktopPeer(Desktop target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected ButtonPeer createButton(Button target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected TextFieldPeer createTextField(TextField target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected LabelPeer createLabel(Label target) throws HeadlessException {
            return null;
        }

        @Override
        protected ListPeer createList(List target) throws HeadlessException {
            return null;
        }

        @Override
        protected CheckboxPeer createCheckbox(Checkbox target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected ScrollbarPeer createScrollbar(Scrollbar target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected ScrollPanePeer createScrollPane(ScrollPane target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected TextAreaPeer createTextArea(TextArea target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected ChoicePeer createChoice(Choice target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected FramePeer createFrame(Frame target) throws HeadlessException {
            return null;
        }

        @Override
        protected CanvasPeer createCanvas(Canvas target) {
            return null;
        }

        @Override
        protected PanelPeer createPanel(Panel target) {
            return null;
        }

        @Override
        protected WindowPeer createWindow(Window target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected DialogPeer createDialog(Dialog target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected MenuBarPeer createMenuBar(MenuBar target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected MenuPeer createMenu(Menu target) throws HeadlessException {
            return null;
        }

        @Override
        protected PopupMenuPeer createPopupMenu(PopupMenu target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected MenuItemPeer createMenuItem(MenuItem target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected FileDialogPeer createFileDialog(FileDialog target)
                throws HeadlessException {
            return null;
        }

        @Override
        protected CheckboxMenuItemPeer createCheckboxMenuItem(
                CheckboxMenuItem target) throws HeadlessException {
            return null;
        }

        @Override
        protected FontPeer getFontPeer(String name, int style) {
            return null;
        }

        @Override
        protected boolean isDynamicLayoutSet() throws HeadlessException {
            return super.isDynamicLayoutSet();
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
        public FontMetrics getFontMetrics(Font font) {
            return null;
        }

        @Override
        public void sync() {

        }

        @Override
        public Image getImage(String filename) {
            return null;
        }

        @Override
        public Image getImage(URL url) {
            return null;
        }

        @Override
        public Image createImage(String filename) {
            return null;
        }

        @Override
        public Image createImage(URL url) {
            return null;
        }

        @Override
        public boolean prepareImage(
                Image image, int width, int height,
                                    ImageObserver observer) {
            return false;
        }

        @Override
        public int checkImage(
            Image image, int width, int height,
                              ImageObserver observer) {
            return 0;
        }

        @Override
        public Image createImage(ImageProducer producer) {
            return null;
        }

        @Override
        public Image createImage(
            byte[] imagedata, int imageoffset,
                                 int imagelength) {
            return null;
        }

        @Override
        public PrintJob getPrintJob(
            Frame frame, String jobtitle,
                                    Properties props) {
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
                DragGestureEvent dge) throws InvalidDnDOperationException {
            return null;
        }

        @Override
        public boolean isModalityTypeSupported(
                ModalityType modalityType) {
            return false;
        }

        @Override
        public boolean isModalExclusionTypeSupported(
                ModalExclusionType modalExclusionType) {
            return false;
        }

        @Override
        public Map<TextAttribute, ?> mapInputMethodHighlight(
                InputMethodHighlight highlight) throws HeadlessException {
            return null;
        }

        protected Context getAndroidContext() {
            return SkinJobGlobals.getAndroidApplicationContext();
        }

        protected void launchIntent(File file, String action) throws IOException {
            Intent intentToOpen = new Intent(action);
            Uri fileUri = Uri.fromFile(file);
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                String mime = URLConnection.guessContentTypeFromStream();
                if (mime == null) {
                    mime = URLConnection.guessContentTypeFromName(file.getName());
                }
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
            public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, Cause cause) {
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
            public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {

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
            public void applyShape(Shape shape) {

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
            public int getCaretPosition() {
                return 0;
            }

            @Override
            public void setCaretPosition(int pos) {

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
            public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, Cause cause) {
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
            public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {

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
            public void applyShape(Shape shape) {

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
            public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, Cause cause) {
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
            public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {

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
            public void applyShape(Shape shape) {

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
            public boolean requestFocus(Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time, Cause cause) {
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
            public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction) {

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
            public void applyShape(Shape shape) {

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
            public boolean isSupported(Action action) {
                return action != Action.PRINT;
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

                /*
                if (ContextCompat.checkSelfPermission(sjAndroidContext,
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(sjAndroidContext,
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
