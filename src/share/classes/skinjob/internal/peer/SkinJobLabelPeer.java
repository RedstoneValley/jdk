package skinjob.internal.peer;

import android.view.Gravity;
import android.widget.TextView;
import java.awt.Label;
import java.awt.peer.LabelPeer;

/**
 * Created by cryoc on 2016-10-09.
 */
public class SkinJobLabelPeer extends SkinJobComponentPeerForView<TextView> implements LabelPeer {
  public SkinJobLabelPeer(Label target) {
    super((TextView) target.sjAndroidWidget);
  }

  @Override
  public void setText(String label) {
    androidWidget.setText(label);
  }

  @Override
  public void setAlignment(int alignment) {
    switch (alignment) {
      case Label.LEFT:
        androidWidget.setGravity(Gravity.LEFT);
        return;
      case Label.CENTER:
        androidWidget.setGravity(Gravity.CENTER_HORIZONTAL);
        return;
      case Label.RIGHT:
        androidWidget.setGravity(Gravity.RIGHT);
        return;
      default:
        throw new IllegalArgumentException(String.format("No horizontal alignment number %d",
            alignment));
    }
  }
}
