package skinjob.internal;

import android.content.Context;
import android.view.View;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.WeakHashMap;
import skinjob.SkinJobGlobals;

/**
 * Created by cryoc on 2016-10-10.
 */

public class DefaultWrappedAndroidObjectsSupplier<TWidget extends View>
    implements WrappedAndroidObjectsSupplier<TWidget> {
  private static final WeakHashMap<Class<? extends View>, DefaultWrappedAndroidObjectsSupplier<?>>
      INSTANCES = new WeakHashMap<>();
  private final Constructor<TWidget> constructor;

  public DefaultWrappedAndroidObjectsSupplier(Class<TWidget> widgetClass) {
    try {
      constructor = widgetClass.getConstructor(Context.class);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    constructor.setAccessible(true);
  }

  public static <TWidget extends View> DefaultWrappedAndroidObjectsSupplier<TWidget> forClass(
      Class<TWidget> widgetClass) {
    DefaultWrappedAndroidObjectsSupplier supplier = INSTANCES.get(widgetClass);
    if (supplier == null) {
      supplier = new DefaultWrappedAndroidObjectsSupplier(widgetClass);
      INSTANCES.put(widgetClass, supplier);
    }
    return supplier;
  }

  @Override
  public Context getAppContext() {
    return SkinJobGlobals.getAndroidApplicationContext();
  }

  @Override
  public TWidget createWidget() {
    try {
      return constructor.newInstance(getAppContext());
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }
}
