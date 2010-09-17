package org.corewall.geology.io;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.corewall.data.Filter;
import org.corewall.data.Filters;
import org.corewall.data.io.AbstractModelReader;
import org.corewall.data.models.Length;
import org.corewall.data.models.Unit;
import org.corewall.geology.models.Image;
import org.corewall.geology.models.Image.Builder;
import org.corewall.scene.Orientation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.google.common.collect.ImmutableSet;
import com.google.inject.internal.Lists;
import com.google.inject.internal.Maps;
import com.google.inject.internal.Sets;

/**
 * Reads images from a Corelyzer CML file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CMLReader extends AbstractModelReader {
	/**
	 * A SAX handler for Corelyzer's CML format.
	 */
	public static class CMLHandler extends DefaultHandler {
		protected List<Map<String, String>> models = Lists.newArrayList();
		protected String session = null;
		protected String track = null;
		protected Set<String> tracks = Sets.newHashSet();

		@Override
		public void startElement(final String uri, final String localName, final String qName,
				final Attributes attributes) throws SAXException {
			// build our base model
			Map<String, String> model = Maps.newHashMap();
			for (int i = 0; i < attributes.getLength(); i++) {
				model.put(attributes.getQName(i), attributes.getValue(i));
			}
			if (!model.containsKey("type")) {
				model.put("type", qName);
			}
			models.add(model);

			// handle some special cases
			String type = attributes.getValue("type");
			if ("session".equals(qName)) {
				session = attributes.getValue("name");
			} else if ("track".equals(type)) {
				track = attributes.getValue("name");
				tracks.add(track);
			} else if ("visual".equals(qName)) {
				if (session != null) {
					model.put("session", session);
				}
				if (track != null) {
					model.put("track", track);
				}
			}
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(CMLReader.class);
	protected CMLHandler handler;
	protected final InputStream in;

	/**
	 * Create a new CMLReader.
	 * 
	 * @param in
	 *            the input stream.
	 */
	public CMLReader(final InputStream in) {
		this.in = in;
	}

	/**
	 * Gets the {@link Image} in the specified track.
	 * 
	 * @param track
	 *            the track.
	 * @return the list of {@link Image}s.
	 */
	public List<Image> getImages(final String track) {
		List<Image> images = Lists.newArrayList();
		Filter filter = Filters.all(Filters.property("type", "core_section"), Filters.property("track", track));
		for (Map<String, String> model : getModels(filter)) {
			Builder builder = new Builder();
			for (Entry<String, String> e : model.entrySet()) {
				String key = e.getKey();
				if ("depth".equals(key)) {
					builder.top(e.getValue());
				} else if ("dpi_x".equals(key)) {
					builder.dpiX(e.getValue());
				} else if ("dpi_y".equals(key)) {
					builder.dpiY(e.getValue());
				} else if ("urn".equals(key)) {
					builder.path(e.getValue());
				} else if ("orientation".equals(key)) {
					if (e.getValue().toLowerCase().charAt(0) == 'p') { // portrait
						builder.orientation(Orientation.VERTICAL);
					} else {
						builder.orientation(Orientation.HORIZONTAL);
					}
				} else if ("length".equals(key)) {
					BigDecimal d = new BigDecimal(e.getValue());
					if (d.doubleValue() > 0.0) {
						builder.length(Length.valueOf(d, Unit.METER));
					}
				}
			}
			images.add(builder.build().convertTo(Unit.METER));
		}
		return images;
	}

	/**
	 * Gets the name of the session in this CML.
	 * 
	 * @return the name of the session in the CML.
	 */
	public String getSession() {
		if (handler == null) {
			getModels();
		}
		return handler.session;
	}

	/**
	 * Gets the names of the tracks in this CML.
	 * 
	 * @return the names of the tracks.
	 */
	public ImmutableSet<String> getTracks() {
		if (handler == null) {
			getModels();
		}
		return ImmutableSet.copyOf(handler.tracks);
	}

	@Override
	protected List<Map<String, String>> parseModels() {
		try {
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			handler = new CMLHandler();
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
