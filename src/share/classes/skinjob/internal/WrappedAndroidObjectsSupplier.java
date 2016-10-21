package skinjob.internal;

import android.content.Context;
import android.view.View;
import java.awt.Toolkit;
import java.io.Serializable;

/**
 * Provides the Android API objects that an AWT object is to wrap.
 */
public abstract class WrappedAndroidObjectsSupplier<TWidget extends View> implements Serializable {
  public abstract Context getAppContext();

  public abstract TWidget createWidget();

  public TWidget createAndInitWidget() {
    TWidget widget = createWidget();
    Toolkit.getDefaultToolkit().sjMaybeWatchWidgetForMouseCoords(widget);
    return widget;
  }
}
