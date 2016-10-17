package java.awt;

import android.content.Context;
import android.view.View;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * Common elements of {@link Component} and {@link MenuComponent}.
 */
public abstract class ComponentOrMenuComponent implements Serializable {
  private static final long serialVersionUID = 5874307035189784860L;
  protected transient Context androidContext;
  protected transient View androidWidget;

  protected WrappedAndroidObjectsSupplier<?> wrappedObjectsSupplier;

  public ComponentOrMenuComponent(
      WrappedAndroidObjectsSupplier<?> wrappedObjectsSupplier) {
    androidContext = wrappedObjectsSupplier.getAppContext();
    androidWidget = wrappedObjectsSupplier.createWidget();
    this.wrappedObjectsSupplier = wrappedObjectsSupplier;
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    androidContext = wrappedObjectsSupplier.getAppContext();
    androidWidget = wrappedObjectsSupplier.createWidget();
  }

  /**
   * Constructs a new component. Class {@code Component} can be
   * extended directly to create a lightweight component that does not
   * utilize an opaque native window. A lightweight component must be
   * hosted by a native container somewhere higher up in the component
   * tree (for example, by a {@code Frame} object).
   */
  protected ComponentOrMenuComponent(Class<? extends View> androidWidgetClass) {
    this(SkinJobWrappedAndroidObjectsSupplier.forClass(androidWidgetClass));
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
