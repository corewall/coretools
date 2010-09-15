package org.corewall.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

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
 * A panel component for rendering a Scene in a freeform fashion.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class FreeformPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener, KeyListener, Scene.ChangeListener,
		Scene.SelectionListener {

	private static final long serialVersionUID = 1L;
	private static final Dimension ZERO = new Dimension(0, 0);

	protected Scene scene = null;
	protected SceneEventHandler handler = null;
	protected Feedback feedback = null;
	protected Orientation orientation;
	protected Point dragStart = null;
	protected SelectionProvider selectionProvider = SelectionProvider.DEFAULT_PROVIDER;
	protected Point offset = new Point(0, 0);
	protected double zoom = 1.0;

	/**
	 * Create a new FreeformPanel.
	 * 
	 * @param scene
	 *            the scene.
	 * @param orientation
	 *            the orientation.
	 */
	public FreeformPanel(final Scene scene, final Orientation orientation) {
		this.orientation = orientation;
		setScene(scene);
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addKeyListener(this);
	}

	private Dimension dim(final Rectangle2D dim, final int padding) {
		if (orientation == Orientation.VERTICAL) {
			return new Dimension((int) Math.ceil(dim.getWidth()), (int) Math.ceil(dim.getHeight()));
		} else {
			return new Dimension((int) Math.ceil(dim.getHeight()), (int) Math.ceil(dim.getWidth()));
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

	@Override
	public Dimension getPreferredSize() {
		if (scene == null) {
			return ZERO;
		}

		scene.validate();
		return dim(scene.getContentSize(), 0);
	}

	/**
	 * Gets the Scene.
	 * 
	 * @return the Scene.
	 */
	public Scene getScene() {
		return scene;
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
		// handle key navigation
		Dimension size = getSize();
		switch (e.getKeyCode()) {
			case KeyEvent.VK_KP_LEFT:
			case KeyEvent.VK_LEFT:
				pan((int) (-0.15 * size.width), 0);
				break;
			case KeyEvent.VK_KP_RIGHT:
			case KeyEvent.VK_RIGHT:
				pan((int) (0.15 * size.width), 0);
				break;
			case KeyEvent.VK_KP_UP:
			case KeyEvent.VK_UP:
				pan(0, (int) (-0.15 * size.height));
				break;
			case KeyEvent.VK_KP_DOWN:
			case KeyEvent.VK_DOWN:
				pan(0, (int) (0.15 * size.height));
				break;
			case KeyEvent.VK_PLUS:
			case KeyEvent.VK_EQUALS:
				zoom(0.15);
				break;
			case KeyEvent.VK_MINUS:
			case KeyEvent.VK_UNDERSCORE:
				zoom(-0.15);
				break;
		}

		if (isFocusOwner() && isEditable()) {
			if (handler != null) {
				updateFeedback(handler.keyPressed(new SceneKeyEvent(this, Part.CONTENTS, e)));
			} else {
				updateFeedback(null);
			}
		}
	}

	public void keyReleased(final KeyEvent e) {
		if (isFocusOwner() && isEditable()) {
			if (handler != null) {
				updateFeedback(handler.keyReleased(new SceneKeyEvent(this, Part.CONTENTS, e)));
			} else {
				updateFeedback(null);
			}
		}
	}

	public void keyTyped(final KeyEvent e) {
		if (isFocusOwner() && isEditable()) {
			if (handler != null) {
				updateFeedback(handler.keyTyped(new SceneKeyEvent(this, Part.CONTENTS, e)));
			} else {
				updateFeedback(null);
			}
		}
	}

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
		// handle dragging
		Point p = e.getPoint();
		if (dragStart != null) {
			int dx = p.x - dragStart.x;
			int dy = p.y - dragStart.y;
			pan(dx, dy);
			repaint();
		}
		dragStart = p;

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
			int x = (orientation == Orientation.VERTICAL) ? e.getX() : (int) (getPreferredSize().getHeight() - e.getY());
			int y = (orientation == Orientation.VERTICAL) ? e.getY() : e.getX();
			SceneMouseEvent sme = new SceneMouseEvent(this, Part.CONTENTS, e);
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
		if (isFocusOwner()) {
			SceneMouseEvent sme = mouseEvent(e);
			setToolTipText(null);
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
		dragStart = e.getPoint();
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
		dragStart = null;
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

	public void mouseWheelMoved(final MouseWheelEvent e) {
		zoom(-0.05 * e.getWheelRotation());
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

		// clear our background
		g2d.setBackground(Color.white);
		g2d.setPaint(Color.white);
		g2d.fill(getVisibleRect());

		// setup the rotation if horizontal
		if (orientation == Orientation.HORIZONTAL) {
			g2d.rotate(-Math.PI / 2);
			g2d.translate(-scene.getContentSize().getWidth(), 0);
		}

		// get a graphics context
		GraphicsContext gfx = new GraphicsContext(new Java2DDriver(g2d, false, this));
		scene.setOrientation(orientation);

		// render the scene
		Dimension size = getSize();
		Rectangle2D sc = scene.getContentSize();
		Rectangle r;
		if (orientation == Orientation.VERTICAL) {
			r = new Rectangle(0, (int) sc.getY() - offset.y, size.width, size.height);
			gfx.pushTransform(AffineTransform.getTranslateInstance(offset.x, 0));
		} else {
			r = new Rectangle(0, (int) sc.getY() - offset.x, size.height, size.width);
			gfx.pushTransform(AffineTransform.getTranslateInstance(-offset.y, 0));
		}
		scene.renderContents(gfx, r);

		// render any feedback
		if (feedback != null) {
			g2d.translate(0, -scene.getContentSize().getY());
			feedback.renderFeedback(gfx);
		}
	}

	/**
	 * Pan the scene.
	 * 
	 * @param dx
	 *            the x delta in pixels.
	 * @param dy
	 *            the y delta in pixels.
	 */
	public void pan(final int dx, final int dy) {
		offset.translate(dx, dy);
		repaint();
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
			scene.setOrientation(orientation);
		}
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
			scene.setOrientation(orientation);
		} else {
			handler = null;
		}
		sceneChanged();
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

	/**
	 * Zoom the scene by the specified factor.
	 * 
	 * @param factor
	 *            the zoom factor.
	 */
	public void zoom(final double factor) {
		double oldScale = scene.getScalingFactor();
		double newScale = oldScale * (1 + factor);
		scene.setScalingFactor(newScale);
		if (orientation == Orientation.VERTICAL) {
			double before = -offset.getY() / oldScale + getSize().getHeight() / 2 / oldScale;
			double after = -offset.getY() / newScale + getSize().getHeight() / 2 / newScale;
			offset.translate(0, (int) Math.round((after - before) * newScale));
		} else {
			double before = -offset.getX() / oldScale + getSize().getWidth() / 2 / oldScale;
			double after = -offset.getX() / newScale + getSize().getWidth() / 2 / newScale;
			offset.translate((int) Math.round((after - before) * newScale), 0);
		}
		repaint();
	}
}