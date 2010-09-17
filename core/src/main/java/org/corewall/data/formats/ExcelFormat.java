package org.corewall.data.formats;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.corewall.data.Factory;
import org.corewall.data.Filter;
import org.corewall.data.Model;

import com.google.common.collect.Lists;
import com.google.inject.internal.Maps;
import com.google.inject.internal.Nullable;

/**
 * Reads models from Excel files.
 * 
 * @author Josh Reed (jareed@andrill.org)
 * 
 * @param <T>
 *            the {@link Model} type.
 */
public class ExcelFormat<T extends Model> extends AbstractFormat<T> {
	protected String[] keys;
	protected final String sheetName;
	protected final int sheetNumber;
	protected final int skip;

	/**
	 * Create a new ExcelFormat.
	 * 
	 * @param id
	 *            the format id.
	 * @param sheetNumber
	 *            the sheet number.
	 * @param factory
	 *            the factory.
	 */
	public ExcelFormat(final String id, final int sheetNumber, final Factory<T> factory) {
		this(id, sheetNumber, 0, null, null, factory);
	}

	/**
	 * Create a new ExcelFormat.
	 * 
	 * @param id
	 *            the format id.
	 * @param sheetNumber
	 *            the sheet number to read.
	 * @param skip
	 *            the number of rows to skip.
	 * @param keys
	 *            the keys.
	 * @param filter
	 *            the filter.
	 * @param factory
	 *            the factory.
	 */
	public ExcelFormat(final String id, final int sheetNumber, final int skip, @Nullable final String[] keys,
			@Nullable final Filter filter, @Nullable final Factory<T> factory) {
		super(id, filter, factory);

		this.sheetName = null;
		this.sheetNumber = sheetNumber;
		this.skip = skip;
		this.keys = keys;
	}

	/**
	 * Create a new ExcelFormat.
	 * 
	 * @param id
	 *            the format id.
	 * @param sheetName
	 *            the sheet name.
	 * @param factory
	 *            the factory.
	 */
	public ExcelFormat(final String id, final String sheetName, final Factory<T> factory) {
		this(id, sheetName, 0, null, null, factory);
	}

	/**
	 * Create a new ExcelFormat.
	 * 
	 * @param id
	 *            the format id.
	 * @param sheetName
	 *            the sheet name.
	 * @param skip
	 *            the number of rows to skip.
	 * @param keys
	 *            the keys.
	 * @param filter
	 *            the filter.
	 * @param factory
	 *            the factory.
	 */
	public ExcelFormat(final String id, final String sheetName, final int skip, final String[] keys,
			final Filter filter, final Factory<T> factory) {
		super(id, filter, factory);

		this.sheetName = sheetName;
		this.sheetNumber = -1;
		this.skip = skip;
		this.keys = keys;
	}

	public List<Map<String, String>> getRaw(final URL url) throws IOException {
		Workbook workbook;
		List<Map<String, String>> models = Lists.newArrayList();
		boolean first = true;
		try {
			// open our workbook and get our sheet
			workbook = WorkbookFactory.create(url.openStream());
			Sheet sheet;
			if (sheetName != null) {
				sheet = workbook.getSheet(sheetName);
				if (sheet == null) {
					throw new IOException("Invalid sheet name '" + sheetName + "'");
				}
			} else {
				sheet = workbook.getSheetAt(sheetNumber);
				if (sheet == null) {
					throw new IOException("Invalid sheet number '" + sheetNumber + "'");
				}
			}

			// read row by row
			for (Row row : sheet) {
				List<String> values = Lists.newArrayList();
				for (Cell cell : row) {
					values.add(cell.toString());
				}
				if (first) {
					first = false;
					if (keys == null) {
						keys = values.toArray(new String[0]);
					}
				} else {
					Map<String, String> map = Maps.newHashMap();
					for (int i = 0; i < values.size(); i++) {
						if (i < keys.length) {
							String key = keys[i];
							if (key != null) {
								map.put(key, values.get(i));
							}
						}
					}
					if (map.size() > 0) {
						models.add(map);
					}
				}
			}
		} catch (InvalidFormatException e) {
			throw new IOException("Invalid Excel Format", e);
		}
		return models;
	}
}
