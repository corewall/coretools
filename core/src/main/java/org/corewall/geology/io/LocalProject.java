package org.corewall.geology.io;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.corewall.data.Filters;
import org.corewall.data.Model;
import org.corewall.data.io.CSVModelReader;
import org.corewall.data.io.CSVModelWriter;
import org.corewall.data.io.ModelReader;
import org.corewall.geology.models.EditableProject;
import org.corewall.geology.models.Image;
import org.corewall.geology.models.Section;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Closeables;

/**
 * A project consists of all data related to a specific outcrop or hole.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class LocalProject implements EditableProject {

	/**
	 * Create a new project.
	 * 
	 * @param name
	 *            the name of the project.
	 * @param file
	 *            the project file.
	 * @return the project.
	 * @throws IOException
	 *             thrown if there was a problem creating the project.
	 */
	public static LocalProject create(final String name, final File file) throws IOException {
		LocalProject project = new LocalProject(name, file);
		project.save();
		return project;
	}

	/**
	 * Opens a project.
	 * 
	 * @param file
	 *            the project file.
	 * @return the project.
	 * @throws IOException
	 *             thrown if there was a problem opening the project.
	 */
	public static LocalProject open(final File file) throws IOException {
		LocalProject project = new LocalProject(file);
		try {
			project.open();
		} catch (Exception e) {
			throw new IOException("Invalid project: " + file.getAbsolutePath(), e);
		}
		return project;
	}

	protected boolean dirty = false;
	protected final List<Image> images = Lists.newLinkedList();
	protected final Set<Listener> listeners = new CopyOnWriteArraySet<Listener>();
	protected final File projectFile;
	protected final Properties properties = new Properties();
	protected final List<Section> sections = Lists.newLinkedList();

	/**
	 * Creates a new project from the specified file.
	 * 
	 * @param projectFile
	 *            the project file.
	 */
	protected LocalProject(final File projectFile) {
		this.projectFile = projectFile;
	}

	/**
	 * Creates a new project with the name and file.
	 * 
	 * @param name
	 *            the project name.
	 * @param projectFile
	 *            the project file.
	 */
	protected LocalProject(final String name, final File projectFile) {
		this.projectFile = projectFile;
		properties.setProperty("name", name);
	}

	public void add(final Image image) {
		if (images.add(image)) {
			added(image);
		}
	}

	public void add(final Section section) {
		if (sections.add(section)) {
			added(section);
		}
	}

	protected void added(final Model model) {
		dirty = true;
		for (Listener l : listeners) {
			l.modelAdded(model);
		}
	}

	public void addListener(final Listener l) {
		listeners.add(l);
	}

	public void changed(final Model model) {
		dirty = true;
		for (Listener l : listeners) {
			l.modelChanged(model);
		}
	}

	public ImmutableList<Image> getImages() {
		return ImmutableList.copyOf(images);
	}

	protected String getImagesFile() {
		return properties.getProperty("images.file", "images.tsv");
	}

	public String getName() {
		return properties.getProperty("name");
	}

	public Properties getProperties() {
		return properties;
	}

	public ImmutableList<Section> getSections() {
		return ImmutableList.copyOf(sections);
	}

	protected String getSectionsFile() {
		return properties.getProperty("sections.file", "sections.tsv");
	}

	public boolean isDirty() {
		return dirty;
	}

	/**
	 * Opens this project.
	 * 
	 * @throws IOException
	 *             thrown if there is an issue opening
	 */
	protected void open() throws IOException {
		readProjectFile();
		readModelTable(getSectionsFile(), sections, Section.factory());
		readModelTable(getImagesFile(), images, Image.factory());
	}

	protected <T extends Model> void readModelTable(final String filename, final List<T> models,
			final ModelReader.Factory<T> factory) throws IOException {
		File table = new File(projectFile.getParentFile(), filename);
		if (table.exists()) {
			FileReader in = null;
			try {
				in = new FileReader(table);
				CSVModelReader csv = new CSVModelReader(in, '\t');
				models.addAll(csv.getModels(Filters.all(), factory));
			} finally {
				Closeables.closeQuietly(in);
			}
		}
	}

	protected void readProjectFile() throws IOException {
		FileReader in = null;
		try {
			in = new FileReader(projectFile);
			properties.load(in);
		} finally {
			Closeables.closeQuietly(in);
		}
	}

	public void remove(final Image image) {
		if (images.remove(image)) {
			remove(image);
		}
	}

	public void remove(final Section section) {
		if (sections.remove(section)) {
			removed(section);
		}
	}

	protected void removed(final Model model) {
		dirty = true;
		for (Listener l : listeners) {
			l.modelRemoved(model);
		}
	}

	public void removeListener(final Listener l) {
		listeners.remove(l);
	}

	public void save() throws IOException {
		writeProjectFile();
		writeModelTable(getSectionsFile(), sections, new String[] { "name", "top", "base" });
		writeModelTable(getImagesFile(), images, new String[] { "orientation", "top", "base", "path", "local" });
		dirty = false;
	}

	protected void writeModelTable(final String filename, final List<? extends Model> models, final String[] keys)
			throws IOException {
		// build our header
		Map<String, String> headers = Maps.newLinkedHashMap();
		for (String s : keys) {
			headers.put(s, s);
		}

		// write out our TSV file
		FileWriter out = null;
		try {
			out = new FileWriter(new File(projectFile.getParentFile(), filename));
			CSVModelWriter csv = new CSVModelWriter(out, '\t', headers);
			csv.write(models);
		} finally {
			Closeables.closeQuietly(out);
		}
	}

	protected void writeProjectFile() throws IOException {
		projectFile.getParentFile().mkdirs();
		FileWriter out = null;
		try {
			out = new FileWriter(projectFile);
			properties.store(out, null);
		} finally {
			Closeables.closeQuietly(out);
		}
	}
}
