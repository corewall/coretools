package org.corewall.data;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * A Format can read data from a URL and output either maps of key-value pairs
 * or actual {@link Model} objects.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * @param <T>
 *            the model type.
 */
public interface Format<T extends Model> {

	/**
	 * Gets the id of this format.
	 * 
	 * @return the id.
	 */
	String getId();

	/**
	 * Gets the models.
	 * 
	 * @param url
	 *            the URL.
	 * @return the list of models.
	 * @throws IOException
	 *             thrown if there is a problem reading the URL.
	 */
	List<T> getModels(URL url) throws IOException;

	/**
	 * Gets the raw maps.
	 * 
	 * @param url
	 *            the URL.
	 * @return the list of maps.
	 * @throws IOException
	 *             thrown if there is a problem reading the URL.
	 */
	List<Map<String, String>> getRaw(URL url) throws IOException;
}
