package java.awt;

import android.view.View;
import java.awt.peer.PanelPeer;

/**
 * SkinJob Android implementation of {@link PanelPeer}.
 */
public class SkinJobPanelPeer extends SkinJobComponentPeerForView<View> implements PanelPeer {
  public SkinJobPanelPeer(Panel target) {
    super(target.androidWidget);
  }
}
