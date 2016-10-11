package java.awt;

import android.widget.EditText;

import java.awt.event.PaintEvent;
import java.awt.im.InputMethodRequests;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.TextFieldPeer;

import sun.awt.CausedFocusEvent;

/**
 * Created by cryoc on 2016-10-09.
 */
class SkinJobTextFieldPeer extends SkinJobComponentPeerForView<EditText> implements TextFieldPeer {
    public SkinJobTextFieldPeer(TextField target) {
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
        androidComponent.setEnabled(editable);
    }

    @Override
    public String getText() {
        return androidComponent.getText().toString();
    }

    @Override
    public void setText(String text) {
        androidComponent.setText(text);
    }

    @Override
    public int getSelectionStart() {
        return androidComponent.getSelectionStart();
    }

    @Override
    public int getSelectionEnd() {
        return androidComponent.getSelectionEnd();
    }

    @Override
    public void select(int selStart, int selEnd) {
        androidComponent.setSelection(selStart, selEnd);
    }

    @Override
    public void setCaretPosition(int pos) {
        androidComponent.setSelection(pos, pos);
    }

    @Override
    public int getCaretPosition() {
        return androidComponent.getSelectionEnd();
    }

    @Override
    public InputMethodRequests getInputMethodRequests() {
        // TODO
        return null;
    }
}
