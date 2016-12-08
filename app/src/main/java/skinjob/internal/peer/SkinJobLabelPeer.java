package skinjob.internal.peer;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.SpannableStringBuilder;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import java.awt.Label;
import java.awt.peer.LabelPeer;

/**
 * SkinJob Android implementation of {@link LabelPeer}.
 */
public class SkinJobLabelPeer extends SkinJobComponentPeerForView<TextView> implements LabelPeer {
  public SkinJobLabelPeer(Label target) {
    super((TextView) target.sjAndroidWidget);
  }

  @Override
  public void setText(String label) {
    setLabel(label);
  }

  @Override
  protected void setTextInternal(SpannableStringBuilder spannableStringBuilder) {
    androidWidget.setText(spannableStringBuilder);
  }

  @SuppressLint("RtlHardcoded")
  @Override
  public void setAlignment(int alignment) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      androidWidget.setTextAlignment(View.TEXT_ALIGNMENT_GRAVITY);
    }
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
        throw new IllegalArgumentException("No horizontal alignment number " + alignment);
    }
  }
}
