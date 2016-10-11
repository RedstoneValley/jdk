package java.awt;

import android.content.Context;
import android.view.View;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by cryoc on 2016-10-10.
 */

public abstract class SkinJobComponent<T extends View> extends Component {
    protected final T androidComponent;

    /**
     * Constructs a new component. Class <code>Component</code> can be
     * extended directly to create a lightweight component that does not
     * utilize an opaque native window. A lightweight component must be
     * hosted by a native container somewhere higher up in the component
     * tree (for example, by a <code>Frame</code> object).
     *
     * @param androidComponent
     * @param androidContext
     */
    protected SkinJobComponent(Class<? extends T> componentClass) {
        try {
            super(componentClass.getConstructor(Context.class).newInstance(SkinJobUtil.getAndroidApplicationContext()),
                    SkinJobUtil.getAndroidApplicationContext());
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
