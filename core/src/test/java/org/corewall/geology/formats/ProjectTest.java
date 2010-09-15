package org.corewall.geology.formats;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.corewall.Platform;
import org.corewall.data.Model;
import org.corewall.geology.io.LocalProject;
import org.corewall.geology.models.EditableProject;
import org.corewall.geology.models.Project;
import org.corewall.geology.models.Section;
import org.corewall.geology.models.EditableProject.Listener;
import org.junit.Test;

public class ProjectTest {
	class MockListener implements Listener {
		Model added;
		Model changed;
		Model removed;

		public void modelAdded(final Model model) {
			added = model;
		}

		public void modelChanged(final Model model) {
			changed = model;
		}

		public void modelRemoved(final Model model) {
			removed = model;
		}
	}

	static {
		Platform.start();
	}

	@Test
	public void testCreate() throws Exception {
		// create a new project
		File file = File.createTempFile("project", ".properties");
		Project project = LocalProject.create("New Project", file);
		assertEquals("New Project", project.getName());

		// now try re-opening the project
		Project project2 = LocalProject.open(file);
		assertEquals("New Project", project2.getName());
	}

	@Test
	public void testListener() throws Exception {
		MockListener l = new MockListener();

		File file = new File(this.getClass().getResource("project.properties").toURI());
		EditableProject project = LocalProject.open(file);
		project.addListener(l);

		Section toAdd = new Section.Builder().name("Added").top(3).base(4).build();
		project.add(toAdd);
		assertEquals(4, project.getSections().size());
		assertEquals(toAdd, l.added);

		Section toRemove = project.getSections().get(0);
		project.remove(toRemove);
		assertEquals(3, project.getSections().size());
		assertEquals(toRemove, l.removed);

		Section toChange = project.getSections().get(0);
		project.changed(toChange);
		assertEquals(3, project.getSections().size());
		assertEquals(toChange, l.changed);
	}

	@Test
	public void testOpen() throws Exception {
		File file = new File(this.getClass().getResource("project.properties").toURI());
		Project project = LocalProject.open(file);
		assertEquals("Test Project", project.getName());
		assertEquals(3, project.getSections().size());
	}

	@Test
	public void testSave() throws Exception {
		// create a new project
		File file = File.createTempFile("project", ".properties");
		EditableProject project = LocalProject.create("New Project", file);

		// add a section and save
		Section s = new Section.Builder().name("Added").top(3).base(4).build();
		project.add(s);
		assertEquals(1, project.getSections().size());
		assertEquals(s, project.getSections().get(0));
		project.save();

		// now try re-opening the project
		Project project2 = LocalProject.open(file);
		assertEquals(1, project2.getSections().size());
		assertEquals(s, project2.getSections().get(0));
	}
}
