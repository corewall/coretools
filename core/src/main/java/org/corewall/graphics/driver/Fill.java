package org.corewall.graphics.driver;

/**
 * The fill class.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class Fill {

	/**
	 * The fill style.
	 */
	public enum Style {
		/**
		 * Background color.
		 */
		COLOR,

		/**
		 * Color gradient.
		 */
		GRADIENT,

		/**
		 * Multiple fills.
		 */
		MULTI,

		/**
		 * Image.
		 */
		TEXTURE
	}

	protected final Style style;

	/**
	 * Create a new fill with the specified style.
	 * 
	 * @param style
	 *            the style.
	 */
	public Fill(final Style style) {
		this.style = style;
	}

	/**
	 * Gets the fill style.
	 * 
	 * @return the style.
	 */
	public Style getStyle() {
		return style;
	}
}
