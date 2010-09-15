/*
 * Copyright (c) Josh Reed, 2009.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.corewall.graphics.driver;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.corewall.Platform;
import org.corewall.graphics.Paper;
import org.corewall.graphics.internal.ImageCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.DefaultFontMapper;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPatternPainter;
import com.lowagie.text.pdf.PdfShading;
import com.lowagie.text.pdf.PdfShadingPattern;
import com.lowagie.text.pdf.ShadingColor;

/**
 * An experimental PDF driver implemented directly on iText.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class PDFDriver implements Driver {
	protected class Arc extends Shape {
		Rectangle2D r;
		double start, extent;
		ArcStyle style;

		public Arc(final Rectangle2D r, final double start, final double extent, final ArcStyle style) {
			this.r = r;
			this.start = start;
			this.extent = extent;
			this.style = style;
		}

		@Override
		public Rectangle2D getBounds() {
			return r;
		}

		@Override
		public void outline(final PdfContentByte content) {
			switch (style) {
				case OPEN:
					outlineOpen(content);
					break;
				case CLOSED:
					outlineClosed(content);
					break;
				case SECTOR:
					outlineSector(content);
					break;
			}
		}

		protected void outlineClosed(final PdfContentByte content) {
			content.arc(x(r.getMinX()), y(r.getMinY()), x(r.getMaxX()), y(r.getMaxY()), (float) start, (float) extent);
			content.closePath();
		}

		protected void outlineOpen(final PdfContentByte content) {
			content.arc(x(r.getMinX()), y(r.getMinY()), x(r.getMaxX()), y(r.getMaxY()), (float) start, (float) extent);
		}

		protected void outlineSector(final PdfContentByte content) {
			content.arc(x(r.getMinX()), y(r.getMinY()), x(r.getMaxX()), y(r.getMaxY()), (float) start, (float) extent);
			content.lineTo(x(r.getCenterX()), y(r.getCenterY()));
			content.closePath();
		}
	}

	protected class Line extends Shape {
		Point2D p1, p2;
		Rectangle2D r;

		public Line(final Point2D p2, final Point2D p1) {
			this.p1 = p1;
			this.p2 = p2;

			double x = Math.min(p1.getX(), p2.getX());
			double y = Math.min(p1.getY(), p2.getY());
			double w = Math.max(p1.getX(), p2.getX()) - x;
			double h = Math.max(p1.getY(), p2.getY()) - y;
			r = new Rectangle2D.Double(x, y, w, h);
		}

		@Override
		public Rectangle2D getBounds() {
			return r;
		}

		@Override
		public void outline(final PdfContentByte content) {
			content.moveTo(x(p1.getX()), y(p1.getY()));
			content.lineTo(x(p2.getX()), y(p2.getY()));
		}
	}

	protected class Oval extends Shape {
		Rectangle2D r;

		public Oval(final Rectangle2D rect) {
			r = rect;
		}

		@Override
		public Rectangle2D getBounds() {
			return r;
		}

		@Override
		public void outline(final PdfContentByte content) {
			content.ellipse(x(r.getMinX()), y(r.getMinY()), x(r.getMaxX()), y(r.getMaxY()));
		}
	}

	protected class Polygon extends Shape {
		List<Point2D> points;
		Rectangle2D r;

		public Polygon(final List<Point2D> points) {
			this.points = points;

			double minX = Double.MAX_VALUE;
			double minY = Double.MAX_VALUE;
			double maxX = Double.MIN_VALUE;
			double maxY = Double.MIN_VALUE;
			for (Point2D p : points) {
				minX = Math.min(minX, p.getX());
				minY = Math.min(minY, p.getY());
				maxX = Math.max(maxX, p.getX());
				maxY = Math.max(minY, p.getY());
			}
			r = new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
		}

		@Override
		public Rectangle2D getBounds() {
			return r;
		}

		@Override
		public void outline(final PdfContentByte content) {
			if (points.size() > 1) {
				Point2D p = points.get(0);
				content.moveTo(x(p.getX()), y(p.getY()));
				for (int i = 1; i < points.size(); i++) {
					p = points.get(i);
					content.lineTo(x(p.getX()), y(p.getY()));
				}
				content.closePath();
			}
		}
	}

	protected class Rectangle extends Shape {
		Rectangle2D r;

		public Rectangle(final Rectangle2D rect) {
			r = rect;
		}

		@Override
		public Rectangle2D getBounds() {
			return r;
		}

		@Override
		public void outline(final PdfContentByte content) {
			content.rectangle(x(r.getMinX()), y(r.getMaxY()), (float) r.getWidth(), (float) r.getHeight());
		}
	}

	protected abstract class Shape {
		public abstract Rectangle2D getBounds();

		public abstract void outline(PdfContentByte content);
	}

	protected static class State {
		Fill fill = new ColorFill(Color.white);
		Color lineColor = Color.black;
		LineStyle lineStyle = LineStyle.SOLID;
		int lineThickness = 1;
	}

	protected static final float LINE_DASH[] = { 18, 9 };
	protected static final float LINE_DASH_DOT[] = { 9, 3, 3, 3 };
	protected static final float LINE_DOT[] = { 3, 3 };
	private static final Logger LOGGER = LoggerFactory.getLogger(PDFDriver.class);

	protected FontMapper fontMapper = new DefaultFontMapper();
	protected final PdfContentByte content;
	protected final Paper paper;
	protected Stack<AffineTransform> transformStack = new Stack<AffineTransform>();
	protected Stack<State> stateStack = new Stack<State>();
	protected final ImageCache imageCache;
	protected Rectangle2D clip = null;

	/**
	 * Create a new PDF driver.
	 * 
	 * @param content
	 *            the content.
	 * @param paper
	 *            the paper.
	 */
	public PDFDriver(final PdfContentByte content, final Paper paper) {
		this.content = content;
		this.paper = paper;
		stateStack.push(new State());
		imageCache = Platform.getService(ImageCache.class);

		AffineTransform tx = new AffineTransform(new double[] { 1, 0, 0, -1 });
		tx.translate(paper.getPrintableX(), -(paper.getPrintableHeight() + paper.getPrintableY()));
		transformStack.push(tx);
	}

	public void dispose() {
		if (clip != null) {
			content.restoreState();
		}
	}

	protected void draw(final Shape shape) {
		State state = stateStack.peek();
		content.setLineWidth(state.lineThickness);
		content.setColorStroke(state.lineColor);
		switch (state.lineStyle) {
			case SOLID:
				content.setLineDash(1);
				break;
			case DASHED:
				content.setLineDash(LINE_DASH, 0);
				break;
			case DOTTED:
				content.setLineDash(LINE_DOT, 0);
				break;
			case DASH_DOTTED:
				content.setLineDash(LINE_DASH_DOT, 0);
				break;
		}
		shape.outline(content);
		content.stroke();
	}

	public void drawArc(final Rectangle2D bounds, final double start, final double extent, final ArcStyle style) {
		draw(new Arc(bounds, start, extent, style));
	}

	public void drawImage(final Point2D point, final Driver.Image image) {
		Future<BufferedImage> future = imageCache.get(image.url, 0, image.isVertical, null);
		try {
			com.lowagie.text.Image i = com.lowagie.text.Image.getInstance(future.get(), Color.white);
			content.addImage(i, i.getWidth(), 0, 0, i.getHeight(), x(point.getX()), y(point.getY() + i.getHeight()), true);
		} catch (DocumentException e) {
			LOGGER.error("Unable to draw image", e);
		} catch (IOException e) {
			LOGGER.error("Unable to draw image", e);
		} catch (InterruptedException e) {
			LOGGER.error("Unable to draw image", e);
		} catch (ExecutionException e) {
			LOGGER.error("Unable to draw image", e);
		}
	}

	public void drawImage(final Rectangle2D rect, final Driver.Image image) {
		Future<BufferedImage> future = imageCache.get(image.url, new Dimension((int) rect.getWidth(), (int) rect.getHeight()), image.isVertical, null);
		try {
			com.lowagie.text.Image i = com.lowagie.text.Image.getInstance(future.get(), Color.white);
			content.addImage(i, (float) rect.getWidth(), 0, 0, (float) rect.getHeight(), x(rect.getX()), y(rect.getY() + rect.getHeight()), true);
		} catch (DocumentException e) {
			LOGGER.error("Unable to draw image", e);
		} catch (IOException e) {
			LOGGER.error("Unable to draw image", e);
		} catch (InterruptedException e) {
			LOGGER.error("Unable to draw image", e);
		} catch (ExecutionException e) {
			LOGGER.error("Unable to draw image", e);
		}
	}

	public void drawLine(final Point2D start, final Point2D end) {
		draw(new Line(start, end));
	}

	public void drawOval(final Rectangle2D bounds) {
		draw(new Oval(bounds));
	}

	public void drawPoint(final Point2D point) {
		double offset = stateStack.peek().lineThickness / 2.0;
		draw(new Line(new Point2D.Double(point.getX() - offset, point.getY()), new Point2D.Double(point.getX() + offset, point.getY())));
	}

	public void drawPolygon(final List<Point2D> points) {
		draw(new Polygon(points));
	}

	public void drawRectangle(final Rectangle2D rect) {
		draw(new Rectangle(rect));
	}

	public void drawString(final Point2D point, final Font font, final String string) {
		content.setFontAndSize(fontMapper.awtToPdf(font), font.getSize2D());
		Color color = stateStack.peek().lineColor;
		content.setColorFill(color);
		content.setColorStroke(color);
		content.beginText();
		content.showTextAligned(PdfContentByte.ALIGN_LEFT, string, x(point.getX()), y(point.getY() + font.getSize2D()), 0);
		content.endText();
	}

	public void drawVerticalString(final Point2D point, final Font font, final String string) {
		content.setFontAndSize(fontMapper.awtToPdf(font), font.getSize2D());
		Color color = stateStack.peek().lineColor;
		content.setColorFill(color);
		content.setColorStroke(color);
		content.beginText();
		for (int i = 0; i < string.length(); i++) {
			content.showTextAligned(PdfContentByte.ALIGN_LEFT, string.substring(i, i + 1), x(point.getX()), y(point.getY() + font.getSize2D() + i
					* font.getSize2D()), 0);
		}
		content.endText();
	}

	protected void fill(final Fill fill, final Shape shape) {
		switch (fill.getStyle()) {
			case COLOR:
				Color color = ((ColorFill) fill).getColor();
				content.setColorFill(color);
				content.fill();
				break;
			case GRADIENT:
				GradientFill gf = (GradientFill) fill;
				Rectangle2D r = shape.getBounds();
				PdfShading gradient;
				if (gf.isHorizontal()) {
					gradient = PdfShading.simpleAxial(content.getPdfWriter(), x(r.getMinX()), y(r.getCenterY()), x(r.getMaxX()), y(r.getCenterY()), gf
							.getStart(), gf.getEnd());
				} else {
					gradient = PdfShading.simpleAxial(content.getPdfWriter(), x(r.getCenterX()), y(r.getMinY()), x(r.getCenterX()), y(r.getMaxY()), gf
							.getStart(), gf.getEnd());
				}
				content.setColorFill(new ShadingColor(new PdfShadingPattern(gradient)));
				content.fill();
				break;
			case TEXTURE:
				TextureFill tf = (TextureFill) fill;
				Future<BufferedImage> future = imageCache.get(tf.getTexture(), 0, true, null);
				try {
					com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(future.get(), null);
					AffineTransform inverse = new AffineTransform();
					double[] mx = new double[6];
					inverse.getMatrix(mx);
					mx[3] = -1;
					mx[5] = content.getPdfDocument().bottom();

					image.setAbsolutePosition(0, 0);
					PdfPatternPainter pattern = content.createPattern(image.getWidth(), image.getHeight());
					pattern.setPatternMatrix((float) mx[0], (float) mx[1], (float) mx[2], (float) mx[3], (float) mx[4], (float) mx[5]);
					pattern.addImage(image);
					content.setPatternFill(pattern);
					content.fill();
				} catch (DocumentException e) {
					LOGGER.error("Unable to load image", e);
				} catch (IOException e) {
					LOGGER.error("Unable to load image", e);
				} catch (InterruptedException e) {
					LOGGER.error("Unable to load image", e);
				} catch (ExecutionException e) {
					LOGGER.error("Unable to load image", e);
				}
				break;
			case MULTI:
				final MultiFill m = (MultiFill) fill;
				for (final Fill f : m.getFills()) {
					shape.outline(content);
					fill(f, shape);
				}
		}
	}

	protected void fill(final Shape shape) {
		// now fill it
		Fill fill = stateStack.peek().fill;
		if (fill == null) {
			return;
		}

		// fill our shape
		shape.outline(content);
		fill(fill, shape);
	}

	public void fillArc(final Rectangle2D bounds, final double start, final double extent, final ArcStyle style) {
		fill(new Arc(bounds, start, extent, style));
	}

	public void fillOval(final Rectangle2D bounds) {
		fill(new Oval(bounds));
	}

	public void fillPolygon(final List<Point2D> points) {
		fill(new Polygon(points));
	}

	public void fillRectangle(final Rectangle2D rect) {
		fill(new Rectangle(rect));
	}

	public Rectangle2D getClip() {
		return clip;
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
		content.setFontAndSize(fontMapper.awtToPdf(font), font.getSize2D());
		float w = content.getEffectiveStringWidth(string, false);
		float h = font.getSize();
		return new Rectangle2D.Double(0, h, w, h);
	}

	public AffineTransform getTransform() {
		return new AffineTransform(transformStack.peek());
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
		if (transformStack.size() > 1) {
			transformStack.pop();
		}
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
		final AffineTransform tx = new AffineTransform(transformStack.peek());
		tx.concatenate(transform);
		transformStack.push(tx);
	}

	public void setClip(final Rectangle2D r) {
		if (clip != null) {
			content.restoreState();
		}
		clip = r;
		if (r != null) {
			content.saveState();
			content.rectangle(x(r.getMinX()), y(r.getMaxY()), (float) r.getWidth(), (float) r.getHeight());
			content.clip();
			content.newPath();
		}
	}

	public void setFill(final Fill fill) {
		stateStack.peek().fill = fill;
	}

	public void setLineColor(final Color lineColor) {
		stateStack.peek().lineColor = lineColor;
	}

	public void setLineStyle(final LineStyle lineStyle) {
		stateStack.peek().lineStyle = lineStyle;
	}

	public void setLineThickness(final int lineThickness) {
		stateStack.peek().lineThickness = lineThickness;
	}

	protected float x(final double x) {
		return (float) transformStack.peek().transform(new Point2D.Double(x, 0), null).getX();
	}

	protected float y(final double y) {
		return (float) transformStack.peek().transform(new Point2D.Double(0, y), null).getY();
	}
}
