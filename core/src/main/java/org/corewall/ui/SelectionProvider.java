package org.corewall.ui;

import java.awt.geom.Point2D;

import org.corewall.scene.Scene;
import org.corewall.scene.Selection;
import org.corewall.scene.event.SceneMouseEvent;

/**
 * Provides the selection.
 */
public interface SelectionProvider {

	/**
	 * Default selection provider.
	 */
	SelectionProvider DEFAULT_PROVIDER = new SelectionProvider() {
		public Selection getSelection(final Scene scene, final SceneMouseEvent e) {
			Object o = scene.findAt(new Point2D.Double(e.getX(), e.getY()), e.getTarget());
			return (o == null) ? Selection.EMPTY : new Selection(o);
		}
	};

	/**
	 * Gets the selection.
	 * 
	 * @param scene
	 *            the scene.
	 * @param event
	 *            the mouse event that triggered a potential selection change.
	 * @return the new selection or null if the selection should not change.
	 */
	Selection getSelection(Scene scene, SceneMouseEvent event);
}