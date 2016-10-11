package java.awt;

import android.widget.ScrollView;

import java.awt.peer.ScrollPanePeer;

/**
 * Created by cryoc on 2016-10-10.
 */
// TODO: All methods are stubs.
public class SkinJobScrollPanePeer extends SkinJobContainerPeer<ScrollView> implements ScrollPanePeer {
    public SkinJobScrollPanePeer(ScrollPane scrollPane) {
        super((ScrollView) scrollPane.androidComponent);
    }

    @Override
    public int getHScrollbarHeight() {
        return androidComponent.getScrollBarSize();
    }

    @Override
    public int getVScrollbarWidth() {
        return androidComponent.getVerticalScrollbarWidth();
    }

    @Override
    public void setScrollPosition(int x, int y) {
        androidComponent.scrollTo(x, y);
    }

    @Override
    public void childResized(int w, int h) {
        // No-op.
    }

    @Override
    public void setUnitIncrement(Adjustable adj, int u) {
        // No-op -- scroll bars in Android can't be discretized.
    }

    @Override
    public void setValue(Adjustable adj, int v) {
        // TODO
    }
}
