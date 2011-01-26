package org.corewall.scene.event;

import java.awt.Color;

import org.corewall.graphics.GraphicsContext;

/**
 * Defines the interface for feedback.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Feedback {
	/**
	 * The feedback type.
	 */
	public enum Type {
		/**
		 * Select-type feedback.
		 */
		SELECT,
		/**
		 * Create-type feedback.
		 */
		CREATE,
		/**
		 * Move-type feedback.
		 */
		MOVE,
		/**
		 * Resize-type feedback.
		 */
		RESIZE,
		/**
		 * Delete-type feedback.
		 */
		DELETE
	}

	/**
	 * Select feedback type.
	 */
	public static final Color COLOR = new Color(0, 0, 0, 192);

	/**
	 * Gets the cursor type of this feedback.
	 * 
	 * @return the cursor type.
	 */
	public abstract int getCursorType();

	/**
	 * Gets a property of this feedback.
	 * 
	 * @param name
	 *            the property.
	 * @return the value of the property.
	 */
	public abstract String getProperty(final String name);

	/**
	 * Gets the target of this feedback.
	 * 
	 * @return the target.
	 */
	public abstract Object getTarget();

	/**
	 * Gets the type of this feedback.
	 * 
	 * @return the request type.
	 */
	public abstract String getType();

	/**
	 * Checks whether this feedback needs rendering.
	 * 
	 * @return true if the feedback needs rendering, false otherwise.
	 */
	public boolean needsRendering();

	/**
	 * Renders feedback.
	 * 
	 * @param graphics
	 *            the graphics object.
	 */
	public abstract void renderFeedback(final GraphicsContext graphics);

}