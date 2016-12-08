package skinjob.internal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import skinjob.SkinJobGlobals;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * Created by chris on 11/25/2016.
 */

public class SkinJobVolatileImage extends VolatileImage implements SkinJobAndroidBitmapWrapper,
    Serializable {
  private static final long serialVersionUID = -5899239510088899399L;
  private final int width;
  private final int height;
  private final ImageCapabilities caps;
  private final int transparency;
  private Bitmap androidBitmap;

  public SkinJobVolatileImage(int width, int height, ImageCapabilities caps, int transparency) {
    this.width = width;
    this.height = height;
    this.caps = caps;
    this.transparency = transparency;
    androidBitmap = Bitmap.createBitmap(width, height, ARGB_8888);
  }

  @Override
  public BufferedImage getSnapshot() {
    return new SkinJobBufferedImage(androidBitmap);
  }

  @Override
  public int getWidth() {
    return width;
  }

  @Override
  public int getHeight() {
    return height;
  }

  @Override
  public Graphics2D createGraphics() {
    return new SkinJobGraphics(androidBitmap);
  }

  @Override
  public int validate(GraphicsConfiguration gc) {
    if (androidBitmap.isRecycled()) {
      return VolatileImage.IMAGE_RESTORED;
    }
    return VolatileImage.IMAGE_OK;
  }

  @Override
  public boolean contentsLost() {
    return androidBitmap.isRecycled();
  }

  @Override
  public ImageCapabilities getCapabilities() {
    return caps;
  }

  @Override
  public int getWidth(ImageObserver observer) {
    return getWidth();
  }

  @Override
  public int getHeight(ImageObserver observer) {
    return getHeight();
  }

  @Override
  public Object getProperty(String name, ImageObserver observer) {
    if ("comment".equals(name)) {
      //noinspection ObjectToString
      return "SkinJob VolatileImage wrapping " + androidBitmap;
    }
    return VolatileImage.UndefinedProperty;
  }

  @Override
  public Bitmap sjGetAndroidBitmap() {
    return androidBitmap;
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    androidBitmap
        .compress(SkinJobGlobals.SERIAL_IMAGE_FORMAT, SkinJobGlobals.SERIAL_IMAGE_QUALITY, stream);
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
