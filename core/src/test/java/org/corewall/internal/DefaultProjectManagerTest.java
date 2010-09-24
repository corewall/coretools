package org.corewall.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.corewall.Platform;
import org.corewall.data.Project;
import org.corewall.data.Project.Attr;
import org.corewall.data.Project.ManifestEntry;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for {@link DefaultProjectManager}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class DefaultProjectManagerTest {
	protected static DefaultProjectManager projects;

	/**
	 * Create a single project manager pointed at a test directory.
	 * 
	 * @throws Exception
	 *             should not happen.
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		URL url = DefaultProjectManagerTest.class.getResource("/projects");
		File root = new File(url.toURI());
		projects = new DefaultProjectManager(root);

		Platform.start();
	}

	/**
	 * Test that the project was found and parsed properly.
	 */
	@Test
	public void testGetProjects() {
		assertEquals(1, projects.getProjects().size());
		Project p = projects.getProjects().iterator().next();
		assertEquals("test", p.getId());
		assertEquals("Test Project", p.getName());
		assertNotNull(p.getPath());
		List<ManifestEntry> manifest = p.getManifest();
		assertEquals(2, manifest.size());
		ManifestEntry e1 = manifest.get(0);
		assertEquals("Sections", e1.getName());
		assertEquals("Section", e1.getType());
		assertEquals("tsv", e1.getFormat());
		assertNotNull(e1.getPath());
		ManifestEntry e2 = manifest.get(1);
		assertEquals("Images", e2.getName());
		assertEquals("Image", e2.getType());
		assertEquals("tsv", e2.getFormat());
		assertNull(e2.getPath());
		assertNull(p.getAttribute(Attr.DESCRIPTION));
		assertEquals("andrill", p.getAttribute(Attr.PROGRAM));
		assertEquals("1", p.getAttribute(Attr.EXPEDITION));
		assertEquals("1", p.getAttribute(Attr.SITE));
		assertEquals("b", p.getAttribute(Attr.HOLE));
		assertEquals("45", p.getAttribute(Attr.LATITUDE));
		assertEquals("66", p.getAttribute(Attr.LONGITUDE));
	}

	/**
	 * Test that the root was set.
	 */
	@Test
	public void testGetRoot() {
		assertNotNull(projects);
		assertNotNull(projects.getRoot());
		assertTrue(projects.getRoot().exists());
	}
}
