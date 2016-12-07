package skinjob.internal;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Constructor;
import java.util.WeakHashMap;

import skinjob.SkinJobGlobals;

/**
 * Initializes the given Android widget/view class using a constructor whose only parameter is a
 * {@link Context}. Will obtain that constructor reflectively, so instantiating this class with a
 * class that lacks one will fail at runtime and be undetectable at compile time.
 */
public class DefaultWrappedAndroidObjectsSupplier<TWidget extends View>
    extends WrappedAndroidObjectsSupplier<TWidget> {
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
  public TWidget createWidget(Context context) {
    try {
      return constructor.newInstance(SkinJobGlobals.getAndroidApplicationContext());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
