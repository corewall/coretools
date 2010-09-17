package org.corewall.data;

import java.util.Map;

/**
 * Filters a list of maps.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Filter {

	/**
	 * Determines whether the specified map should be accepted or not.
	 * 
	 * @param map
	 *            the map.
	 * @return true if it is accepted, false otherwise.
	 */
	boolean accept(Map<String, String> map);
}