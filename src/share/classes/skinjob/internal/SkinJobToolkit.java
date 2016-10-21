package skinjob.internal;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Checkbox;
import java.awt.CheckboxMenuItem;
import java.awt.Choice;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dialog.ModalExclusionType;
import java.awt.Dialog.ModalityType;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Label;
import java.awt.List;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Panel;
import java.awt.PopupMenu;
import java.awt.PrintJob;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
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
import java.lang.reflect.Field;
import java.net.URL;
import java.util.Map;
import java.util.Properties;
import skinjob.SkinJobGlobals;
import skinjob.internal.peer.SkinJobButtonPeer;
import skinjob.internal.peer.SkinJobCanvasPeer;
import skinjob.internal.peer.SkinJobCheckboxPeer;
import skinjob.internal.peer.SkinJobChoicePeer;
import skinjob.internal.peer.SkinJobDesktopPeer;
import skinjob.internal.peer.SkinJobDragSourceContextPeer;
import skinjob.internal.peer.SkinJobFileDialogPeer;
import skinjob.internal.peer.SkinJobFontPeer;
import skinjob.internal.peer.SkinJobLabelPeer;
import skinjob.internal.peer.SkinJobListPeer;
import skinjob.internal.peer.SkinJobMenuBarPeer;
import skinjob.internal.peer.SkinJobMenuItemPeer;
import skinjob.internal.peer.SkinJobMenuPeer;
import skinjob.internal.peer.SkinJobPanelPeer;
import skinjob.internal.peer.SkinJobPopupMenuPeer;
import skinjob.internal.peer.SkinJobScrollPanePeer;
import skinjob.internal.peer.SkinJobScrollbarPeer;
import skinjob.internal.peer.SkinJobTextFieldPeer;
import skinjob.internal.peer.SkinJobWindowPeer;

/**
 * The Android implementation of {@link Toolkit}.
 */
public class SkinJobToolkit extends Toolkit {

  protected final Context androidContext;
  protected final EventQueue eventQueue = new EventQueue();
  protected Clipboard clipboard;

  /**
   * Singleton unless subclassed.
   */
  public SkinJobToolkit() {
    androidContext = getAndroidContext();
  }

  protected Context getAndroidContext() {
    return SkinJobGlobals.getAndroidApplicationContext();
  }

  @Override
  protected DesktopPeer createDesktopPeer(Desktop target) throws HeadlessException {
    return new SkinJobDesktopPeer(androidContext);
  }

  @Override
  protected ButtonPeer createButton(Button target) throws HeadlessException {
    return new SkinJobButtonPeer(target);
  }

  @Override
  protected TextFieldPeer createTextField(TextField target) throws HeadlessException {
    return new SkinJobTextFieldPeer(target);
  }

  @Override
  protected LabelPeer createLabel(Label target) throws HeadlessException {
    return new SkinJobLabelPeer(target);
  }

  @Override
  protected ListPeer createList(List target) throws HeadlessException {
    return new SkinJobListPeer(target);
  }

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
    return new SkinJobTextFieldPeer(target);
  }

  @Override
  protected ChoicePeer createChoice(Choice target) throws HeadlessException {
    return new SkinJobChoicePeer(target);
  }

  @Override
  protected FramePeer createFrame(Frame target) throws HeadlessException {
    return new SkinJobWindowPeer<android.view.Window>(target);
  }

  @Override
  protected CanvasPeer createCanvas(Canvas target) {
    return new SkinJobCanvasPeer(target);
  }

  @Override
  protected PanelPeer createPanel(Panel target) {
    return new SkinJobPanelPeer(target);
  }

  @Override
  protected WindowPeer createWindow(Window target) throws HeadlessException {
    return new SkinJobWindowPeer<android.view.Window>(target);
  }

  @Override
  protected DialogPeer createDialog(Dialog target) throws HeadlessException {
    return new SkinJobWindowPeer<android.view.Window>(target);
  }

  @Override
  protected MenuBarPeer createMenuBar(MenuBar target) throws HeadlessException {
    return new SkinJobMenuBarPeer(target);
  }

  @Override
  protected MenuPeer createMenu(Menu target) throws HeadlessException {
    return new SkinJobMenuPeer(target);
  }

  @Override
  protected PopupMenuPeer createPopupMenu(PopupMenu target) throws HeadlessException {
    return new SkinJobPopupMenuPeer(target);
  }

  @Override
  protected MenuItemPeer createMenuItem(MenuItem target) throws HeadlessException {
    return new SkinJobMenuItemPeer(target);
  }

  @Override
  protected FileDialogPeer createFileDialog(FileDialog target) throws HeadlessException {
    return new SkinJobFileDialogPeer(target);
  }

  @Override
  protected CheckboxMenuItemPeer createCheckboxMenuItem(CheckboxMenuItem target)
      throws HeadlessException {
    return new SkinJobMenuItemPeer(target);
  }

  @Override
  protected FontPeer getFontPeer(String name, int style) {
    return new SkinJobFontPeer();
  }

  @Override
  public Dimension getScreenSize() throws HeadlessException {
    return SkinJobGraphicsConfiguration.getDefault().getBounds().getSize();
  }

  @Override
  public int getScreenResolution() throws HeadlessException {
    return (int) SkinJobGraphicsConfiguration.getDefault().dpi;
  }

  @Override
  public ColorModel getColorModel() throws HeadlessException {
    return ColorModel.getRGBdefault();
  }

  @Override
  @SuppressWarnings("unchecked")
  public String[] getFontList() {
    Class<Typeface> typefaceClass = Typeface.class;
    try {
      Field systemFontMapField = typefaceClass.getField("sSystemFontMap");
      systemFontMapField.setAccessible(true);
      return ((Map<String, ?>) systemFontMapField.get(null)).keySet().toArray(new String[0]);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public FontMetrics getFontMetrics(Font font) {
    return new SkinJobFontMetrics(font);
  }

  @Override
  public void sync() {
    // No-op
  }

  @Override
  public Image getImage(String filename) {
    return new SkinJobImage(filename);
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
  public DragSourceContextPeer createDragSourceContextPeer(DragGestureEvent dge)
      throws InvalidDnDOperationException {
    return new SkinJobDragSourceContextPeer(dge);
  }

  @Override
  public boolean isModalityTypeSupported(ModalityType modalityType) {
    return false;
  }

  @Override
  public boolean isModalExclusionTypeSupported(ModalExclusionType modalExclusionType) {
    return false;
  }

  @Override
  public Map<TextAttribute, ?> mapInputMethodHighlight(InputMethodHighlight highlight)
      throws HeadlessException {
    return null;
  }
}
