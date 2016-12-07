/*
 * Copyright (c) 1997, 2013, Oracle and/or its affiliates. All rights reserved.
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

package java.awt.print;

import java.awt.AWTError;
import java.awt.HeadlessException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.print.attribute.standard.MediaPrintableArea;

import sun.awt.DocFlavor.SERVICE_FORMATTED;
import sun.awt.print.OrientationRequested;
import sun.awt.print.PrintService;
import sun.awt.print.PrintServiceLookup;

/**
 * The {@code PrinterJob} class is the principal class that controls printing. An application calls
 * methods in this class to set up a job, optionally to invoke a print dialog with the user, and
 * then to print the pages of the job.
 */
public abstract class PrinterJob {
  protected static final float POINTS_PER_INCH = 72.0f;

 /* Public Class Methods */

  /**
   * A {@code PrinterJob} object should be created using the static {@link #getPrinterJob()
   * getPrinterJob} method.
   */
  public PrinterJob() {
  }

  /**
   * Creates and returns a {@code PrinterJob} which is initially associated with the default
   * printer. If no printers are available on the system, a PrinterJob will still be returned from
   * this method, but {@code getPrintService()} will return {@code null}, and calling {@link
   * #print() print} with this {@code PrinterJob} might generate an exception.  Applications that
   * need to determine if there are suitable printers before creating a {@code PrinterJob} should
   * ensure that the array returned from {@link #lookupPrintServices() lookupPrintServices} is not
   * empty.
   *
   * @return a new {@code PrinterJob}.
   * @throws SecurityException if a security manager exists and its {@link
   * SecurityManager#checkPrintJobAccess} method disallows this thread from creating a print job
   * request
   */
  public static PrinterJob getPrinterJob() {
    SecurityManager security = System.getSecurityManager();
    if (security != null) {
      security.checkPrintJobAccess();
    }
    return (PrinterJob) AccessController.doPrivileged(new PrivilegedAction() {
      @Override
      public Object run() {
        String nm = System.getProperty("java.awt.printerjob", null);
        try {
          return Class.forName(nm).newInstance();
        } catch (ClassNotFoundException e) {
          throw new AWTError("PrinterJob not found: " + nm);
        } catch (InstantiationException e) {
          throw new AWTError("Could not instantiate PrinterJob: " + nm);
        } catch (IllegalAccessException e) {
          throw new AWTError("Could not access PrinterJob: " + nm);
        }
      }
    });
  }

  /**
   * A convenience method which looks up 2D print services. Services returned from this method may
   * be installed on {@code PrinterJob}s which support print services. Calling this method is
   * equivalent to calling
   * <p>
   * and specifying a Pageable DocFlavor.
   *
   * @return a possibly empty array of 2D print services.
   * @since 1.4
   */
  public static PrintService[] lookupPrintServices() {
    return PrintServiceLookup.
        lookupPrintServices(SERVICE_FORMATTED.PAGEABLE, null);
  }


 /* Public Methods */

  /**
   * Returns the service (printer) for this printer job. Implementations of this class which do not
   * support print services may return null.  null will also be returned if no printers are
   * available.
   *
   * @return the service for this printer job.
   * @see #setPrintService(PrintService)
   * @see #getPrinterJob()
   * @since 1.4
   */
  public PrintService getPrintService() {
    return null;
  }

  /**
   * Associate this PrinterJob with a new PrintService. This method is overridden by subclasses
   * which support specifying a Print Service.
   * <p>
   * Throws {@code PrinterException} if the specified service cannot support the {@code Pageable}
   * and {@code Printable} interfaces necessary to support 2D printing.
   *
   * @param service a print service that supports 2D printing
   * @throws PrinterException if the specified service does not support 2D printing, or this
   * PrinterJob class does not support setting a 2D print service, or the specified service is
   * otherwise not a valid print service.
   * @see #getPrintService
   * @since 1.4
   */
  public void setPrintService(PrintService service) throws PrinterException {
    throw new PrinterException("Setting a service is not supported on this class");
  }

  /**
   * Calls {@code painter} to render the pages.  The pages in the document to be printed by this
   * {@code PrinterJob} are rendered by the {@link Printable} object, {@code painter}.  The {@link
   * PageFormat} for each page is the default page format.
   *
   * @param painter the {@code Printable} that renders each page of the document.
   */
  public abstract void setPrintable(Printable painter);

  /**
   * Calls {@code painter} to render the pages in the specified {@code format}.  The pages in the
   * document to be printed by this {@code PrinterJob} are rendered by the {@code Printable} object,
   * {@code painter}. The {@code PageFormat} of each page is {@code format}.
   *
   * @param painter the {@code Printable} called to render each page of the document
   * @param format the size and orientation of each page to be printed
   */
  public abstract void setPrintable(Printable painter, PageFormat format);

  /**
   * Queries {@code document} for the number of pages and the {@code PageFormat} and {@code
   * Printable} for each page held in the {@code Pageable} instance, {@code document}.
   *
   * @param document the pages to be printed. It can not be {@code null}.
   * @throws NullPointerException the {@code Pageable} passed in was {@code null}.
   * @see PageFormat
   * @see Printable
   */
  public abstract void setPageable(Pageable document) throws NullPointerException;

  /**
   * Presents a dialog to the user for changing the properties of the print job. This method will
   * display a native dialog if a native print service is selected, and user choice of printers will
   * be restricted to these native print services. To present the cross platform print dialog for
   * all services, including native ones instead use {@code printDialog(PrintRequestAttributeSet)}.
   * <p>
   * PrinterJob implementations which can use PrintService's will update the PrintService for this
   * PrinterJob to reflect the new service selected by the user.
   *
   * @return {@code true} if the user does not cancel the dialog; {@code false} otherwise.
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   */
  public abstract boolean printDialog() throws HeadlessException;

  /**
   * A convenience method which displays a cross-platform print dialog for all services which are
   * capable of printing 2D graphics using the {@code Pageable} interface. The selected printer when
   * the dialog is initially displayed will reflect the print service currently attached to this
   * print job. If the user changes the print service, the PrinterJob will be updated to reflect
   * this, unless the user cancels the dialog. As well as allowing the user to select the
   * destination printer, the user can also select values of various print request attributes.
   * <p>
   * The attributes parameter on input will reflect the applications required initial selections in
   * the user dialog. Attributes not specified display using the default for the service. On return
   * it will reflect the user's choices. Selections may be updated by the implementation to be
   * consistent with the supported values for the currently selected print service.
   * <p>
   * As the user scrolls to a new print service selection, the values copied are based on the
   * settings for the previous service, together with any user changes. The values are not based on
   * the original settings supplied by the client.
   * <p>
   * With the exception of selected printer, the PrinterJob state is not updated to reflect the
   * user's changes. For the selections to affect a printer job, the attributes must be specified in
   * the call to the {@code print(PrintRequestAttributeSet)} method. If using the Pageable
   * interface, clients which intend to use media selected by the user must create a PageFormat
   * derived from the user's selections. If the user cancels the dialog, the attributes will not
   * reflect any changes made by the user.
   *
   * @param attributes on input is application supplied attributes, on output the contents are
   * updated to reflect user choices. This parameter may not be null.
   * @return {@code true} if the user does not cancel the dialog; {@code false} otherwise.
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @throws NullPointerException if {@code attributes} parameter is null.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @since 1.4
   */
  public boolean printDialog(PrintRequestAttributeSet attributes) throws HeadlessException {

    if (attributes == null) {
      throw new NullPointerException("attributes");
    }
    return printDialog();
  }

  /**
   * Displays a dialog that allows modification of a {@code PageFormat} instance. The {@code page}
   * argument is used to initialize controls in the page setup dialog. If the user cancels the
   * dialog then this method returns the original {@code page} object unmodified. If the user okays
   * the dialog then this method returns a new {@code PageFormat} object with the indicated changes.
   * In either case, the original {@code page} object is not modified.
   *
   * @param page the default {@code PageFormat} presented to the user for modification
   * @return the original {@code page} object if the dialog is cancelled; a new {@code PageFormat}
   * object containing the format indicated by the user if the dialog is acknowledged.
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @since 1.2
   */
  public abstract PageFormat pageDialog(PageFormat page) throws HeadlessException;

  /**
   * A convenience method which displays a cross-platform page setup dialog. The choices available
   * will reflect the print service currently set on this PrinterJob.
   * <p>
   * The attributes parameter on input will reflect the client's required initial selections in the
   * user dialog. Attributes which are not specified display using the default for the service. On
   * return it will reflect the user's choices. Selections may be updated by the implementation to
   * be consistent with the supported values for the currently selected print service.
   * <p>
   * The return value will be a PageFormat equivalent to the selections in the
   * PrintRequestAttributeSet. If the user cancels the dialog, the attributes will not reflect any
   * changes made by the user, and the return value will be null.
   *
   * @param attributes on input is application supplied attributes, on output the contents are
   * updated to reflect user choices. This parameter may not be null.
   * @return a page format if the user does not cancel the dialog; {@code null} otherwise.
   * @throws HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @throws NullPointerException if {@code attributes} parameter is null.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @since 1.4
   */
  public PageFormat pageDialog(PrintRequestAttributeSet attributes) throws HeadlessException {

    if (attributes == null) {
      throw new NullPointerException("attributes");
    }
    return pageDialog(defaultPage());
  }

  /**
   * Clones the {@code PageFormat} argument and alters the clone to describe a default page size and
   * orientation.
   *
   * @param page the {@code PageFormat} to be cloned and altered
   * @return clone of {@code page}, altered to describe a default {@code PageFormat}.
   */
  public abstract PageFormat defaultPage(PageFormat page);

  /**
   * Creates a new {@code PageFormat} instance and sets it to a default size and orientation.
   *
   * @return a {@code PageFormat} set to a default size and orientation.
   */
  public PageFormat defaultPage() {
    return defaultPage(new PageFormat());
  }

  /**
   * Calculates a {@code PageFormat} with values consistent with those supported by the current
   * {@code PrintService} for this job (ie the value returned by {@code getPrintService()}) and
   * media, printable area and orientation contained in {@code attributes}.
   * <p>
   * Calling this method does not update the job. It is useful for clients that have a set of
   * attributes obtained from {@code printDialog(PrintRequestAttributeSet attributes)} and need a
   * PageFormat to print a Pageable object.
   *
   * @param attributes a set of printing attributes, for example obtained from calling printDialog.
   * If {@code attributes} is null a default PageFormat is returned.
   * @return a {@code PageFormat} whose settings conform with those of the current service and the
   * specified attributes.
   * @since 1.6
   */
  public PageFormat getPageFormat(PrintRequestAttributeSet attributes) {

    PrintService service = getPrintService();
    PageFormat pf = defaultPage();

    if (service == null || attributes == null) {
      return pf;
    }

    MediaPrintableArea mpa = (MediaPrintableArea) attributes.get(MediaPrintableArea.class);
    OrientationRequested orientReq
        = (OrientationRequested) attributes.get(OrientationRequested.class);

    if (mpa == null && orientReq == null) {
      return pf;
    }
    Paper paper = pf.getPaper();

        /* If there's a media but no media printable area, we can try
         * to retrieve the default value for mpa and use that.
         */
    if (mpa == null &&
        service.isAttributeCategorySupported(MediaPrintableArea.class)) {
      Object mpaVals = service.getSupportedAttributeValues(MediaPrintableArea.class,
          null,
          attributes);
      if (mpaVals instanceof MediaPrintableArea[] && ((MediaPrintableArea[]) mpaVals).length > 0) {
        mpa = ((MediaPrintableArea[]) mpaVals)[0];
      }
    }

    if (mpa != null && service.isAttributeValueSupported(mpa, null, attributes)) {
      float[] printableArea = mpa.getPrintableArea(MediaPrintableArea.INCH);
      for (int i = 0; i < printableArea.length; i++) {
        printableArea[i] *= POINTS_PER_INCH;
      }
      paper.setImageableArea(printableArea[0],
          printableArea[1],
          printableArea[2],
          printableArea[3]);
    }

    if (orientReq != null && service.isAttributeValueSupported(orientReq, null, attributes)) {
      int orient;
      if (orientReq == OrientationRequested.REVERSE_LANDSCAPE) {
        orient = PageFormat.REVERSE_LANDSCAPE;
      } else if (orientReq == OrientationRequested.LANDSCAPE) {
        orient = PageFormat.LANDSCAPE;
      } else {
        orient = PageFormat.PORTRAIT;
      }
      pf.setOrientation(orient);
    }

    pf.setPaper(paper);
    pf = validatePage(pf);
    return pf;
  }

  /**
   * Returns the clone of {@code page} with its settings adjusted to be compatible with the current
   * printer of this {@code PrinterJob}.  For example, the returned {@code PageFormat} could have
   * its imageable area adjusted to fit within the physical area of the paper that is used by the
   * current printer.
   *
   * @param page the {@code PageFormat} that is cloned and whose settings are changed to be
   * compatible with the current printer
   * @return a {@code PageFormat} that is cloned from {@code page} and whose settings are changed to
   * conform with this {@code PrinterJob}.
   */
  public abstract PageFormat validatePage(PageFormat page);

  /**
   * Prints a set of pages.
   *
   * @throws PrinterException an error in the print system caused the job to be aborted.
   * @see Book
   * @see Pageable
   * @see Printable
   */
  public abstract void print() throws PrinterException;

  /**
   * Prints a set of pages using the settings in the attribute set. The default implementation
   * ignores the attribute set.
   * <p>
   * Note that some attributes may be set directly on the PrinterJob by equivalent method calls,
   * (for example), copies: {@code setcopies(int)}, job name: {@code setJobName(String)} and
   * specifying media size and orientation though the {@code PageFormat} object.
   * <p>
   * If a supported attribute-value is specified in this attribute set, it will take precedence over
   * the API settings for this print() operation only. The following behaviour is specified for
   * PageFormat: If a client uses the Printable interface, then the {@code attributes} parameter to
   * this method is examined for attributes which specify media (by size), orientation, and
   * imageable area, and those are used to construct a new PageFormat which is passed to the
   * Printable object's print() method. See {@link Printable} for an explanation of the required
   * behaviour of a Printable to ensure optimal printing via PrinterJob. For clients of the Pageable
   * interface, the PageFormat will always be as supplied by that interface, on a per page basis.
   * <p>
   * These behaviours allow an application to directly pass the user settings returned from {@code
   * printDialog(PrintRequestAttributeSet attributes} to this print() method.
   * <p>
   *
   * @param attributes a set of attributes for the job
   * @throws PrinterException an error in the print system caused the job to be aborted.
   * @see Book
   * @see Pageable
   * @see Printable
   * @since 1.4
   */
  public void print(PrintRequestAttributeSet attributes) throws PrinterException {
    print();
  }

  /**
   * Gets the number of copies to be printed.
   *
   * @return the number of copies to be printed.
   * @see #setCopies
   */
  public abstract int getCopies();

  /**
   * Sets the number of copies to be printed.
   *
   * @param copies the number of copies to be printed
   * @see #getCopies
   */
  public abstract void setCopies(int copies);

  /**
   * Gets the name of the printing user.
   *
   * @return the name of the printing user
   */
  public abstract String getUserName();

  /**
   * Gets the name of the document to be printed.
   *
   * @return the name of the document to be printed.
   * @see #setJobName
   */
  public abstract String getJobName();

  /**
   * Sets the name of the document to be printed. The document name can not be {@code null}.
   *
   * @param jobName the name of the document to be printed
   * @see #getJobName
   */
  public abstract void setJobName(String jobName);

  /**
   * Cancels a print job that is in progress.  If {@link #print() print} has been called but has not
   * returned then this method signals that the job should be cancelled at the next chance. If there
   * is no print job in progress then this call does nothing.
   */
  public abstract void cancel();

  /**
   * Returns {@code true} if a print job is in progress, but is going to be cancelled at the next
   * opportunity; otherwise returns {@code false}.
   *
   * @return {@code true} if the job in progress is going to be cancelled; {@code false} otherwise.
   */
  public abstract boolean isCancelled();
}
