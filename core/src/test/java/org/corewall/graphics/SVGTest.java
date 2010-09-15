package org.corewall.graphics;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.corewall.Platform;
import org.corewall.graphics.SVGGraphics;
import org.junit.Test;

/**
 * Tests for {@link SVGGraphics}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SVGTest extends TestCase {
	static {
		Platform.start();
		(new File("build")).mkdirs();
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
