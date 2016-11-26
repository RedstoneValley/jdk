package skinjob.internal;

import android.graphics.Bitmap;

import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.ImageCapabilities;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;

import static android.graphics.Bitmap.Config.ARGB_8888;

/**
 * Created by chris on 11/25/2016.
 */

public class SkinJobVolatileImage extends VolatileImage implements SkinJobAndroidBitmapWrapper {
    private final int width;
    private final int height;
    private final ImageCapabilities caps;
    private final int transparency;
    private final Bitmap androidBitmap;

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
            return "SkinJob VolatileImage wrapping " + androidBitmap;
        }
        return VolatileImage.UndefinedProperty;
    }

    @Override
    public Bitmap sjGetAndroidBitmap() {
        return androidBitmap;
    }
}
