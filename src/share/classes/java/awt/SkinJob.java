package java.awt;

import android.app.Application;
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
public class SkinJob {

  /**
   * Since Android only recognizes normal and bold, font weights greater than or equal to this
   * value become bold and the rest become normal.
   */
  public static volatile float boldThreshold = TextAttribute.WEIGHT_MEDIUM;
  /**
   * Whether {@link SkinJobListPeer#select(int)} should be animated.
   */
  public static volatile boolean animateListAutoSelection = true;
  /**
   * How far {@link SkinJobComponentPeerForView#setZOrder(ComponentPeer)} should place a
   * component in front of the other component, as a multiple of
   * {@link android.util.DisplayMetrics#density}.
   */
  public static volatile float layerZSpacing = 100.0f;
  public static volatile View menuDivider;
  private static Resources systemResources = Resources.getSystem();
  public static volatile int menuTextColor = systemResources.getColor(
      android.R.color.primary_text_dark, systemResources.newTheme());

  /**
   * Height at which {@link FontMetrics} should report text is struck through, as a multiple of
   * {@link android.graphics.Paint.FontMetrics#ascent}. Doesn't affect the actual appearance of the
   * strikethrough, but must exist for backward-compatibility.
   */
  public static volatile float strikeThroughOffset = 0.5f;

  static {
    Context context = getAndroidApplicationContext();
    menuDivider = View.inflate(context,
        android.R.drawable.divider_horizontal_dim_dark,
        new ListView(context));
  }

  /**
   * Do not instantiate.
   */
  private SkinJob() {
  }

  public static Context getAndroidApplicationContext() {
    try {
      final Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
      final Method method = activityThreadClass.getMethod("currentApplication");
      return (Application) method.invoke(null, (Object[]) null);
    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException
        | ClassNotFoundException e) {
      throw new AWTError(e.toString());
    }
  }
}
