package java.awt;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.awt.datatransfer.Clipboard;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;
import java.awt.font.TextAttribute;
import java.awt.im.InputMethodHighlight;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.peer.ButtonPeer;
import java.awt.peer.CanvasPeer;
import java.awt.peer.CheckboxMenuItemPeer;
import java.awt.peer.CheckboxPeer;
import java.awt.peer.ChoicePeer;
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
import java.net.URL;
import java.util.Map;
import java.util.Properties;

/**
 * The Android implementation of {@link Toolkit}.
 */
public class SkinJobToolkit extends Toolkit {

    /** Singleton unless subclassed. */
    protected SkinJobToolkit() {
        androidContext = getAndroidContext();
    }

    protected final Context androidContext;
    protected final EventQueue eventQueue = new EventQueue();
    protected Clipboard clipboard;

    @Override
    protected CheckboxPeer createCheckbox(Checkbox target) throws HeadlessException {
        return new SkinJobCheckboxPeer(target);
    }

    @Override
    protected ScrollbarPeer createScrollbar(Scrollbar target) throws HeadlessException {
        return new SkinJobScrollbarPeer(target);
    }

    @Override
    protected ScrollPanePeer createScrollPane(ScrollPane target) throws HeadlessException {
        return new SkinJobScrollPanePeer(target);
    }

    @Override
    protected TextAreaPeer createTextArea(TextArea target) throws HeadlessException {
        return null;
    }

    @Override
    protected ChoicePeer createChoice(Choice target) throws HeadlessException {
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
    protected WindowPeer createWindow(Window target) throws HeadlessException {
        return null;
    }

    @Override
    protected DialogPeer createDialog(Dialog target) throws HeadlessException {
        return null;
    }

    @Override
    protected MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException {
        return null;
    }

    @Override
    protected MenuPeer createMenu(Menu target) throws HeadlessException {
        return null;
    }

    @Override
    protected PopupMenuPeer createPopupMenu(PopupMenu target) throws HeadlessException {
        return null;
    }

    @Override
    protected MenuItemPeer createMenuItem(MenuItem target) throws HeadlessException {
        return null;
    }

    @Override
    protected FileDialogPeer createFileDialog(FileDialog target) throws HeadlessException {
        return null;
    }

    @Override
    protected CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target) throws HeadlessException {
        return null;
    }

    @Override
    protected FontPeer getFontPeer(String name, int style) {
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
    public boolean prepareImage(Image image, int width, int height, ImageObserver observer) {
        return false;
    }

    @Override
    public int checkImage(Image image, int width, int height, ImageObserver observer) {
        return 0;
    }

    @Override
    public Image createImage(ImageProducer producer) {
        return null;
    }

    @Override
    public Image createImage(byte[] imagedata, int imageoffset, int imagelength) {
        return null;
    }

    @Override
    public PrintJob getPrintJob(Frame frame, String jobtitle, Properties props) {
        return null;
    }

    @Override
    public void beep() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone r = RingtoneManager.getRingtone(androidContext, notification);
        r.play();
    }

    @Override
    public Clipboard getSystemClipboard() throws HeadlessException {
        if (clipboard == null) {
            clipboard = new SkinJobClipboard(androidContext);
        }
        return clipboard;
    }

    @Override
    protected EventQueue getSystemEventQueueImpl() {
        return eventQueue;
    }

    @Override
    public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dge) throws InvalidDnDOperationException {
        return new SkinJobDragSourceContextPeer(dge);
    }

    @Override
    public boolean isModalityTypeSupported(Dialog.ModalityType modalityType) {
        return false;
    }

    @Override
    public boolean isModalExclusionTypeSupported(Dialog.ModalExclusionType modalExclusionType) {
        return false;
    }

    @Override
    public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight) throws HeadlessException {
        return null;
    }

    protected Context getAndroidContext() {
        return SkinJobUtil.getAndroidApplicationContext();
    }

    @Override
    protected DesktopPeer createDesktopPeer(Desktop target)
            throws HeadlessException {
        return new SkinJobDesktopPeer(androidContext);
    }

    @Override
    protected ButtonPeer createButton(Button target)
            throws HeadlessException {
        return new SkinJobButtonPeer(target);
    }

    @Override
    protected TextFieldPeer createTextField(TextField target)
            throws HeadlessException {
        return new SkinJobTextFieldPeer(target);
    }

    @Override
    protected LabelPeer createLabel(Label target)
            throws HeadlessException {
        return new SkinJobLabelPeer(target);
    }

    @Override
    protected ListPeer createList(List target)
            throws HeadlessException {
        return new SkinJobListPeer(target);
    }

}
