package org.corewall.data;

import java.util.Map;



/**
 * Pre-defined filters.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public final class Filters {

	/**
	 * Accepts if all filters match.
	 */
	private static class AllFilter implements Filter {
		private final Filter[] filters;

		public AllFilter(final Filter... filters) {
			this.filters = filters;
		}

		public boolean accept(final Map<String, String> map) {
			for (Filter f : filters) {
				if (!f.accept(map)) {
					return false;
				}
			}
			return true;
		}
	}

	/**
	 * Accepts if any filters match.
	 */
	private static class AnyFilter implements Filter {
		private final Filter[] filters;

		public AnyFilter(final Filter... filters) {
			this.filters = filters;
		}

		public boolean accept(final Map<String, String> map) {
			for (Filter f : filters) {
				if (f.accept(map)) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Filters by property and optionally value.
	 */
	private static class PropertyFilter implements Filter {
		private static final String WILDCARD = "*";
		private final String name;
		private final String value;

		public PropertyFilter(final String name) {
			this(name, WILDCARD);
		}

		public PropertyFilter(final String name, final String value) {
			this.name = name;
			this.value = value;
		}

		public boolean accept(final Map<String, String> map) {
			return (map.containsKey(name) && ((WILDCARD.equals(value)) || value.equals(map.get(name))));
		}
	}

	/**
	 * Accept all.
	 */
	private static final Filter ALL = new Filter() {
		public boolean accept(final Map<String, String> map) {
			return true;
		}
	};

	/**
	 * Accept none.
	 */
	private static final Filter NONE = new Filter() {
		public boolean accept(final Map<String, String> map) {
			return false;
		};
	};

	/**
	 * Constructs a filter that accepts all models.
	 * 
	 * @return the filter.
	 */
	public static Filter all() {
		return ALL;
	}

	/**
	 * Constructs a composite filter that accepts a model if all specified
	 * filters match.
	 * 
	 * @param filters
	 *            the filters.
	 * @return the composite filter.
	 */
	public static Filter all(final Filter... filters) {
		return new AllFilter(filters);
	}

	/**
	 * Constructs a composite filter that accepts a model if any specified
	 * filters match.
	 * 
	 * @param filters
	 *            the filters.
	 * @return the composite filter.
	 */
	public static Filter any(final Filter... filters) {
		return new AnyFilter(filters);
	}

	/**
	 * Constructs a filter that accepts no models.
	 * 
	 * @return the filter.
	 */
	public static Filter none() {
		return NONE;
	}

	/**
	 * Constructs a filter that accepts all models with the specified property
	 * name defined, regardless of value.
	 * 
	 * @param name
	 *            the property name.
	 * @return the filter.
	 */
	public static Filter property(final String name) {
		return new PropertyFilter(name);
	}

	/**
	 * Constructs a filter that accepts all models with the specified property
	 * and value.
	 * 
	 * @param name
	 *            the property name.
	 * @param value
	 *            the property value.
	 * @return the filter.
	 */
	public static Filter property(final String name, final String value) {
		return new PropertyFilter(name, value);
	}
}
