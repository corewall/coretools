package org.corewall.scene;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Map;

import org.corewall.data.models.Length;
import org.corewall.data.models.Unit;
import org.corewall.graphics.GraphicsContext;
import org.corewall.scene.event.SceneEventHandler;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.internal.Maps;

/**
 * An abstract implementation of the {@link Track} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * 
 * @param <T>
 *            the track's model type.
 */
public abstract class AbstractTrack<T> implements Track {
	protected List<T> models = Lists.newLinkedList();
	protected Map<String, String> parameters = Maps.newHashMap();
	protected Scene scene = null;

	/**
	 * Adds the specified model to this track.
	 * 
	 * @param model
	 *            the model.
	 */
	public void add(final T model) {
		if (models.add(model)) {
			invalidate();
		}
	}

	/**
	 * Adds the specified list of models to this track.
	 * 
	 * @param models
	 *            the list of models.
	 */
	public void addAll(final List<T> models) {
		if (this.models.addAll(models)) {
			invalidate();
		}
	}

	public Object findAt(final Point2D screen, final Part part) {
		return null;
	}

	public abstract Rectangle2D getContentSize();

	public SceneEventHandler getEventHandler() {
		return null;
	}

	public String getParameter(final String name, final String defaultValue) {
		String value = parameters.get(name);
		return ((value == null) || "".equals(value.trim()) ? defaultValue : value);
	}

	public ImmutableMap<String, String> getParameters() {
		return ImmutableMap.copyOf(parameters);
	}

	public Scene getScene() {
		return scene;
	}

	protected void invalidate() {
		if (scene != null) {
			scene.invalidate();
		}
	}

	/**
	 * Compares two doubles for equality taking into account floating point
	 * issues.
	 * 
	 * @param a
	 *            the first double.
	 * @param b
	 *            the second double.
	 * @return true if equals, false otherwise.
	 */
	protected boolean preciseEquals(final double a, final double b) {
		return Math.abs(a - b) < 1E-6;
	}

	/**
	 * Compares two doubles taking into account floating point issues.
	 * 
	 * @param a
	 *            the first double.
	 * @param b
	 *            the second double.
	 * @return true if a is greater than b, false otherwise.
	 */
	protected boolean preciseGreaterThan(final double a, final double b) {
		return a - b > 1E-6;
	}

	/**
	 * Compares two doubles taking into account floating point issues.
	 * 
	 * @param a
	 *            the first double.
	 * @param b
	 *            the second double.
	 * @return true if a is less than b, false otherwise.
	 */
	protected boolean preciseLessThan(final double a, final double b) {
		return b - a > 1E-6;
	}

	/**
	 * Remove the specified model from this track.
	 * 
	 * @param model
	 *            the model.
	 */
	public void remove(final T model) {
		if (models.remove(model)) {
			invalidate();
		}
	}

	/**
	 * Removes all of the models in the specified list from this track.
	 * 
	 * @param models
	 *            the list of models to remove.
	 */
	public void removeAll(final List<T> models) {
		if (this.models.removeAll(models)) {
			invalidate();
		}
	}

	public abstract void renderContents(final GraphicsContext graphics, final Rectangle2D bounds);

	public void renderFooter(final GraphicsContext graphics, final Rectangle2D bounds) {
		// override to draw a header
	}

	public void renderHeader(final GraphicsContext graphics, final Rectangle2D bounds) {
		// override to draw a header
	}

	/**
	 * Scales the specified double value. This value is assumed to already be in
	 * scene units.
	 * 
	 * @param value
	 *            the value.
	 * @return the scaled value.
	 * 
	 * @see Scene#getSceneUnits()
	 * @see Scene#getScalingFactor()
	 */
	protected double scale(final double value) {
		return value * scene.getScalingFactor();
	}

	/**
	 * Scales the specified {@link Length}. The length will first be converted
	 * to scene units and then scaled.
	 * 
	 * @param length
	 *            the length.
	 * @return the scaled value.
	 * 
	 * @see Scene#getSceneUnits()
	 * @see Scene#getScalingFactor()
	 */
	protected double scale(final Length length) {
		return scale(length.to(scene.getSceneUnits()).getValue().doubleValue());
	}

	public void setParameter(final String name, final String value) {
		if ((value == null) || "".equals(value.trim())) {
			parameters.remove(name);
		} else {
			parameters.put(name, value);
		}
	}

	public void setScene(final Scene scene) {
		this.scene = scene;
	}

	/**
	 * Converts the specified y-value to scene units taking into account the
	 * scene origin.
	 * 
	 * @param value
	 *            the y-value.
	 * @return the scene units.
	 */
	protected double toScene(final double value) {
		if (scene.getOrigin() == Origin.TOP) {
			return value / scene.getScalingFactor();
		} else {
			Rectangle2D r = scene.getContentSize();
			return ((r.getMaxY() - value) / scene.getScalingFactor()) + (r.getMinY() / scene.getScalingFactor());
		}
	}

	/**
	 * Converts the specified y-value to screen units taking into account the
	 * scene origin.
	 * 
	 * @param value
	 *            the y-value.
	 * @return the screen units.
	 */
	protected double toScreen(final double value) {
		if (scene.getOrigin() == Origin.TOP) {
			return value * scene.getScalingFactor();
		} else {
			Rectangle2D r = scene.getContentSize();
			return r.getMaxY() - (value * scene.getScalingFactor()) + r.getMinY();
		}
	}

	/**
	 * Converts the specified Length to screen units taking into account the
	 * scene origin.
	 * 
	 * @param length
	 *            the length.
	 * @return the screen units value.
	 */
	protected double toScreen(final Length length) {
		return toScreen(length.to(scene.getSceneUnits()).getValue().doubleValue());
	}

	protected double[] toScreen(final Length l1, final Length l2) {
		double s1 = toScreen(l1);
		double s2 = toScreen(l2);
		return new double[] { Math.min(s1, s2), Math.max(s1, s2) };
	}

	protected Rectangle2D translate(final Rectangle2D r, final double dx, final double dy) {
		return new Rectangle2D.Double(r.getX() + dx, r.getY() + dy, r.getWidth(), r.getHeight());
	}

	/**
	 * Unscales the specified value to scene units.
	 * 
	 * @param value
	 *            the value.
	 * @return the unscaled value.
	 * 
	 * @see Scene#getSceneUnits()
	 * @see Scene#getScalingFactor()
	 */
	protected double unscale(final double value) {
		return value / scene.getScalingFactor();
	}

	/**
	 * Unscales the specified value to the specified units.
	 * 
	 * @param value
	 *            the value.
	 * @param unit
	 *            the desired units.
	 * @return the unscaled value as a {@link Length}.
	 * 
	 * @see Scene#getSceneUnits()
	 * @see Scene#getScalingFactor()
	 */
	protected Length unscale(final double value, final Unit unit) {
		return Length.valueOf(unscale(value), scene.getSceneUnits()).to(unit);
	}
}
