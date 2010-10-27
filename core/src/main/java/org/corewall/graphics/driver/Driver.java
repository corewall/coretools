package org.corewall.graphics.driver;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.net.URL;
import java.util.List;

/**
 * The interface for a 2D graphics driver.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Driver {
	/**
	 * Arc styles.
	 */
	public enum ArcStyle {
		/**
		 * Closed arc with a straight line from start point to end point.
		 */
		CLOSED,

		/**
		 * Open arc.
		 */
		OPEN,

		/**
		 * Closed arc with a straight line from start point to circle center to
		 * end point.
		 */
		SECTOR
	}

	/**
	 * A wrapper for images.
	 */
	static class Image {
		public URL url;
		public boolean isVertical;

		public Image(final URL url) {
			this.url = url;
			isVertical = true;
		}

		public Image(final URL url, final boolean isVertical) {
			this.url = url;
			this.isVertical = isVertical;
		}
	}

	/**
	 * Line styles.
	 */
	public enum LineStyle {
		/**
		 * Mixed dashed and dotted line.
		 */
		DASH_DOTTED,

		/**
		 * Dashed line.
		 */
		DASHED,

		/**
		 * Dotted line.
		 */
		DOTTED,

		/**
		 * Solid line.
		 */
		SOLID
	}

	/**
	 * Disposes any resources that need disposing.
	 */
	void dispose();

	/**
	 * Draws an arc.
	 * 
	 * @param bounds
	 *            the bounds of the arc.
	 * @param start
	 *            the starting angle.
	 * @param extent
	 *            the angle extent.
	 * @param style
	 *            the arc style.
	 */
	void drawArc(Rectangle2D bounds, double start, double extent, ArcStyle style);

	/**
	 * Draw the image at the specified point.
	 * 
	 * @param point
	 *            the point.
	 * @param image
	 *            the image.
	 */
	void drawImage(Point2D point, Image image);

	/**
	 * Draws the image in the specified rectangle.
	 * 
	 * @param rect
	 *            the rectangle.
	 * @param image
	 *            the image.
	 */
	void drawImage(Rectangle2D rect, Image image);

	/**
	 * Draws a line.
	 * 
	 * @param start
	 *            the start.
	 * @param end
	 *            the end.
	 */
	void drawLine(Point2D start, Point2D end);

	/**
	 * Draws an oval.
	 * 
	 * @param bounds
	 *            the bounds of the oval.
	 */
	void drawOval(Rectangle2D bounds);

	/**
	 * Draws a point.
	 * 
	 * @param point
	 *            the point to draw.
	 */
	void drawPoint(Point2D point);

	/**
	 * Draws a polygon.
	 * 
	 * @param points
	 *            the points.
	 */
	void drawPolygon(List<Point2D> points);

	/**
	 * Draws a rectangle.
	 * 
	 * @param rect
	 *            the rectangle.
	 */
	void drawRectangle(Rectangle2D rect);

	/**
	 * Draws a string.
	 * 
	 * @param point
	 *            the point,
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	void drawString(Point2D point, Font font, String string);

	/**
	 * Draws a vertical string.
	 * 
	 * @param point
	 *            the point,
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 */
	void drawVerticalString(Point2D point, Font font, String string);

	/**
	 * Fills an arc.
	 * 
	 * @param bounds
	 *            the bounds of the arc.
	 * @param start
	 *            the starting angle.
	 * @param extent
	 *            the angle extents.
	 * @param style
	 *            the arc style.
	 */
	void fillArc(Rectangle2D bounds, double start, double extent, ArcStyle style);

	/**
	 * Fills an oval.
	 * 
	 * @param bounds
	 *            the bounds of the oval.
	 */
	void fillOval(Rectangle2D bounds);

	/**
	 * Fills a polygon.
	 * 
	 * @param points
	 *            the points.
	 */
	void fillPolygon(List<Point2D> points);

	/**
	 * Fills a rectangle.
	 * 
	 * @param rect
	 *            the rectangle.
	 */
	void fillRectangle(Rectangle2D rect);

	/**
	 * Gets the clip rectangle.
	 * 
	 * @return the clip rectangle.
	 */
	Rectangle2D getClip();

	/**
	 * Gets the fill.
	 * 
	 * @return the fill.
	 */
	Fill getFill();

	/**
	 * Gets the line color.
	 * 
	 * @return the line color.
	 */
	Color getLineColor();

	/**
	 * Gets the line style.
	 * 
	 * @return the line style.
	 */
	LineStyle getLineStyle();

	/**
	 * Gets the line thickness.
	 * 
	 * @return the line thickness.
	 */
	int getLineThickness();

	/**
	 * Gets the bounds of the specified string in the specified font.
	 * 
	 * @param font
	 *            the font.
	 * @param string
	 *            the string.
	 * @return the bounds.
	 */
	Rectangle2D getStringBounds(Font font, String string);

	/**
	 * Gets the current affine transform.
	 * 
	 * @return the affine transform.
	 */
	AffineTransform getTransform();

	/**
	 * Pops the state of the driver, including the Fill and the line properties.
	 */
	void popState();

	/**
	 * Pop the top most recent affine transform from the transform stack.
	 */
	void popTransform();

	/**
	 * Saves the state of the driver, including the Fill and the line
	 * properties.
	 */
	void pushState();

	/**
	 * Push a new affine transform onto the transform stack. The stack follows
	 * the last-specified-first-applied rule. Calls to pushTransform() should be
	 * accompanied with a corresponding popTransform() call.
	 * 
	 * @param transform
	 *            the new transform.
	 */
	void pushTransform(AffineTransform transform);

	/**
	 * Sets the clip rectangle.
	 * 
	 * @param r
	 *            the clip rectangle.
	 */
	void setClip(Rectangle2D r);

	/**
	 * Sets the fill.
	 * 
	 * @param fill
	 *            the fill.
	 */
	void setFill(Fill fill);

	/**
	 * Sets the line color.
	 * 
	 * @param color
	 *            the line color.
	 */
	void setLineColor(Color color);

	/**
	 * Sets the line style.
	 * 
	 * @param style
	 *            the line style.
	 */
	void setLineStyle(LineStyle style);

	/**
	 * Sets the line thickness.
	 * 
	 * @param thickness
	 *            the thickness.
	 */
	void setLineThickness(int thickness);
}
