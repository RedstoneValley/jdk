package java.awt;

import android.widget.EditText;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;

/**
 * Created by cryoc on 2016-10-09.
 */
class SkinJobTextFieldPeer extends SkinJobComponentPeerForView<EditText>
    implements TextFieldPeer, TextAreaPeer {
  public SkinJobTextFieldPeer(TextField target) {
    super((EditText) target.androidWidget);
  }

  public SkinJobTextFieldPeer(TextArea target) {
    super((EditText) target.androidWidget);
  }

  @Override
  public void setEchoChar(char echoChar) {
    // TODO: This means put the EditText in password mode
  }

  @Override
  public Dimension getPreferredSize(int columns) {
    // TODO
    return null;
  }

  @Override
  public Dimension getMinimumSize(int columns) {
    // TODO
    return null;
  }

  @Override
  public void setEditable(boolean editable) {
    androidWidget.setEnabled(editable);
  }

  @Override
  public String getText() {
    return androidWidget.getText().toString();
  }

  @Override
  public void setText(String text) {
    androidWidget.setText(text);
  }

  @Override
  public int getSelectionStart() {
    return androidWidget.getSelectionStart();
  }

  @Override
  public int getSelectionEnd() {
    return androidWidget.getSelectionEnd();
  }

  @Override
  public void select(int selStart, int selEnd) {
    androidWidget.setSelection(selStart, selEnd);
  }

  @Override
  public int getCaretPosition() {
    return androidWidget.getSelectionEnd();
  }

  @Override
  public void setCaretPosition(int pos) {
    androidWidget.setSelection(pos, pos);
  }

  @Override
  public InputMethodRequests getInputMethodRequests() {
    // TODO
    return null;
  }

  @Override
  public void insert(String text, int pos) {
    androidWidget.getText().insert(pos, text);
  }

  @Override
  public void replaceRange(String text, int start, int end) {
    androidWidget.getText().replace(start, end, text);
  }

  @Override
  public Dimension getPreferredSize(int rows, int columns) {
    // TODO
    return null;
  }

  @Override
  public Dimension getMinimumSize(int rows, int columns) {
    // TODO
    return null;
  }
}
