package java.awt;

import android.widget.Button;
import java.awt.peer.ButtonPeer;

/**
 * Created by cryoc on 2016-10-09.
 */
class SkinJobButtonPeer extends SkinJobComponentPeerForView<android.widget.Button>
    implements ButtonPeer {
  public SkinJobButtonPeer(Button androidComponent) {
    super(androidComponent);
  }

  public SkinJobButtonPeer(Button androidComponent, GraphicsConfiguration configuration) {
    super(androidComponent, configuration);
  }

  public SkinJobButtonPeer(java.awt.Button target) {
    this((Button) target.androidWidget);
  }

  @Override
  public void setLabel(String label) {
    // TODO
  }
}
