package org.corewall.internal;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.corewall.Locator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.inject.Singleton;

/**
 * A default implementation of the Locator interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@Singleton
public class DefaultLocator implements Locator {
	private static final class Resources extends URLClassLoader {
		public Resources(final ClassLoader parent) {
			super(new URL[0], parent);
		}

		@Override
		public void addURL(final URL url) {
			super.addURL(url);
		}
	}

	private static Logger LOGGER = LoggerFactory.getLogger(DefaultLocator.class);
	private File currentDirectory = new File(".");
	private Resources resources = new Resources(DefaultLocator.class.getClassLoader());
	protected final SetMultimap<String, Object> services = LinkedHashMultimap.create();

	/**
	 * Create a new DefaultResourceLoader.
	 */
	public DefaultLocator() {
		LOGGER.debug("initialized");
	}

	public void addResource(final URL resource) {
		resources.addURL(resource);
		LOGGER.debug("Added resource {}", resource);
	}

	private File findFile(final String url) {
		// get our path
		String path = url;
		if (path.startsWith("file:")) {
			path = path.substring(6);
		}

		// find the file
		File file = new File(path);
		if (file.isAbsolute() || file.exists()) {
			currentDirectory = file.getParentFile();
			return file;
		} else {
			return new File(currentDirectory, path);
		}
	}

	public URL getResource(final String path) {
		ImmutableList<URL> list = getResources(path);
		if (list.isEmpty()) {
			return null;
		} else {
			return list.get(0);
		}
	}

	public ImmutableList<URL> getResources(final String path) {
		List<URL> list = new ArrayList<URL>();
		if (path != null) {
			try {
				if (path.startsWith("rsrc:") || path.startsWith("classpath:")) {
					list.addAll(Collections.list(resources.getResources(strip(path))));
				} else if (path.startsWith("file:") || path.startsWith("http:") || path.startsWith("ftp:")
						|| path.startsWith("jar:")) {
					list.add(new URL(path));
				} else {
					try {
						list.add(new URL(path));
					} catch (MalformedURLException mue) {
						list.add(findFile(path).toURI().toURL());
					}
				}
			} catch (MalformedURLException e) {
				LOGGER.warn("Unable to get resources for {}: {}", path, e.getMessage());
			} catch (IOException e) {
				LOGGER.warn("Unable to get resources for {}: {}", path, e.getMessage());
			}
		}
		return ImmutableList.copyOf(list);
	}

	private String strip(final String path) {
		final int idx = path.indexOf(':');
		String stripped = (idx > -1) ? path.substring(idx + 1) : path;
		while (stripped.charAt(0) == '/') {
			stripped = stripped.substring(1);
		}
		return stripped;
	}
}
