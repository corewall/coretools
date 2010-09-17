package org.corewall.data.formats;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.corewall.data.Factory;
import org.corewall.data.Filter;
import org.corewall.data.Format;
import org.corewall.data.Model;

import com.google.common.collect.Lists;
import com.google.inject.internal.Nullable;

/**
 * An abstract implemenation of the {@link Format} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * 
 * @param <T>
 *            the {@link Model} type.
 */
public abstract class AbstractFormat<T extends Model> implements Format<T> {
	protected Factory<T> factory;
	protected Filter filter;
	protected String id;

	protected AbstractFormat(final String id, @Nullable final Filter filter, @Nullable final Factory<T> factory) {
		this.id = id;
		this.filter = filter;
		this.factory = factory;
	}

	public String getId() {
		return id;
	}

	public List<T> getModels(final URL url) throws IOException {
		List<T> list = Lists.newLinkedList();
		if (factory == null) {
			for (Map<String, String> map : getRaw(url)) {
				if ((filter == null) || filter.accept(map)) {
					if (factory != null) {
						T built = factory.build(map);
						if (built != null) {
							list.add(built);
						}
					}
				}
			}
		}
		return list;
	}
}
