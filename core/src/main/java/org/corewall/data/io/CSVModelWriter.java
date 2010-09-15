package org.corewall.data.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import org.corewall.data.Model;

import au.com.bytecode.opencsv.CSVWriter;

/**
 * Writes models to character-separated value (CSV) files.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CSVModelWriter implements ModelWriter {
	protected final CSVWriter writer;
	protected final Map<String, String> headers;

	/**
	 * Creates a new CSVModelWriter from the specified {@link CSVWriter} and
	 * header map. The header map consists of a model key to label mapping which
	 * is used to determine the which columns appear in the CSV and in which
	 * order. If a model key does not appear in the header map, it will be
	 * skipped in the output. If headers is null, no header row will be
	 * generated.
	 * 
	 * @param writer
	 *            the {@link CSVWriter}.
	 * @param headers
	 *            the header map.
	 */
	public CSVModelWriter(final CSVWriter writer, final Map<String, String> headers) {
		this.writer = writer;
		this.headers = headers;
	}

	/**
	 * Creates a new CSVModelWriter from the specified {@link Writer} and
	 * separator character.
	 * 
	 * @param out
	 *            the {@link OutputStream}.
	 * @param separator
	 *            the separator.
	 */
	public CSVModelWriter(final OutputStream out, final char separator) {
		this(new CSVWriter(new OutputStreamWriter(out), separator), null);
	}

	/**
	 * Creates a new CSVModelWriter from the specified {@link Writer}, separator
	 * character, and header map.
	 * 
	 * @param out
	 *            the {@link OutputStream}.
	 * @param separator
	 *            the separator.
	 * @param headers
	 *            the headers.
	 */
	public CSVModelWriter(final OutputStream out, final char separator, final Map<String, String> headers) {
		this(new CSVWriter(new OutputStreamWriter(out), separator), headers);
	}

	/**
	 * Creates a new CSVModelWriter from the specified {@link Writer} and
	 * separator character.
	 * 
	 * @param writer
	 *            the {@link Writer}.
	 * @param separator
	 *            the separator.
	 */
	public CSVModelWriter(final Writer writer, final char separator) {
		this(new CSVWriter(writer, separator), null);
	}

	/**
	 * Creates a new CSVModelWriter from the specified {@link Writer}, separator
	 * character, and header map.
	 * 
	 * @param writer
	 *            the {@link Writer}.
	 * @param separator
	 *            the separator.
	 * @param headers
	 *            the headers.
	 */
	public CSVModelWriter(final Writer writer, final char separator, final Map<String, String> headers) {
		this(new CSVWriter(writer, separator), headers);
	}

	public void write(final List<? extends Model> models) throws IOException {
		if (models.size() == 0) {
			return;
		}

		// get our keys
		String[] keys;
		if (headers == null) {
			keys = models.get(0).toMap().keySet().toArray(null);
		} else {
			keys = headers.keySet().toArray(new String[0]);
		}

		// write our header if necessary
		String[] row = new String[keys.length];
		if (headers != null) {
			for (int i = 0; i < keys.length; i++) {
				row[i] = headers.get(keys[i]);
			}
			writer.writeNext(row);
		}

		// write our models
		for (Model m : models) {
			Map<String, String> map = m.toMap();
			for (int i = 0; i < keys.length; i++) {
				row[i] = map.get(keys[i]);
			}
			writer.writeNext(row);
		}
		writer.close();
	}
}
