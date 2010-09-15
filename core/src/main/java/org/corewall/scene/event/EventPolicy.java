package org.corewall.scene.event;

import org.corewall.scene.Track;
import org.corewall.scene.edit.Command;

/**
 * Defines the interface for an EventPolicy. An EventPolicy takes an event and
 * converts it into a {@link Feedback} and a {@link Command}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface EventPolicy {
	/**
	 * The event type.
	 */
	public enum Type {
		CREATE, MOVE, RESIZE, KEY
	}

	/**
	 * Gets the {@link Command} for the specified event.
	 * 
	 * @param e
	 *            the event.
	 * @param m
	 *            the target.
	 * @return the command or null.
	 */
	Command getCommand(SceneEvent e, Object m);

	/**
	 * Gets the {@link Feedback} for the specified event.
	 * 
	 * @param e
	 *            the event.
	 * @param m
	 *            the target.
	 * @return the feedback or null.
	 */
	Feedback getFeedback(SceneEvent e, Object m);

	/**
	 * Gets the track associated with this policy.
	 * 
	 * @return the track.
	 */
	Track getTrack();

	/**
	 * Gets the type of this {@link EventPolicy}.
	 * 
	 * @return the type.
	 */
	EventPolicy.Type getType();

	/**
	 * Sets the track associated with this policy.
	 * 
	 * @param track
	 *            the track.
	 */
	void setTrack(Track track);
}
