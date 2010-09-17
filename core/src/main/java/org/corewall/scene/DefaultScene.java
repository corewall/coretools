package org.corewall.scene;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.corewall.data.models.Unit;
import org.corewall.graphics.GraphicsContext;
import org.corewall.scene.edit.CommandStack;
import org.corewall.scene.event.DefaultSceneEventHandler;
import org.corewall.scene.event.SceneEventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.internal.Maps;

/**
 * The default {@link Scene} implementation.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultScene implements Scene {
	private static final String BORDERS_PROP = "borders";
	private static final String DEFAULT_BORDERS = Boolean.toString(true);
	private static final String DEFAULT_ORIENTATION = Orientation.VERTICAL.toString();
	private static final String DEFAULT_ORIGIN = Origin.TOP.toString();
	private static final String DEFAULT_SCALE = "1";
	private static final String DEFAULT_UNITS = Unit.METER.getAbbr();
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultScene.class);
	private static final String ORIENTATION_PROP = "orientation";
	private static final String ORIGIN_PROP = "origin";
	private static final String SCALE_PROP = "scale";
	private static final String UNITS_PROP = "units";

	protected List<ChangeListener> changeListeners = new CopyOnWriteArrayList<ChangeListener>();
	protected CommandStack commandStack = null;
	protected Map<Track, String> constraints = Maps.newHashMap();
	protected Rectangle contents = new Rectangle();
	protected int footerHeight = 36;
	private SceneEventHandler handler;
	protected int headerHeight = 36;
	protected Map<Track, Rectangle> layout = Maps.newHashMap();
	protected Map<String, String> parameters = Maps.newHashMap();
	protected int preferredWidth = -1;
	protected Selection selection = Selection.EMPTY;
	protected List<SelectionListener> selectionListeners = new CopyOnWriteArrayList<SelectionListener>();
	protected List<Track> tracks = new ArrayList<Track>();
	private AtomicBoolean valid = new AtomicBoolean(false);

	/**
	 * Create a new DefaultScene.
	 */
	public DefaultScene() {
		this(Origin.TOP);
	}

	/**
	 * Create a new DefaultScene with the specified origin.
	 * 
	 * @param origin
	 *            the origin.
	 */
	public DefaultScene(final Origin origin) {
		setOrigin(origin);
	}

	/**
	 * Create a new DefaultScene from the specified scene.
	 * 
	 * @param scene
	 *            the scene.
	 */
	public DefaultScene(final Scene scene) {
		setOrigin(scene.getOrigin());
		setPreferredWidth(scene.getPreferredWidth());
		setScalingFactor(scene.getScalingFactor());
		for (Track t : scene.getTracks()) {
			try {
				addTrack(t.getClass().newInstance(), scene.getTrackConstraints(t));
			} catch (InstantiationException e) {
				LOGGER.error("Unable to clone track {}: {}", t.getClass().getName(), e.getMessage());
			} catch (IllegalAccessException e) {
				LOGGER.error("Unable to clone track {}: {}", t.getClass().getName(), e.getMessage());
			}
		}
		parameters.putAll(scene.getParameters());
	}

	public void addChangeListener(final ChangeListener l) {
		if (!changeListeners.contains(l)) {
			changeListeners.add(l);
		}
	}

	public void addSelectionListener(final SelectionListener l) {
		if (!selectionListeners.contains(l)) {
			selectionListeners.add(l);
		}
	}

	public void addTrack(final Track track, final String constraints) {
		tracks.add(track);
		track.setScene(this);
		if (constraints != null) {
			this.constraints.put(track, constraints);
		}
		layout.put(track, new Rectangle());
		invalidate();
	}

	protected SceneEventHandler createHandler() {
		return new DefaultSceneEventHandler(this, layout);
	}

	public Object findAt(final Point2D screen, final Part part) {
		Track track = findTrack(screen, part);
		if (track == null) {
			return this;
		} else {
			Object target = track.findAt(screen, part);
			return target == null ? track : target;
		}
	}

	/**
	 * Finds the track at the specified point and part.
	 * 
	 * @param screen
	 *            the point in screen coordinates.
	 * @param part
	 *            the scene part.
	 * @return the track or null if not found.
	 */
	public Track findTrack(final Point2D screen, final Part part) {
		for (Entry<Track, Rectangle> e : layout.entrySet()) {
			Rectangle r = e.getValue();
			if ((screen.getX() >= r.getMinX()) && (screen.getX() <= r.getMaxX())) {
				return e.getKey();
			}
		}
		return null;
	}

	public CommandStack getCommandStack() {
		return commandStack;
	}

	public Rectangle2D getContentSize() {
		validate();
		return contents;
	}

	public SceneEventHandler getEventHandler() {
		if (handler == null) {
			handler = new DefaultSceneEventHandler(this, layout);
		}
		return handler;
	}

	public Rectangle2D getFooterSize() {
		validate();
		return new Rectangle2D.Double(0, 0, contents.width, footerHeight);
	}

	public Rectangle2D getHeaderSize() {
		validate();
		return new Rectangle2D.Double(0, 0, contents.width, headerHeight);
	}

	public Orientation getOrientation() {
		if (getParameter(ORIENTATION_PROP, DEFAULT_ORIENTATION).equals(DEFAULT_ORIENTATION)) {
			return Orientation.VERTICAL;
		} else {
			return Orientation.HORIZONTAL;
		}
	}

	public Origin getOrigin() {
		if (Origin.TOP.name().equalsIgnoreCase(getParameter(ORIGIN_PROP, DEFAULT_ORIGIN))) {
			return Origin.TOP;
		} else {
			return Origin.BASE;
		}
	}

	public String getParameter(final String name, final String defaultValue) {
		String value = parameters.get(name);
		if ((value == null) || "".equals(value.trim())) {
			return defaultValue;
		} else {
			return value;
		}
	}

	public ImmutableMap<String, String> getParameters() {
		return ImmutableMap.copyOf(parameters);
	}

	public double getPreferredWidth() {
		return preferredWidth;
	}

	public double getScalingFactor() {
		return parse(getParameter(SCALE_PROP, DEFAULT_SCALE));
	}

	public Unit getSceneUnits() {
		return Unit.get(getParameter(UNITS_PROP, DEFAULT_UNITS));
	}

	public Selection getSelection() {
		return selection;
	}

	public String getTrackConstraints(final Track track) {
		return constraints.get(track);
	}

	public ImmutableList<Track> getTracks() {
		return ImmutableList.copyOf(tracks);
	}

	public void invalidate() {
		if (valid.getAndSet(false)) {
			for (ChangeListener l : changeListeners) {
				l.sceneChanged();
			}
		}
	}

	protected void layoutTracks() {
		int width = 0;
		int minContent = Integer.MAX_VALUE;
		int maxContent = Integer.MIN_VALUE;
		int expandable = 0;

		// Step 1: calculate width from constraints or default track width
		for (Track t : tracks) {
			Rectangle2D size = t.getContentSize();
			Rectangle lr = layout.get(t);

			// get our constraint
			String constraint = constraints.get(t);
			if ((constraint != null) && (constraint.indexOf('*') > -1)) {
				expandable++;
			}

			// set our coordinates
			lr.x = width;
			lr.y = 0;

			// calculate our width
			int w = parseConstraint(constraint);
			if (w > 0) {
				lr.width = w;
			} else {
				lr.width = (int) Math.ceil(size.getWidth());
			}
			width += lr.width;

			if (size.getHeight() >= 0) {
				minContent = (int) Math.min(minContent, Math.floor(size.getMinY()));
				maxContent = (int) Math.max(maxContent, Math.ceil(size.getMaxY()));
			}
		}

		// adjust the track widths to fit the preferred width
		if ((preferredWidth > 0) && (expandable > 0)) {
			int adjust = preferredWidth - width;
			if (adjust >= 0) {
				adjust = adjust / expandable;
			} else {
				adjust = adjust / tracks.size();
			}
			width = 0;

			// adjust the tracks
			for (Track t : tracks) {
				Rectangle lr = layout.get(t);
				String constraint = constraints.get(t);

				lr.x = width;
				if (adjust < 0) {
					lr.width = lr.width + adjust;
				} else if ((constraint != null) && (constraint.indexOf('*') > -1)) {
					lr.width = lr.width + adjust;
				}
				width += lr.width;
			}
			width = preferredWidth;
		}

		if (minContent == Integer.MAX_VALUE) {
			minContent = 0;
		}
		if (maxContent == Integer.MIN_VALUE) {
			maxContent = 0;
		}

		contents.width = width;
		contents.y = minContent;
		contents.height = maxContent - minContent;
	}

	protected double parse(final String number) {
		try {
			return Double.parseDouble(number);
		} catch (final NumberFormatException nfe) {
			return -1;
		}
	}

	protected int parseConstraint(final String constraint) {
		if ((constraint == null) || constraint.contains("*")) {
			return -1;
		} else if (constraint.contains("in")) {
			return (int) Math.ceil(parse(constraint.replace("in", "").trim()) * 72);
		} else {
			return (int) Math.ceil(parse(constraint));
		}
	}

	public void removeChangeListener(final ChangeListener l) {
		changeListeners.remove(l);
	}

	public void removeSelectionListener(final SelectionListener l) {
		selectionListeners.remove(l);
	}

	/**
	 * Remove a track from the scene.
	 * 
	 * @param track
	 *            the track to remove.
	 */
	public void removeTrack(final Track track) {
		if (tracks.remove(track)) {
			track.setScene(null);
			constraints.remove(track);
			layout.remove(track);
			invalidate();
		}
	}

	public void renderContents(final GraphicsContext graphics, final Rectangle2D clip) {
		boolean isHorizontal = getOrientation() == Orientation.HORIZONTAL;
		int x = 0;
		Rectangle2D r = (clip == null) ? contents : clip;
		graphics.pushTransform(AffineTransform.getTranslateInstance((isHorizontal ? 1 : 0), -r.getMinY()));
		for (Track t : tracks) {
			int w = layout.get(t).width;
			Rectangle bounds = new Rectangle(x, (int) Math.floor(r.getY()), w, (int) Math.ceil(r.getHeight()));
			graphics.setClip(new Rectangle(x - 1, (int)
					Math.floor(r.getY()) - 1, w + 2, (int) Math.ceil(r
							.getHeight()) + 2));
			graphics.pushState();
			t.renderContents(graphics, bounds);
			graphics.popState();
			graphics.setClip(null);
			if (shouldRenderBorders()) {
				graphics.drawRectangle(bounds);
			}
			x += w;
		}
		graphics.popTransform();
	}

	public void renderFooter(final GraphicsContext graphics) {
		int x = 0;
		for (Track t : tracks) {
			int w = layout.get(t).width;
			Rectangle bounds = new Rectangle(x, 1, w, footerHeight - 1);
			graphics.setClip(bounds);
			graphics.pushState();
			t.renderFooter(graphics, bounds);
			graphics.popState();
			graphics.setClip(null);
			if (shouldRenderBorders()) {
				graphics.drawRectangle(bounds);
			}
			x += w;
		}
	}

	public void renderHeader(final GraphicsContext graphics) {
		int x = 0;
		for (Track t : tracks) {
			int w = layout.get(t).width;
			Rectangle bounds = new Rectangle(x, 0, w, headerHeight - 1);
			graphics.setClip(bounds);
			graphics.pushState();
			t.renderHeader(graphics, bounds);
			graphics.popState();
			graphics.setClip(null);
			if (shouldRenderBorders()) {
				graphics.drawRectangle(bounds);
			}
			x += w;
		}
	}

	public void setCommandStack(final CommandStack commandStack) {
		this.commandStack = commandStack;
	}

	public void setOrientation(final Orientation orientation) {
		setParameter(ORIENTATION_PROP, orientation.toString());
	}

	/**
	 * Sets the origin of this scene.
	 * 
	 * @param origin
	 *            the origin.
	 */
	public void setOrigin(final Origin origin) {
		setParameter(ORIGIN_PROP, origin.toString());
		invalidate();
	}

	public void setParameter(final String name, final String value) {
		if ((value == null) || "".equals(value.trim())) {
			parameters.remove(name);
		} else {
			parameters.put(name, value);
		}
	}

	public void setPreferredWidth(final double width) {
		preferredWidth = (int) Math.ceil(width);
		invalidate();
	}

	/**
	 * Sets whether this scene should render the scene and track borders.
	 * 
	 * @param borders
	 *            true if rendering borders, false otherwise.
	 */
	public void setRenderBorders(final boolean borders) {
		setParameter(BORDERS_PROP, "" + borders);
	}

	public void setScalingFactor(final double scalingFactor) {
		setParameter(SCALE_PROP, "" + scalingFactor);
		invalidate();
	}

	public void setSceneUnits(final Unit unit) {
		setParameter(UNITS_PROP, unit.getAbbr());
		invalidate();
	}

	public void setSelection(final Selection selection) {
		Selection old = this.selection;
		this.selection = (selection == null) ? Selection.EMPTY : selection;
		if (!old.equals(this.selection)) {
			LOGGER.debug("Selection changed {}", this.selection);
			for (SelectionListener l : selectionListeners) {
				LOGGER.debug("Notifying {} of selection change", l);
				l.selectionChanged(this.selection);
			}
		}
	}

	/**
	 * Checks whether this scene is rendering scene and track borders.
	 * 
	 * @return true if rendering borders, false otherwise.
	 */
	public boolean shouldRenderBorders() {
		return Boolean.parseBoolean(getParameter(BORDERS_PROP, DEFAULT_BORDERS));
	}

	public void validate() {
		if (!valid.getAndSet(true)) {
			layoutTracks();
		}
	}
}
