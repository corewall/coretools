package org.corewall.geology.models;

import java.util.Properties;

import com.google.common.collect.ImmutableList;

/**
 * Defines the interface for a project.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface Project {

	/**
	 * Gets the images in this project.
	 * 
	 * @return the immutable list of images.
	 */
	public abstract ImmutableList<Image> getImages();

	/**
	 * Gets the name of this project.
	 * 
	 * @return the project.
	 */
	public abstract String getName();

	/**
	 * Gets the properties of this project.
	 * 
	 * @return the properties.
	 */
	public abstract Properties getProperties();

	/**
	 * Gets the sections in this project.
	 * 
	 * @return the immutable list of sections.
	 */
	public abstract ImmutableList<Section> getSections();
}