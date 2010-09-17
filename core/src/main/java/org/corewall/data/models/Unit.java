package org.corewall.data.models;

import java.math.BigDecimal;

/**
 * Standard units of length.
 */
public enum Unit {
	/**
	 * Millimeter unit.
	 */
	MILLIMETER("mm", "1000"),

	/**
	 * Centimeter unit.
	 */
	CENTIMETER("cm", "100"),

	/**
	 * Meter unit.
	 */
	METER("m", "1"),

	/**
	 * Kilometer unit.
	 */
	KILOMETER("km", "0.001"),

	/**
	 * Inch unit.
	 */
	INCH("in", "39.3700787"),

	/**
	 * Foot unit.
	 */
	FOOT("ft", "3.2808399"),

	/**
	 * Yard unit.
	 */
	YARD("yd", "1.0936133");

	/**
	 * Gets a Unit from a string abbreviation.
	 * 
	 * @param abbr
	 *            the abbreviation.
	 * 
	 * @return the unit.
	 */
	public static Unit get(final String abbr) {
		if ("mm".equals(abbr)) {
			return MILLIMETER;
		} else if ("cm".equals(abbr)) {
			return CENTIMETER;
		} else if ("m".equals(abbr)) {
			return METER;
		} else if ("km".equals(abbr)) {
			return KILOMETER;
		} else if ("in".equals(abbr)) {
			return INCH;
		} else if ("ft".equals(abbr)) {
			return FOOT;
		} else if ("yd".equals(abbr)) {
			return YARD;
		} else {
			throw new RuntimeException("Unrecognized unit '" + abbr + "'");
		}
	}

	protected final BigDecimal factor;
	protected final String abbr;

	/**
	 * Create a new Unit with the specified abbreviation and factor to convert
	 * to meters.
	 * 
	 * @param abbr
	 *            the abbreviation.
	 * @param factor
	 *            the conversion factor.
	 */
	Unit(final String abbr, final String factor) {
		this.abbr = abbr;
		this.factor = new BigDecimal(factor);
	}

	/**
	 * Gets the abbreviation for this unit.
	 * 
	 * @return the abbreviation.
	 */
	public String getAbbr() {
		return abbr;
	}

	/**
	 * Gets the conversion factor for this unit.
	 * 
	 * @return the conversion factor.
	 */
	public BigDecimal getFactor() {
		return factor;
	}

	@Override
	public String toString() {
		return abbr;
	}
}