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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

abstract class InputStreamTests extends InputTests {

  private static Group streamRoot;
  static Group streamTestRoot;

  protected InputStreamTests(Group parent, String nodeName, String description) {
    super(parent, nodeName, description);
    addDependency(generalSourceRoot);
    addDependencies(imageioGeneralOptRoot, true);
  }

  public static void init() {
    streamRoot = new Group(inputRoot, "stream", "Image Stream Benchmarks");
    streamTestRoot = new Group(streamRoot, "tests", "ImageInputStream Tests");

    new IISConstruct();
    new IISRead();
    new IISReadByteArray();
    new IISReadFullyByteArray();
    new IISReadBit();
    new IISReadByte();
    new IISReadUnsignedByte();
    new IISReadShort();
    new IISReadUnsignedShort();
    new IISReadInt();
    new IISReadUnsignedInt();
    new IISReadFloat();
    new IISReadLong();
    new IISReadDouble();
    new IISSkipBytes();
  }

  private static class Context extends InputTests.Context {
    ImageInputStream inputStream;
    final int scanlineStride; // width of a scanline (in bytes)
    final int length; // length of the entire stream (in bytes)
    final byte[] byteBuf;

    Context(TestEnvironment env, Result result) {
      super(env, result);

      // 4 bytes per "pixel"
      scanlineStride = size << 2;

      // tack on an extra 4 bytes, so that in the 1x1 case we can
      // call readLong() or readDouble() without hitting EOF
      length = scanlineStride * size + 4;

      // big enough for one scanline
      byteBuf = new byte[scanlineStride];

      initInput();

      try {
        inputStream = createImageInputStream();
      } catch (IOException e) {
        System.err.println("Error creating ImageInputStream");
      }
    }

    @Override
    void initContents(File f) throws IOException {
      FileOutputStream fos = null;
      try {
        fos = new FileOutputStream(f);
        initContents(fos);
      } finally {
        fos.close();
      }
    }

    @Override
    void initContents(OutputStream out) throws IOException {
      for (int i = 0; i < size; i++) {
        out.write(byteBuf);
      }
      out.write(new byte[4]); // add the 4 byte pad
      out.flush();
    }

    @Override
    void cleanup(TestEnvironment env) {
      super.cleanup(env);
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          System.err.println("error closing stream");
        }
        inputStream = null;
      }
    }
  }

  private static class IISConstruct extends InputStreamTests {
    public IISConstruct() {
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
      Context ictx = (Context) ctx;
      try {
        --numReps;
        do {
          ImageInputStream iis = ictx.createImageInputStream();
          iis.close();
          ictx.closeOriginalStream();
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

  private static class IISRead extends InputStreamTests {
    public IISRead() {
      super(streamTestRoot, "read", "read()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos >= length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.read();
          pos++;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadByteArray extends InputStreamTests {
    public IISReadByteArray() {
      super(streamTestRoot, "readByteArray", "read(byte[]) (one \"scanline\" at a time)");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      byte[] buf = ictx.byteBuf;
      int scanlineStride = ictx.scanlineStride;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + scanlineStride > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.read(buf);
          pos += scanlineStride;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadFullyByteArray extends InputStreamTests {
    public IISReadFullyByteArray() {
      super(streamTestRoot, "readFullyByteArray", "readFully(byte[]) (one \"scanline\" at a time)");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      byte[] buf = ictx.byteBuf;
      int scanlineStride = ictx.scanlineStride;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + scanlineStride > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readFully(buf);
          pos += scanlineStride;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadBit extends InputStreamTests {
    public IISReadBit() {
      super(streamTestRoot, "readBit", "readBit()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length * 8;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos >= length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readBit();
          pos++;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadByte extends InputStreamTests {
    public IISReadByte() {
      super(streamTestRoot, "readByte", "readByte()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos >= length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readByte();
          pos++;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadUnsignedByte extends InputStreamTests {
    public IISReadUnsignedByte() {
      super(streamTestRoot, "readUnsignedByte", "readUnsignedByte()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos >= length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readUnsignedByte();
          pos++;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadShort extends InputStreamTests {
    public IISReadShort() {
      super(streamTestRoot, "readShort", "readShort()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + 2 > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readShort();
          pos += 2;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadUnsignedShort extends InputStreamTests {
    public IISReadUnsignedShort() {
      super(streamTestRoot, "readUnsignedShort", "readUnsignedShort()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + 2 > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readUnsignedShort();
          pos += 2;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadInt extends InputStreamTests {
    public IISReadInt() {
      super(streamTestRoot, "readInt", "readInt()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + 4 > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readInt();
          pos += 4;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadUnsignedInt extends InputStreamTests {
    public IISReadUnsignedInt() {
      super(streamTestRoot, "readUnsignedInt", "readUnsignedInt()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + 4 > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readUnsignedInt();
          pos += 4;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadFloat extends InputStreamTests {
    public IISReadFloat() {
      super(streamTestRoot, "readFloat", "readFloat()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + 4 > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readFloat();
          pos += 4;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadLong extends InputStreamTests {
    public IISReadLong() {
      super(streamTestRoot, "readLong", "readLong()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + 8 > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readLong();
          pos += 8;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISReadDouble extends InputStreamTests {
    public IISReadDouble() {
      super(streamTestRoot, "readDouble", "readDouble()");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + 8 > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.readDouble();
          pos += 8;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }

  private static class IISSkipBytes extends InputStreamTests {
    public IISSkipBytes() {
      super(streamTestRoot, "skipBytes", "skipBytes() (one \"scanline\" at a time)");
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
      Context ictx = (Context) ctx;
      ImageInputStream iis = ictx.inputStream;
      int scanlineStride = ictx.scanlineStride;
      int length = ictx.length;
      int pos = 0;
      try {
        iis.mark();
        --numReps;
        do {
          if (pos + scanlineStride > length) {
            iis.reset();
            iis.mark();
            pos = 0;
          }
          iis.skipBytes(scanlineStride);
          pos += scanlineStride;
          --numReps;
        } while (numReps >= 0);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          iis.reset();
        } catch (IOException e) {
        }
      }
    }
  }


}
