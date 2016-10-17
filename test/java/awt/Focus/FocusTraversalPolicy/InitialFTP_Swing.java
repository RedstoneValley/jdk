/*
 * Copyright (c) 2012, 2013, Oracle and/or its affiliates. All rights reserved.
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
  @bug       7125044
  @summary   Tests default focus traversal policy in Swing toplevel windows.
  @author    anton.tarasov@sun.com: area=awt.focus
*/

import java.awt.Button;
import java.awt.FlowLayout;

public final class InitialFTP_Swing {
  private InitialFTP_Swing() {
  }

  public static void main(String[] args) {
        SwingFrame f0 = new SwingFrame("frame0");
        f0.setVisible(true);

        InitialFTP.test(f0, LayoutFocusTraversalPolicy.class);

        SwingFrame f1 = new SwingFrame("frame1");
        f1.setVisible(true);

        InitialFTP.test(f1, LayoutFocusTraversalPolicy.class);

        System.out.println("Test passed.");
    }
}

class SwingFrame extends JFrame {
    final JButton button = new JButton(Button.base);
    final JTextArea text = new JTextArea("qwerty");
    final JList list = new JList(new String[] {"one", "two", "three"});

    public SwingFrame(String title) {
        super(title);

        setLayout(new FlowLayout());
        add(button);
        add(text);
        add(list);
        pack();
    }
}
