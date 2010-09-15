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