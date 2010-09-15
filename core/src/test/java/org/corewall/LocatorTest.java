package org.corewall;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for the {@link Locator} service.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class LocatorTest {

	@BeforeClass
	public static void startPlatform() {
		Platform.start();
	}

	private Locator locator;

	private void checkExists(final URL url) {
		assertNotNull(url);
		assertTrue(url.toExternalForm().startsWith("file:/"));
		try {
			assertTrue(new File(url.toURI()).exists());
		} catch (URISyntaxException e) {
			assertTrue("Unexpected exception", false);
		}
	}

	@Before
	public void setUp() {
		locator = Platform.getService(Locator.class);
	}

	/**
	 * Tests adding resources and then ensure it is found once added.
	 * 
	 * @throws IOException
	 *             should never happen.
	 */
	@Test
	public void testAddResource() throws IOException {
		File file = new File("./testFile");
		file.createNewFile();
		locator.addResource(file.toURI().toURL());

		URL found = locator.getResource(file.getAbsolutePath());
		checkExists(found);
		file.delete();
	}

	/**
	 * Tests locating http: resources.
	 */
	@Test
	public void testHttp() {
		URL url = locator.getResource("http://google.com");
		assertEquals("http://google.com", url.toExternalForm());
	}

	/**
	 * Tests invalid resource handling.
	 */
	@Test
	public void testInvalid() {
		assertNull(locator.getResource("rsrc://////invalid/resource"));

		String file = locator.getResource("foobar").toExternalForm();
		assertTrue(file.startsWith("file:"));
		assertTrue(file.endsWith("foobar"));
	}

	/**
	 * Tests finding resources on the classpath.
	 */
	@Test
	public void testLoadClassPath() {
		URL url = locator.getResource("classpath:/META-INF/services/org.andrill.coretools.TestService");
		checkExists(url);
		assertEquals(url, locator.getResource("rsrc:/META-INF/services/org.andrill.coretools.TestService"));
	}
}
