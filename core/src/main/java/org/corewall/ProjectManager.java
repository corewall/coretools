package org.corewall;

import java.io.File;
import java.io.IOException;

import org.corewall.data.Project;
import org.corewall.internal.DefaultProjectManager;

import com.google.common.collect.ImmutableList;
import com.google.inject.ImplementedBy;

/**
 * Manages access to the persistent {@link Project}s available to the platform.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@ImplementedBy(DefaultProjectManager.class)
public interface ProjectManager {

	/**
	 * Indicates an attempt to overwrite an existing project.
	 */
	class ProjectExistsException extends Exception {
		private static final long serialVersionUID = 1L;

		public ProjectExistsException(final String id) {
			super(id);
		}
	}

	/**
	 * Add a new managed project.
	 * 
	 * @param project
	 *            the project.
	 * @throws ProjectExistsException
	 *             thrown if the project id is not unique.
	 * @throws IOException
	 *             thrown if there is a problem writing the project file.
	 * @see ProjectManager#overwrite(Project);
	 */
	void add(Project project) throws ProjectExistsException, IOException;

	/**
	 * Gets the list of available projects.
	 * 
	 * @return the list of projects.
	 */
	ImmutableList<Project> getProjects();

	/**
	 * Gets the root of this project manager.
	 * 
	 * @return the root.
	 */
	File getRoot();

	/**
	 * Overwrite a managed project.
	 * 
	 * @param project
	 *            the project.
	 * @throws IOException
	 *             thrown if there is a problem writing the project file.
	 */
	void overwrite(Project project) throws IOException;
}
