package org.corewall;

import java.net.URL;

import org.corewall.internal.DefaultLocator;

import com.google.common.collect.ImmutableList;
import com.google.inject.ImplementedBy;

/**
 * Locates resources.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@ImplementedBy(DefaultLocator.class)
public interface Locator {

	/**
	 * Add a resource to this locator.
	 * 
	 * @param resource
	 *            the resource.
	 */
	void addResource(URL resource);

	/**
	 * Find a resource.
	 * 
	 * @param path
	 *            the path to the resource.
	 * 
	 * @return the resource URL or null if not found.
	 */
	URL getResource(String path);

	/**
	 * Find all instances of a resource.
	 * 
	 * @param path
	 *            the path.
	 * @return the list of resource URLs.
	 */
	ImmutableList<URL> getResources(String path);
}
