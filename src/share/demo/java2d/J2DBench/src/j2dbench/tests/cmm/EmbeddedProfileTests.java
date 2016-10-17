/*
 * Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
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

package j2dbench.tests.cmm;

import j2dbench.Group;
import j2dbench.Option;
import j2dbench.Result;
import j2dbench.TestEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/* This benchmark verifies how changes in cmm library affects image decoding */
public class EmbeddedProfileTests extends ColorConversionTests {

  protected static Group grpRoot;
  protected static Group grpOptionsRoot;

  protected static Option inputImages;

  public EmbeddedProfileTests(Group parent, String nodeName, String description) {
    super(parent, nodeName, description);
    addDependencies(grpOptionsRoot, true);
  }

  public static void init() {
    grpRoot = new Group(colorConvRoot, "embed", "Embedded Profile Tests");

    grpOptionsRoot = new Group(grpRoot, "embedOptions", "Options");

    inputImages = createImageList();

    new ReadImageTest();
  }

  private static Option createImageList() {
    IccImageResource[] images = IccImageResource.values();

    int num = images.length;

    String[] names = new String[num];
    String[] abbrev = new String[num];
    String[] descr = new String[num];

    for (int i = 0; i < num; i++) {
      names[i] = images[i].toString();
      abbrev[i] = images[i].abbrev;
      descr[i] = images[i].description;
    }

    return new ObjectList(grpOptionsRoot,
        "Images",
        "Input Images",
        names,
        images,
        abbrev,
        descr,
        1);
  }

  @Override
  public Object initTest(TestEnvironment env, Result res) {
    return new Context(env, res);
  }

  private enum IccImageResource {
    SMALL("images/img_icc_small.jpg", "512x512", "Small: 512x512"),
    MEDIUM("images/img_icc_medium.jpg", "2048x2048", "Medium: 2048x2048"),
    LARGE("images/img_icc_large.jpg", "4096x4096", "Large: 4096x4096");

    public final URL url;
    public final String abbrev;
    public final String description;

    IccImageResource(String file, String name, String description) {
      url = CMMTests.class.getResource(file);
      abbrev = name;
      this.description = description;
    }
  }

  private static class Context {
    URL input;

    public Context(TestEnvironment env, Result res) {

      IccImageResource icc_input = (IccImageResource) env.getModifier(inputImages);

      input = icc_input.url;
    }
  }

  private static class ReadImageTest extends EmbeddedProfileTests {
    public ReadImageTest() {
      super(grpRoot, "embd_img_read", "ImageReader.read()");
    }

    @Override
    public void runTest(Object octx, int numReps) {
      Context ctx = (Context) octx;
      URL url = ctx.input;
      ImageInputStream iis;
      ImageReader reader;

      try {
        iis = ImageIO.createImageInputStream(url.openStream());
        reader = ImageIO.getImageReaders(iis).next();
      } catch (IOException e) {
        throw new RuntimeException("Unable to run the becnhmark", e);
      }

      --numReps;
      do {
        try {
          reader.setInput(iis);
          BufferedImage img = reader.read(0);
          reader.reset();

          iis = ImageIO.createImageInputStream(url.openStream());
        } catch (Exception e) {
          e.printStackTrace();
        }
        --numReps;
      } while (numReps >= 0);
    }
  }

  @Override
  public void cleanupTest(TestEnvironment env, Object o) {
    Context ctx = (Context) o;
    ctx.input = null;
  }
}
