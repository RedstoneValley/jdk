package skinjob.internal;

import android.content.Context;
import android.view.View;

import java.awt.Component;
import java.awt.MenuComponent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

import skinjob.SkinJobGlobals;

/**
 * Common elements of {@link Component} and {@link MenuComponent}.
 */
public abstract class ComponentOrMenuComponent
        implements Serializable {
  private static final long serialVersionUID = 5874307035189784860L;
  public transient View sjAndroidWidget;
  protected transient Context sjAndroidContext;

  public ComponentOrMenuComponent() {
    sjInitAndroidFields();
  }

  private void sjInitAndroidFields() {
    sjAndroidContext = SkinJobGlobals.getAndroidApplicationContext();
    sjAndroidWidget = sjGetWrappedAndroidObjectsSupplier().createAndInitWidget();
  }

  protected WrappedAndroidObjectsSupplier<?> sjGetWrappedAndroidObjectsSupplier() {
    return new DefaultWrappedAndroidObjectsSupplier<>(View.class);
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    sjInitAndroidFields();
  }

  /**
   * Will be called when a subclass is deserialized from a non-SkinJob implementation of AWT.
   *
   * CAUTION: Deserializing such objects is still experimental.
   */
  private void readObjectNoData() {
    sjInitAndroidFields();
  }

  /**
   * Gets the parent of this component.
   *
   * @return the parent of this component
   * @since JDK1.0
   */
  public abstract Object getParent();

  /**
   * Gets this component's locking object (the object that owns the thread
   * synchronization monitor) for AWT component-tree and layout
   * operations.
   *
   * @return this component's locking object
   */
  public abstract Object getTreeLock();
}
