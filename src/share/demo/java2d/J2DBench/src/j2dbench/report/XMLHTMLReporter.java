/*
 * Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved.
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
  XMLHTMLReporter.java
  <p>
  Generates HTML reports from XML results

  @author Rakesh Menon
 */

package j2dbench.report;

import j2dbench.report.J2DAnalyzer.ResultHolder;
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
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import sun.awt.OSInfo;

public final class XMLHTMLReporter {

  /**
   * Flag to indicate - Generate new report or append to existing report
   */
  private static final int HTMLGEN_FILE_NEW = 1;
  private static final int HTMLGEN_FILE_UPDATE = 2;
  /**
   * Color -> Better, Same, Worse
   */
  private static final String[] color = {"#99FF99", "#CCFFFF", "#FFCC00"};
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
  public static final List groups = new ArrayList();
  public static final Map groupNames = new HashMap();
  /**
   * Level at which tests are grouped to be displayed in summary
   */
  public static int LEVEL = 2;
  /**
   * String for holding base-build and target-build version
   */
  private static String baseBuild = "";
  private static String targetBuild = "";

  private XMLHTMLReporter() {
  }

  public static void setGroupLevel(int level) {
    LEVEL = level;
  }

  /**
   * Add Test Group to the list
   */
  private static void addGroup(String testName) {

    String[] testNameSplit = testName.replace('.', '_').split("_");
    String group = testNameSplit[0];
    for (int i = 1; i < LEVEL; i++) {
      group = group + "." + testNameSplit[i];
    }

    if (!groups.contains(group)) {
      groups.add(group);
      groupNames.put(group, getDisplayGroupName(group));
    }
  }

  /**
   * Generate a Display Name for this group
   */
  private static String getDisplayGroupName(String group) {

    String[] groupSplit = group.replace('.', '_').split("_");

    StringBuilder groupName = new StringBuilder();
    String tempName;

    for (int i = 0; i < groupSplit.length; i++) {
      tempName = groupSplit[i].substring(0, 1).toUpperCase() + groupSplit[i].substring(1);
      if (i == 0) {
        groupName.append(tempName);
      } else {
        groupName.append(" ").append(tempName);
      }
    }

    return groupName.toString();
  }

  /**
   * Get the group to which this testcase belongs
   */
  private static String getGroup(String testName) {

    String[] testNameSplit = testName.replace('.', '_').split("_");
    String group = testNameSplit[0];
    for (int i = 1; i < LEVEL; i++) {
      group = group + "." + testNameSplit[i];
    }

    return group;
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

  /**
   * Generate an HTML report based on the XML results file passed -
   * J2DBench_Results.html
   */
  public static void generateReport(String resultsDir, String xmlFileName) {

    try {

      String strhtml = null;
      String strstr = null;
      String[] tempstr2 = new String[2];
      String[] tempstr = new String[2];

      J2DAnalyzer.readResults(xmlFileName);
      SingleResultSetHolder srsh = (SingleResultSetHolder) J2DAnalyzer.results.elementAt(0);
      Enumeration enum_ = srsh.getKeyEnumeration();
      Vector keyvector = new Vector();
      while (enum_.hasMoreElements()) {
        keyvector.add(enum_.nextElement());
      }
      String[] keys = new String[keyvector.size()];
      keyvector.copyInto(keys);
      J2DAnalyzer.sort(keys);

      File reportFile = new File(resultsDir, "J2DBench_Results.html");
      PrintWriter writer = openFile(reportFile.getAbsolutePath(), HTMLGEN_FILE_NEW);

      writer.println("<html><body bgcolor=\"#ffffff\"><hr size=\"1\">");
      writer.println("<center><h2>J2DBench2 - Report</h2>");
      writer.println("</center><hr size=\"1\"><br>");
      writer.println("<table cols=\"2\" cellspacing=\"2\" " +
          "cellpadding=\"5\" " +
          "border=\"0\" width=\"80%\">");
      writer.println(
          "<tr><td bgcolor=\"#CCCCFF\" colspan=\"2\">" + "<b>Build Details</b></td></tr>");
      writer.println("<tr>");
      writer.println("<td bgcolor=\"#f0f0f0\">Description</td>");
      writer.println("<td>" + srsh.getDescription() + "</td>");
      writer.println("</tr>");
      writer.println("<tr><td bgcolor=\"#f0f0f0\">From Date</td>");
      writer.println("<td>" +
          dateFormat.format(new Date(srsh.getStartTime())) +
          "</td></tr>");
      writer.println("<tr><td bgcolor=\"#f0f0f0\">To Date</td>");
      writer.println("<td>" +
          dateFormat.format(new Date(srsh.getEndTime())) +
          "</td></tr>");
      writer.flush();

      //System Properties
      writer.println("<tr><td bgcolor=\"#CCCCFF\"><b>System Property</b>" +
          "</td><td bgcolor=\"#CCCCFF\">" +
          "<b>Value</b></td></tr>");
      String key;
      String value;
      Map sysProps = srsh.getProperties();
      for (Object o : sysProps.keySet()) {
        key = o.toString();
        value = sysProps.get(key).toString();
        writer.println("<tr><td bgcolor=\"#f0f0f0\">" +
            key + "</td><td>" + value + "&nbsp;</td></tr>");
      }
      writer.flush();

      writer.println("</table>");
      writer.println("<br>");
      writer.println("<hr size=\"1\">");
      writer.println("<br>");

      writer.println("<table cellspacing=\"0\" " + "cellpadding=\"3\" border=\"1\" width=\"80%\">");
      writer.println("<tr>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Num Reps</b></td>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Num Units</b></td>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Name</b></td>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Options</b></td>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Score</b></td>");
      writer.println("</tr>");
      writer.flush();

      for (String key1 : keys) {

        ResultHolder testResult = srsh.getResultByKey(key1);

        writer.println("<tr>");
        writer.println("<td>" + testResult.getReps() + "</td>");
        writer.println("<td>" + testResult.getUnits() + "</td>");
        writer.println("<td>" + testResult.getName() + "</td>");
        writer.println("<td valign=\"center\"><ul>");
        Map map = testResult.getOptions();
        for (Object o : map.keySet()) {
          key = o.toString();
          value = map.get(key).toString();
          writer.println("<li>" + key + " = " + value + "</li>");
        }
        writer.println("</ul></td>");
        writer.println("<td valign=\"center\">" +
            decimalFormat.format(testResult.getScore()) +
            "</td>");
        writer.println("</tr>");
      }
      writer.flush();

      writer.println("</table>");

      writer.println("<br><hr WIDTH=\"100%\" size=\"1\">");
      writer.println("</p><hr WIDTH=\"100%\" size=\"1\"></body></html>");

      writer.flush();
      writer.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Generate the reports from the base & target result XML
   */
  public static void generateComparisonReport(
      String resultsDir, String baseXMLFileName, String targetXMLFileName) {

    XMLHTMLReporter.resultsDir = resultsDir;

    //Get Base XML File ResultSetHolder
    J2DAnalyzer.readResults(baseXMLFileName);
    SingleResultSetHolder baseSRSH = (SingleResultSetHolder) J2DAnalyzer.results.elementAt(0);
    Enumeration baseEnum_ = baseSRSH.getKeyEnumeration();
    Vector baseKeyvector = new Vector();
    while (baseEnum_.hasMoreElements()) {
      baseKeyvector.add(baseEnum_.nextElement());
    }
    String[] baseKeys = new String[baseKeyvector.size()];
    baseKeyvector.copyInto(baseKeys);
    J2DAnalyzer.sort(baseKeys);

    //Get Target XML File ResultSetHolder
    J2DAnalyzer.readResults(targetXMLFileName);
    SingleResultSetHolder targetSRSH = (SingleResultSetHolder) J2DAnalyzer.results.elementAt(1);
    Enumeration targetEnum_ = baseSRSH.getKeyEnumeration();
    Vector targetKeyvector = new Vector();
    while (targetEnum_.hasMoreElements()) {
      targetKeyvector.add(targetEnum_.nextElement());
    }
    String[] targetKeys = new String[targetKeyvector.size()];
    targetKeyvector.copyInto(targetKeys);
    J2DAnalyzer.sort(targetKeys);

    baseBuild = (String) baseSRSH.getProperties().get("java.vm.version");
    targetBuild = (String) targetSRSH.getProperties().get("java.vm.version");
    generateSysPropsReport(targetSRSH);

    File reportFile = new File(resultsDir, "J2DBench_Complete_Report.html");
    PrintWriter writer = openFile(reportFile.getAbsolutePath(), HTMLGEN_FILE_NEW);

    String header = getHeader(baseSRSH,
        targetSRSH,
        "J2DBench - Complete Report",
        "System_Properties.html");
    writer.println(header);
    writer.flush();

    StringBuilder startTags = new StringBuilder();
    startTags.append("<tr>");
    startTags.append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Num Reps</b></td>");
    startTags.append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Num Units</b></td>");
    startTags.append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Name</b></td>");
    startTags.append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Options</b></td>");
    startTags
        .append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>")
        .append(baseBuild)
        .append(" Score</b></td>");
    startTags
        .append("<td bgcolor=\"#CCCCFF\" align=\"center\"><b>")
        .append(targetBuild)
        .append(" Score</b></td>");
    startTags.append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>% Speedup</b></td>");
    startTags.append("</tr>");

    StringBuffer worseResultTags = new StringBuffer(startTags.toString());
    StringBuffer sameResultTags = new StringBuffer(startTags.toString());
    StringBuffer betterResultTags = new StringBuffer(startTags.toString());

    Map consolBaseRes = new HashMap();
    Map consolTargetResult = new HashMap();

    Map testCaseBaseResult = new HashMap();
    Map testCaseResultCount = new HashMap();
    Map testCaseTargetResult = new HashMap();

    for (String targetKey : targetKeys) {

      ResultHolder baseTCR = baseSRSH.getResultByKey(targetKey);
      ResultHolder targetTCR = targetSRSH.getResultByKey(targetKey);

      Object curTestCountObj = testCaseResultCount.get(baseTCR.getName());
      int curTestCount = 0;
      if (curTestCountObj != null) {
        curTestCount = (Integer) curTestCountObj;
      }
      curTestCount++;
      testCaseBaseResult.put(baseTCR.getName() + "_" +
          (curTestCount - 1), baseTCR);
      testCaseTargetResult.put(targetTCR.getName() + "_" +
          (curTestCount - 1), targetTCR);
      testCaseResultCount.put(baseTCR.getName(), curTestCount);

      /*****************************************************************
       Add the Test to Group List
       */
      addGroup(baseTCR.getName());

      double baseScore = baseTCR.getScore();
      double targetScore = targetTCR.getScore();

      int selColorIndex = selectColor(baseScore, targetScore);

      StringBuilder tagBuffer = new StringBuilder();

      tagBuffer.append("<tr bgcolor=\"").append(color[selColorIndex]).append("\">");
      tagBuffer.append("<td align=\"center\">").append(baseTCR.getScore()).append("</td>");
      tagBuffer.append("<td align=\"center\">").append(baseTCR.getUnits()).append("</td>");
      tagBuffer.append("<td align=\"center\">").append(baseTCR.getName()).append("</td>");
      tagBuffer.append("<td valign=\"center\"><ul>");
      Map map = baseTCR.getOptions();
      for (Object o : map.keySet()) {
        Object key = o.toString();
        Object value = map.get(key).toString();
        tagBuffer.append("<li>").append(key).append(" = ").append(value).append("</li>");
      }
      tagBuffer.append("</ul></td>");
      tagBuffer
          .append("<td valign=\"center\" align=\"center\">")
          .append(decimalFormat.format(baseTCR.getScore()))
          .append("</td>");
      tagBuffer
          .append("<td valign=\"center\" align=\"center\">")
          .append(decimalFormat.format(targetTCR.getScore()))
          .append("</td>");
      tagBuffer
          .append("<td valign=\"center\" align=\"center\">")
          .append(decimalFormat.format(calculateSpeedupPercentage(baseTCR.getScore(),
              targetTCR.getScore())))
          .append("</td>");
      tagBuffer.append("</tr>");

      switch (selColorIndex) {
        case 0:
          betterResultTags.append(tagBuffer);
          break;
        case 1:
          sameResultTags.append(tagBuffer);
          break;
        case 2:
          worseResultTags.append(tagBuffer);
          break;
      }

      Object curTotalScoreObj = consolBaseRes.get(baseTCR.getName());
      double curTotalScore = 0;
      if (curTotalScoreObj != null) {
        curTotalScore = (Double) curTotalScoreObj;
      }
      curTotalScore += baseTCR.getScore();
      consolBaseRes.put(baseTCR.getName(), curTotalScore);

      curTotalScoreObj = consolTargetResult.get(targetTCR.getName());
      curTotalScore = 0;
      if (curTotalScoreObj != null) {
        curTotalScore = (Double) curTotalScoreObj;
      }
      curTotalScore += targetTCR.getScore();
      consolTargetResult.put(targetTCR.getName(), curTotalScore);
    }

    writer.println("<br><hr WIDTH=\"100%\" size=\"1\">");
    writer.println("<A NAME=\"results\"></A><H3>Results:</H3>");

    writer.println("<table cellspacing=\"0\" " + "cellpadding=\"3\" border=\"1\" width=\"80%\">");

    writer.println("<tr><td colspan=\"7\" bgcolor=\"#f0f0f0\">" +
        "<font size=\"+1\">Tests which run BETTER on target" +
        "</font></td></tr>");
    writer.println(betterResultTags);
    writer.flush();

    writer.println("<tr><td colspan=\"7\">&nbsp;<br>&nbsp;</td></tr>");
    writer.println("<tr><td colspan=\"7\" bgcolor=\"#f0f0f0\">" +
        "<font size=\"+1\">Tests which run " +
        "SAME on target</font></td></tr>");
    writer.println(sameResultTags);
    writer.flush();

    writer.println("<tr><td colspan=\"7\">&nbsp;<br>&nbsp;</td></tr>");
    writer.println("<tr><td colspan=\"7\" bgcolor=\"#f0f0f0\">" +
        "<font size=\"+1\">Tests which run WORSE on target" +
        "</font></td></tr>");
    writer.println(worseResultTags);
    writer.flush();

    writer.println("</table>");

    writer.println(getFooter());
    writer.flush();

    writer.close();

    generateTestCaseSummaryReport(baseSRSH,
        targetSRSH,
        consolBaseRes,
        consolTargetResult,
        testCaseBaseResult,
        testCaseResultCount,
        testCaseTargetResult);

    generateGroupSummaryReport(baseSRSH,
        targetSRSH,
        consolBaseRes,
        consolTargetResult,
        testCaseBaseResult,
        testCaseResultCount,
        testCaseTargetResult);
  }

  /**
   * Generate Group-Summary report - Summary_Report.html
   */
  private static void generateGroupSummaryReport(
      SingleResultSetHolder baseSRSH, SingleResultSetHolder targetSRSH, Map consolBaseResult,
      Map consolTargetResult, Map testCaseBaseResult, Map testCaseResultCount,
      Map testCaseTargetResult) {

    File groupSummaryReportFile = new File(resultsDir, "Summary_Report.html");
    PrintWriter writer = openFile(groupSummaryReportFile.getAbsolutePath(), HTMLGEN_FILE_NEW);

    String header = getHeader(baseSRSH,
        targetSRSH,
        "J2DBench - Summary Report",
        "System_Properties.html");
    writer.println(header);
    writer.flush();

    writer.println("<br><hr size=\"1\">");

    Map baseValuesMap = new HashMap();
    Map targetValuesMap = new HashMap();

    String tempGroup;
    for (int i = 0; i < groups.size(); i++) {
      tempGroup = groups.get(i).toString();
      baseValuesMap.put(tempGroup, 0d);
      targetValuesMap.put(tempGroup, 0d);
    }

    Object key;
    double baseValue, targetValue;
    Iterator resultsIter = consolBaseResult.keySet().iterator();

    while (resultsIter.hasNext()) {

      key = resultsIter.next();

      baseValue = (Double) consolBaseResult.get(key);
      targetValue = (Double) consolTargetResult.get(key);

      tempGroup = getGroup(key.toString());

      Object curTotalScoreObj;
      double curTotalScore = 0;

      curTotalScoreObj = baseValuesMap.get(tempGroup);
      if (curTotalScoreObj != null) {
        curTotalScore = (Double) curTotalScoreObj;
      }
      curTotalScore += baseValue;
      baseValuesMap.put(tempGroup, curTotalScore);

      curTotalScore = 0;
      curTotalScoreObj = targetValuesMap.get(tempGroup);
      if (curTotalScoreObj != null) {
        curTotalScore = (Double) curTotalScoreObj;
      }
      curTotalScore += targetValue;
      targetValuesMap.put(tempGroup, curTotalScore);
    }

    writer.println("<A NAME=\"results_summary\"></A>" + "<H3>Results Summary:</H3>");
    writer.println(
        "<table cols=\"4\" cellspacing=\"0\" " + "cellpadding=\"3\" border=\"1\" width=\"80%\">");
    writer.println("<TR BGCOLOR=\"#CCCCFF\">");
    writer.println("<TD><B>Testcase</B></TD>");
    writer.println("<TD align=\"center\"><B>Score for " + baseBuild +
        "</B></TD>");
    writer.println("<TD align=\"center\"><B>Score for " + targetBuild +
        "</B></TD>");
    writer.println("<TD align=\"center\"><B>% Speedup</TD>");
    writer.println("</TR>");

    StringBuffer betterResultTags = new StringBuffer();
    StringBuffer sameResultTags = new StringBuffer();
    StringBuffer worseResultTags = new StringBuffer();

    resultsIter = baseValuesMap.keySet().iterator();

    double speedup;

    while (resultsIter.hasNext()) {

      key = resultsIter.next();

      baseValue = (Double) baseValuesMap.get(key);
      targetValue = (Double) targetValuesMap.get(key);
      speedup = calculateSpeedupPercentage(baseValue, targetValue);

      int selColorIndex = selectColor(baseValue, targetValue);

      String tcFileName = key.toString().replace('.', '_');
      tcFileName = tcFileName.toLowerCase() + ".html";

      switch (selColorIndex) {
        case 0:
          betterResultTags.append("<tr bgcolor=\"").append(color[selColorIndex]).append("\">");
          betterResultTags
              .append("<td><a href=" + "\"Testcase_Summary_Report.html#status_")
              .append(key)
              .append("\">")
              .append(groupNames.get(key))
              .append("</a></td>");
          betterResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(baseValue))
              .append("</td>");
          betterResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(targetValue))
              .append("</td>");
          betterResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(speedup))
              .append("</td>");
          betterResultTags.append("</tr>");
          break;
        case 1:
          sameResultTags.append("<tr bgcolor=\"").append(color[selColorIndex]).append("\">");
          sameResultTags
              .append("<td>" + "<a href=\"Testcase_Summary_Report.html#status_")
              .append(key)
              .append("\">")
              .append(groupNames.get(key))
              .append("</a></td>");
          sameResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(baseValue))
              .append("</td>");
          sameResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(targetValue))
              .append("</td>");
          sameResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(speedup))
              .append("</td>");
          sameResultTags.append("</tr>");
          break;
        case 2:
          worseResultTags.append("<tr bgcolor=\"").append(color[selColorIndex]).append("\">");
          worseResultTags
              .append("<td>" + "<a href=\"Testcase_Summary_Report.html#status_")
              .append(key)
              .append("\">")
              .append(groupNames.get(key))
              .append("</a></td>");
          worseResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(baseValue))
              .append("</td>");
          worseResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(targetValue))
              .append("</td>");
          worseResultTags
              .append("<td align=\"center\">")
              .append(decimalFormat.format(speedup))
              .append("</td>");
          worseResultTags.append("</tr>");
          break;
      }
    }

    writer.println(betterResultTags);
    writer.flush();

    writer.println(sameResultTags);
    writer.flush();

    writer.println(worseResultTags);
    writer.flush();

    writer.println("</table>");

    writer.println(getFooter());
    writer.flush();
    writer.close();
  }

  /**
   * Generate Testcase Summary Report - Testcase_Summary_Report.html
   */
  private static void generateTestCaseSummaryReport(
      SingleResultSetHolder baseSRSH, SingleResultSetHolder targetSRSH, Map consolBaseResult,
      Map consolTargetResult, Map testCaseBaseResult, Map testCaseResultCount,
      Map testCaseTargetResult) {

    File tcSummaryReportFile = new File(resultsDir, "Testcase_Summary_Report.html");
    PrintWriter writer = openFile(tcSummaryReportFile.getAbsolutePath(), HTMLGEN_FILE_NEW);

    String header = getHeader(baseSRSH,
        targetSRSH,
        "J2DBench - Testcase Summary Report",
        "System_Properties.html");
    writer.println(header);
    writer.flush();

    StringBuilder testResultsStartBuffer = new StringBuilder();
    testResultsStartBuffer.append("<TR BGCOLOR=\"#CCCCFF\">");
    testResultsStartBuffer.append("<TD><B>Testcase</B></TD>");
    testResultsStartBuffer
        .append("<TD align=\"center\"><B>Score for ")
        .append(baseBuild)
        .append("</B></TD>");
    testResultsStartBuffer
        .append("<TD align=\"center\"><B>Score for ")
        .append(targetBuild)
        .append("</B></TD>");
    testResultsStartBuffer.append("<TD align=\"center\"><B>% Speedup</TD>");
    testResultsStartBuffer.append("</TR>");

    StringBuffer testResultsScoreBuffer = new StringBuffer();
    testResultsScoreBuffer.append("<table cols=\"4\" cellspacing=\"0\" " +
        "cellpadding=\"3\" border=\"1\" " +
        "width=\"80%\">");

    StringBuilder betterResultTags = new StringBuilder();
    StringBuilder sameResultTags = new StringBuilder();
    StringBuilder worseResultTags = new StringBuilder();

    Double baseValue = null, targetValue = null;

    String curGroupName;
    String curTestName;

    Object[] groupNameArray = groups.toArray();
    Arrays.sort(groupNameArray);

    Object[] testCaseList = consolBaseResult.keySet().toArray();
    Arrays.sort(testCaseList);

    writer.println("<br><hr size=\"1\"><br>");
    writer.println("<A NAME=\"status\"></A><H3>Status:</H3>");

    writer.println("<table cellspacing=\"0\" " + "cellpadding=\"3\" border=\"1\" width=\"80%\">");

    for (int j = 0; j < groupNameArray.length; j++) {

      if (j != 0) {
        testResultsScoreBuffer.append("<tr><td colspan=\"4\">&nbsp;" + "<br>&nbsp;</td></tr>");
        writer.println("<tr><td colspan=\"5\">&nbsp;<br>&nbsp;" + "</td></tr>");
      }

      curGroupName = groupNameArray[j].toString();

      writer.println("<tr><td colspan=\"5\" valign=\"center\" " +
          "bgcolor=\"#f0f0f0\">" +
          "<A NAME=\"status_" + curGroupName + "\"></A>" +
          "<font size=\"+1\">Status - " +
          groupNames.get(curGroupName) + "</font></td></tr>");
      writer.println("<tr>");
      writer.println("<td bgcolor=\"#CCCCFF\"><b>Tests " + "Performance</b></td>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>BETTER (Num / %)</b></td>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>SAME (Num / %)</b></td>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>WORSE (Num / %)</b></td>");
      writer.println("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Total</b></td>");
      writer.println("</tr>");
      writer.flush();

      testResultsScoreBuffer
          .append("<tr><td colspan=\"4\" " + "valign=\"center\" " + "bgcolor=\"#f0f0f0\">"
              + "<A NAME=\"test_result_")
          .append(curGroupName)
          .append("\"></A><font size=\"+1\">")
          .append("Test Results - ")
          .append(groupNames.get(curGroupName))
          .append("</font></td></tr>");
      testResultsScoreBuffer.append(testResultsStartBuffer);

      String[] tableTags;

      for (Object aTestCaseList : testCaseList) {

        curTestName = aTestCaseList.toString();

        if (curTestName.startsWith(curGroupName)) {

          tableTags = generateTestCaseReport(
              curGroupName,
              curTestName,
              baseSRSH,
              targetSRSH,
              testCaseResultCount,
              testCaseBaseResult,
              testCaseTargetResult);

          writer.println(tableTags[0]);
          writer.flush();

          testResultsScoreBuffer.append(tableTags[1]);
        }
      }
    }

    testResultsScoreBuffer.append("</table>");
    writer.println("</table>");

    writer.println("<br><hr size=\"1\"><br>");
    writer.println("<A NAME=\"test_results\"></A><H3>Test Results:</H3>");
    writer.println(testResultsScoreBuffer);
    writer.flush();

    writer.println(getFooter());
    writer.flush();

    writer.close();
  }

  /**
   * |----------|------------------------|--------------------------|-----------|
   * | Testcase | Score for <base build> | Score for <target build> | % Speedup |
   * |----------|------------------------|--------------------------|-----------|
   */
  private static String getTestResultsTableForSummary(
      String testName, double baseScore, double targetScore) {

    double totalScore = baseScore + targetScore;

    String fileName = testName.replace('.', '_');
    fileName = fileName.toLowerCase() + ".html";

    int selColorIndex = selectColor(baseScore, targetScore);

    String buffer = "<TR BGCOLOR=\"" + color[selColorIndex] + "\">" +
        "<TD><P><A HREF=\"testcases/" +
        fileName +
        "\">" +
        testName +
        "</A></P></TD>" +
        "<TD align=\"center\"><P><A HREF=\"testcases/" +
        fileName +
        "\"><B>" +
        decimalFormat.format(baseScore) +
        "</B></A></P></TD>" +
        "<TD align=\"center\"><P><A HREF=\"testcases/" +
        fileName +
        "\"><B>" +
        decimalFormat.format(targetScore) +
        "</B></A></P></TD>" +
        "<TD align=\"center\"><P><A HREF=\"testcases/" +
        fileName +
        "\"><B>" +
        decimalFormat.format(calculateSpeedupPercentage(baseScore, targetScore)) +
        "</B></A></P></TD>" +
        "</TR>";

    return buffer;
  }

  /**
   * |-------------------|-------------------|-----------------|-------------------|--------|
   * | Tests Performance | BETTER  (Num / %) | SAME  (Num / %) | WORSE  ( Num / %) | Total  |
   * |-------------------|-------------------|-----------------|-------------------|--------|
   */
  private static String getStatusTableForSummary(
      String curGroupName, String testName, int nBetter, int nSame, int nWorse) {

    String fileName = testName.replace('.', '_');
    fileName = fileName.toLowerCase() + ".html";

    int totalTests = nBetter + nSame + nWorse;

    String buffer = "<TR>" +
        "<TD><P><A HREF=\"#test_result_" +
        curGroupName +
        "\">" +
        testName +
        "</A></P></TD>" +
        "<TD BGCOLOR=\"#99FF99\" align=\"center\"><P>" + "<A HREF=\"#test_result_" +
        curGroupName +
        "\"><B>" +
        nBetter +
        "</A></B>&nbsp;&nbsp;&nbsp;&nbsp;(" +
        nBetter * 100 / totalTests +
        "%)</P></TD>" +
        "<TD BGCOLOR=\"#CCFFFF\" align=\"center\"><P>" + "<A HREF=\"#test_result_" +
        curGroupName +
        "\"><B>" +
        nSame +
        "</A></B>&nbsp;&nbsp;&nbsp;&nbsp;(" +
        nSame * 100 / totalTests +
        "%)</P></TD>" +
        "<TD BGCOLOR=\"#FFCC00\" align=\"center\"><P>" + "<A HREF=\"#test_result_" +
        curGroupName +
        "\"><B>" +
        nWorse +
        "</A></B>&nbsp;&nbsp;&nbsp;&nbsp;(" +
        nWorse * 100 / totalTests +
        "%)</P></TD>" +
        "<TD BGCOLOR=\"#FFFFFF\" align=\"center\"><P>" + "<A HREF=\"#test_result_" +
        curGroupName +
        "\"><B>" +
        totalTests +
        "</B></A></P></TD>" +
        "</TR>";

    return buffer;
  }

  /**
   * |-------------------|-----------------|------------------------------|
   * | Tests performance | Number of tests | % from total number of tests |
   * |-------------------|-----------------|------------------------------|
   */
  private static String getPerformanceTableForTestcase(
      String testName, int nBetter, int nSame, int nWorse) {

    String buffer = "<hr size=\"1\">" +
        "<H3>Status:</H3>" +
        "<table cols=\"4\" cellspacing=\"0\" " + "cellpadding=\"3\" border=\"1\" width=\"80%\">" +
        "<TR BGCOLOR=\"#CCCCFF\">" +
        "<TD align=\"center\"><B>Tests performance</B></TD>" +
        "<TD align=\"center\"><B>Number of tests</B></TD>" +
        "<TD align=\"center\"><B>% from total number of " + "tests</B></TD>" +
        "</TR>" +
        "<TR BGCOLOR=\"#99FF99\">" +
        "<TD><P><A HREF=\"#better\">" + "Target is at least 10 percent BETTER</A></P></TD>" +
        "<TD align=\"center\"><P><A HREF=\"#better\"><B>" +
        nBetter +
        "</B></A></P></TD>" +
        "<TD align=\"center\"><P>" +
        nBetter * 100 / totalTests +
        "</P></TD>" +
        "</TR>" +
        "<TR BGCOLOR=\"#CCFFFF\">" +
        "<TD><P><A HREF=\"#same\">" + "The same within 10 percent</A></P></TD>" +
        "<TD align=\"center\"><P><A HREF=\"#same\"><B>" +
        nSame +
        "</B></A></P></TD>" +
        "<TD align=\"center\"><P>" + nSame * 100 / totalTests + "</P></TD>" +
        "</TR>" +
        "<TR BGCOLOR=\"#FFCC00\">" +
        "<TD><P><A HREF=\"#worse\">" + "Target is at least 10 percent WORSE</A></P></TD>" +
        "<TD align=\"center\"><P><A HREF=\"#worse\"><B>" +
        nWorse +
        "</B></A></P></TD>" +
        "<TD align=\"center\"><P>" + nWorse * 100 / totalTests + "</P></TD>" +
        "</TR>" +
        "</TABLE>";

    int totalTests = nBetter + nSame + nWorse;

    return buffer;
  }

  /**
   * |-----------|---------|--------------------|----------------------|------------|
   * | Num Units | Options | <base build> Score | <target build> Score | % Speedup  |
   * |-----------|---------|--------------------|----------------------|------------|
   * <p>
   * String[0] = getStatusTableForSummary()
   * String[1] = getTestResultsTableForSummary()
   * <p>
   * Generate Testcase Report - testcases/<testcase name>.html
   */
  private static String[] generateTestCaseReport(
      String curGroupName, Object key, SingleResultSetHolder baseSRSH,
      SingleResultSetHolder targetSRSH, Map testCaseResultCount, Map testCaseBaseResult,
      Map testCaseTargetResult) {

    int numBetterTestCases = 0;
    int numWorseTestCases = 0;
    int numSameTestCases = 0;

    StringBuilder tcStartTags = new StringBuilder();
    tcStartTags.append("<tr>");
    tcStartTags.append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Num Units</b></td>");
    tcStartTags.append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>Options</b></td>");
    tcStartTags
        .append("<td bgcolor=\"#CCCCFF\" align=\"center\"><b>")
        .append(baseBuild)
        .append(" Score</b></td>");
    tcStartTags
        .append("<td bgcolor=\"#CCCCFF\" align=\"center\"><b>")
        .append(targetBuild)
        .append(" Score</b></td>");
    tcStartTags.append("<td bgcolor=\"#CCCCFF\" align=\"center\">" + "<b>% Speedup</b></td>");
    tcStartTags.append("</tr>");

    StringBuffer worseTestcaseResultTags = new StringBuffer(tcStartTags.toString());
    StringBuffer sameTestcaseResultTags = new StringBuffer(tcStartTags.toString());
    StringBuffer betterTestcaseResultTags = new StringBuffer(tcStartTags.toString());

    Object curTestCountObj = testCaseResultCount.get(key.toString());
    int curTestCount = 0;
    if (curTestCountObj != null) {
      curTestCount = (Integer) curTestCountObj;
    }

    String fileName = key.toString().replace('.', '_');
    fileName = fileName.toLowerCase() + ".html";
    File testcaseReportFile = new File(resultsDir + File.separator + "testcases", fileName);
    PrintWriter writer = openFile(testcaseReportFile.getAbsolutePath(), HTMLGEN_FILE_NEW);

    String header = getHeader(baseSRSH,
        targetSRSH,
        "J2DBench - " + key,
        "../System_Properties.html");
    writer.println(header);
    writer.flush();

    double totalBaseScore = 0;
    double totalTargetScore = 0;

    for (int i = 0; i < curTestCount; i++) {

      ResultHolder baseTCR = (ResultHolder) testCaseBaseResult.get(key + "_" + i);
      ResultHolder targetTCR = (ResultHolder) testCaseTargetResult.get(key + "_" + i);

      double baseScore = baseTCR.getScore();
      double targetScore = targetTCR.getScore();

      StringBuilder tcTagBuffer = new StringBuilder();

      int selColorIndex = selectColor(baseScore, targetScore);
      tcTagBuffer.append("<tr bgcolor=\"").append(color[selColorIndex]).append("\">");
      tcTagBuffer.append("<td align=\"center\">").append(baseTCR.getUnits()).append("</td>");
      tcTagBuffer.append("<td valign=\"center\">");

      Map map = baseTCR.getOptions();
      Iterator iter = map.keySet().iterator();
      Object subKey, subValue;
      tcTagBuffer.append("<ul>");
      while (iter.hasNext()) {
        subKey = iter.next().toString();
        subValue = map.get(subKey).toString();
        tcTagBuffer.append("<li>").append(subKey).append(" = ").append(subValue).append("</li>");
      }
      tcTagBuffer.append("</ul></td>");
      tcTagBuffer
          .append("<td valign=\"center\" align=\"center\">")
          .append(decimalFormat.format(baseTCR.getScore()))
          .append("</td>");
      tcTagBuffer
          .append("<td valign=\"center\" align=\"center\">")
          .append(decimalFormat.format(targetTCR.getScore()))
          .append("</td>");
      tcTagBuffer
          .append("<td valign=\"center\" align=\"center\">")
          .append(decimalFormat.format(calculateSpeedupPercentage(baseTCR.getScore(),
              targetTCR.getScore())))
          .append("</td>");
      tcTagBuffer.append("</tr>");

      totalBaseScore += baseTCR.getScore();
      totalTargetScore += targetTCR.getScore();

      switch (selColorIndex) {
        case 0:
          betterTestcaseResultTags.append(tcTagBuffer);
          numBetterTestCases++;
          break;
        case 1:
          sameTestcaseResultTags.append(tcTagBuffer);
          numSameTestCases++;
          break;
        case 2:
          worseTestcaseResultTags.append(tcTagBuffer);
          numWorseTestCases++;
          break;
      }
    }

    String performanceTable = getPerformanceTableForTestcase(key.toString(),
        numBetterTestCases,
        numSameTestCases,
        numWorseTestCases);

    writer.println(performanceTable);
    writer.flush();

    writer.println("<hr size=\"1\">");
    writer.println("<A NAME=\"details\"></A><H3>Details:</H3>");

    writer.println("<table cellspacing=\"0\" " + "cellpadding=\"3\" border=\"1\" width=\"80%\">");

    writer.println("<tr><td colspan=\"5\" " +
        "valign=\"center\" bgcolor=\"#f0f0f0\">" +
        "<a name=\"better\"></a><font size=\"+1\">" +
        key +
        " Tests which run BETTER on target</font></td></tr>");
    writer.println(betterTestcaseResultTags);
    writer.flush();

    writer.println("<tr><td colspan=\"5\">&nbsp;<br>&nbsp;</td></tr>");

    writer.println("<tr><td colspan=\"5\" " +
        "valign=\"center\" bgcolor=\"#f0f0f0\">" +
        "<a name=\"same\"></a><font size=\"+1\">" +
        key +
        " Tests which run SAME on target</font></td></tr>");
    writer.println(sameTestcaseResultTags);
    writer.flush();

    writer.println("<tr><td colspan=\"5\">&nbsp;<br>&nbsp;</td></tr>");

    writer.println("<tr><td colspan=\"5\" " +
        "valign=\"center\" bgcolor=\"#f0f0f0\">" +
        "<a name=\"worse\"></a><font size=\"+1\">" +
        key +
        " Tests which run WORSE on target</font></td></tr>");
    writer.println(worseTestcaseResultTags);
    writer.flush();

    writer.println("</table>");

    writer.println(getFooter());
    writer.flush();

    writer.close();

    String statusTable = getStatusTableForSummary(curGroupName,
        key.toString(),
        numBetterTestCases,
        numSameTestCases,
        numWorseTestCases);

    String testResultsTable = getTestResultsTableForSummary(key.toString(),
        totalBaseScore,
        totalTargetScore);

    return new String[]{statusTable, testResultsTable};
  }

  /**
   * Returns footer tag for HTML files
   */
  private static String getFooter() {

    String buffer = "<br><hr WIDTH=\"100%\" size=\"1\">" +
        "<A NAME=\"legend\"></A><H3>Legend:</H3>" +
        "<table cellspacing=\"0\" cellpadding=\"3\" " + "border=\"1\" width=\"80%\">" +
        "<TR BGCOLOR=\"" +
        color[0] +
        "\"><TD>The result for " +
        targetBuild +
        " is at least 10 percent BETTER than for " +
        baseBuild +
        "</TD></TR>" +
        "<TR BGCOLOR=\"" +
        color[1] +
        "\"><TD>The results for " +
        targetBuild +
        " and " +
        baseBuild +
        " are within 10 percent</TD></TR>" +
        "<TR BGCOLOR=\"" +
        color[2] +
        "\"><TD>The result for " +
        targetBuild +
        " is at least 10 percent WORSE than " +
        baseBuild +
        "</TD></TR>" +
        "<TR><TD>The 'Score' is a number of " +
        "successful rendering " +
        "operations per second</TD></TR>" +
        "</table>" +
        "<br><hr WIDTH=\"100%\" size=\"1\">" +
        "</p><hr WIDTH=\"100%\" size=\"1\"></body></html>";

    return buffer;
  }

  /**
   * Returns header tag for HTML files
   */
  private static String getHeader(
      SingleResultSetHolder baseSRSH, SingleResultSetHolder targetSRSH, String title,
      String sysPropLoc) {

    StringBuilder buffer = new StringBuilder();

    String headerTitle = getHeaderTitle(title);
    buffer.append(headerTitle);

    //System Properties
    buffer
        .append("<tr><td bgcolor=\"#CCCCFF\">" + "<b><A HREF=\"")
        .append(sysPropLoc)
        .append("\">System Property</A>")
        .append("</b></td>")
        .append("<td bgcolor=\"#CCCCFF\"><b><A HREF=\"")
        .append(sysPropLoc)
        .append("\">Value<A></b></td></tr>");
    Map sysProps = targetSRSH.getProperties();
    buffer
        .append("<tr><td bgcolor=\"#f0f0f0\">os.name</td><td>")
        .append(sysProps.get("os.name"))
        .append("</td></tr>");
    buffer
        .append("<tr><td bgcolor=\"#f0f0f0\">os.version</td><td>")
        .append(sysProps.get(OSInfo.OS_VERSION))
        .append("</td></tr>");
    buffer
        .append("<tr><td bgcolor=\"#f0f0f0\">os.arch</td><td>")
        .append(sysProps.get("os.arch"))
        .append("</td></tr>");
    buffer
        .append("<tr><td bgcolor=\"#f0f0f0\">sun.desktop</td><td>")
        .append(sysProps.get("sun.desktop"))
        .append("</td></tr>");

    buffer.append("</table>");

    return buffer.toString();
  }

  /**
   * Returns start tag and title tag for HTML files
   */
  private static String getHeaderTitle(String title) {

    String buffer = "<html><head><title>" + title + "</title></head>" +
        "<body bgcolor=\"#ffffff\"><hr size=\"1\">" +
        "<center><h2>" + title + "</h2>" +
        "</center><hr size=\"1\"><br>" +
        "<table cols=\"2\" cellspacing=\"2\" cellpadding=\"5\" " + "border=\"0\" width=\"80%\">" +
        "<tr><td bgcolor=\"#CCCCFF\" colspan=\"2\">" + "<b>Test Details</b></td></tr>" +
        "<tr><td bgcolor=\"#f0f0f0\">Base Build</td>" +
        "<td>" + baseBuild + "</td></tr>" +
        "<tr><td bgcolor=\"#f0f0f0\">Target Build</td>" +
        "<td>" + targetBuild + "</td></tr>";

    return buffer;
  }

  /**
   * Generats System-Properties HTML file - System_Property.html
   */
  private static void generateSysPropsReport(SingleResultSetHolder srsh) {

    File sysPropsFile = new File(resultsDir, "System_Properties.html");
    PrintWriter writer = openFile(sysPropsFile.getAbsolutePath(), HTMLGEN_FILE_NEW);

    String headerTitle = getHeaderTitle("System Properties");
    writer.println(headerTitle);
    writer.flush();

    writer.println("<tr><td bgcolor=\"#CCCCFF\"><b>" +
        "System Property</b></td><td bgcolor=\"#CCCCFF\">" +
        "<b>Value</b></td></tr>");

    String key;
    String value;
    Map sysProps = srsh.getProperties();
    for (Object o : sysProps.keySet()) {
      key = o.toString();
      value = sysProps.get(key).toString();
      writer.println("<tr><td bgcolor=\"#f0f0f0\">" +
          key + "</td><td>" + value + "</td></tr>");
    }
    writer.println("</table>");
    writer.flush();

    writer.println("<br><hr WIDTH=\"100%\" size=\"1\">");
    writer.println("</p><hr WIDTH=\"100%\" size=\"1\"></body></html>");

    writer.flush();
  }

  /**
   * Returns the index of color from color array based on the results
   * Can change this implementation so as to select based on some analysis.
   */
  private static int selectColor(double baseScore, double targetScore) {

    double res = calculateSpeedupPercentage(baseScore, targetScore);

    if (res < -10) {
      return 2;
    } else if (res > 10) {
      return 0;
    } else {
      return 1;
    }
  }

  /**
   * Calculate Speedup Percentage ->
   * ((target_score - base_score) * 100) / baseScore
   * Can change this implementation so as to provide some analysis.
   */
  private static double calculateSpeedupPercentage(double baseScore, double targetScore) {
    return (targetScore - baseScore) * 100 / baseScore;
  }

  private static void printUsage() {
    String usage = "\njava XMLHTMLReporter [options]      " +
        "                                      \n\n" +
        "where options include:                " +
        "                                      \n" +
        "    -r | -results <result directory>  " +
        "directory to which reports are stored \n" +
        "    -basexml | -b <xml file path>     " +
        "path to base-build result             \n" +
        "    -targetxml | -t <xml file path>   " +
        "path to target-build result           \n" +
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
    String baseXML = null;
    String targetXML = null;
    String resultXML = null;
    int group = 2;

        /* ---- Analysis Mode ----
            BEST    = 1;
            WORST   = 2;
            AVERAGE = 3;
            MIDAVG  = 4;
         ------------------------ */
    int analyzerMode = 4;

    try {

      for (int i = 0; i < args.length; i++) {
        if (args[i].startsWith("-results") || args[i].startsWith("-r")) {
          i++;
          resDir = args[i];
        } else if (args[i].startsWith("-basexml") || args[i].startsWith("-b")) {
          i++;
          baseXML = args[i];
        } else if (args[i].startsWith("-targetxml") || args[i].startsWith("-t")) {
          i++;
          targetXML = args[i];
        } else if (args[i].startsWith("-resultxml") || args[i].startsWith("-xml")) {
          i++;
          resultXML = args[i];
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
        }
      }
    } catch (Exception e) {
      printUsage();
    }

    if (resDir != null) {

      setGroupLevel(group);
      J2DAnalyzer.setMode(analyzerMode);

      if (targetXML != null && baseXML != null) {
        generateComparisonReport(resDir, baseXML, targetXML);
      } else if (resultXML != null) {
        generateReport(resDir, resultXML);
      } else {
        printUsage();
      }
    } else {
      printUsage();
    }
  }
}
