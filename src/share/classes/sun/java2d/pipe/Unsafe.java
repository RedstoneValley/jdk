package sun.java2d.pipe;

/**
 * Created by cryoc on 2016-10-17.
 */
// TODO: Unstub fields
public class Unsafe {
  public static final long ARRAY_BYTE_BASE_OFFSET = 0;
  public static final long ARRAY_SHORT_BASE_OFFSET = 0;
  public static final long ARRAY_INT_BASE_OFFSET = 0;
  public static final long ARRAY_FLOAT_BASE_OFFSET = 0;
  public static final long ARRAY_LONG_BASE_OFFSET = 0;
  private static Unsafe unsafe;

  public static Unsafe getUnsafe() {
    return unsafe;
  }

  public long allocateMemory(int numBytes) {
    // TODO
    return 0;
  }

  public void putByte(long curAddress, byte x) {
    // TODO
  }

  public void copyMemory(
      Object src, long srcOffsetInBytes, Object dest, long destOffsetInBytes, long lengthInBytes) {
    // TODO
  }

  public void putShort(long curAddress, short x) {
    // TODO
  }

  public void putInt(long l, int x) {
    // TODO
  }

  public void putFloat(long curAddress, float x) {
    // TODO
  }

  public void putLong(long curAddress, long x) {
    // TODO
  }

  public void putDouble(long curAddress, double x) {
    // TODO
  }
}
