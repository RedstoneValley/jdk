package skinjob.internal.peer;

import android.content.res.ColorStateList;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.View;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Insets;
import java.awt.Point;
import java.awt.peer.ComponentPeer;

import skinjob.SkinJobGlobals;
import skinjob.internal.SkinJobGraphics;
import skinjob.internal.SkinJobGraphicsConfiguration;
import skinjob.util.SkinJobUtil;
import sun.awt.CausedFocusEvent.Cause;

/**
 * Skeletal implementation of {@link SkinJobComponentPeer}&lt;T extends {@link View}&gt;.
 */
public abstract class SkinJobComponentPeerForView<T extends View> extends SkinJobComponentPeer<T> {

  protected final Graphics graphics;
  protected CharSequence text;

  public SkinJobComponentPeerForView(T androidComponent) {
    this(androidComponent, SkinJobGraphicsConfiguration.get(androidComponent.getDisplay()));
  }

  public SkinJobComponentPeerForView(T androidComponent, GraphicsConfiguration configuration) {
    super(androidComponent, configuration);
    graphics = new SkinJobGraphics(androidComponent.getDrawingCache());
  }

  @Override
  public Graphics getGraphics() {
    return graphics;
  }

  @Override
  public void setForeground(Color c) {
    super.setForeground(c);
    androidWidget.setForegroundTintList(ColorStateList.valueOf(c.getRGB()));
  }

  @Override
  public synchronized void setFont(Font f) {
    super.setFont(f);
    updateStyledText();
  }

  protected synchronized void updateStyledText() {
    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
    if (font != null) {
      for (Object span : font.sjGetAndroidSpans()) {
        spannableStringBuilder.setSpan(span, 0, text.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
      }
    }
    setTextInternal(spannableStringBuilder);
  }

  protected void setTextInternal(SpannableStringBuilder spannableStringBuilder) {
    // No-op if the subclass can't implement.
  }

  @Override
  public void setVisible(boolean v) {
    androidWidget.setVisibility(v ? View.VISIBLE : View.INVISIBLE);
  }

  @Override
  public void setEnabled(boolean e) {
    androidWidget.setEnabled(e);
  }

  @Override
  public void paint(Graphics g) {
    androidWidget.draw(getCanvas(g));
  }

  @Override
  public void setBounds(int x, int y, int width, int height, int op) {
    SkinJobUtil.setBounds(androidWidget, x, y, width, height, op);
  }

  @Override
  public Point getLocationOnScreen() {
    int[] location = new int[2];
    androidWidget.getLocationOnScreen(location);
    return new Point(location[0], location[1]);
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(androidWidget.getMeasuredWidth(), androidWidget.getMeasuredHeight());
  }

  @Override
  public Dimension getMinimumSize() {
    return new Dimension(androidWidget.getMinimumWidth(), androidWidget.getMinimumHeight());
  }

  @Override
  public void setBackground(Color c) {
    androidWidget.setBackgroundColor(c.getRGB());
  }

  @Override
  public boolean requestFocus(
      Component lightweightChild, boolean temporary, boolean focusedWindowChangeAllowed, long time,
      Cause cause) {
    return androidWidget.requestFocus();
  }

  @Override
  public boolean isFocusable() {
    return androidWidget.isFocusable();
  }

  @Override
  public boolean handlesWheelScrolling() {
    return androidWidget.isSelected() && androidWidget.isVerticalScrollBarEnabled();
  }

  @Override
  public void setZOrder(ComponentPeer above) {
    if (above instanceof SkinJobComponentPeer<?>) {
      Object otherAndroidComponent = ((SkinJobComponentPeer<?>) above).androidWidget;
      if (otherAndroidComponent instanceof View) {
        DisplayMetrics metrics = new DisplayMetrics();
        androidWidget.getDisplay().getMetrics(metrics);
        androidWidget.setCameraDistance(((View) otherAndroidComponent).getCameraDistance()
            - metrics.density * SkinJobGlobals.layerZSpacing);
        return;
      }
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public Insets getInsets() {
    return new Insets(
        androidWidget.getTop(),
        androidWidget.getLeft(),
        androidWidget.getBottom(),
        androidWidget.getRight());
  }

  @Override
  public void endLayout() {
    androidWidget.requestLayout();
  }

  @Override
  public void layout() {
    endLayout();
  }

  public synchronized void setLabel(String text) {
    this.text = text;
    updateStyledText();
  }
}
