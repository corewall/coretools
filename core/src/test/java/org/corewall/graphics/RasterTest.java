package org.corewall.graphics;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.corewall.Platform;
import org.corewall.graphics.RasterGraphics;
import org.junit.Test;

/**
 * Tests for {@link RasterGraphics}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RasterTest extends TestCase {
	static {
		Platform.start();
		(new File("build")).mkdirs();
	}

	/**
	 * Test writing out to BMP.
	 * 
	 * @throws IOException
	 *             signals a problem with the test.
	 */
	@Test
	public void testBMP() throws IOException {
		GraphicsHarness.testRaster(new File("build/test.bmp"));
	}

	/**
	 * Test writing out to JPEG.
	 * 
	 * @throws IOException
	 *             signals a problem with the test.
	 */
	@Test
	public void testJPEG() throws IOException {
		GraphicsHarness.testRaster(new File("build/test.jpeg"));
	}

	/**
	 * Test writing out to PNG.
	 * 
	 * @throws IOException
	 *             signals a problem with the test.
	 */
	@Test
	public void testPNG() throws IOException {
		GraphicsHarness.testRaster(new File("build/test.png"));
	}
}
