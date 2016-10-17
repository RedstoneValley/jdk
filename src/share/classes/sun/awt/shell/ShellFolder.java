/*
 * Copyright (c) 2000, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Callable;

/**
 * @author Michael Martak
 * @since 1.4
 */

public abstract class ShellFolder extends File {
  private static final String COLUMN_NAME = "FileChooser.fileNameHeaderText";
  private static final String COLUMN_SIZE = "FileChooser.fileSizeHeaderText";
  private static final String COLUMN_DATE = "FileChooser.fileDateHeaderText";
  private static final ShellFolderManager shellFolderManager;
  private static final Invoker invoker;
  /**
   * Provides a default comparator for the default column set
   */
  private static final Comparator DEFAULT_COMPARATOR = new Comparator() {
    @Override
    public int compare(Object o1, Object o2) {
      int gt;

      if (o1 == null && o2 == null) {
        gt = 0;
      } else if (o1 != null && o2 == null) {
        gt = 1;
      } else if (o1 == null) {
        gt = -1;
      } else if (o1 instanceof Comparable) {
        gt = ((Comparable) o1).compareTo(o2);
      } else {
        gt = 0;
      }

      return gt;
    }
  };
  static final Comparator<File> FILE_COMPARATOR = new Comparator<File>() {
    @Override
    public int compare(File f1, File f2) {
      ShellFolder sf1 = null;
      ShellFolder sf2 = null;

      if (f1 instanceof ShellFolder) {
        sf1 = (ShellFolder) f1;
        if (sf1.isFileSystem()) {
          sf1 = null;
        }
      }
      if (f2 instanceof ShellFolder) {
        sf2 = (ShellFolder) f2;
        if (sf2.isFileSystem()) {
          sf2 = null;
        }
      }

      if (sf1 != null && sf2 != null) {
        return sf1.compareTo(sf2);
      } else if (sf1 != null) {
        // Non-file shellfolders sort before files
        return -1;
      } else if (sf2 != null) {
        return 1;
      } else {
        String name1 = f1.getName();
        String name2 = f2.getName();

        // First ignore case when comparing
        int diff = name1.compareToIgnoreCase(name2);
        return diff != 0 ? diff : name1.compareTo(name2);
      }
    }
  };

  static {
    String managerClassName = (String) Toolkit.getDefaultToolkit().
        getDesktopProperty("Shell.shellFolderManager");
    Class<ShellFolderManager> managerClass = null;
    try {
      managerClass = Class.forName(managerClassName, false, null);
      if (!ShellFolderManager.class.isAssignableFrom(managerClass)) {
        managerClass = null;
      }
      // swallow the exceptions below and use default shell folder
    } catch (ClassNotFoundException | SecurityException | NullPointerException e) {
    }

    if (managerClass == null) {
      managerClass = ShellFolderManager.class;
    }
    try {
      shellFolderManager = managerClass.newInstance();
    } catch (InstantiationException e) {
      throw new Error("Could not instantiate Shell Folder Manager: " + managerClass.getName());
    } catch (IllegalAccessException e) {
      throw new Error("Could not access Shell Folder Manager: " + managerClass.getName());
    }

    invoker = shellFolderManager.createInvoker();
  }

  protected final ShellFolder parent;

  /**
   * Create a file system shell folder from a file
   */
  ShellFolder(ShellFolder parent, String pathname) {
    super(pathname != null ? pathname : "ShellFolder");
    this.parent = parent;
  }

  /**
   * Return a shell folder from a file object
   *
   * @throws FileNotFoundException if file does not exist
   */
  public static ShellFolder getShellFolder(File file) throws FileNotFoundException {
    if (file instanceof ShellFolder) {
      return (ShellFolder) file;
    }
    if (!file.exists()) {
      throw new FileNotFoundException();
    }
    return shellFolderManager.createShellFolder(file);
  }

  /**
   * @param key a {@code String}
   * @return An Object matching the string {@code key}.
   * @see ShellFolderManager#get(String)
   */
  public static Object get(String key) {
    return shellFolderManager.get(key);
  }

  /**
   * Does {@code dir} represent a "computer" such as a node on the network, or
   * "My Computer" on the desktop.
   */
  public static boolean isComputerNode(File dir) {
    return shellFolderManager.isComputerNode(dir);
  }

  /**
   * @return Whether this is a file system root directory
   */
  public static boolean isFileSystemRoot(File dir) {
    return shellFolderManager.isFileSystemRoot(dir);
  }

  /**
   * Canonicalizes files that don't have symbolic links in their path.
   * Normalizes files that do, preserving symbolic links from being resolved.
   */
  public static File getNormalizedFile(File f) throws IOException {
    File canonical = f.getCanonicalFile();
    if (f.equals(canonical)) {
      // path of f doesn't contain symbolic links
      return canonical;
    }

    // preserve symbolic links from being resolved
    return new File(f.toURI().normalize());
  }

  public static void sort(List<? extends File> files) {
    if (files == null || files.size() <= 1) {
      return;
    }

    // To avoid loads of synchronizations with Invoker and improve performance we
    // synchronize the whole code of the sort method once
    invoke(new Callable<Void>() {
      @Override
      public Void call() {
        // Check that we can use the ShellFolder.sortChildren() method:
        //   1. All files have the same non-null parent
        //   2. All files is ShellFolders
        File commonParent = null;

        for (File file : files) {
          File parent = file.getParentFile();

          if (parent == null || !(file instanceof ShellFolder)) {
            commonParent = null;

            break;
          }

          if (commonParent == null) {
            commonParent = parent;
          } else {
            if (commonParent != parent && !commonParent.equals(parent)) {
              commonParent = null;

              break;
            }
          }
        }

        if (commonParent instanceof ShellFolder) {
          ((ShellFolder) commonParent).sortChildren(files);
        } else {
          Collections.sort(files, FILE_COMPARATOR);
        }

        return null;
      }
    });
  }

  public static ShellFolderColumnInfo[] getFolderColumns(File dir) {
    ShellFolderColumnInfo[] columns = null;

    if (dir instanceof ShellFolder) {
      columns = ((ShellFolder) dir).getFolderColumns();
    }

    if (columns == null) {
      columns = new ShellFolderColumnInfo[]{
          new ShellFolderColumnInfo(COLUMN_NAME,
              150,
              SwingConstants.LEADING,
              true,
              null,
              FILE_COMPARATOR), new ShellFolderColumnInfo(COLUMN_SIZE,
          75,
          SwingConstants.RIGHT,
          true,
          DEFAULT_COMPARATOR,
          true), new ShellFolderColumnInfo(COLUMN_DATE,
          130,
          SwingConstants.LEADING,
          true,
          DEFAULT_COMPARATOR,
          true)};
    }

    return columns;
  }

  public static Object getFolderColumnValue(File file, int column) {
    if (file instanceof ShellFolder) {
      Object value = ((ShellFolder) file).getFolderColumnValue(column);
      if (value != null) {
        return value;
      }
    }

    if (file == null || !file.exists()) {
      return null;
    }

    switch (column) {
      case 0:
        // By default, file name will be rendered using getSystemDisplayName()
        return file;

      case 1: // size
        return file.isDirectory() ? null : file.length();

      case 2: // date
        if (isFileSystemRoot(file)) {
          return null;
        }
        long time = file.lastModified();
        return time == 0L ? null : new Date(time);

      default:
        return null;
    }
  }

  // Static

  /**
   * Invokes the {@code task} which doesn't throw checked exceptions
   * from its {@code call} method. If invokation is interrupted then Thread.currentThread()
   * .isInterrupted() will
   * be set and result will be {@code null}
   */
  public static <T> T invoke(Callable<T> task) {
    try {
      return invoke(task, RuntimeException.class);
    } catch (InterruptedException e) {
      return null;
    }
  }

  /**
   * Invokes the {@code task} which throws checked exceptions from its {@code call} method.
   * If invokation is interrupted then Thread.currentThread().isInterrupted() will
   * be set and InterruptedException will be thrown as well.
   */
  public static <T, E extends Throwable> T invoke(Callable<T> task, Class<E> exceptionClass)
      throws InterruptedException, E {
    try {
      return invoker.invoke(task);
    } catch (Exception e) {
      if (e instanceof RuntimeException) {
        // Rethrow unchecked exceptions
        throw (RuntimeException) e;
      }

      if (e instanceof InterruptedException) {
        // Set isInterrupted flag for current thread
        Thread.currentThread().interrupt();

        // Rethrow InterruptedException
        throw (InterruptedException) e;
      }

      if (exceptionClass.isInstance(e)) {
        throw exceptionClass.cast(e);
      }

      throw new RuntimeException("Unexpected error", e);
    }
  }

  /**
   * @return Whether this is a file system shell folder
   */
  public boolean isFileSystem() {
    return !getPath().startsWith("ShellFolder");
  }

  /**
   * This method must be implemented to make sure that no instances
   * of {@code ShellFolder} are ever serialized. If {@code isFileSystem()} returns
   * {@code true}, then the object should be representable with an instance of
   * {@code java.io.File} instead. If not, then the object is most likely
   * depending on some internal (native) state and cannot be serialized.
   *
   * @returns a {@code java.io.File} replacement object, or {@code null}
   * if no suitable replacement can be found.
   */
  protected abstract Object writeReplace() throws ObjectStreamException;

  /**
   * Returns the path for this object's parent,
   * or {@code null} if this object does not name a parent
   * folder.
   *
   * @return the path as a String for this object's parent,
   * or {@code null} if this object does not name a parent
   * folder
   * @since 1.4
   */
  @Override
  public String getParent() {
    if (parent == null && isFileSystem()) {
      return super.getParent();
    }
    return parent != null ? parent.getPath() : null;
  }

  /**
   * Returns a File object representing this object's parent,
   * or {@code null} if this object does not name a parent
   * folder.
   *
   * @return a File object representing this object's parent,
   * or {@code null} if this object does not name a parent
   * folder
   * @since 1.4
   */
  @Override
  public File getParentFile() {
    if (parent != null) {
      return parent;
    } else if (isFileSystem()) {
      return super.getParentFile();
    } else {
      return null;
    }
  }

  @Override
  public boolean isAbsolute() {
    return !isFileSystem() || super.isAbsolute();
  }

  @Override
  public File getAbsoluteFile() {
    return isFileSystem() ? super.getAbsoluteFile() : this;
  }

  // Override File methods

  @Override
  public boolean canRead() {
    return !isFileSystem() || super.canRead();       // ((Fix?))
  }

  /**
   * Returns true if folder allows creation of children.
   * True for the "Desktop" folder, but false for the "My Computer"
   * folder.
   */
  @Override
  public boolean canWrite() {
    return isFileSystem() && super.canWrite();     // ((Fix?))
  }

  @Override
  public boolean exists() {
    // Assume top-level drives exist, because state is uncertain for
    // removable drives.
    return !isFileSystem() || isFileSystemRoot(this) || super.exists();
  }

  @Override
  public boolean isDirectory() {
    return !isFileSystem() || super.isDirectory();   // ((Fix?))
  }

  @Override
  public boolean isFile() {
    return isFileSystem() ? super.isFile() : !isDirectory();      // ((Fix?))
  }

  @Override
  public long lastModified() {
    return isFileSystem() ? super.lastModified() : 0L;    // ((Fix?))
  }

  @Override
  public long length() {
    return isFileSystem() ? super.length() : 0L;  // ((Fix?))
  }

  @Override
  public boolean createNewFile() throws IOException {
    return isFileSystem() && super.createNewFile();
  }

  @Override
  public boolean delete() {
    return isFileSystem() && super.delete();       // ((Fix?))
  }

  @Override
  public void deleteOnExit() {
    if (isFileSystem()) {
      super.deleteOnExit();
    } else {
      // Do nothing       // ((Fix?))
    }
  }

  @Override
  public File[] listFiles() {
    return listFiles(true);
  }

  @Override
  public boolean mkdir() {
    return isFileSystem() && super.mkdir();
  }

  @Override
  public boolean mkdirs() {
    return isFileSystem() && super.mkdirs();
  }

  @Override
  public boolean renameTo(File dest) {
    return isFileSystem() && super.renameTo(dest); // ((Fix?))
  }

  @Override
  public boolean setLastModified(long time) {
    return isFileSystem() && super.setLastModified(time); // ((Fix?))
  }

  @Override
  public boolean setReadOnly() {
    return isFileSystem() && super.setReadOnly(); // ((Fix?))
  }

  /**
   * Compares this ShellFolder with the specified ShellFolder for order.
   *
   * @see #compareTo(Object)
   */
  @Override
  public int compareTo(File file2) {
    if (file2 == null || !(file2 instanceof ShellFolder) || ((ShellFolder) file2).isFileSystem()) {

      return isFileSystem() ? super.compareTo(file2) : -1;
    } else {
      return isFileSystem() ? 1 : getName().compareTo(file2.getName());
    }
  }

  public String toString() {
    return isFileSystem() ? super.toString() : getDisplayName();
  }

  public File[] listFiles(boolean includeHiddenFiles) {
    File[] files = super.listFiles();

    if (!includeHiddenFiles) {
      Vector<File> v = new Vector<>();
      int nameCount = files == null ? 0 : files.length;
      for (int i = 0; i < nameCount; i++) {
        if (!files[i].isHidden()) {
          v.addElement(files[i]);
        }
      }
      files = v.toArray(new File[v.size()]);
    }

    return files;
  }

  /**
   * @return Whether this shell folder is a link
   */
  public abstract boolean isLink();

  /**
   * @return The shell folder linked to by this shell folder, or null
   * if this shell folder is not a link
   */
  public abstract ShellFolder getLinkLocation() throws FileNotFoundException;

  /**
   * @return The name used to display this shell folder
   */
  public abstract String getDisplayName();

  /**
   * @return The type of shell folder as a string
   */
  public abstract String getFolderType();

  /**
   * @return The executable type as a string
   */
  public abstract String getExecutableType();

  /**
   * @param getLargeIcon whether to return large icon (ignored in base implementation)
   * @return The icon used to display this shell folder
   */
  public Image getIcon(boolean getLargeIcon) {
    return null;
  }

  public void sortChildren(List<? extends File> files) {
    // To avoid loads of synchronizations with Invoker and improve performance we
    // synchronize the whole code of the sort method once
    invoke(new Callable<Void>() {
      @Override
      public Void call() {
        Collections.sort(files, FILE_COMPARATOR);

        return null;
      }
    });
  }

  public ShellFolderColumnInfo[] getFolderColumns() {
    return null;
  }

  public Object getFolderColumnValue(int column) {
    return null;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof ShellFolder)) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    ShellFolder that = (ShellFolder) o;

    return getParent() != null ? getParent().equals(that.getParent()) : that.getParent() == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (getParent() != null ? getParent().hashCode() : 0);
    return result;
  }

  /**
   * Interface allowing to invoke tasks in different environments on different platforms.
   */
  public interface Invoker {
    /**
     * Invokes a callable task.
     *
     * @param task a task to invoke
     * @return the result of {@code task}'s invokation
     * @throws Exception {@code InterruptedException} or an exception that was thrown from the
     *                   {@code task}
     */
    <T> T invoke(Callable<T> task) throws Exception;
  }
}
