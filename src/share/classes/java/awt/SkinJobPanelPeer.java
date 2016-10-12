package java.awt;

import android.view.View;

import java.awt.peer.PanelPeer;

/**
 * SkinJob Android implementation of {@link PanelPeer}.
 */
public class SkinJobPanelPeer extends SkinJobComponentPeerForView<View> implements PanelPeer {
    public SkinJobPanelPeer(Panel target) {
        super(target.androidWidget);
    }

    @Override
    public Insets getInsets() {
        // TODO
        return null;
    }

    @Override
    public void beginValidate() {
        // No-op.
    }

    @Override
    public void endValidate() {
        // No-op.
    }

    @Override
    public void beginLayout() {
        // No-op.
    }

    @Override
    public void endLayout() {
        // No-op.
    }
}
