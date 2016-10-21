package skinjob.internal.peer;

import android.text.SpannableStringBuilder;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.peer.CheckboxMenuItemPeer;
import skinjob.SkinJobGlobals;
import skinjob.internal.TextAttributesDecoder;

/**
 * SkinJobGlobals Android implementation of {@link CheckboxMenuItemPeer}.
 */
public class SkinJobMenuItemPeer implements CheckboxMenuItemPeer {
  protected final android.view.MenuItem androidMenuItem;

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
    new TextAttributesDecoder(SkinJobGlobals.defaultForegroundColor)
        .addAttributes(f.getAttributes())
        .applyTo(decoratedTitle, 0, decoratedTitle.length());
    androidMenuItem.setTitle(decoratedTitle);
  }

  @Override
  public void setState(boolean state) {
    androidMenuItem.setChecked(state);
  }
}
