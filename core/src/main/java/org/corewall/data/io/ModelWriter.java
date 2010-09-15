package org.corewall.data.io;

import java.io.IOException;
import java.util.List;

import org.corewall.data.Model;


/**
 * Writes models to a resource.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface ModelWriter {

	/**
	 * Writes the list of models.
	 * 
	 * @param models
	 *            the models.
	 * @throws IOException
	 *             thrown if there is any problem writing the models.
	 */
	void write(List<? extends Model> models) throws IOException;
}
