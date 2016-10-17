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
import j2dbench.Result;
import j2dbench.TestEnvironment;
import java.io.IOException;

abstract class OutputStreamTests extends OutputTests {

  private static Group streamRoot;
  static Group streamTestRoot;

  protected OutputStreamTests(Group parent, String nodeName, String description) {
    super(parent, nodeName, description);
    addDependency(generalDestRoot);
    addDependencies(imageioGeneralOptRoot, true);
  }

  public static void init() {
    streamRoot = new Group(outputRoot, "stream", "Image Stream Benchmarks");
    streamTestRoot = new Group(streamRoot, "tests", "ImageOutputStream Tests");

    new IOSConstruct();
    new IOSWrite();
    new IOSWriteByteArray();
    new IOSWriteBit();
    new IOSWriteByte();
    new IOSWriteShort();
    new IOSWriteInt();
    new IOSWriteFloat();
    new IOSWriteLong();
    new IOSWriteDouble();
  }

  private static class Context extends OutputTests.Context {
    ImageOutputStream outputStream;
    final int scanlineStride; // width of a scanline (in bytes)
    final int length; // length of the entire stream (in bytes)
    final byte[] byteBuf;

    Context(TestEnvironment env, Result result) {
      super(env, result);

      // 4 bytes per "pixel"
      scanlineStride = size << 2;

      // tack on an extra 4 bytes, so that in the 1x1 case we can
      // call writeLong() or writeDouble() before resetting
      length = scanlineStride * size + 4;

      // big enough for one scanline
      byteBuf = new byte[scanlineStride];

      initOutput();

      try {
        outputStream = createImageOutputStream();
      } catch (IOException e) {
        System.err.println("Error creating ImageOutputStream");
      }
    }

    @Override
    void cleanup(TestEnvironment env) {
      super.cleanup(env);
      if (outputStream != null) {
        try {
          outputStream.close();
        } catch (IOException e) {
          System.err.println("error closing stream");
        }
        outputStream = null;
      }
    }
  }

  private static class IOSConstruct extends OutputStreamTests {
    public IOSConstruct() {
      super(streamTestRoot, "construct", "Construct");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(1);
      result.setUnitName("stream");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      try {
        --numReps;
        do {
          ImageOutputStream ios = octx.createImageOutputStream();
          ios.close();
          octx.closeOriginalStream();
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }  @Override
  public void cleanupTest(TestEnvironment env, Object ctx) {
    Context iioctx = (Context) ctx;
    iioctx.cleanup(env);
  }

  private static class IOSWrite extends OutputStreamTests {
    public IOSWrite() {
      super(streamTestRoot, "write", "write()");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(1);
      result.setUnitName("byte");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      int length = octx.length;
      int pos = 0;
      try {
        ios.mark();
        --numReps;
        do {
          if (pos >= length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.write(0);
          pos++;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IOSWriteByteArray extends OutputStreamTests {
    public IOSWriteByteArray() {
      super(streamTestRoot, "writeByteArray", "write(byte[]) (one \"scanline\" at a time)");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(ctx.scanlineStride);
      result.setUnitName("byte");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      byte[] buf = octx.byteBuf;
      int scanlineStride = octx.scanlineStride;
      int length = octx.length;
      int pos = 0;
      try {
        ios.mark();
        --numReps;
        do {
          if (pos + scanlineStride > length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.write(buf);
          pos += scanlineStride;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IOSWriteBit extends OutputStreamTests {
    public IOSWriteBit() {
      super(streamTestRoot, "writeBit", "writeBit()");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(1);
      result.setUnitName("bit");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      int length = octx.length * 8; // measured in bits
      int pos = 0; // measured in bits
      try {
        ios.mark();
        --numReps;
        do {
          if (pos >= length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.writeBit(0);
          pos++;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IOSWriteByte extends OutputStreamTests {
    public IOSWriteByte() {
      super(streamTestRoot, "writeByte", "writeByte()");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(1);
      result.setUnitName("byte");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      int length = octx.length;
      int pos = 0;
      try {
        ios.mark();
        --numReps;
        do {
          if (pos >= length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.writeByte(0);
          pos++;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IOSWriteShort extends OutputStreamTests {
    public IOSWriteShort() {
      super(streamTestRoot, "writeShort", "writeShort()");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(2);
      result.setUnitName("byte");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      int length = octx.length;
      int pos = 0;
      try {
        ios.mark();
        --numReps;
        do {
          if (pos + 2 > length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.writeShort(0);
          pos += 2;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IOSWriteInt extends OutputStreamTests {
    public IOSWriteInt() {
      super(streamTestRoot, "writeInt", "writeInt()");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(4);
      result.setUnitName("byte");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      int length = octx.length;
      int pos = 0;
      try {
        ios.mark();
        --numReps;
        do {
          if (pos + 4 > length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.writeInt(0);
          pos += 4;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IOSWriteFloat extends OutputStreamTests {
    public IOSWriteFloat() {
      super(streamTestRoot, "writeFloat", "writeFloat()");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(4);
      result.setUnitName("byte");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      int length = octx.length;
      int pos = 0;
      try {
        ios.mark();
        --numReps;
        do {
          if (pos + 4 > length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.writeFloat(0.0f);
          pos += 4;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IOSWriteLong extends OutputStreamTests {
    public IOSWriteLong() {
      super(streamTestRoot, "writeLong", "writeLong()");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(8);
      result.setUnitName("byte");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      int length = octx.length;
      int pos = 0;
      try {
        ios.mark();
        --numReps;
        do {
          if (pos + 8 > length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.writeLong(0L);
          pos += 8;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IOSWriteDouble extends OutputStreamTests {
    public IOSWriteDouble() {
      super(streamTestRoot, "writeDouble", "writeDouble()");
    }

    @Override
    public Object initTest(TestEnvironment env, Result result) {
      Context ctx = new Context(env, result);
      result.setUnits(8);
      result.setUnitName("byte");
      return ctx;
    }

    @Override
    public void runTest(Object ctx, int numReps) {
      Context octx = (Context) ctx;
      ImageOutputStream ios = octx.outputStream;
      int length = octx.length;
      int pos = 0;
      try {
        ios.mark();
        --numReps;
        do {
          if (pos + 8 > length) {
            ios.reset();
            ios.mark();
            pos = 0;
          }
          ios.writeDouble(0.0);
          pos += 8;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          ios.reset();
        } catch (IOException e) {
        }
      }
    }
  }


}
