package sun.java2d;

/**
 * Created by cryoc on 2016-10-20.
 */

public interface StateTrackable {
  public enum State {
    STABLE, IMMUTABLE, DYNAMIC, UNTRACKABLE
  }
}
