/*
 * Copyright (c) Josh Reed, 2009.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.corewall;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	private static Logger LOGGER = LoggerFactory.getLogger(Platform.class);
	private static boolean started = false;
	private static Injector injector = null;

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
				if (!spi.startsWith("#")) {
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
		start(new HashSet<Module>(), true);
	}

	/**
	 * Starts the platform with the specified set of modules and the discover
	 * flag.
	 * 
	 * @param modules
	 *            the set of modules.
	 * @param discover
	 *            the discover flag.
	 */
	public static void start(final Set<Module> modules, final boolean discover) {
		if (!started) {
			started = true;

			// build our final set of modules to start
			Set<Module> set = Sets.newHashSet();
			set.addAll(modules);
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

			// start the modules
			LOGGER.info("Starting modules: {}", modules);
			injector = Guice.createInjector(modules);
		}
	}

	Platform() {
		// not to be instantiated
	}
}
