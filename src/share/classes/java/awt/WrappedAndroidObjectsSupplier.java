package java.awt;

import android.content.Context;
import android.view.View;
import java.io.Serializable;

/**
 * Provides the Android API objects that an AWT object is to wrap.
 */
public interface WrappedAndroidObjectsSupplier<TWidget extends View> extends Serializable {
  Context getAppContext();

  TWidget createWidget();
}
