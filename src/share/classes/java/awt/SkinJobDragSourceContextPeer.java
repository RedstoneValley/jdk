package java.awt;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;

/**
 * Created by cryoc on 2016-10-10.
 */
class SkinJobDragSourceContextPeer implements DragSourceContextPeer {
    public SkinJobDragSourceContextPeer(DragGestureEvent dge) {

    }

    @Override
    public void startDrag(DragSourceContext dsc, Cursor c, Image dragImage, Point imageOffset) throws InvalidDnDOperationException {

    }

    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public void setCursor(Cursor c) throws InvalidDnDOperationException {

    }

    @Override
    public void transferablesFlavorsChanged() {

    }
}
