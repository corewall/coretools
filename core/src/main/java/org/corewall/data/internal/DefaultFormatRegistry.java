package org.corewall.data.internal;

import java.util.Map;
import java.util.Set;

import org.corewall.data.Format;
import org.corewall.data.FormatRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.internal.Maps;

/**
 * Default implementation of the {@link FormatRegistry} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@Singleton
public class DefaultFormatRegistry implements FormatRegistry {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultFormatRegistry.class);
	protected final Map<String, Format<?>> formats;

	/**
	 * Create a new DefaultFormatRegistry.
	 */
	DefaultFormatRegistry() {
		formats = Maps.newHashMap();
		LOG.debug("Initialized");
	}

	public Format<?> get(final String id) {
		return formats.get(id);
	}

	@Inject(optional = true)
	void inject(final Set<Format<?>> injected) {
		for (Format<?> f : injected) {
			formats.put(f.getId(), f);
		}
	}

	public void register(final Format<?> format) {
		formats.put(format.getId(), format);
	}
}
