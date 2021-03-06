package org.corewall.internal;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.corewall.Locator;
import org.corewall.Platform;
import org.corewall.data.Project;
import org.corewall.data.Project.Attr;
import org.corewall.internal.DefaultProject.DefaultManifestEntry;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses the standard Project XML format.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ProjectReader extends DefaultHandler {
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
	public ProjectReader(final File file) {
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
		if (Project.PROJECT_URI.equals(uri) && "name".equals(localName)) {
			String value = val();
			if (value == null) {
				throw new SAXException("'name' element must be specified and non-empty");
			} else {
				project.setName(value);
			}
		} else if (Project.PROJECT_URI.equals(uri) && "description".equals(localName)) {
			project.setAttribute(Attr.DESCRIPTION, val());
		} else if (Project.PROJECT_URI.equals(uri) && "program".equals(localName)) {
			project.setAttribute(Attr.PROGRAM, val());
		} else if (Project.PROJECT_URI.equals(uri) && "expedition".equals(localName)) {
			project.setAttribute(Attr.EXPEDITION, val());
		} else if (Project.PROJECT_URI.equals(uri) && "site".equals(localName)) {
			project.setAttribute(Attr.SITE, val());
		} else if (Project.PROJECT_URI.equals(uri) && "hole".equals(localName)) {
			project.setAttribute(Attr.HOLE, val());
		} else if (Project.GEO_URI.equals(uri) && "lat".equals(localName)) {
			project.setAttribute(Attr.LATITUDE, val());
		} else if (Project.GEO_URI.equals(uri) && "long".equals(localName)) {
			project.setAttribute(Attr.LONGITUDE, val());
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
		if (Project.PROJECT_URI.equals(uri) && "project".equals(localName)) {
			project.setId(checkAttr("id", attributes.getValue("id")));
		} else if (Project.PROJECT_URI.equals(uri) && "entry".equals(localName)) {
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
			project.addEntry(new DefaultManifestEntry(name, type, format, url));
		}
	}

	protected String val() {
		String value = buffer.toString();
		return ("".equals(value) ? null : value);
	}
}
