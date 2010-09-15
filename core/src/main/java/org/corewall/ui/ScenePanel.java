/*
 * Copyright (c) Josh Reed, 2009.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.corewall.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.corewall.graphics.GraphicsContext;
import org.corewall.graphics.driver.Java2DDriver;
import org.corewall.scene.Orientation;
import org.corewall.scene.Part;
import org.corewall.scene.Scene;
import org.corewall.scene.Selection;
import org.corewall.scene.edit.CommandStack;
import org.corewall.scene.event.Feedback;
import org.corewall.scene.event.SceneEventHandler;
import org.corewall.scene.event.SceneKeyEvent;
import org.corewall.scene.event.SceneMouseEvent;

/**
 * A panel component for rendering a Scene.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ScenePanel extends JPanel implements MouseListener, MouseMotionListener, KeyListener,
		Scene.ChangeListener, Scene.SelectionListener, AdjustmentListener, Scrollable {
	private static final long serialVersionUID = 1L;
	private static final Dimension ZERO = new Dimension(0, 0);

	protected Scene scene = null;
	protected Part part = Part.CONTENTS;
	protected SceneEventHandler handler = null;
	protected Feedback feedback = null;
	protected Orientation orientation;
	protected int padding = 0;
	protected int last = 0;
	protected int height = 0;
	protected AtomicBoolean repainting = new AtomicBoolean(false);
	protected SelectionProvider selectionProvider = SelectionProvider.DEFAULT_PROVIDER;
	protected int scrollUnits = 20;

	/**
	 * Create a new ScenePanel.
	 */
	public ScenePanel() {
		this(null, Part.CONTENTS, Orientation.VERTICAL, 0);
	}

	/**
	 * Create a new ScenePanel.
	 * 
	 * @param scene
	 *            the scene.
	 * @param part
	 *            the part.
	 * @param orientation
	 *            the orientation.
	 */
	public ScenePanel(final Scene scene, final Part part, final Orientation orientation) {
		this(scene, part, orientation, 0);
	}

	/**
	 * Create a new ScenePanel.
	 * 
	 * @param scene
	 *            the scene.
	 * @param part
	 *            the part.
	 * @param orientation
	 *            the orientation.
	 * @param padding
	 *            the amount of padding to display in scene units.
	 */
	public ScenePanel(final Scene scene, final Part part, final Orientation orientation, final int padding) {
		this.part = part;
		this.orientation = orientation;
		this.padding = padding;
		setScene(scene);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	public void adjustmentValueChanged(final AdjustmentEvent e) {
		if (!repainting.getAndSet(true)) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					repaint(getVisibleRect());
					repainting.set(false);
				}
			});
		}
	}

	private Dimension dim(final Rectangle2D dim, final int padding) {
		if (orientation == Orientation.VERTICAL) {
			return new Dimension((int) Math.ceil(dim.getWidth()), (int) Math.ceil(dim.getHeight() + padding));
		} else {
			return new Dimension((int) Math.ceil(dim.getHeight() + padding), (int) Math.ceil(dim.getWidth()));
		}
	}

	protected Cursor getCursor(final int type) {
		if (orientation == Orientation.HORIZONTAL) {
			switch (type) {
				case Cursor.N_RESIZE_CURSOR:
					return new Cursor(Cursor.W_RESIZE_CURSOR);
				case Cursor.NW_RESIZE_CURSOR:
					return new Cursor(Cursor.SW_RESIZE_CURSOR);
				case Cursor.NE_RESIZE_CURSOR:
					return new Cursor(Cursor.NW_RESIZE_CURSOR);
				case Cursor.E_RESIZE_CURSOR:
					return new Cursor(Cursor.N_RESIZE_CURSOR);
				case Cursor.S_RESIZE_CURSOR:
					return new Cursor(Cursor.E_RESIZE_CURSOR);
				case Cursor.SW_RESIZE_CURSOR:
					return new Cursor(Cursor.SE_RESIZE_CURSOR);
				case Cursor.SE_RESIZE_CURSOR:
					return new Cursor(Cursor.NE_RESIZE_CURSOR);
				case Cursor.W_RESIZE_CURSOR:
					return new Cursor(Cursor.S_RESIZE_CURSOR);
				default:
					return new Cursor(type);
			}
		} else {
			return new Cursor(type);
		}
	}

	/**
	 * Gets the orientation.
	 * 
	 * @return the orientation.
	 */
	public Orientation getOrientation() {
		return orientation;
	}

	/**
	 * Gets the padding of this panel in pixels.
	 * 
	 * @return the padding.
	 */
	public int getPadding() {
		return padding;
	}

	/**
	 * Gets the scene part.
	 * 
	 * @return the scene part.
	 */
	public Part getPart() {
		return part;
	}

	public Dimension getPreferredScrollableViewportSize() {
		return getPreferredSize();
	}

	@Override
	public Dimension getPreferredSize() {
		if (scene == null) {
			return ZERO;
		}

		scene.validate();
		switch (part) {
			case HEADER:
				return dim(scene.getHeaderSize(), 0);
			case CONTENTS:
				return dim(scene.getContentSize(), padding);
			case FOOTER:
				return dim(scene.getFooterSize(), 0);
			default:
				return ZERO;
		}
	}

	/**
	 * Gets the Scene.
	 * 
	 * @return the Scene.
	 */
	public Scene getScene() {
		return scene;
	}

	public int getScrollableBlockIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
		if (orientation == SwingConstants.HORIZONTAL) {
			return visibleRect.width - scrollUnits;
		} else {
			return visibleRect.height - scrollUnits;
		}
	}

	public boolean getScrollableTracksViewportHeight() {
		return orientation == Orientation.HORIZONTAL;
	}

	public boolean getScrollableTracksViewportWidth() {
		return orientation == Orientation.VERTICAL;
	}

	public int getScrollableUnitIncrement(final Rectangle visibleRect, final int orientation, final int direction) {
		// Get the current position.
		int currentPosition = 0;
		if (orientation == SwingConstants.HORIZONTAL) {
			currentPosition = visibleRect.x;
		} else {
			currentPosition = visibleRect.y;
		}

		// Return the number of pixels between currentPosition
		// and the nearest tick mark in the indicated direction.
		if (direction < 0) {
			int newPosition = currentPosition - (currentPosition / scrollUnits) * scrollUnits;
			return (newPosition == 0) ? scrollUnits : newPosition;
		} else {
			return ((currentPosition / scrollUnits) + 1) * scrollUnits - currentPosition;
		}
	}

	protected String getToolTip(final SceneMouseEvent sme) {
		return "";
		/*
		 * LabelProvider labelProvider = scene.getAdapter(LabelProvider.class);
		 * String text = (labelProvider == null ? null : labelProvider.getLabel(
		 * new Point2D.Double(sme.getX(), sme.getY()), sme.getTarget())); if
		 * (text == null) { return null; } else if (text.contains("\n") ||
		 * text.contains("/>") || text.contains("</")) { return "<html>" +
		 * text.replace("\n", "<br/>") + "</html>"; } else { return text; }
		 */
	}

	protected boolean isEditable() {
		if (scene == null) {
			return false;
		} else {
			CommandStack edit = scene.getCommandStack();
			return ((edit != null) && edit.canExecute());
		}
	}

	public void keyPressed(final KeyEvent e) {
		if (isFocusOwner() && isEditable()) {
			if (handler != null) {
				updateFeedback(handler.keyPressed(new SceneKeyEvent(this, part, e)));
			} else {
				updateFeedback(null);
			}
		}
	}

	public void keyReleased(final KeyEvent e) {
		if (isFocusOwner() && isEditable()) {
			if (handler != null) {
				updateFeedback(handler.keyReleased(new SceneKeyEvent(this, part, e)));
			} else {
				updateFeedback(null);
			}
		}
	}

	public void keyTyped(final KeyEvent e) {
		if (isFocusOwner() && isEditable()) {
			if (handler != null) {
				updateFeedback(handler.keyTyped(new SceneKeyEvent(this, part, e)));
			} else {
				updateFeedback(null);
			}
		}
	}

	/*
	 * public void modelAdded(final Model model) { if ((part ==
	 * ScenePart.CONTENTS) && (scene != null) && (scene.getOrigin() ==
	 * Origin.BASE)) { int diff = (int) (scene.getContentSize().getHeight() -
	 * height); Rectangle visible = getVisibleRect(); if (orientation ==
	 * Orientation.VERTICAL) { scrollRectToVisible(new Rectangle(visible.x, last
	 * + diff, visible.width, visible.height / 2)); } else {
	 * scrollRectToVisible(new Rectangle(last + diff, visible.y, visible.width /
	 * 2, visible.height)); } } }
	 * 
	 * public void modelRemoved(final Model model) { // do nothing }
	 * 
	 * public void modelUpdated(final Model model) { // do nothing }
	 */

	public void mouseClicked(final MouseEvent e) {
		requestFocusInWindow();
		if (isFocusOwner() && isEditable()) {
			SceneMouseEvent sme;
			if ((handler != null) && ((sme = mouseEvent(e)) != null)) {
				updateFeedback(handler.mouseClicked(sme));
			} else {
				updateFeedback(null);
			}
		}

	}

	public void mouseDragged(final MouseEvent e) {
		requestFocusInWindow();
		if (isFocusOwner() && isEditable()) {
			SceneMouseEvent sme;
			if ((handler != null) && ((sme = mouseEvent(e)) != null)) {
				updateFeedback(handler.mouseDragged(sme));
			} else {
				updateFeedback(null);
			}
		}
	}

	public void mouseEntered(final MouseEvent e) {
		requestFocusInWindow();
		updateFeedback(null);
	}

	private SceneMouseEvent mouseEvent(final MouseEvent e) {
		if (scene == null) {
			return null;
		} else {
			int x = (orientation == Orientation.VERTICAL) ? e.getX()
					: (int) (getPreferredSize().getHeight() - e.getY());
			int y = (orientation == Orientation.VERTICAL) ? e.getY() : e.getX();
			SceneMouseEvent sme = new SceneMouseEvent(this, part, e);
			sme.setX(x);
			sme.setY(y + (int) Math.ceil(scene.getContentSize().getMinY()));
			return sme;
		}
	}

	public void mouseExited(final MouseEvent e) {
		updateFeedback(null);
	}

	public void mouseMoved(final MouseEvent e) {
		requestFocusInWindow();
		if (scene != null) {
			last = (orientation == Orientation.VERTICAL ? e.getY() : e.getX());
			height = (int) scene.getContentSize().getHeight();
		}
		if (isFocusOwner()) {
			SceneMouseEvent sme = mouseEvent(e);
			setToolTipText((sme == null ? null : getToolTip(sme)));
			if (isEditable()) {
				if ((handler != null) && (sme != null)) {
					updateFeedback(handler.mouseMoved(sme));
				} else {
					updateFeedback(null);
				}
			}
		}
	}

	public void mousePressed(final MouseEvent e) {
		requestFocusInWindow();
		if (isFocusOwner()) {
			SceneMouseEvent sme = mouseEvent(e);
			if ((sme != null) && (selectionProvider != null)) {
				Selection selection = selectionProvider.getSelection(scene, sme);
				if ((selection != null) && (scene != null)) {
					scene.setSelection(selection);
				}
			}
			if (isEditable()) {
				if ((handler != null) && ((sme) != null)) {
					updateFeedback(handler.mousePressed(sme));
				} else {
					updateFeedback(null);
				}
			}
		}
	}

	public void mouseReleased(final MouseEvent e) {
		requestFocusInWindow();
		if (isFocusOwner() && isEditable()) {
			SceneMouseEvent sme;
			if ((handler != null) && ((sme = mouseEvent(e)) != null)) {
				updateFeedback(handler.mouseReleased(sme));
			} else {
				updateFeedback(null);
			}
		}
	}

	@Override
	protected void paintComponent(final Graphics g) {
		if (scene == null) {
			return;
		}

		// validate the scene
		scene.validate();

		// get our graphics
		final Graphics2D g2d = (Graphics2D) g;

		// clear our clip
		g2d.setBackground(Color.white);
		g2d.setPaint(Color.white);
		g2d.fill(getVisibleRect());

		// get a graphics context
		if (orientation == Orientation.HORIZONTAL) {
			g2d.rotate(-Math.PI / 2);
			g2d.translate(-scene.getContentSize().getWidth(), 0);
		}
		GraphicsContext gfx = new GraphicsContext(new Java2DDriver(g2d, false, this));
		switch (part) {
			case HEADER:
				scene.renderHeader(gfx);
				break;
			case CONTENTS:
				Dimension size = getPreferredSize();
				Rectangle2D sc = scene.getContentSize();
				if (orientation == Orientation.VERTICAL) {
					scene.renderContents(gfx, new Rectangle(0, (int) sc.getY(), size.width, size.height));
				} else {
					scene.renderContents(gfx, new Rectangle(0, (int) sc.getY(), size.height, size.width));
				}
				break;
			case FOOTER:
				scene.renderFooter(gfx);
				break;
		}

		// render any feedback
		if (feedback != null) {
			g2d.translate(0, -scene.getContentSize().getY());
			feedback.renderFeedback(gfx);
		}
	}

	public void sceneChanged() {
		invalidate();
		Component parent = getParent();
		if (parent != null) {
			parent.invalidate();
			parent.validate();
			parent.repaint();
		}
	}

	public void selectionChanged(final Selection selection) {
		sceneChanged();
	}

	/**
	 * Sets the orientation of this panel.
	 * 
	 * @param orientation
	 *            the new orientation.
	 */
	public void setOrientation(final Orientation orientation) {
		this.orientation = orientation;
		if (scene != null) {
			scene.setParameter("orientation", orientation.toString().toLowerCase());
		}
		sceneChanged();
	}

	/**
	 * Sets the padding of this panel in pixels.
	 * 
	 * @param padding
	 *            the padding.
	 */
	public void setPadding(final int padding) {
		this.padding = padding;
		sceneChanged();
	}

	/**
	 * Sets the scene part.
	 * 
	 * @param part
	 *            the part.
	 */
	public void setPart(final Part part) {
		this.part = part;
		sceneChanged();
	}

	/**
	 * Sets the Scene.
	 * 
	 * @param scene
	 *            the scene.
	 */
	public void setScene(final Scene scene) {
		if (this.scene != null) {
			this.scene.removeChangeListener(this);
			this.scene.removeSelectionListener(this);
		}
		this.scene = scene;
		if (scene != null) {
			handler = scene.getEventHandler();
			scene.addChangeListener(this);
			scene.addSelectionListener(this);
			scene.setParameter("orientation", orientation.toString().toLowerCase());
		} else {
			handler = null;
		}
		sceneChanged();
	}

	/**
	 * Sets the scroll unit increment in pixels.
	 * 
	 * @param pixels
	 *            the scroll unit increment.
	 */
	public void setScrollUnitIncrement(final int pixels) {
		scrollUnits = pixels;
	}

	/**
	 * Sets the selection provider.
	 * 
	 * @param selectionProvider
	 *            the selection provider.
	 */
	public void setSelectionProvider(final SelectionProvider selectionProvider) {
		this.selectionProvider = selectionProvider;
	}

	protected void updateFeedback(final Feedback feedback) {
		this.feedback = feedback;
		if ((feedback == null) || !isEditable()) {
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		} else {
			setCursor(getCursor(feedback.getCursorType()));
		}
		sceneChanged();
	}
}
