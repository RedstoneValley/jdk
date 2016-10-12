package java.awt;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.SpannableStringBuilder;
import android.widget.TextView;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * SkinJob Android implementation of {@link Graphics}.
 */
public class SkinJobGraphics extends Graphics {
  private static final String TAG = "SkinJobGraphics";
  private Set<CancelableImageObserver> pendingObservers
      = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
  private Canvas canvas;
  private int color = Color.BLACK.getRGB();
  private android.graphics.Paint pen;
  private android.graphics.Paint brush;
  private android.graphics.Paint eraser;

  public SkinJobGraphics(Bitmap androidBitmap) {
    pen = new android.graphics.Paint();
    pen.setStrokeWidth(0);
    pen.setStyle(Paint.Style.STROKE);
    brush = new android.graphics.Paint();
    brush.setStyle(Paint.Style.FILL);
    eraser = new android.graphics.Paint();
    eraser.setStyle(Paint.Style.FILL);
    eraser.setAlpha(0);
    canvas = new Canvas(androidBitmap);
  }

  @Override
  public Graphics create() {
    return null;
  }

  @Override
  public void translate(int x, int y) {
    canvas.translate(x, y);
  }

  @Override
  public Color getColor() {
    return new Color(color, true);
  }

  @Override
  public synchronized void setColor(Color c) {
    color = c.getRGB();
    pen.setColor(color);
    brush.setColor(color);
  }

  @Override
  public void setPaintMode() {
    // TODO
  }

  @Override
  public void setXORMode(Color c1) {
    // TODO
  }

  @Override
  public Font getFont() {
    return null;
  }

  @Override
  public void setFont(Font font) {

  }

  @Override
  public FontMetrics getFontMetrics(Font f) {
    return new SkinJobFontMetrics(f);
  }

  @Override
  public Rectangle getClipBounds() {
    return null;
  }

  @Override
  public void clipRect(int x, int y, int width, int height) {

  }

  @Override
  public void setClip(int x, int y, int width, int height) {

  }

  @Override
  public Shape getClip() {
    return null;
  }

  @Override
  public void setClip(Shape clip) {

  }

  @Override
  public void copyArea(int x, int y, int width, int height, int dx, int dy) {

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
  public void drawString(String str, int x, int y) {
    canvas.drawText(str, x, y, brush);
  }

  @Override
  public synchronized void drawString(AttributedCharacterIterator iterator, int x, int y) {
    SpannableStringBuilder formattedText = new SpannableStringBuilder();
    int charsWritten = 0;
    for (char c = iterator.first(); c != CharacterIterator.DONE; c = iterator.next()) {
      formattedText.append(c);
      charsWritten++;
      Map<AttributedCharacterIterator.Attribute, Object> attributes = iterator.getAttributes();
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
      bg.setStyle(Paint.Style.FILL);
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
    bg.setStyle(Paint.Style.FILL);
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
    bg.setStyle(Paint.Style.FILL);
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

  public Canvas getCanvas() {
    return canvas;
  }

  private static abstract class CancelableImageObserver implements ImageObserver {
    private AtomicBoolean canceled = new AtomicBoolean(false);

    public void cancel() {
      canceled.lazySet(true);
    }

    public abstract boolean imageUpdateInternal(
        Image img, int infoflags, int x, int y, int width, int height);

    @Override
    public boolean imageUpdate(
        Image img, int infoflags, int x, int y, int width, int height) {
      if (canceled.get()) {
        return false;
      } else {
        return imageUpdateInternal(img, infoflags, x, y, width, height);
      }
    }
  }
}
