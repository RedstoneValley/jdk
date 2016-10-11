package java.awt;

import android.util.DisplayMetrics;
import android.view.Display;

import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.util.WeakHashMap;

/**
 * Created by cryoc on 2016-10-10.
 */
public class SkinJobGraphicsConfiguration extends GraphicsConfiguration {
    private static final WeakHashMap<Display, SkinJobGraphicsConfiguration> INSTANCES =
            new WeakHashMap<>();
    protected static final AffineTransform IDENTITY_TRANSFORM = new AffineTransform();
    protected final AffineTransform scale72DpiInput;
    protected final Rectangle displayBounds;
    protected final GraphicsDevice device;

    protected SkinJobGraphicsConfiguration(Display androidDisplay, GraphicsDevice device) {
        DisplayMetrics metrics = new DisplayMetrics();
        androidDisplay.getMetrics(metrics);
        scale72DpiInput = new AffineTransform();
        scale72DpiInput.scale(metrics.xdpi / 72.0, metrics.ydpi / 72.0);
        displayBounds = new Rectangle(metrics.widthPixels, metrics.heightPixels);
        this.device = device;
        INSTANCES.put(androidDisplay, this);
    }

    public static SkinJobGraphicsConfiguration get(Display androidDisplay) {
        SkinJobGraphicsConfiguration instance = INSTANCES.get(androidDisplay);
        if (instance == null) {
            return new SkinJobGraphicsConfiguration(androidDisplay);
        }
        return instance;
    }

    protected SkinJobGraphicsConfiguration(Display androidDisplay) {
        this(androidDisplay, new SkinJobGraphicsDevice(androidDisplay));
    }

    @Override
    public GraphicsDevice getDevice() {
        // TODO
        return null;
    }

    @Override
    public ColorModel getColorModel() {
        return ColorModel.getRGBdefault();
    }

    @Override
    public ColorModel getColorModel(int transparency) {
        return ColorModel.getRGBdefault();
    }

    @Override
    public AffineTransform getDefaultTransform() {
        return IDENTITY_TRANSFORM;
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        return scale72DpiInput;
    }

    @Override
    public Rectangle getBounds() {
        return displayBounds;
    }
}
