package org.corewall.data;

import java.util.Map;

/**
 * Builds a {@link Model} from a map.
 * 
 * @param <T>
 *            the Model type.
 */
public interface Factory<T extends Model> {

	/**
	 * Builds a Model from the specified map.
	 * 
	 * @param map
	 *            the map.
	 * @return the model.
	 */
	T build(Map<String, String> map);
}