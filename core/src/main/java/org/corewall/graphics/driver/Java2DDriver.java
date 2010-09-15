package org.corewall.graphics.driver;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

import org.corewall.Locator;
import org.corewall.Platform;
import org.corewall.graphics.internal.ImageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of the Driver interface for Java2D.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Java2DDriver implements Driver {
	protected static class State {
		Fill fill = new ColorFill(Color.white);
		Color lineColor = Color.black;
		LineStyle lineStyle = LineStyle.SOLID;
		int lineThickness = 1;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Java2DDriver.class);
	private static final Rectangle ANCHOR = new Rectangle(0, 0, 32, 32);
	protected static final float LINE_DASH[] = { 18, 9 };
	protected static final float LINE_DASH_DOT[] = { 9, 3, 3, 3 };
	protected static final float LINE_DOT[] = { 3, 3 };

	protected Graphics2D g2d = null;
	protected final ImageCache imageCache;
	protected final Locator locator;
	protected Rectangle2D originalClip = null;
	protected AffineTransform originalTransform = null;
	protected boolean scaleStrokes = false;
	protected BasicStroke stroke = null;
	protected Stack<AffineTransform> transforms = new Stack<AffineTransform>();
	protected Stack<State> stateStack = new Stack<State>();
	protected JComponent interactive = null;
	protected BufferedImage imageError = null;
	protected BufferedImage imageLoading = null;

	/**
	 * Create a new Java2DDriver.
	 * 
	 * @param graphics
	 *            the Java2D graphics object.
	 */
	public Java2DDriver(final Graphics2D graphics) {
		this(graphics, false, null);
	}

	/**
	 * Create a new Java2DDriver.
	 * 
	 * @param graphics
	 *            the Java2D graphics object.
	 * @param scaleStrokes
	 *            true if stroke widths should be scaled, false otherwise.
	 * @param interactive
	 *            the component who the Graphics2D object belongs to, or null.
	 */
	public Java2DDriver(final Graphics2D graphics, final boolean scaleStrokes, final JComponent interactive) {
		g2d = graphics;
		this.scaleStrokes = scaleStrokes;
		originalTransform = g2d.getTransform();
		originalClip = g2d.getClipBounds();
		transforms.push(new AffineTransform());
		stateStack.push(new State());
		this.interactive = interactive;
		locator = Platform.getService(Locator.class);
		imageCache = Platform.getService(ImageCache.class);
	}

	private List<Paint> createPaints(final Fill fill, final Shape shape) {
		final List<Paint> paints = new ArrayList<Paint>();

		// if no fill, then just fill with white
		if (fill == null) {
			paints.add(Color.white);
			return paints;
		}

		// create paints based on fill style
		switch (fill.getStyle()) {
			case COLOR:
				final ColorFill c = (ColorFill) fill;
				paints.add(c.getColor());
				break;
			case GRADIENT:
				final GradientFill g = (GradientFill) fill;
				final Rectangle2D r = shape.getBounds2D();
				Point2D p1,
				p2;
				if (g.isHorizontal()) {
					p1 = new Point2D.Double(r.getMinX(), r.getCenterY());
					p2 = new Point2D.Double(r.getMaxX(), r.getCenterY());
				} else {
					p1 = new Point2D.Double(r.getCenterX(), r.getMinY());
					p2 = new Point2D.Double(r.getCenterX(), r.getMaxY());
				}
				paints.add(new GradientPaint(p1, g.getStart(), p2, g.getEnd()));
				break;
			case TEXTURE:
				final TextureFill t = (TextureFill) fill;
				BufferedImage iimage;
				try {
					iimage = imageCache.get(t.getTexture(), 0, true, interactive).get();
					if (iimage != null) {
						paints.add(new TexturePaint(iimage, new Rectangle(0, 0, iimage.getWidth(), iimage.getHeight())));
					} else {
						LOGGER.error("Unable to load texture {}", t.getTexture().toExternalForm());
					}
				} catch (InterruptedException e) {
					LOGGER.error("Unable to load texture {}: {}", t.getTexture().toExternalForm(), e.getMessage());
				} catch (ExecutionException e) {
					LOGGER.error("Unable to load texture {}: {}", t.getTexture().toExternalForm(), e.getMessage());
				}
				break;
			case MULTI:
				final MultiFill m = (MultiFill) fill;
				for (final Fill f : m.getFills()) {
					paints.addAll(createPaints(f, shape));
				}
		}
		return paints;
	}

	private Shape createPolygon(final List<Point2D> points) {
		final GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
		boolean first = true;
		for (final Point2D p : points) {
			if (first) {
				path.moveTo((float) p.getX(), (float) p.getY());
			} else {
				path.lineTo((float) p.getX(), (float) p.getY());
			}
			first = false;
		}
		path.closePath();
		return path;
	}

	private void createStroke() {
		State state = stateStack.peek();
		switch (state.lineStyle) {
			case SOLID:
				stroke = new BasicStroke(state.lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, state.lineThickness, null, 0);
				break;
			case DASHED:
				stroke = new BasicStroke(state.lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, state.lineThickness, LINE_DASH, 0);
				break;
			case DOTTED:
				stroke = new BasicStroke(state.lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, state.lineThickness, LINE_DOT, 0);
				break;
			case DASH_DOTTED:
				stroke = new BasicStroke(state.lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, state.lineThickness, LINE_DASH_DOT, 0);
				break;
			default:
				stroke = new BasicStroke(state.lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, state.lineThickness, null, 0);
				break;
		}
	}

	public void dispose() {
		if (g2d != null) {
			g2d.dispose();
			g2d = null;
		}
		interactive = null;
		stateStack.clear();
		transforms.clear();
		originalClip = null;
		originalTransform = null;
	}

	private void draw(final Shape s) {
		prepareDraw();
		if (scaleStrokes) {
			g2d.draw(s);
		} else {
			g2d.setTransform(originalTransform);
			g2d.draw(transforms.peek().createTransformedShape(s));
			g2d.transform(transforms.peek());
		}
	}

	public void drawArc(final Rectangle2D bounds, final double start, final double extent, final ArcStyle style) {
		draw(new Arc2D.Double(bounds, start, extent, getArcType(style)));
	}

	public void drawImage(final Point2D point, final Image image) {
		prepareDraw();
		Future<BufferedImage> future = imageCache.get(image.url, 0, image.isVertical, interactive);
		if ((interactive == null) || future.isDone()) {
			try {
				BufferedImage bi = future.get();
				if (bi != null) {
					g2d.drawImage(bi, (int) point.getX(), (int) point.getY(), null);
				} else {
					drawImageError(new Rectangle2D.Double(point.getX(), point.getY(), ANCHOR.getWidth(), ANCHOR.getHeight()), image.url);
				}
			} catch (InterruptedException e) {
				LOGGER.error("drawImage() error", e);
				drawImageError(new Rectangle2D.Double(point.getX(), point.getY(), ANCHOR.getWidth(), ANCHOR.getHeight()), image.url);
			} catch (ExecutionException e) {
				LOGGER.error("drawImage() error", e);
				drawImageError(new Rectangle2D.Double(point.getX(), point.getY(), ANCHOR.getWidth(), ANCHOR.getHeight()), image.url);
			}
		} else {
			drawImageLoading(new Rectangle2D.Double(point.getX(), point.getY(), ANCHOR.getWidth(), ANCHOR.getHeight()), image.url);
		}
	}

	public void drawImage(final Rectangle2D rect, final Image image) {
		prepareDraw();
		Future<BufferedImage> future = imageCache.get(image.url, new Dimension((int) rect.getWidth(), (int) rect.getHeight()), image.isVertical, interactive);
		if ((interactive == null) || future.isDone()) {
			try {
				BufferedImage bi = future.get();
				if (bi != null) {
					g2d.drawImage(bi, (int) rect.getX(), (int) rect.getY(), (int) rect.getWidth(), (int) rect.getHeight(), null);
				} else {
					drawImageError(rect, image.url);
				}
			} catch (InterruptedException e) {
				LOGGER.error("drawImage() error", e);
				drawImageError(rect, image.url);
			} catch (ExecutionException e) {
				LOGGER.error("drawImage() error", e);
				drawImageError(rect, image.url);
			}
		} else {
			drawImageLoading(rect, image.url);
		}
	}

	protected void drawImageError(final Rectangle2D r, final URL orig) {
		if (imageError == null) {
			try {
				imageError = ImageIO.read(locator.getResource("rsrc:org/corewall/graphics/driver/error.png"));
			} catch (IOException e) {
				LOGGER.error("Unable to load 'error.png'");
			}
		}
		if (imageError != null) {
			g2d.setPaint(new TexturePaint(imageError, ANCHOR));
			g2d.fill(r);
		}
	}

	protected void drawImageLoading(final Rectangle2D r, final URL url) {
		// try a placeholder image first
		Future<BufferedImage> placeholder = imageCache.getClosest(url, new Dimension((int) r.getWidth(), (int) r.getHeight()));
		if (placeholder == null) {
			internalDrawImageLoading(r);
		} else {
			try {
				BufferedImage image = placeholder.get();
				if (image == null) {
					internalDrawImageLoading(r);
				} else {
					g2d.drawImage(image, (int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight(), null);
				}
			} catch (InterruptedException e) {
				LOGGER.error("drawImageLoading() error", e);
				internalDrawImageLoading(r);
			} catch (ExecutionException e) {
				LOGGER.error("drawImageLoading() error", e);
				internalDrawImageLoading(r);
			}
		}
	}

	public void drawLine(final Point2D start, final Point2D end) {
		draw(new Line2D.Double(start, end));
	}

	public void drawOval(final Rectangle2D bounds) {
		draw(new Ellipse2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()));
	}

	public void drawPoint(final Point2D point) {
		prepareDraw();
		draw(new Line2D.Double(point, point));
	}

	public void drawPolygon(final List<Point2D> points) {
		draw(createPolygon(points));
	}

	public void drawRectangle(final Rectangle2D rect) {
		draw(rect);
	}

	public void drawString(final Point2D point, final Font font, final String string) {
		prepareDraw();
		g2d.setFont(font);
		g2d.drawString(string, (int) point.getX(), (int) point.getY() + font.getSize());
	}

	public void drawVerticalString(final Point2D point, final Font font, final String string) {
		prepareDraw();
		g2d.setFont(font);
		AffineTransform undoRot = AffineTransform.getRotateInstance(Math.PI / 2);
		undoRot.translate(0, -(2 * point.getX() + font.getSize()));
		pushTransform(undoRot);
		g2d.drawString(string, (int) point.getY(), (int) point.getX() + font.getSize());
		popTransform();
	}

	private void fill(final Shape s) {
		for (final Paint p : createPaints(stateStack.peek().fill, s)) {
			g2d.setPaint(p);
			g2d.fill(s);
		}
	}

	public void fillArc(final Rectangle2D bounds, final double start, final double extent, final ArcStyle style) {
		fill(new Arc2D.Double(bounds, start, extent, getArcType(style)));
	}

	public void fillOval(final Rectangle2D bounds) {
		fill(new Ellipse2D.Double(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight()));
	}

	public void fillPolygon(final List<Point2D> points) {
		fill(createPolygon(points));
	}

	public void fillRectangle(final Rectangle2D rect) {
		fill(rect);
	}

	private int getArcType(final ArcStyle style) {
		switch (style) {
			case OPEN:
				return Arc2D.OPEN;
			case CLOSED:
				return Arc2D.CHORD;
			case SECTOR:
				return Arc2D.PIE;
			default:
				return -1;
		}
	}

	public Rectangle2D getClip() {
		return g2d.getClipBounds();
	}

	public Fill getFill() {
		return stateStack.peek().fill;
	}

	public Color getLineColor() {
		return stateStack.peek().lineColor;
	}

	public LineStyle getLineStyle() {
		return stateStack.peek().lineStyle;
	}

	public int getLineThickness() {
		return stateStack.peek().lineThickness;
	}

	public Rectangle2D getStringBounds(final Font font, final String string) {
		final FontMetrics metrics = g2d.getFontMetrics(font);
		return metrics.getStringBounds(string, g2d);
	}

	public AffineTransform getTransform() {
		return new AffineTransform(transforms.peek());
	}

	protected void internalDrawImageLoading(final Rectangle2D r) {
		if (imageLoading == null) {
			try {
				imageLoading = ImageIO.read(locator.getResource("rsrc:org/corewall/graphics/driver/loading.png"));
			} catch (IOException e) {
				LOGGER.error("Unable to load 'loading.png'");
			}
		}
		if (imageLoading != null) {
			g2d.setPaint(new TexturePaint(imageLoading, ANCHOR));
			g2d.fill(r);
		}
	}

	public void popState() {
		if (!stateStack.isEmpty()) {
			State state = stateStack.pop();
			setFill(state.fill);
			setLineColor(state.lineColor);
			setLineStyle(state.lineStyle);
			setLineThickness(state.lineThickness);
		}
	}

	public void popTransform() {
		if (!transforms.isEmpty()) {
			transforms.pop();
			g2d.setTransform(originalTransform);
			if (!transforms.isEmpty()) {
				g2d.transform(transforms.peek());
			}
		}
	}

	private void prepareDraw() {
		if (stroke == null) {
			createStroke();
		}
		g2d.setStroke(stroke);
		g2d.setPaint(stateStack.peek().lineColor);
	}

	public void pushState() {
		State state = new State();
		state.fill = getFill();
		state.lineColor = getLineColor();
		state.lineStyle = getLineStyle();
		state.lineThickness = getLineThickness();
		stateStack.push(state);
	}

	public void pushTransform(final AffineTransform transform) {
		final AffineTransform tx = new AffineTransform(transforms.peek());
		tx.concatenate(transform);
		transforms.push(tx);
		g2d.transform(transform);
	}

	public void setClip(final Rectangle2D r) {
		if (r == null) {
			g2d.setTransform(originalTransform);
			g2d.setClip(originalClip);
			g2d.transform(transforms.peek());
		} else if (originalClip != null) {
			g2d.setTransform(originalTransform);
			g2d.setClip(transforms.peek().createTransformedShape(r).getBounds2D().createIntersection(originalClip));
			g2d.transform(transforms.peek());
		} else {
			g2d.setClip(r);
		}
	}

	public void setFill(final Fill fill) {
		stateStack.peek().fill = fill;
	}

	public void setLineColor(final Color color) {
		stateStack.peek().lineColor = color;
		stroke = null;
	}

	public void setLineStyle(final LineStyle style) {
		stateStack.peek().lineStyle = style;
		stroke = null;
	}

	public void setLineThickness(final int thickness) {
		stateStack.peek().lineThickness = thickness;
		stroke = null;
	}
}
