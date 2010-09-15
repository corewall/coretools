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

import java.net.URL;

import org.corewall.internal.DefaultLocator;

import com.google.common.collect.ImmutableList;
import com.google.inject.ImplementedBy;

/**
 * Locates resources.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@ImplementedBy(DefaultLocator.class)
public interface Locator {

	/**
	 * Add a resource to this locator.
	 * 
	 * @param resource
	 *            the resource.
	 */
	void addResource(URL resource);

	/**
	 * Find a resource.
	 * 
	 * @param path
	 *            the path to the resource.
	 * 
	 * @return the resource URL or null if not found.
	 */
	URL getResource(String path);

	/**
	 * Find all instances of a resource.
	 * 
	 * @param path
	 *            the path.
	 * @return the list of resource URLs.
	 */
	ImmutableList<URL> getResources(String path);
}
