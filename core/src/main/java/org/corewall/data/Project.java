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
