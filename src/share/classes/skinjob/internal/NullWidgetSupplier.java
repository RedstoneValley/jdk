package skinjob.internal;

import android.content.Context;
import android.view.View;
import skinjob.SkinJobGlobals;

/**
 * Created by cryoc on 2016-10-16.
 */
public final class NullWidgetSupplier implements WrappedAndroidObjectsSupplier<View> {
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
  public Context getAppContext() {
    return SkinJobGlobals.getAndroidApplicationContext();
  }

  @Override
  public View createWidget() {
    return null;
  }
}
