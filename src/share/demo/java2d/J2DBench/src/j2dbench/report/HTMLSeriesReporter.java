/*
 * Copyright (c) 2005, 2011, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/*
 * This source code is provided to illustrate the usage of a given feature
 * or technique and has been deliberately simplified. Additional steps
 * required for a production-quality application, such as security checks,
 * input validation and proper error handling, might not be present in
 * this sample code.
 */

/*
  HTMLSeriesReporter.java
  <p>
  Show series data in graphical form.
 */

package j2dbench.report;

import j2dbench.report.J2DAnalyzer.ResultHolder;
import j2dbench.report.J2DAnalyzer.ResultSetHolder;
import j2dbench.report.J2DAnalyzer.SingleResultSetHolder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public final class HTMLSeriesReporter {

  static final Comparator numericComparator = new Comparator() {
    @Override
    public int compare(Object lhs, Object rhs) {
      double lval = -1;
      try {
        lval = Double.parseDouble((String) lhs);
      } catch (NumberFormatException pe) {
      }
      double rval = -1;
      try {
        rval = Double.parseDouble((String) rhs);
      } catch (NumberFormatException pe) {
      }
      double delta = lval - rval;

      return delta == 0 ? 0 : delta < 0 ? -1 : 1;
    }
  };
  /**
   * Flag to indicate - Generate new report or append to existing report
   */
  private static final int HTMLGEN_FILE_NEW = 1;
  private static final int HTMLGEN_FILE_UPDATE = 2;
  private static final DecimalFormat decimalFormat = new DecimalFormat("0.##");
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
      "EEE, MMM d, yyyy G 'at' HH:mm:ss z", Locale.US);
  /**
   * Path to results directory where all results are stored
   */
  public static String resultsDir = ".";
  /**
   * Holds the groups and corresponding group-display-names
   */
  public static List groups = new ArrayList();
  public static Map groupNames = new HashMap();
  /**
   * Level at which tests are grouped to be displayed in summary
   */
  public static int LEVEL = 2;
  static boolean logScale;

  private HTMLSeriesReporter() {
  }

  /**
   * Opens a File and returns a PrintWriter instance based on new/update
   * option specified in argument.
   */
  private static PrintWriter openFile(String name, int nSwitch) {

    FileOutputStream file = null;
    OutputStreamWriter writer = null;

    try {
      switch (nSwitch) {
        case 1: // HTMLGEN_FILE_NEW
          file = new FileOutputStream(name, false);
          break;
        case 2: // HTMLGEN_FILE_UPDATE
          file = new FileOutputStream(name, true);
          break;
      }
      writer = new OutputStreamWriter(file);
    } catch (IOException ee) {
      System.out.println("Error opening file: " + ee);
      System.exit(1);
    }

    return new PrintWriter(new BufferedWriter(writer));
  }

  private static void generateSeriesReport(String resultsDir, ArrayList xmlFileNames) {
    for (int i = 0; i < xmlFileNames.size(); ++i) {
      String xml = (String) xmlFileNames.get(i);
      try {
        J2DAnalyzer.readResults(xml);
      } catch (Exception e) {
        System.err.println("Error: " + e.getMessage());
      }
    }

    // first, display the values of system properties that distinguish the
    // sets, and the values of the system properties that are common to all sets

    File reportFile = new File(resultsDir, "series.html");
    PrintWriter w = openFile(reportFile.getAbsolutePath(), HTMLGEN_FILE_NEW);

    w.println("<html><body bgcolor='#ffffff'>");
    w.println("<hr size='1'/><center><h2>J2DBench Series</h2></center><hr size='1'/>");

    // collect system properties common to all result sets
    // and those unique to only some sets
    // first collect all the property keys.  these should be the same, but we'll play
    // it safe.

    // final since referenced from inner class comparator below
    SingleResultSetHolder[] results = new SingleResultSetHolder[J2DAnalyzer.results.size()];
    Set propKeys = new HashSet();
    for (int i = 0; i < results.length; ++i) {
      SingleResultSetHolder srsh = (SingleResultSetHolder) J2DAnalyzer.results.get(i);
      Map props = srsh.getProperties();
      Set keys = props.keySet();
      propKeys.addAll(keys);
      results[i] = srsh;
    }

    Map[] uniqueProps = new Map[results.length];
    Map commonProps = new HashMap();
    for (int i = 0; i < results.length; ++i) {
      Map m = new HashMap();
      m.putAll(results[i].getProperties());
      uniqueProps[i] = m;
    }

    {
      Iterator iter = propKeys.iterator();
      loop:
      while (iter.hasNext()) {
        Object k = iter.next();
        Object v = null;
        for (int i = 0; i < uniqueProps.length; ++i) {
          Map props = uniqueProps[i];
          if (i == 0) {
            v = props.get(k);
          } else {
            Object mv = props.get(k);
            if (v == null ? null != mv : !v.equals(mv)) {
              // not common, keep this key
              continue loop;
            }
          }
        }

        // common, so put value in commonProps and remove this key
        commonProps.put(k, v);
        for (Map uniqueProp : uniqueProps) {
          uniqueProp.remove(k);
        }
      }
    }

    String[] hexColor = {
        "#fc9505", "#fcd805", "#fc5c05", "#b5fc05", "1cfc05", "#05fc7a", "#44ff88", "#77ff77",
        "#aaff66", "#ddff55", "#ffff44", "#ffdd33",};
    Comparator comparator = new Comparator() {
      @Override
      public int compare(Object lhs, Object rhs) {
        return ((Comparable<String>) ((Entry) lhs).getKey()).compareTo((String) ((Entry) rhs).getKey());
      }
    };

    // write table of unique and common properties
    w.println("<br/>");
    w.println(
        "<table align='center' cols='2' cellspacing='0' cellpadding='0' border='0' width='80%'>");
    w.println("<tr><th colspan='2' bgcolor='#aaaaaa'>Result Set Properties</th></tr>");
    for (int i = 0; i < results.length; ++i) {
      String titl = results[i].getTitle();
      String desc = results[i].getDescription();
      w.println(
          "<tr bgcolor='" + hexColor[i % hexColor.length] + "'><th>" + titl + "</th><td>" + desc
              + "</td></tr>");
      TreeSet ts = new TreeSet(comparator);
      ts.addAll(uniqueProps[i].entrySet());
      for (Object t : ts) {
        Entry e = (Entry) t;
        w.println(
            "<tr><td width='30%'><b>" + e.getKey() + "</b></td><td>" + e.getValue() + "</td></tr>");
      }
    }

    w.println("<tr><th colspan='2'>&nbsp;</th></tr>");
    w.println("<tr><th colspan='2' bgcolor='#aaaaaa'>Common Properties</th></tr>");
    {
      TreeSet ts = new TreeSet(comparator);
      ts.addAll(commonProps.entrySet());
      for (Object t : ts) {
        Entry e = (Entry) t;
        w.println(
            "<tr><td width='30%'><b>" + e.getKey() + "</b></td><td>" + e.getValue() + "</td></tr>");
      }
    }
    w.println("<tr><th colspan='2'>&nbsp;</th></tr>");
    w.println("<tr><th colspan='2' bgcolor='#aaaaaa'>Common Test Options</th></tr>");
    {
      TreeSet ts = new TreeSet(String.CASE_INSENSITIVE_ORDER);
      ts.addAll(ResultHolder.commonkeys.keySet());
      for (Object key : ts) {
        Object val = ResultHolder.commonkeymap.get(key);
        w.println("<tr><td width='30%'><b>" + key + "</b></td><td>" + val + "</td></tr>");
      }
    }
    w.println("</table>");

    // for each test that appears in one or more result sets
    // for each option that has multiple values
    // for each value
    // for each result set
    // display count and bar

    Map testRuns = new HashMap(); // from test name to resultholders
    Set testNames = new TreeSet(String.CASE_INSENSITIVE_ORDER);
    for (SingleResultSetHolder result : results) {
      Enumeration en = result.getResultEnumeration();
      while (en.hasMoreElements()) {
        ResultHolder rh = (ResultHolder) en.nextElement();
        String name = rh.getName();
        testNames.add(name);

        ArrayList list = (ArrayList) testRuns.get(name);
        if (list == null) {
          list = new ArrayList();
          testRuns.put(name, list);
        }
        list.add(rh);
      }
    }

    w.println("<hr size='1' width='60%'/>");

    w.println("<br/>");
    w.println(
        "<table align='center' cols='2' cellspacing='0' cellpadding='0' border='0' width='80%'>");
    for (Object testName : testNames) {
      String name = (String) testName;
      w.println("<tr bgcolor='#aaaaaa'><th colspan='2'>" + name + "</th></tr>");

      double bestScore = 0;

      // get sorted list of variable options for this test
      // optionMap maps each option to a value map.  the value map contains all the values,
      // sorted depending on the value type (numeric or string).  it maps
      // from each (string) value to a list of all the resultholders for that value
      // value.

      Map optionMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
      ArrayList list = (ArrayList) testRuns.get(name);
      for (Object aList : list) {
        ResultHolder rh = (ResultHolder) aList;
        Hashtable options = rh.getOptions();
        Set entries = options.entrySet();
        for (Object entry : entries) {
          Entry e = (Entry) entry;
          Object key = e.getKey();
          if (ResultHolder.commonkeys.contains(key)) {
            continue;
          }
          Object val = e.getValue();

          Map vmap = (Map) optionMap.get(key);
          if (vmap == null) {
            // determine how to sort
            boolean numeric = false;
            try {
              Integer.parseInt((String) val);
              numeric = true;
            } catch (NumberFormatException pe) {
            }

            Comparator c = numeric ? numericComparator : String.CASE_INSENSITIVE_ORDER;
            vmap = new TreeMap(c);
            optionMap.put(key, vmap);
          }

          ArrayList vlist = (ArrayList) vmap.get(val);
          if (vlist == null) {
            vlist = new ArrayList();
            vmap.put(val, vlist);
          }
          vlist.add(rh);

          double score = rh.getScore();
          if (score > bestScore) {
            bestScore = score;
          }
        }
      }

      for (Object o2 : optionMap.keySet()) {
        String optionName = (String) o2;
        Map optionValues = (Map) optionMap.get(optionName);
        if (optionValues.size() == 1) {
          continue; // don't group by this if only one value
        }

        StringBuilder grouping = new StringBuilder();
        grouping.append("Grouped by ").append(optionName).append(", Result set");
        for (Object o1 : optionMap.keySet()) {
          String oname2 = (String) o1;
          if (oname2.equals(optionName)) {
            continue;
          }
          Map ov2 = (Map) optionMap.get(oname2);
          if (ov2.size() == 1) {
            continue;
          }
          grouping.append(", ").append(oname2);
          Iterator ov2i = ov2.entrySet().iterator();
          grouping.append(" (");
          boolean comma = false;
          while (ov2i.hasNext()) {
            if (comma) {
              grouping.append(", ");
            }
            grouping.append(((Entry) ov2i.next()).getKey());
            comma = true;
          }
          grouping.append(")");
        }
        w.println("<tr><td colspan='2'>&nbsp;</td></tr>");
        w.println("<tr><td colspan='2'><b>" + grouping + "</b></td></tr>");
        for (Object o : optionValues.keySet()) {
          String valueName = (String) o;
          w.print(
              "<tr><td align='right' valign='center' width='10%'>" + valueName + "&nbsp;</td><td>");
          ArrayList resultList = (ArrayList) optionValues.get(valueName);

          // sort the result list in order of the sets the results come from
          // we count on this being a stable sort, otherwise we'd have to also sort
          // within each result set on all other variables
          Comparator c = new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
              ResultSetHolder lh = ((ResultHolder) lhs).rsh;
              ResultSetHolder rh = ((ResultHolder) rhs).rsh;
              int li = -1;
              for (int k = 0; k < results.length; ++k) {
                if (results[k] == lh) {
                  li = k;
                  break;
                }
              }
              int ri = -1;
              for (int k = 0; k < results.length; ++k) {
                if (results[k] == rh) {
                  ri = k;
                  break;
                }
              }
              return li - ri;
            }
          };

          w.println("   <div style='height: 5'>&nbsp;</div>");
          ResultHolder[] sorted = new ResultHolder[resultList.size()];
          sorted = (ResultHolder[]) resultList.toArray(sorted);
          Arrays.sort(sorted, c);
          for (ResultHolder holder : sorted) {
            String color = null;
            for (int n = 0; n < results.length; ++n) {
              if (results[n] == holder.rsh) {
                color = hexColor[n];
              }
            }
            double score = holder.getScore();
            int pix = 0;
            if (bestScore > 1) {
              double scale = logScale ? Math.log(score) / Math.log(bestScore) : score / bestScore;

              pix = (int) (scale * 80.0);
            }

            w.println("   <div style='width: " + pix +
                "%; height: 15; font-size: smaller; valign: center; background-color: " + color
                + "'>" +
                "<div align='right' style='height: 15'>" + (int) score + "&nbsp;</div></div>");
          }
          w.println("</td></tr>");
        }
      }

      w.println("<tr><td colspan='2'>&nbsp;</td></tr>");
    }
    w.println("</table>");
    w.println("<br/>");

    w.println("</body></html>");
    w.flush();
    w.close();
  }

  private static void printUsage() {
    String usage = "\njava HTMLSeriesReporter [options] resultfile...   " +
        "                                     \n\n" +
        "where options include:                " +
        "                                      \n" +
        "    -r | -results <result directory>  " +
        "directory to which reports are stored \n" +
        "    -ls                               " +
        "display using logarithmic scale       \n" +
        "    -resultxml | -xml <xml file path> " +
        "path to result XML                    \n" +
        "    -group | -g  <level>              " +
        "group-level for tests                 \n" +
        "                                      " +
        " [ 1 , 2 , 3 or 4 ]                   \n" +
        "    -analyzermode | -am               " +
        "mode to be used for finding score     \n" +
        "                                      " +
        " [ BEST , WORST , AVERAGE , MIDAVG ]  ";
    System.out.println(usage);
    System.exit(0);
  }

  /**
   * main
   */
  public static void main(String[] args) {

    String resDir = ".";
    ArrayList results = new ArrayList();
    int group;

        /* ---- Analysis Mode ----
            BEST    = 1;
            WORST   = 2;
            AVERAGE = 3;
            MIDAVG  = 4;
         ------------------------ */
    int analyzerMode = 4;

    try {

      for (int i = 0; i < args.length; i++) {
        if (args[i].startsWith("-ls")) {
          logScale = true;
        } else if (args[i].startsWith("-results") || args[i].startsWith("-r")) {
          i++;
          resDir = args[i];
        } else if (args[i].startsWith("-group") || args[i].startsWith("-g")) {
          i++;
          group = Integer.parseInt(args[i]);
          System.out.println("Grouping Level for tests: " + group);
        } else if (args[i].startsWith("-analyzermode") || args[i].startsWith("-am")) {
          i++;
          String strAnalyzerMode = args[i];
          if ("BEST".equalsIgnoreCase(strAnalyzerMode)) {
            analyzerMode = 0;
          } else if ("WORST".equalsIgnoreCase(strAnalyzerMode)) {
            analyzerMode = 1;
          } else if ("AVERAGE".equalsIgnoreCase(strAnalyzerMode)) {
            analyzerMode = 2;
          } else if ("MIDAVG".equalsIgnoreCase(strAnalyzerMode)) {
            analyzerMode = 3;
          } else {
            printUsage();
          }
          System.out.println("Analyzer-Mode: " + analyzerMode);
        } else {
          results.add(args[i]);
        }
      }
    } catch (Exception e) {
      printUsage();
    }

    if (resDir != null) {
      J2DAnalyzer.setMode(analyzerMode);

      generateSeriesReport(resDir, results);
    } else {
      printUsage();
    }
  }
}
