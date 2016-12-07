package skinjob.internal.peer;

import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.dnd.peer.DragSourceContextPeer;

/**
 * Created by cryoc on 2016-10-10.
 */
public class SkinJobDragSourceContextPeer implements DragSourceContextPeer {
  public SkinJobDragSourceContextPeer(DragGestureEvent dge) {
    // TODO
  }

  @Override
  public void startDrag(DragSourceContext dsc, Cursor c, Image dragImage, Point imageOffset)
      throws InvalidDnDOperationException {
    // TODO
  }

  @Override
  public Cursor getCursor() {
    // TODO
    return null;
  }

  @Override
  public void setCursor(Cursor c) throws InvalidDnDOperationException {
    // TODO
  }

  @Override
  public void transferablesFlavorsChanged() {
    // TODO
  }
}
