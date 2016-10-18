/*
 * Copyright (c) 2002, 2014, Oracle and/or its affiliates. All rights reserved.
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

package sun.java2d;

import java.awt.SkinJob;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class is used for registering and disposing the native
 * data associated with java objects.
 * <p>
 * The object can register itself by calling one of the addRecord
 * methods and providing either the pointer to the native disposal
 * method or a descendant of the DisposerRecord class with overridden
 * dispose() method.
 * <p>
 * When the object becomes unreachable, the dispose() method
 * of the associated DisposerRecord object will be called.
 *
 * @see DisposerRecord
 */
public class Disposer implements Runnable {
  public static final int WEAK = 0;
  public static final int PHANTOM = 1;
  private static final ReferenceQueue queue = new ReferenceQueue();
  private static final Hashtable records = new Hashtable();
  private static final Disposer disposerInstance;
  public static int refType = PHANTOM;
  private static ArrayList<DisposerRecord> deferredRecords = new ArrayList<>();

  static {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      @Override
      public Void run() {
        System.loadLibrary("awt");
        return null;
      }
    });
    String type = System.getProperty("sun.java2d.reftype");
    if (type != null) {
      if ("weak".equals(type)) {
        refType = WEAK;
        System.err.println("Using WEAK refs");
      } else {
        refType = PHANTOM;
        System.err.println("Using PHANTOM refs");
      }
    }
    disposerInstance = new Disposer();
    AccessController.doPrivileged((PrivilegedAction<Void>) () -> {
                    /* The thread must be a member of a thread group
                     * which will not get GCed before VM exit.
                     * Make its parent the top-level thread group.
                     */
      ThreadGroup rootTG = SkinJob.getRootThreadGroup();
      Thread t = new Thread(rootTG, disposerInstance, "Java2D Disposer");
      t.setContextClassLoader(null);
      t.setDaemon(true);
      t.setPriority(Thread.MAX_PRIORITY);
      t.start();
      return null;
    });
  }

  /**
   * Registers the object and the native data for later disposal.
   *
   * @param target Object to be registered
   * @param rec    the associated DisposerRecord object
   * @see DisposerRecord
   */
  public static void addRecord(Object target, DisposerRecord rec) {
    disposerInstance.add(target, rec);
  }

  private static void clearDeferredRecords() {
    if (deferredRecords == null || deferredRecords.isEmpty()) {
      return;
    }
    for (int i = 0; i < deferredRecords.size(); i++) {
      try {
        DisposerRecord rec = deferredRecords.get(i);
        rec.dispose();
      } catch (Exception e) {
        System.out.println("Exception while disposing deferred rec.");
      }
    }
    deferredRecords.clear();
  }

  /* This is intended for use in conjunction with addReference(..)
   */
  public static ReferenceQueue getQueue() {
    return queue;
  }

  /**
   * Performs the actual registration of the target object to be disposed.
   *
   * @param target Object to be registered, or if target is an instance
   *               of DisposerTarget, its associated disposer referent
   *               will be the Object that is registered
   * @param rec    the associated DisposerRecord object
   * @see DisposerRecord
   */
  synchronized void add(Object target, DisposerRecord rec) {
    if (target instanceof DisposerTarget) {
      target = ((DisposerTarget) target).getDisposerReferent();
    }
    Reference ref;
    ref = refType == PHANTOM ? new PhantomReference(target, queue)
        : new WeakReference(target, queue);
    records.put(ref, rec);
  }

  @Override
  public void run() {
    while (true) {
      try {
        Object obj = queue.remove();
        ((Reference) obj).clear();
        DisposerRecord rec = (DisposerRecord) records.remove(obj);
        rec.dispose();
        clearDeferredRecords();
      } catch (Exception e) {
        System.out.println("Exception while removing reference.");
      }
    }
  }
}
