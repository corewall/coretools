package org.corewall.geology.models;

import java.text.DecimalFormat;
import java.util.Map;

import org.corewall.data.Length;
import org.corewall.data.Model;
import org.corewall.data.Unit;

import com.google.common.collect.Maps;

/**
 * Models a section.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Section implements Model {
	/**
	 * A helper class for building {@link Image}s.
	 */
	public static class Builder {
		private final Map<String, String> map;

		/**
		 * Creates a new builder.
		 */
		public Builder() {
			map = Maps.newHashMap();
		}

		/**
		 * Sets the base.
		 * 
		 * @param value
		 *            the base.
		 * @return the builder as a convenience.
		 */
		public Builder base(final double value) {
			map.put(BASE_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the base.
		 * 
		 * @param value
		 *            the base.
		 * @return the builder as a convenience.
		 */
		public Builder base(final Length value) {
			map.put(BASE_KEY, "" + value);
			return this;
		}

		/**
		 * Sets the base.
		 * 
		 * @param value
		 *            the base.
		 * @return the builder as a convenience.
		 */
		public Builder base(final String value) {
			map.put(BASE_KEY, value);
			return this;
		}

		/**
		 * Builds the section.
		 * 
		 * @return the built section.
		 */
		public Section build() {
			return new Section(map);
		}

		/**
		 * Sets the length.
		 * 
		 * @param value
		 *            the length.
		 * @return the builder as a convenience.
		 */
		public Builder length(final double value) {
			map.put(LENGTH_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the length.
		 * 
		 * @param value
		 *            the length.
		 * @return the builder as a convenience.
		 */
		public Builder length(final Length value) {
			map.put(LENGTH_KEY, "" + value);
			return this;
		}

		/**
		 * Sets the length.
		 * 
		 * @param value
		 *            the length.
		 * @return the builder as a convenience.
		 */
		public Builder length(final String value) {
			map.put(LENGTH_KEY, value);
			return this;
		}

		/**
		 * Sets the name.
		 * 
		 * @param value
		 *            the name.
		 * @return the builder as a convenience.
		 */
		public Builder name(final String value) {
			map.put(NAME_KEY, value);
			return this;
		}

		/**
		 * Sets the top.
		 * 
		 * @param value
		 *            the top.
		 * @return the builder as a convenience.
		 */
		public Builder top(final double value) {
			map.put(TOP_KEY, NUM.format(value));
			return this;
		}

		/**
		 * Sets the top.
		 * 
		 * @param value
		 *            the top.
		 * @return the builder as a convenience.
		 */
		public Builder top(final Length value) {
			map.put(TOP_KEY, "" + value);
			return this;
		}

		/**
		 * Sets the top.
		 * 
		 * @param value
		 *            the top.
		 * @return the builder as a convenience.
		 */
		public Builder top(final String value) {
			map.put(TOP_KEY, value);
			return this;
		}
	}

	private static final DecimalFormat NUM = new DecimalFormat("0.####");
	protected Length top;
	protected Length base;
	protected Length length;
	protected String name;

	// keys
	protected static final String BASE_KEY = "base";
	protected static final String TOP_KEY = "top";
	protected static final String LENGTH_KEY = "length";
	protected static final String NAME_KEY = "name";

	/**
	 * Builds a section from a map of properties.
	 * 
	 * @param properties
	 *            the properties map.
	 */
	public Section(final Map<String, String> properties) {
		// parse the name
		String name = properties.get(NAME_KEY);
		if (name != null) {
			this.name = name;
		}

		// parse top
		String top = properties.get(TOP_KEY);
		if (top == null) {
			this.top = Length.valueOf(0, Unit.METER);
		} else {
			this.top = Length.valueOf(top);
		}

		// parse base if specified
		String base = properties.get(BASE_KEY);
		if (base != null) {
			this.base = Length.valueOf(base);
		}

		// parse length if specified
		String length = properties.get(LENGTH_KEY);
		if ((length != null) && (this.base == null)) {
			this.length = Length.valueOf(length);
		}

		// set our base if only length was specified
		if ((this.base == null) && (this.length != null)) {
			this.base = this.top.plus(this.length);
		}

		// set our length if top and base specified
		if (this.length == null) {
			this.length = this.base.minus(this.top);
		}
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
		Section other = (Section) obj;
		if (base == null) {
			if (other.base != null) {
				return false;
			}
		} else if (!base.equals(other.base)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (top == null) {
			if (other.top != null) {
				return false;
			}
		} else if (!top.equals(other.top)) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the base position of this section.
	 * 
	 * @return the base.
	 */
	public Length getBase() {
		return base;
	}

	/**
	 * Gets the length of this section.
	 * 
	 * @return the length.
	 */
	public Length getLength() {
		return length;
	}

	/**
	 * Gets the name of this section.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the top position of this section.
	 * 
	 * @return the top.
	 */
	public Length getTop() {
		return top;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((top == null) ? 0 : top.hashCode());
		return result;
	}

	public Map<String, String> toMap() {
		Map<String, String> map = Maps.newHashMapWithExpectedSize(3);
		map.put(NAME_KEY, getName());
		map.put(TOP_KEY, getTop().toString());
		map.put(BASE_KEY, getBase().toString());
		return map;
	}

	@Override
	public String toString() {
		return "Section " + toMap();
	}
}
