package org.corewall.scene.edit;


/**
 * A test command.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class TestCommand extends AbstractCommand {
	/**
	 * The last state.
	 */
	public String last = null;

	@Override
	protected void executeCommand() {
		last = "execute";
	}

	public String getLabel() {
		return "TestCommand";
	}

	@Override
	protected void undoCommand() {
		last = "undo";
	}
}
