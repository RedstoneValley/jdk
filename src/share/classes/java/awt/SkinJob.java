package java.awt;

import static java.awt.RenderingHints.KEY_ALPHA_INTERPOLATION;
import static java.awt.RenderingHints.KEY_ANTIALIASING;
import static java.awt.RenderingHints.KEY_COLOR_RENDERING;
import static java.awt.RenderingHints.KEY_DITHERING;
import static java.awt.RenderingHints.KEY_FRACTIONALMETRICS;
import static java.awt.RenderingHints.KEY_INTERPOLATION;
import static java.awt.RenderingHints.KEY_RENDERING;
import static java.awt.RenderingHints.KEY_STROKE_CONTROL;
import static java.awt.RenderingHints.KEY_TEXT_ANTIALIASING;
import static java.awt.RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY;
import static java.awt.RenderingHints.VALUE_ANTIALIAS_ON;
import static java.awt.RenderingHints.VALUE_COLOR_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_DITHER_ENABLE;
import static java.awt.RenderingHints.VALUE_FRACTIONALMETRICS_OFF;
import static java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
import static java.awt.RenderingHints.VALUE_RENDER_QUALITY;
import static java.awt.RenderingHints.VALUE_STROKE_NORMALIZE;
import static java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON;

import android.R.color;
import android.R.drawable;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ListView;
import java.awt.font.TextAttribute;
import java.awt.peer.ComponentPeer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Static members used to implement AWT on Android. Public mutable fields in this class control
 * certain behaviors of wrapped Android objects that aren't fully specified by AWT's implementation
 * contract.
 */
@SuppressWarnings("MagicNumber") // "magic" numbers are only used as default-defaults
public final class SkinJob {

  /**
   * Conversion from an elliptical arc to a cubic Bezier spline is inherently approximate, and is
   * used in {@link java.awt.geom.ArcIterator}. The more segment endpoints the spline has, the
   * better the approximation, but the longer it takes to draw. This constant determines the maximum
   * angle between segment endpoints (which will be at equal angles across the arc). OpenJDK AWT
   * uses a value of 90.0. With that value, an approximate circle of radius 1.0 will fit between two
   * concentric true circles of radius 1.0 and radius 1.00027253.
   */
  public static final double maxDegreesPerArcSegment = 90.0;
  /**
   * Default value for the miterLimit parameter of {@link
   * BasicStroke#BasicStroke(float, int, int, float)} when called from another constructor that
   * doesn't take that parameter. OpenJDK AWT uses 10.0f.
   */
  public static final float defaultMiterLimit = 10.0f;
  /**
   * Since Android only recognizes normal and bold, font weights greater than or equal to this
   * value become bold and the rest become normal.
   */
  public static final float boldThreshold = TextAttribute.WEIGHT_MEDIUM;
  /**
   * Whether {@link SkinJobListPeer#select(int)} should be animated.
   */
  public static final boolean animateListAutoSelection = true;
  /**
   * How far {@link SkinJobComponentPeerForView#setZOrder(ComponentPeer)} should place a
   * component in front of the other component, as a multiple of
   * {@link android.util.DisplayMetrics#density}.
   */
  public static final float layerZSpacing = 100.0f;
  public static final View menuDivider;
  public static final int inputMethodMenuX = 60;
  public static final int inputMethodMenuY = 80;

  private static final Resources systemResources = Resources.getSystem();
  public static volatile int defaultForegroundColor = systemResources.getColor(
      color.primary_text_dark,
      systemResources.newTheme());
  /**
   * Height at which {@link FontMetrics} should report text is struck through, as a multiple of
   * {@link android.graphics.Paint.FontMetrics#ascent}. Doesn't affect the actual appearance of the
   * strikethrough, but must exist for backward-compatibility.
   */
  public static volatile float strikeThroughOffset = 0.5f;
  /*
   * Size of the array of precomputed character widths in each {@link FontMetrics}. Code points
   * lower than this value will be stored in the array; the rest will be recalculated on demand.
   * Using a higher value will require more memory, but will improve performance when rendering
   * text where a lot of characters have high code-point values (e.g. those in non-Latin alphabets).
   * Should never exceed 0x10FFFD, since that's the highest Unicode point. OpenJDK AWT uses 256.
   */
  public static volatile int precomputedCharacterWidthArraySize = 256;
  public static volatile int defaultDragThreshold = 5;
  public static volatile int defaultFontSize = 12;
  public static volatile Font defaultFont = new Font(Font.DIALOG, Font.PLAIN, defaultFontSize);
  public static RenderingHints defaultRenderingHints;
  /**
   * The application's {@link Context} instance. To maintain backward compatibility with AWT apps
   * not designed for Android, SkinJob will look up the {@code Context} on demand if the application
   * hasn't set this variable.
   */
  public static volatile Context registeredAndroidContext = null;
  public static volatile int defaultAutoscrollInitialDelayMs = 100;
  public static volatile int defaultAutoscrollIntervalMs = 100;
  private static volatile SkinJobGraphicsEnvironment graphicsEnvironment;

  static {
    Context context = getAndroidApplicationContext();
    menuDivider = View.inflate(context,
        drawable.divider_horizontal_dim_dark,
        new ListView(context));
    HashMap<RenderingHints.Key, Object> m = new HashMap<>();
    m.put(KEY_ALPHA_INTERPOLATION, VALUE_ALPHA_INTERPOLATION_QUALITY);
    m.put(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
    m.put(KEY_COLOR_RENDERING, VALUE_COLOR_RENDER_QUALITY);
    m.put(KEY_DITHERING, VALUE_DITHER_ENABLE);
    m.put(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_OFF);
    m.put(KEY_INTERPOLATION, VALUE_INTERPOLATION_BICUBIC);
    m.put(KEY_RENDERING, VALUE_RENDER_QUALITY);
    m.put(KEY_STROKE_CONTROL, VALUE_STROKE_NORMALIZE);
    m.put(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
    // TODO: Is one of VALUE_TEXT_ANTIALIAS_LCD_* suitable for a typical mobile (OLED) screen?
    defaultRenderingHints = new RenderingHints(m);
  }

  /**
   * Do not instantiate.
   */
  private SkinJob() {
  }

  public static Context getAndroidApplicationContext() {
    Context context = registeredAndroidContext;
    if (context != null) {
      return context;
    }
    try {
      Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
      Method method = activityThreadClass.getMethod("currentApplication");
      return (Context) method.invoke(null, (Object[]) null);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
        | ClassNotFoundException e) {
      throw new AWTError(e.toString());
    }
  }

  public static synchronized SkinJobGraphicsEnvironment getGraphicsEnvironment() {
    if (graphicsEnvironment == null) {
      graphicsEnvironment = new SkinJobGraphicsEnvironment(getAndroidApplicationContext());
    }
    return graphicsEnvironment;
  }

  public static ThreadGroup getRootThreadGroup() {
    ThreadGroup group = Thread.currentThread().getThreadGroup();
    if (group == null) {
      return null;
    }
    while (true) {
      ThreadGroup parent = group.getParent();
      if (parent == null || parent.equals(group)) {
        return group;
      }
      group = parent;
    }
  }
}
