package org.corewall.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import javax.swing.JFileChooser;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.corewall.ProjectManager;
import org.corewall.data.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.io.Closeables;
import com.google.inject.Singleton;
import com.google.inject.internal.Lists;

/**
 * A default implementation of the {@link ProjectManager} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@Singleton
public class DefaultProjectManager implements ProjectManager {
	private static final String CORE_WALL = "CoreWall";
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProjectManager.class);
	private static final String PROJECT_XML = "project.xml";
	private static final String PROJECTS = "Projects";
	protected final File root;

	/**
	 * Create a new DefaultProjectManager.
	 */
	public DefaultProjectManager() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("windows")) {
			// should point to My Documents equivalent regardless of locale
			File docs = new JFileChooser().getFileSystemView().getDefaultDirectory();
			root = new File(docs, CORE_WALL + File.separator + PROJECTS);
		} else if (os.startsWith("mac os")) {
			root = new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + CORE_WALL
					+ File.separator + PROJECTS);
		} else {
			root = new File(System.getProperty("user.home") + File.separator + CORE_WALL + File.separator + PROJECTS);
		}
		root.mkdirs();
	}

	/**
	 * Create a new DefaultProjectManager with the specified root.
	 * 
	 * @param root
	 *            the root.
	 */
	public DefaultProjectManager(final File root) {
		this.root = root;
	}

	public void add(final Project project) throws IOException, ProjectExistsException {
		// check for uniqueness
		boolean unique = true;
		for (Project p : getProjects()) {
			if (p.getId().equals(project.getId())) {
				unique = false;
			}
		}
		if (!unique) {
			throw new ProjectExistsException(project.getId());
		}

		// write out the file
		File dir = new File(root, project.getId());
		dir.mkdirs();

		ProjectWriter writer = null;
		try {
			writer = new ProjectWriter(new FileWriter(new File(dir, PROJECT_XML)));
			writer.write(project);
		} finally {
			Closeables.closeQuietly(writer);
		}
	}

	public ImmutableList<Project> getProjects() {
		Builder<Project> projects = ImmutableList.builder();
		for (File file : scanProjects()) {
			Project project = parseProject(file);
			if (project != null) {
				projects.add(project);
			}
		}
		return projects.build();
	}

	public File getRoot() {
		return root;
	}

	@Override
	public void overwrite(final Project project) throws IOException {
		// find the path
		URL path = null;
		for (Project p : getProjects()) {
			if (p.getId().equals(project.getId())) {
				path = p.getPath();
			}
		}

		ProjectWriter writer = null;
		try {
			File dir;
			if (path == null) {
				dir = new File(root, project.getId());
			} else {
				dir = new File(path.toURI());
			}
			dir.mkdirs();
			writer = new ProjectWriter(new FileWriter(new File(dir, PROJECT_XML)));
			writer.write(project);
		} catch (URISyntaxException e) {
			throw new IOException("Invalid URI", e);
		} finally {
			Closeables.closeQuietly(writer);
		}
	}

	protected Project parseProject(final File file) {
		FileInputStream in = null;
		ProjectReader handler = new ProjectReader(file);
		Project project = null;
		try {
			in = new FileInputStream(file);
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			SAXParser parser = factory.newSAXParser();
			parser.parse(in, handler);
			project = handler.getProject();
		} catch (ParserConfigurationException e) {
			LOGGER.error("No SAX parser", e);
			throw new RuntimeException("No SAX parser", e);
		} catch (SAXException e) {
			LOGGER.error("XML parsing error", e);
		} catch (IOException e) {
			LOGGER.error("I/O error", e);
			throw new RuntimeException("I/O error", e);
		} finally {
			Closeables.closeQuietly(in);
		}
		return project;
	}

	protected List<File> scanProjects() {
		List<File> projects = Lists.newArrayList();
		for (File dir : root.listFiles()) {
			if (dir.isDirectory()) {
				File project = new File(dir, PROJECT_XML);
				if (project.exists()) {
					projects.add(project);
				}
			}
		}
		return projects;
	}
}
