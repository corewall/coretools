package org.corewall.ui.app;

import javax.swing.Action;

/**
 * Defines the interface for a menu contribution.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * 
 */
public interface MenuContribution {
	/**
	 * Selection condition to control enablement.
	 */
	interface Condition {
		void selected(Object selection);
	}

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
	 * Gets the enablement condition.
	 * 
	 * @return the condition or null.
	 */
	Condition getCondition();

	/**
	 * Gets the menu path.
	 * 
	 * @return the menu path.
	 */
	String getMenu();
}
