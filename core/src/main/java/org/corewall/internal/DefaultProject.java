package org.corewall.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.corewall.data.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;

/**
 * A default implementation of the {@link Project} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultProject implements Project {
	static class DefaultManifestEntry implements ManifestEntry {
		private final String format;
		private final String name;
		private final URL path;
		private final String type;

		public DefaultManifestEntry(final String name, final String type, final String format, final URL path) {
			this.name = name;
			this.type = type;
			this.format = format;
			this.path = path;
		}

		public String getFormat() {
			return format;
		}

		public String getName() {
			return name;
		}

		public URL getPath() {
			return path;
		}

		public String getType() {
			return type;
		}
	}

	private static final Logger LOG = LoggerFactory.getLogger(DefaultProject.class);
	protected String id;
	protected List<ManifestEntry> manifest = Lists.newArrayList();
	protected String name;
	protected URL path;
	protected Map<String, String> properties = Maps.newHashMap();

	/**
	 * Adds a manifest entry.
	 * 
	 * @param entry
	 *            the entry.
	 */
	public void addEntry(final ManifestEntry entry) {
		manifest.add(entry);
	}

	@Override
	public String getAttribute(final Attr attr) {
		return properties.get(attr.toString());
	}

	public String getId() {
		return id;
	}

	public ImmutableList<ManifestEntry> getManifest() {
		return ImmutableList.copyOf(manifest);
	}

	public String getName() {
		return name;
	}

	public URL getPath() {
		return path;
	}

	public ImmutableMap<String, String> getProperties() {
		return ImmutableMap.copyOf(properties);
	}

	/**
	 * Removes a standard attribute.
	 * 
	 * @param attr
	 *            the attribute.
	 */
	public void removeAttribute(final Attr attr) {
		properties.remove(attr.toString());
	}

	/**
	 * Removes a manifest entry.
	 * 
	 * @param entry
	 *            the entry.
	 */
	public void removeEntry(final ManifestEntry entry) {
		manifest.remove(entry);
	}

	/**
	 * Sets a standard attribute.
	 * 
	 * @param attr
	 *            the attribute.
	 * @param value
	 *            the attribute value.
	 */
	public void setAttribute(final Attr attr, final String value) {
		if (value == null) {
			removeAttribute(attr);
		} else {
			properties.put(attr.toString(), value);
		}
	}

	/**
	 * Sets the id.
	 * 
	 * @param id
	 *            the id.
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Sets the name.
	 * 
	 * @param name
	 *            the name.
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Sets the path.
	 * 
	 * @param file
	 *            the file.
	 */
	public void setPath(final File file) {
		try {
			path = file.toURI().toURL();
		} catch (MalformedURLException e) {
			// should never happen
			LOG.warn("Invalid project path", e);
		}
	}

	/**
	 * Sets the path.
	 * 
	 * @param path
	 *            the path.
	 */
	public void setPath(final URL path) {
		this.path = path;
	}
}
