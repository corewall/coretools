package org.corewall.scene.edit;

/**
 * An abstract implementation of the Command interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public abstract class AbstractCommand implements Command {
	protected boolean executed = false;

	public boolean canExecute() {
		return !executed;
	}

	public boolean canUndo() {
		return executed;
	}

	public void execute() {
		if (canExecute()) {
			executeCommand();
			executed = true;
		}
	}

	/**
	 * Execute the command action.
	 */
	protected abstract void executeCommand();

	public void redo() {
		execute();
	}

	public void undo() {
		if (canUndo()) {
			undoCommand();
			executed = false;
		}
	}

	/**
	 * Undo the command action.
	 */
	protected abstract void undoCommand();
}
