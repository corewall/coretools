package org.corewall.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.corewall.Platform;
import org.corewall.data.Project;
import org.corewall.data.Project.Attr;
import org.corewall.data.Project.ManifestEntry;
import org.corewall.internal.DefaultProject.DefaultManifestEntry;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.google.common.io.Closeables;

/**
 * Exercises the {@link ProjectWriter} class.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ProjectWriterTest {
	/**
	 * Create a single project manager pointed at a test directory.
	 * 
	 * @throws Exception
	 *             should not happen.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		Platform.start();
	}

	protected DefaultProject project;

	/**
	 * Create an example project to serialize.
	 * 
	 * @throws MalformedURLException
	 *             should never be thrown.
	 */
	@Before
	public void setUp() throws MalformedURLException {
		project = new DefaultProject();
		project.setId("test");
		project.setPath(ProjectWriterTest.class.getResource("/projects/test"));
		project.setName("Test Project");
		project.setAttribute(Attr.LATITUDE, "45");
		project.setAttribute(Attr.LONGITUDE, "66");
		project.setAttribute(Attr.PROGRAM, "andrill");
		project.setAttribute(Attr.EXPEDITION, "1");
		project.setAttribute(Attr.SITE, "1");
		project.setAttribute(Attr.HOLE, "b");
		project.addEntry(new DefaultManifestEntry("Sections", "Section", "tsv", ProjectWriterTest.class
				.getResource("/projects/test/sections.dat")));
		project.addEntry(new DefaultManifestEntry("Images", "Image", "tsv", null));
	}

	/**
	 * Test serializing and re-reading things in.
	 * 
	 * @throws IOException
	 *             should not be thrown.
	 */
	@Test
	public void testWriter() throws IOException {
		File file = new File("build/test.xml");

		// write out the project
		ProjectWriter writer = null;
		try {
			writer = new ProjectWriter(new FileWriter(file));
			writer.write(project);
		} finally {
			Closeables.closeQuietly(writer);
		}

		// read it back in and make sure it is the same
		FileInputStream in = null;
		ProjectReader handler = new ProjectReader(file);
		Project project = null;
		try {
			in = new FileInputStream(file);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(in, handler);
			project = handler.getProject();
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			Closeables.closeQuietly(in);
		}

		// test the project
		assertEquals("test", project.getId());
		assertEquals("Test Project", project.getName());
		assertNotNull(project.getPath());
		List<ManifestEntry> manifest = project.getManifest();
		assertEquals(2, manifest.size());
		ManifestEntry e1 = manifest.get(0);
		assertEquals("Sections", e1.getName());
		assertEquals("Section", e1.getType());
		assertEquals("tsv", e1.getFormat());
		assertNull(e1.getPath());
		ManifestEntry e2 = manifest.get(1);
		assertEquals("Images", e2.getName());
		assertEquals("Image", e2.getType());
		assertEquals("tsv", e2.getFormat());
		assertNull(e2.getPath());
		assertNull(project.getAttribute(Attr.DESCRIPTION));
		assertEquals("andrill", project.getAttribute(Attr.PROGRAM));
		assertEquals("1", project.getAttribute(Attr.EXPEDITION));
		assertEquals("1", project.getAttribute(Attr.SITE));
		assertEquals("b", project.getAttribute(Attr.HOLE));
		assertEquals("45", project.getAttribute(Attr.LATITUDE));
		assertEquals("66", project.getAttribute(Attr.LONGITUDE));
	}
}
