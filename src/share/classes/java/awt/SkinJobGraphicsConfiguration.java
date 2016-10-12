package java.awt;

import android.hardware.display.DisplayManager;
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
    private static final Display DEFAULT_DISPLAY = SkinJob
            .getAndroidApplicationContext()
            .getSystemService(DisplayManager.class)
            .getDisplay(Display.DEFAULT_DISPLAY);
    protected final AffineTransform scale72DpiInput;
    protected final Rectangle displayBounds;
    protected final GraphicsDevice device;
    protected final double dpi;

    protected SkinJobGraphicsConfiguration(Display androidDisplay, GraphicsDevice device) {
        DisplayMetrics metrics = new DisplayMetrics();
        androidDisplay.getMetrics(metrics);
        dpi = Math.sqrt(metrics.xdpi * metrics.ydpi);
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

    public static SkinJobGraphicsConfiguration getDefault() {
        return get(DEFAULT_DISPLAY);
    }

    @Override
    public GraphicsDevice getDevice() {
        return device;
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
