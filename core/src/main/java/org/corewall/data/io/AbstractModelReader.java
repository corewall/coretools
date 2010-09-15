package org.corewall.data.io;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

/**
 * An abstract implementation of the ModelReader interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractModelReader implements ModelReader {
	protected List<Map<String, String>> cached;

	/**
	 * {@inheritDoc}
	 */
	public List<Map<String, String>> getModels() {
		if (cached == null) {
			cached = parseModels();
		}
		return cached;
	}

	/**
	 * {@inheritDoc}
	 */
	public List<Map<String, String>> getModels(final Filter filter) {
		List<Map<String, String>> list = Lists.newLinkedList();
		for (Map<String, String> map : getModels()) {
			if ((filter == null) || filter.accept(map)) {
				list.add(map);
			}
		}
		return list;
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> List<T> getModels(final Filter filter, final Factory<T> factory) {
		List<T> list = Lists.newLinkedList();
		for (Map<String, String> map : getModels()) {
			if ((filter == null) || filter.accept(map)) {
				if (factory != null) {
					T built = factory.build(map);
					if (built != null) {
						list.add(built);
					}
				}
			}
		}
		return list;
	}

	protected abstract List<Map<String, String>> parseModels();
}
