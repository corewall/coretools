package org.corewall.graphics;

import java.io.File;
import java.io.IOException;

import org.corewall.Platform;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link SVGGraphics}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SVGTest {

	@BeforeClass
	public static void startPlatform() {
		Platform.start();
	}

	/**
	 * Tests the SVGGraphics.
	 * 
	 * @throws IOException
	 *             indicates a problem with the test.
	 */
	@Test
	public void testSVG() throws IOException {
		SVGGraphics svg = new SVGGraphics();
		GraphicsHarness.test(svg);
		svg.write(new File("build/test.svg"));
	}
}
