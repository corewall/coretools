package org.corewall.geology.io;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.corewall.data.io.AbstractModelReader;
import org.corewall.data.io.Filters;
import org.corewall.geology.models.XYDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;

/**
 * Reads data in the Corelyzer XML format.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class XMLDataReader extends AbstractModelReader {

	/**
	 * A SAX handler for Corelyzer's data format.
	 * 
	 * @author Josh Reed (jareed@andrill.org)
	 */
	public static class XMLDataHandler extends DefaultHandler {
		protected List<Map<String, String>> models = Lists.newArrayList();
		protected double depth = 0.0;
		protected double offset = 0.0;
		protected String sensor = null;
		protected Map<String, String> section = null;
		protected Map<String, String[]> fields = Maps.newHashMap();
		protected StringBuilder buffer = new StringBuilder();

		@Override
		public void characters(final char[] ch, final int start, final int length) throws SAXException {
			buffer.append(ch, start, length);
		}

		@Override
		public void endElement(final String uri, final String localName, final String qName) throws SAXException {
			if ("id".equals(qName)) {
				section.put("id", buffer.toString());
			} else if ("depth_unit".equals(qName)) {
				section.put("depth_unit", buffer.toString());
			} else if ("depth".equals(qName)) {
				depth = parseDouble(buffer.toString(), 0);
			} else if ("sensor".equals(qName)) {
				Map<String, String> model = Maps.newHashMap();
				models.add(model);
				model.put("type", "sensor");
				model.put("offset", NUM.format(offset));
				model.put("depth", NUM.format(depth));
				model.put("field_id", sensor);
				String[] field = fields.get(sensor);
				model.put("field_name", field[0]);
				model.put("field_unit", field[1]);
				model.put("value", NUM.format(parseDouble(buffer.toString(), -1)));
			} else if ("offset".equals(qName)) {
				offset = parseDouble(buffer.toString(), 0);
			} else if ("top".equals(qName)) {
				section.put("top", buffer.toString());
			}
		}

		@Override
		public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) throws SAXException {
			buffer = new StringBuilder();
			if ("section".equals(qName)) {
				section = Maps.newHashMap();
				models.add(section);
				offset = parseDouble(attributes.getValue("offset"), 0);
				section.put("offset", NUM.format(offset));
				section.put("type", "section");
			} else if ("field".equals(qName)) {
				String id = attributes.getValue("localid");
				String name = attributes.getValue("name");
				String units = attributes.getValue("units");
				fields.put(id, new String[] { name, units });
				section.put("slot" + id, name);
			} else if ("sensor".equals(qName)) {
				sensor = attributes.getValue("id");
			}
		}
	}

	private static final DecimalFormat NUM = new DecimalFormat("0.####");
	private static final Logger LOGGER = LoggerFactory.getLogger(XMLDataReader.class);

	protected static double parseDouble(final String str, final double defaultValue) {
		if ((str == null) || "".equals(str.trim())) {
			return defaultValue;
		} else {
			try {
				return Double.parseDouble(str);
			} catch (NumberFormatException e) {
				return defaultValue;
			}
		}
	}

	protected final InputStream in;
	protected XMLDataHandler handler = null;

	/**
	 * Creates a new XMLDataReader.
	 * 
	 * @param in
	 *            the input stream.
	 */
	public XMLDataReader(final InputStream in) {
		this.in = in;
	}

	/**
	 * Gets the {@link XYDataSet} with the specified name.
	 * 
	 * @param name
	 *            the name.
	 * @return the {@link XYDataSet}
	 */
	public XYDataSet getDataSet(final String name) {
		return getDataSet(name, getModels(Filters.property("field_name", name)));
	}

	protected XYDataSet getDataSet(final String name, final List<Map<String, String>> models) {
		XYDataSet dataset = new XYDataSet(name);
		for (Map<String, String> model : models) {
			double x = parseDouble(model.get("depth"), 0) + parseDouble(model.get("offset"), 0);
			double y = parseDouble(model.get("value"), -1);
			dataset.add(x, y);
		}
		return dataset;
	}

	/**
	 * Gets the {@link XYDataSet} with the specified slot number.
	 * 
	 * @param slot
	 *            the slot number.
	 * @return the {@link XYDataSet}
	 */
	public XYDataSet getDataSetBySlot(final String slot) {
		return getDataSet(slot, getModels(Filters.property("field_id", slot)));
	}

	/**
	 * Gets the names of all {@link XYDataSet}s in this XML data file.
	 * 
	 * @return the set of names.
	 */
	public ImmutableSet<String> getDataSets() {
		if (handler == null) {
			getModels();
		}

		// build our field set
		ImmutableSet.Builder<String> set = ImmutableSet.builder();
		for (String[] f : handler.fields.values()) {
			set.add(f[0]);
		}
		return set.build();
	}

	@Override
	protected List<Map<String, String>> parseModels() {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			handler = new XMLDataHandler();
			parser.parse(in, handler);
			return handler.models;
		} catch (ParserConfigurationException e) {
			LOGGER.error("No SAX parser", e);
			throw new RuntimeException("No SAX parser", e);
		} catch (SAXException e) {
			LOGGER.error("XML parsing error", e);
			throw new RuntimeException("XML parsing error", e);
		} catch (IOException e) {
			LOGGER.error("I/O error", e);
			throw new RuntimeException("I/O error", e);
		}
	}
}
