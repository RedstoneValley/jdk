/*
 * Copyright (c) 2002, 2011, Oracle and/or its affiliates. All rights reserved.
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

package j2dbench;

import j2dbench.Option.ObjectChoice;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Vector;

public class Result {
  public static final int RATE_UNKNOWN = 0;

  public static final int WORK_OPS = 1;
  public static final int WORK_UNITS = 2;
  public static final int WORK_THOUSANDS = 4;
  public static final int WORK_MILLIONS = 6;
  public static final int WORK_AUTO = 8;

  public static final int TIME_SECONDS = 10;
  public static final int TIME_MILLIS = 11;
  public static final int TIME_MICROS = 12;
  public static final int TIME_NANOS = 13;
  public static final int TIME_AUTO = 14;
  private static final HashMap unitMap;
  static Group resultoptroot;
  static ObjectChoice timeOpt;
  static ObjectChoice workOpt;
  static ObjectChoice rateOpt;

  static {
    unitMap = new HashMap();
    unitMap.put("U", WORK_UNITS);
    unitMap.put("M", WORK_MILLIONS);
    unitMap.put("K", WORK_THOUSANDS);
    unitMap.put("A", WORK_AUTO);
    unitMap.put("MU", WORK_MILLIONS);
    unitMap.put("KU", WORK_THOUSANDS);
    unitMap.put("AU", WORK_AUTO);

    unitMap.put("O", WORK_UNITS | WORK_OPS);
    unitMap.put("NO", WORK_UNITS | WORK_OPS);
    unitMap.put("MO", WORK_MILLIONS | WORK_OPS);
    unitMap.put("KO", WORK_THOUSANDS | WORK_OPS);
    unitMap.put("AO", WORK_AUTO | WORK_OPS);

    unitMap.put("s", TIME_SECONDS);
    unitMap.put("m", TIME_MILLIS);
    unitMap.put("u", TIME_MICROS);
    unitMap.put("n", TIME_NANOS);
    unitMap.put("a", TIME_AUTO);
  }

  String unitname = "unit";
  final Test test;
  int repsPerRun;
  int unitsPerRep;
  final Vector times;
  Hashtable modifiers;
  Throwable error;

  public Result(Test test) {
    this.test = test;
    repsPerRun = 1;
    unitsPerRep = 1;
    times = new Vector();
  }

  public static void init() {
    resultoptroot = new Group(TestEnvironment.globaloptroot, "results", "Result Options");

    String[] workStrings = {
        "units", "kilounits", "megaunits", "autounits", "ops", "kiloops", "megaops", "autoops",};
    String[] workDescriptions = {
        "Test Units", "Thousands of Test Units", "Millions of Test Units", "Auto-scaled Test Units",
        "Operations", "Thousands of Operations", "Millions of Operations",
        "Auto-scaled Operations",};
    Integer[] workObjects = {
        WORK_UNITS, WORK_THOUSANDS, WORK_MILLIONS, WORK_AUTO, WORK_OPS | WORK_UNITS, WORK_OPS | WORK_THOUSANDS, WORK_OPS | WORK_MILLIONS,
        WORK_OPS | WORK_AUTO,};
    workOpt = new ObjectChoice(
        resultoptroot,
        "workunits",
        "Work Units",
        workStrings,
        workObjects,
        workStrings,
        workDescriptions,
        0);
    String[] timeStrings = {
        "sec", "msec", "usec", "nsec", "autosec",};
    String[] timeDescriptions = {
        "Seconds", "Milliseconds", "Microseconds", "Nanoseconds", "Auto-scaled seconds",};
    Integer[] timeObjects = {
        TIME_SECONDS, TIME_MILLIS, TIME_MICROS, TIME_NANOS, TIME_AUTO,};
    timeOpt = new ObjectChoice(
        resultoptroot,
        "timeunits",
        "Time Units",
        timeStrings,
        timeObjects,
        timeStrings,
        timeDescriptions,
        0);
    String[] rateStrings = {
        "unitspersec", "secsperunit",};
    String[] rateDescriptions = {
        "Work units per Time", "Time units per Work",};
    Boolean[] rateObjects = {
        Boolean.FALSE, Boolean.TRUE,};
    rateOpt = new ObjectChoice(
        resultoptroot,
        "ratio",
        "Rate Ratio",
        rateStrings,
        rateObjects,
        rateStrings,
        rateDescriptions,
        0);
  }

  public static int parseUnit(String c) {
    Integer u = (Integer) unitMap.get(c);
    if (u != null) {
      return u;
    }
    return RATE_UNKNOWN;
  }

  public void setReps(int reps) {
    repsPerRun = reps;
  }

  public void setUnits(int units) {
    unitsPerRep = units;
  }

  public void setUnitName(String name) {
    unitname = name;
  }

  public void addTime(long time) {
    if (J2DBench.printresults.isEnabled()) {
      System.out.println(test + " took " + time + "ms for " +
          getRepsPerRun() + " reps");
    }
    times.addElement(time);
  }

  public Throwable getError() {
    return error;
  }

  public void setError(Throwable t) {
    error = t;
  }

  public int getRepsPerRun() {
    return repsPerRun;
  }

  public int getUnitsPerRep() {
    return unitsPerRep;
  }

  public long getUnitsPerRun() {
    return (long) getRepsPerRun() * (long) getUnitsPerRep();
  }

  public Hashtable getModifiers() {
    return modifiers;
  }

  public void setModifiers(Hashtable modifiers) {
    this.modifiers = modifiers;
  }

  public long getNumRuns() {
    return times.size();
  }

  public long getTime(int index) {
    return (Long) times.elementAt(index);
  }

  public double getRepsPerSecond(int index) {
    return getRepsPerRun() * 1000.0 / getTime(index);
  }

  public double getUnitsPerSecond(int index) {
    return getUnitsPerRun() * 1000.0 / getTime(index);
  }

  public long getTotalReps() {
    return getRepsPerRun() * getNumRuns();
  }

  public long getTotalUnits() {
    return getUnitsPerRun() * getNumRuns();
  }

  public long getTotalTime() {
    long totalTime = 0;
    for (int i = 0; i < times.size(); i++) {
      totalTime += getTime(i);
    }
    return totalTime;
  }

  public double getAverageRepsPerSecond() {
    return getTotalReps() * 1000.0 / getTotalTime();
  }

  public double getAverageUnitsPerSecond() {
    return getTotalUnits() * 1000.0 / getTotalTime();
  }

  public String getAverageString() {
    int timeScale = timeOpt.getIntValue();
    int workScale = workOpt.getIntValue();
    boolean invertRate = rateOpt.getBooleanValue();
    double time = getTotalTime();
    String timeprefix = "";
    switch (timeScale) {
      case TIME_AUTO:
      case TIME_SECONDS:
        time /= 1000;
        break;
      case TIME_MILLIS:
        timeprefix = "m";
        break;
      case TIME_MICROS:
        time *= 1000.0;
        timeprefix = "u";
        break;
      case TIME_NANOS:
        time *= 1000000.0;
        timeprefix = "n";
        break;
    }

    String workprefix = "";
    boolean isOps = (workScale & WORK_OPS) != 0;
    String workname = isOps ? "op" : unitname;
    double work = isOps ? getTotalReps() : getTotalUnits();
    switch (workScale & ~WORK_OPS) {
      case WORK_AUTO:
      case WORK_UNITS:
        break;
      case WORK_THOUSANDS:
        work /= 1000.0;
        workprefix = "K";
        break;
      case WORK_MILLIONS:
        work /= 1000000.0;
        workprefix = "M";
        break;
    }
    if (invertRate) {
      double rate = time / work;
      if (timeScale == TIME_AUTO) {
        if (rate < 1.0) {
          rate *= 1000.0;
          timeprefix = "m";
          if (rate < 1.0) {
            rate *= 1000.0;
            timeprefix = "u";
            if (rate < 1.0) {
              rate *= 1000.0;
              timeprefix = "n";
            }
          }
        }
      }
      return rate + " " + timeprefix + "secs/" + workprefix + workname;
    } else {
      double rate = work / time;
      if (workScale == WORK_AUTO) {
        if (rate > 1000.0) {
          rate /= 1000.0;
          workprefix = "K";
          if (rate > 1000.0) {
            rate /= 1000.0;
            workprefix = "M";
          }
        }
      }
      return rate + " " + workprefix + workname + "s/" + timeprefix + "sec";
    }
  }

  public void summarize() {
    if (error != null) {
      System.out.println(test + " skipped due to " + error);
      error.printStackTrace(System.out);
    } else {
      System.out.println(test + " averaged " + getAverageString());
    }
    Enumeration enum_ = modifiers.keys();
    System.out.print("    with");
    String sep = " ";
    while (enum_.hasMoreElements()) {
      Modifier mod = (Modifier) enum_.nextElement();
      Object v = modifiers.get(mod);
      System.out.print(sep);
      System.out.print(mod.getAbbreviatedModifierDescription(v));
      sep = ", ";
    }
    System.out.println();
  }

  public void write(PrintWriter pw) {
    pw.println("  <result " +
        "num-reps=\"" + getRepsPerRun() + "\" " +
        "num-units=\"" + getUnitsPerRep() + "\" " +
        "name=\"" + test.getTreeName() + "\">");
    Enumeration enum_ = modifiers.keys();
    while (enum_.hasMoreElements()) {
      Modifier mod = (Modifier) enum_.nextElement();
      Object v = modifiers.get(mod);
      String val = mod.getModifierValueName(v);
      pw.println("    <option " +
          "key=\"" + mod.getTreeName() + "\" " +
          "value=\"" + val + "\"/>");
    }
    for (int i = 0; i < getNumRuns(); i++) {
      pw.println("    <time value=\"" + getTime(i) + "\"/>");
    }
    pw.println("  </result>");
  }
}
