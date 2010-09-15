package org.corewall.data.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVReader;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.google.inject.internal.Maps;

/**
 * Reads models from character-separated value (CSV) files.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CSVModelReader extends AbstractModelReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVModelReader.class);

	protected final CSVReader reader;
	protected String[] keys = null;
	protected boolean first = true;

	/**
	 * Create a new CSVModelReader from the specified {@link CSVReader} and
	 * keys. The values in the passed array will be used as the keys in the
	 * model maps. The keys array supports null values which will effectively
	 * skip a particular column in the data file.
	 * 
	 * @param reader
	 *            the configured CSV reader.
	 * @param keys
	 *            the array of keys.
	 */
	public CSVModelReader(final CSVReader reader, final String[] keys) {
		this.reader = reader;
		this.keys = keys;
	}

	/**
	 * Creates a new CSVModelReader.
	 * 
	 * @param in
	 *            the the input stream.
	 * @param separator
	 *            the separator char.
	 */
	public CSVModelReader(final InputStream in, final char separator) {
		this(new CSVReader(new InputStreamReader(in), separator), null);
	}

	/**
	 * Creates a new CSVModelReader.
	 * 
	 * @param in
	 *            the the input stream.
	 * @param separator
	 *            the separator char.
	 * @param keys
	 *            the keys.
	 */
	public CSVModelReader(final InputStream in, final char separator, final String[] keys) {
		this(new CSVReader(new InputStreamReader(in), separator, '"', 1), keys);
	}

	/**
	 * Creates a new CSVModelReader.
	 * 
	 * @param reader
	 *            the reader.
	 * @param separator
	 *            the separator char.
	 */
	public CSVModelReader(final Reader reader, final char separator) {
		this(new CSVReader(reader, separator), null);
	}

	/**
	 * Creates a new CSVModelReader.
	 * 
	 * @param reader
	 *            the reader.
	 * @param separator
	 *            the separator char.
	 * @param keys
	 *            the keys.
	 */
	public CSVModelReader(final Reader reader, final char separator, final String[] keys) {
		this(new CSVReader(reader, separator, '"', 1), keys);
	}

	@Override
	protected List<Map<String, String>> parseModels() {
		List<Map<String, String>> models = Lists.newArrayList();
		try {
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
		} catch (IOException e) {
			LOGGER.error("Unable to parse CSV file", e);
		} finally {
			Closeables.closeQuietly(reader);
		}
		return models;
	}
}
