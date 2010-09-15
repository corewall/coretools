package org.corewall.scene.event;

import java.awt.Rectangle;
import java.util.Map;

import org.corewall.scene.Scene;
import org.corewall.scene.Track;

/**
 * A default implementation of the {@link SceneEventHandler} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultSceneEventHandler implements SceneEventHandler {
	protected final Scene scene;
	protected final Map<Track, Rectangle> layout;
	protected Track track = null;
	protected SceneEventHandler handler = null;

	/**
	 * Create a new DefaultEventHandler.
	 * 
	 * @param scene
	 *            the scene.
	 * @param layout
	 *            the layout.
	 */
	public DefaultSceneEventHandler(final Scene scene, final Map<Track, Rectangle> layout) {
		this.scene = scene;
		this.layout = layout;
	}

	protected SceneEventHandler getHandler(final SceneMouseEvent e) {
		if (e == null) {
			return handler;
		}

		// find our track and adapt it to an event handler
		Track t = null;
		for (Map.Entry<Track, Rectangle> entry : layout.entrySet()) {
			Rectangle r = entry.getValue();
			if ((e.getX() >= r.getMinX()) && (e.getX() <= r.getMaxX())) {
				t = entry.getKey();
			}
		}
		if (t != track) {
			track = t;
			handler = (t == null) ? null : track.getEventHandler();
		}

		return handler;
	}

	public Feedback keyPressed(final SceneKeyEvent e) {
		SceneEventHandler handler = getHandler(null);
		if (handler != null) {
			return handler.keyPressed(e);
		} else {
			return null;
		}
	}

	public Feedback keyReleased(final SceneKeyEvent e) {
		SceneEventHandler handler = getHandler(null);
		if (handler != null) {
			return handler.keyReleased(e);
		} else {
			return null;
		}
	}

	public Feedback keyTyped(final SceneKeyEvent e) {
		SceneEventHandler handler = getHandler(null);
		if (handler != null) {
			return handler.keyTyped(e);
		} else {
			return null;
		}
	}

	public Feedback mouseClicked(final SceneMouseEvent e) {
		SceneEventHandler handler = getHandler(e);
		if (handler != null) {
			return handler.mouseClicked(e);
		} else {
			return null;
		}
	}

	public Feedback mouseDragged(final SceneMouseEvent e) {
		SceneEventHandler handler = getHandler(e);
		if (handler != null) {
			return handler.mouseDragged(e);
		} else {
			return null;
		}
	}

	public Feedback mouseMoved(final SceneMouseEvent e) {
		SceneEventHandler handler = getHandler(e);
		if (handler != null) {
			return handler.mouseMoved(e);
		} else {
			return null;
		}
	}

	public Feedback mousePressed(final SceneMouseEvent e) {
		SceneEventHandler handler = getHandler(e);
		if (handler != null) {
			return handler.mousePressed(e);
		} else {
			return null;
		}
	}

	public Feedback mouseReleased(final SceneMouseEvent e) {
		SceneEventHandler handler = getHandler(e);
		if (handler != null) {
			return handler.mouseReleased(e);
		} else {
			return null;
		}
	}
}
