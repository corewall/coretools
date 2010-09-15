package org.corewall.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.corewall.graphics.driver.ColorFill;
import org.corewall.graphics.driver.Driver;
import org.corewall.graphics.driver.Driver.ArcStyle;
import org.corewall.graphics.driver.Driver.LineStyle;
import org.corewall.graphics.driver.GradientFill;
import org.corewall.graphics.driver.MultiFill;
import org.corewall.graphics.driver.TextureFill;

/**
 * A harness to test out graphics {@link Driver}s.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class GraphicsHarness {

	/**
	 * Exercises the specified GraphicsContext.
	 * 
	 * @param graphics
	 *            the graphics context.
	 * @throws IOException
	 *             should never be thrown.
	 */
	public static void test(final GraphicsContext graphics) throws IOException {
		// point, line, rectangle
		graphics.drawPoint(350, 0);
		graphics.drawLine(0, 0, 300, 0);
		graphics.drawRectangle(400, 0, 50, 100);

		graphics.setLineThickness(5);
		graphics.drawPoint(350, 10);
		graphics.drawLine(0, 10, 300, 10);
		graphics.drawRectangle(410, 10, 50, 100);

		graphics.setLineColor(Color.blue);
		graphics.drawPoint(350, 20);
		graphics.drawLine(0, 20, 300, 20);
		graphics.drawRectangle(420, 20, 50, 100);

		graphics.setLineStyle(LineStyle.DASHED);
		graphics.setLineColor(Color.black);
		graphics.setLineThickness(1);
		graphics.drawPoint(350, 30);
		graphics.drawLine(0, 30, 300, 30);
		graphics.drawRectangle(430, 30, 50, 100);

		graphics.setLineStyle(LineStyle.DOTTED);
		graphics.drawPoint(350, 40);
		graphics.drawLine(0, 40, 300, 40);
		graphics.drawRectangle(440, 40, 50, 100);

		graphics.setLineStyle(LineStyle.DASH_DOTTED);
		graphics.drawPoint(350, 50);
		graphics.drawLine(0, 50, 300, 50);
		graphics.drawRectangle(450, 50, 50, 100);

		graphics.setLineStyle(LineStyle.SOLID);
		graphics.drawPoint(350, 60);
		graphics.drawLine(0, 60, 300, 60);
		graphics.drawRectangle(460, 60, 50, 100);

		// arc, circles, ovals
		graphics.drawArc(0, 100, 100, 50, 0, 30, ArcStyle.SECTOR);
		graphics.drawArc(0, 100, 100, 50, 35, 30, ArcStyle.OPEN);
		graphics.drawArc(0, 100, 100, 50, 70, 90, ArcStyle.CLOSED);

		graphics.setFill(Color.red);
		graphics.fillOval(150, 100, 100, 50);
		graphics.drawOval(150, 100, 100, 50);

		graphics.setLineStyle(LineStyle.DASHED);
		graphics.setLineColor(Color.blue);
		graphics.setLineThickness(3);
		graphics.drawOval(300, 100, 50, 50);

		// polygon
		graphics.setLineColor(Color.black);
		graphics.setLineStyle(LineStyle.SOLID);
		graphics.setLineThickness(1);
		List<Point2D> points = new ArrayList<Point2D>();
		points.add(new Point2D.Double(0, 200));
		points.add(new Point2D.Double(300, 200));
		points.add(new Point2D.Double(100, 220));
		points.add(new Point2D.Double(100, 230));
		points.add(new Point2D.Double(50, 240));
		points.add(new Point2D.Double(50, 250));
		points.add(new Point2D.Double(0, 250));
		graphics.drawPolygon(points);

		// text
		graphics.drawString(350, 250, new Font("SanSerif", Font.BOLD, 12), "drawString");

		Rectangle2D left = new Rectangle2D.Double(0, 300, 100, 50);
		graphics.drawRectangle(left);
		graphics.drawStringLeft(left, new Font("Serif", Font.ITALIC, 14), "Left");

		Rectangle2D center = new Rectangle2D.Double(150, 300, 100, 50);
		graphics.drawRectangle(center);
		graphics.drawStringCenter(center, new Font("Serif", Font.PLAIN, 14), "Center");

		Rectangle2D right = new Rectangle2D.Double(300, 300, 100, 50);
		graphics.drawRectangle(right);
		graphics.drawStringRight(right, new Font("Dialog", Font.PLAIN, 14), "Right");

		// images
		URL image = GraphicsHarness.class.getResource("/org/corewall/graphics/icon.png");

		graphics.drawImage(0, 400, image);

		Rectangle2D rect = new Rectangle2D.Double(300, 400, 50, 50);
		graphics.drawImage(rect, image);
		graphics.drawRectangle(rect);

		// fills and transforms and clipping
		graphics.setFill(Color.yellow);
		graphics.fillRectangle(0, 500, 50, 50);

		graphics.pushTransform(AffineTransform.getTranslateInstance(100, 100));
		graphics.setFill(new GradientFill(Color.red, Color.blue, false));
		graphics.fillRectangle(0, 400, 50, 50);
		graphics.popTransform();

		graphics.setFill(new GradientFill(Color.red, Color.blue, true));
		graphics.fillRectangle(175, 500, 50, 50);

		graphics.setClip(new Rectangle2D.Double(300, 500, 100, 100));
		graphics.setFill(new MultiFill(new ColorFill(Color.green), new TextureFill(image)));
		graphics.fillRectangle(250, 450, 200, 200);
		graphics.drawRectangle(250, 450, 200, 200);
		graphics.setClip(new Rectangle2D.Double(325, 525, 50, 50));
		graphics.setFill(Color.red);
		graphics.fillRectangle(250, 450, 200, 200);
		graphics.drawRectangle(250, 450, 200, 200);
		graphics.setClip(null);

		graphics.dispose();
	}

	/**
	 * Tests the graphics harness with the raster driver.
	 * 
	 * @param file
	 *            the file.
	 * @throws IOException
	 *             should never happen.
	 */
	public static void testRaster(final File file) throws IOException {
		Paper paper = Paper.getDefault();
		RasterGraphics raster = new RasterGraphics(paper.getWidth(), paper.getHeight(), true);
		raster.pushTransform(AffineTransform.getTranslateInstance(36, 36));
		test(raster);
		raster.write(file);
	}
}
