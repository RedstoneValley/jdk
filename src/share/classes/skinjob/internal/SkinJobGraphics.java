package skinjob.internal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.widget.TextView;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.text.AttributedCharacterIterator.Attribute;
import java.text.CharacterIterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import skinjob.SkinJobGlobals;
import skinjob.util.Geometry;

import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_BEVEL;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.BasicStroke.JOIN_ROUND;
import static skinjob.util.SkinJobUtil.asAndroidBitmap;

/**
 * SkinJob Android implementation of {@link Graphics}.
 */
public class SkinJobGraphics extends Graphics2D {
    private static final String TAG = "SkinJobGraphics";
    private static final Color TRANSPARENT = new Color(0);
    private final Set<CancelableImageObserver> pendingObservers = Collections.synchronizedSet(
            new HashSet<>());
    private final Canvas canvas;
    private final Bitmap bitmap;
    private final android.graphics.Paint pen;
    private final android.graphics.Paint brush;
    private final android.graphics.Paint eraser;
    private final RenderingHints renderingHints = SkinJobGlobals.defaultRenderingHints;
    private Stroke stroke = new BasicStroke();
    private java.awt.Paint awtPaint;
    private int color = Color.BLACK.getRGB();
    private Shape clip;
    // TODO: Most methods currently don't apply the transform; they need to!
    private AffineTransform transform = new AffineTransform();
    private Font font = SkinJobGlobals.defaultFont;

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

    public Bitmap sjGetAndroidBitmap() {
        return bitmap;
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
        brush.setColorFilter(new PorterDuffColorFilter(c1.getRGB(), Mode.XOR));
        pen.setColorFilter(new PorterDuffColorFilter(c1.getRGB(), Mode.XOR));
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
        if (clip == null) {
            return null;
        } else {
            return clip.getBounds();
        }
    }

    @Override
    public synchronized void clipRect(int x, int y, int width, int height) {
        if (clip == null) {
            // No existing clip to intersect with
            setClip(x, y, width, height);
        } else {
            Area clipArea = Geometry.asArea(clip);
            clipArea.intersect(new Area(new Rectangle2D.Double(x, y, width, height)));
            clip = clipArea;
        }
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

    protected void drawPath(int[] xPoints, int[] yPoints, int nPoints, boolean close, Paint paint) {
        Path path = new Path();
        path.moveTo(xPoints[0], yPoints[0]);
        for (int i = 1; i < nPoints; i++) {
            path.lineTo(xPoints[i], yPoints[i]);
        }
        if (close) {
            path.lineTo(xPoints[0], yPoints[0]);
        }
        canvas.drawPath(path, paint);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        drawPath(xPoints, yPoints, nPoints, false, pen);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        drawPath(xPoints, yPoints, nPoints, true, pen);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        drawPath(xPoints, yPoints, nPoints, true, brush);
    }

    @Override
    public synchronized boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        AffineTransform combinedTransform = new AffineTransform(transform);
        combinedTransform.translate(x, y);
        canvas.drawBitmap(asAndroidBitmap(img),
                Geometry.transformToMatrix(combinedTransform), brush);
        return true;
    }

    @Override
    public synchronized boolean drawImage(Image img, int x, int y, int width, int height,
                ImageObserver observer) {
        return drawImage(img, x, y, width, height, TRANSPARENT, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        CancelableImageObserver wrapperObserver = new CancelableImageObserver(observer) {
            @Override
            public boolean imageUpdateInternal(
                    Image img_, int infoflags, int x_, int y_, int width, int height) {
                if (width <= 0 || height <= 0) {
                    return false;
                }
                drawImage(img, x, y, bgcolor, observer);
                return true;
            }
        };
        int width = img.getWidth(wrapperObserver);
        int height = img.getHeight(wrapperObserver);
        if (width >= 0 && height >= 0) {
            Paint bg = new Paint();
            bg.setStyle(Style.FILL);
            bg.setColor(bgcolor.getRGB());
            canvas.drawRect(x, y, x + width, y + height, bg);
            return drawImage(img, x, y, observer);
        } else {
            pendingObservers.add(wrapperObserver);
            return false;
        }
    }

    @Override
    public boolean drawImage(
            Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {

        final Paint currentBrush;
        // Position & scale image, & apply current transform too
        final AffineTransform combinedTransform;
        synchronized (this) {
            currentBrush = brush;
            combinedTransform = new AffineTransform(transform);
        }
        combinedTransform.translate(x, y);

        CancelableImageObserver wrapperObserver = new CancelableImageObserver(observer) {
            @Override
            public boolean imageUpdateInternal(
                    Image img_, int infoflags, int x_, int y_, int origWidth, int origHeight) {
                float scaleX = width / (float) origWidth;
                float scaleY = height / (float) origHeight;
                canvas.drawBitmap(asAndroidBitmap(img),
                        Geometry.transformToMatrix(combinedTransform), currentBrush);
                drawImage(img, x, y, bgcolor, observer);
                return false;
            }
        };
        Paint bg = new Paint();
        bg.setStyle(Style.FILL);
        bg.setColor(bgcolor.getRGB());
        canvas.drawRect(x, y, x + width, y + height, bg);
        int origWidth = img.getWidth(wrapperObserver);
        int origHeight = img.getHeight(wrapperObserver);
        if (origWidth >= 0 && origHeight >= 0) {
            wrapperObserver.imageUpdateInternal(img, 0, x, y, origWidth, origHeight);
            return true;
        } else {
            pendingObservers.add(wrapperObserver);
            return false;
        }
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
        this.color = color;
    }

    @Override
    public void draw(Shape s) {
        canvas.drawPath(Geometry.asAndroidPath(s, transform), pen);
    }

    private synchronized void drawBitmap(Bitmap bitmap, AffineTransform transform) {
        canvas.drawBitmap(bitmap, Geometry.transformToMatrix(transform), brush);
    }

    @Override
    public boolean drawImage(
            Image img, AffineTransform xform, ImageObserver obs) {
        drawBitmap(asAndroidBitmap(img), xform);
        return true;
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
        drawBitmap(asAndroidBitmap(img), xform);
    }

    @Override
    public void drawRenderableImage(
            RenderableImage img, AffineTransform xform) {
        drawRenderedImage(img.createDefaultRendering(), xform);
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
        int nGlyphs = g.getNumGlyphs();
        Paint fontAndColor = new Paint(g.getFont().sjGetAndroidPaint());
        fontAndColor.setColor(color);
        for (int i = 0; i < nGlyphs; i++) {
            Point2D glyphPos = g.getGlyphPosition(i);
            Point2D absGlyphPos = new Point2D.Double(x + glyphPos.getX(), y + glyphPos.getY());
            char[] glyph = Character.toChars(g.getGlyphCode(i));
            canvas.drawText(glyph,
                    0,
                    glyph.length,
                    (float) absGlyphPos.getX(),
                    (float) absGlyphPos.getY(),
                    fontAndColor);
        }
    }

    @Override
    public synchronized void fill(Shape s) {
        canvas.drawPath(Geometry.asAndroidPath(s, transform), brush);
    }

    @Override
    public synchronized boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        if (!clip.intersects(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight())) {
            return false;
        }
        Shape shapeToCheck;
        if (onStroke) {
            shapeToCheck = stroke.createStrokedShape(s);
        } else {
            shapeToCheck = s;
        }
        Area transformed = new Area(shapeToCheck);
        transformed.transform(transform);
        return transformed.intersects(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight());
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return null;
    }

    @Override
    public void setRenderingHint(Key hintKey, Object hintValue) {
        renderingHints.put(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(Key hintKey) {
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
                new TextAttributesDecoder(color)
                        .addAttributes(attributes)
                        .applyTo(formattedText, charsWritten - 1, charsWritten);
            }
        }
        TextView formattedTextView = new TextView(SkinJobGlobals.getAndroidApplicationContext());
        formattedTextView.setText(formattedText);
        int right, bottom;
        if (clip == null) {
            right = canvas.getWidth();
            bottom = canvas.getHeight();
        } else {
            Rectangle2D bounds = clip.getBounds2D();
            right = (int) bounds.getMaxX();
            bottom = (int) bounds.getMaxY();
        }
        formattedTextView.layout(x, y, right, bottom);
        formattedTextView.draw(canvas);
    }

    @Override
    public synchronized void translate(double tx, double ty) {
        transform = new AffineTransform(transform);
        transform.translate(tx, ty);
    }

    @Override
    public synchronized void rotate(double theta) {
        transform = new AffineTransform(transform);
        transform.rotate(theta);
    }

    @Override
    public synchronized void rotate(double theta, double x, double y) {
        transform = new AffineTransform(transform);
        transform.rotate(theta, x, y);
    }

    @Override
    public synchronized void scale(double sx, double sy) {
        transform = new AffineTransform(transform);
        transform.scale(sx, sy);
    }

    @Override
    public synchronized void shear(double shx, double shy) {
        transform = new AffineTransform(transform);
        transform.shear(shx, shy);
    }

    @Override
    public synchronized void transform(AffineTransform tx) {
        transform = new AffineTransform(transform);
        transform.concatenate(tx);
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
    public synchronized void clip(Shape s) {
        clip = Geometry.getIntersection(clip, s);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return null;
    }

    public Canvas sjGetAndroidCanvas() {
        return canvas;
    }

    private abstract static class CancelableImageObserver implements ImageObserver {
        private final AtomicBoolean canceled = new AtomicBoolean(false);
        private volatile ImageObserver innerObserver;

        CancelableImageObserver(ImageObserver innerObserver) {
            this.innerObserver = innerObserver;
        }

        public void cancel() {
            canceled.lazySet(true);
            innerObserver = null;
        }

        public abstract boolean imageUpdateInternal(
                Image img, int infoflags, int x, int y, int width, int height);

        @Override
        public boolean imageUpdate(
                Image img, int infoflags, int x, int y, int width, int height) {
            boolean drawn = false;
            if (!canceled.get()) {
                drawn = imageUpdateInternal(img, infoflags, x, y, width, height);
            }
            ImageObserver thisInnerObserver = innerObserver;
            if (thisInnerObserver != null) {
                if (drawn) {
                    cancel(); // No longer need to observe this image
                    infoflags |= ALLBITS;
                }
                return thisInnerObserver.imageUpdate(img, infoflags, x, y, width, height);
            }
            return false;
        }
    }
}
