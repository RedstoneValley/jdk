package java.awt;

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

/**
 * Static members used to implement AWT on Android. Public mutable fields in this class control
 * certain behaviors of wrapped Android objects that aren't fully specified by AWT's implementation
 * contract.
 */
@SuppressWarnings("MagicNumber")
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

  private static final Resources systemResources = Resources.getSystem();

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
  public static final int menuTextColor = systemResources.getColor(
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

  static {
    Context context = getAndroidApplicationContext();
    menuDivider = View.inflate(context,
        drawable.divider_horizontal_dim_dark,
        new ListView(context));
  }

  /**
   * The application's {@link Context} instance. To maintain backward compatibility with AWT apps
   * not designed for Android, SkinJob will look up the {@code Context} on demand if the application
   * hasn't set this variable.
   */
  public static volatile Context registeredAndroidContext = null;

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
}
