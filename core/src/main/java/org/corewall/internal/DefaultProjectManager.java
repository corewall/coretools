package org.corewall.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import com.google.inject.internal.Lists;

/**
 * A default implementation of the {@link ProjectManager} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
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

	protected Project parseProject(final File file) {
		FileInputStream in = null;
		ProjectParser handler = new ProjectParser(file);
		Project project = null;
		try {
			in = new FileInputStream(file);
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
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
