package org.corewall.geology.formats;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.corewall.data.io.Filters;
import org.corewall.geology.io.XMLDataReader;
import org.corewall.geology.models.XYDataSet;
import org.junit.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests for {@link XMLDataReader}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class XMLDataReaderTest {

	/**
	 * Tests parsing Corelyzer XML data into {@link XYDataSet}s.
	 * 
	 * @throws IOException
	 *             indicates a problem with the test.
	 */
	@Test
	public void testModeled() throws IOException {
		XMLDataReader data = new XMLDataReader(this.getClass().getResource("data.xml").openStream());
		assertEquals(ImmutableSet.of("density"), data.getDataSets());
		XYDataSet density = data.getDataSet("density");
		assertNotNull(density);
		assertEquals(97, density.getSize());

		XYDataSet slot0 = data.getDataSetBySlot("0");
		assertNotNull(slot0);
		assertEquals(97, slot0.getSize());
	}

	/**
	 * Tests parsing Corelyzer XML data into raw model maps.
	 * 
	 * @throws IOException
	 *             indicates a problem with the test.
	 */
	@Test
	public void testRaw() throws IOException {
		XMLDataReader data = new XMLDataReader(this.getClass().getResource("data.xml").openStream());
		assertEquals(98, data.getModels().size());
		assertEquals(1, data.getModels(Filters.property("type", "section")).size());
		assertEquals(97, data.getModels(Filters.property("type", "sensor")).size());
	}
}
