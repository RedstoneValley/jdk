package skinjob.internal;

import android.graphics.Bitmap;

import java.awt.image.BufferedImage;

/**
 * Created by chris on 11/25/2016.
 */
public class SkinJobBufferedImage extends BufferedImage implements SkinJobAndroidBitmapWrapper {
    private final Bitmap androidBitmap;
    public SkinJobBufferedImage(Bitmap androidBitmap) {
        super(androidBitmap.getWidth(), androidBitmap.getHeight(), TYPE_INT_ARGB);
        this.androidBitmap = androidBitmap;
        // TODO: Convert androidBitmap contents to the BufferedImage contents
    }

    @Override
    public Bitmap sjGetAndroidBitmap() {
        return androidBitmap;
    }
}
