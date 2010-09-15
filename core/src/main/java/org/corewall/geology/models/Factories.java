package org.corewall.geology.models;

import java.util.Map;
import java.util.Map.Entry;

import org.corewall.data.io.ModelReader.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pre-defined factories for creating models.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public final class Factories {
	/**
	 * An abstract implementation of the {@link Factory} interface that allows
	 * for re-writing the model map before constructing the model object.
	 * 
	 * @author Josh Reed (jareed@andrill.org)
	 * @param <T>
	 *            the type that the factory creates.
	 */
	public static abstract class RewritingFactory<T> implements Factory<T> {
		protected final Map<String, String> rewrite;
		protected final Map<String, String> defaults;

		/**
		 * Create a new {@link RewritingFactory} with the specified property
		 * map.
		 * 
		 * @param rewrite
		 *            the property names to rewrite.
		 */
		public RewritingFactory(final Map<String, String> rewrite) {
			this.rewrite = rewrite;
			this.defaults = null;
		}

		/**
		 * Create a new {@link RewritingFactory} with the specified property
		 * map.
		 * 
		 * @param rewrite
		 *            the property names to rewrite.
		 * @param defaults
		 *            the default properties.
		 */
		public RewritingFactory(final Map<String, String> rewrite, final Map<String, String> defaults) {
			this.rewrite = rewrite;
			this.defaults = defaults;
		}

		public T build(final Map<String, String> map) {
			return internalBuild(rewrite(map));
		}

		/**
		 * Constructs the model object from the specified model map.
		 * 
		 * @param map
		 *            the model map.
		 * @return the model object.
		 */
		protected abstract T internalBuild(Map<String, String> map);

		protected Map<String, String> rewrite(final Map<String, String> map) {
			if (rewrite != null) {
				for (Entry<String, String> e : rewrite.entrySet()) {
					String before = e.getKey();
					String after = e.getValue();
					if (map.containsKey(before)) {
						String value = map.remove(before);
						if (after != null) {
							map.put(after, value);
						}
					}
				}
			}
			if (defaults != null) {
				for (Entry<String, String> e : defaults.entrySet()) {
					if (!map.containsKey(e.getKey())) {
						map.put(e.getKey(), e.getValue());
					}
				}
			}
			return map;
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(Factories.class);

	/**
	 * Returns a {@link Factory} that creates {@link XYDatum}s from model maps.
	 * The model maps are assumed to use the same property names as expected by
	 * the XYDatum object.
	 * 
	 * @return the {@link Factory} instance.
	 */
	public static Factory<XYDatum> datum() {
		return datum(null, null);
	}

	/**
	 * Returns a {@link Factory} that creates {@link XYDatum}s from model maps.
	 * The properties in the model map will be re-written according to the
	 * specified re-write map.
	 * 
	 * @param rewrite
	 *            the rewrite map.
	 * @param defaults
	 *            the defaults.
	 * 
	 * @return the {@link Factory} instance.
	 */
	public static Factory<XYDatum> datum(final Map<String, String> rewrite, final Map<String, String> defaults) {
		return new RewritingFactory<XYDatum>(rewrite, defaults) {
			@Override
			protected XYDatum internalBuild(final Map<String, String> map) {
				try {
					return new XYDatum(map);
				} catch (RuntimeException e) {
					LOGGER.warn("Invalid datum", e);
					return null;
				}
			}
		};
	}

	/**
	 * Returns a {@link Factory} that creates {@link Image}s from model maps.
	 * The model maps are assumed to use the same property names as expected by
	 * the Image object.
	 * 
	 * @return the {@link Factory} instance.
	 */
	public static Factory<Image> image() {
		return image(null, null);
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
	public static Factory<Image> image(final Map<String, String> rewrite, final Map<String, String> defaults) {
		return new RewritingFactory<Image>(rewrite, defaults) {
			@Override
			protected Image internalBuild(final Map<String, String> map) {
				return new Image(map);
			}
		};
	}

	/**
	 * Returns a {@link Factory} that creates {@link Section}s from model maps.
	 * The model maps are assumed to use the same property names as expected by
	 * the Section object.
	 * 
	 * @return the {@link Factory} instance.
	 */
	public static Factory<Section> section() {
		return section(null, null);
	}

	/**
	 * Returns a factory that creates {@link Section}s from model maps. The
	 * properties in the model map will be re-written according to the specified
	 * rewrite map.
	 * 
	 * @param rewrite
	 *            the rewrite map.
	 * @param defaults
	 *            the defaults.
	 * @return the {@link Factory} instance.
	 */
	public static Factory<Section> section(final Map<String, String> rewrite, final Map<String, String> defaults) {
		return new RewritingFactory<Section>(rewrite, defaults) {
			@Override
			protected Section internalBuild(final Map<String, String> map) {
				return new Section(map);
			}
		};
	}

	private Factories() {
		// not to be instantiated
	}
}
