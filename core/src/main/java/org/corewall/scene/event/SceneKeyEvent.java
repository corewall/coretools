package org.corewall.scene.event;

import java.awt.event.KeyEvent;

import org.corewall.scene.Part;

/**
 * A scene keyboard event.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SceneKeyEvent extends SceneEvent {
	protected char keyChar;
	protected int keyCode;

	/**
	 * Create a new SceneKeyEvent.
	 */
	public SceneKeyEvent() {
	}

	/**
	 * Create a new SceneKeyEvent.
	 * 
	 * @param source
	 *            the source.
	 * @param target
	 *            the target.
	 * @param modifiers
	 *            the modifiers.
	 * @param keyCode
	 *            the key code.
	 * @param keyChar
	 *            the key character.
	 */
	public SceneKeyEvent(final Object source, final Part target, final int modifiers, final int keyCode,
			final char keyChar) {
		this.target = target;
		this.source = source;
		this.modifiers = modifiers;
		this.keyCode = keyCode;
		this.keyChar = keyChar;
	}

	/**
	 * Create a new SceneKeyEvent from the specified AWT KeyEvent.
	 * 
	 * @param source
	 *            the source.
	 * @param target
	 *            the target.
	 * @param event
	 *            the event.
	 */
	public SceneKeyEvent(final Object source, final Part target, final KeyEvent event) {
		this.source = source;
		this.target = target;
		modifiers = event.getModifiersEx();
		keyCode = event.getKeyCode();
		keyChar = event.getKeyChar();
	}

	/**
	 * Gets the key character.
	 * 
	 * @return the key chararacter.
	 */
	public char getKeyChar() {
		return keyChar;
	}

	/**
	 * Gets the key code.
	 * 
	 * @return the key code.
	 */
	public int getKeyCode() {
		return keyCode;
	}

	/**
	 * Sets the key character.
	 * 
	 * @param keyChar
	 *            the key character.
	 */
	public void setKeyChar(final char keyChar) {
		this.keyChar = keyChar;
	}

	/**
	 * Sets the key code.
	 * 
	 * @param keyCode
	 *            the key code.
	 */
	public void setKeyCode(final int keyCode) {
		this.keyCode = keyCode;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SceneKeyEvent [");
		sb.append("Source: " + getSource() + ", ");
		sb.append("Target: " + getTarget() + ", ");
		sb.append("Modifiers: " + getModifiers() + ", ");
		sb.append("Alt: " + isAltDown() + ", ");
		sb.append("Ctrl: " + isControlDown() + ", ");
		sb.append("Meta: " + isMetaDown() + ", ");
		sb.append("Shift: " + isShiftDown() + ", ");
		sb.append("Key Code: " + getKeyCode() + ", ");
		sb.append("Key Char: " + getKeyChar());
		sb.append(']');
		return sb.toString();
	}
}
