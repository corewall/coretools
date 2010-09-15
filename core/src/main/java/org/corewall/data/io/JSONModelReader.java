package org.corewall.data.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Closeables;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;

/**
 * A {@link ModelReader} for JSON.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class JSONModelReader extends AbstractModelReader {
	private static final Logger LOG = LoggerFactory.getLogger(JSONModelReader.class);
	protected final Reader reader;

	/**
	 * Create a new JSONModelReader for the specified {@link InputStream}.
	 * 
	 * @param in
	 *            the input stream.
	 */
	public JSONModelReader(final InputStream in) {
		this(new InputStreamReader(in));
	}

	/**
	 * Create a new JSONModelReader for the specified {@link Reader}.
	 * 
	 * @param reader
	 *            the reader.
	 */
	public JSONModelReader(final Reader reader) {
		this.reader = reader;
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

	@Override
	protected List<Map<String, String>> parseModels() {
		List<Map<String, String>> models = Lists.newArrayList();
		JSONArray array;
		try {
			array = new JSONArray(new JSONTokener(reader));
			for (int i = 0; i < array.length(); i++) {
				models.add(json2map(array.getJSONObject(i)));
			}
		} catch (JSONException e) {
			LOG.error("Unable to parse JSON", e);
		} finally {
			Closeables.closeQuietly(reader);
		}
		return models;
	}
}
