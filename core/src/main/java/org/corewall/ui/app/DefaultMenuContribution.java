package org.corewall.ui.app;

import javax.swing.Action;

/**
 * A default implementation of the {@link MenuContribution} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * 
 * @param <E>
 *            the target class.
 */
public class DefaultMenuContribution<E> implements MenuContribution<E> {
	interface Validator<E> {
		boolean isEnabled(E target);
	}

	protected Action action;
	protected final String application;
	protected String menu;
	protected Class<E> targetClass;
	protected Validator<E> validator;

	/**
	 * Create a new DefaultMenuContribution.
	 * 
	 * @param application
	 *            the application.
	 * @param targetClass
	 *            the target class.
	 * @param menu
	 *            the menu path.
	 * @param action
	 *            the action.
	 */
	public DefaultMenuContribution(final String application, final Class<E> targetClass, final String menu,
			final Action action) {
		this(application, targetClass, menu, action, null);
	}

	/**
	 * Create a new DefaultMenuContribution.
	 * 
	 * @param application
	 *            the application.
	 * @param targetClass
	 *            the target class.
	 * @param menu
	 *            the menu path.
	 * @param action
	 *            the action.
	 * @param validator
	 *            the validator.
	 */
	public DefaultMenuContribution(final String application, final Class<E> targetClass, final String menu,
			final Action action, final Validator<E> validator) {
		this.application = application;
		this.targetClass = targetClass;
		this.menu = menu;
		this.action = action;
		this.validator = validator;
	}

	/**
	 * Create a new DefaultMenuContribution.
	 * 
	 * @param application
	 *            the application.
	 * @param menu
	 *            the menu path.
	 * @param action
	 *            the action.
	 */
	public DefaultMenuContribution(final String application, final String menu, final Action action) {
		this(application, null, menu, action, null);
	}

	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public String getApplication() {
		return application;
	}

	@Override
	public String getMenu() {
		return menu;
	}

	@Override
	public Class<E> getTargetClass() {
		return targetClass;
	}

	@Override
	public boolean isEnabled(final E target) {
		if (validator == null) {
			return true;
		} else {
			return validator.isEnabled(target);
		}
	}

	/**
	 * Sets the action.
	 * 
	 * @param action
	 *            the action.
	 */
	public void setAction(final Action action) {
		this.action = action;
	}

	/**
	 * Sets the menu.
	 * 
	 * @param menu
	 *            the menu.
	 */
	public void setMenu(final String menu) {
		this.menu = menu;
	}

	/**
	 * Sets the target class.
	 * 
	 * @param targetClass
	 *            the target class.
	 */
	public void setTargetClass(final Class<E> targetClass) {
		this.targetClass = targetClass;
	}

	/**
	 * Sets the validator.
	 * 
	 * @param validator
	 *            the validator.
	 */
	public void setValidator(final Validator<E> validator) {
		this.validator = validator;
	}
}
