package org.corewall.graphics;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.corewall.graphics.driver.Java2DDriver;


/**
 * A RasterGraphics renders graphics to an image.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RasterGraphics extends GraphicsContext {
	protected final BufferedImage image;

	/**
	 * Create a new RasterGraphics.
	 * 
	 * @param width
	 *            the width.
	 * @param height
	 *            the height.
	 * @param antialias
	 *            true if the graphics should be antialiased, false otherwise.
	 */
	public RasterGraphics(final int width, final int height, final boolean antialias) {
		this(width, height, antialias, Color.white);
	}

	/**
	 * Create a new RasterGraphics.
	 * 
	 * @param width
	 *            the width.
	 * @param height
	 *            the height.
	 * @param antialias
	 *            true if the graphics should be antialiased, false otherwise.
	 * @param color
	 *            the background color.
	 */
	public RasterGraphics(final int width, final int height, final boolean antialias, final Color color) {
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = image.createGraphics();
		g2d.setBackground(color);
		g2d.setPaint(color);
		g2d.fillRect(0, 0, width, height);
		if (antialias) {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		} else {
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		}
		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		driver = new Java2DDriver(g2d);
	}

	/**
	 * Gets the image the graphics was rendered to.
	 * 
	 * @return the image.
	 */
	public BufferedImage getImage() {
		return image;
	}
	
	/**
	 * Writes the graphics to the specified image file.
	 * 
	 * @param file
	 *            the image file.
	 * @throws IOException
	 *             thrown if any error occurs.
	 */
	public void write(File file) throws IOException {
		String format = file.getName().substring(file.getName().lastIndexOf('.') + 1);
		OutputStream out = null;
		try {
			out = new FileOutputStream(file);
			write(out, format);
		} finally {
			if (out != null) {
				out.close();
			}
		}
	}
	
	/**
	 * Writes the graphics to the specified output stream.
	 * 
	 * @param out
	 *            the output stream.
	 * @param format
	 *            the format.
	 * @throws IOException
	 *             thrown if any error occurs.
	 */
	public void write(OutputStream out, String format) throws IOException {
		ImageIO.write(image, format, out);
	}
}
