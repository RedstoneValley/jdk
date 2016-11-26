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

    public SkinJobBufferedImage(int width, int height) {
        super(width, height, TYPE_INT_ARGB);
        androidBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    @Override
    public Bitmap sjGetAndroidBitmap() {
        return androidBitmap;
    }
}
