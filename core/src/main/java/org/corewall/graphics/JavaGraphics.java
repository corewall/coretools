package org.corewall.graphics;

import java.awt.Graphics2D;

import javax.swing.JComponent;

import org.corewall.graphics.driver.Java2DDriver;

/**
 * A JavaGraphics renders interactively to a Swing component.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class JavaGraphics extends GraphicsContext {

	/**
	 * Create a new JavaGraphics.
	 * 
	 * @param g2d
	 *            the Java Graphics2D object.
	 * @param interactive
	 *            the component to refresh.
	 */
	public JavaGraphics(final Graphics2D g2d, final JComponent interactive) {
		super(new Java2DDriver(g2d, false, interactive));
	}
}
