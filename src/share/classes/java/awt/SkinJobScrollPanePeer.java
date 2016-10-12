package java.awt;

import android.widget.ScrollView;

import java.awt.peer.ScrollPanePeer;

/**
 * Created by cryoc on 2016-10-10.
 */
public class SkinJobScrollPanePeer
        extends SkinJobComponentPeerForView<ScrollView> implements ScrollPanePeer {
    public SkinJobScrollPanePeer(ScrollPane scrollPane) {
        super((ScrollView) scrollPane.androidWidget);
    }

    @Override
    public int getHScrollbarHeight() {
        return androidWidget.getScrollBarSize();
    }

    @Override
    public int getVScrollbarWidth() {
        return androidWidget.getVerticalScrollbarWidth();
    }

    @Override
    public void setScrollPosition(int x, int y) {
        androidWidget.scrollTo(x, y);
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

    @Override
    public Insets getInsets() {
        return null;
        // TODO
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
