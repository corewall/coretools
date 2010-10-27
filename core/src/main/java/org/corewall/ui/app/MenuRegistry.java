package org.corewall.ui.app;

import org.corewall.ui.app.internal.DefaultMenuRegistry;

import com.google.common.collect.ImmutableList;
import com.google.inject.ImplementedBy;

/**
 * The menu registry.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@ImplementedBy(DefaultMenuRegistry.class)
public interface MenuRegistry {
	/**
	 * Gets the menu contributions for the specified application.
	 * 
	 * @param application
	 *            the application.
	 * @return the immutable list of contributions.
	 */
	ImmutableList<MenuContribution> getMenuContributions(String application);

	/**
	 * Registers a menu contribution.
	 * 
	 * @param contribution
	 *            the contribution.
	 */
	void register(MenuContribution contribution);
}
