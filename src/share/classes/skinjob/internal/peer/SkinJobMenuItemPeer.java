package skinjob.internal.peer;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.awt.Font;
import java.awt.MenuItem;
import java.awt.peer.CheckboxMenuItemPeer;

/**
 * SkinJobGlobals Android implementation of {@link CheckboxMenuItemPeer}.
 */
public class SkinJobMenuItemPeer implements CheckboxMenuItemPeer {
  protected final android.view.MenuItem androidMenuItem;
  protected Font font;
  protected CharSequence label;

  public SkinJobMenuItemPeer(MenuItem target) {
    androidMenuItem = target.androidMenuItem;
    label = androidMenuItem.getTitle();
  }

  @Override
  public synchronized void setLabel(String label) {
    androidMenuItem.setTitle(label);
    updateText();
  }

  @Override
  public void setEnabled(boolean e) {
    androidMenuItem.setEnabled(e);
  }

  @Override
  public void dispose() {
    // No-op.
  }

  protected synchronized void updateText() {
    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(label);
    if (font != null) {
      for (Object span : font.sjGetAndroidSpans()) {
        spannableStringBuilder.setSpan(span, 0, label.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
      }
    }
    androidMenuItem.setTitle(spannableStringBuilder);
  }

  @Override
  public synchronized void setFont(Font f) {
    font = f;
    updateText();
  }

  @Override
  public void setState(boolean state) {
    androidMenuItem.setChecked(state);
  }
}
