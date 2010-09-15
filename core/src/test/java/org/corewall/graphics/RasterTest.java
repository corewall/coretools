package org.corewall.graphics;

import java.io.File;
import java.io.IOException;

import org.corewall.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link RasterGraphics}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RasterTest {
	@BeforeClass
	public static void startPlatform() {
		Platform.start();
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
