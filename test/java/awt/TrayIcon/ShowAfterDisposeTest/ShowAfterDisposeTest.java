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
  test
  @bug 6384984 8004032
  @summary TrayIcon try to dispay a tooltip when is not visible
  @author Dmitry.Cherepanov@sun.com area=awt.tray
  @run applet/manual=yesno ShowAfterDisposeTest.html
*/

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

public class ShowAfterDisposeTest extends Applet
{
    boolean traySupported;

    public void init()
    {
        setLayout(new BorderLayout ());

        String[] instructions;
        traySupported = SystemTray.isSupported();
        if (traySupported)
        {
            instructions = new String[]{
                "1) When the test starts an icon is added to the SystemTray area.",
                "2a) If you use Apple OS X,",
                "    right click on this icon (it's important to click before the tooltip is shown).",
                "    The icon should disappear.",
                "2b) If you use other os (Windows, Linux, Solaris),",
                "    double click on this icon (it's important to click before the tooltip is shown).",
                "    The icon should disappear.",
                "3) If the bug is reproducible then the test will fail without assistance.",
                "4) Just press the 'pass' button."
            };
        }
        else
        {
            instructions = new String[]{
              "The test cannot be run because SystemTray is not supported.",
              "Simply press PASS button."
            };
        }
        Sysout.createDialogWithInstructions(instructions);
    }

    public void start ()
    {
        setSize (200,200);
        setVisible(true);
        validate();

        if (!traySupported)
        {
            return;
        }

        BufferedImage img = new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, 32, 32);
        g.setColor(Color.RED);
        g.fillRect(6, 6, 20, 20);
        g.dispose();

        SystemTray tray = SystemTray.getSystemTray();
        TrayIcon icon = new TrayIcon(img);
        icon.setImageAutoSize(true);
        icon.addActionListener(new ActionListener()
            {
                @Override
                public void actionPerformed(ActionEvent ev)
                {
                    tray.remove(icon);
                }
            }
        );

        try {
            tray.add(icon);
        } catch (AWTException e) {
            Sysout.println(e.toString());
            Sysout.println("!!! The test coudn't be performed !!!");
            return;
        }
        icon.setToolTip("tooltip");
    }
}

/***************************************************
 Standard Test Machinery
 DO NOT modify anything below -- it's a standard
 chunk of code whose purpose is to make user
 interaction uniform, and thereby make it simpler
 to read and understand someone else's test.
 */

/**
 This is part of the standard test machinery.
 It creates a dialog (with the instructions), and is the interface
  for sending text messages to the user.
 To print the instructions, send an array of strings to Sysout.createDialog
  WithInstructions method.  Put one line of instructions per array entry.
 To display a message for the tester to see, simply call Sysout.println
  with the string to be displayed.
 This mimics System.out.println but works within the test harness as well
  as standalone.
 */

final class Sysout
{
    private static TestDialog dialog;

    private Sysout() {
    }

    public static void createDialogWithInstructions( String[] instructions )
    {
        dialog = new TestDialog( new Frame(), "Instructions" );
        dialog.printInstructions( instructions );
        dialog.setVisible(true);
        println( "Any messages for the tester will display here." );
    }

    public static void createDialog( )
    {
        dialog = new TestDialog( new Frame(), "Instructions" );
        String[] defInstr = { "Instructions will appear here. ", "" } ;
        dialog.printInstructions( defInstr );
        dialog.setVisible(true);
        println( "Any messages for the tester will display here." );
    }

    public static void printInstructions( String[] instructions )
    {
        dialog.printInstructions( instructions );
    }

    public static void println( String messageIn )
    {
        dialog.displayMessage( messageIn );
    }
}

/**
  This is part of the standard test machinery.  It provides a place for the
   test instructions to be displayed, and a place for interactive messages
   to the user to be displayed.
  To have the test instructions displayed, see Sysout.
  To have a message to the user be displayed, see Sysout.
  Do not call anything in this dialog directly.
  */
class TestDialog extends Dialog
{

    private static final long serialVersionUID = 4421905612345965770L;
    final TextArea instructionsText;
    final TextArea messageText;
    final int maxStringLength = 80;

    //DO NOT call this directly, go through Sysout
    public TestDialog( Frame frame, String name )
    {
        super( frame, name );
        int scrollBoth = TextArea.SCROLLBARS_BOTH;
        instructionsText = new TextArea( "", 15, maxStringLength, scrollBoth );
        add(BorderLayout.NORTH, instructionsText);

        messageText = new TextArea( "", 5, maxStringLength, scrollBoth );
        add(BorderLayout.CENTER, messageText);

        pack();

        setVisible(true);
    }

    //DO NOT call this directly, go through Sysout
    public void printInstructions( String[] instructions )
    {
        //Clear out any current instructions
        instructionsText.setText( "" );

        //Go down array of instruction strings

        String printStr, remainingStr;
        for (String instruction : instructions) {
            //chop up each into pieces maxSringLength long
            remainingStr = instruction;
            while (!remainingStr.isEmpty()) {
                //if longer than max then chop off first max chars to print
                if (remainingStr.length() >= maxStringLength) {
                    //Try to chop on a word boundary
                    int posOfSpace = remainingStr.
                        lastIndexOf(' ', maxStringLength - 1);

                    if (posOfSpace <= 0) {
                        posOfSpace = maxStringLength - 1;
                    }

                    printStr = remainingStr.substring(0, posOfSpace + 1);
                    remainingStr = remainingStr.substring(posOfSpace + 1);
                }
                //else just print
                else {
                    printStr = remainingStr;
                    remainingStr = "";
                }

                instructionsText.append(printStr + "\n");
            }
        }
    }

    //DO NOT call this directly, go through Sysout
    public void displayMessage( String messageIn )
    {
        messageText.append( messageIn + "\n" );
        System.out.println(messageIn);
    }
}
