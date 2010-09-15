package org.corewall.scene.edit;

/**
 * A composite command contains multiple other commands that should be treated as a single command.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CompositeCommand implements Command {
	protected final Command[] commands;
	protected final String label;
	protected boolean executed = false;

	/**
	 * Create a new CompositeCommand with the specified label and commands.
	 * 
	 * @param label
	 *            the label.
	 * @param commands
	 *            the commands.
	 */
	public CompositeCommand(final String label, final Command... commands) {
		this.label = label;
		this.commands = commands;
	}

	public boolean canExecute() {
		boolean executable = true;
		for (Command c : commands) {
			executable &= c.canExecute();
		}
		return !executed && executable;
	}

	public boolean canUndo() {
		boolean undoable = true;
		for (Command c : commands) {
			undoable &= c.canUndo();
		}
		return executed && undoable;
	}

	public void execute() {
		if (canExecute()) {
			for (Command c : commands) {
				c.execute();
			}
			executed = true;
		}
	}

	public String getLabel() {
		return label;
	}

	public void redo() {
		execute();
	}

	public void undo() {
		if (canUndo()) {
			for (Command c : commands) {
				c.undo();
			}
			executed = false;
		}
	}
}
