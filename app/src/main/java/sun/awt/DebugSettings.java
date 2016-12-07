/*
 * Copyright (c) 1999, 2013, Oracle and/or its affiliates. All rights reserved.
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

package sun.awt;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StringBufferInputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

/*
 * Internal class that manages sun.awt.Debug settings.
 * Settings can be specified on a global, per-package,
 * or per-class level.
 *
 * Properties affecting the behaviour of the Debug class are
 * loaded from the awtdebug.properties file at class load
 * time. The properties file is assumed to be in the
 * user.home directory. A different file can be used
 * by setting the awtdebug.properties system property.
 *      e.g. java -Dawtdebug.properties=foo.properties
 *
 * Only properties beginning with 'awtdebug' have any
 * meaning-- all other properties are ignored.
 *
 * You can override the properties file by specifying
 * 'awtdebug' props as system properties on the command line.
 *      e.g. java -Dawtdebug.trace=true
 * Properties specific to a package or a class can be set
 * by qualifying the property names as follows:
 *      awtdebug.<property name>.<class or package name>
 * So for example, turning on tracing in the com.acme.Fubar
 * class would be done as follows:
 *      awtdebug.trace.com.acme.Fubar=true
 *
 * Class settings always override package settings, which in
 * turn override global settings.
 *
 * Addition from July, 2007.
 *
 * After the fix for 4638447 all the usage of DebugHelper
 * classes in Java code are replaced with the corresponding
 * Java Logging API calls. This file is now used only to
 * control native logging.
 *
 * To enable native logging you should set the following
 * system property to 'true': sun.awt.nativedebug. After
 * the native logging is enabled, the actual debug settings
 * are read the same way as described above (as before
 * the fix for 4638447).
 */
final class DebugSettings {
  /* standard debug property key names */
  static final String PREFIX = "awtdebug";
  static final String PROP_FILE = "properties";
  private static final String TAG = "AWT DebugSettings";
  /* default property settings */
  private static final String[] DEFAULT_PROPS = {
      "awtdebug.assert=true", "awtdebug.trace=false", "awtdebug.on=true", "awtdebug.ctrace=false"};
  private static final String PROP_CTRACE = "ctrace";
  private static final int PROP_CTRACE_LEN = PROP_CTRACE.length();
  /* global instance of the settings object */
  private static DebugSettings instance;
  private final Properties props = new Properties();

  private DebugSettings() {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      @Override
      public Void run() {
        loadProperties();
        return null;
      }
    });
  }

  static void init() {
    if (instance != null) {
      return;
    }

    NativeLibLoader.loadLibraries();
    instance = new DebugSettings();
  }

  /*
   * Load debug properties from file, then override
   * with any command line specified properties
   */
  synchronized void loadProperties() {
    // setup initial properties
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      @Override
      public Void run() {
        loadDefaultProperties();
        loadFileProperties();
        loadSystemProperties();
        return null;
      }
    });

    // echo the initial property settings to stdout
    Log.v(TAG, "DebugSettings:\n" + this);
  }

  public String toString() {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    PrintStream pout = new PrintStream(bout);
    for (String key : props.stringPropertyNames()) {
      String value = props.getProperty(key, "");
      pout.println(key + " = " + value);
    }
    return new String(bout.toByteArray());
  }

  /*
   * Sets up default property values
   */
  void loadDefaultProperties() {
    // is there a more inefficient way to setup default properties?
    // maybe, but this has got to be close to 100% non-optimal
    try {
      for (String DEFAULT_PROP : DEFAULT_PROPS) {
        InputStream in = new StringBufferInputStream(DEFAULT_PROP);
        props.load(in);
        in.close();
      }
    } catch (IOException ioe) {
    }
  }

  /*
   * load properties from file, overriding defaults
   */
  void loadFileProperties() {
    String propPath;
    Properties fileProps;

    // check if the user specified a particular settings file
    propPath = System.getProperty(PREFIX + "." + PROP_FILE, "");
    if ("".equals(propPath)) {
      // otherwise get it from the user's home directory
      propPath = System.getProperty("user.home", "") +
          File.separator +
          PREFIX + "." + PROP_FILE;
    }

    File propFile = new File(propPath);
    try {
      Log.v(TAG, "Reading debug settings from '" + propFile.getCanonicalPath() + "'...");

      FileInputStream fin = new FileInputStream(propFile);
      props.load(fin);
      fin.close();
    } catch (FileNotFoundException fne) {
      Log.v(TAG, "Did not find settings file.");
    } catch (IOException ioe) {
      Log.v(TAG, "Problem reading settings", ioe);
    }
  }

  /*
   * load properties from system props (command line spec'd usually),
   * overriding default or file properties
   */
  void loadSystemProperties() {
    // override file properties with system properties
    Properties sysProps = System.getProperties();
    for (String key : sysProps.stringPropertyNames()) {
      String value = sysProps.getProperty(key, "");
      // copy any "awtdebug" properties over
      if (key.startsWith(PREFIX)) {
        props.setProperty(key, value);
      }
    }
  }

  /**
   * Gets named boolean property
   *
   * @param key Name of property
   * @param defval Default value if property does not exist
   * @return boolean value of the named property
   */
  public synchronized boolean getBoolean(String key, boolean defval) {
    String value = getString(key, String.valueOf(defval));
    return "true".equalsIgnoreCase(value);
  }

  /**
   * Gets named integer property
   *
   * @param key Name of property
   * @param defval Default value if property does not exist
   * @return integer value of the named property
   */
  public synchronized int getInt(String key, int defval) {
    String value = getString(key, String.valueOf(defval));
    return Integer.parseInt(value);
  }

  /**
   * Gets named String property
   *
   * @param key Name of property
   * @param defval Default value if property does not exist
   * @return string value of the named property
   */
  public synchronized String getString(String key, String defval) {
    String actualKeyName = PREFIX + "." + key;
    //println(actualKeyName+"="+value);
    return props.getProperty(actualKeyName, defval);
  }
}
