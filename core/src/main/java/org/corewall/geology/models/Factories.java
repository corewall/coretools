package org.corewall.geology.models;

import java.util.Map;

import org.corewall.data.AbstractFactory;
import org.corewall.data.Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pre-defined factories for creating models.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public final class Factories {
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
		return new AbstractFactory<XYDatum>(rewrite, defaults) {
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

	private Factories() {
		// not to be instantiated
	}
}
