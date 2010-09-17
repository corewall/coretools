package org.corewall.data.io;

import java.util.List;
import java.util.Map;

/**
 * Reads models from a resource.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface ModelReader {

	/**
	 * Gets all models.
	 * 
	 * @return the list of models.
	 */
	List<Map<String, String>> getModels();

	/**
	 * Gets the filtered models.
	 * 
	 * @param filter
	 *            the filter.
	 * @return the filtered list of models.
	 */
	List<Map<String, String>> getModels(Filter filter);

	/**
	 * Gets the model POJOs.
	 * 
	 * @param <T>
	 *            the POJO type.
	 * @param filter
	 *            the filter.
	 * @param factory
	 *            the POJO factory.
	 * @return the list of model POJOs.
	 */
	<T> List<T> getModels(Filter filter, Factory<T> factory);
}
