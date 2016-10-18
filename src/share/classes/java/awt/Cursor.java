/*
 * Copyright (c) 1996, 2013, Oracle and/or its affiliates. All rights reserved.
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
package java.awt;

import android.util.Log;
import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Properties;
import java.util.StringTokenizer;
import sun.awt.AWTAccessor;
import sun.awt.AWTAccessor.CursorAccessor;
import sun.java2d.Disposer;
import sun.java2d.DisposerRecord;

/**
 * A class to encapsulate the bitmap representation of the mouse cursor.
 *
 * @author Amy Fowler
 * @see Component#setCursor
 */
public class Cursor implements Serializable {

  /**
   * The default cursor type (gets set if no cursor is defined).
   */
  public static final int DEFAULT_CURSOR = 0;

  /**
   * The crosshair cursor type.
   */
  public static final int CROSSHAIR_CURSOR = 1;

  /**
   * The text cursor type.
   */
  public static final int TEXT_CURSOR = 2;

  /**
   * The wait cursor type.
   */
  public static final int WAIT_CURSOR = 3;

  /**
   * The south-west-resize cursor type.
   */
  public static final int SW_RESIZE_CURSOR = 4;

  /**
   * The south-east-resize cursor type.
   */
  public static final int SE_RESIZE_CURSOR = 5;

  /**
   * The north-west-resize cursor type.
   */
  public static final int NW_RESIZE_CURSOR = 6;

  /**
   * The north-east-resize cursor type.
   */
  public static final int NE_RESIZE_CURSOR = 7;

  /**
   * The north-resize cursor type.
   */
  public static final int N_RESIZE_CURSOR = 8;

  /**
   * The south-resize cursor type.
   */
  public static final int S_RESIZE_CURSOR = 9;

  /**
   * The west-resize cursor type.
   */
  public static final int W_RESIZE_CURSOR = 10;

  /**
   * The east-resize cursor type.
   */
  public static final int E_RESIZE_CURSOR = 11;

  /**
   * The hand cursor type.
   */
  public static final int HAND_CURSOR = 12;

  /**
   * The move cursor type.
   */
  public static final int MOVE_CURSOR = 13;
  /**
   * The type associated with all custom cursors.
   */
  public static final int CUSTOM_CURSOR = -1;
  /* Localization names and default values */
  static final String[][] cursorProperties = {
      {"AWT.DefaultCursor", "Default Cursor"}, {"AWT.CrosshairCursor", "Crosshair Cursor"},
      {"AWT.TextCursor", "Text Cursor"}, {"AWT.WaitCursor", "Wait Cursor"},
      {"AWT.SWResizeCursor", "Southwest Resize Cursor"},
      {"AWT.SEResizeCursor", "Southeast Resize Cursor"},
      {"AWT.NWResizeCursor", "Northwest Resize Cursor"},
      {"AWT.NEResizeCursor", "Northeast Resize Cursor"},
      {"AWT.NResizeCursor", "North Resize Cursor"}, {"AWT.SResizeCursor", "South Resize Cursor"},
      {"AWT.WResizeCursor", "West Resize Cursor"}, {"AWT.EResizeCursor", "East Resize Cursor"},
      {"AWT.HandCursor", "Hand Cursor"}, {"AWT.MoveCursor", "Move Cursor"},};
  /**
   * This field is a private replacement for 'predefined' array.
   */
  private static final Cursor[] predefinedPrivate = new Cursor[14];
  private static final Hashtable<String, Cursor> systemCustomCursors = new Hashtable<>(1);
  static final String systemCustomCursorDirPrefix = initCursorDir();

  /*
   * hashtable, filesystem dir prefix, filename, and properties for custom cursors support
   */
  static final String systemCustomCursorPropertiesFile = systemCustomCursorDirPrefix
      + "cursors.properties";
  private static final String CursorDotPrefix = "Cursor.";
  private static final String DotFileSuffix = ".File";
  private static final String DotHotspotSuffix = ".HotSpot";
  private static final String DotNameSuffix = ".Name";
  /*
   * JDK 1.1 serialVersionUID
   */
  private static final long serialVersionUID = 8028237497568985504L;
  private static final String TAG = "java.awt.Cursor";
  /**
   * @deprecated As of JDK version 1.7, the {@link #getPredefinedCursor(int)}
   * method should be used instead.
   */
  @Deprecated protected static final Cursor[] predefined = new Cursor[14];
  static Properties systemCustomCursorProperties;

  static {
    AWTAccessor.setCursorAccessor(new CursorAccessor() {
      @Override
      public long getPData(Cursor cursor) {
        return cursor.pData;
      }

      @Override
      public void setPData(Cursor cursor, long pData) {
        cursor.pData = pData;
      }

      @Override
      public int getType(Cursor cursor) {
        return cursor.type;
      }
    });
  }

  /**
   * The user-visible name of the cursor.
   *
   * @serial
   * @see #getName()
   */
  protected String name;
  /**
   * The chosen cursor type initially set to
   * the {@code DEFAULT_CURSOR}.
   *
   * @serial
   * @see #getType()
   */
  int type = DEFAULT_CURSOR;
  transient CursorDisposer disposer;
  /**
   * Hook into native data.
   */
  transient long pData;
  private transient Object anchor = new Object();

  /**
   * Creates a new cursor object with the specified type.
   *
   * @param type the type of cursor
   * @throws IllegalArgumentException if the specified cursor type
   *                                  is invalid
   */
  public Cursor(int type) {
    if (type < DEFAULT_CURSOR || type > MOVE_CURSOR) {
      throw new IllegalArgumentException("illegal cursor type");
    }
    this.type = type;

    // Lookup localized name.
    name = Toolkit.getProperty(cursorProperties[type][0], cursorProperties[type][1]);
  }

  /**
   * Creates a new custom cursor object with the specified name.<p>
   * Note:  this constructor should only be used by AWT implementations
   * as part of their support for custom cursors.  Applications should
   * use Toolkit.createCustomCursor().
   *
   * @param name the user-visible name of the cursor.
   * @see Toolkit#createCustomCursor
   */
  protected Cursor(String name) {
    type = CUSTOM_CURSOR;
    this.name = name;
  }

  private static String initCursorDir() {
    String jhome = System.getProperty("java.home");
    return jhome +
        File.separator + "lib" + File.separator + "images" +
        File.separator + "cursors" + File.separator;
  }

  /**
   * Returns a cursor object with the specified predefined type.
   *
   * @param type the type of predefined cursor
   * @return the specified predefined cursor
   * @throws IllegalArgumentException if the specified cursor type is
   *                                  invalid
   */
  public static Cursor getPredefinedCursor(int type) {
    if (type < DEFAULT_CURSOR || type > MOVE_CURSOR) {
      throw new IllegalArgumentException("illegal cursor type");
    }
    Cursor c = predefinedPrivate[type];
    if (c == null) {
      predefinedPrivate[type] = c = new Cursor(type);
    }
    // fill 'predefined' array for backwards compatibility.
    if (predefined[type] == null) {
      predefined[type] = c;
    }
    return c;
  }

  /**
   * Returns a system-specific custom cursor object matching the
   * specified name.  Cursor names are, for example: "Invalid.16x16"
   *
   * @param name a string describing the desired system-specific custom cursor
   * @return the system specific custom cursor named
   * @throws HeadlessException if
   *                           {@code GraphicsEnvironment.isHeadless} returns true
   */
  public static Cursor getSystemCustomCursor(String name) throws AWTException, HeadlessException {
    Cursor cursor = systemCustomCursors.get(name);

    if (cursor == null) {
      synchronized (systemCustomCursors) {
        if (systemCustomCursorProperties == null) {
          loadSystemCustomCursorProperties();
        }
      }

      String prefix = CursorDotPrefix + name;
      String key = prefix + DotFileSuffix;

      if (!systemCustomCursorProperties.containsKey(key)) {
        Log.v(TAG, "Cursor.getSystemCustomCursor(" + name + ") returned null");
        return null;
      }

      String fileName = systemCustomCursorProperties.getProperty(key);

      String localized = systemCustomCursorProperties.getProperty(prefix + DotNameSuffix);

      if (localized == null) {
        localized = name;
      }

      String hotspot = systemCustomCursorProperties.getProperty(prefix + DotHotspotSuffix);

      if (hotspot == null) {
        throw new AWTException("no hotspot property defined for cursor: " + name);
      }

      StringTokenizer st = new StringTokenizer(hotspot, ",");

      if (st.countTokens() != 2) {
        throw new AWTException("failed to parse hotspot property for cursor: " + name);
      }

      int x;
      int y;

      try {
        x = Integer.parseInt(st.nextToken());
        y = Integer.parseInt(st.nextToken());
      } catch (NumberFormatException nfe) {
        throw new AWTException("failed to parse hotspot property for cursor: " + name);
      }

      try {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage(systemCustomCursorDirPrefix + fileName);
        cursor = toolkit.createCustomCursor(image, new Point(x, y), localized);
      } catch (Exception e) {
        throw new AWTException("Exception: " + e.getClass() + " " + e.getMessage() +
            " occurred while creating cursor " + name);
      }

      if (cursor == null) {
        Log.v(TAG, "Cursor.getSystemCustomCursor(" + name + ") returned null");
      } else {
        systemCustomCursors.put(name, cursor);
      }
    }

    return cursor;
  }

  /**
   * Return the system default cursor.
   */
  public static Cursor getDefaultCursor() {
    return getPredefinedCursor(DEFAULT_CURSOR);
  }

  /*
   * load the cursor.properties file
   */
  private static void loadSystemCustomCursorProperties() throws AWTException {
    synchronized (systemCustomCursors) {
      systemCustomCursorProperties = new Properties();

      try {
        AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
          @Override
          public Object run() throws Exception {
            FileInputStream fis = null;
            try {
              fis = new FileInputStream(systemCustomCursorPropertiesFile);
              systemCustomCursorProperties.load(fis);
            } finally {
              if (fis != null) {
                fis.close();
              }
            }
            return null;
          }
        });
      } catch (Exception e) {
        systemCustomCursorProperties = null;
        throw new AWTException("Exception: " + e.getClass() + " " +
            e.getMessage() + " occurred while loading: " +
            systemCustomCursorPropertiesFile);
      }
    }
  }

  static void finalizeImpl(long pData) {
    // TODO: This is native in OpenJDK AWT
  }

  private void setPData(long pData) {
    this.pData = pData;
    if (disposer == null) {
      disposer = new CursorDisposer(pData);
      // anchor is null after deserialization
      if (anchor == null) {
        anchor = new Object();
      }
      Disposer.addRecord(anchor, disposer);
    } else {
      disposer.pData = pData;
    }
  }

  /**
   * Returns the type for this cursor.
   */
  public int getType() {
    return type;
  }

  /**
   * Returns the name of this cursor.
   *
   * @return a localized description of this cursor.
   * @since 1.2
   */
  public String getName() {
    return name;
  }

  /**
   * Returns a string representation of this cursor.
   *
   * @return a string representation of this cursor.
   * @since 1.2
   */
  public String toString() {
    return getClass().getName() + "[" + getName() + "]";
  }

  static class CursorDisposer implements DisposerRecord {
    volatile long pData;

    public CursorDisposer(long pData) {
      this.pData = pData;
    }

    @Override
    public void dispose() {
      if (pData != 0) {
        finalizeImpl(pData);
      }
    }
  }
}
