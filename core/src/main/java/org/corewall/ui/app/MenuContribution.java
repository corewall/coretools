package org.corewall.ui.app;

import javax.swing.Action;

/**
 * Defines the interface for a menu contribution.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * 
 * @param <E>
 *            the target class.
 */
public interface MenuContribution<E> {

	/**
	 * Gets the action to perform.
	 * 
	 * @return the action.
	 */
	Action getAction();

	/**
	 * Gets the application the contribution is for.
	 * 
	 * @return the application.
	 */
	String getApplication();

	/**
	 * Gets the menu path.
	 * 
	 * @return the menu path.
	 */
	String getMenu();

	/**
	 * Gets the target class.
	 * 
	 * @return the target class or null.
	 */
	Class<E> getTargetClass();

	/**
	 * Tests whether the contribution should be enabled for the specified target
	 * or not.
	 * 
	 * @param target
	 *            the target.
	 * @return true if it is enabled, false otherwise.
	 */
	boolean isEnabled(E target);
}
