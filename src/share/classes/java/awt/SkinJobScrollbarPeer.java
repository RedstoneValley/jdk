package java.awt;

import android.widget.ScrollView;
import java.awt.peer.ScrollbarPeer;

/**
 * Created by cryoc on 2016-10-10.
 */
public class SkinJobScrollbarPeer extends SkinJobComponentPeerForView<ScrollView>
    implements ScrollbarPeer {
  public SkinJobScrollbarPeer(Scrollbar target) {
    super((ScrollView) target.androidWidget);
  }

  @Override
  public void setValues(int value, int visible, int minimum, int maximum) {
    // TODO
  }

  @Override
  public void setLineIncrement(int l) {
    // TODO
  }

  @Override
  public void setPageIncrement(int l) {
    // TODO
  }
}
