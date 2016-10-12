package java.awt;

import android.text.SpannableStringBuilder;
import java.awt.peer.CheckboxMenuItemPeer;

/**
 * SkinJob Android implementation of {@link CheckboxMenuItemPeer}.
 */
public class SkinJobMenuItemPeer implements CheckboxMenuItemPeer {
  protected final android.view.MenuItem androidMenuItem;
  protected int textColor = SkinJob.menuTextColor;

  public SkinJobMenuItemPeer(MenuItem target) {
    androidMenuItem = target.androidMenuItem;
  }

  @Override
  public void setLabel(String label) {
    androidMenuItem.setTitle(label);
  }

  @Override
  public void setEnabled(boolean e) {
    androidMenuItem.setEnabled(e);
  }

  @Override
  public void dispose() {
    // No-op.
  }

  @Override
  public void setFont(Font f) {
    SpannableStringBuilder decoratedTitle = new SpannableStringBuilder(androidMenuItem
        .getTitle()
        .toString());
    new SkinJobTextAttributesDecoder(textColor)
        .addAttributes(f.getAttributes())
        .applyTo(decoratedTitle, 0, decoratedTitle.length());
    androidMenuItem.setTitle(decoratedTitle);
  }

  @Override
  public void setState(boolean state) {
    androidMenuItem.setChecked(state);
  }
}
