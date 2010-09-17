package org.corewall.geology.models;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Map;

import org.corewall.Locator;
import org.corewall.Platform;
import org.corewall.data.AbstractFactory;
import org.corewall.data.Factory;
import org.corewall.data.Model;
import org.corewall.data.models.Length;
import org.corewall.data.models.Unit;
import org.corewall.graphics.internal.ImageInfo;
import org.corewall.scene.Orientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * Models an image.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Image implements Model {
	/**
	 * A helper class for building {@link Image}s.
	 */
	public static class Builder {
		private final Map<String, String> map;

		/**
		 * Creates a new builder.
		 */
		public Builder() {
			map = Maps.newHashMap();
		}

		/**
		 * Sets the base.
		 * 
		 * @param value
		 *            the base.
		 * @return the builder as a convenience.
		 */
		public Builder base(final double value) {
			map.put(BASE_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the base.
		 * 
		 * @param value
		 *            the base.
		 * @return the builder as a convenience.
		 */
		public Builder base(final Length value) {
			map.put(BASE_KEY, "" + value);
			return this;
		}

		/**
		 * Sets the base.
		 * 
		 * @param value
		 *            the base.
		 * @return the builder as a convenience.
		 */
		public Builder base(final String value) {
			map.put(BASE_KEY, value);
			return this;
		}

		/**
		 * Builds the image.
		 * 
		 * @return the built image.
		 */
		public Image build() {
			return new Image(map);
		}

		/**
		 * Sets the resolution in dots per cm.
		 * 
		 * @param value
		 *            the dpcm.
		 * @return the builder as a convenience.
		 */
		public Builder dpcm(final double value) {
			map.put(DPCM_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the resolution in dots per cm.
		 * 
		 * @param value
		 *            the dpcm.
		 * @return the builder as a convenience.
		 */
		public Builder dpcm(final String value) {
			map.put(DPCM_KEY, value);
			return this;
		}

		/**
		 * Sets the x resolution in dots per cm.
		 * 
		 * @param value
		 *            the dpcm.
		 * @return the builder as a convenience.
		 */
		public Builder dpcmX(final double value) {
			map.put(DPCM_X_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the x resolution in dots per cm.
		 * 
		 * @param value
		 *            the dpcm.
		 * @return the builder as a convenience.
		 */
		public Builder dpcmX(final String value) {
			map.put(DPCM_X_KEY, value);
			return this;
		}

		/**
		 * Sets the y resolution in dots per cm.
		 * 
		 * @param value
		 *            the dpcm.
		 * @return the builder as a convenience.
		 */
		public Builder dpcmY(final double value) {
			map.put(DPCM_Y_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the y resolution in dots per cm.
		 * 
		 * @param value
		 *            the dpcm.
		 * @return the builder as a convenience.
		 */
		public Builder dpcmY(final String value) {
			map.put(DPCM_Y_KEY, value);
			return this;
		}

		/**
		 * Sets the resolution in dots per inch.
		 * 
		 * @param value
		 *            the dpi.
		 * @return the builder as a convenience.
		 */
		public Builder dpi(final double value) {
			map.put(DPI_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the resolution in dots per inch.
		 * 
		 * @param value
		 *            the dpi.
		 * @return the builder as a convenience.
		 */
		public Builder dpi(final String value) {
			map.put(DPI_KEY, value);
			return this;
		}

		/**
		 * Sets the x resolution in dots per inch.
		 * 
		 * @param value
		 *            the dpi.
		 * @return the builder as a convenience.
		 */
		public Builder dpiX(final double value) {
			map.put(DPI_X_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the x resolution in dots per inch.
		 * 
		 * @param value
		 *            the dpi.
		 * @return the builder as a convenience.
		 */
		public Builder dpiX(final String value) {
			map.put(DPI_X_KEY, value);
			return this;
		}

		/**
		 * Sets the y resolution in dots per inch.
		 * 
		 * @param value
		 *            the dpi.
		 * @return the builder as a convenience.
		 */
		public Builder dpiY(final double value) {
			map.put(DPI_Y_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the y resolution in dots per inch.
		 * 
		 * @param value
		 *            the dpi.
		 * @return the builder as a convenience.
		 */
		public Builder dpiY(final String value) {
			map.put(DPI_Y_KEY, value);
			return this;
		}

		/**
		 * Sets the height.
		 * 
		 * @param value
		 *            the height.
		 * @return the builder as a convenience.
		 */
		public Builder height(final int value) {
			map.put(HEIGHT_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the height.
		 * 
		 * @param value
		 *            the height.
		 * @return the builder as a convenience.
		 */
		public Builder height(final String value) {
			map.put(HEIGHT_KEY, value);
			return this;
		}

		/**
		 * Sets the length.
		 * 
		 * @param value
		 *            the length.
		 * @return the builder as a convenience.
		 */
		public Builder length(final double value) {
			map.put(LENGTH_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the length.
		 * 
		 * @param value
		 *            the length.
		 * @return the builder as a convenience.
		 */
		public Builder length(final Length value) {
			map.put(LENGTH_KEY, "" + value);
			return this;
		}

		/**
		 * Sets the length.
		 * 
		 * @param value
		 *            the length.
		 * @return the builder as a convenience.
		 */
		public Builder length(final String value) {
			map.put(LENGTH_KEY, value);
			return this;
		}

		/**
		 * Sets the orientation.
		 * 
		 * @param value
		 *            the orientation.
		 * @return the builder as a convenience.
		 */
		public Builder orientation(final Orientation value) {
			map.put(ORIENTATION_KEY, value.toString());
			return this;
		}

		/**
		 * Sets the orientation.
		 * 
		 * @param value
		 *            the orientation.
		 * @return the builder as a convenience.
		 */
		public Builder orientation(final String value) {
			map.put(ORIENTATION_KEY, value);
			return this;
		}

		/**
		 * Sets the path.
		 * 
		 * @param value
		 *            the path.
		 * @return the builder as a convenience.
		 */
		public Builder path(final String value) {
			map.put(PATH_KEY, value);
			return this;
		}

		/**
		 * Sets the path.
		 * 
		 * @param url
		 *            the path.
		 * @return the builder as a convenience.
		 */
		public Builder path(final URL url) {
			map.put(PATH_KEY, url.toExternalForm());
			return this;
		}

		/**
		 * Sets the top.
		 * 
		 * @param value
		 *            the top.
		 * @return the builder as a convenience.
		 */
		public Builder top(final double value) {
			map.put(TOP_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the top.
		 * 
		 * @param value
		 *            the top.
		 * @return the builder as a convenience.
		 */
		public Builder top(final Length value) {
			map.put(TOP_KEY, "" + value);
			return this;
		}

		/**
		 * Sets the top.
		 * 
		 * @param value
		 *            the top.
		 * @return the builder as a convenience.
		 */
		public Builder top(final String value) {
			map.put(TOP_KEY, value);
			return this;
		}

		/**
		 * Sets the type.
		 * 
		 * @param value
		 *            the type.
		 * @return the builder as a convenience.
		 */
		public Builder type(final String value) {
			map.put(TYPE_KEY, value);
			return this;
		}

		/**
		 * Sets the width.
		 * 
		 * @param value
		 *            the width.
		 * @return the builder as a convenience.
		 */
		public Builder width(final int value) {
			map.put(WIDTH_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the width.
		 * 
		 * @param value
		 *            the width.
		 * @return the builder as a convenience.
		 */
		public Builder width(final String value) {
			map.put(WIDTH_KEY, value);
			return this;
		}
	}

	// keys
	protected static final String BASE_KEY = "base";
	protected static final String DPCM_KEY = "dpcm";
	protected static final String DPCM_X_KEY = "dpcmX";
	protected static final String DPCM_Y_KEY = "dpcmY";
	protected static final String DPI_KEY = "dpi";
	protected static final String DPI_X_KEY = "dpiX";
	protected static final String DPI_Y_KEY = "dpiY";
	protected static final String HEIGHT_KEY = "height";
	protected static final String LENGTH_KEY = "length";
	private static final Logger LOGGER = LoggerFactory.getLogger(Image.class);
	private static final DecimalFormat NUM = new DecimalFormat("0.####");
	protected static final String ORIENTATION_KEY = "orientation";
	protected static final String PATH_KEY = "path";
	protected static final String TOP_KEY = "top";
	protected static final String TYPE_KEY = "type";
	protected static final String WIDTH_KEY = "width";

	/**
	 * Creates a new Image builder.
	 * 
	 * @return the builder.
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Returns a {@link Factory} that creates {@link Image}s from model maps.
	 * The model maps are assumed to use the same property names as expected by
	 * the Image object.
	 * 
	 * @return the {@link Factory} instance.
	 */
	public static Factory<Image> factory() {
		return factory(null, null);
	}

	/**
	 * Returns a factory that creates {@link Image}s from model maps. The
	 * properties in the model map will be re-written according to the specified
	 * rewrite map.
	 * 
	 * @param rewrite
	 *            the rewrite map.
	 * @param defaults
	 *            the defaults.
	 * @return the {@link Factory} instance.
	 */
	public static Factory<Image> factory(final Map<String, String> rewrite, final Map<String, String> defaults) {
		return new AbstractFactory<Image>(rewrite, defaults) {
			@Override
			protected Image internalBuild(final Map<String, String> map) {
				return new Image(map);
			}
		};
	}

	protected Length base;
	protected double dpiX = -1;
	protected double dpiY = -1;
	protected int height = -1;
	protected Length length;
	protected Orientation orientation;
	protected boolean parsed = false;
	protected URL path;
	protected Length top;
	protected String type = null;
	protected int width = -1;

	/**
	 * Builds an image from a map of properties.
	 * 
	 * @param properties
	 *            the properties map.
	 */
	public Image(final Map<String, String> properties) {
		// parse our path
		path = Platform.getService(Locator.class).getResource(properties.get(PATH_KEY));
		if (path == null) {
			LOGGER.error("No 'path' key or invalid 'path' URL.");
			throw new RuntimeException("No 'path' key or invalid 'path' URL.");
		}

		// parse our type
		type = properties.get(TYPE_KEY);

		// parse our orientation
		String orientation = properties.get(ORIENTATION_KEY);
		if ((orientation != null) && (orientation.toLowerCase().charAt(0) == 'h')) {
			this.orientation = Orientation.HORIZONTAL;
		} else if ((orientation != null) && (orientation.toLowerCase().charAt(0) == 'v')) {
			this.orientation = Orientation.VERTICAL;
		} else {
			LOGGER.debug("No orientation set for {}, defaulting to horizontal", path.toExternalForm());
			this.orientation = Orientation.HORIZONTAL;
		}

		// parse top
		String top = properties.get(TOP_KEY);
		if (top == null) {
			this.top = Length.valueOf(0, Unit.METER);
		} else {
			this.top = Length.valueOf(top);
		}

		// parse base if specified
		String base = properties.get(BASE_KEY);
		if (base != null) {
			this.base = Length.valueOf(base);
		}

		// parse length if specified
		String length = properties.get(LENGTH_KEY);
		if (length != null) {
			this.length = Length.valueOf(length);
		}

		// parse image width and height
		String width = properties.get(WIDTH_KEY);
		if (width != null) {
			this.width = Integer.valueOf(width);
		}
		String height = properties.get(HEIGHT_KEY);
		if (height != null) {
			this.height = Integer.valueOf(height);
		}

		// parse resolution
		// dpi
		String dpiX = properties.get(DPI_X_KEY);
		if (dpiX != null) {
			this.dpiX = Double.valueOf(dpiX);
		}
		String dpiY = properties.get(DPI_Y_KEY);
		if (dpiY != null) {
			this.dpiY = Double.valueOf(dpiY);
		}
		String dpi = properties.get(DPI_KEY);
		if (dpi != null) {
			double parsed = Double.valueOf(dpi);
			if (preciseEquals(this.dpiX, -1.0)) {
				this.dpiX = parsed;
			}
			if (preciseEquals(this.dpiY, -1.0)) {
				this.dpiY = parsed;
			}
		}
		// dpcm
		String dpcmX = properties.get(DPCM_X_KEY);
		if ((dpcmX != null) && (preciseEquals(this.dpiX, -1))) {
			this.dpiX = Double.valueOf(dpcmX) * 2.54;
		}
		String dpcmY = properties.get(DPCM_Y_KEY);
		if ((dpcmY != null) && (preciseEquals(this.dpiY, -1))) {
			this.dpiY = Double.valueOf(dpcmY) * 2.54;
		}
		String dpcm = properties.get(DPCM_KEY);
		if (dpcm != null) {
			double parsed = Double.valueOf(dpcm) * 2.54;
			if (preciseEquals(this.dpiX, -1)) {
				this.dpiX = parsed;
			}
			if (preciseEquals(this.dpiY, -1)) {
				this.dpiY = parsed;
			}
		}

		// fill in any unspecified properties
		if (this.base == null) {
			if (this.length != null) {
				this.base = this.top.plus(this.length);
			} else {
				int pixels = (this.orientation == Orientation.HORIZONTAL ? this.width : this.height);
				double resolution = (this.orientation == Orientation.HORIZONTAL ? this.dpiX : this.dpiY);
				if ((pixels == -1) || (preciseEquals(resolution, -1))) {
					parseImageInfo();
					pixels = (this.orientation == Orientation.HORIZONTAL ? this.width : this.height);
					resolution = (this.orientation == Orientation.HORIZONTAL ? this.dpiX : this.dpiY);
				}

				// set our length and base
				this.length = Length.valueOf(pixels / resolution, Unit.INCH);
				this.base = this.top.plus(this.length);
			}
		}

		if (this.length == null) {
			this.length = this.base.minus(this.top);
		}
	}

	/**
	 * Converts all lengths (top, base, length) to the specified unit.
	 * 
	 * @param unit
	 *            the unit.
	 * @return this image as a convenience.
	 */
	public Image convertTo(final Unit unit) {
		top = top.to(unit);
		base = base.to(unit);
		length = length.to(unit);
		return this;
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
		Image other = (Image) obj;
		if (base == null) {
			if (other.base != null) {
				return false;
			}
		} else if (!base.equals(other.base)) {
			return false;
		}
		if (orientation == null) {
			if (other.orientation != null) {
				return false;
			}
		} else if (!orientation.equals(other.orientation)) {
			return false;
		}
		if (path == null) {
			if (other.path != null) {
				return false;
			}
		} else if (!path.toExternalForm().equals(other.path.toExternalForm())) {
			return false;
		}
		if (top == null) {
			if (other.top != null) {
				return false;
			}
		} else if (!top.equals(other.top)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the base position of this image.
	 * 
	 * @return the base.
	 */
	public Length getBase() {
		return base;
	}

	/**
	 * Gets the DPI along the x-axis of this image.
	 * 
	 * @return the DPI.
	 */
	public double getDpiX() {
		if (preciseEquals(dpiX, -1)) {
			parseImageInfo();
		}
		return dpiX;
	}

	/**
	 * Gets the DPI along the y-axis of this image.
	 * 
	 * @return the DPI.
	 */
	public double getDpiY() {
		if (preciseEquals(dpiY, -1)) {
			parseImageInfo();
		}
		return dpiY;
	}

	/**
	 * Gets the height of this image in pixels.
	 * 
	 * @return the height.
	 */
	public int getHeight() {
		if (height == -1) {
			parseImageInfo();
		}
		return height;
	}

	/**
	 * Gets the length of this image.
	 * 
	 * @return the image.
	 */
	public Length getLength() {
		return length;
	}

	/**
	 * Gets the orientation of this image.
	 * 
	 * @return the orientation.
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Gets the path to this image.
	 * 
	 * @return the path.
	 */
	public URL getPath() {
		return path;
	}

	/**
	 * Gets the top position of this image.
	 * 
	 * @return the top.
	 */
	public Length getTop() {
		return top;
	}

	/**
	 * Gets the type of this image.
	 * 
	 * @return the type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the width of this image in pixels.
	 * 
	 * @return the width.
	 */
	public int getWidth() {
		if (width == -1) {
			parseImageInfo();
		}
		return width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + ((orientation == null) ? 0 : orientation.hashCode());
		result = prime * result + ((path == null) ? 0 : path.toExternalForm().hashCode());
		result = prime * result + ((top == null) ? 0 : top.hashCode());
		return result;
	}

	protected void parseImageInfo() {
		if (parsed) {
			return;
		}
		parsed = true;

		// determine our width and height
		ImageInfo ii = new ImageInfo();
		InputStream in = null;
		try {
			in = path.openStream();
			ii.setInput(in);
			if (ii.check()) {
				if (width == -1) {
					width = ii.getWidth();
				}
				if (height == -1) {
					height = ii.getHeight();
				}

				// calculate DPI
				int pixels = (orientation == Orientation.HORIZONTAL ? width : height);
				double dpi = pixels / length.to(Unit.INCH).getValue().doubleValue();
				if (preciseEquals(dpiX, -1)) {
					dpiX = dpi;
				}
				if (preciseEquals(dpiY, -1)) {
					dpiY = dpi;
				}
			}

			// warn about invalid values
			if (width == -1) {
				LOGGER.warn("'width' was not specified and could not be parsed from the images, defaulting to -1");
			}
			if (height == -1) {
				LOGGER.warn("'height' was not specified and could not be parsed from the images, defaulting to -1");
			}
			if (preciseEquals(dpiX, -1)) {
				LOGGER.warn("'dpiX' was not specified and could not be parsed from the images, defaulting to -1");
			}
			if (preciseEquals(dpiY, -1)) {
				LOGGER.warn("'dpiY' was not specified and could not be parsed from the images, defaulting to -1");
			}
		} catch (IOException e) {
			LOGGER.error("Unable to parse image info for " + path.toExternalForm(), e);
		} finally {
			Closeables.closeQuietly(in);
		}
	}

	private boolean preciseEquals(final double a, final double b) {
		return Math.abs(a - b) < 1E-6;
	}

	/**
	 * Serializes this Image model to a map.
	 * 
	 * @return the serialized model as a map.
	 */
	public Map<String, String> toMap() {
		Map<String, String> map = Maps.newHashMapWithExpectedSize(10);
		map.put(PATH_KEY, getPath().toExternalForm());
		map.put(TYPE_KEY, (type == null ? "" : type));
		map.put(ORIENTATION_KEY, getOrientation().name());
		map.put(TOP_KEY, getTop().toString());
		map.put(BASE_KEY, getBase().toString());
		map.put(LENGTH_KEY, getLength().toString());
		map.put(DPI_X_KEY, NUM.format(getDpiX()));
		map.put(DPI_Y_KEY, NUM.format(getDpiY()));
		map.put(WIDTH_KEY, NUM.format(getWidth()));
		map.put(HEIGHT_KEY, NUM.format(getHeight()));
		return map;
	}

	@Override
	public String toString() {
		return "Image " + toMap();
	}
}
