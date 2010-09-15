package org.corewall.scene;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.corewall.graphics.GraphicsContext;
import org.corewall.scene.event.SceneEventHandler;

import com.google.common.collect.ImmutableMap;

/**
 * Defines the interface of a track. A Track is responsible for visualizing
 * Models. For consistency, Tracks are considered to be oriented vertically when
 * calculating sizes and rendering.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Track {
	/**
	 * Find the track element at the specified point in screen coordinates.
	 * 
	 * @param screen
	 *            the screen coordinates.
	 * @param part
	 *            the scene part.
	 * @return the track element or null.
	 */
	Object findAt(Point2D screen, Part part);

	/**
	 * Gets the size of this track's contents.
	 * 
	 * @return the contents size.
	 */
	Rectangle2D getContentSize();

	/**
	 * Gets the event handler (if any) for this track.
	 * 
	 * @return the event handler.
	 */
	SceneEventHandler getEventHandler();

	/**
	 * Gets a configuration parameter for this track.
	 * 
	 * @param name
	 *            the name.
	 * @param defaultValue
	 *            the default value.
	 * @return the configured parameter or the default value if not set.
	 */
	String getParameter(String name, String defaultValue);

	/**
	 * Gets the parameters of this track.
	 * 
	 * @return the parameters.
	 */
	ImmutableMap<String, String> getParameters();

	/**
	 * Gets the scene this track exists in.
	 * 
	 * @return the scene.
	 */
	Scene getScene();

	/**
	 * Render the contents of this track.
	 * 
	 * @param graphics
	 *            the graphics.
	 * @param bounds
	 *            the bounds.
	 */
	void renderContents(GraphicsContext graphics, Rectangle2D bounds);

	/**
	 * Render the footer of this track.
	 * 
	 * @param graphics
	 *            the graphics.
	 * @param bounds
	 *            the bounds.
	 */
	void renderFooter(GraphicsContext graphics, Rectangle2D bounds);

	/**
	 * Render the header of this track.
	 * 
	 * @param graphics
	 *            the graphics.
	 * @param bounds
	 *            the bounds.
	 */
	void renderHeader(GraphicsContext graphics, Rectangle2D bounds);

	/**
	 * Sets a configuration parameter for this track.
	 * 
	 * @param name
	 *            the name.
	 * @param value
	 *            the value.
	 */
	void setParameter(String name, String value);

	/**
	 * Sets the scene this track exists in.
	 * 
	 * @param scene
	 *            the scene.
	 */
	void setScene(Scene scene);
}
