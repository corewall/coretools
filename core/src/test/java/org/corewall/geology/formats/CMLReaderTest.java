package org.corewall.geology.formats;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.corewall.Platform;
import org.corewall.data.io.Filters;
import org.corewall.geology.io.CMLReader;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests for {@link CMLReader}
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CMLReaderTest {

	@BeforeClass
	public static void startPlatform() {
		Platform.start();
	}

	/**
	 * Tests parsing CML into standard models.
	 * 
	 * @throws IOException
	 *             indicates a problem with the test.
	 */
	@Test
	public void testModeled() throws IOException {
		CMLReader cml = new CMLReader(this.getClass().getResource("session.cml").openStream());
		assertEquals(35, cml.getModels().size());
		assertEquals("Default", cml.getSession());
		assertEquals(ImmutableSet.of("Test"), cml.getTracks());
		assertEquals(30, cml.getImages("Test").size());
	}

	/**
	 * Tests parsing CML into raw model maps.
	 * 
	 * @throws IOException
	 *             indicates a problem with the test.
	 */
	@Test
	public void testRaw() throws IOException {
		CMLReader cml = new CMLReader(this.getClass().getResource("session.cml").openStream());
		assertEquals(35, cml.getModels().size());
		assertEquals(1, cml.getModels(Filters.property("type", "scene")).size());
		assertEquals(1, cml.getModels(Filters.property("type", "session")).size());
		assertEquals(1, cml.getModels(Filters.property("type", "dataset")).size());
		assertEquals(1, cml.getModels(Filters.property("type", "track")).size());
		assertEquals(1, cml.getModels(Filters.property("type", "graph")).size());
		assertEquals(30, cml.getModels(Filters.property("type", "core_section")).size());
	}
}
