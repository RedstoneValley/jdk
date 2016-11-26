package skinjob.internal.peer;

import android.text.SpannableStringBuilder;
import android.widget.Button;

import java.awt.peer.ButtonPeer;

/**
 * Created by cryoc on 2016-10-09.
 */
public class SkinJobButtonPeer extends SkinJobComponentPeerForView<android.widget.Button>
    implements ButtonPeer {
  public SkinJobButtonPeer(Button androidComponent) {
    super(androidComponent);
  }

  public SkinJobButtonPeer(java.awt.Button target) {
    this((Button) target.sjAndroidWidget);
  }

  @Override
  protected void setTextInternal(SpannableStringBuilder spannableStringBuilder) {
    androidWidget.setText(spannableStringBuilder);
  }
}
