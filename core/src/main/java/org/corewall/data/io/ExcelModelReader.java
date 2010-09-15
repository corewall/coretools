package org.corewall.data.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.inject.internal.Maps;

/**
 * Reads models from Excel spreadsheets.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ExcelModelReader extends AbstractModelReader {
	private static final Logger LOGGER = LoggerFactory.getLogger(ExcelModelReader.class);
	protected final Sheet sheet;
	protected String[] keys = null;
	protected boolean first = true;

	/**
	 * Create a new ExcelModelReader for the specified input stream and sheet
	 * name.
	 * 
	 * @param in
	 *            the input stream.
	 * @param sheet
	 *            the sheet index.
	 */
	public ExcelModelReader(final InputStream in, final int sheet) {
		this(in, sheet, null, true);
	}

	/**
	 * Create a new ExcelModelReader for the specified input stream and sheet
	 * index. The values in the passed array will be used as the keys in the
	 * model maps. The keys array supports null values which will effectively
	 * skip a particular column in the data file.
	 * 
	 * @param in
	 *            the input stream.
	 * @param sheet
	 *            the sheet index.
	 * @param keys
	 *            the keys.
	 * @param skip
	 *            flag to skip the first row as a header row.
	 */
	public ExcelModelReader(final InputStream in, final int sheet, final String[] keys, final boolean skip) {
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(in);
			this.sheet = workbook.getSheetAt(sheet);
			this.keys = keys;
			this.first = skip;
		} catch (InvalidFormatException e) {
			LOGGER.error("Invalid Excel workbook", e);
			throw new RuntimeException("Invalid Excel workbook", e);
		} catch (IOException e) {
			LOGGER.error("Invalid Excel workbook", e);
			throw new RuntimeException("Invalid Excel workbook", e);
		}
		if (this.sheet == null) {
			LOGGER.error("Invalid sheet index {}", sheet);
			throw new RuntimeException("Invalid sheet index " + sheet);
		}
	}

	/**
	 * Create a new ExcelModelReader for the specified input stream and sheet
	 * name.
	 * 
	 * @param in
	 *            the input stream.
	 * @param sheet
	 *            the sheet name.
	 */
	public ExcelModelReader(final InputStream in, final String sheet) {
		this(in, sheet, null, true);
	}

	/**
	 * Create a new ExcelModelReader for the specified input stream and sheet
	 * name. The values in the passed array will be used as the keys in the
	 * model maps. The keys array supports null values which will effectively
	 * skip a particular column in the data file.
	 * 
	 * @param in
	 *            the input stream.
	 * @param sheet
	 *            the sheet name.
	 * @param keys
	 *            the keys.
	 * @param skip
	 *            flag to skip the first row as a header row.
	 */
	public ExcelModelReader(final InputStream in, final String sheet, final String[] keys, final boolean skip) {
		Workbook workbook;
		try {
			workbook = WorkbookFactory.create(in);
			this.sheet = workbook.getSheet(sheet);
			this.keys = keys;
			this.first = skip;
		} catch (InvalidFormatException e) {
			LOGGER.error("Invalid Excel workbook", e);
			throw new RuntimeException("Invalid Excel workbook", e);
		} catch (IOException e) {
			LOGGER.error("Invalid Excel workbook", e);
			throw new RuntimeException("Invalid Excel workbook", e);
		}
		if (this.sheet == null) {
			LOGGER.error("Invalid sheet name {}", sheet);
			throw new RuntimeException("Invalid sheet name " + sheet);
		}
	}

	@Override
	protected List<Map<String, String>> parseModels() {
		List<Map<String, String>> models = Lists.newArrayList();
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
		return models;
	}
}
