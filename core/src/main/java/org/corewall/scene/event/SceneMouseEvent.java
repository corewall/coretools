package org.corewall.scene.event;

import java.awt.event.MouseEvent;

import org.corewall.scene.Part;

/**
 * A scene mouse event.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SceneMouseEvent extends SceneEvent {
	/**
	 * The mouse button of the event.
	 */
	public enum Button {
		BUTTON1, BUTTON2, BUTTON3, NO_BUTTON
	}

	protected Button button;
	protected int clickCount;
	protected int dragX = -1, dragY = -1;
	protected int x, y;

	/**
	 * Create a new SceneMouseEvent.
	 */
	public SceneMouseEvent() {
	}

	/**
	 * Create a new SceneMouseEvent.
	 * 
	 * @param source
	 *            the source.
	 * @param target
	 *            the target.
	 * @param modifiers
	 *            the modifiers.
	 * @param x
	 *            the x coordinate in diagram space.
	 * @param y
	 *            the y coordinate in diagram space.
	 * @param button
	 *            the button.
	 * @param clickCount
	 *            the click count.
	 */
	public SceneMouseEvent(final Object source, final Part target, final int modifiers, final int x, final int y,
			final Button button, final int clickCount) {
		this.target = target;
		this.source = source;
		this.modifiers = modifiers;
		this.x = x;
		this.y = y;
		this.button = button;
		this.clickCount = clickCount;
	}

	/**
	 * Create a new SceneMouseEvent from the specified AWT MouseEvent.
	 * 
	 * @param source
	 *            the source.
	 * @param target
	 *            the target.
	 * @param event
	 *            the mouse event.
	 */
	public SceneMouseEvent(final Object source, final Part target, final MouseEvent event) {
		this.source = source;
		this.target = target;
		modifiers = event.getModifiersEx();
		x = event.getX();
		y = event.getY();
		clickCount = event.getClickCount();
		switch (event.getButton()) {
			case MouseEvent.BUTTON1:
				button = Button.BUTTON1;
				break;
			case MouseEvent.BUTTON2:
				button = Button.BUTTON2;
				break;
			case MouseEvent.BUTTON3:
				button = Button.BUTTON3;
				break;
			default:
				button = Button.NO_BUTTON;
		}
	}

	/**
	 * Gets the button.
	 * 
	 * @return the button.
	 */
	public Button getButton() {
		return button;
	}

	/**
	 * Gets the click count.
	 * 
	 * @return the click count.
	 */
	public int getClickCount() {
		return clickCount;
	}

	/**
	 * Gets the x coordinate in diagram space.
	 * 
	 * @return the x coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Gets the y coordinate in diagram space.
	 * 
	 * @return the y coordinate.
	 */
	public int getY() {
		return y;
	}

	/**
	 * Set the button.
	 * 
	 * @param button
	 *            the button.
	 */
	public void setButton(final Button button) {
		this.button = button;
	}

	/**
	 * Sets teh click count.
	 * 
	 * @param clickCount
	 *            the click count.
	 */
	public void setClickCount(final int clickCount) {
		this.clickCount = clickCount;
	}

	/**
	 * Sets the x coordinate in diagram space.
	 * 
	 * @param x
	 *            the x coordinate.
	 */
	public void setX(final int x) {
		this.x = x;
	}

	/**
	 * ets the y coordinate in diagram space.
	 * 
	 * @param y
	 *            the y coordinate.
	 */
	public void setY(final int y) {
		this.y = y;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("SceneMouseEvent [");
		sb.append("Source: " + getSource() + ", ");
		sb.append("Target: " + getTarget() + ", ");
		sb.append("Modifiers: " + getModifiers() + ", ");
		sb.append("Alt: " + isAltDown() + ", ");
		sb.append("Ctrl: " + isControlDown() + ", ");
		sb.append("Meta: " + isMetaDown() + ", ");
		sb.append("Shift: " + isShiftDown() + ", ");
		sb.append("Button: " + getButton() + ", ");
		sb.append("Point: (" + getX() + ", " + getY() + "), ");
		sb.append("Click Count: " + getClickCount());
		sb.append(']');
		return sb.toString();
	}
}
