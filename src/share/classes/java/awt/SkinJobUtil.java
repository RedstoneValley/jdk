package java.awt;

import android.app.Application;
import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by cryoc on 2016-10-09.
 */

final class SkinJobUtil {
    /** Shouldn't be instantiated. */
    private SkinJobUtil() {}

    public static Context getAndroidApplicationContext() {
        try {
            final Class<?> activityThreadClass =
                    Class.forName("android.app.ActivityThread");
            final Method method = activityThreadClass.getMethod("currentApplication");
            return (Application) method.invoke(null, (Object[]) null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
                | ClassNotFoundException e) {
            throw new AWTError(e.toString());
        }
    }
}
