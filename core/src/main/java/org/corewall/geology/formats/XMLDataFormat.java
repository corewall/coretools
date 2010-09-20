package org.corewall.geology.formats;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

import org.corewall.geology.models.XYDataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;

/**
 * Reads data from Corelyzer's XML format.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class XMLDataFormat {
	/**
	 * A SAX handler for Corelyzer's data format.
	 */
	public static class XMLDataHandler extends DefaultHandler {
		protected StringBuilder buffer = new StringBuilder();
		protected double depth = 0.0;
		protected Map<String, String[]> fields = Maps.newHashMap();
		protected List<Map<String, String>> models = Lists.newArrayList();
		protected double offset = 0.0;
		protected Map<String, String> section = null;
		protected String sensor = null;

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
		public void startElement(final String uri, final String localName, final String qName,
				final Attributes attributes) throws SAXException {
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

	private static final Logger LOGGER = LoggerFactory.getLogger(XMLDataFormat.class);

	private static final DecimalFormat NUM = new DecimalFormat("0.####");

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

	protected final String id;

	public XMLDataFormat(final String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public List<XYDataSet> getModels(final URL url) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Map<String, String>> getRaw(final URL url) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
