package skinjob.internal.peer;

import android.view.View;

import java.awt.Panel;
import java.awt.peer.PanelPeer;

/**
 * SkinJobGlobals Android implementation of {@link PanelPeer}.
 */
public class SkinJobPanelPeer extends SkinJobComponentPeerForView<View> implements PanelPeer {
  public SkinJobPanelPeer(Panel target) {
    super(target.sjAndroidWidget);
  }
}
