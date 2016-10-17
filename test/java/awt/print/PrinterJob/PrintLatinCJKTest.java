/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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

/*
  @test
 * @bug 800535 8022536
 * @summary JDK7 Printing: CJK and Latin Text in string overlap
 * @run main/manual=yesno PrintLatinCJKTest
 */

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

public class PrintLatinCJKTest implements Printable, ActionListener {

    static final PrintLatinCJKTest testInstance = new PrintLatinCJKTest();
    private PageFormat pf;

    static final String info =
       "To test 8022536, if a remote printer is the system default,"+
       "it should show in the dialog as the selected printer.\n"+
       "You need a printer for this test. If you have none, let "+
       "the test pass. If there is a printer, press Print, send "+
       "the output to the printer, and examine it. It should have "+
       "text looking like this : \u4e00\u4e01\u4e02\u4e03\u4e04English.";

    public static void showFrame() {
         JFrame f = new JFrame();
         JTextArea jta = new JTextArea(info, 4, 30);
         jta.setLineWrap(true);
         jta.setWrapStyleWord(true);
         f.add(BorderLayout.CENTER, jta);
         JButton b = new JButton("Print");
         b.addActionListener(testInstance);
         f.add(BorderLayout.SOUTH, b);
         f.pack();
         f.setVisible(true);
    }

    @Override
    public int print(Graphics g, PageFormat pf, int pageIndex)
                         throws PrinterException {

        if (pageIndex > 0) {
            return Printable.NO_SUCH_PAGE;
        }
        g.translate((int) pf.getImageableX(), (int) pf.getImageableY());
        g.setFont(new Font(OwnedWindowsSerialization.DIALOG_LABEL, Font.PLAIN, 36));
        g.drawString("\u4e00\u4e01\u4e02\u4e03\u4e04English", 20, 100);
        return Printable.PAGE_EXISTS;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(testInstance);
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                showFrame();
            }
        });
    }
}
