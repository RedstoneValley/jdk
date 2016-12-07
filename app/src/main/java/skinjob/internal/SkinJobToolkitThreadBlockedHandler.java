package skinjob.internal;

import java.util.concurrent.locks.ReentrantLock;

import sun.awt.datatransfer.ToolkitThreadBlockedHandler;

/**
 * Created by chris on 12/5/2016.
 */
public class SkinJobToolkitThreadBlockedHandler extends ReentrantLock
    implements ToolkitThreadBlockedHandler {
  private static final long serialVersionUID = 6741255445420742222L;

  @Override
  public void enter() {
    // TODO: Reverse-engineer the contract for this method
    lock();
  }

  @Override
  public void exit() {
    // TODO: Reverse-engineer the contract for this method
    unlock();
  }
}
