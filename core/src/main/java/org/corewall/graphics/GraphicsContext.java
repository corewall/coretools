package org.corewall.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import org.corewall.graphics.driver.ColorFill;
import org.corewall.graphics.driver.Driver;
import org.corewall.graphics.driver.Fill;
import org.corewall.graphics.driver.GradientFill;
import org.corewall.graphics.driver.MultiFill;
import org.corewall.graphics.driver.TextureFill;
import org.corewall.graphics.internal.ImageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;

/**
 * A simple 2D graphics API that can be mapped to Java2D, SWT, and other
 * graphics implementations.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class GraphicsContext implements Driver {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphicsContext.class);
	protected Driver driver = null;

	/**
	 * Create a new GraphicsContext.
	 */
	protected GraphicsContext() {
	}

	/**
	 * Create a new GraphicsContext.
	 * 
	 * @param driver
	 *            the driver.
	 */
	public GraphicsContext(final Driver driver) {
		this.driver = driver;
	}

	public void dispose() {
		driver.dispose();
	}

	/**
	 * Convenience method for drawing arcs.
	 * 
	 * @see Driver#drawArc(Rectangle2D, double, double, Driver.ArcStyle)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param start
	 *            the starting angle.
	 * @param extent
	 *            the angle extent.
	 * @param style
	 *            the arc style.
	 */
	public void drawArc(final double x, final double y, final double w, final double h, final double start, final double extent, final ArcStyle style) {
		drawArc(new Rectangle2D.Double(x, y, w, h), start, extent, style);
	}

	public void drawArc(final Rectangle2D bounds, final double start, final double extent, final ArcStyle style) {
		driver.drawArc(bounds, start, extent, style);
	}

	/**
	 * Convenience method for drawing images.
	 * 
	 * @see Driver#drawImage(Rectangle2D, Image)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param image
	 *            the image.
	 */
	public void drawImage(final double x, final double y, final double w, final double h, final URL image) {
		drawImage(new Rectangle2D.Double(x, y, w, h), image);
	}

	/**
	 * Convenience method for drawing images.
	 * 
	 * @see Driver#drawImage(Point2D, Image)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param image
	 *            the image.
	 */
	public void drawImage(final double x, final double y, final URL image) {
		drawImage(new Point2D.Double(x, y), image);
	}

	public void drawImage(final Point2D point, final Image image) {
		driver.drawImage(point, image);
	}

	/**
	 * Convenience to draw a vertical image.
	 * 
	 * @param point
	 *            the point.
	 * @param image
	 *            the image URL.
	 */
	public void drawImage(final Point2D point, final URL image) {
		if (image == null) {
			return;
		}
		driver.drawImage(point, new Image(image));
	}

	/**
	 * Convenience to draw an image.
	 * 
	 * @param point
	 *            the point.
	 * @param image
	 *            the image URL.
	 * @param isVertical
	 *            the vertical flag.
	 */
	public void drawImage(final Point2D point, final URL image, final boolean isVertical) {
		if (image == null) {
			return;
		}
		driver.drawImage(point, new Image(image, isVertical));
	}

	public void drawImage(final Rectangle2D rect, final Image image) {
		driver.drawImage(rect, image);
	}

	/**
	 * Convenience to draw an image.
	 * 
	 * @param rect
	 *            the rectangle.
	 * @param image
	 *            the image URL.
	 */
	public void drawImage(final Rectangle2D rect, final URL image) {
		if (image == null) {
			return;
		}
		driver.drawImage(rect, new Image(image));
	}

	/**
	 * Convenience to draw a vertical image.
	 * 
	 * @param rect
	 *            the rectangle.
	 * @param image
	 *            the image URL.
	 * @param isVertical
	 *            the vertical flag.
	 */
	public void drawImage(final Rectangle2D rect, final URL image, final boolean isVertical) {
		if (image == null) {
			return;
		}
		driver.drawImage(rect, new Image(image, isVertical));
	}

	/**
	 * Convenience method to draw an image center-aligned in a rectangle.
	 * 
	 * @see GraphicsContext#drawImageCenter(Rectangle2D, URL)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param image
	 *            the image.
	 */
	public void drawImageCenter(final double x, final double y, final double w, final double h, final URL image) {
		drawImageCenter(new Rectangle2D.Double(x, y, w, h), image);
	}

	/**
	 * Draws the image center-aligned in the specified rectangle.
	 * 
	 * @param rect
	 *            the rectangle.
	 * @param image
	 *            the image.
	 */
	public void drawImageCenter(final Rectangle2D rect, final URL image) {
		if (image == null) {
			return;
		}

		InputStream in = null;
		try {
			in = image.openStream();
			final ImageInfo ii = new ImageInfo();
			ii.setInput(in);
			if (ii.check()) {
				final double iar = (double) ii.getWidth() / (double) ii.getHeight();
				final double rar = rect.getWidth() / rect.getHeight();
				if (rar > iar) {
					drawImage(rect.getCenterX() - ii.getHeight() * iar / 2, rect.getCenterY() - (double) ii.getHeight() / 2, ii.getHeight() * iar, ii
							.getHeight(), image);
				} else {
					drawImage(rect.getCenterX() - (double) ii.getWidth() / 2, rect.getCenterY() - ii.getWidth() / iar / 2, ii.getWidth(), ii.getWidth() / iar,
							image);
				}
			}
		} catch (final IOException ioe) {
			LOGGER.warn("Unable to center-align image {}: {}", image, ioe.getMessage());
		} finally {
			Closeables.closeQuietly(in);
		}
	}

	/**
	 * Convenience method to draw an image left-aligned in a rectangle.
	 * 
	 * @see GraphicsContext#drawImageLeft(Rectangle2D, URL)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param image
	 *            the image.
	 */
	public void drawImageLeft(final double x, final double y, final double w, final double h, final URL image) {
		drawImageLeft(new Rectangle2D.Double(x, y, w, h), image);
	}

	/**
	 * Draws an image left-aligned in the specified rectangle.
	 * 
	 * @param rect
	 *            the rectangle.
	 * @param image
	 *            the image.
	 */
	public void drawImageLeft(final Rectangle2D rect, final URL image) {
		if (image == null) {
			return;
		}

		InputStream in = null;
		try {
			in = image.openStream();
			final ImageInfo ii = new ImageInfo();
			ii.setInput(in);
			if (ii.check()) {
				final double iar = (double) ii.getWidth() / (double) ii.getHeight();
				final double rar = rect.getWidth() / rect.getHeight();
				if (rar > iar) {
					drawImage(rect.getX(), rect.getY(), ii.getHeight() * iar, ii.getHeight(), image);
				} else {
					drawImage(rect.getX(), rect.getY(), ii.getWidth(), ii.getWidth() / iar, image);
				}
			}
		} catch (final IOException ioe) {
			LOGGER.warn("Unable to left-align image {}: {}", image, ioe.getMessage());
		} finally {
			Closeables.closeQuietly(in);
		}
	}

	/**
	 * Convenience method to draw an image right-aligned in a rectangle.
	 * 
	 * @see GraphicsContext#drawImageRight(Rectangle2D, URL)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param image
	 *            the image.
	 */
	public void drawImageRight(final double x, final double y, final double w, final double h, final URL image) {
		drawImageRight(new Rectangle2D.Double(x, y, w, h), image);
	}

	/**
	 * Draws an image right-aligned in the specified rectangle.
	 * 
	 * @param rect
	 *            the rectangle.
	 * @param image
	 *            the image.
	 */
	public void drawImageRight(final Rectangle2D rect, final URL image) {
		if (image == null) {
			return;
		}

		InputStream in = null;
		try {
			in = image.openStream();
			final ImageInfo ii = new ImageInfo();
			ii.setInput(in);
			if (ii.check()) {
				final double iar = (double) ii.getWidth() / (double) ii.getHeight();
				final double rar = rect.getWidth() / rect.getHeight();
				if (rar > iar) {
					drawImage(rect.getMaxX() - ii.getHeight() * iar, rect.getY(), ii.getHeight() * iar, ii.getHeight(), image);
				} else {
					drawImage(rect.getMaxX() - ii.getWidth(), rect.getY(), ii.getWidth(), ii.getWidth() / iar, image);
				}
			}
		} catch (final IOException ioe) {
			LOGGER.warn("Unable to right-align image {}: {}", image, ioe.getMessage());
		} finally {
			Closeables.closeQuietly(in);
		}
	}

	/**
	 * Convenience method for drawing lines.
	 * 
	 * @see Driver#drawLine(Point2D, Point2D)
	 * 
	 * @param x1
	 *            the starting x.
	 * @param y1
	 *            the starting y.
	 * @param x2
	 *            the ending x.
	 * @param y2
	 *            the ending y
	 */
	public void drawLine(final double x1, final double y1, final double x2, final double y2) {
		drawLine(new Point2D.Double(x1, y1), new Point2D.Double(x2, y2));
	}

	public void drawLine(final Point2D start, final Point2D end) {
		driver.drawLine(start, end);
	}

	/**
	 * Convenience method for drawing ovals.
	 * 
	 * @see Driver#drawOval(Rectangle2D)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 */
	public void drawOval(final double x, final double y, final double w, final double h) {
		drawOval(new Rectangle2D.Double(x, y, w, h));
	}

	public void drawOval(final Rectangle2D bounds) {
		driver.drawOval(bounds);
	}

	/**
	 * Convenience method for drawing points.
	 * 
	 * @see Driver#drawPoint(Point2D)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 */
	public void drawPoint(final double x, final double y) {
		drawPoint(new Point2D.Double(x, y));
	}

	public void drawPoint(final Point2D point) {
		driver.drawPoint(point);
	}

	public void drawPolygon(final List<Point2D> points) {
		driver.drawPolygon(points);
	}

	/**
	 * Convenience method for drawing rectangles.
	 * 
	 * @see Driver#drawRectangle(Rectangle2D)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 */
	public void drawRectangle(final double x, final double y, final double w, final double h) {
		drawRectangle(new Rectangle2D.Double(x, y, w, h));
	}

	public void drawRectangle(final Rectangle2D rect) {
		driver.drawRectangle(rect);
	}

	/**
	 * Convenience method for drawing a string.
	 * 
	 * @see Driver#drawString(Point2D, Font, String)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawString(final double x, final double y, final Font font, final String string) {
		drawString(new Point2D.Double(x, y), font, string);
	}

	public void drawString(final Point2D point, final Font font, final String string) {
		driver.drawString(point, font, string);
	}

	/**
	 * Convenience method to draw a string centered in a rectangle.
	 * 
	 * @see GraphicsContext#drawStringCenter(Rectangle2D, Font, String)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawStringCenter(final double x, final double y, final double w, final double h, final Font font, final String string) {
		drawStringCenter(new Rectangle2D.Double(x, y, w, h), font, string);
	}

	/**
	 * Draws a string centered in a rectangle.
	 * 
	 * @param bounds
	 *            the rectangle.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawStringCenter(final Rectangle2D bounds, final Font font, final String string) {
		final Rectangle2D sb = getStringBounds(font, string);
		drawString(bounds.getCenterX() - sb.getWidth() / 2, bounds.getCenterY() - (double) font.getSize() / 2, font, string);
	}

	/**
	 * Convenience method to draw a string left-aligned in a rectangle.
	 * 
	 * @see GraphicsContext#drawStringLeft(Rectangle2D, Font, String)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawStringLeft(final double x, final double y, final double w, final double h, final Font font, final String string) {
		drawStringLeft(new Rectangle2D.Double(x, y, w, h), font, string);
	}

	/**
	 * Draws a string left-aligned in a rectangle.
	 * 
	 * @param bounds
	 *            the rectangle.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawStringLeft(final Rectangle2D bounds, final Font font, final String string) {
		drawString(bounds.getMinX(), bounds.getCenterY() - (double) font.getSize() / 2, font, string);
	}

	/**
	 * Convenience method to draw a string right-aligned in a rectangle.
	 * 
	 * @see GraphicsContext#drawStringRight(Rectangle2D, Font, String)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param font
	 *            the font.
	 * @param string
	 *            string.
	 */
	public void drawStringRight(final double x, final double y, final double w, final double h, final Font font, final String string) {
		drawStringRight(new Rectangle2D.Double(x, y, w, h), font, string);
	}

	/**
	 * Draws a string right-aligned in a rectangle.
	 * 
	 * @param bounds
	 *            the rectangle.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawStringRight(final Rectangle2D bounds, final Font font, final String string) {
		final Rectangle2D sb = getStringBounds(font, string);
		drawString(bounds.getMaxX() - sb.getWidth(), bounds.getCenterY() - (double) font.getSize() / 2, font, string);
	}

	/**
	 * Convenience method to draw a vertical string.
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawVerticalString(final double x, final double y, final Font font, final String string) {
		driver.drawVerticalString(new Point2D.Double(x, y), font, string);
	}

	public void drawVerticalString(final Point2D point, final Font font, final String string) {
		driver.drawVerticalString(point, font, string);
	}

	/**
	 * Convenience method to draw a vertical string centered in a rectangle.
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawVerticalStringCenter(final double x, final double y, final double w, final double h, final Font font, final String string) {
		drawVerticalStringCenter(new Rectangle2D.Double(x, y, w, h), font, string);
	}

	/**
	 * Convenience method to draw a vertical string centered in a rectangle.
	 * 
	 * @param bounds
	 *            the rectangle.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawVerticalStringCenter(final Rectangle2D bounds, final Font font, final String string) {
		final Rectangle2D sb = getStringBounds(font, string);
		drawVerticalString(bounds.getCenterX() - (double) font.getSize() / 2, bounds.getCenterY() - sb.getWidth() / 2, font, string);
	}

	/**
	 * Convenience method to draw a vertical string left-aligned in a rectangle.
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawVerticalStringLeft(final double x, final double y, final double w, final double h, final Font font, final String string) {
		drawVerticalStringLeft(new Rectangle2D.Double(x, y, w, h), font, string);
	}

	/**
	 * Convenience method to draw a vertical string left-aligned in a rectangle.
	 * 
	 * @param bounds
	 *            the rectangle.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawVerticalStringLeft(final Rectangle2D bounds, final Font font, final String string) {
		drawVerticalString(bounds.getCenterX() - (double) font.getSize() / 2, bounds.getY(), font, string);
	}

	/**
	 * Convenience method to draw a vertical string right-aligned in a
	 * rectangle.
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawVerticalStringRight(final double x, final double y, final double w, final double h, final Font font, final String string) {
		drawVerticalStringRight(new Rectangle2D.Double(x, y, w, h), font, string);
	}

	/**
	 * Convenience method to draw a vertical string right-aligned in a
	 * rectangle.
	 * 
	 * @param bounds
	 *            the rectangle.
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	public void drawVerticalStringRight(final Rectangle2D bounds, final Font font, final String string) {
		final Rectangle2D sb = getStringBounds(font, string);
		drawVerticalString(bounds.getCenterX() - (double) font.getSize() / 2, bounds.getMaxY() - sb.getWidth(), font, string);
	}

	/**
	 * Convenience method for drawing filled arcs.
	 * 
	 * @see Driver#drawArc(Rectangle2D, double, double, Driver.ArcStyle)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 * @param start
	 *            the starting angle.
	 * @param extent
	 *            the angle extent.
	 * @param style
	 *            the arc style.
	 */
	public void fillArc(final double x, final double y, final double w, final double h, final double start, final double extent, final ArcStyle style) {
		fillArc(new Rectangle2D.Double(x, y, w, h), start, extent, style);
	}

	public void fillArc(final Rectangle2D bounds, final double start, final double extent, final ArcStyle style) {
		driver.fillArc(bounds, start, extent, style);
	}

	/**
	 * Convenience method for drawing filled ovals.
	 * 
	 * @see Driver#drawOval(Rectangle2D)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 */
	public void fillOval(final double x, final double y, final double w, final double h) {
		fillOval(new Rectangle2D.Double(x, y, w, h));
	}

	public void fillOval(final Rectangle2D bounds) {
		driver.fillOval(bounds);
	}

	public void fillPolygon(final List<Point2D> points) {
		driver.fillPolygon(points);
	}

	/**
	 * Convenience method for drawing rectangles.
	 * 
	 * @see Driver#drawRectangle(Rectangle2D)
	 * 
	 * @param x
	 *            the x.
	 * @param y
	 *            the y.
	 * @param w
	 *            the width.
	 * @param h
	 *            the height.
	 */
	public void fillRectangle(final double x, final double y, final double w, final double h) {
		fillRectangle(new Rectangle2D.Double(x, y, w, h));
	}

	public void fillRectangle(final Rectangle2D rect) {
		driver.fillRectangle(rect);
	}

	public Rectangle2D getClip() {
		return driver.getClip();
	}

	public Fill getFill() {
		return driver.getFill();
	}

	public Color getLineColor() {
		return driver.getLineColor();
	}

	public LineStyle getLineStyle() {
		return driver.getLineStyle();
	}

	public int getLineThickness() {
		return driver.getLineThickness();
	}

	public Rectangle2D getStringBounds(final Font font, final String string) {
		return driver.getStringBounds(font, string);
	}

	public AffineTransform getTransform() {
		return driver.getTransform();
	}

	public void popState() {
		driver.popState();
	}

	public void popTransform() {
		driver.popTransform();
	}

	public void pushState() {
		driver.pushState();
	}

	public void pushTransform(final AffineTransform transform) {
		driver.pushTransform(transform);
	}

	public void setClip(final Rectangle2D r) {
		driver.setClip(r);
	}

	/**
	 * Sets the driver for this GraphicsContext.
	 * 
	 * @param driver
	 *            the driver.
	 */
	public void setDriver(final Driver driver) {
		this.driver = driver;
	}

	/**
	 * Convenience method for setting the fill to a color.
	 * 
	 * @param color
	 *            the color.
	 */
	public void setFill(final Color color) {
		setFill(new ColorFill(color));
	}

	/**
	 * Convenience method for setting the fill to a gradient.
	 * 
	 * @param start
	 *            the starting color.
	 * @param end
	 *            the ending color.
	 * @param horizontal
	 *            true if horizontal, false if vertical.
	 */
	public void setFill(final Color start, final Color end, final boolean horizontal) {
		setFill(new GradientFill(start, end, horizontal));
	}

	/**
	 * Convenience method for setting the fill to a composite/multi fill.
	 * 
	 * @param fills
	 *            the fills.
	 */
	public void setFill(final Fill... fills) {
		setFill(new MultiFill(fills));
	}

	public void setFill(final Fill fill) {
		driver.setFill(fill);
	}

	/**
	 * Convenience method for setting the fill to a texture.
	 * 
	 * @param image
	 *            the texture.
	 */
	public void setFill(final URL image) {
		setFill(new TextureFill(image));
	}

	public void setLineColor(final Color color) {
		driver.setLineColor(color);
	}

	public void setLineStyle(final LineStyle style) {
		driver.setLineStyle(style);
	}

	public void setLineThickness(final int thickness) {
		driver.setLineThickness(thickness);
	}

	/**
	 * Sets the graphics state.
	 * 
	 * @param lineColor
	 *            the line color.
	 * @param thickness
	 *            the line thickness.
	 * @param style
	 *            the line style.
	 * @param background
	 *            the background color.
	 */
	public void setState(final Color lineColor, final int thickness, final LineStyle style, final Color background) {
		setLineColor(lineColor);
		setLineThickness(thickness);
		setLineStyle(style);
		setFill(background);
	}

	/**
	 * Sets the graphics state.
	 * 
	 * @param lineColor
	 *            the line color.
	 * @param thickness
	 *            the line thickness.
	 * @param style
	 *            the line style.
	 * @param fill
	 *            the fill.
	 */
	public void setState(final Color lineColor, final int thickness, final LineStyle style, final Fill fill) {
		setLineColor(lineColor);
		setLineThickness(thickness);
		setLineStyle(style);
		setFill(fill);
	}
}
