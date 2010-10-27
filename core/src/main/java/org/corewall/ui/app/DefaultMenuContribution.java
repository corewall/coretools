package org.corewall.ui.app;

import javax.swing.Action;

/**
 * A default implementation of the {@link MenuContribution} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultMenuContribution implements MenuContribution {
	/**
	 * A {@link Condition} that enables the action only when the selection is of
	 * the specified class.
	 */
	public static class ClassCondition implements Condition {
		private final Class<?> clazz;
		private final Action action;

		/**
		 * Create a new ClassCondition.
		 * 
		 * @param action
		 *            the action.
		 * @param clazz
		 *            the class.
		 */
		public ClassCondition(final Action action, final Class<?> clazz) {
			this.action = action;
			this.clazz = clazz;
		}

		@Override
		public void selected(final Object selection) {
			action.setEnabled((selection != null) && (selection.getClass() == clazz));
		}
	}

	protected final Action action;
	protected final String application;
	protected final String menu;
	protected final Condition condition;

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
		this(application, menu, action, null);
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
	 * @param condition
	 *            the condition.
	 */
	public DefaultMenuContribution(final String application, final String menu, final Action action,
			final Condition condition) {
		this.application = application;
		this.menu = menu;
		this.action = action;
		this.condition = condition;
	}

	@Override
	public Action getAction() {
		return action;
	}

	@Override
	public String getApplication() {
		return application;
	}

	public Condition getCondition() {
		return condition;
	}

	@Override
	public String getMenu() {
		return menu;
	}
}
