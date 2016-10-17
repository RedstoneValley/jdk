/*
 * Copyright (c) 2006, 2011, Oracle and/or its affiliates. All rights reserved.
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

package j2dbench.tests.iio;

import j2dbench.Group;
import j2dbench.Group.EnableSet;
import j2dbench.Option;
import j2dbench.Result;
import j2dbench.TestEnvironment;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

abstract class OutputTests extends IIOTests {

  protected static final int OUTPUT_FILE = 1;
  protected static final int OUTPUT_ARRAY = 2;
  protected static final int OUTPUT_FILECHANNEL = 3;

  protected static final ImageOutputStreamSpi fileChannelIOSSpi;
  protected static Group outputRoot;
  protected static Group outputOptRoot;
  protected static Group generalOptRoot;
  protected static EnableSet generalDestRoot;
  protected static Option destFileOpt;
  protected static Option destByteArrayOpt;
  protected static Group imageioGeneralOptRoot;
  protected static Option destFileChannelOpt;
  protected static Option useCacheTog;

  static {
    if (hasImageIO) {
      ImageIO.scanForPlugins();
      IIORegistry registry = IIORegistry.getDefaultInstance();
      java.util.Iterator spis = registry.getServiceProviders(ImageOutputStreamSpi.class, false);
      while (spis.hasNext()) {
        ImageOutputStreamSpi spi = (ImageOutputStreamSpi) spis.next();
        String klass = spi.getClass().getName();
        if (klass.endsWith("ChannelImageOutputStreamSpi")) {
          fileChannelIOSSpi = spi;
          break;
        }
      }
    }
  }

  protected OutputTests(Group parent, String nodeName, String description) {
    super(parent, nodeName, description);
  }

  public static void init() {
    outputRoot = new Group(iioRoot, "output", "Output Benchmarks");
    outputRoot.setTabbed();

    // Options
    outputOptRoot = new Group(outputRoot, "opts", "Options");

    // General Options
    generalOptRoot = new Group(outputOptRoot, "general", "General Options");
    generalDestRoot = new EnableSet(generalOptRoot, "dest", "Destintations");
    destFileOpt = new OutputType("file", "File", OUTPUT_FILE);
    destByteArrayOpt = new OutputType("byteArray", "byte[]", OUTPUT_ARRAY);

    if (hasImageIO) {
      // Image I/O Options
      imageioGeneralOptRoot = new Group(outputOptRoot, "imageio", "Image I/O Options");
      if (fileChannelIOSSpi != null) {
        destFileChannelOpt = new OutputType("fileChannel", "FileChannel", OUTPUT_FILECHANNEL);
      }
      useCacheTog = new Toggle(imageioGeneralOptRoot,
          "useCache",
          "ImageIO.setUseCache()",
          Toggle.Off);
    }

    OutputImageTests.init();
    if (hasImageIO) {
      OutputStreamTests.init();
    }
  }

  protected static class OutputType extends Enable {
    private final int type;

    public OutputType(String nodeName, String description, int type) {
      super(generalDestRoot, nodeName, description, false);
      this.type = type;
    }

    public int getType() {
      return type;
    }

    @Override
    public String getAbbreviatedModifierDescription(Object value) {
      return getModifierValueName(value);
    }

    @Override
    public String getModifierValueName(Object val) {
      return getNodeName();
    }
  }

  protected abstract static class Context {
    final int size;
    Object output;
    final int outputType;
    OutputStream origStream;

    Context(TestEnvironment env, Result result) {
      size = env.getIntValue(sizeList);
      if (hasImageIO) {
        if (env.getModifier(useCacheTog) != null) {
          ImageIO.setUseCache(env.isEnabled(useCacheTog));
        }
      }

      OutputType t = (OutputType) env.getModifier(generalDestRoot);
      outputType = t.getType();
    }

    void initOutput() {
      if (outputType == OUTPUT_FILE || outputType == OUTPUT_FILECHANNEL) {
        try {
          File outputfile = File.createTempFile("iio", ".tmp");
          outputfile.deleteOnExit();
          output = outputfile;
        } catch (IOException e) {
          System.err.println("error creating temp file");
          e.printStackTrace();
        }
      }
    }

    ImageOutputStream createImageOutputStream() throws IOException {
      ImageOutputStream ios;
      switch (outputType) {
        case OUTPUT_FILE:
          ios = new FileImageOutputStream((File) output);
          break;
        case OUTPUT_ARRAY:
          ByteArrayOutputStream baos = new ByteArrayOutputStream();
          BufferedOutputStream bos = new BufferedOutputStream(baos);
          ios = ImageIO.getUseCache() ? new FileCacheImageOutputStream(bos, null)
              : new MemoryCacheImageOutputStream(bos);
          break;
        case OUTPUT_FILECHANNEL:
          FileOutputStream fos = new FileOutputStream((File) output);
          origStream = fos;
          FileChannel fc = fos.getChannel();
          ios = fileChannelIOSSpi.createOutputStreamInstance(fc, false, null);
          break;
        default:
          ios = null;
          break;
      }
      return ios;
    }

    void closeOriginalStream() throws IOException {
      if (origStream != null) {
        origStream.close();
        origStream = null;
      }
    }

    void cleanup(TestEnvironment env) {
    }
  }
}
