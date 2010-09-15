package org.corewall.data;

import java.util.Map;

/**
 * Defines the interface for a model.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Model {

	/**
	 * Gets this properties of this model as a map.
	 * 
	 * @return the map.
	 */
	Map<String, String> toMap();
}
