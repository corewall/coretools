package org.corewall.graphics.driver;

import java.awt.Color;

/**
 * A color fill.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ColorFill extends Fill {
	private final Color color;

	/**
	 * Create a new color fill.
	 * 
	 * @param color
	 *            the color.
	 */
	public ColorFill(final Color color) {
		super(Style.COLOR);
		this.color = color;
	}

	/**
	 * Gets the color.
	 * 
	 * @return the color.
	 */
	public Color getColor() {
		return color;
	}
}