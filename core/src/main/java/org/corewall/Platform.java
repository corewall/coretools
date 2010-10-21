package org.corewall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

import org.corewall.data.formats.StandardFormats;
import org.corewall.geology.formats.GeologyModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.internal.Sets;

/**
 * Starts the platform.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public final class Platform {
	private static Injector injector = null;
	private static Logger LOGGER = LoggerFactory.getLogger(Platform.class);

	/**
	 * The standard set of modules started when {@link Platform#start()} is
	 * called.
	 */
	private static final Module[] STANDARD_MODULES = new Module[] { new StandardFormats(), new GeologyModule() };
	private static boolean started = false;

	/**
	 * Gets a service from the platform.
	 * 
	 * @param <E>
	 *            the service type.
	 * @param clazz
	 *            the service class.
	 * @return the instance.
	 */
	public static <E> E getService(final Class<E> clazz) {
		if (!started) {
			throw new IllegalStateException("The platform has not been started");
		} else {
			return injector.getInstance(clazz);
		}
	}

	@SuppressWarnings("unchecked")
	protected static <E> void parseSPI(final URL url, final Set<E> set) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				final String spi = line.trim();
				if (spi.charAt(0) != '#') {
					try {
						final E instance = (E) Class.forName(spi).newInstance();
						set.add(instance);
						LOGGER.debug("Instantiated implementation {} for service {}", instance, url.getFile());
					} catch (final InstantiationException e) {
						LOGGER.error("Unable to instantiate service {} {}", spi, e.getMessage());
						e.printStackTrace();
					} catch (final IllegalAccessException e) {
						LOGGER.error("Unable to instantiate service {} {}", spi, e.getMessage());
						e.printStackTrace();
					} catch (final ClassNotFoundException e) {
						LOGGER.error("Unable to instantiate service {} {}", spi, e.getMessage());
						e.printStackTrace();
					}
				}
			}
		} catch (final IOException e) {
			LOGGER.warn("Unable to read service definition {} {}", url, e);
		} finally {
			Closeables.closeQuietly(br);
		}
	}

	/**
	 * Starts the platform.
	 */
	public static void start() {
		start(true, true);
	}

	/**
	 * Starts the platform with the specified set of modules and the discover
	 * flag.
	 * 
	 * @param standard
	 *            the flag indicating whether standard modules should be
	 *            included.
	 * @param discover
	 *            the discover flag.
	 * @param modules
	 *            the set of additional modules to include.
	 */
	public static void start(final boolean standard, final boolean discover, final Module... modules) {
		if (!started) {
			started = true;

			// build our final set of modules to start
			Set<Module> set = Sets.newHashSet();

			// add our standard modules
			if (standard) {
				for (Module m : STANDARD_MODULES) {
					set.add(m);
				}
			}

			// discover modules if enabled
			if (discover) {
				try {
					for (URL url : Collections.list(Platform.class.getClassLoader().getResources(
							"META-INF/services/com.google.inject.Module"))) {
						parseSPI(url, set);
					}
				} catch (IOException ioe) {
					LOGGER.error("Unable to discover modules", ioe);
				}
			}

			// add all of the additional modules
			for (Module m : modules) {
				set.add(m);
			}

			// start the modules
			LOGGER.info("Starting modules: {}", set);
			injector = Guice.createInjector(set);
		}
	}

	Platform() {
		// not to be instantiated
	}
}
