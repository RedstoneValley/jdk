package java.awt;

import android.view.View;

import java.awt.peer.ContainerPeer;

/**
 * Created by cryoc on 2016-10-10.
 */
public class SkinJobContainerPeer<T extends View> extends SkinJobComponentPeer<T> implements ContainerPeer {
    public SkinJobContainerPeer(T androidComponent, GraphicsConfiguration configuration) {
        super(androidComponent, configuration);
    }

    public SkinJobContainerPeer(T androidComponent) {
        super(androidComponent);
    }

    @Override
    public Insets getInsets() {
        return null; // TODO
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
