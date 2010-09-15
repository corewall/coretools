package org.corewall.graphics.internal;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import org.corewall.graphics.driver.ColorFill;
import org.corewall.graphics.driver.Driver;
import org.corewall.graphics.driver.Fill;
import org.corewall.graphics.driver.GradientFill;
import org.corewall.graphics.driver.MultiFill;
import org.corewall.graphics.driver.TextureFill;

/**
 * A graphics driver that generates Javascript for the HTML canvas tag.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class HTMLCanvasDriver implements Driver {
	/**
	 * This implementation is based on EllipticalArc.java by Luc Maisonobe at
	 * <http://www.spaceroots.org/documents/ellipse/EllipticalArc.java>.
	 */
	protected class Arc extends Shape {
		private final double[][][] coeffs3Low = new double[][][] {
				{ { 3.85268, -21.229, -0.330434, 0.0127842 }, { -1.61486, 0.706564, 0.225945, 0.263682 }, { -0.910164, 0.388383, 0.00551445, 0.00671814 },
						{ -0.630184, 0.192402, 0.0098871, 0.0102527 } },
				{ { -0.162211, 9.94329, 0.13723, 0.0124084 }, { -0.253135, 0.00187735, 0.0230286, 0.01264 }, { -0.0695069, -0.0437594, 0.0120636, 0.0163087 },
						{ -0.0328856, -0.00926032, -0.00173573, 0.00527385 } } };
		private final double[][][] coeffs3High = new double[][][] {
				{ { 0.0899116, -19.2349, -4.11711, 0.183362 }, { 0.138148, -1.45804, 1.32044, 1.38474 }, { 0.230903, -0.450262, 0.219963, 0.414038 },
						{ 0.0590565, -0.101062, 0.0430592, 0.0204699 } },
				{ { 0.0164649, 9.89394, 0.0919496, 0.00760802 }, { 0.0191603, -0.0322058, 0.0134667, -0.0825018 },
						{ 0.0156192, -0.017535, 0.00326508, -0.228157 }, { -0.0236752, 0.0405821, -0.0173086, 0.176187 } } };
		private final double[] safety3 = new double[] { 0.001, 4.98, 0.207, 0.0067 };

		Rectangle2D r;
		double start, extent;
		ArcStyle style;

		public Arc(final Rectangle2D r, final double start, final double extent, final ArcStyle style) {
			this.r = r;
			this.start = start;
			this.extent = extent;
			this.style = style;
		}

		public int estimateError(final int i, final double etaA, final double etaB) {
			double eta = 0.5 * (etaA + etaB);
			double a = r.getWidth() / 2;
			double b = r.getHeight() / 2;
			double x = b / a;
			double dEta = etaB - etaA;
			double cos2 = Math.cos(2 * eta);
			double cos4 = Math.cos(4 * eta);
			double cos6 = Math.cos(6 * eta);

			// select the right coefficients set according to degree and b/a
			double[][][] coeffs = (x < 0.25) ? coeffs3Low : coeffs3High;
			double[] safety = safety3;

			double c0 = rationalFunction(x, coeffs[0][0]) + cos2 * rationalFunction(x, coeffs[0][1]) + cos4 * rationalFunction(x, coeffs[0][2]) + cos6
					* rationalFunction(x, coeffs[0][3]);

			double c1 = rationalFunction(x, coeffs[1][0]) + cos2 * rationalFunction(x, coeffs[1][1]) + cos4 * rationalFunction(x, coeffs[1][2]) + cos6
					* rationalFunction(x, coeffs[1][3]);

			return (int) (rationalFunction(x, safety) * a * Math.exp(c0 + c1 * dEta));
		}

		@Override
		public Rectangle2D getBounds() {
			return r;
		}

		@Override
		protected void path(final boolean fill) {
			double eta1 = Math.toRadians(start);
			double eta2 = Math.toRadians(start + extent);

			// find the number of curves needed
			boolean found = false;
			int n = 1;
			while ((!found) && (n < 1024)) {
				double dEta = (eta2 - eta1) / n;
				if (dEta <= 0.5 * Math.PI) {
					double etaB = eta1;
					found = true;
					for (int i = 0; found && (i < n); ++i) {
						double etaA = etaB;
						etaB += dEta;
						found = (estimateError(2, etaA, etaB) <= 0.5);
					}
				}
				n = n << 1;
			}

			double dEta = (eta2 - eta1) / n;
			double etaB = eta1;

			double a = r.getWidth() / 2;
			double b = r.getHeight() / 2;
			double cx = r.getCenterX();
			double cy = r.getCenterY();
			double cosEtaB = Math.cos(etaB);
			double sinEtaB = Math.sin(etaB);
			double aCosEtaB = a * cosEtaB;
			double bSinEtaB = b * sinEtaB;
			double aSinEtaB = a * sinEtaB;
			double bCosEtaB = b * cosEtaB;
			double xB = cx + aCosEtaB * 1 - bSinEtaB * 0;
			double yB = cy + aCosEtaB * 0 + bSinEtaB * 1;
			double xBDot = -aSinEtaB * 1 - bCosEtaB * 0;
			double yBDot = -aSinEtaB * 0 + bCosEtaB * 1;

			double t = Math.tan(0.5 * dEta);
			double alpha = Math.sin(dEta) * (Math.sqrt(4 + 3 * t * t) - 1) / 3;

			canvasFunc("beginPath");
			canvasFunc("moveTo", xB, yB);

			for (int i = 0; i < n; ++i) {
				double xA = xB;
				double yA = yB;
				double xADot = xBDot;
				double yADot = yBDot;

				etaB += dEta;
				cosEtaB = Math.cos(etaB);
				sinEtaB = Math.sin(etaB);
				aCosEtaB = a * cosEtaB;
				bSinEtaB = b * sinEtaB;
				aSinEtaB = a * sinEtaB;
				bCosEtaB = b * cosEtaB;
				xB = cx + aCosEtaB * 1 - bSinEtaB * 0;
				yB = cy + aCosEtaB * 0 + bSinEtaB * 1;
				xBDot = -aSinEtaB * 1 - bCosEtaB * 0;
				yBDot = -aSinEtaB * 0 + bCosEtaB * 1;

				canvasFunc("bezierCurveTo", (xA + alpha * xADot), (yA + alpha * yADot), (xB - alpha * xBDot), (yB - alpha * yBDot), xB, yB);
			}

			if (style == ArcStyle.SECTOR) {
				canvasFunc("lineTo", cx, cy);
			}
			if ((style == ArcStyle.SECTOR) || (style == ArcStyle.CLOSED)) {
				canvasFunc("closePath");
			}

			canvasFunc((fill ? "fill" : "stroke"));
		}

		private double rationalFunction(final double x, final double[] c) {
			return (x * (x * c[0] + c[1]) + c[2]) / (x + c[3]);
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
		protected void path(final boolean fill) {
			canvasFunc("beginPath");
			canvasFunc("moveTo", p1.getX(), p1.getY());
			canvasFunc("lineTo", p2.getX(), p2.getY());
			canvasFunc((fill ? "fill" : "stroke"));
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
		public void path(final boolean fill) {
			if (points.size() > 1) {
				canvasFunc("beginPath");
				Point2D p = points.get(0);
				canvasFunc("moveTo", p.getX(), p.getY());
				for (int i = 1; i < points.size(); i++) {
					p = points.get(i);
					canvasFunc("lineTo", p.getX(), p.getY());
				}
				canvasFunc("closePath");
				canvasFunc((fill ? "fill" : "stroke"));
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
		public void path(final boolean fill) {
			canvasFunc((fill ? "fillRect" : "strokeRect"), r.getX(), r.getY(), r.getWidth(), r.getHeight());
		}
	}

	protected abstract class Shape {
		public void draw() {
			path(false);
		}

		public void fill() {
			path(true);
		}

		public abstract Rectangle2D getBounds();

		protected abstract void path(boolean fill);
	}

	protected static class State {
		Fill fill = new ColorFill(Color.white);
		Color lineColor = Color.black;
		LineStyle lineStyle = LineStyle.SOLID;
		int lineThickness = 1;
	}

	private static final NumberFormat DEC = new DecimalFormat("0.####");

	protected final String functionName;
	protected final String contextVar;
	protected Stack<AffineTransform> transformStack = new Stack<AffineTransform>();
	protected Stack<State> stateStack = new Stack<State>();
	protected StringBuilder script;
	protected boolean strokeChanged = false;
	protected boolean fillChanged = false;
	protected AtomicInteger tempId = new AtomicInteger(0);

	/**
	 * Create a new CanvasDriver with the specified function and context
	 * variable names.
	 * 
	 * @param functionName
	 *            the function to wrap the script in.
	 * @param contextVar
	 *            the context variable.
	 */
	public HTMLCanvasDriver(final String functionName, final String contextVar) {
		this.functionName = functionName;
		this.contextVar = contextVar;
		this.script = new StringBuilder();
		transformStack.push(new AffineTransform());
		stateStack.push(new State());
	}

	protected void canvasFunc(final String name, final Object... args) {
		script.append(contextVar + "." + name + "(");
		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				script.append(",");
			}
			Object v = args[i];
			if (v instanceof String) {
				String str = v.toString();
				script.append("'" + str.replaceAll("'", "\\'") + "'");
			} else if (v instanceof Number) {
				script.append(DEC.format(v));
			} else {
				script.append(v);
			}
		}
		script.append(");\n");
	}

	protected void canvasProp(final String name, final Object value) {
		if (value instanceof String) {
			String str = value.toString();
			script.append(contextVar + "." + name + "='" + str.replaceAll("'", "\\'") + "';\n");
		} else if (value instanceof Number) {
			script.append(contextVar + "." + name + "=" + DEC.format(value) + ";\n");
		} else {
			script.append(contextVar + "." + name + "=" + value + ";\n");
		}
	}

	protected String colorToStyle(final Color color) {
		return "rgba(" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "," + ((color.getAlpha()) / 255.0) + ")";
	}

	public void dispose() {
		// do nothing
	}

	protected void draw(final Shape shape) {
		if (strokeChanged) {
			strokeChanged = false;
			canvasProp("strokeStyle", colorToStyle(stateStack.peek().lineColor));
			canvasProp("lineWidth", stateStack.peek().lineThickness);
			// TODO add support for LineStyle
		}
		shape.draw();
	}

	public void drawArc(final Rectangle2D bounds, final double start, final double extent, final ArcStyle style) {
		draw(new Arc(bounds, 360 - (start + extent), extent, style));
	}

	public void drawImage(final Point2D point, final Image image) {
		int id = tempId.incrementAndGet();
		script.append("var i" + id + "=new Image();\n");
		script.append("i" + id + ".onload = function(){\n");
		script.append(contextVar + ".drawImage(this," + point.getX() + "," + point.getY() + ");\n");
		script.append("};\n");
		script.append("i" + id + ".src='" + image.url.toExternalForm() + "';\n");
	}

	public void drawImage(final Rectangle2D rect, final Image image) {
		int id = tempId.incrementAndGet();
		script.append("var i" + id + "=new Image();\n");
		script.append("i" + id + ".onload = function(){\n");
		script.append(contextVar + ".drawImage(this," + rect.getX() + "," + rect.getY() + "," + rect.getWidth() + "," + rect.getHeight() + ");\n");
		script.append("};\n");
		script.append("i" + id + ".src='" + image.url.toExternalForm() + "';\n");
	}

	public void drawLine(final Point2D start, final Point2D end) {
		draw(new Line(start, end));
	}

	public void drawOval(final Rectangle2D bounds) {
		draw(new Arc(bounds, 0, 360, ArcStyle.CLOSED));
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
		// TODO Auto-generated method stub
	}

	public void drawVerticalString(final Point2D point, final Font font, final String string) {
		// TODO Auto-generated method stub
	}

	protected void fill(final Shape shape, final Fill fill) {
		Rectangle2D r = shape.getBounds();
		if (fill == null) {
			canvasProp("fillStyle", colorToStyle(Color.white));
			shape.fill();
		} else if (fill.getStyle() == Fill.Style.COLOR) {
			ColorFill c = (ColorFill) fill;
			canvasProp("fillStyle", colorToStyle(c.getColor()));
			shape.fill();
		} else if (fill.getStyle() == Fill.Style.GRADIENT) {
			GradientFill g = (GradientFill) fill;
			int id = tempId.incrementAndGet();
			script.append("var g" + id + "=");
			if (g.isHorizontal()) {
				canvasFunc("createLinearGradient", r.getX(), r.getCenterY(), r.getMaxX(), r.getCenterY());
			} else {
				canvasFunc("createLinearGradient", r.getCenterX(), r.getY(), r.getCenterX(), r.getMaxY());
			}
			script.append("g" + id + ".addColorStop(0,'" + colorToStyle(g.getStart()) + "');\n");
			script.append("g" + id + ".addColorStop(1,'" + colorToStyle(g.getEnd()) + "');\n");
			script.append(contextVar + ".fillStyle=g" + id + ";\n");
			shape.fill();
		} else if (fill.getStyle() == Fill.Style.TEXTURE) {
			TextureFill t = (TextureFill) fill;
			int id = tempId.incrementAndGet();
			script.append("var i" + id + "=new Image();\n");
			script.append("i" + id + ".onload = function(){\n");
			script.append(contextVar + ".fillStyle=" + contextVar + ".createPattern(this,'repeat');\n");
			shape.fill();
			script.append("}\n");
			script.append("i" + id + ".src='" + t.getTexture().toExternalForm() + "';\n");
		} else if (fill.getStyle() == Fill.Style.MULTI) {
			MultiFill m = (MultiFill) fill;
			for (Fill f : m.getFills()) {
				fill(shape, f);
			}
		}
	}

	public void fillArc(final Rectangle2D bounds, final double start, final double extent, final ArcStyle style) {
		fill(new Arc(bounds, 360 - (start + extent), extent, style), stateStack.peek().fill);
	}

	public void fillOval(final Rectangle2D bounds) {
		fill(new Arc(bounds, 0, 360, ArcStyle.CLOSED), stateStack.peek().fill);
	}

	public void fillPolygon(final List<Point2D> points) {
		fill(new Polygon(points), stateStack.peek().fill);
	}

	public void fillRectangle(final Rectangle2D rect) {
		fill(new Rectangle(rect), stateStack.peek().fill);
	}

	public Rectangle2D getClip() {
		// TODO
		return null;
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

	/**
	 * Gets the drawing script.
	 * 
	 * @return the script.
	 */
	public String getScript() {
		if (functionName != null) {
			return "function " + functionName + "(){\n" + script.toString() + "}\n";
		} else {
			return script.toString();
		}
	}

	public Rectangle2D getStringBounds(final Font font, final String string) {
		return new Rectangle2D.Double(0, 0, font.getSize() * string.length(), font.getSize());
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
		if (!transformStack.isEmpty()) {
			double[] matrix = new double[6];
			transformStack.pop().getMatrix(matrix);
			canvasFunc("setTransform", matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
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
		double[] matrix = new double[6];
		tx.getMatrix(matrix);
		canvasFunc("setTransform", matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
	}

	/**
	 * Resets this driver.
	 */
	public void reset() {
		script = new StringBuilder();
	}

	public void setClip(final Rectangle2D r) {
		// TODO
	}

	public void setFill(final Fill fill) {
		stateStack.peek().fill = fill;
		fillChanged = true;
	}

	public void setLineColor(final Color color) {
		stateStack.peek().lineColor = color;
		strokeChanged = true;
	}

	public void setLineStyle(final LineStyle style) {
		stateStack.peek().lineStyle = style;
		strokeChanged = true;
	}

	public void setLineThickness(final int thickness) {
		stateStack.peek().lineThickness = thickness;
		strokeChanged = true;
	}
}
