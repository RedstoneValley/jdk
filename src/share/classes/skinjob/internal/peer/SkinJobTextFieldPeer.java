package skinjob.internal.peer;

import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import java.awt.Dimension;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextAreaPeer;
import java.awt.peer.TextFieldPeer;

/**
 * Created by cryoc on 2016-10-09.
 */
public class SkinJobTextFieldPeer extends SkinJobComponentPeerForView<EditText>
    implements TextFieldPeer, TextAreaPeer {
  private volatile boolean isPassword = false;
  private PasswordMasker passwordMasker = new PasswordMasker();
  public SkinJobTextFieldPeer(TextField target) {
    super((EditText) target.sjAndroidWidget);
  }
  public SkinJobTextFieldPeer(TextArea target) {
    super((EditText) target.sjAndroidWidget);
  }

  @Override
  public synchronized void setEchoChar(char echoChar) {
    if (!isPassword) {
      // TODO: Does this work even though sjAndroidWidget doesn't have android:password="true"?
      // If not, create a new sjAndroidWidget
      androidWidget.setTransformationMethod(passwordMasker);
      isPassword = true;
    }
    passwordMasker.maskCharacter = echoChar;
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

  private static class PasswordMasker extends PasswordTransformationMethod {
    private volatile char maskCharacter;

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
      int codePointLength = Character.codePointCount(source, 0, source.length());
      return new String(new char[codePointLength]).replace('\0', maskCharacter);
    }
  }
}
