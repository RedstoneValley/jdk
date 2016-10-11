package java.awt;

import android.content.Context;
import android.view.View;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.WeakHashMap;

/**
 * Created by cryoc on 2016-10-10.
 */

public class SkinJobWrappedAndroidObjectsSupplier<TWidget extends View>
        implements WrappedAndroidObjectsSupplier<TWidget> {
    private static final WeakHashMap<Class<? extends View>, SkinJobWrappedAndroidObjectsSupplier<?>>
            INSTANCES = new WeakHashMap<>();
    private final Constructor<TWidget> constructor;

    public SkinJobWrappedAndroidObjectsSupplier(Class<TWidget> widgetClass) {
        try {
            constructor = widgetClass.getConstructor(Context.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        constructor.setAccessible(true);
    }

    public static <TWidget extends View> SkinJobWrappedAndroidObjectsSupplier<TWidget> forClass(
            Class<TWidget> widgetClass) {
        SkinJobWrappedAndroidObjectsSupplier supplier = INSTANCES.get(widgetClass);
        if (supplier == null) {
            supplier = new SkinJobWrappedAndroidObjectsSupplier(widgetClass);
            INSTANCES.put(widgetClass, supplier);
        }
        return supplier;
    }
    @Override
    public Context getAppContext() {
        return SkinJobUtil.getAndroidApplicationContext();
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
