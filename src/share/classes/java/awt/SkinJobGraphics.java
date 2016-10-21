package java.awt;

import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_BEVEL;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.BasicStroke.JOIN_ROUND;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.TextView;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SkinJob Android implementation of {@link Graphics}.
 */
public class SkinJobGraphics extends Graphics2D {
  private static final String TAG = "SkinJobGraphics";
  private final Set<CancelableImageObserver> pendingObservers = Collections.synchronizedSet(
      Collections.newSetFromMap(new WeakHashMap<>()));
  private final Canvas canvas;
  private final Bitmap bitmap;
  private final android.graphics.Paint pen;
  private final android.graphics.Paint brush;
  private final android.graphics.Paint eraser;
  private final RenderingHints renderingHints = SkinJob.defaultRenderingHints;
  private Stroke stroke = new BasicStroke();
  private java.awt.Paint awtPaint;
  private int color = Color.BLACK.getRGB();
  private Shape clip;
  private AffineTransform transform = new AffineTransform();
  private Font font = SkinJob.defaultFont;
  private ArrayList<Paint> pens;

  public SkinJobGraphics(Bitmap androidBitmap) {
    pen = new android.graphics.Paint();
    pen.setStrokeWidth(0);
    pen.setStyle(Style.STROKE);
    brush = new android.graphics.Paint();
    brush.setStyle(Style.FILL);
    eraser = new android.graphics.Paint();
    eraser.setStyle(Style.FILL);
    eraser.setAlpha(0);
    bitmap = androidBitmap;
    canvas = new Canvas(androidBitmap);
    clip = new Rectangle2D.Double(0, 0, androidBitmap.getWidth(), androidBitmap.getHeight());
  }

  @Override
  public Graphics create() {
    return null;
  }

  @Override
  public Color getColor() {
    return new Color(color, true);
  }

  @Override
  public synchronized void setColor(Color c) {
    setColor(c.getRGB());
  }

  @Override
  public void setPaintMode() {
    brush.setColorFilter(null);
    pen.setColorFilter(null);
  }

  /**
   * TODO: Check whether this actually meets the spec (i.e. first paints {@link #color}, then paints
   * {@code c1} over the pixels that were already that color.
   *
   * @param c1 the XOR alternation color
   */
  @Override
  public void setXORMode(Color c1) {
    brush.setColorFilter(new PorterDuffColorFilter(c1.getRGB(), PorterDuff.Mode.XOR));
    pen.setColorFilter(new PorterDuffColorFilter(c1.getRGB(), PorterDuff.Mode.XOR));
  }

  @Override
  public Font getFont() {
    return font;
  }

  @Override
  public void setFont(Font font) {
    this.font = font;
  }

  @Override
  public FontMetrics getFontMetrics(Font f) {
    return new SkinJobFontMetrics(f);
  }

  @Override
  public Rectangle getClipBounds() {
    // TODO
    return clip.getBounds();
  }

  @Override
  public void clipRect(int x, int y, int width, int height) {
    // TODO
  }

  @Override
  public void setClip(int x, int y, int width, int height) {
    clip = new Rectangle2D.Double(x, y, width, height);
  }

  @Override
  public Shape getClip() {
    return clip;
  }

  @Override
  public void setClip(Shape clip) {
    this.clip = clip;
  }

  @Override
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {
    // TODO
  }

  @Override
  public void drawLine(int x1, int y1, int x2, int y2) {
    canvas.drawLine(x1, y1, x2, y2, pen);
  }

  @Override
  public void fillRect(int x, int y, int width, int height) {
    canvas.drawRect(x, y, x + width, y + height, brush);
  }

  @Override
  public void clearRect(int x, int y, int width, int height) {
    canvas.drawRect(x, y, x + width, y + height, eraser);
  }

  @Override
  public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    canvas.drawRoundRect(x, y, x + width, y + height, arcWidth, arcHeight, pen);
  }

  @Override
  public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
    canvas.drawRoundRect(x, y, x + width, y + height, arcWidth, arcHeight, brush);
  }

  @Override
  public void drawOval(int x, int y, int width, int height) {
    canvas.drawOval(x, y, x + width, y + height, pen);
  }

  @Override
  public void fillOval(int x, int y, int width, int height) {
    canvas.drawOval(x, y, x + width, y + height, brush);
  }

  @Override
  public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    canvas.drawArc(x, y, x + width, y + height, startAngle, arcAngle, false, pen);
  }

  @Override
  public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
    canvas.drawArc(x, y, x + width, y + height, startAngle, arcAngle, false, brush);
  }

  @Override
  public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
    for (int i = 1; i < nPoints; i++) {
      drawLine(xPoints[i - 1], yPoints[i - 1], xPoints[i], yPoints[i]);
    }
  }

  @Override
  public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    drawPolyline(xPoints, yPoints, nPoints);
    drawLine(xPoints[nPoints - 1], yPoints[nPoints - 1], xPoints[0], yPoints[0]);
  }

  @Override
  public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
    // TODO
  }

  @Override
  public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
    // TODO
    return false;
  }

  @Override
  public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
    // TODO
    return false;
  }

  @Override
  public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
    CancelableImageObserver wrapperObserver = new CancelableImageObserver() {
      @Override
      public boolean imageUpdateInternal(
          Image img_, int infoflags, int x_, int y_, int width, int height) {
        drawImage(img, x, y, bgcolor, observer);
        return false;
      }
    };
    pendingObservers.add(wrapperObserver);
    int width = img.getWidth(wrapperObserver);
    int height = img.getHeight(wrapperObserver);
    if (width >= 0 && height >= 0) {
      Paint bg = new Paint();
      bg.setStyle(Style.FILL);
      bg.setColor(bgcolor.getRGB());
      canvas.drawRect(x, y, x + width, y + height, bg);
      return drawImage(img, x, y, observer);
    } else {
      return false;
    }
  }

  @Override
  public boolean drawImage(
      Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
    Paint bg = new Paint();
    bg.setStyle(Style.FILL);
    bg.setColor(bgcolor.getRGB());
    canvas.drawRect(x, y, x + width, y + height, bg);
    return drawImage(img, x, y, width, height, observer);
  }

  @Override
  public boolean drawImage(
      Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
      ImageObserver observer) {
    // TODO
    return false;
  }

  @Override
  public boolean drawImage(
      Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
      Color bgcolor, ImageObserver observer) {
    Paint bg = new Paint();
    bg.setStyle(Style.FILL);
    bg.setColor(bgcolor.getRGB());
    canvas.drawRect(dx1, dy1, dx2, dy2, bg);
    return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
  }

  @Override
  public void dispose() {
    for (CancelableImageObserver observer : pendingObservers) {
      observer.cancel();
    }
  }

  public synchronized void setColor(int color) {
    pen.setColor(color);
    brush.setColor(color);
  }

  @Override
  public void draw(Shape s) {
    fill(stroke.createStrokedShape(s));
  }

  @Override
  public boolean drawImage(
      Image img, AffineTransform xform, ImageObserver obs) {
    // TODO
    return false;
  }

  @Override
  public void drawImage(
      BufferedImage img, BufferedImageOp op, int x, int y) {
    Rectangle2D filteredSize = op.getBounds2D(img);
    BufferedImage filtered = new BufferedImage((int) filteredSize.getWidth(),
        (int) filteredSize.getHeight(),
        img.getType());
    op.filter(img, filtered);
    drawImage(filtered, x, y, null);
  }

  @Override
  public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
    // TODO
  }

  @Override
  public void drawRenderableImage(
      RenderableImage img, AffineTransform xform) {
    // TODO
  }

  @Override
  public void drawString(String str, float x, float y) {
    canvas.drawText(str, x, y, brush);
  }

  @Override
  public void drawString(AttributedCharacterIterator iterator, float x, float y) {
    drawString(iterator, (int) x, (int) y);
  }

  @Override
  public void drawGlyphVector(GlyphVector g, float x, float y) {
    // TODO
  }

  @Override
  public void fill(Shape s) {
    // TODO
  }

  @Override
  public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
    // TODO
    return false;
  }

  @Override
  public GraphicsConfiguration getDeviceConfiguration() {
    return null;
  }

  @Override
  public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
    renderingHints.put(hintKey, hintValue);
  }

  @Override
  public Object getRenderingHint(RenderingHints.Key hintKey) {
    return renderingHints.get(hintKey);
  }

  @Override
  public void addRenderingHints(Map<?, ?> hints) {
    renderingHints.putAll(hints);
  }

  @Override
  public RenderingHints getRenderingHints() {
    return renderingHints;
  }

  @Override
  public void setRenderingHints(Map<?, ?> hints) {
    renderingHints.clear();
    renderingHints.putAll(hints);
  }

  @Override
  public void translate(int x, int y) {
    canvas.translate(x, y);
  }

  @Override
  public void drawString(String str, int x, int y) {
    drawString(str, (float) x, (float) y);
  }

  @Override
  public synchronized void drawString(AttributedCharacterIterator iterator, int x, int y) {
    SpannableStringBuilder formattedText = new SpannableStringBuilder();
    int charsWritten = 0;
    for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next()) {
      formattedText.append(c);
      charsWritten++;
      Map<Attribute, Object> attributes = iterator.getAttributes();
      if (!attributes.isEmpty()) {
        new SkinJobTextAttributesDecoder(color)
            .addAttributes(attributes)
            .applyTo(formattedText, charsWritten - 1, charsWritten);
      }
    }
    TextView formattedTextView = new TextView(SkinJob.getAndroidApplicationContext());
    formattedTextView.setText(formattedText);
    formattedTextView.layout(x, y, canvas.getWidth(), canvas.getHeight());
    formattedTextView.draw(canvas);
  }

  @Override
  public void translate(double tx, double ty) {
    // TODO
  }

  @Override
  public void rotate(double theta) {
    // TODO
  }

  @Override
  public void rotate(double theta, double x, double y) {
    // TODO
  }

  @Override
  public void scale(double sx, double sy) {
    // TODO
  }

  @Override
  public void shear(double shx, double shy) {
    // TODO
  }

  @Override
  public void transform(AffineTransform Tx) {
    transform.concatenate(Tx);
  }

  @Override
  public AffineTransform getTransform() {
    return transform;
  }

  @Override
  public void setTransform(AffineTransform Tx) {
    transform = Tx;
  }

  @Override
  public java.awt.Paint getPaint() {
    return awtPaint;
  }

  @Override
  public void setPaint(java.awt.Paint paint) {
    awtPaint = paint;
  }

  @Override
  public Composite getComposite() {
    // TODO
    return null;
  }

  @Override
  public void setComposite(Composite comp) {
    // TODO
  }

  @Override
  public Color getBackground() {
    // TODO
    return null;
  }

  @Override
  public void setBackground(Color color) {
    // TODO
  }

  @Override
  public synchronized Stroke getStroke() {
    return stroke;
  }

  /**
   * Only {@link BasicStroke} attributes are supported when using {@code draw*} methods other than
   * {@link #draw(Shape)}.
   *
   * @param s the {@code Stroke} object to be used to stroke a
   *          {@code Shape} during the rendering process
   */
  @Override
  public synchronized void setStroke(Stroke s) {
    stroke = s;
    if (s instanceof BasicStroke) {
      BasicStroke basicStroke = (BasicStroke) s;
      pen.setStrokeWidth(basicStroke.getLineWidth());
      pen.setStrokeMiter(basicStroke.getMiterLimit());
      int join = basicStroke.getLineJoin();
      switch (join) {
        case JOIN_BEVEL:
          pen.setStrokeJoin(Join.BEVEL);
          break;
        case JOIN_MITER:
          pen.setStrokeJoin(Join.MITER);
          break;
        case JOIN_ROUND:
          pen.setStrokeJoin(Join.ROUND);
          break;
        default:
          Log.w(TAG, "Ignoring unknown stroke join type " + join);
      }
      int cap = basicStroke.getEndCap();
      switch (cap) {
        case CAP_BUTT:
          pen.setStrokeCap(Cap.BUTT);
          break;
        case CAP_ROUND:
          pen.setStrokeCap(Cap.ROUND);
          break;
        case CAP_SQUARE:
          pen.setStrokeCap(Cap.SQUARE);
          break;
        default:
          Log.w(TAG, "Ignoring unknown stroke cap type " + join);
      }
      float[] dashes = basicStroke.getDashArray();
      if (dashes != null && dashes.length > 1) {
        pen.setPathEffect(new DashPathEffect(dashes, basicStroke.getDashPhase()));
      } else {
        pen.setPathEffect(null);
      }
    }
  }

  @Override
  public void clip(Shape s) {
    // TODO
  }

  @Override
  public FontRenderContext getFontRenderContext() {
    return null;
  }

  public Canvas getCanvas() {
    return canvas;
  }

  private abstract static class CancelableImageObserver implements ImageObserver {
    private final AtomicBoolean canceled = new AtomicBoolean(false);

    CancelableImageObserver() {
    }

    public void cancel() {
      canceled.lazySet(true);
    }

    public abstract boolean imageUpdateInternal(
        Image img, int infoflags, int x, int y, int width, int height);

    @Override
    public boolean imageUpdate(
        Image img, int infoflags, int x, int y, int width, int height) {
      return canceled.get() ? false : imageUpdateInternal(img, infoflags, x, y, width, height);
    }
  }
}
