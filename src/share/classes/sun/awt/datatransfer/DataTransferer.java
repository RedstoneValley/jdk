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

package sun.awt.datatransfer;

import android.util.Log;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.SystemFlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Provides a set of functions to be shared among the DataFlavor class and
 * platform-specific data transfer implementations.
 * <p>
 * The concept of "flavors" and "natives" is extended to include "formats",
 * which are the numeric values Win32 and X11 use to express particular data
 * types. Like FlavorMap, which provides getNativesForFlavors(DataFlavor[]) and
 * getFlavorsForNatives(String[]) functions, DataTransferer provides a set
 * of getFormatsFor(Transferable|Flavor|Flavors) and
 * getFlavorsFor(Format|Formats) functions.
 * <p>
 * Also provided are functions for translating a Transferable into a byte
 * array, given a source DataFlavor and a target format, and for translating
 * a byte array or InputStream into an Object, given a source format and
 * a target DataFlavor.
 *
 * @author David Mendenhall
 * @author Danila Sinopalnikov
 * @since 1.3.1
 */
public abstract class DataTransferer {

  /**
   * The {@code DataFlavor} representing plain text with Unicode
   * encoding, where:
   * <pre>
   *     representationClass = java.lang.String
   *     mimeType            = "text/plain; charset=Unicode"
   * </pre>
   */
  public static final DataFlavor plainTextStringFlavor;

  /**
   * The {@code DataFlavor} representing a Java text encoding String
   * encoded in UTF-8, where
   * <pre>
   *     representationClass = [B
   *     mimeType            = "application/x-java-text-encoding"
   * </pre>
   */
  public static final DataFlavor javaTextEncodingFlavor;
  /**
   * The end-of-line markers for the Set of textNatives.
   */
  static final Map nativeEOLNs = Collections.synchronizedMap(new HashMap());
  /**
   * The number of terminating NUL bytes for the Set of textNatives.
   */
  static final Map nativeTerminators = Collections.synchronizedMap(new HashMap());
  /**
   * Tracks whether a particular text/* MIME type supports the charset
   * parameter. The Map is initialized with all of the standard MIME types
   * listed in the DataFlavor.selectBestTextFlavor method comment. Additional
   * entries may be added during the life of the JRE for text/<other> types.
   */
  private static final Map textMIMESubtypeCharsetSupport;
  /**
   * A collection of all natives listed in flavormap.properties with
   * a primary MIME type of "text".
   */
  private static final Set textNatives = Collections.synchronizedSet(new HashSet());
  /**
   * The native encodings/charsets for the Set of textNatives.
   */
  private static final Map nativeCharsets = Collections.synchronizedMap(new HashMap());
  /**
   * The key used to store pending data conversion requests for an AppContext.
   */
  private static final String DATA_CONVERTER_KEY = "DATA_CONVERTER_KEY";
  private static final String TAG = "AWT DataTransfer";
  private static final String[] DEPLOYMENT_CACHE_PROPERTIES = {
      "deployment.system.cachedir", "deployment.user.cachedir", "deployment.javaws.cachedir",
      "deployment.javapi.cachedir"};
  private static final ArrayList<File> deploymentCacheDirectoryList = new ArrayList<>();
  /**
   * Cache of the platform default encoding as specified in the
   * "file.encoding" system property.
   */
  private static String defaultEncoding;
  /**
   * The singleton DataTransferer instance. It is created during MToolkit
   * or WToolkit initialization.
   */
  private static DataTransferer transferer;

  static {
    DataFlavor tPlainTextStringFlavor = null;
    try {
      tPlainTextStringFlavor = new DataFlavor("text/plain;charset=Unicode;class=java.lang.String");
    } catch (ClassNotFoundException cannotHappen) {
    }
    plainTextStringFlavor = tPlainTextStringFlavor;

    DataFlavor tJavaTextEncodingFlavor = null;
    try {
      tJavaTextEncodingFlavor = new DataFlavor("application/x-java-text-encoding;class=\"[B\"");
    } catch (ClassNotFoundException cannotHappen) {
    }
    javaTextEncodingFlavor = tJavaTextEncodingFlavor;

    Map tempMap = new HashMap(17);
    tempMap.put("sgml", Boolean.TRUE);
    tempMap.put("xml", Boolean.TRUE);
    tempMap.put("html", Boolean.TRUE);
    tempMap.put("enriched", Boolean.TRUE);
    tempMap.put("richtext", Boolean.TRUE);
    tempMap.put("uri-list", Boolean.TRUE);
    tempMap.put("directory", Boolean.TRUE);
    tempMap.put("css", Boolean.TRUE);
    tempMap.put("calendar", Boolean.TRUE);
    tempMap.put("plain", Boolean.TRUE);
    tempMap.put("rtf", Boolean.FALSE);
    tempMap.put("tab-separated-values", Boolean.FALSE);
    tempMap.put("t140", Boolean.FALSE);
    tempMap.put("rfc822-headers", Boolean.FALSE);
    tempMap.put("parityfec", Boolean.FALSE);
    textMIMESubtypeCharsetSupport = Collections.synchronizedMap(tempMap);
  }

  /**
   * Converts an arbitrary text encoding to its canonical name.
   */
  public static String canonicalName(String encoding) {
    if (encoding == null) {
      return null;
    }
    try {
      return Charset.forName(encoding).name();
    } catch (IllegalCharsetNameException icne) {
      return encoding;
    } catch (UnsupportedCharsetException uce) {
      return encoding;
    }
  }

  /**
   * If the specified flavor is a text flavor which supports the "charset"
   * parameter, then this method returns that parameter, or the default
   * charset if no such parameter was specified at construction. For non-
   * text DataFlavors, and for non-charset text flavors, this method returns
   * null.
   */
  public static String getTextCharset(DataFlavor flavor) {
    if (!isFlavorCharsetTextType(flavor)) {
      return null;
    }

    String encoding = flavor.getParameter("charset");

    return encoding != null ? encoding : getDefaultTextCharset();
  }

  /**
   * Returns the platform's default character encoding.
   */
  public static String getDefaultTextCharset() {
    if (defaultEncoding != null) {
      return defaultEncoding;
    }
    return defaultEncoding = Charset.defaultCharset().name();
  }

  /**
   * Tests only whether the flavor's MIME type supports the charset
   * parameter. Must only be called for flavors with a primary type of
   * "text".
   */
  public static boolean doesSubtypeSupportCharset(DataFlavor flavor) {
    if (!"text".equals(flavor.getPrimaryType())) {
      Log.d(TAG, "Assertion (\"text\".equals(flavor.getPrimaryType())) failed");
    }

    String subType = flavor.getSubType();
    if (subType == null) {
      return false;
    }

    Object support = textMIMESubtypeCharsetSupport.get(subType);

    if (support != null) {
      return support == Boolean.TRUE;
    }

    boolean ret_val = flavor.getParameter("charset") != null;
    textMIMESubtypeCharsetSupport.put(subType, ret_val ? Boolean.TRUE : Boolean.FALSE);
    return ret_val;
  }

  public static boolean doesSubtypeSupportCharset(String subType, String charset) {
    Object support = textMIMESubtypeCharsetSupport.get(subType);

    if (support != null) {
      return support == Boolean.TRUE;
    }

    boolean ret_val = charset != null;
    textMIMESubtypeCharsetSupport.put(subType, ret_val ? Boolean.TRUE : Boolean.FALSE);
    return ret_val;
  }

  /**
   * Returns whether this flavor is a text type which supports the
   * 'charset' parameter.
   */
  public static boolean isFlavorCharsetTextType(DataFlavor flavor) {
    // Although stringFlavor doesn't actually support the charset
    // parameter (because its primary MIME type is not "text"), it should
    // be treated as though it does. stringFlavor is semantically
    // equivalent to "text/plain" data.
    if (DataFlavor.stringFlavor.equals(flavor)) {
      return true;
    }

    if (!"text".equals(flavor.getPrimaryType()) || !doesSubtypeSupportCharset(flavor)) {
      return false;
    }

    Class rep_class = flavor.getRepresentationClass();

    if (flavor.isRepresentationClassReader() ||
        String.class.equals(rep_class) ||
        flavor.isRepresentationClassCharBuffer() ||
        char[].class.equals(rep_class)) {
      return true;
    }

    if (!(flavor.isRepresentationClassInputStream() ||
              flavor.isRepresentationClassByteBuffer() ||
              byte[].class.equals(rep_class))) {
      return false;
    }

    String charset = flavor.getParameter("charset");

    return charset == null
        || isEncodingSupported(charset); // null equals default encoding which is always supported
  }

  /**
   * Returns whether this flavor is a text type which does not support the
   * 'charset' parameter.
   */
  public static boolean isFlavorNoncharsetTextType(DataFlavor flavor) {
    return !(!"text".equals(flavor.getPrimaryType()) || doesSubtypeSupportCharset(flavor)) && (flavor.isRepresentationClassInputStream() || flavor.isRepresentationClassByteBuffer() || byte[].class.equals(flavor.getRepresentationClass()));

  }

  /**
   * Determines whether this JRE can both encode and decode text in the
   * specified encoding.
   */
  public static boolean isEncodingSupported(String encoding) {
    if (encoding == null) {
      return false;
    }
    try {
      return Charset.isSupported(encoding);
    } catch (IllegalCharsetNameException icne) {
      return false;
    }
  }

  /**
   * Returns {@code true} if the given type is a java.rmi.Remote.
   */
  public static boolean isRemote(Class<?> type) {
    return RMI.isRemote(type);
  }

  /**
   * Returns an Iterator which traverses a SortedSet of Strings which are
   * a total order of the standard character sets supported by the JRE. The
   * ordering follows the same principles as DataFlavor.selectBestTextFlavor.
   * So as to avoid loading all available character converters, optional,
   * non-standard, character sets are not included.
   */
  public static Set<String> standardEncodings() {
    return StandardEncodingsHolder.standardEncodings;
  }

  /**
   * Converts a FlavorMap to a FlavorTable.
   */
  public static FlavorTable adaptFlavorMap(FlavorMap map) {
    if (map instanceof FlavorTable) {
      return (FlavorTable) map;
    }

    return new FlavorTable() {
      @Override
      public Map getNativesForFlavors(DataFlavor[] flavors) {
        return map.getNativesForFlavors(flavors);
      }

      @Override
      public Map getFlavorsForNatives(String[] natives) {
        return map.getFlavorsForNatives(natives);
      }

      @Override
      public List getNativesForFlavor(DataFlavor flav) {
        Map natives = getNativesForFlavors(new DataFlavor[]{flav});
        String nat = (String) natives.get(flav);
        if (nat != null) {
          List list = new ArrayList(1);
          list.add(nat);
          return list;
        } else {
          return Collections.EMPTY_LIST;
        }
      }

      @Override
      public List getFlavorsForNative(String nat) {
        Map flavors = getFlavorsForNatives(new String[]{nat});
        DataFlavor flavor = (DataFlavor) flavors.get(nat);
        if (flavor != null) {
          List list = new ArrayList(1);
          list.add(flavor);
          return list;
        } else {
          return Collections.EMPTY_LIST;
        }
      }
    };
  }

  /**
   * Returns an object that represents a mapping between the specified
   * key and value. <tt>null</tt> values and the <tt>null</tt> keys are
   * permitted. The internal representation of the mapping object is
   * irrelevant. The only requrement is that the two mapping objects are equal
   * if and only if their keys are equal and their values are equal.
   * More formally, the two mapping objects are equal if and only if
   * <tt>(value1 == null ? value2 == null : value1.equals(value2))
   * && (key1 == null ? key2 == null : key1.equals(key2))</tt>.
   */
  private static Object createMapping(Object key, Object value) {
    // NOTE: Should be updated to use AbstractMap.SimpleEntry as
    // soon as it is made public.
    return Arrays.asList(key, value);
  }

  private static ProtectionDomain getUserProtectionDomain(Transferable contents) {
    return contents.getClass().getProtectionDomain();
  }

  static boolean isFileInWebstartedCache(File f) {

    if (deploymentCacheDirectoryList.isEmpty()) {
      for (String cacheDirectoryProperty : DEPLOYMENT_CACHE_PROPERTIES) {
        String cacheDirectoryPath = System.getProperty(cacheDirectoryProperty);
        if (cacheDirectoryPath != null) {
          try {
            File cacheDirectory = new File(cacheDirectoryPath).getCanonicalFile();
            deploymentCacheDirectoryList.add(cacheDirectory);
          } catch (IOException ioe) {
          }
        }
      }
    }

    for (File deploymentCacheDirectory : deploymentCacheDirectoryList) {
      for (File dir = f; dir != null; dir = dir.getParentFile()) {
        if (dir.equals(deploymentCacheDirectory)) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Helper function to convert a Set of DataFlavors to a sorted array.
   * The array will be sorted according to {@code DataFlavorComparator}.
   */
  public static DataFlavor[] setToSortedDataFlavorArray(Set flavorsSet) {
    DataFlavor[] flavors = new DataFlavor[flavorsSet.size()];
    flavorsSet.toArray(flavors);
    Comparator comparator = new DataFlavorComparator(IndexedComparator.SELECT_WORST);
    Arrays.sort(flavors, comparator);
    return flavors;
  }

  /**
   * Helper function to convert an InputStream to a byte[] array.
   */
  protected static byte[] inputStreamToByteArray(InputStream str) throws IOException {
    try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
      int len;
      byte[] buf = new byte[8192];

      while ((len = str.read(buf)) != -1) {
        baos.write(buf, 0, len);
      }

      return baos.toByteArray();
    }
  }

  /**
   * Returns a new copy of the contained marshalled object.
   */
  static Object getMarshalledObject(Object obj) throws IOException, ClassNotFoundException {
    try {
      return RMI.marshallGet.invoke(obj);
    } catch (IllegalAccessException x) {
      throw new AssertionError(x);
    } catch (InvocationTargetException x) {
      Throwable cause = x.getCause();
      if (cause instanceof IOException) {
        throw (IOException) cause;
      }
      if (cause instanceof ClassNotFoundException) {
        throw (ClassNotFoundException) cause;
      }
      throw new AssertionError(x);
    }
  }

  /**
   * Returns the default Unicode encoding for the platform. The encoding
   * need not be canonical. This method is only used by the archaic function
   * DataFlavor.getTextPlainUnicodeFlavor().
   */
  public abstract String getDefaultUnicodeEncoding();

  /**
   * This method is called for text flavor mappings established while parsing
   * the flavormap.properties file. It stores the "eoln" and "terminators"
   * parameters which are not officially part of the MIME type. They are
   * MIME parameters specific to the flavormap.properties file format.
   */
  public void registerTextFlavorProperties(
      String nat, String charset, String eoln, String terminators) {
    Long format = getFormatForNativeAsLong(nat);

    textNatives.add(format);
    nativeCharsets.put(format,
        charset != null && !charset.isEmpty() ? charset : getDefaultTextCharset());
    if (eoln != null && !eoln.isEmpty() && !"\n".equals(eoln)) {
      nativeEOLNs.put(format, eoln);
    }
    if (terminators != null && !terminators.isEmpty()) {
      Integer iTerminators = Integer.valueOf(terminators);
      if (iTerminators > 0) {
        nativeTerminators.put(format, iTerminators);
      }
    }
  }

  /**
   * Determines whether the native corresponding to the specified long format
   * was listed in the flavormap.properties file.
   */
  protected boolean isTextFormat(long format) {
    return textNatives.contains(format);
  }

  protected String getCharsetForTextFormat(Long lFormat) {
    return (String) nativeCharsets.get(lFormat);
  }

  /**
   * Specifies whether text imported from the native system in the specified
   * format is locale-dependent. If so, when decoding such text,
   * 'nativeCharsets' should be ignored, and instead, the Transferable should
   * be queried for its javaTextEncodingFlavor data for the correct encoding.
   */
  public abstract boolean isLocaleDependentTextFormat(long format);

  /**
   * Determines whether the DataFlavor corresponding to the specified long
   * format is DataFlavor.javaFileListFlavor.
   */
  public abstract boolean isFileFormat(long format);

  /**
   * Determines whether the DataFlavor corresponding to the specified long
   * format is DataFlavor.imageFlavor.
   */
  public abstract boolean isImageFormat(long format);

  /**
   * Determines whether the format is a URI list we can convert to
   * a DataFlavor.javaFileListFlavor.
   */
  protected boolean isURIListFormat(long format) {
    return false;
  }

  /**
   * Returns a Map whose keys are all of the possible formats into which the
   * Transferable's transfer data flavors can be translated. The value of
   * each key is the DataFlavor in which the Transferable's data should be
   * requested when converting to the format.
   * <p>
   * The map keys are sorted according to the native formats preference
   * order.
   */
  public SortedMap<Long, DataFlavor> getFormatsForTransferable(
      Transferable contents, FlavorTable map) {
    DataFlavor[] flavors = contents.getTransferDataFlavors();
    if (flavors == null) {
      return new TreeMap();
    }
    return getFormatsForFlavors(flavors, map);
  }

  /**
   * Returns a Map whose keys are all of the possible formats into which data
   * in the specified DataFlavors can be translated. The value of each key
   * is the DataFlavor in which the Transferable's data should be requested
   * when converting to the format.
   * <p>
   * The map keys are sorted according to the native formats preference
   * order.
   *
   * @param flavors the data flavors
   * @param map     the FlavorTable which contains mappings between
   *                DataFlavors and data formats
   * @throws NullPointerException if flavors or map is {@code null}
   */
  public SortedMap<Long, DataFlavor> getFormatsForFlavors(
      DataFlavor[] flavors, FlavorTable map) {
    Map<Long, DataFlavor> formatMap = new HashMap<>(flavors.length);
    Map<Long, DataFlavor> textPlainMap = new HashMap<>(flavors.length);
    // Maps formats to indices that will be used to sort the formats
    // according to the preference order.
    // Larger index value corresponds to the more preferable format.
    Map indexMap = new HashMap(flavors.length);
    Map textPlainIndexMap = new HashMap(flavors.length);

    int currentIndex = 0;

    // Iterate backwards so that preferred DataFlavors are used over
    // other DataFlavors. (See javadoc for
    // Transferable.getTransferDataFlavors.)
    for (int i = flavors.length - 1; i >= 0; i--) {
      DataFlavor flavor = flavors[i];
      if (flavor == null) {
        continue;
      }

      // Don't explicitly test for String, since it is just a special
      // case of Serializable
      if (flavor.isFlavorTextType() ||
          flavor.isFlavorJavaFileListType() ||
          DataFlavor.imageFlavor.equals(flavor) ||
          flavor.isRepresentationClassSerializable() ||
          flavor.isRepresentationClassInputStream() ||
          flavor.isRepresentationClassRemote()) {
        List natives = map.getNativesForFlavor(flavor);

        currentIndex += natives.size();

        for (Object aNative : natives) {
          Long lFormat = getFormatForNativeAsLong((String) aNative);
          Integer index = currentIndex;
          currentIndex--;

          formatMap.put(lFormat, flavor);
          indexMap.put(lFormat, index);

          // SystemFlavorMap.getNativesForFlavor will return
          // text/plain natives for all text/*. While this is good
          // for a single text/* flavor, we would prefer that
          // text/plain native data come from a text/plain flavor.
          if ("text".equals(flavor.getPrimaryType()) && "plain".equals(flavor.getSubType())
              || flavor.equals(DataFlavor.stringFlavor)) {
            textPlainMap.put(lFormat, flavor);
            textPlainIndexMap.put(lFormat, index);
          }
        }

        currentIndex += natives.size();
      }
    }

    formatMap.putAll(textPlainMap);
    indexMap.putAll(textPlainIndexMap);

    // Sort the map keys according to the formats preference order.
    Comparator comparator = new IndexOrderComparator(indexMap, IndexedComparator.SELECT_WORST);
    SortedMap sortedMap = new TreeMap(comparator);
    sortedMap.putAll(formatMap);

    return sortedMap;
  }

  /**
   * Returns a Map whose keys are all of the possible DataFlavors into which
   * data in the specified formats can be translated. The value of each key
   * is the format in which the Clipboard or dropped data should be requested
   * when converting to the DataFlavor.
   */
  public Map getFlavorsForFormats(long[] formats, FlavorTable map) {
    Map flavorMap = new HashMap(formats.length);
    Set mappingSet = new HashSet(formats.length);
    Set flavorSet = new HashSet(formats.length);

    // First step: build flavorSet, mappingSet and initial flavorMap
    // flavorSet  - the set of all the DataFlavors into which
    //              data in the specified formats can be translated;
    // mappingSet - the set of all the mappings from the specified formats
    //              into any DataFlavor;
    // flavorMap  - after this step, this map maps each of the DataFlavors
    //              from flavorSet to any of the specified formats.
    for (long format : formats) {
      String nat = getNativeForFormat(format);
      List flavors = map.getFlavorsForNative(nat);

      for (Object flavor1 : flavors) {
        DataFlavor flavor = (DataFlavor) flavor1;

        // Don't explicitly test for String, since it is just a special
        // case of Serializable
        if (flavor.isFlavorTextType() ||
            flavor.isFlavorJavaFileListType() ||
            DataFlavor.imageFlavor.equals(flavor) ||
            flavor.isRepresentationClassSerializable() ||
            flavor.isRepresentationClassInputStream() ||
            flavor.isRepresentationClassRemote()) {
          Long lFormat = format;
          Object mapping = createMapping(lFormat, flavor);
          flavorMap.put(flavor, lFormat);
          mappingSet.add(mapping);
          flavorSet.add(flavor);
        }
      }
    }

    // Second step: for each DataFlavor try to figure out which of the
    // specified formats is the best to translate to this flavor.
    // Then map each flavor to the best format.
    // For the given flavor, FlavorTable indicates which native will
    // best reflect data in the specified flavor to the underlying native
    // platform. We assume that this native is the best to translate
    // to this flavor.
    // Note: FlavorTable allows one-way mappings, so we can occasionally
    // map a flavor to the format for which the corresponding
    // format-to-flavor mapping doesn't exist. For this reason we have built
    // a mappingSet of all format-to-flavor mappings for the specified formats
    // and check if the format-to-flavor mapping exists for the
    // (flavor,format) pair being added.
    for (Object aFlavorSet : flavorSet) {
      DataFlavor flavor = (DataFlavor) aFlavorSet;

      List natives = map.getNativesForFlavor(flavor);

      for (Object aNative : natives) {
        Long lFormat = getFormatForNativeAsLong((String) aNative);
        Object mapping = createMapping(lFormat, flavor);

        if (mappingSet.contains(mapping)) {
          flavorMap.put(flavor, lFormat);
          break;
        }
      }
    }

    return flavorMap;
  }

  /**
   * Returns a Set of all DataFlavors for which
   * 1) a mapping from at least one of the specified formats exists in the
   * specified map and
   * 2) the data translation for this mapping can be performed by the data
   * transfer subsystem.
   *
   * @param formats the data formats
   * @param map     the FlavorTable which contains mappings between
   *                DataFlavors and data formats
   * @throws NullPointerException if formats or map is {@code null}
   */
  public Set getFlavorsForFormatsAsSet(long[] formats, FlavorTable map) {
    Set flavorSet = new HashSet(formats.length);

    for (long format : formats) {
      String nat = getNativeForFormat(format);
      List flavors = map.getFlavorsForNative(nat);

      for (Object flavor1 : flavors) {
        DataFlavor flavor = (DataFlavor) flavor1;

        // Don't explicitly test for String, since it is just a special
        // case of Serializable
        if (flavor.isFlavorTextType() ||
            flavor.isFlavorJavaFileListType() ||
            DataFlavor.imageFlavor.equals(flavor) ||
            flavor.isRepresentationClassSerializable() ||
            flavor.isRepresentationClassInputStream() ||
            flavor.isRepresentationClassRemote()) {
          flavorSet.add(flavor);
        }
      }
    }

    return flavorSet;
  }

  /**
   * Returns an array of all DataFlavors for which
   * 1) a mapping from the specified format exists in the specified map and
   * 2) the data translation for this mapping can be performed by the data
   * transfer subsystem.
   * The array will be sorted according to a
   * {@code DataFlavorComparator} created with the specified
   * map as an argument.
   *
   * @param format the data format
   * @param map    the FlavorTable which contains mappings between
   *               DataFlavors and data formats
   * @throws NullPointerException if map is {@code null}
   */
  public DataFlavor[] getFlavorsForFormatAsArray(long format, FlavorTable map) {
    return getFlavorsForFormatsAsArray(new long[]{format}, map);
  }

  /**
   * Returns an array of all DataFlavors for which
   * 1) a mapping from at least one of the specified formats exists in the
   * specified map and
   * 2) the data translation for this mapping can be performed by the data
   * transfer subsystem.
   * The array will be sorted according to a
   * {@code DataFlavorComparator} created with the specified
   * map as an argument.
   *
   * @param formats the data formats
   * @param map     the FlavorTable which contains mappings between
   *                DataFlavors and data formats
   * @throws NullPointerException if formats or map is {@code null}
   */
  public DataFlavor[] getFlavorsForFormatsAsArray(long[] formats, FlavorTable map) {
    // getFlavorsForFormatsAsSet() is less expensive than
    // getFlavorsForFormats().
    return setToSortedDataFlavorArray(getFlavorsForFormatsAsSet(formats, map));
  }

  /**
   * Looks-up or registers the String native with the native data transfer
   * system and returns a long format corresponding to that native.
   */
  protected abstract Long getFormatForNativeAsLong(String str);

  /**
   * Looks-up the String native corresponding to the specified long format in
   * the native data transfer system.
   */
  protected abstract String getNativeForFormat(long format);

  /* Contains common code for finding the best charset for
   * clipboard string encoding/decoding, basing on clipboard
   * format and localeTransferable(on decoding, if available)
   */
  private String getBestCharsetForTextFormat(Long lFormat, Transferable localeTransferable)
      throws IOException {
    String charset = null;
    if (localeTransferable != null &&
        isLocaleDependentTextFormat(lFormat) &&
        localeTransferable.isDataFlavorSupported(javaTextEncodingFlavor)) {
      try {
        charset = new String((byte[]) localeTransferable.getTransferData(javaTextEncodingFlavor),
            "UTF-8");
      } catch (UnsupportedFlavorException cannotHappen) {
      }
    } else {
      charset = getCharsetForTextFormat(lFormat);
    }
    if (charset == null) {
      // Only happens when we have a custom text type.
      charset = getDefaultTextCharset();
    }
    return charset;
  }

  /**
   * Translating either a byte array or an InputStream into an String.
   * Strip terminators and search-and-replace EOLN.
   * <p>
   * Native to Java string conversion
   */
  @SuppressWarnings("AssignmentToForLoopParameter")
  private String translateBytesToString(byte[] bytes, long format, Transferable localeTransferable)
      throws IOException {

    Long lFormat = format;
    String charset = getBestCharsetForTextFormat(lFormat, localeTransferable);

    // Locate terminating NUL bytes. Note that if terminators is 0,
    // the we never added an entry to nativeTerminators anyway, so
    // we'll skip code altogether.

    // In other words: we are doing char alignment here basing on suggestion
    // that count of zero-'terminators' is a number of bytes in one symbol
    // for selected charset (clipboard format). It is not complitly true for
    // multibyte coding like UTF-8, but helps understand the procedure.
    // "abcde\0" -> "abcde"

    String eoln = (String) nativeEOLNs.get(lFormat);
    Integer terminators = (Integer) nativeTerminators.get(lFormat);
    int count;
    if (terminators != null) {
      int numTerminators = terminators;
      search:
      for (count = 0; count < bytes.length - numTerminators + 1; count += numTerminators) {
        for (int i = count; i < count + numTerminators; i++) {
          if (bytes[i] != 0x0) {
            continue search;
          }
        }
        // found terminators
        break search;
      }
    } else {
      count = bytes.length;
    }

    // Decode text to chars. Don't include any terminators.
    String converted = new String(bytes, 0, count, charset);

    // Search and replace EOLN. Note that if EOLN is "\n", then we
    // never added an entry to nativeEOLNs anyway, so we'll skip this
    // code altogether.
    // Count of NUL-terminators and EOLN coding are platform-specific and
    // loaded from flavormap.properties file
    // windows: "abc\r\nde" -> "abc\nde"

    if (eoln != null) {

            /* Fix for 4463560: replace EOLNs symbol-by-symbol instead
             * of using buf.replace()
             */

      char[] buf = converted.toCharArray();
      char[] eoln_arr = eoln.toCharArray();
      int j = 0;
      boolean match;

      for (int i = 0; i < buf.length; ) {
        // Catch last few bytes
        if (i + eoln_arr.length > buf.length) {
          buf[j] = buf[i];
          j++;
          i++;
          continue;
        }

        match = true;
        for (int k = 0, l = i; k < eoln_arr.length; k++, l++) {
          if (eoln_arr[k] != buf[l]) {
            match = false;
            break;
          }
        }
        if (match) {
          buf[j] = '\n';
          j++;
          i += eoln_arr.length;
        } else {
          buf[j] = buf[i];
          j++;
          i++;
        }
      }
      converted = new String(buf, 0, j);
    }

    return converted;
  }

  boolean isForbiddenToRead(File file, ProtectionDomain protectionDomain) {
    if (null == protectionDomain) {
      return false;
    }
    try {
      FilePermission filePermission = new FilePermission(file.getCanonicalPath(), "read, delete");
      if (protectionDomain.implies(filePermission)) {
        return false;
      }
    } catch (IOException e) {
    }

    return true;
  }

  public Object translateBytes(
      byte[] bytes, DataFlavor flavor, long format, Transferable localeTransferable)
      throws IOException {

    Object theObject = null;

    // Source data is a file list. Use the dragQueryFile native function to
    // do most of the decoding. Then wrap File objects around the String
    // filenames and return a List.
    if (isFileFormat(format)) {
      if (!DataFlavor.javaFileListFlavor.equals(flavor)) {
        throw new IOException("data translation failed");
      }
      String[] filenames = dragQueryFile(bytes);
      if (filenames == null) {
        return null;
      }

      // Convert the strings to File objects
      File[] files = new File[filenames.length];
      for (int i = 0; i < filenames.length; i++) {
        files[i] = new File(filenames[i]);
      }

      // Turn the list of Files into a List and return
      theObject = Arrays.asList(files);

      // Source data is a URI list. Convert to DataFlavor.javaFileListFlavor
      // where possible.
    } else if (isURIListFormat(format) && DataFlavor.javaFileListFlavor.equals(flavor)) {

      try (ByteArrayInputStream str = new ByteArrayInputStream(bytes)) {

        URI[] uris = dragQueryURIs(str, format, localeTransferable);
        if (uris == null) {
          return null;
        }
        List<File> files = new ArrayList<>();
        for (URI uri : uris) {
          try {
            files.add(new File(uri));
          } catch (IllegalArgumentException illegalArg) {
            // When converting from URIs to less generic files,
            // common practice (Wine, SWT) seems to be to
            // silently drop the URIs that aren't local files.
          }
        }
        theObject = files;
      }

      // Target data is a String. Strip terminating NUL bytes. Decode bytes
      // into characters. Search-and-replace EOLN.
    } else if (String.class.equals(flavor.getRepresentationClass()) &&
        isFlavorCharsetTextType(flavor) && isTextFormat(format)) {

      theObject = translateBytesToString(bytes, format, localeTransferable);

      // Target data is a Reader. Obtain data in InputStream format, encoded
      // as "Unicode" (utf-16be). Then use an InputStreamReader to decode
      // back to chars on demand.
    } else if (flavor.isRepresentationClassReader()) {
      try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
        theObject = translateStream(bais, flavor, format, localeTransferable);
      }
      // Target data is a CharBuffer. Recur to obtain String and wrap.
    } else if (flavor.isRepresentationClassCharBuffer()) {
      if (!(isFlavorCharsetTextType(flavor) && isTextFormat(format))) {
        throw new IOException("cannot transfer non-text data as CharBuffer");
      }

      CharBuffer buffer = CharBuffer.wrap(translateBytesToString(bytes,
          format,
          localeTransferable));

      theObject = constructFlavoredObject(buffer, flavor, CharBuffer.class);

      // Target data is a char array. Recur to obtain String and convert to
      // char array.
    } else if (char[].class.equals(flavor.getRepresentationClass())) {
      if (!(isFlavorCharsetTextType(flavor) && isTextFormat(format))) {
        throw new IOException("cannot transfer non-text data as char array");
      }

      theObject = translateBytesToString(bytes, format, localeTransferable).toCharArray();

      // Target data is a ByteBuffer. For arbitrary flavors, just return
      // the raw bytes. For text flavors, convert to a String to strip
      // terminators and search-and-replace EOLN, then reencode according to
      // the requested flavor.
    } else if (flavor.isRepresentationClassByteBuffer()) {
      if (isFlavorCharsetTextType(flavor) && isTextFormat(format)) {
        bytes = translateBytesToString(bytes, format, localeTransferable).getBytes(getTextCharset(
            flavor));
      }

      ByteBuffer buffer = ByteBuffer.wrap(bytes);
      theObject = constructFlavoredObject(buffer, flavor, ByteBuffer.class);

      // Target data is a byte array. For arbitrary flavors, just return
      // the raw bytes. For text flavors, convert to a String to strip
      // terminators and search-and-replace EOLN, then reencode according to
      // the requested flavor.
    } else if (byte[].class.equals(flavor.getRepresentationClass())) {
      theObject = isFlavorCharsetTextType(flavor) && isTextFormat(format) ?
          translateBytesToString(bytes,
          format,
          localeTransferable).getBytes(getTextCharset(flavor)) : bytes;

      // Target data is an InputStream. For arbitrary flavors, just return
      // the raw bytes. For text flavors, decode to strip terminators and
      // search-and-replace EOLN, then reencode according to the requested
      // flavor.
    } else if (flavor.isRepresentationClassInputStream()) {

      try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
        theObject = translateStream(bais, flavor, format, localeTransferable);
      }
    } else if (flavor.isRepresentationClassRemote()) {
      try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
          ObjectInputStream ois = new ObjectInputStream(bais)) {
        theObject = getMarshalledObject(ois.readObject());
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }

      // Target data is Serializable
    } else if (flavor.isRepresentationClassSerializable()) {

      try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes)) {
        theObject = translateStream(bais, flavor, format, localeTransferable);
      }

      // Target data is Image
    } else if (DataFlavor.imageFlavor.equals(flavor)) {
      if (!isImageFormat(format)) {
        throw new IOException("data translation failed");
      }

      theObject = platformImageBytesToImage(bytes, format);
    }

    if (theObject == null) {
      throw new IOException("data translation failed");
    }

    return theObject;
  }

  /**
   * Primary translation function for translating
   * an InputStream into an Object, given a source format and a target
   * DataFlavor.
   */
  public Object translateStream(
      InputStream str, DataFlavor flavor, long format, Transferable localeTransferable)
      throws IOException {

    Object theObject = null;
    // Source data is a URI list. Convert to DataFlavor.javaFileListFlavor
    // where possible.
    if (isURIListFormat(format) && DataFlavor.javaFileListFlavor.equals(flavor)) {

      URI[] uris = dragQueryURIs(str, format, localeTransferable);
      if (uris == null) {
        return null;
      }
      ArrayList files = new ArrayList();
      for (URI uri : uris) {
        try {
          files.add(new File(uri));
        } catch (IllegalArgumentException illegalArg) {
          // When converting from URIs to less generic files,
          // common practice (Wine, SWT) seems to be to
          // silently drop the URIs that aren't local files.
        }
      }
      theObject = files;

      // Target data is a String. Strip terminating NUL bytes. Decode bytes
      // into characters. Search-and-replace EOLN.
    } else if (String.class.equals(flavor.getRepresentationClass()) &&
        isFlavorCharsetTextType(flavor) && isTextFormat(format)) {

      return translateBytesToString(inputStreamToByteArray(str), format, localeTransferable);

      // Special hack to maintain backwards-compatibility with the brokenness
      // of StringSelection. Return a StringReader instead of an InputStream.
      // Recur to obtain String and encapsulate.
    } else if (DataFlavor.plainTextFlavor.equals(flavor)) {
      theObject = new StringReader(translateBytesToString(inputStreamToByteArray(str),
          format,
          localeTransferable));

      // Target data is an InputStream. For arbitrary flavors, just return
      // the raw bytes. For text flavors, decode to strip terminators and
      // search-and-replace EOLN, then reencode according to the requested
      // flavor.
    } else if (flavor.isRepresentationClassInputStream()) {
      theObject = translateStreamToInputStream(str, flavor, format, localeTransferable);

      // Target data is a Reader. Obtain data in InputStream format, encoded
      // as "Unicode" (utf-16be). Then use an InputStreamReader to decode
      // back to chars on demand.
    } else if (flavor.isRepresentationClassReader()) {
      if (!(isFlavorCharsetTextType(flavor) && isTextFormat(format))) {
        throw new IOException("cannot transfer non-text data as Reader");
      }

      InputStream is = (InputStream) translateStreamToInputStream(str,
          DataFlavor.plainTextFlavor,
          format,
          localeTransferable);

      String unicode = getTextCharset(DataFlavor.plainTextFlavor);

      Reader reader = new InputStreamReader(is, unicode);

      theObject = constructFlavoredObject(reader, flavor, Reader.class);
      // Target data is a byte array
    } else if (byte[].class.equals(flavor.getRepresentationClass())) {
      theObject = isFlavorCharsetTextType(flavor) && isTextFormat(format) ?
          translateBytesToString(inputStreamToByteArray(str),
          format,
          localeTransferable).getBytes(getTextCharset(flavor)) : inputStreamToByteArray(str);
      // Target data is an RMI object
    } else if (flavor.isRepresentationClassRemote()) {

      try (ObjectInputStream ois = new ObjectInputStream(str)) {
        theObject = getMarshalledObject(ois.readObject());
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }

      // Target data is Serializable
    } else if (flavor.isRepresentationClassSerializable()) {
      try (ObjectInputStream ois = new ObjectInputStream(str)) {
        theObject = ois.readObject();
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }
      // Target data is Image
    } else if (DataFlavor.imageFlavor.equals(flavor)) {
      if (!isImageFormat(format)) {
        throw new IOException("data translation failed");
      }
      theObject = platformImageBytesToImage(inputStreamToByteArray(str), format);
    }

    if (theObject == null) {
      throw new IOException("data translation failed");
    }

    return theObject;
  }

  /**
   * For arbitrary flavors, just use the raw InputStream. For text flavors,
   * ReencodingInputStream will decode and reencode the InputStream on demand
   * so that we can strip terminators and search-and-replace EOLN.
   */
  private Object translateStreamToInputStream(
      InputStream str, DataFlavor flavor, long format, Transferable localeTransferable)
      throws IOException {
    if (isFlavorCharsetTextType(flavor) && isTextFormat(format)) {
      str = new ReencodingInputStream(str, format, getTextCharset(flavor), localeTransferable);
    }

    return constructFlavoredObject(str, flavor, InputStream.class);
  }

  /**
   * We support representations which are exactly of the specified Class,
   * and also arbitrary Objects which have a constructor which takes an
   * instance of the Class as its sole parameter.
   */
  private Object constructFlavoredObject(Object arg, DataFlavor flavor, Class clazz)
      throws IOException {
    Class dfrc = flavor.getRepresentationClass();

    if (clazz.equals(dfrc)) {
      return arg; // simple case
    } else {
      Constructor[] constructors;

      try {
        constructors = (Constructor[]) AccessController.doPrivileged(new PrivilegedAction() {
          @Override
          public Object run() {
            return dfrc.getConstructors();
          }
        });
      } catch (SecurityException se) {
        throw new IOException(se.getMessage());
      }

      Constructor constructor = null;

      for (Constructor constructor1 : constructors) {
        if (!Modifier.isPublic(constructor1.getModifiers())) {
          continue;
        }

        Class[] ptypes = constructor1.getParameterTypes();

        if (ptypes != null && ptypes.length == 1 &&
            clazz.equals(ptypes[0])) {
          constructor = constructor1;
          break;
        }
      }

      if (constructor == null) {
        throw new IOException("can't find <init>(L" + clazz +
            ";)V for class: " + dfrc.getName());
      }

      try {
        return constructor.newInstance(arg);
      } catch (Exception e) {
        throw new IOException(e.getMessage());
      }
    }
  }

  /**
   * Decodes a byte array into a set of String filenames.
   */
  protected abstract String[] dragQueryFile(byte[] bytes);

  /**
   * Decodes URIs from either a byte array or a stream.
   */
  protected URI[] dragQueryURIs(InputStream stream, long format, Transferable localeTransferable)
      throws IOException {
    throw new IOException(new UnsupportedOperationException("not implemented on this platform"));
  }

  /**
   * Translates either a byte array or an input stream which contain
   * platform-specific image data in the given format into an Image.
   */

  protected abstract Image platformImageBytesToImage(
      byte[] bytes, long format) throws IOException;

  public abstract ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler();

  /**
   * Returns platform-specific mappings for the specified native.
   * If there are no platform-specific mappings for this native, the method
   * returns an empty {@code List}.
   */
  public LinkedHashSet<DataFlavor> getPlatformMappingsForNative(String nat) {
    return new LinkedHashSet<>();
  }

  /**
   * Returns platform-specific mappings for the specified flavor.
   * If there are no platform-specific mappings for this flavor, the method
   * returns an empty {@code List}.
   */
  public LinkedHashSet<String> getPlatformMappingsForFlavor(DataFlavor df) {
    return new LinkedHashSet<>();
  }

  /**
   * Lazy initialization of Standard Encodings.
   */
  private static final class StandardEncodingsHolder {
    static final SortedSet<String> standardEncodings = load();

    private static SortedSet<String> load() {
      Comparator comparator = new CharsetComparator(IndexedComparator.SELECT_WORST);
      SortedSet<String> tempSet = new TreeSet<>(comparator);
      tempSet.add("US-ASCII");
      tempSet.add("ISO-8859-1");
      tempSet.add("UTF-8");
      tempSet.add("UTF-16BE");
      tempSet.add("UTF-16LE");
      tempSet.add("UTF-16");
      tempSet.add(getDefaultTextCharset());
      return Collections.unmodifiableSortedSet(tempSet);
    }
  }

  /**
   * A Comparator which includes a helper function for comparing two Objects
   * which are likely to be keys in the specified Map.
   */
  public abstract static class IndexedComparator implements Comparator, Serializable {

    /**
     * The best Object (e.g., DataFlavor) will be the last in sequence.
     */
    public static final boolean SELECT_BEST = true;

    /**
     * The best Object (e.g., DataFlavor) will be the first in sequence.
     */
    public static final boolean SELECT_WORST = false;
    private static final long serialVersionUID = 81689337367964003L;

    protected final boolean order;

    public IndexedComparator() {
      this(SELECT_BEST);
    }

    public IndexedComparator(boolean order) {
      this.order = order;
    }

    /**
     * Helper method to compare two objects by their Integer indices in the
     * given map. If the map doesn't contain an entry for either of the
     * objects, the fallback index will be used for the object instead.
     *
     * @param indexMap      the map which maps objects into Integer indexes.
     * @param obj1          the first object to be compared.
     * @param obj2          the second object to be compared.
     * @param fallbackIndex the Integer to be used as a fallback index.
     * @return a negative integer, zero, or a positive integer as the
     * first object is mapped to a less, equal to, or greater
     * index than the second.
     */
    protected static int compareIndices(
        Map indexMap, Object obj1, Object obj2, Integer fallbackIndex) {
      Integer index1 = (Integer) indexMap.get(obj1);
      Integer index2 = (Integer) indexMap.get(obj2);

      if (index1 == null) {
        index1 = fallbackIndex;
      }
      if (index2 == null) {
        index2 = fallbackIndex;
      }

      return index1.compareTo(index2);
    }

    /**
     * Helper method to compare two objects by their Long indices in the
     * given map. If the map doesn't contain an entry for either of the
     * objects, the fallback index will be used for the object instead.
     *
     * @param indexMap      the map which maps objects into Long indexes.
     * @param obj1          the first object to be compared.
     * @param obj2          the second object to be compared.
     * @param fallbackIndex the Long to be used as a fallback index.
     * @return a negative integer, zero, or a positive integer as the
     * first object is mapped to a less, equal to, or greater
     * index than the second.
     */
    protected static int compareLongs(Map indexMap, Object obj1, Object obj2, Long fallbackIndex) {
      Long index1 = (Long) indexMap.get(obj1);
      Long index2 = (Long) indexMap.get(obj2);

      if (index1 == null) {
        index1 = fallbackIndex;
      }
      if (index2 == null) {
        index2 = fallbackIndex;
      }

      return index1.compareTo(index2);
    }
  }

  /**
   * An IndexedComparator which compares two String charsets. The comparison
   * follows the rules outlined in DataFlavor.selectBestTextFlavor. In order
   * to ensure that non-Unicode, non-ASCII, non-default charsets are sorted
   * in alphabetical order, charsets are not automatically converted to their
   * canonical forms.
   */
  public static class CharsetComparator extends IndexedComparator {
    private static final Map charsets;
    private static final Integer DEFAULT_CHARSET_INDEX = 2;
    private static final Integer OTHER_CHARSET_INDEX = 1;
    private static final Integer WORST_CHARSET_INDEX = 0;
    private static final Integer UNSUPPORTED_CHARSET_INDEX = Integer.MIN_VALUE;
    private static final String UNSUPPORTED_CHARSET = "UNSUPPORTED";
    private static final long serialVersionUID = -11291056541271784L;
    private static String defaultEncoding;

    static {
      HashMap charsetsMap = new HashMap(8, 1.0f);

      // we prefer Unicode charsets
      charsetsMap.put(canonicalName("UTF-16LE"), 4);
      charsetsMap.put(canonicalName("UTF-16BE"), 5);
      charsetsMap.put(canonicalName("UTF-8"), 6);
      charsetsMap.put(canonicalName("UTF-16"), 7);

      // US-ASCII is the worst charset supported
      charsetsMap.put(canonicalName("US-ASCII"), WORST_CHARSET_INDEX);

      String defEncoding = canonicalName(getDefaultTextCharset());

      if (charsetsMap.get(defaultEncoding) == null) {
        charsetsMap.put(defaultEncoding, DEFAULT_CHARSET_INDEX);
      }
      charsetsMap.put(UNSUPPORTED_CHARSET, UNSUPPORTED_CHARSET_INDEX);

      charsets = Collections.unmodifiableMap(charsetsMap);
    }

    public CharsetComparator() {
      this(SELECT_BEST);
    }

    public CharsetComparator(boolean order) {
      super(order);
    }

    /**
     * Returns encoding for the specified charset according to the
     * following rules:
     * <ul>
     * <li>If the charset is {@code null}, then {@code null} will
     * be returned.
     * <li>Iff the charset specifies an encoding unsupported by this JRE,
     * {@code UNSUPPORTED_CHARSET} will be returned.
     * <li>If the charset specifies an alias name, the corresponding
     * canonical name will be returned iff the charset is a known
     * Unicode, ASCII, or default charset.
     * </ul>
     *
     * @param charset the charset.
     * @return an encoding for this charset.
     */
    protected static String getEncoding(String charset) {
      if (charset == null) {
        return null;
      } else if (!isEncodingSupported(charset)) {
        return UNSUPPORTED_CHARSET;
      } else {
        // Only convert to canonical form if the charset is one
        // of the charsets explicitly listed in the known charsets
        // map. This will happen only for Unicode, ASCII, or default
        // charsets.
        String canonicalName = canonicalName(charset);
        return charsets.containsKey(canonicalName) ? canonicalName : charset;
      }
    }

    /**
     * Compares two String objects. Returns a negative integer, zero,
     * or a positive integer as the first charset is worse than, equal to,
     * or better than the second.
     *
     * @param obj1 the first charset to be compared
     * @param obj2 the second charset to be compared
     * @return a negative integer, zero, or a positive integer as the
     * first argument is worse, equal to, or better than the
     * second.
     * @throws ClassCastException   if either of the arguments is not
     *                              instance of String
     * @throws NullPointerException if either of the arguments is
     *                              {@code null}.
     */
    @Override
    public int compare(Object obj1, Object obj2) {
      String charset1;
      String charset2;
      if (order == SELECT_BEST) {
        charset1 = (String) obj1;
        charset2 = (String) obj2;
      } else {
        charset1 = (String) obj2;
        charset2 = (String) obj1;
      }

      return compareCharsets(charset1, charset2);
    }

    /**
     * Compares charsets. Returns a negative integer, zero, or a positive
     * integer as the first charset is worse than, equal to, or better than
     * the second.
     * <p>
     * Charsets are ordered according to the following rules:
     * <ul>
     * <li>All unsupported charsets are equal.
     * <li>Any unsupported charset is worse than any supported charset.
     * <li>Unicode charsets, such as "UTF-16", "UTF-8", "UTF-16BE" and
     * "UTF-16LE", are considered best.
     * <li>After them, platform default charset is selected.
     * <li>"US-ASCII" is the worst of supported charsets.
     * <li>For all other supported charsets, the lexicographically less
     * one is considered the better.
     * </ul>
     *
     * @param charset1 the first charset to be compared
     * @param charset2 the second charset to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * first argument is worse, equal to, or better than the
     * second.
     */
    protected int compareCharsets(String charset1, String charset2) {
      charset1 = getEncoding(charset1);
      charset2 = getEncoding(charset2);

      int comp = compareIndices(charsets, charset1, charset2, OTHER_CHARSET_INDEX);

      if (comp == 0) {
        return charset2.compareTo(charset1);
      }

      return comp;
    }
  }

  /**
   * An IndexedComparator which compares two DataFlavors. For text flavors,
   * the comparison follows the rules outlined in
   * DataFlavor.selectBestTextFlavor. For non-text flavors, unknown
   * application MIME types are preferred, followed by known
   * application/x-java-* MIME types. Unknown application types are preferred
   * because if the user provides his own data flavor, it will likely be the
   * most descriptive one. For flavors which are otherwise equal, the
   * flavors' string representation are compared in the alphabetical order.
   */
  public static class DataFlavorComparator extends IndexedComparator {

    private static final Map exactTypes;
    private static final Map primaryTypes;
    private static final Map nonTextRepresentations;
    private static final Map textTypes;
    private static final Map decodedTextRepresentations;
    private static final Map encodedTextRepresentations;
    private static final Integer UNKNOWN_OBJECT_LOSES = Integer.MIN_VALUE;
    private static final Integer UNKNOWN_OBJECT_WINS = Integer.MAX_VALUE;
    private static final Long UNKNOWN_OBJECT_LOSES_L = Long.MIN_VALUE;
    private static final Long UNKNOWN_OBJECT_WINS_L = Long.MAX_VALUE;
    private static final long serialVersionUID = 1064616167465172951L;

    static {
      HashMap exactTypesMap = new HashMap(4, 1.0f);

      // application/x-java-* MIME types
      exactTypesMap.put("application/x-java-file-list", 0);
      exactTypesMap.put(DataFlavor.javaSerializedObjectMimeType, 1);
      exactTypesMap.put(DataFlavor.javaJVMLocalObjectMimeType, 2);
      exactTypesMap.put(DataFlavor.javaRemoteObjectMimeType, 3);

      exactTypes = Collections.unmodifiableMap(exactTypesMap);

      HashMap primaryTypesMap = new HashMap(1, 1.0f);

      primaryTypesMap.put("application", 0);

      primaryTypes = Collections.unmodifiableMap(primaryTypesMap);

      HashMap nonTextRepresentationsMap = new HashMap(3, 1.0f);

      nonTextRepresentationsMap.put(InputStream.class, 0);
      nonTextRepresentationsMap.put(Serializable.class, 1);

      Class<?> remoteClass = remoteClass();
      if (remoteClass != null) {
        nonTextRepresentationsMap.put(remoteClass, 2);
      }

      nonTextRepresentations = Collections.unmodifiableMap(nonTextRepresentationsMap);

      HashMap textTypesMap = new HashMap(16, 1.0f);

      // plain text
      textTypesMap.put(SystemFlavorMap.TEXT_PLAIN_BASE_TYPE, 0);

      // stringFlavor
      textTypesMap.put(DataFlavor.javaSerializedObjectMimeType, 1);

      // misc
      textTypesMap.put("text/calendar", 2);
      textTypesMap.put("text/css", 3);
      textTypesMap.put("text/directory", 4);
      textTypesMap.put("text/parityfec", 5);
      textTypesMap.put("text/rfc822-headers", 6);
      textTypesMap.put("text/t140", 7);
      textTypesMap.put("text/tab-separated-values", 8);
      textTypesMap.put("text/uri-list", 9);

      // enriched
      textTypesMap.put("text/richtext", 10);
      textTypesMap.put("text/enriched", 11);
      textTypesMap.put("text/rtf", 12);

      // markup
      textTypesMap.put("text/html", 13);
      textTypesMap.put("text/xml", 14);
      textTypesMap.put("text/sgml", 15);

      textTypes = Collections.unmodifiableMap(textTypesMap);

      HashMap decodedTextRepresentationsMap = new HashMap(4, 1.0f);

      decodedTextRepresentationsMap.put(char[].class, 0);
      decodedTextRepresentationsMap.put(CharBuffer.class, 1);
      decodedTextRepresentationsMap.put(String.class, 2);
      decodedTextRepresentationsMap.put(Reader.class, 3);

      decodedTextRepresentations = Collections.unmodifiableMap(decodedTextRepresentationsMap);

      HashMap encodedTextRepresentationsMap = new HashMap(3, 1.0f);

      encodedTextRepresentationsMap.put(byte[].class, 0);
      encodedTextRepresentationsMap.put(ByteBuffer.class, 1);
      encodedTextRepresentationsMap.put(InputStream.class, 2);

      encodedTextRepresentations = Collections.unmodifiableMap(encodedTextRepresentationsMap);
    }

    private final CharsetComparator charsetComparator;

    public DataFlavorComparator() {
      this(SELECT_BEST);
    }

    public DataFlavorComparator(boolean order) {
      super(order);

      charsetComparator = new CharsetComparator(order);
    }

    /**
     * Returns java.rmi.Remote.class if RMI is present; otherwise {@code null}.
     */
    static Class<?> remoteClass() {
      return RMI.remoteClass;
    }

    @Override
    public int compare(Object obj1, Object obj2) {
      DataFlavor flavor1;
      DataFlavor flavor2;
      if (order == SELECT_BEST) {
        flavor1 = (DataFlavor) obj1;
        flavor2 = (DataFlavor) obj2;
      } else {
        flavor1 = (DataFlavor) obj2;
        flavor2 = (DataFlavor) obj1;
      }

      if (flavor1.equals(flavor2)) {
        return 0;
      }

      int comp;

      String primaryType1 = flavor1.getPrimaryType();
      String subType1 = flavor1.getSubType();
      String mimeType1 = primaryType1 + "/" + subType1;
      Class class1 = flavor1.getRepresentationClass();

      String primaryType2 = flavor2.getPrimaryType();
      String subType2 = flavor2.getSubType();
      String mimeType2 = primaryType2 + "/" + subType2;
      Class class2 = flavor2.getRepresentationClass();

      if (flavor1.isFlavorTextType() && flavor2.isFlavorTextType()) {
        // First, compare MIME types
        comp = compareIndices(textTypes, mimeType1, mimeType2, UNKNOWN_OBJECT_LOSES);
        if (comp != 0) {
          return comp;
        }

        // Only need to test one flavor because they both have the
        // same MIME type. Also don't need to worry about accidentally
        // passing stringFlavor because either
        //   1. Both flavors are stringFlavor, in which case the
        //      equality test at the top of the function succeeded.
        //   2. Only one flavor is stringFlavor, in which case the MIME
        //      type comparison returned a non-zero value.
        if (doesSubtypeSupportCharset(flavor1)) {
          // Next, prefer the decoded text representations of Reader,
          // String, CharBuffer, and [C, in that order.
          comp = compareIndices(decodedTextRepresentations, class1, class2, UNKNOWN_OBJECT_LOSES);
          if (comp != 0) {
            return comp;
          }

          // Next, compare charsets
          comp = charsetComparator.compareCharsets(getTextCharset(flavor1),
              getTextCharset(flavor2));
          if (comp != 0) {
            return comp;
          }
        }

        // Finally, prefer the encoded text representations of
        // InputStream, ByteBuffer, and [B, in that order.
        comp = compareIndices(encodedTextRepresentations, class1, class2, UNKNOWN_OBJECT_LOSES);
        if (comp != 0) {
          return comp;
        }
      } else {
        // First, prefer application types.
        comp = compareIndices(primaryTypes, primaryType1, primaryType2, UNKNOWN_OBJECT_LOSES);
        if (comp != 0) {
          return comp;
        }

        if (flavor1.isFlavorTextType()) {
          return 1;
        }

        if (flavor2.isFlavorTextType()) {
          return -1;
        }

        // Next, look for application/x-java-* types. Prefer unknown
        // MIME types because if the user provides his own data flavor,
        // it will likely be the most descriptive one.
        comp = compareIndices(exactTypes, mimeType1, mimeType2, UNKNOWN_OBJECT_WINS);
        if (comp != 0) {
          return comp;
        }

        // Finally, prefer the representation classes of Remote,
        // Serializable, and InputStream, in that order.
        comp = compareIndices(nonTextRepresentations, class1, class2, UNKNOWN_OBJECT_LOSES);
        if (comp != 0) {
          return comp;
        }
      }

      // The flavours are not equal but still not distinguishable.
      // Compare String representations in alphabetical order
      return flavor1.getMimeType().compareTo(flavor2.getMimeType());
    }
  }

  /*
   * Given the Map that maps objects to Integer indices and a boolean value,
   * this Comparator imposes a direct or reverse order on set of objects.
   * <p>
   * If the specified boolean value is SELECT_BEST, the Comparator imposes the
   * direct index-based order: an object A is greater than an object B if and
   * only if the index of A is greater than the index of B. An object that
   * doesn't have an associated index is less or equal than any other object.
   * <p>
   * If the specified boolean value is SELECT_WORST, the Comparator imposes the
   * reverse index-based order: an object A is greater than an object B if and
   * only if A is less than B with the direct index-based order.
   */
  public static class IndexOrderComparator extends IndexedComparator {
    private static final Integer FALLBACK_INDEX = Integer.MIN_VALUE;
    private static final long serialVersionUID = -4855480188670929103L;
    private final Map indexMap;

    public IndexOrderComparator(Map indexMap) {
      super(SELECT_BEST);
      this.indexMap = indexMap;
    }

    public IndexOrderComparator(Map indexMap, boolean order) {
      super(order);
      this.indexMap = indexMap;
    }

    @Override
    public int compare(Object obj1, Object obj2) {
      return order == SELECT_WORST ? -compareIndices(indexMap, obj1, obj2, FALLBACK_INDEX)
          : compareIndices(indexMap, obj1, obj2, FALLBACK_INDEX);
    }
  }

  /**
   * A class that provides access to java.rmi.Remote and java.rmi.MarshalledObject
   * without creating a static dependency.
   */
  private static final class RMI {
    private static final Class<?> remoteClass = getClass("java.rmi.Remote");
    private static final Class<?> marshallObjectClass = getClass("java.rmi.MarshalledObject");
    static final Constructor<?> marshallCtor = getConstructor(marshallObjectClass,
        Object.class);
    static final Method marshallGet = getMethod(marshallObjectClass, "get");

    private static Class<?> getClass(String name) {
      try {
        return Class.forName(name, true, null);
      } catch (ClassNotFoundException e) {
        return null;
      }
    }

    private static Constructor<?> getConstructor(Class<?> c, Class<?>... types) {
      try {
        return c == null ? null : c.getDeclaredConstructor(types);
      } catch (NoSuchMethodException x) {
        throw new AssertionError(x);
      }
    }

    private static Method getMethod(Class<?> c, String name, Class<?>... types) {
      try {
        return c == null ? null : c.getMethod(name, types);
      } catch (NoSuchMethodException e) {
        throw new AssertionError(e);
      }
    }

    /**
     * Returns {@code true} if the given class is java.rmi.Remote.
     */
    static boolean isRemote(Class<?> c) {
      return remoteClass == null ? null : remoteClass.isAssignableFrom(c);
    }
  }

  /**
   * Used for decoding and reencoding an InputStream on demand so that we
   * can strip NUL terminators and perform EOLN search-and-replace.
   */
  public class ReencodingInputStream extends InputStream {
    protected final char[] in = new char[2];
    protected BufferedReader wrapped;
    protected byte[] out;

    protected CharsetEncoder encoder;
    protected CharBuffer inBuf;
    protected ByteBuffer outBuf;

    protected char[] eoln;
    protected int numTerminators;

    protected boolean eos;
    protected int index, limit;

    public ReencodingInputStream(
        InputStream bytestream, long format, String targetEncoding, Transferable localeTransferable)
        throws IOException {
      Long lFormat = format;

      String sourceEncoding = null;
      if (isLocaleDependentTextFormat(format) &&
          localeTransferable != null &&
          localeTransferable.
              isDataFlavorSupported(javaTextEncodingFlavor)) {
        try {
          sourceEncoding = new String((byte[]) localeTransferable.
              getTransferData(javaTextEncodingFlavor), "UTF-8");
        } catch (UnsupportedFlavorException cannotHappen) {
        }
      } else {
        sourceEncoding = getCharsetForTextFormat(lFormat);
      }

      if (sourceEncoding == null) {
        // Only happens when we have a custom text type.
        sourceEncoding = getDefaultTextCharset();
      }
      wrapped = new BufferedReader(new InputStreamReader(bytestream, sourceEncoding));

      if (targetEncoding == null) {
        // Throw NullPointerException for compatibility with the former
        // call to sun.io.CharToByteConverter.getConverter(null)
        // (Charset.forName(null) throws unspecified IllegalArgumentException
        // now; see 6228568)
        throw new NullPointerException("null target encoding");
      }

      try {
        encoder = Charset.forName(targetEncoding).newEncoder();
        out = new byte[(int) (encoder.maxBytesPerChar() * 2 + 0.5)];
        inBuf = CharBuffer.wrap(in);
        outBuf = ByteBuffer.wrap(out);
      } catch (IllegalCharsetNameException | UnsupportedOperationException | UnsupportedCharsetException e) {
        throw new IOException(e.toString());
      }

      String sEoln = (String) nativeEOLNs.get(lFormat);
      if (sEoln != null) {
        eoln = sEoln.toCharArray();
      }

      // A hope and a prayer that this works generically. This will
      // definitely work on Win32.
      Integer terminators = (Integer) nativeTerminators.get(lFormat);
      if (terminators != null) {
        numTerminators = terminators;
      }
    }

    private int readChar() throws IOException {
      int c = wrapped.read();

      if (c == -1) { // -1 is EOS
        eos = true;
        return -1;
      }

      // "c == 0" is not quite correct, but good enough on Windows.
      if (numTerminators > 0 && c == 0) {
        eos = true;
        return -1;
      }
      if (eoln != null && matchCharArray(eoln, c)) {
        c = '\n' & 0xFFFF;
      }

      return c;
    }

    @Override
    public int read() throws IOException {
      if (eos) {
        return -1;
      }

      if (index >= limit) {
        // deal with supplementary characters
        int c = readChar();
        if (c == -1) {
          return -1;
        }

        in[0] = (char) c;
        in[1] = 0;
        inBuf.limit(1);
        if (Character.isHighSurrogate((char) c)) {
          c = readChar();
          if (c != -1) {
            in[1] = (char) c;
            inBuf.limit(2);
          }
        }

        inBuf.rewind();
        outBuf.limit(out.length).rewind();
        encoder.encode(inBuf, outBuf, false);
        outBuf.flip();
        limit = outBuf.limit();

        index = 0;

        return read();
      } else {
        int result = out[index] & 0xFF;
        index++;
        return result;
      }
    }

    @Override
    public int available() throws IOException {
      return eos ? 0 : limit - index;
    }

    @Override
    public void close() throws IOException {
      wrapped.close();
    }

    /**
     * Checks to see if the next array.length characters in wrapped
     * match array. The first character is provided as c. Subsequent
     * characters are read from wrapped itself. When this method returns,
     * the wrapped index may be different from what it was when this
     * method was called.
     */
    private boolean matchCharArray(char[] array, int c) throws IOException {
      wrapped.mark(array.length);  // BufferedReader supports mark

      int count = 0;
      if ((char) c == array[0]) {
        for (count = 1; count < array.length; count++) {
          c = wrapped.read();
          if (c == -1 || (char) c != array[count]) {
            break;
          }
        }
      }

      if (count == array.length) {
        return true;
      } else {
        wrapped.reset();
        return false;
      }
    }
  }
}
