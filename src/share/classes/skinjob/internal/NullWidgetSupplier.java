package skinjob.internal;

import android.content.Context;
import android.view.View;

/**
 * Created by cryoc on 2016-10-16.
 */
public final class NullWidgetSupplier extends WrappedAndroidObjectsSupplier<View> {
  private static final NullWidgetSupplier instance = new NullWidgetSupplier();

  /**
   * Singleton.
   */
  private NullWidgetSupplier() {
  }

  public static NullWidgetSupplier getInstance() {
    return instance;
  }

  @Override
  public View createWidget(Context context) {
    return null;
  }
}
