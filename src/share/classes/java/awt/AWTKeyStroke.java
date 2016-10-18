/*
 * Copyright (c) 2000, 2013, Oracle and/or its affiliates. All rights reserved.
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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import sun.awt.AppContext;

/**
 * An {@code AWTKeyStroke} represents a key action on the
 * keyboard, or equivalent input device. {@code AWTKeyStroke}s
 * can correspond to only a press or release of a
 * particular key, just as {@code KEY_PRESSED} and
 * {@code KEY_RELEASED} {@code KeyEvent}s do;
 * alternately, they can correspond to typing a specific Java character, just
 * as {@code KEY_TYPED} {@code KeyEvent}s do.
 * In all cases, {@code AWTKeyStroke}s can specify modifiers
 * (alt, shift, control, meta, altGraph, or a combination thereof) which must be present
 * during the action for an exact match.
 * <p>
 * {@code AWTKeyStrokes} are immutable, and are intended
 * to be unique. Client code should never create an
 * {@code AWTKeyStroke} on its own, but should instead use
 * a variant of {@code getAWTKeyStroke}. Client use of these factory
 * methods allows the {@code AWTKeyStroke} implementation
 * to cache and share instances efficiently.
 *
 * @author Arnaud Weber
 * @author David Mendenhall
 * @see #getAWTKeyStroke
 * @since 1.4
 */
public class AWTKeyStroke implements Serializable {
  static final long serialVersionUID = -6430539691155161871L;
  //A key for the collection of AWTKeyStrokes within AppContext.
  private static final Object APP_CONTEXT_CACHE_KEY = new Object();
  //A key withing the cache
  private static final AWTKeyStroke APP_CONTEXT_KEYSTROKE_KEY = new AWTKeyStroke();
  private static Map<String, Integer> modifierKeywords;
  /**
   * Associates VK_XXX (as a String) with code (as Integer). This is
   * done to avoid the overhead of the reflective call to find the
   * constant.
   */
  private static VKCollection vks;

  private char keyChar = KeyEvent.CHAR_UNDEFINED;
  private int keyCode = KeyEvent.VK_UNDEFINED;
  private int modifiers;
  private boolean onKeyRelease;

  /**
   * Constructs an {@code AWTKeyStroke} with default values.
   * The default values used are:
   * <table border="1" summary="AWTKeyStroke default values">
   * <tr><th>Property</th><th>Default Value</th></tr>
   * <tr>
   * <td>Key Char</td>
   * <td>{@code KeyEvent.CHAR_UNDEFINED}</td>
   * </tr>
   * <tr>
   * <td>Key Code</td>
   * <td>{@code KeyEvent.VK_UNDEFINED}</td>
   * </tr>
   * <tr>
   * <td>Modifiers</td>
   * <td>none</td>
   * </tr>
   * <tr>
   * <td>On key release?</td>
   * <td>{@code false}</td>
   * </tr>
   * </table>
   * <p>
   * {@code AWTKeyStroke}s should not be constructed
   * by client code. Use a variant of {@code getAWTKeyStroke}
   * instead.
   *
   * @see #getAWTKeyStroke
   */
  protected AWTKeyStroke() {
  }

  /**
   * Constructs an {@code AWTKeyStroke} with the specified
   * values. {@code AWTKeyStroke}s should not be constructed
   * by client code. Use a variant of {@code getAWTKeyStroke}
   * instead.
   *
   * @param keyChar      the character value for a keyboard key
   * @param keyCode      the key code for this {@code AWTKeyStroke}
   * @param modifiers    a bitwise-ored combination of any modifiers
   * @param onKeyRelease {@code true} if this
   *                     {@code AWTKeyStroke} corresponds
   *                     to a key release; {@code false} otherwise
   * @see #getAWTKeyStroke
   */
  protected AWTKeyStroke(char keyChar, int keyCode, int modifiers, boolean onKeyRelease) {
    this.keyChar = keyChar;
    this.keyCode = keyCode;
    this.modifiers = modifiers;
    this.onKeyRelease = onKeyRelease;
  }

  /*
   * Reads keystroke class from AppContext and if null, puts there the
   * AWTKeyStroke class.
   * Must be called under locked AWTKeyStro
   */
  private static Class<AWTKeyStroke> getAWTKeyStrokeClass() {
    Class<AWTKeyStroke> clazz = (Class) AppContext.getAppContext().get(AWTKeyStroke.class);
    if (clazz == null) {
      clazz = AWTKeyStroke.class;
      AppContext.getAppContext().put(AWTKeyStroke.class, AWTKeyStroke.class);
    }
    return clazz;
  }

  /**
   * Registers a new class which the factory methods in
   * {@code AWTKeyStroke} will use when generating new
   * instances of {@code AWTKeyStroke}s. After invoking this
   * method, the factory methods will return instances of the specified
   * Class. The specified Class must be either {@code AWTKeyStroke}
   * or derived from {@code AWTKeyStroke}, and it must have a
   * no-arg constructor. The constructor can be of any accessibility,
   * including {@code private}. This operation
   * flushes the current {@code AWTKeyStroke} cache.
   *
   * @param subclass the new Class of which the factory methods should create
   *                 instances
   * @throws IllegalArgumentException if subclass is {@code null},
   *                                  or if subclass does not have a no-arg constructor
   * @throws ClassCastException       if subclass is not
   *                                  {@code AWTKeyStroke}, or a class derived from
   *                                  {@code AWTKeyStroke}
   */
  protected static void registerSubclass(Class<?> subclass) {
    if (subclass == null) {
      throw new IllegalArgumentException("subclass cannot be null");
    }
    synchronized (AWTKeyStroke.class) {
      Class<AWTKeyStroke> keyStrokeClass = (Class) AppContext
          .getAppContext()
          .get(AWTKeyStroke.class);
      if (keyStrokeClass != null && keyStrokeClass.equals(subclass)) {
        // Already registered
        return;
      }
    }
    if (!AWTKeyStroke.class.isAssignableFrom(subclass)) {
      throw new ClassCastException("subclass is not derived from AWTKeyStroke");
    }

    Constructor ctor = getCtor(subclass);

    String couldNotInstantiate = "subclass could not be instantiated";

    if (ctor == null) {
      throw new IllegalArgumentException(couldNotInstantiate);
    }
    try {
      AWTKeyStroke stroke = (AWTKeyStroke) ctor.newInstance((Object[]) null);
    } catch (NoSuchMethodError | InvocationTargetException | IllegalAccessException | InstantiationException | ExceptionInInitializerError e) {
      throw new IllegalArgumentException(couldNotInstantiate);
    }

    synchronized (AWTKeyStroke.class) {
      AppContext.getAppContext().put(AWTKeyStroke.class, subclass);
      AppContext.getAppContext().remove(APP_CONTEXT_CACHE_KEY);
      AppContext.getAppContext().remove(APP_CONTEXT_KEYSTROKE_KEY);
    }
  }

  /* returns noarg Constructor for class with accessible flag. No security
     threat as accessible flag is set only for this Constructor object,
     not for Class constructor.
   */
  private static Constructor getCtor(Class clazz) {
    return AccessController.doPrivileged(new PrivilegedAction<Constructor>() {
      @Override
      public Constructor run() {
        try {
          Constructor ctor1 = clazz.getDeclaredConstructor((Class[]) null);
          if (ctor1 != null) {
            ctor1.setAccessible(true);
          }
          return ctor1;
        } catch (SecurityException | NoSuchMethodException e) {
        }
        return null;
      }
    });
  }

  private static synchronized AWTKeyStroke getCachedStroke(
      char keyChar, int keyCode, int modifiers, boolean onKeyRelease) {
    Map<AWTKeyStroke, AWTKeyStroke> cache = (Map) AppContext
        .getAppContext()
        .get(APP_CONTEXT_CACHE_KEY);
    AWTKeyStroke cacheKey = (AWTKeyStroke) AppContext
        .getAppContext()
        .get(APP_CONTEXT_KEYSTROKE_KEY);

    if (cache == null) {
      cache = new HashMap<>();
      AppContext.getAppContext().put(APP_CONTEXT_CACHE_KEY, cache);
    }

    if (cacheKey == null) {
      try {
        Class<AWTKeyStroke> clazz = getAWTKeyStrokeClass();
        cacheKey = (AWTKeyStroke) getCtor(clazz).newInstance((Object[]) null);
        AppContext.getAppContext().put(APP_CONTEXT_KEYSTROKE_KEY, cacheKey);
      } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
        assert false;
      }
    }
    cacheKey.keyChar = keyChar;
    cacheKey.keyCode = keyCode;
    cacheKey.modifiers = mapNewModifiers(mapOldModifiers(modifiers));
    cacheKey.onKeyRelease = onKeyRelease;

    AWTKeyStroke stroke = cache.get(cacheKey);
    if (stroke == null) {
      stroke = cacheKey;
      cache.put(stroke, stroke);
      AppContext.getAppContext().remove(APP_CONTEXT_KEYSTROKE_KEY);
    }
    return stroke;
  }

  /**
   * Returns a shared instance of an {@code AWTKeyStroke}
   * that represents a {@code KEY_TYPED} event for the
   * specified character.
   *
   * @param keyChar the character value for a keyboard key
   * @return an {@code AWTKeyStroke} object for that key
   */
  public static AWTKeyStroke getAWTKeyStroke(char keyChar) {
    return getCachedStroke(keyChar, KeyEvent.VK_UNDEFINED, 0, false);
  }

  /**
   * Returns a shared instance of an {@code AWTKeyStroke}
   * that represents a {@code KEY_TYPED} event for the
   * specified Character object and a set of modifiers. Note
   * that the first parameter is of type Character rather than
   * char. This is to avoid inadvertent clashes with
   * calls to {@code getAWTKeyStroke(int keyCode, int modifiers)}.
   * <p>
   * The modifiers consist of any combination of following:<ul>
   * <li>java.awt.event.InputEvent.SHIFT_DOWN_MASK
   * <li>java.awt.event.InputEvent.CTRL_DOWN_MASK
   * <li>java.awt.event.InputEvent.META_DOWN_MASK
   * <li>java.awt.event.InputEvent.ALT_DOWN_MASK
   * <li>java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK
   * </ul>
   * The old modifiers listed below also can be used, but they are
   * mapped to _DOWN_ modifiers. <ul>
   * <li>java.awt.event.InputEvent.SHIFT_MASK
   * <li>java.awt.event.InputEvent.CTRL_MASK
   * <li>java.awt.event.InputEvent.META_MASK
   * <li>java.awt.event.InputEvent.ALT_MASK
   * <li>java.awt.event.InputEvent.ALT_GRAPH_MASK
   * </ul>
   * also can be used, but they are mapped to _DOWN_ modifiers.
   * <p>
   * Since these numbers are all different powers of two, any combination of
   * them is an integer in which each bit represents a different modifier
   * key. Use 0 to specify no modifiers.
   *
   * @param keyChar   the Character object for a keyboard character
   * @param modifiers a bitwise-ored combination of any modifiers
   * @return an {@code AWTKeyStroke} object for that key
   * @throws IllegalArgumentException if {@code keyChar} is
   *                                  {@code null}
   * @see InputEvent
   */
  public static AWTKeyStroke getAWTKeyStroke(Character keyChar, int modifiers) {
    if (keyChar == null) {
      throw new IllegalArgumentException("keyChar cannot be null");
    }
    return getCachedStroke(keyChar, KeyEvent.VK_UNDEFINED, modifiers, false);
  }

  /**
   * Returns a shared instance of an {@code AWTKeyStroke},
   * given a numeric key code and a set of modifiers, specifying
   * whether the key is activated when it is pressed or released.
   * <p>
   * The "virtual key" constants defined in
   * {@code java.awt.event.KeyEvent} can be
   * used to specify the key code. For example:<ul>
   * <li>{@code java.awt.event.KeyEvent.VK_ENTER}
   * <li>{@code java.awt.event.KeyEvent.VK_TAB}
   * <li>{@code java.awt.event.KeyEvent.VK_SPACE}
   * </ul>
   * Alternatively, the key code may be obtained by calling
   * {@code java.awt.event.KeyEvent.getExtendedKeyCodeForChar}.
   * <p>
   * The modifiers consist of any combination of:<ul>
   * <li>java.awt.event.InputEvent.SHIFT_DOWN_MASK
   * <li>java.awt.event.InputEvent.CTRL_DOWN_MASK
   * <li>java.awt.event.InputEvent.META_DOWN_MASK
   * <li>java.awt.event.InputEvent.ALT_DOWN_MASK
   * <li>java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK
   * </ul>
   * The old modifiers <ul>
   * <li>java.awt.event.InputEvent.SHIFT_MASK
   * <li>java.awt.event.InputEvent.CTRL_MASK
   * <li>java.awt.event.InputEvent.META_MASK
   * <li>java.awt.event.InputEvent.ALT_MASK
   * <li>java.awt.event.InputEvent.ALT_GRAPH_MASK
   * </ul>
   * also can be used, but they are mapped to _DOWN_ modifiers.
   * <p>
   * Since these numbers are all different powers of two, any combination of
   * them is an integer in which each bit represents a different modifier
   * key. Use 0 to specify no modifiers.
   *
   * @param keyCode      an int specifying the numeric code for a keyboard key
   * @param modifiers    a bitwise-ored combination of any modifiers
   * @param onKeyRelease {@code true} if the {@code AWTKeyStroke}
   *                     should represent a key release; {@code false} otherwise
   * @return an AWTKeyStroke object for that key
   * @see KeyEvent
   * @see InputEvent
   */
  public static AWTKeyStroke getAWTKeyStroke(int keyCode, int modifiers, boolean onKeyRelease) {
    return getCachedStroke(KeyEvent.CHAR_UNDEFINED, keyCode, modifiers, onKeyRelease);
  }

  /**
   * Returns a shared instance of an {@code AWTKeyStroke},
   * given a numeric key code and a set of modifiers. The returned
   * {@code AWTKeyStroke} will correspond to a key press.
   * <p>
   * The "virtual key" constants defined in
   * {@code java.awt.event.KeyEvent} can be
   * used to specify the key code. For example:<ul>
   * <li>{@code java.awt.event.KeyEvent.VK_ENTER}
   * <li>{@code java.awt.event.KeyEvent.VK_TAB}
   * <li>{@code java.awt.event.KeyEvent.VK_SPACE}
   * </ul>
   * The modifiers consist of any combination of:<ul>
   * <li>java.awt.event.InputEvent.SHIFT_DOWN_MASK
   * <li>java.awt.event.InputEvent.CTRL_DOWN_MASK
   * <li>java.awt.event.InputEvent.META_DOWN_MASK
   * <li>java.awt.event.InputEvent.ALT_DOWN_MASK
   * <li>java.awt.event.InputEvent.ALT_GRAPH_DOWN_MASK
   * </ul>
   * The old modifiers <ul>
   * <li>java.awt.event.InputEvent.SHIFT_MASK
   * <li>java.awt.event.InputEvent.CTRL_MASK
   * <li>java.awt.event.InputEvent.META_MASK
   * <li>java.awt.event.InputEvent.ALT_MASK
   * <li>java.awt.event.InputEvent.ALT_GRAPH_MASK
   * </ul>
   * also can be used, but they are mapped to _DOWN_ modifiers.
   * <p>
   * Since these numbers are all different powers of two, any combination of
   * them is an integer in which each bit represents a different modifier
   * key. Use 0 to specify no modifiers.
   *
   * @param keyCode   an int specifying the numeric code for a keyboard key
   * @param modifiers a bitwise-ored combination of any modifiers
   * @return an {@code AWTKeyStroke} object for that key
   * @see KeyEvent
   * @see InputEvent
   */
  public static AWTKeyStroke getAWTKeyStroke(int keyCode, int modifiers) {
    return getCachedStroke(KeyEvent.CHAR_UNDEFINED, keyCode, modifiers, false);
  }

  /**
   * Returns an {@code AWTKeyStroke} which represents the
   * stroke which generated a given {@code KeyEvent}.
   * <p>
   * This method obtains the keyChar from a {@code KeyTyped}
   * event, and the keyCode from a {@code KeyPressed} or
   * {@code KeyReleased} event. The {@code KeyEvent} modifiers are
   * obtained for all three types of {@code KeyEvent}.
   *
   * @param anEvent the {@code KeyEvent} from which to
   *                obtain the {@code AWTKeyStroke}
   * @return the {@code AWTKeyStroke} that precipitated the event
   * @throws NullPointerException if {@code anEvent} is null
   */
  public static AWTKeyStroke getAWTKeyStrokeForEvent(KeyEvent anEvent) {
    int id = anEvent.getID();
    switch (id) {
      case KeyEvent.KEY_PRESSED:
      case KeyEvent.KEY_RELEASED:
        return getCachedStroke(KeyEvent.CHAR_UNDEFINED,
            anEvent.getKeyCode(),
            anEvent.getModifiers(),
            id == KeyEvent.KEY_RELEASED);
      case KeyEvent.KEY_TYPED:
        return getCachedStroke(anEvent.getKeyChar(),
            KeyEvent.VK_UNDEFINED,
            anEvent.getModifiers(),
            false);
      default:
        // Invalid ID for this KeyEvent
        return null;
    }
  }

  /**
   * Parses a string and returns an {@code AWTKeyStroke}.
   * The string must have the following syntax:
   * <pre>
   *    &lt;modifiers&gt;* (&lt;typedID&gt; | &lt;pressedReleasedID&gt;)
   *
   *    modifiers := shift | control | ctrl | meta | alt | altGraph
   *    typedID := typed &lt;typedKey&gt;
   *    typedKey := string of length 1 giving Unicode character.
   *    pressedReleasedID := (pressed | released) key
   *    key := KeyEvent key code name, i.e. the name following "VK_".
   * </pre>
   * If typed, pressed or released is not specified, pressed is assumed. Here
   * are some examples:
   * <pre>
   *     "INSERT" =&gt; getAWTKeyStroke(KeyEvent.VK_INSERT, 0);
   *     "control DELETE" =&gt; getAWTKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK);
   *     "alt shift X" =&gt; getAWTKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK | InputEvent
   *     .SHIFT_MASK);
   *     "alt shift released X" =&gt; getAWTKeyStroke(KeyEvent.VK_X, InputEvent.ALT_MASK |
   *     InputEvent.SHIFT_MASK, true);
   *     "typed a" =&gt; getAWTKeyStroke('a');
   * </pre>
   *
   * @param s a String formatted as described above
   * @return an {@code AWTKeyStroke} object for that String
   * @throws IllegalArgumentException if {@code s} is {@code null},
   *                                  or is formatted incorrectly
   */
  public static AWTKeyStroke getAWTKeyStroke(String s) {
    if (s == null) {
      throw new IllegalArgumentException("String cannot be null");
    }

    String errmsg = "String formatted incorrectly";

    StringTokenizer st = new StringTokenizer(s, " ");

    int mask = 0;
    boolean released = false;
    boolean typed = false;
    boolean pressed = false;

    synchronized (AWTKeyStroke.class) {
      if (modifierKeywords == null) {
        Map<String, Integer> uninitializedMap = new HashMap<>(8, 1.0f);
        uninitializedMap.put("shift", InputEvent.SHIFT_DOWN_MASK | InputEvent.SHIFT_MASK);
        uninitializedMap.put("control", InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK);
        uninitializedMap.put("ctrl", InputEvent.CTRL_DOWN_MASK | InputEvent.CTRL_MASK);
        uninitializedMap.put("meta", InputEvent.META_DOWN_MASK | InputEvent.META_MASK);
        uninitializedMap.put("alt", InputEvent.ALT_DOWN_MASK | InputEvent.ALT_MASK);
        uninitializedMap.put("altGraph", InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.ALT_GRAPH_MASK);
        uninitializedMap.put("button1", InputEvent.BUTTON1_DOWN_MASK);
        uninitializedMap.put("button2", InputEvent.BUTTON2_DOWN_MASK);
        uninitializedMap.put("button3", InputEvent.BUTTON3_DOWN_MASK);
        modifierKeywords = Collections.synchronizedMap(uninitializedMap);
      }
    }

    int count = st.countTokens();

    for (int i = 1; i <= count; i++) {
      String token = st.nextToken();

      if (typed) {
        if (token.length() != 1 || i != count) {
          throw new IllegalArgumentException(errmsg);
        }
        return getCachedStroke(token.charAt(0), KeyEvent.VK_UNDEFINED, mask, false);
      }

      if (pressed || released || i == count) {
        if (i != count) {
          throw new IllegalArgumentException(errmsg);
        }

        String keyCodeName = "VK_" + token;
        int keyCode = getVKValue(keyCodeName);

        return getCachedStroke(KeyEvent.CHAR_UNDEFINED, keyCode, mask, released);
      }

      if ("released".equals(token)) {
        released = true;
        continue;
      }
      if ("pressed".equals(token)) {
        pressed = true;
        continue;
      }
      if ("typed".equals(token)) {
        typed = true;
        continue;
      }

      Integer tokenMask = modifierKeywords.get(token);
      if (tokenMask != null) {
        mask |= tokenMask;
      } else {
        throw new IllegalArgumentException(errmsg);
      }
    }

    throw new IllegalArgumentException(errmsg);
  }

  private static VKCollection getVKCollection() {
    if (vks == null) {
      vks = new VKCollection();
    }
    return vks;
  }

  /**
   * Returns the integer constant for the KeyEvent.VK field named
   * {@code key}. This will throw an
   * {@code IllegalArgumentException} if {@code key} is
   * not a valid constant.
   */
  private static int getVKValue(String key) {
    VKCollection vkCollect = getVKCollection();

    Integer value = vkCollect.findCode(key);

    if (value == null) {
      int keyCode;
      String errmsg = "String formatted incorrectly";

      try {
        keyCode = KeyEvent.class.getField(key).getInt(KeyEvent.class);
      } catch (NoSuchFieldException | IllegalAccessException nsfe) {
        throw new IllegalArgumentException(errmsg);
      }
      value = keyCode;
      vkCollect.put(key, value);
    }
    return value;
  }

  static String getModifiersText(int modifiers) {
    StringBuilder buf = new StringBuilder();

    if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
      buf.append("shift ");
    }
    if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
      buf.append("ctrl ");
    }
    if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
      buf.append("meta ");
    }
    if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
      buf.append("alt ");
    }
    if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
      buf.append("altGraph ");
    }
    if ((modifiers & InputEvent.BUTTON1_DOWN_MASK) != 0) {
      buf.append("button1 ");
    }
    if ((modifiers & InputEvent.BUTTON2_DOWN_MASK) != 0) {
      buf.append("button2 ");
    }
    if ((modifiers & InputEvent.BUTTON3_DOWN_MASK) != 0) {
      buf.append("button3 ");
    }

    return buf.toString();
  }

  static String getVKText(int keyCode) {
    VKCollection vkCollect = getVKCollection();
    Integer key = keyCode;
    String name = vkCollect.findName(key);
    if (name != null) {
      return name.substring(3);
    }
    int expected_modifiers = Modifier.PUBLIC | Modifier.STATIC | Modifier.FINAL;

    Field[] fields = KeyEvent.class.getDeclaredFields();
    for (Field field : fields) {
      try {
        if (field.getModifiers() == expected_modifiers && field.getType() == Integer.TYPE && field
            .getName()
            .startsWith("VK_") && field.getInt(KeyEvent.class) == keyCode) {
          name = field.getName();
          vkCollect.put(name, key);
          return name.substring(3);
        }
      } catch (IllegalAccessException e) {
        assert false;
      }
    }
    return "UNKNOWN";
  }

  private static int mapOldModifiers(int modifiers) {
    if ((modifiers & InputEvent.SHIFT_MASK) != 0) {
      modifiers |= InputEvent.SHIFT_DOWN_MASK;
    }
    if ((modifiers & InputEvent.ALT_MASK) != 0) {
      modifiers |= InputEvent.ALT_DOWN_MASK;
    }
    if ((modifiers & InputEvent.ALT_GRAPH_MASK) != 0) {
      modifiers |= InputEvent.ALT_GRAPH_DOWN_MASK;
    }
    if ((modifiers & InputEvent.CTRL_MASK) != 0) {
      modifiers |= InputEvent.CTRL_DOWN_MASK;
    }
    if ((modifiers & InputEvent.META_MASK) != 0) {
      modifiers |= InputEvent.META_DOWN_MASK;
    }

    modifiers &= InputEvent.SHIFT_DOWN_MASK | InputEvent.ALT_DOWN_MASK
        | InputEvent.ALT_GRAPH_DOWN_MASK | InputEvent.CTRL_DOWN_MASK | InputEvent.META_DOWN_MASK
        | InputEvent.BUTTON1_DOWN_MASK | InputEvent.BUTTON2_DOWN_MASK
        | InputEvent.BUTTON3_DOWN_MASK;

    return modifiers;
  }

  private static int mapNewModifiers(int modifiers) {
    if ((modifiers & InputEvent.SHIFT_DOWN_MASK) != 0) {
      modifiers |= InputEvent.SHIFT_MASK;
    }
    if ((modifiers & InputEvent.ALT_DOWN_MASK) != 0) {
      modifiers |= InputEvent.ALT_MASK;
    }
    if ((modifiers & InputEvent.ALT_GRAPH_DOWN_MASK) != 0) {
      modifiers |= InputEvent.ALT_GRAPH_MASK;
    }
    if ((modifiers & InputEvent.CTRL_DOWN_MASK) != 0) {
      modifiers |= InputEvent.CTRL_MASK;
    }
    if ((modifiers & InputEvent.META_DOWN_MASK) != 0) {
      modifiers |= InputEvent.META_MASK;
    }

    return modifiers;
  }

  /**
   * Returns the character for this {@code AWTKeyStroke}.
   *
   * @return a char value
   * @see #getAWTKeyStroke(char)
   * @see KeyEvent#getKeyChar
   */
  public final char getKeyChar() {
    return keyChar;
  }

  /**
   * Returns the numeric key code for this {@code AWTKeyStroke}.
   *
   * @return an int containing the key code value
   * @see #getAWTKeyStroke(int, int)
   * @see KeyEvent#getKeyCode
   */
  public final int getKeyCode() {
    return keyCode;
  }

  /**
   * Returns the modifier keys for this {@code AWTKeyStroke}.
   *
   * @return an int containing the modifiers
   * @see #getAWTKeyStroke(int, int)
   */
  public final int getModifiers() {
    return modifiers;
  }

  /**
   * Returns whether this {@code AWTKeyStroke} represents a key release.
   *
   * @return {@code true} if this {@code AWTKeyStroke}
   * represents a key release; {@code false} otherwise
   * @see #getAWTKeyStroke(int, int, boolean)
   */
  public final boolean isOnKeyRelease() {
    return onKeyRelease;
  }

  /**
   * Returns the type of {@code KeyEvent} which corresponds to
   * this {@code AWTKeyStroke}.
   *
   * @return {@code KeyEvent.KEY_PRESSED},
   * {@code KeyEvent.KEY_TYPED},
   * or {@code KeyEvent.KEY_RELEASED}
   * @see KeyEvent
   */
  public final int getKeyEventType() {
    if (keyCode == KeyEvent.VK_UNDEFINED) {
      return KeyEvent.KEY_TYPED;
    } else {
      return onKeyRelease ? KeyEvent.KEY_RELEASED : KeyEvent.KEY_PRESSED;
    }
  }

  /**
   * Returns a numeric value for this object that is likely to be unique,
   * making it a good choice as the index value in a hash table.
   *
   * @return an int that represents this object
   */
  @SuppressWarnings("NonFinalFieldReferencedInHashCode")
  public int hashCode() {
    return ((int) keyChar + 1) * 2 * (keyCode + 1) * (modifiers + 1) + (onKeyRelease ? 1 : 2);
  }

  /**
   * Returns true if this object is identical to the specified object.
   *
   * @param anObject the Object to compare this object to
   * @return true if the objects are identical
   */
  @SuppressWarnings("NonFinalFieldReferenceInEquals")
  public final boolean equals(Object anObject) {
    if (anObject instanceof AWTKeyStroke) {
      AWTKeyStroke ks = (AWTKeyStroke) anObject;
      return ks.keyChar == keyChar && ks.keyCode == keyCode &&
          ks.onKeyRelease == onKeyRelease &&
          ks.modifiers == modifiers;
    }
    return false;
  }

  /**
   * Returns a string that displays and identifies this object's properties.
   * The {@code String} returned by this method can be passed
   * as a parameter to {@code getAWTKeyStroke(String)} to produce
   * a key stroke equal to this key stroke.
   *
   * @return a String representation of this object
   * @see #getAWTKeyStroke(String)
   */
  public String toString() {
    return keyCode == KeyEvent.VK_UNDEFINED ? getModifiersText(modifiers) + "typed " + keyChar
        : getModifiersText(modifiers) +
            (onKeyRelease ? "released" : "pressed") + " " +
            getVKText(keyCode);
  }

  /**
   * Returns a cached instance of {@code AWTKeyStroke} (or a subclass of
   * {@code AWTKeyStroke}) which is equal to this instance.
   *
   * @return a cached instance which is equal to this instance
   */
  protected Object readResolve() throws ObjectStreamException {
    synchronized (AWTKeyStroke.class) {
      if (getClass().equals(getAWTKeyStrokeClass())) {
        return getCachedStroke(keyChar, keyCode, modifiers, onKeyRelease);
      }
    }
    return this;
  }
}

class VKCollection {
  final Map<Integer, String> code2name;
  final Map<String, Integer> name2code;

  public VKCollection() {
    code2name = new HashMap<>();
    name2code = new HashMap<>();
  }

  public synchronized void put(String name, Integer code) {
    assert name != null && code != null;
    assert findName(code) == null;
    assert findCode(name) == null;
    code2name.put(code, name);
    name2code.put(name, code);
  }

  public synchronized Integer findCode(String name) {
    assert name != null;
    return name2code.get(name);
  }

  public synchronized String findName(Integer code) {
    assert code != null;
    return code2name.get(code);
  }
}
