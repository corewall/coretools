package org.corewall;

import java.io.File;

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
}
