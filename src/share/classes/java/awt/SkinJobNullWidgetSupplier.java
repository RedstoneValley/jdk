package java.awt;

import android.content.Context;
import android.view.View;

/**
 * Created by cryoc on 2016-10-16.
 */
final class SkinJobNullWidgetSupplier implements WrappedAndroidObjectsSupplier<View> {
  private static final SkinJobNullWidgetSupplier instance = new SkinJobNullWidgetSupplier();

  public static SkinJobNullWidgetSupplier getInstance() {
    return instance;
  }

  /** Singleton. */
  private SkinJobNullWidgetSupplier() {}

  @Override
  public Context getAppContext() {
    return SkinJob.getAndroidApplicationContext();
  }

  @Override
  public View createWidget() {
    return null;
  }
}
