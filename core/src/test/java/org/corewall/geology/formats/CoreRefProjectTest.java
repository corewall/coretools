package org.corewall.geology.formats;

import static org.junit.Assert.assertEquals;

import org.corewall.Platform;
import org.corewall.geology.io.CoreRefProject;
import org.junit.Test;

public class CoreRefProjectTest {

	static {
		Platform.start();
	}

	@Test
	public void test() {
		CoreRefProject project = new CoreRefProject("and1-1b");
		assertEquals("and1-1b", project.getName());
		assertEquals(0, project.getProperties().size());
		assertEquals(0, project.getSections().size());
		assertEquals(1278, project.getImages().size());
	}
}
