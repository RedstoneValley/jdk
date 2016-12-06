package skinjob.internal.peer;

import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;

import java.awt.Rectangle;
import java.awt.peer.RobotPeer;

import skinjob.SkinJobGlobals;

/**
 * Created by chris on 12/5/2016.
 */
public class SkinJobRobotPeer implements RobotPeer {
    @Override
    public void mouseMove(int x, int y) {
        // TODO
    }

    @Override
    public void mousePress(int buttons) {
        // TODO
    }

    @Override
    public void mouseRelease(int buttons) {
        // TODO
    }

    @Override
    public void mouseWheel(int wheelAmt) {
        // TODO
    }

    @Override
    public void keyPress(int keycode) {
        SkinJobGlobals.getAndroidApplicationContext().getSystemService(InputMethodManager.class)
                .dispatchKeyEventFromInputMethod(null, new KeyEvent(KeyEvent.ACTION_DOWN, keycode));
    }

    @Override
    public void keyRelease(int keycode) {
        SkinJobGlobals.getAndroidApplicationContext().getSystemService(InputMethodManager.class)
                .dispatchKeyEventFromInputMethod(null, new KeyEvent(KeyEvent.ACTION_UP, keycode));
    }

    @Override
    public int getRGBPixel(int x, int y) {
        // TODO
        return 0;
    }

    @Override
    public int[] getRGBPixels(Rectangle bounds) {
        // TODO
        return new int[0];
    }

    @Override
    public void dispose() {
        // No-op.
    }
}
