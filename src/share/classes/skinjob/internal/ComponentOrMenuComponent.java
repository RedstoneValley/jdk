package skinjob.internal;

import android.content.Context;
import android.view.View;
import java.awt.Component;
import java.awt.MenuComponent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Common elements of {@link Component} and {@link MenuComponent}.
 */
public abstract class ComponentOrMenuComponent implements Serializable {
  private static final long serialVersionUID = 5874307035189784860L;
  public transient View sjAndroidWidget;
  protected transient Context sjAndroidContext;
  protected WrappedAndroidObjectsSupplier<?> wrappedObjectsSupplier;

  public ComponentOrMenuComponent(
      WrappedAndroidObjectsSupplier<?> wrappedObjectsSupplier) {
    sjAndroidContext = wrappedObjectsSupplier.getAppContext();
    sjAndroidWidget = wrappedObjectsSupplier.createWidget();
    this.wrappedObjectsSupplier = wrappedObjectsSupplier;
  }

  /**
   * Constructs a new component. Class {@code Component} can be
   * extended directly to create a lightweight component that does not
   * utilize an opaque native window. A lightweight component must be
   * hosted by a native container somewhere higher up in the component
   * tree (for example, by a {@code Frame} object).
   */
  protected ComponentOrMenuComponent(Class<? extends View> androidWidgetClass) {
    this(DefaultWrappedAndroidObjectsSupplier.forClass(androidWidgetClass));
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    sjAndroidContext = wrappedObjectsSupplier.getAppContext();
    sjAndroidWidget = wrappedObjectsSupplier.createWidget();
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
