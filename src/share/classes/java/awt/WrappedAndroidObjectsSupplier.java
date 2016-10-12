package java.awt;

import android.content.Context;
import android.view.View;

/**
 * Provides the Android API objects that an AWT object is to wrap.
 */
public interface WrappedAndroidObjectsSupplier<TWidget extends View> {
  Context getAppContext();

  TWidget createWidget();
}
