package sun.java2d;

import sun.java2d.StateTrackable.State;

/**
 * Created by cryoc on 2016-10-20.
 */
public class StateTrackableDelegate {
  private State state;

  public StateTrackableDelegate(State initialState) {
    state = initialState;
  }

  public static StateTrackableDelegate createInstance(State initialState) {
    // TODO
    return new StateTrackableDelegate(initialState);
  }

  public void markDirty() {
    // TODO
  }

  public void setUntrackable() {
    state = State.UNTRACKABLE;
  }
}
