package org.corewall.graphics.driver;

import java.net.URL;

/**
 * A texture fill.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class TextureFill extends Fill {
	private final URL image;

	/**
	 * Create a new texture fill.
	 * 
	 * @param image
	 *            the texture.
	 */
	public TextureFill(final URL image) {
		super(Style.TEXTURE);
		this.image = image;
	}

	/**
	 * Gets the texture.
	 * 
	 * @return the texture.
	 */
	public URL getTexture() {
		return image;
	}
}