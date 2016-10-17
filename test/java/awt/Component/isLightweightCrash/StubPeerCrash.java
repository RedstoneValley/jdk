/*
 * Copyright (c) 2007, 2008, Oracle and/or its affiliates. All rights reserved.
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
  @bug 6255653
  @summary REGRESSION: Override isLightweight() causes access violation in awt.dll
  @author Andrei Dmitriev: area=awt-component
  @run main StubPeerCrash
*/

/*
 * The test may not crash for several times so iteratively continue up to some limit.
 */

import java.awt.*;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.peer.*;
import java.awt.event.PaintEvent;
import java.awt.image.ImageProducer;
import java.awt.image.ImageObserver;
import java.awt.image.ColorModel;
import java.awt.image.VolatileImage;
import sun.awt.CausedFocusEvent.Cause;
import sun.java2d.pipe.Region;

public final class StubPeerCrash {
    public static final int ITERATIONS = 20;

    private StubPeerCrash() {
    }

    public static void main(String []s)
    {
        for (int i = 0; i < ITERATIONS; i++){
            showFrame(i);
        }
    }

    private static void showFrame(int i){
        System.out.println("iteration = "+i);
        Frame f = new Frame();
        f.add(new AHeavyweightComponent());
        f.setVisible(true);
        f.setVisible(false);
    }
}

class AHeavyweightComponent extends Component {
    private static final long serialVersionUID = -2355192972546054656L;
    private final ComponentPeer peer = new StubComponentPeer();

    public AHeavyweightComponent(){
    }

    @Override
    public boolean isLightweight() {
        return false;
    }

    @Override
    public ComponentPeer getPeer(){
        return peer;
    }
}

class StubComponentPeer implements ComponentPeer {
    @Override
    public boolean isObscured(){return true;}

    @Override
    public boolean canDetermineObscurity(){return true;}

    @Override
    public void                setVisible(boolean b){}

    @Override
    public void                setEnabled(boolean b){}

    @Override
    public void                paint(Graphics g){}

    public void                repaint(long tm, int x, int y, int width, int height){}

    @Override
    public void                print(Graphics g){}

    @Override
    public void                setBounds(int x, int y, int width, int height, int op){}

    @Override
    public void                handleEvent(AWTEvent e){}

    @Override
    public void                coalescePaintEvent(PaintEvent e){}

    @Override
    public Point               getLocationOnScreen(){return null;}

    @Override
    public Dimension           getPreferredSize(){return null;}

    @Override
    public Dimension           getMinimumSize(){return null;}

    @Override
    public ColorModel          getColorModel(){return null;}

    public Toolkit             getToolkit(){return null;}

    @Override
    public Graphics            getGraphics(){return null;}

    @Override
    public FontMetrics         getFontMetrics(Font font){return null;}

    @Override
    public void                dispose(){}

    @Override
    public void                setForeground(Color c){}

    @Override
    public void                setBackground(Color c){}

    @Override
    public void                setFont(Font f){}

    @Override
    public void                updateCursorImmediately(){}

    @Override
    public boolean             requestFocus(Component lightweightChild,
                                     boolean temporary,
                                     boolean focusedWindowChangeAllowed,
                                     long time, Cause cause){
        return true;
    }

    @Override
    public boolean             isFocusable(){return true;}

    @Override
    public Image               createImage(ImageProducer producer){return null;}

    @Override
    public Image               createImage(int width, int height){return null;}

    @Override
    public VolatileImage       createVolatileImage(int width, int height){return null;}

    @Override
    public boolean             prepareImage(Image img, int w, int h, ImageObserver o){return true;}

    @Override
    public int                 checkImage(Image img, int w, int h, ImageObserver o){return 0;}

    @Override
    public GraphicsConfiguration getGraphicsConfiguration(){return null;}

    @Override
    public boolean     handlesWheelScrolling(){return true;}

    @Override
    public void createBuffers(int numBuffers, BufferCapabilities caps) throws AWTException{}

    @Override
    public Image getBackBuffer(){return null;}

    @Override
    public void flip(int x1, int y1, int x2, int y2, FlipContents flipAction){}

    @Override
    public void destroyBuffers(){}

    /**
     * Reparents this peer to the new parent referenced by {@code newContainer} peer
     * Implementation depends on toolkit and container.
     * @param newContainer peer of the new parent container
     * @since 1.5
     */
    @Override
    public void reparent(ContainerPeer newContainer){}

    /**
     * Returns whether this peer supports reparenting to another parent withour destroying the peer
     * @return true if appropriate reparent is supported, false otherwise
     * @since 1.5
     */
    @Override
    public boolean isReparentSupported(){return true;}

    /**
     * Used by lightweight implementations to tell a ComponentPeer to layout
     * its sub-elements.  For instance, a lightweight Checkbox needs to layout
     * the box, as well as the text label.
     */
    @Override
    public void        layout(){}

    public    Rectangle getBounds(){return null;}

    /**
     * Applies the shape to the native component window.
     * @since 1.7
     */
    @Override
    public void applyShape(Region shape){}

    /**
     * DEPRECATED:  Replaced by getPreferredSize().
     */
    public Dimension           preferredSize(){return null;}

    /**
     * DEPRECATED:  Replaced by getMinimumSize().
     */
    public Dimension           minimumSize(){return null;}

    /**
     * DEPRECATED:  Replaced by setVisible(boolean).
     */
    public void                show(){}

    /**
     * DEPRECATED:  Replaced by setVisible(boolean).
     */
    public void                hide(){}

    /**
     * DEPRECATED:  Replaced by setEnabled(boolean).
     */
    public void                enable(){}

    /**
     * DEPRECATED:  Replaced by setEnabled(boolean).
     */
    public void                disable(){}

    /**
     * DEPRECATED:  Replaced by setBounds(int, int, int, int).
     */
    public void                reshape(int x, int y, int width, int height){}
}
