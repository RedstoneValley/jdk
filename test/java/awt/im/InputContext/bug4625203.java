/*
 * Copyright (c) 2007, Oracle and/or its affiliates. All rights reserved.
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
 * @test
 * @bug 4625203
 * @summary 1. install two input locales on Windows
 *          2. run this application
 *          3. press Alt+Shift to switch to another input locale
 *          4. push jButton1 button
 *          5. If the input locale does not change, it is SUCCESS.
 *             If the input locale changes to the default one, it is FAILURE.
 */

import java.awt.*;
import java.util.Locale;
import java.awt.event.*;

public class bug4625203 extends JFrame {
    final JTextField jTextField1 = new JTextField();
    final JButton jButton1 = new JButton();
    Locale locale;
    public int n;

    public bug4625203() {
        try {
            jbInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        bug4625203 frame1 = new bug4625203();
        frame1.setSize(400,300);
        frame1.setVisible(true);
    }

    private void jbInit() throws Exception {
        jTextField1.setText("jTextField1");
        jButton1.setText("jButton1");
        jButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                this_windowClosing(e);
            }
        });
        getContentPane().add(jTextField1, BorderLayout.CENTER);
        getContentPane().add(jButton1, BorderLayout.SOUTH);
    }

    void jButton1_actionPerformed(ActionEvent e) {
        locale = ((JButton) e.getSource()).getInputContext().getLocale();
        System.out.println("locale" + n + ":" + locale);
        bug4625203 frame2 = new bug4625203();
        frame2.n = n + 1;
        frame2.setSize(400,300);
        frame2.setTitle("test:" + n);
        frame2.setVisible(true);
    }

    void this_windowClosing(WindowEvent e) {
        System.exit(0);
    }
}
