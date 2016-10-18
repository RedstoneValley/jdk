package java.awt.dnd;

/**
 * Created by cryoc on 2016-10-17.
 */
public class Timer {
  private boolean coalesce;
  private int initialDelay;
  private boolean running;

  public Timer(int i, DropTarget.DropTargetAutoScroller dropTargetAutoScroller) {
  }

  public void setCoalesce(boolean coalesce) {
    this.coalesce = coalesce;
  }

  public void setInitialDelay(int initialDelay) {
    this.initialDelay = initialDelay;
  }

  public void start() {
    // TODO
  }

  public void stop() {
    // TODO
  }

  public boolean isRunning() {
    return running;
  }
}
