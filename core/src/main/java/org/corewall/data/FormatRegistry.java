package org.corewall.data;

import org.corewall.data.internal.DefaultFormatRegistry;

import com.google.inject.ImplementedBy;

/**
 * A registry of all available {@link Format}s.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@ImplementedBy(DefaultFormatRegistry.class)
public interface FormatRegistry {

	/**
	 * Gets the format with the specified id.
	 * 
	 * @param id
	 *            the id.
	 * @return the Format or null if no format is registered with that id.
	 */
	Format<?> get(String id);

	/**
	 * Register a new format.
	 * 
	 * @param format
	 *            the format.
	 */
	void register(Format<?> format);
}
