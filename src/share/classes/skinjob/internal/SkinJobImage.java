package skinjob.internal;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

/**
 * Created by cryoc on 2016-10-11.
 */
public class SkinJobImage extends Image {
  protected final Bitmap androidBitmap;
  protected final ImageProducer source;

  public SkinJobImage(String filename) {
    this(BitmapFactory.decodeFile(filename));
  }

  public SkinJobImage(Bitmap androidBitmap) {
    this(null, androidBitmap);
  }

  public SkinJobImage(ImageProducer source, Bitmap androidBitmap) {
    this.androidBitmap = androidBitmap;
    this.source = source;
  }

  @Override
  public int getWidth(ImageObserver observer) {
    return androidBitmap.getWidth();
  }

  @Override
  public int getHeight(ImageObserver observer) {
    return androidBitmap.getHeight();
  }

  @Override
  public ImageProducer getSource() {
    return source;
  }

  @Override
  public Graphics getGraphics() {
    if (androidBitmap.isMutable()) {
      return new SkinJobGraphics(androidBitmap);
    }
    return null;
  }

  @Override
  public Object getProperty(String name, ImageObserver observer) {
    return null;
  }
}
