package org.corewall.geology.models;

import java.text.DecimalFormat;
import java.util.Map;

import org.corewall.data.Length;
import org.corewall.data.Model;
import org.corewall.data.Unit;

import com.google.common.collect.Maps;

/**
 * The Datum class holds the x and y values for a data point.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class XYDatum implements Comparable<XYDatum>, Model {
	private static final DecimalFormat NUM = new DecimalFormat("0.####");

	protected double x, y;

	/**
	 * Creates a new XYDatum.
	 * 
	 * @param x
	 *            the x value.
	 * @param y
	 *            the y value.
	 */
	public XYDatum(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Create a new XYDatum from the specified properties map.
	 * 
	 * @param properties
	 *            the properties.
	 */
	public XYDatum(final Map<String, String> properties) {
		String x = properties.get("x");
		if ((x == null) || "".equals(x.trim())) {
			throw new RuntimeException("Invalid 'x' key: '" + x + "'");
		}

		String y = properties.get("y");
		if ((y == null) || "".equals(y.trim())) {
			throw new RuntimeException("Invalid 'y' key: '" + x + "'");
		}

		this.x = Double.valueOf(x);
		this.y = Double.valueOf(y);
	}

	/**
	 * Implemented for Comparable.
	 */
	public int compareTo(final XYDatum o) {
		return Double.compare(x, o.x);
	}

	/**
	 * Converts the x value of this datum.
	 * 
	 * @param from
	 *            the current units.
	 * @param to
	 *            the desired units.
	 */
	public void convertX(final Unit from, final Unit to) {
		x = Length.valueOf(x, from).to(to).getValue().doubleValue();
	}

	/**
	 * Converts the y value of this datum.
	 * 
	 * @param from
	 *            the current units.
	 * @param to
	 *            the desired units.
	 */
	public void convertY(final Unit from, final Unit to) {
		y = Length.valueOf(y, from).to(to).getValue().doubleValue();
	}

	/**
	 * Gets the x value of this datum.
	 * 
	 * @return the x value.
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the y value of this datum.
	 * 
	 * @return the y value.
	 */
	public double getY() {
		return y;
	}

	/**
	 * Gets this datum as an array of doubles.
	 * 
	 * @return the x and y values of this
	 */
	public double[] toArray() {
		return new double[] { x, y };
	}

	public Map<String, String> toMap() {
		Map<String, String> map = Maps.newHashMapWithExpectedSize(2);
		map.put("x", NUM.format(x));
		map.put("y", NUM.format(y));
		return map;
	}

	@Override
	public String toString() {
		return "(" + NUM.format(x) + ", " + NUM.format(y) + ")";
	}
}
