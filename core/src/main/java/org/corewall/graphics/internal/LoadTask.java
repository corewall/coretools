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
package org.corewall.graphics.internal;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Load image from URL task.
 */
public class LoadTask implements Callable<BufferedImage> {
	static class Params {
		String path;
		Future<File> file;
		int level = 0;
		boolean isVertical = false;
		WeakReference<JComponent> component;

		Params(final String path, final Future<File> file, final int level, final boolean isVertical, final JComponent component) {
			this.path = path;
			this.file = file;
			this.level = level;
			this.isVertical = isVertical;
			this.component = new WeakReference<JComponent>(component);
		}

		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Params other = (Params) obj;
			if (isVertical != other.isVertical) {
				return false;
			}
			if (level != other.level) {
				return false;
			}
			if (path == null) {
				if (other.path != null) {
					return false;
				}
			} else if (!path.equals(other.path)) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + (isVertical ? 1231 : 1237);
			result = prime * result + level;
			result = prime * result + ((path == null) ? 0 : path.hashCode());
			return result;
		}

	}

	private static final Logger LOGGER = LoggerFactory.getLogger(LoadTask.class);

	final Params params;

	/**
	 * Create a new LoadTask.
	 * 
	 * @param params
	 *            the params.
	 */
	public LoadTask(final Params params) {
		this.params = params;
	}

	public BufferedImage call() throws Exception {
		ImageInputStream iis = null;
		try {
			LOGGER.debug("Loading {} @ {}", params.path, params.level);

			// set decimation level
			final ImageReader imgReader = ImageIO.getImageReadersBySuffix(suffix(params.path)).next();
			iis = ImageIO.createImageInputStream(params.file.get());
			imgReader.setInput(iis);
			final ImageReadParam readParam = imgReader.getDefaultReadParam();
			if (params.level > 0) {
				readParam.setSourceSubsampling(params.level, params.level, 0, 0);
			}

			// load image
			BufferedImage image = imgReader.read(0, readParam);

			// rotate right if not vertical
			if (!params.isVertical) {
				// create our new image and graphics
				final BufferedImage out = new BufferedImage(image.getHeight(), image.getWidth(), image.getType());
				final Graphics2D g2 = out.createGraphics();

				// get our dimensions
				final double w = image.getWidth();
				final double h = image.getHeight();

				// setup up an affine transformation
				final AffineTransform at = AffineTransform.getRotateInstance(Math.PI / 2, h / 2, w / 2);
				at.translate((h - w) / 2, (w - h) / 2);
				g2.drawRenderedImage(image, at);
				g2.dispose();
				return out;
			} else {
				return image;
			}
		} catch (IOException ioe) {
			LOGGER.error("Unable to load image", ioe);
			return null;
		} finally {
			if (iis != null) {
				try {
					iis.close();
				} catch (IOException e) {
					// ignore
				}
			}
			final JComponent component = params.component.get();
			if (component != null) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						component.invalidate();
						component.validate();
						component.repaint();
					}
				});
			}
		}
	}

	private String suffix(final String str) {
		int index = str.lastIndexOf('.');
		if (index == -1) {
			return str;
		} else {
			return str.substring(index + 1);
		}
	}
}