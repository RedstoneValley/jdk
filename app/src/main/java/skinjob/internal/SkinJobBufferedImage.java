package skinjob.internal;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import skinjob.SkinJobGlobals;

/**
 * Created by chris on 11/25/2016.
 */
public class SkinJobBufferedImage extends BufferedImage implements SkinJobAndroidBitmapWrapper,
    Serializable {
  private static final long serialVersionUID = -4732300791764352434L;
  private Bitmap androidBitmap;

  public SkinJobBufferedImage(Bitmap androidBitmap) {
    super(androidBitmap.getWidth(), androidBitmap.getHeight(), TYPE_INT_ARGB);
    this.androidBitmap = androidBitmap;
    // TODO: Convert androidBitmap contents to the BufferedImage contents
  }

  public SkinJobBufferedImage(int width, int height) {
    super(width, height, TYPE_INT_ARGB);
    androidBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
  }

  @Override
  public Bitmap sjGetAndroidBitmap() {
    return androidBitmap;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    androidBitmap.compress(
        SkinJobGlobals.SERIAL_IMAGE_FORMAT, SkinJobGlobals.SERIAL_IMAGE_QUALITY, stream);
    out.writeObject(stream.toByteArray());
  }

  private void readObject(ObjectInputStream in) throws IOException {
    try {
      byte[] androidBitmapBytes = (byte[]) in.readObject();
      androidBitmap = BitmapFactory
          .decodeByteArray(androidBitmapBytes, 0, androidBitmapBytes.length);
    } catch (ClassNotFoundException | ClassCastException e) {
      throw new IOException(e);
    }
  }
}
