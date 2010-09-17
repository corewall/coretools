package org.corewall.data.formats;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.corewall.data.Factory;
import org.corewall.data.Filter;
import org.corewall.data.Model;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;
import com.google.inject.internal.Nullable;

/**
 * Reads models from JSON files.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * 
 * @param <T>
 *            the {@link Model} type.
 */
public class JSONFormat<T extends Model> extends AbstractFormat<T> {
	private static final Logger LOG = LoggerFactory.getLogger(JSONFormat.class);

	/**
	 * Create a new JSON format.
	 * 
	 * @param id
	 *            the id.
	 * @param filter
	 *            the filter.
	 * @param factory
	 *            the factory.
	 */
	public JSONFormat(final String id, @Nullable final Filter filter, @Nullable final Factory<T> factory) {
		super(id, filter, factory);
	}

	public List<Map<String, String>> getRaw(final URL url) throws IOException {
		List<Map<String, String>> models = Lists.newArrayList();
		Reader reader = null;
		JSONArray array;
		try {
			reader = new InputStreamReader(url.openStream());
			array = new JSONArray(new JSONTokener(reader));
			for (int i = 0; i < array.length(); i++) {
				models.add(json2map(array.getJSONObject(i)));
			}
		} catch (JSONException e) {
			throw new IOException("Unable to parse JSON", e);
		} finally {
			Closeables.closeQuietly(reader);
		}
		return models;
	}

	protected Map<String, String> json2map(final JSONObject json) {
		Map<String, String> map = Maps.newHashMap();
		for (String key : JSONObject.getNames(json)) {
			try {
				map.put(key, json.get(key).toString());
			} catch (JSONException e) {
				LOG.error("Unable to get value for key: " + key, e);
			}
		}
		return map;
	}
}
