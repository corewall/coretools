package org.corewall.data.formats;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.corewall.data.Factory;
import org.corewall.data.Filter;
import org.corewall.data.Model;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;
import com.google.inject.internal.Nullable;

/**
 * Reads models from character-separated value files.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * 
 * @param <T>
 *            the {@link Model} type.
 */
public class CSVFormat<T extends Model> extends AbstractFormat<T> {
	protected final char escape;
	protected String[] keys;
	protected final int line;
	protected final char quote;
	protected final char separator;

	/**
	 * Create a new CSVFormat.
	 * 
	 * @param id
	 *            the id.
	 * @param separator
	 *            the separator character.
	 * @param quote
	 *            the quote character.
	 * @param escape
	 *            the escape character.
	 * @param line
	 *            the number of lines to skip.
	 * @param keys
	 *            the header keys to use.
	 * @param filter
	 *            the filter.
	 * @param factory
	 *            the factory.
	 */
	public CSVFormat(final String id, final char separator, final char quote, final char escape, final int line,
			@Nullable final String[] keys, @Nullable final Filter filter, @Nullable final Factory<T> factory) {
		super(id, filter, factory);

		this.separator = separator;
		this.quote = quote;
		this.escape = escape;
		this.line = line;
		this.keys = keys;
	}

	/**
	 * Create a new CSVFormat.
	 * 
	 * @param id
	 *            the id.
	 * @param separator
	 *            the separator character.
	 * @param factory
	 *            the factory.
	 */
	public CSVFormat(final String id, final char separator, final Factory<T> factory) {
		this(id, separator, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER,
				CSVReader.DEFAULT_SKIP_LINES, null, null, factory);
	}

	/**
	 * Create a new CSVFormat.
	 * 
	 * @param id
	 *            the id.
	 * @param separator
	 *            the separator character.
	 * @param filter
	 *            the model filter.
	 * @param factory
	 *            the factory.
	 */
	public CSVFormat(final String id, final char separator, final Filter filter, final Factory<T> factory) {
		this(id, separator, CSVParser.DEFAULT_QUOTE_CHARACTER, CSVParser.DEFAULT_ESCAPE_CHARACTER,
				CSVReader.DEFAULT_SKIP_LINES, null, filter, factory);
	}

	public List<Map<String, String>> getRaw(final URL url) throws IOException {
		List<Map<String, String>> models = Lists.newArrayList();
		boolean first = true;
		CSVReader reader = null;
		try {
			reader = new CSVReader(new InputStreamReader(url.openStream()), separator, quote, escape, line);
			String[] row;
			while ((row = reader.readNext()) != null) {
				if ((keys == null) && first) {
					first = false;
					keys = row;
				} else {
					Map<String, String> map = Maps.newHashMap();
					for (int i = 0; i < row.length; i++) {
						if (i < keys.length) {
							String key = keys[i];
							if (key != null) {
								map.put(key, row[i]);
							}
						}
					}
					if (map.size() > 0) {
						models.add(map);
					}
				}
			}
		} finally {
			Closeables.closeQuietly(reader);
		}
		return models;
	}
}
