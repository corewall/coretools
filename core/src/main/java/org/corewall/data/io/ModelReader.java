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
package org.corewall.data.io;

import java.util.List;
import java.util.Map;

/**
 * Reads models from a resource.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface ModelReader {

	/**
	 * Converts a model map to a POJO.
	 * 
	 * @param <T>
	 *            the POJO type.
	 */
	interface Factory<T> {
		T build(Map<String, String> map);
	}

	/**
	 * Filters a list of model maps.
	 */
	interface Filter {
		boolean accept(Map<String, String> map);
	}

	/**
	 * Gets all models.
	 * 
	 * @return the list of models.
	 */
	List<Map<String, String>> getModels();

	/**
	 * Gets the filtered models.
	 * 
	 * @param filter
	 *            the filter.
	 * @return the filtered list of models.
	 */
	List<Map<String, String>> getModels(Filter filter);

	/**
	 * Gets the model POJOs.
	 * 
	 * @param <T>
	 *            the POJO type.
	 * @param filter
	 *            the filter.
	 * @param factory
	 *            the POJO factory.
	 * @return the list of model POJOs.
	 */
	<T> List<T> getModels(Filter filter, Factory<T> factory);
}
