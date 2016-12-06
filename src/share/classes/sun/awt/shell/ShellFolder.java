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

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Michael Martak
 * @since 1.4
 */

public class ShellFolder extends File {
  private static final ShellFolderManager shellFolderManager;
  private static final ShellFolderManager.DirectInvoker invoker;
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
      managerClass = (Class<ShellFolderManager>) Class.forName(managerClassName, false, null);
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
   * @param key a {@code String}
   * @return An Object matching the string {@code key}.
   * @see ShellFolderManager#get(String)
   */
  public static Object get(String key) {
    return shellFolderManager.get(key);
  }

  /**
   * @return Whether this is a file system root directory
   */
  public static boolean isFileSystemRoot(File dir) {
    return shellFolderManager.isFileSystemRoot(dir);
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
   * This method is implemented to make sure that no instances
   * of {@code ShellFolder} are ever serialized. An instance of
   * this default implementation can always be represented with a
   * {@code java.io.File} object instead.
   *
   * @returns a {@code java.io.File} replacement object.
   */
  protected Object writeReplace() throws ObjectStreamException {
    return new File(getPath());
  }

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

  /**
   * @return An array of shell folders that are children of this shell folder
   * object, null if this shell folder is empty.
   */
  @Override
  public File[] listFiles() {
    File[] files = super.listFiles();
    if (files != null) {
      for (int i = 0; i < files.length; i++) {
        files[i] = new ShellFolder(this, files[i].getAbsolutePath());
      }
    }
    return files;
  }

  /**
   * @return Whether this shell folder is a link
   */
  public boolean isLink() {
    return false; // Not supported by default
  }

  /**
   * @return The name used to display this shell folder
   */
  public String getDisplayName() {
    return getName();
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
   * @return Whether this shell folder is marked as hidden
   */
  @Override
  public boolean isHidden() {
    String fileName = getName();
    return !fileName.isEmpty() && fileName.charAt(0) == '.';
  }
}
