package org.corewall.internal;

import java.io.Closeable;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import org.corewall.data.Project;
import org.corewall.data.Project.Attr;
import org.corewall.data.Project.ManifestEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.google.common.io.Closeables;

/**
 * Writes a Project XML file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ProjectWriter implements Closeable {
	private static final Logger LOG = LoggerFactory.getLogger(ProjectWriter.class);
	protected final Writer writer;

	/**
	 * Create a new ProjectWriter with the specified writer.
	 * 
	 * @param writer
	 *            the writer.
	 */
	public ProjectWriter(final Writer writer) {
		this.writer = writer;
	}

	@Override
	public void close() throws IOException {
		Closeables.closeQuietly(writer);
	}

	/**
	 * Write the specified project.
	 * 
	 * @param project
	 *            the project.
	 * @throws IOException
	 *             thrown if there is a problem writing the XML.
	 */
	public void write(final Project project) throws IOException {
		try {
			StreamResult stream = new StreamResult(writer);
			SAXTransformerFactory transformer = (SAXTransformerFactory) TransformerFactory.newInstance();
			transformer.setAttribute("indent-number", 4);
			TransformerHandler xml = transformer.newTransformerHandler();
			Transformer serializer = xml.getTransformer();
			serializer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			serializer.setOutputProperty(OutputKeys.INDENT, "yes");
			xml.setResult(stream);

			// create an attributes object for re-use
			AttributesImpl attrs = new AttributesImpl();

			// start our document
			xml.startDocument();

			// write our project node
			attrs.clear();
			attrs.addAttribute("", "xmlns:geo", "xmlns:geo", "CDATA", Project.GEO_URI);
			attrs.addAttribute("", "id", "id", "CDATA", project.getId());
			xml.startElement(Project.PROJECT_URI, "", "project", attrs);

			// write our name node
			attrs.clear();
			xml.startElement("", "", "name", attrs);
			xml.characters(project.getName().toCharArray(), 0, project.getName().length());
			xml.endElement("", "", "name");

			// write our attributes
			writeAttr(xml, Attr.DESCRIPTION, project.getAttribute(Attr.DESCRIPTION));
			writeAttr(xml, Attr.PROGRAM, project.getAttribute(Attr.PROGRAM));
			writeAttr(xml, Attr.EXPEDITION, project.getAttribute(Attr.EXPEDITION));
			writeAttr(xml, Attr.SITE, project.getAttribute(Attr.SITE));
			writeAttr(xml, Attr.HOLE, project.getAttribute(Attr.HOLE));
			writeAttr(xml, Attr.LATITUDE, project.getAttribute(Attr.LATITUDE));
			writeAttr(xml, Attr.LONGITUDE, project.getAttribute(Attr.LONGITUDE));

			// write our manifest
			attrs.clear();
			xml.startElement("", "", "manifest", attrs);
			for (ManifestEntry entry : project.getManifest()) {
				attrs.clear();
				attrs.addAttribute("", "", "name", "CDATA", entry.getName());
				attrs.addAttribute("", "", "type", "CDATA", entry.getType());
				attrs.addAttribute("", "", "format", "CDATA", entry.getFormat());
				URL path = entry.getPath();
				if (path != null) {
					try {
						URI uri = project.getPath().toURI().relativize(path.toURI());
						boolean relative = (!uri.equals(path.toURI()));
						attrs.addAttribute("", "", "path", "CDATA", uri.toString());
						attrs.addAttribute("", "", "relative", "CDATA", Boolean.toString(relative));
					} catch (URISyntaxException e) {
						LOG.error("Invalid URI", e);
					}
				} else {
					attrs.addAttribute("", "", "path", "CDATA", Boolean.toString(true));
					attrs.addAttribute("", "", "relative", "CDATA", Boolean.toString(true));
				}
				xml.startElement("", "", "entry", attrs);
				xml.endElement("", "", "entry");
			}
			xml.endElement("", "", "manifest");

			xml.endElement("", "", "project");
			xml.endDocument();
		} catch (SAXException e) {
			throw new IOException(e);
		} catch (TransformerConfigurationException e) {
			throw new IOException(e);
		}
	}

	protected void writeAttr(final TransformerHandler xml, final Project.Attr attr, final String value)
			throws SAXException {
		if (value != null) {
			String name;
			if (attr == Attr.LATITUDE) {
				name = "geo:lat";
			} else if (attr == Attr.LONGITUDE) {
				name = "geo:long";
			} else {
				name = attr.toString().toLowerCase();
			}
			xml.startElement("", "", name, new AttributesImpl());
			xml.characters(value.toCharArray(), 0, value.length());
			xml.endElement("", "", name);
		}
	}
}
