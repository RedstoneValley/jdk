package java.awt;

import android.view.Gravity;
import android.widget.TextView;

import java.awt.event.PaintEvent;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;
import java.awt.image.VolatileImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.ContainerPeer;
import java.awt.peer.LabelPeer;

import sun.awt.CausedFocusEvent;

/**
 * Created by cryoc on 2016-10-09.
 */
class SkinJobLabelPeer extends SkinJobComponentPeerForView<TextView> implements LabelPeer {
    public SkinJobLabelPeer(Label target) {
        super((TextView) target.androidWidget);
    }

    @Override
    public void setText(String label) {
        androidComponent.setText(label);
    }

    @Override
    public void setAlignment(int alignment) {
        switch (alignment) {
            case Label.LEFT:
                androidComponent.setGravity(Gravity.LEFT);
                return;
            case Label.CENTER:
                androidComponent.setGravity(Gravity.CENTER_HORIZONTAL);
                return;
            case Label.RIGHT:
                androidComponent.setGravity(Gravity.RIGHT);
                return;
            default:
                throw new IllegalArgumentException(
                        String.format("No horizontal alignment number %d", alignment));
        }
    }
}
