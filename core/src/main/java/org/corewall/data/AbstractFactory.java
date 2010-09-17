package org.corewall.data;

import java.util.Map;
import java.util.Map.Entry;


/**
 * An abstract implementation of the {@link Factory} interface that allows for
 * re-writing the model map before constructing the model object.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * @param <T>
 *            the type that the factory creates.
 */
public abstract class AbstractFactory<T extends Model> implements Factory<T> {
	protected final Map<String, String> defaults;
	protected final Map<String, String> rewrite;

	/**
	 * Create a new {@link AbstractFactory} with the specified property map.
	 * 
	 * @param rewrite
	 *            the property names to rewrite.
	 */
	public AbstractFactory(final Map<String, String> rewrite) {
		this.rewrite = rewrite;
		this.defaults = null;
	}

	/**
	 * Create a new {@link AbstractFactory} with the specified property map.
	 * 
	 * @param rewrite
	 *            the property names to rewrite.
	 * @param defaults
	 *            the default properties.
	 */
	public AbstractFactory(final Map<String, String> rewrite, final Map<String, String> defaults) {
		this.rewrite = rewrite;
		this.defaults = defaults;
	}

	public T build(final Map<String, String> map) {
		return internalBuild(rewrite(map));
	}

	/**
	 * Constructs the model object from the specified model map.
	 * 
	 * @param map
	 *            the model map.
	 * @return the model object.
	 */
	protected abstract T internalBuild(Map<String, String> map);

	protected Map<String, String> rewrite(final Map<String, String> map) {
		if (rewrite != null) {
			for (Entry<String, String> e : rewrite.entrySet()) {
				String before = e.getKey();
				String after = e.getValue();
				if (map.containsKey(before)) {
					String value = map.remove(before);
					if (after != null) {
						map.put(after, value);
					}
				}
			}
		}
		if (defaults != null) {
			for (Entry<String, String> e : defaults.entrySet()) {
				if (!map.containsKey(e.getKey())) {
					map.put(e.getKey(), e.getValue());
				}
			}
		}
		return map;
	}
}