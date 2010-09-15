package org.corewall.geology.models;

import java.io.IOException;

import org.corewall.data.Model;

/**
 * Defines a project that can be edited.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public interface EditableProject extends Project {
	/**
	 * Listens to changes from projects.
	 */
	interface Listener {
		/**
		 * Called when a model is added to this project.
		 * 
		 * @param model
		 *            the model.
		 */
		void modelAdded(Model model);

		/**
		 * Called when a model is changed in this project.
		 * 
		 * @param model
		 *            the model.
		 */
		void modelChanged(Model model);

		/**
		 * Called when a model is removed from this project.
		 * 
		 * @param model
		 *            the model.
		 */
		void modelRemoved(Model model);
	}

	/**
	 * Adds a {@link Image} to this project.
	 * 
	 * @param image
	 *            the image.
	 */
	public abstract void add(final Image image);

	/**
	 * Adds a {@link Section} to this project.
	 * 
	 * @param section
	 *            the section.
	 */
	public abstract void add(final Section section);

	/**
	 * Adds a listener to this project.
	 * 
	 * @param l
	 *            the listener.
	 */
	public abstract void addListener(final Listener l);

	/**
	 * Indicate that the specified model has changed.
	 * 
	 * @param model
	 *            the model.
	 */
	public abstract void changed(final Model model);

	/**
	 * Checks whether this project is dirty.
	 * 
	 * @return true if the project has changed, false otherwise.
	 */
	public abstract boolean isDirty();

	/**
	 * Removes an {@link Image} from this project.
	 * 
	 * @param image
	 *            the image.
	 */
	public abstract void remove(final Image image);

	/**
	 * Removes a {@link Section} from this project.
	 * 
	 * @param section
	 *            the section.
	 */
	public abstract void remove(final Section section);

	/**
	 * Removes a listener from this project.
	 * 
	 * @param l
	 *            the listener.
	 */
	public abstract void removeListener(final Listener l);

	/**
	 * Saves this project.
	 * 
	 * @throws IOException
	 *             thrown if there is a problem saving the project.
	 */
	public abstract void save() throws IOException;

}