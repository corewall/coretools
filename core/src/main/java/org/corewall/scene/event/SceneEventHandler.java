package org.corewall.scene.event;

/**
 * Defines the interface for a scene event handler.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface SceneEventHandler {

	/**
	 * Fired when a key is pressed.
	 * 
	 * @param e
	 *            the event.
	 * @return the feedback.
	 */
	Feedback keyPressed(SceneKeyEvent e);

	/**
	 * Fired when a key is released.
	 * 
	 * @param e
	 *            the event.
	 * @return the feedback.
	 */
	Feedback keyReleased(SceneKeyEvent e);

	/**
	 * Fired when a key is typed.
	 * 
	 * @param e
	 *            the event.
	 * @return the feedback.
	 */
	Feedback keyTyped(SceneKeyEvent e);

	/**
	 * Fired when the mouse is clicked.
	 * 
	 * @param e
	 *            the event.
	 * @return the feedback.
	 */
	Feedback mouseClicked(SceneMouseEvent e);

	/**
	 * Fired when the mouse is dragged.
	 * 
	 * @param e
	 *            the event.
	 * @return the feedback.
	 */
	Feedback mouseDragged(SceneMouseEvent e);

	/**
	 * Fired when the mouse is moved.
	 * 
	 * @param e
	 *            the event.
	 * @return the feedback.
	 */
	Feedback mouseMoved(SceneMouseEvent e);

	/**
	 * Fired when the mouse is pressed.
	 * 
	 * @param e
	 *            the event.
	 * @return the feedback.
	 */
	Feedback mousePressed(SceneMouseEvent e);

	/**
	 * Fired when the mouse is released.
	 * 
	 * @param e
	 *            the event.
	 * @return the feedback.
	 */
	Feedback mouseReleased(SceneMouseEvent e);
}
