package org.corewall.ui.data.internal;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import org.corewall.Platform;
import org.corewall.ProjectManager;
import org.corewall.ProjectManager.ProjectExistsException;
import org.corewall.data.Project;
import org.corewall.ui.data.DataManager;

/**
 * An action for creating a new {@link Project}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class NewProjectAction extends AbstractAction {
	/**
	 * The command identifier to use in the ActionMap.
	 */
	public static final String COMMAND = "project/new";
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new NewProjectAction.
	 */
	public NewProjectAction() {
		super("New Project");
		putValue(ACCELERATOR_KEY, keystroke(KeyEvent.VK_N, InputEvent.SHIFT_DOWN_MASK));
		putValue(SHORT_DESCRIPTION, "Create a New Project");
		putValue(ACTION_COMMAND_KEY, COMMAND);
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		DataManager app = DataManager.getInstance();

		// get our project
		Project project = NewProjectDialog.showDialog(app.getApplicationWindow());
		if (project != null) {
			ProjectManager projects = Platform.getService(ProjectManager.class);
			try {
				projects.add(project);
			} catch (ProjectExistsException e) {
				if (JOptionPane
						.showConfirmDialog(app.getApplicationWindow(), "A project with the id '" + project.getId()
								+ "' already exists.  Would you like to overwrite?", "Project Already Exists",
								JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
					try {
						projects.overwrite(project);
					} catch (IOException e1) {
						JOptionPane.showMessageDialog(app.getApplicationWindow(),
								"Unable to save project: " + e1.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(app.getApplicationWindow(), "Unable to save project: " + e.getMessage(),
						"Save Error", JOptionPane.ERROR_MESSAGE);
			}
			app.refresh();
		}
	}

	protected KeyStroke keystroke(final int key) {
		return KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
	}

	protected KeyStroke keystroke(final int key, final int modifier) {
		return KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | modifier);
	}
}
