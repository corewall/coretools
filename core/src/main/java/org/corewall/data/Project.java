package org.corewall.data;

import java.net.URL;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * The Project interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Project {
	/**
	 * The standard attributes.
	 */
	enum Attr {
		DESCRIPTION, EXPEDITION, HOLE, LATITUDE, LONGITUDE, PROGRAM, SITE
	}

	/**
	 * Defines a pointer to a data file.
	 */
	interface ManifestEntry {
		/**
		 * Gets the format.
		 * 
		 * @return the format.
		 */
		String getFormat();

		/**
		 * Gets the name.
		 * 
		 * @return the name.
		 */
		String getName();

		/**
		 * Gets the path.
		 * 
		 * @return the path.
		 */
		URL getPath();

		/**
		 * Gets the type.
		 * 
		 * @return the type.
		 */
		String getType();
	}

	/**
	 * The Geo Schema URI.
	 */
	String GEO_URI = "http://www.w3.org/2003/01/geo/wgs84_pos#";

	/**
	 * The Project Schema URI.
	 */
	String PROJECT_URI = "http://corewall.org/1.0/project";

	/**
	 * Gets the project attribute value.
	 * 
	 * @param attr
	 *            the attribute.
	 * @return the value or null if not set.
	 */
	String getAttribute(Attr attr);

	/**
	 * Gets the id.
	 * 
	 * @return the id.
	 */
	String getId();

	/**
	 * Gets the manifest entries.
	 * 
	 * @return the immutable list of entries.
	 */
	ImmutableList<ManifestEntry> getManifest();

	/**
	 * Gets the name.
	 * 
	 * @return the name.
	 */
	String getName();

	/**
	 * Gets the path.
	 * 
	 * @return the path.
	 */
	URL getPath();

	/**
	 * Gets the project properties.
	 * 
	 * @return the properties.
	 */
	ImmutableMap<String, String> getProperties();
}
