package org.corewall.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.corewall.Locator;
import org.corewall.Platform;
import org.corewall.data.Project;
import org.corewall.internal.DefaultProject.DefaultManifestEntry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses the standard Project XML format.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ProjectParser extends DefaultHandler {
	protected StringBuilder buffer = new StringBuilder();
	protected final File file;
	protected final Locator locator;
	protected final DefaultProject project;

	/**
	 * Create a new ProjectParser.
	 * 
	 * @param file
	 *            the project file.
	 */
	public ProjectParser(final File file) {
		project = new DefaultProject();
		project.setPath(file.getParentFile());
		locator = Platform.getService(Locator.class);
		this.file = file.getParentFile();
	}

	@Override
	public void characters(final char[] ch, final int start, final int length) throws SAXException {
		buffer.append(ch, start, length);
	}

	protected String checkAttr(final String name, final String value) throws SAXException {
		if ((value == null) || "".equals(value.trim())) {
			throw new SAXException("'" + name + "' attribute must be specified and non-empty");
		} else {
			return value;
		}
	}

	@Override
	public void endElement(final String uri, final String localName, final String qName) throws SAXException {
		if ("name".equals(qName)) {
			String value = val();
			if (value == null) {
				throw new SAXException("'name' element must be specified and non-empty");
			} else {
				project.setName(value);
			}
		} else if (!"manifest".equals(qName)) {
			String name = qName;
			int i = name.indexOf(':');
			if (i > -1) {
				name = name.substring(i + 1);
			}
			project.put(name, val());
		}
	}

	/**
	 * Gets the parsed project.
	 * 
	 * @return the project.
	 * @throws SAXException
	 *             thrown if the parsed project was not complete.
	 */
	public Project getProject() throws SAXException {
		if (project.getName() == null) {
			throw new SAXException("'name' element must be specified and non-empty");
		}
		return project;
	}

	@Override
	public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
			throws SAXException {
		buffer = new StringBuilder();
		if ("project".equals(qName)) {
			project.setId(checkAttr("id", attributes.getValue("id")));
		} else if ("entry".equals(qName)) {
			String name = checkAttr("name", attributes.getValue("name"));
			String type = checkAttr("type", attributes.getValue("type"));
			String format = checkAttr("format", attributes.getValue("format"));
			String relative = checkAttr("relative", attributes.getValue("relative"));
			String location = checkAttr("path", attributes.getValue("path"));
			URL url = null;
			if (Boolean.parseBoolean(relative)) {
				File path = new File(file, location);
				if (path.exists()) {
					try {
						url = path.toURI().toURL();
					} catch (MalformedURLException e) {
						// should never happen as verified the file exists
					}
				}
			} else {
				url = locator.getResource(location);
			}
			project.add(new DefaultManifestEntry(name, type, format, url));
		}
	}

	protected String val() {
		String value = buffer.toString();
		return ("".equals(value) ? null : value);
	}
}
