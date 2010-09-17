package org.corewall.data.models;

import java.math.BigDecimal;
import java.text.DecimalFormat;


/**
 * A length has a value and a unit.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Length implements Comparable<Length> {
	private static final DecimalFormat NUM = new DecimalFormat("0.####");

	/**
	 * Create a new Length from the specified BigDecimal and unit.
	 * 
	 * @param value
	 *            the value.
	 * @param unit
	 *            the unit.
	 * @return the Length.
	 */
	public static Length valueOf(final BigDecimal value, final Unit unit) {
		return new Length(value, unit);
	}

	/**
	 * Create a new Length from the specified double value and unit.
	 * 
	 * @param value
	 *            the value.
	 * @param unit
	 *            the unit.
	 * @return the Length.
	 */
	public static Length valueOf(final double value, final Unit unit) {
		return new Length(BigDecimal.valueOf(value), unit);
	}

	/**
	 * Create a new Length from the specified long value and unit.
	 * 
	 * @param value
	 *            the value.
	 * @param unit
	 *            the unit.
	 * @return the Length.
	 */
	public static Length valueOf(final long value, final Unit unit) {
		return new Length(BigDecimal.valueOf(value), unit);
	}

	/**
	 * Create a new Length from the specified string.
	 * 
	 * @param value
	 *            the value.
	 * @return the Length.
	 */
	public static Length valueOf(final String value) {
		if (value.endsWith("in") || value.endsWith("ft") || value.endsWith("yd") || value.endsWith("mm")
				|| value.endsWith("cm") || value.endsWith("km")) {
			return Length.valueOf(value.substring(0, value.length() - 2).trim(), Unit
					.get(value.substring(value.length() - 2)));
		} else if (value.endsWith("m")) {
			return Length.valueOf(value.substring(0, value.length() - 1).trim(), Unit.METER);
		} else {
			return Length.valueOf(value, Unit.METER);
		}
	}

	/**
	 * Create a new Length from the specified string value and unit.
	 * 
	 * @param value
	 *            the value.
	 * @param unit
	 *            the unit.
	 * @return the Length.
	 */
	public static Length valueOf(final String value, final Unit unit) {
		return new Length(new BigDecimal(value), unit);
	}

	private BigDecimal value;
	private Unit unit;

	/**
	 * Create a new Length.
	 * 
	 * @param value
	 *            the value.
	 * @param unit
	 *            the unit.
	 */
	public Length(final BigDecimal value, final Unit unit) {
		this.value = value;
		this.unit = unit;
	}

	public int compareTo(final Length o) {
		Length sameUnits = o.to(unit);
		return value.compareTo(sameUnits.getValue());
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Length other = (Length) obj;
		if (unit == null) {
			if (other.unit != null) {
				return false;
			}
		} else if (!unit.equals(other.unit)) {
			return false;
		}
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the unit of this Length.
	 * 
	 * @return the unit.
	 */
	public Unit getUnit() {
		return unit;
	}

	/**
	 * Gets the value of this Length.
	 * 
	 * @return the value.
	 */
	public BigDecimal getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((unit == null) ? 0 : unit.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	/**
	 * Subtracts a unit-less value from this Length and returns a new Length.
	 * 
	 * @param number
	 *            the value.
	 * @return the new length.
	 */
	public Length minus(final BigDecimal number) {
		return new Length(value.subtract(number), unit);
	}

	/**
	 * Subtracts a unit-less value from this Length and returns a new Length.
	 * 
	 * @param number
	 *            the value.
	 * @return the new length.
	 */
	public Length minus(final double number) {
		return new Length(value.subtract(new BigDecimal(number)), unit);
	}

	/**
	 * Subtracts the specified Length from this Length and returns the result as
	 * a new Length with the same units as this Length.
	 * 
	 * @param length
	 *            the length.
	 * @return the new length.
	 */
	public Length minus(final Length length) {
		Length converted = length.to(unit);
		return new Length(value.subtract(converted.value), unit);
	}

	/**
	 * Subtracts a unit-less value from this Length and returns a new Length.
	 * 
	 * @param number
	 *            the value.
	 * @return the new length.
	 */
	public Length minus(final long number) {
		return new Length(value.subtract(new BigDecimal(number)), unit);
	}

	/**
	 * Adds this a unit-less value to this Length and returns a new Length.
	 * 
	 * @param number
	 *            the value.
	 * @return the new length.
	 */
	public Length plus(final BigDecimal number) {
		return new Length(value.add(number), unit);
	}

	/**
	 * Adds this a unit-less value to this Length and returns a new Length.
	 * 
	 * @param number
	 *            the value.
	 * @return the new length.
	 */
	public Length plus(final double number) {
		return new Length(value.add(new BigDecimal(number)), unit);
	}

	/**
	 * Returns a new Length which is the sum of this Length and the specified
	 * Length. The returned Length will have the same unit as this Length.
	 * 
	 * @param length
	 *            the length.
	 * @return the new length.
	 */
	public Length plus(final Length length) {
		Length converted = length.to(unit);
		return new Length(value.add(converted.getValue()), unit);
	}

	/**
	 * Adds this a unit-less value to this Length and returns a new Length.
	 * 
	 * @param number
	 *            the value.
	 * @return the new length.
	 */
	public Length plus(final long number) {
		return new Length(value.add(new BigDecimal(number)), unit);
	}

	/**
	 * Convert this length to a different unit.
	 * 
	 * @param newUnit
	 *            the new unit.
	 * @return the converted length.
	 */
	public Length to(final Unit newUnit) {
		if (newUnit == unit) {
			return this;
		} else {
			return Length.valueOf((value.doubleValue() / unit.getFactor().doubleValue())
					* newUnit.getFactor().doubleValue(), newUnit);
		}
	}

	@Override
	public String toString() {
		return NUM.format(value) + " " + unit;
	}
}
