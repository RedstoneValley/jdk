/*
 * Copyright (c) 1996, 2014, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Dialog;
import java.awt.Font;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/**
 * Provides the definitions of the five logical fonts: Serif, SansSerif,
 * Monospaced, Dialog, and DialogInput. The necessary information
 * is obtained from fontconfig files.
 */
public abstract class FontConfiguration {

  /////////////////////////////////////////////////////////////////////
  // Methods for handling font and style names                       //
  /////////////////////////////////////////////////////////////////////
  protected static final int NUM_FONTS = 5;
  protected static final int NUM_STYLES = 4;
  protected static final String[] fontNames = {
      "serif", "sansserif", "monospaced", Dialog.base, "dialoginput"};
  protected static final String[] styleNames = {"plain", "bold", "italic", "bolditalic"};
  //utility "empty" objects
  static final int[] EMPTY_INT_ARRAY = new int[0];
  private static final String TAG = "sun.awt.FontConfig";
  private static final int INDEX_fallbackScripts = 15;
  //static global runtime env
  protected static String osVersion;
  protected static String osName;
  protected static String encoding; // canonical name of default nio charset
  protected static Locale startupLocale;

  /////////////////////////////////////////////////////////////////////
  // methods for initializing the FontConfig                         //
  /////////////////////////////////////////////////////////////////////
  protected static Hashtable localeMap;
  protected static boolean isProperties = true;
  //private static boolean loadingProperties;
  static short stringIDNum;
  static short[] stringIDs;
  static StringBuilder stringTable;
  static short[] head;
  static short[] table_scriptIDs;
  static short[] table_scriptFonts;
  static short[] table_elcIDs;
  static short[] table_sequences;
  static short[] table_fontfileNameIDs;
  static short[] table_componentFontNameIDs;
  static short[] table_filenames;
  static short[] table_exclusions;
  static short[] table_proportionals;
  static short[] table_scriptFontsMotif;
  static short[] table_alphabeticSuffix;
  static short[] table_stringIDs;
  static char[] table_stringTable;
  //runtime cache
  static String[] stringCache;
  private static FontConfiguration fontConfig;
  final Map<String, String> filenamesMap = new HashMap<>();
  final Set<String> coreFontFileNames = new HashSet<>();
  private final short[][][] compFontNameIDs = new short[NUM_FONTS][NUM_STYLES][];
  private final int[][][] compExclusions = new int[NUM_FONTS][][];
  private final int[] compCoreNum = new int[NUM_FONTS];
  private final Set<Short> coreFontNameIDs = new HashSet<>();
  private final Set<Short> fallbackFontNameIDs = new HashSet<>();
  ////////////////////////////////////////////////////////////////////////
  // Methods for extracting information from the fontconfig data for AWT//
  ////////////////////////////////////////////////////////////////////////
  private final Hashtable charsetRegistry = new Hashtable(5);
  private final FontDescriptor[][][] fontDescriptors = new FontDescriptor[NUM_FONTS][NUM_STYLES][];
  protected boolean preferLocaleFonts;
  protected boolean preferPropFonts;
  /* Mappings from file encoding to font config name for font supporting
   * the corresponding language. This is filled in by initReorderMap()
   */
  protected HashMap reorderMap;
  /* Used on Linux to test if a file referenced in a font configuration
   * file exists in the location that is expected. If it does, no need
   * to search for it. If it doesn't then unless its a fallback font,
   * return that expensive code should be invoked to search for the font.
   */ HashMap<String, Boolean> existsMap;
  private File fontConfigFile;
  private boolean foundOsSpecificFile;
  private boolean inited;
  private String javaLib;
  private short initELC = -1;
  private Locale initLocale;
  private String initEncoding;
  //////////////////////////////////////////////////////////////////////
  //  reordering                                                      //
  //////////////////////////////////////////////////////////////////////
  private String alphabeticSuffix;
  private int numCoreFonts = -1;
  private String[] componentFonts;
  private HashMap<String, Short> reorderScripts;

  /* A default FontConfiguration must be created before an alternate
   * one to ensure proper static initialisation takes place.
   */
  public FontConfiguration() {
    Log.i(TAG, "Creating standard Font Configuration");
    setOsNameAndVersion();  /* static initialization */
    setEncoding();          /* static initialization */
        /* Separating out the file location from the rest of the
         * initialisation, so the caller has the option of doing
         * something else if a suitable file isn't found.
         */
    findFontConfigFile();
  }

  public FontConfiguration(boolean preferLocaleFonts, boolean preferPropFonts) {
    Log.i(TAG, "Creating alternate Font Configuration");
    this.preferLocaleFonts = preferLocaleFonts;
    this.preferPropFonts = preferPropFonts;
        /* fontConfig should be initialised by default constructor, and
         * its data tables can be shared, since readFontConfigFile doesn't
         * update any other state. Also avoid a doPrivileged block.
         */
    initFontConfig();
  }

  /**
   * Checks whether the given font family name is a valid logical font name.
   * The check is case insensitive.
   */
  public static boolean isLogicalFontFamilyName(String fontName) {
    return isLogicalFontFamilyNameLC(fontName.toLowerCase(Locale.ENGLISH));
  }

  ////////////////////////////////////////////////////////////////////////
  // methods for extracting information from the fontconfig data for 2D //
  ////////////////////////////////////////////////////////////////////////

  /**
   * Checks whether the given font family name is a valid logical font name.
   * The check is case sensitive.
   */
  public static boolean isLogicalFontFamilyNameLC(String fontName) {
    for (String fontName1 : fontNames) {
      if (fontName.equals(fontName1)) {
        return true;
      }
    }
    return false;
  }

  protected static int getFontIndex(String fontName) {
    return getArrayIndex(fontNames, fontName);
  }

  private static int getArrayIndex(String[] names, String name) {
    for (int i = 0; i < names.length; i++) {
      if (name.equals(names[i])) {
        return i;
      }
    }
    assert false;
    return 0;
  }

  protected static int getStyleIndex(int style) {
    switch (style) {
      case Font.PLAIN:
        return 0;
      case Font.BOLD:
        return 1;
      case Font.ITALIC:
        return 2;
      case Font.BOLD | Font.ITALIC:
        return 3;
      default:
        return 0;
    }
  }

  /* Called to determine if there's a re-order sequence for this locale/
   * encoding. If there's none then the caller can "bail" and avoid
   * unnecessary work
   */
  public static boolean willReorderForStartupLocale() {
    return getReorderSequence() != null;
  }

  private static Object getReorderSequence() {
    if (fontConfig.reorderMap == null) {
      fontConfig.initReorderMap();
    }
    HashMap reorderMap = fontConfig.reorderMap;

        /* Find the most specific mapping */
    String language = startupLocale.getLanguage();
    String country = startupLocale.getCountry();
    Object val = reorderMap.get(encoding + "." + language + "." + country);
    if (val == null) {
      val = reorderMap.get(encoding + "." + language);
    }
    if (val == null) {
      val = reorderMap.get(encoding);
    }
    return val;
  }

  static Vector splitSequence(String sequence) {
    //String.split would be more convenient, but incurs big performance penalty
    Vector parts = new Vector();
    int start = 0;
    int end;
    while ((end = sequence.indexOf(',', start)) >= 0) {
      parts.add(sequence.substring(start, end));
      start = end + 1;
    }
    if (sequence.length() > start) {
      parts.add(sequence.substring(start, sequence.length()));
    }
    return parts;
  }

  /* Return the fontID of the platformFontName defined in this font config
   * by "LogicalFontName.StyleName.CharacterSubsetName" entry or
   * "allfonts.CharacterSubsetName" entry in properties format fc file.
   */
  protected static short getComponentFontID(short scriptID, int fontIndex, int styleIndex) {
    short fid = table_scriptFonts[scriptID];
    //System.out.println("fid=" + fid + "/ scriptID=" + scriptID + ", fi=" + fontIndex + ", si="
    // + styleIndex);
    return fid >= 0 ? fid : table_scriptFonts[-fid + fontIndex * NUM_STYLES + styleIndex];
  }

  private static int[] getExclusionRanges(short scriptID) {
    short exID = table_exclusions[scriptID];
    if (exID == 0) {
      return EMPTY_INT_ARRAY;
    } else {
      char[] exChar = getString(exID).toCharArray();
      int[] exInt = new int[exChar.length / 2];
      int i = 0;
      for (int j = 0; j < exInt.length; j++) {
        exInt[j] = (exChar[i] << 16) + (exChar[i] & 0xffff);
        i++;
        i++;
      }
      return exInt;
    }
  }

  private static boolean contains(short[] IDs, short id, int limit) {
    for (int i = 0; i < limit; i++) {
      if (IDs[i] == id) {
        return true;
      }
    }
    return false;
  }

  /* Return the PlatformFontName from its fontID*/
  protected static String getComponentFontName(short id) {
    if (id < 0) {
      return null;
    }
    return getString(table_componentFontNameIDs[id]);
  }

  private static String getScriptName(short scriptID) {
    return getString(table_scriptIDs[scriptID]);
  }

  private static short[] getFallbackScripts() {
    return getShortArray(head[INDEX_fallbackScripts]);
  }

  static short[] toList(Map<String, Short> map) {
    short[] list = new short[map.size()];
    Arrays.fill(list, (short) -1);
    for (Entry<String, Short> entry : map.entrySet()) {
      list[entry.getValue()] = getStringID(entry.getKey());
    }
    return list;
  }

  protected static String getString(short stringID) {
    if (stringID == 0) {
      return null;
    }
        /*
        if (loadingProperties) {
            return stringTable.substring(stringIDs[stringID],
                                         stringIDs[stringID+1]);
        }
        */
    //sync if we want it to be MT-enabled
    if (stringCache[stringID] == null) {
      stringCache[stringID] = new String(table_stringTable,
          table_stringIDs[stringID],
          table_stringIDs[stringID + 1] - table_stringIDs[stringID]);
    }
    return stringCache[stringID];
  }

  private static short[] getShortArray(short shortArrayID) {
    String s = getString(shortArrayID);
    char[] cc = s.toCharArray();
    short[] ss = new short[cc.length];
    for (int i = 0; i < cc.length; i++) {
      ss[i] = (short) (cc[i] & 0xffff);
    }
    return ss;
  }

  static short getStringID(String s) {
    if (s == null) {
      return (short) 0;
    }
    short pos0 = (short) stringTable.length();
    stringTable.append(s);
    short pos1 = (short) stringTable.length();

    stringIDs[stringIDNum] = pos0;
    stringIDs[stringIDNum + 1] = pos1;
    stringIDNum++;
    if (stringIDNum + 1 >= stringIDs.length) {
      short[] tmp = new short[stringIDNum + 1000];
      System.arraycopy(stringIDs, 0, tmp, 0, stringIDNum);
      stringIDs = tmp;
    }
    return (short) (stringIDNum - 1);
  }

  static short getShortArrayID(short[] sa) {
    char[] cc = new char[sa.length];
    for (int i = 0; i < sa.length; i++) {
      cc[i] = (char) sa[i];
    }
    String s = new String(cc);
    return getStringID(s);
  }

  /**
   * Fills in this instance's osVersion and osName members. By
   * default uses the system properties os.name and os.version;
   * subclasses may override.
   */
  protected void setOsNameAndVersion() {
    osName = System.getProperty("os.name");
    osVersion = System.getProperty(OSInfo.OS_VERSION);
  }

  private void setEncoding() {
    encoding = Charset.defaultCharset().name();
    startupLocale = SunToolkit.getStartupLocale();
  }

  private void findFontConfigFile() {

    foundOsSpecificFile = true; // default assumption.
    String javaHome = System.getProperty("java.home");
    if (javaHome == null) {
      throw new Error("java.home property not set");
    }
    javaLib = javaHome + File.separator + "lib";
    String userConfigFile = System.getProperty("sun.awt.fontconfig");
    fontConfigFile = userConfigFile != null ? new File(userConfigFile)
        : findFontConfigFile(javaLib);
  }

  private File findImpl(String fname) {
    File f = new File(fname + ".properties");
    if (f.canRead()) {
      isProperties = true;
      return f;
    }
    f = new File(fname + ".bfc");
    if (f.canRead()) {
      isProperties = false;
      return f;
    }
    return null;
  }

  private File findFontConfigFile(String javaLib) {
    String baseName = javaLib + File.separator + "fontconfig";
    File configFile;
    String osMajorVersion = null;
    if (osVersion != null && osName != null) {
      configFile = findImpl(baseName + "." + osName + "." + osVersion);
      if (configFile != null) {
        return configFile;
      }
      int decimalPointIndex = osVersion.indexOf(".");
      if (decimalPointIndex != -1) {
        osMajorVersion = osVersion.substring(0, osVersion.indexOf("."));
        configFile = findImpl(baseName + "." + osName + "." + osMajorVersion);
        if (configFile != null) {
          return configFile;
        }
      }
    }
    if (osName != null) {
      configFile = findImpl(baseName + "." + osName);
      if (configFile != null) {
        return configFile;
      }
    }
    if (osVersion != null) {
      configFile = findImpl(baseName + "." + osVersion);
      if (configFile != null) {
        return configFile;
      }
      if (osMajorVersion != null) {
        configFile = findImpl(baseName + "." + osMajorVersion);
        if (configFile != null) {
          return configFile;
        }
      }
    }
    foundOsSpecificFile = false;

    configFile = findImpl(baseName);
    if (configFile != null) {
      return configFile;
    }
    return null;
  }

  /**
   * set initLocale, initEncoding and initELC for this FontConfig object
   * currently we just simply use the startup locale and encoding
   */
  private void initFontConfig() {
    initLocale = startupLocale;
    initEncoding = encoding;
    if (preferLocaleFonts && !willReorderForStartupLocale()) {
      preferLocaleFonts = false;
    }
    initELC = getInitELC();
    initAllComponentFonts();
  }

  //"ELC" stands for "Encoding.Language.Country". This method returns
  //the ID of the matched elc setting of "initLocale" in elcIDs table.
  //If no match is found, it returns the default ID, which is
  //"NULL.NULL.NULL" in elcIDs table.
  private short getInitELC() {
    if (initELC != -1) {
      return initELC;
    }
    HashMap<String, Integer> elcIDs = new HashMap<>();
    for (int i = 0; i < table_elcIDs.length; i++) {
      elcIDs.put(getString(table_elcIDs[i]), i);
    }
    String language = initLocale.getLanguage();
    String country = initLocale.getCountry();
    String elc;
    initELC = elcIDs.containsKey(elc = initEncoding + "." + language + "." + country)
        || elcIDs.containsKey(elc = initEncoding + "." + language) || elcIDs.containsKey(
        elc = initEncoding) ? elcIDs.get(elc).shortValue()
        : elcIDs.get("NULL.NULL.NULL").shortValue();
    int i = 0;
    while (i < table_alphabeticSuffix.length) {
      if (initELC == table_alphabeticSuffix[i]) {
        alphabeticSuffix = getString(table_alphabeticSuffix[i + 1]);
        return initELC;
      }
      i += 2;
    }
    return initELC;
  }

  private void initAllComponentFonts() {
    short[] fallbackScripts = getFallbackScripts();
    for (int fontIndex = 0; fontIndex < NUM_FONTS; fontIndex++) {
      short[] coreScripts = getCoreScripts(fontIndex);
      compCoreNum[fontIndex] = coreScripts.length;
            /*
            System.out.println("coreScriptID=" + table_sequences[initELC * 5 + fontIndex]);
            for (int i = 0; i < coreScripts.length; i++) {
            System.out.println("  " + i + " :" + getString(table_scriptIDs[coreScripts[i]]));
            }
            */
      //init exclusionRanges
      int[][] exclusions = new int[coreScripts.length][];
      for (int i = 0; i < coreScripts.length; i++) {
        exclusions[i] = getExclusionRanges(coreScripts[i]);
      }
      compExclusions[fontIndex] = exclusions;
      //init componentFontNames
      for (int styleIndex = 0; styleIndex < NUM_STYLES; styleIndex++) {
        int index;
        short[] nameIDs = new short[coreScripts.length + fallbackScripts.length];
        //core
        for (index = 0; index < coreScripts.length; index++) {
          nameIDs[index] = getComponentFontID(coreScripts[index], fontIndex, styleIndex);
          if (preferLocaleFonts && localeMap != null &&
              false) {
            nameIDs[index] = remapLocaleMap(fontIndex,
                styleIndex,
                coreScripts[index],
                nameIDs[index]);
          }
          if (preferPropFonts) {
            nameIDs[index] = remapProportional(fontIndex, nameIDs[index]);
          }
          //System.out.println("nameid=" + nameIDs[index]);
          coreFontNameIDs.add(nameIDs[index]);
        }
        //fallback
        for (short fallbackScript : fallbackScripts) {
          short id = getComponentFontID(fallbackScript, fontIndex, styleIndex);
          if (preferLocaleFonts && localeMap != null &&
              false) {
            id = remapLocaleMap(fontIndex, styleIndex, fallbackScript, id);
          }
          if (preferPropFonts) {
            id = remapProportional(fontIndex, id);
          }
          if (contains(nameIDs, id, index)) {
            continue;
          }
                    /*
                      System.out.println("fontIndex=" + fontIndex + ", styleIndex=" + styleIndex
                           + ", fbIndex=" + i + ",fbS=" + fallbackScripts[i] + ", id=" + id);
                    */
          fallbackFontNameIDs.add(id);
          nameIDs[index] = id;
          index++;
        }
        if (index < nameIDs.length) {
          short[] newNameIDs = new short[index];
          System.arraycopy(nameIDs, 0, newNameIDs, 0, index);
          nameIDs = newNameIDs;
        }
        compFontNameIDs[fontIndex][styleIndex] = nameIDs;
      }
    }
  }

  private short remapLocaleMap(int fontIndex, int styleIndex, short scriptID, short fontID) {
    String scriptName = getString(table_scriptIDs[scriptID]);

    String value = (String) localeMap.get(scriptName);
    if (value == null) {
      String fontName = fontNames[fontIndex];
      String styleName = styleNames[styleIndex];
      value = (String) localeMap.get(fontName + "." + styleName + "." + scriptName);
    }
    if (value == null) {
      return fontID;
    }

    for (int i = 0; i < table_componentFontNameIDs.length; i++) {
      String name = getString(table_componentFontNameIDs[i]);
      if (value.equalsIgnoreCase(name)) {
        fontID = (short) i;
        break;
      }
    }
    return fontID;
  }

  private short remapProportional(int fontIndex, short id) {
    if (preferPropFonts &&
        table_proportionals.length != 0 &&
        fontIndex != 2 &&         //"monospaced"
        fontIndex != 4) {         //"dialoginput"
      int i = 0;
      while (i < table_proportionals.length) {
        if (table_proportionals[i] == id) {
          return table_proportionals[i + 1];
        }
        i += 2;
      }
    }
    return id;
  }

  /**
   * Returns a fallback name for the given font name. For a few known
   * font names, matching logical font names are returned. For all
   * other font names, defaultFallback is returned.
   * defaultFallback differs between AWT and 2D.
   */
  public abstract String getFallbackFamilyName(String fontName, String defaultFallback);

  //////////////////////////////////////////////////////////////////////
  // Data table access methods                                        //
  //////////////////////////////////////////////////////////////////////

  /* Platform-specific mappings */
  protected abstract void initReorderMap();

  /* Move item at index "src" to "dst", shuffling all values in
   * between down
   */
  private void shuffle(String[] seq, int src, int dst) {
    if (dst >= src) {
      return;
    }
    String tmp = seq[src];
    System.arraycopy(seq, dst, seq, dst + 1, src - dst);
    seq[dst] = tmp;
  }

  /* This method reorders the sequence such that the matches for the
   * file encoding are moved ahead of other elements.
   * If an encoding uses more than one font, they are all moved up.
   */
  private void reorderSequenceForLocale(String[] seq) {
    Object val = getReorderSequence();
    if (val instanceof String) {
      for (int i = 0; i < seq.length; i++) {
        if (seq[i].equals(val)) {
          shuffle(seq, i, 0);
          return;
        }
      }
    } else if (val instanceof String[]) {
      String[] fontLangs = (String[]) val;
      for (int l = 0; l < fontLangs.length; l++) {
        for (int i = 0; i < seq.length; i++) {
          if (seq[i].equals(fontLangs[l])) {
            shuffle(seq, i, l);
          }
        }
      }
    }
  }

  protected String[] split(String sequence) {
    Vector v = splitSequence(sequence);
    return (String[]) v.toArray(new String[0]);
  }

  /**
   * Returns FontDescriptors describing the physical fonts used for the
   * given logical font name and style. The font name is interpreted
   * in a case insensitive way.
   * The style argument is interpreted as in java.awt.Font.Font.
   */
  public FontDescriptor[] getFontDescriptors(String fontName, int style) {
    assert isLogicalFontFamilyName(fontName);
    fontName = fontName.toLowerCase(Locale.ENGLISH);
    int fontIndex = getFontIndex(fontName);
    int styleIndex = getStyleIndex(style);
    return getFontDescriptors(fontIndex, styleIndex);
  }

  private FontDescriptor[] getFontDescriptors(int fontIndex, int styleIndex) {
    FontDescriptor[] descriptors = fontDescriptors[fontIndex][styleIndex];
    if (descriptors == null) {
      descriptors = buildFontDescriptors(fontIndex, styleIndex);
      fontDescriptors[fontIndex][styleIndex] = descriptors;
    }
    return descriptors;
  }

  protected FontDescriptor[] buildFontDescriptors(int fontIndex, int styleIndex) {
    String fontName = fontNames[fontIndex];
    String styleName = styleNames[styleIndex];

    short[] scriptIDs = getCoreScripts(fontIndex);
    short[] nameIDs = compFontNameIDs[fontIndex][styleIndex];
    String[] sequence = new String[scriptIDs.length];
    String[] names = new String[scriptIDs.length];
    for (int i = 0; i < sequence.length; i++) {
      names[i] = getComponentFontName(nameIDs[i]);
      sequence[i] = getScriptName(scriptIDs[i]);
      if (alphabeticSuffix != null && "alphabetic".equals(sequence[i])) {
        sequence[i] = sequence[i] + "/" + alphabeticSuffix;
      }
    }
    int[][] fontExclusionRanges = compExclusions[fontIndex];

    FontDescriptor[] descriptors = new FontDescriptor[names.length];

    for (int i = 0; i < names.length; i++) {
      String awtFontName;
      String encoding;

      awtFontName = makeAWTFontName(names[i], sequence[i]);

      // look up character encoding
      encoding = getEncoding(names[i], sequence[i]);
      if (encoding == null) {
        encoding = "default";
      }
      CharsetEncoder enc = getFontCharsetEncoder(encoding.trim(), awtFontName);

      // we already have the exclusion ranges
      int[] exclusionRanges = fontExclusionRanges[i];

      // create descriptor
      descriptors[i] = new FontDescriptor(awtFontName, enc, exclusionRanges);
    }
    return descriptors;
  }

  /**
   * Returns the AWT font name for the given platform font name and
   * character subset.
   */
  protected String makeAWTFontName(String platformFontName, String characterSubsetName) {
    return platformFontName;
  }

  /**
   * Returns the java.io name of the platform character encoding for the
   * given AWT font name and character subset. May return "default"
   * to indicate that getDefaultFontCharset should be called to obtain
   * a charset encoder.
   */
  protected abstract String getEncoding(String awtFontName, String characterSubsetName);

  private CharsetEncoder getFontCharsetEncoder(String charsetName, String fontName) {

    Charset fc;
    fc = "default".equals(charsetName) ? (Charset) charsetRegistry.get(fontName)
        : (Charset) charsetRegistry.get(charsetName);
    if (fc != null) {
      return fc.newEncoder();
    }

    if (!charsetName.startsWith("sun.awt.") && !"default".equals(charsetName)) {
      fc = Charset.forName(charsetName);
    } else {
      Class fcc = (Class) AccessController.doPrivileged(new PrivilegedAction() {
        @Override
        public Object run() {
          try {
            return Class.forName(charsetName, true, ClassLoader.getSystemClassLoader());
          } catch (ClassNotFoundException e) {
          }
          return null;
        }
      });

      if (fcc != null) {
        try {
          fc = (Charset) fcc.newInstance();
        } catch (Exception e) {
        }
      }
    }
    if (fc == null) {
      fc = getDefaultFontCharset(fontName);
    }

    if ("default".equals(charsetName)) {
      charsetRegistry.put(fontName, fc);
    } else {
      charsetRegistry.put(charsetName, fc);
    }
    return fc.newEncoder();
  }

  protected abstract Charset getDefaultFontCharset(
      String fontName);

  protected short[] getCoreScripts(int fontIndex) {
    short elc = getInitELC();
        /*
          System.out.println("getCoreScripts: elc=" + elc + ", fontIndex=" + fontIndex);
          short[] ss = getShortArray(table_sequences[elc * NUM_FONTS + fontIndex]);
          for (int i = 0; i < ss.length; i++) {
              System.out.println("     " + getString((short)table_scriptIDs[ss[i]]));
          }
          */
    short[] scripts = getShortArray(table_sequences[elc * NUM_FONTS + fontIndex]);
    if (preferLocaleFonts) {
      if (reorderScripts == null) {
        reorderScripts = new HashMap<>();
      }
      String[] ss = new String[scripts.length];
      for (int i = 0; i < ss.length; i++) {
        ss[i] = getScriptName(scripts[i]);
        reorderScripts.put(ss[i], scripts[i]);
      }
      reorderSequenceForLocale(ss);
      for (int i = 0; i < ss.length; i++) {
        scripts[i] = reorderScripts.get(ss[i]);
      }
    }
    return scripts;
  }
}
