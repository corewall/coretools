package org.corewall.graphics.driver;

import java.awt.Color;

/**
 * A gradient fill.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class GradientFill extends Fill {
	private final boolean horizontal;
	private final Color start, end;

	/**
	 * Create a new gradient fill.
	 * 
	 * @param start
	 *            the starting color.
	 * @param end
	 *            the ending color.
	 * @param horizontal
	 *            true if a horizontal gradient, false otherwise.
	 */
	public GradientFill(final Color start, final Color end, final boolean horizontal) {
		super(Style.GRADIENT);
		this.start = start;
		this.end = end;
		this.horizontal = horizontal;
	}

	/**
	 * Get the ending color.
	 * 
	 * @return the ending color.
	 */
	public Color getEnd() {
		return end;
	}

	/**
	 * Get the starting color.
	 * 
	 * @return the starting color.
	 */
	public Color getStart() {
		return start;
	}

	/**
	 * Get the horizontal flag.
	 * 
	 * @return the horizontal flag.
	 */
	public boolean isHorizontal() {
		return horizontal;
	}
}