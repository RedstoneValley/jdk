package skinjob.internal;

import android.content.Context;
import android.view.View;

import java.awt.Toolkit;

import skinjob.SkinJobGlobals;

/**
 * Provides the Android API objects that an AWT object is to wrap.
 */
public abstract class WrappedAndroidObjectsSupplier<TWidget extends View> {

  public TWidget createWidget() {
    return createWidget(SkinJobGlobals.getAndroidApplicationContext());
  }

  public abstract TWidget createWidget(Context context);

  public TWidget createAndInitWidget() {
    TWidget widget = createWidget();
    Toolkit.getDefaultToolkit().sjMaybeWatchWidgetForMouseCoords(widget);
    return widget;
  }
}
