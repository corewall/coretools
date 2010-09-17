package org.corewall.scene;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.corewall.data.models.Unit;
import org.corewall.graphics.GraphicsContext;
import org.corewall.graphics.Paper;
import org.corewall.scene.edit.CommandStack;
import org.corewall.scene.event.SceneEventHandler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Wraps an existing scene and provides paged rendering.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class PageableScene implements Scene {
	protected final Scene scene;
	protected final Paper paper;
	protected double start = 0;
	protected double perPage = 1;
	protected final boolean renderHeader;
	protected final boolean renderFooter;

	/**
	 * Create a new PageableScene.
	 * 
	 * @param scene
	 *            the scene.
	 * @param paper
	 *            the paper.
	 * @param perPage
	 *            the number of scene units per page.
	 */
	public PageableScene(final Scene scene, final Paper paper, final double perPage) {
		this(scene, paper, scene.getContentSize().getY() / scene.getScalingFactor(), perPage, true, true);
	}

	/**
	 * Create a new PageableScene.
	 * 
	 * @param scene
	 *            the scene.
	 * @param paper
	 *            the paper.
	 * @param start
	 *            the starting value.
	 * @param perPage
	 *            the number of scene units per page.
	 */
	public PageableScene(final Scene scene, final Paper paper, final double start, final double perPage) {
		this(scene, paper, start, perPage, true, true);
	}

	/**
	 * Create a new PageableScene.
	 * 
	 * @param scene
	 *            the scene.
	 * @param paper
	 *            the paper.
	 * @param start
	 *            the starting value.
	 * @param perPage
	 *            the number of scene units per page.
	 * @param renderHeader
	 *            the render header flag.
	 * @param renderFooter
	 *            the render footer flag.
	 */
	public PageableScene(final Scene scene, final Paper paper, final double start, final double perPage,
			final boolean renderHeader, final boolean renderFooter) {
		this.scene = scene;
		this.paper = paper;
		this.start = start;
		this.renderHeader = renderHeader;
		this.renderFooter = renderFooter;
		setPreferredWidth(paper.getPrintableWidth());
		setPerPage(perPage);
	}

	public void addChangeListener(final ChangeListener l) {
		scene.addChangeListener(l);
	}

	public void addSelectionListener(final SelectionListener l) {
		scene.addSelectionListener(l);
	}

	public void addTrack(final Track track, final String constraints) {
		scene.addTrack(track, constraints);
	}

	public Object findAt(final Point2D screen, final Part part) {
		return scene.findAt(screen, part);
	}

	public CommandStack getCommandStack() {
		return scene.getCommandStack();
	}

	public Rectangle2D getContentSize() {
		return scene.getContentSize();
	}

	/**
	 * Gets the content size for the specified page.
	 * 
	 * @param page
	 *            the page number.
	 * @return the content size.
	 */
	public Rectangle2D getContentSize(final int page) {
		Rectangle2D content = scene.getContentSize();
		return new Rectangle2D.Double(content.getX(), (page - 1) * perPage * scene.getScalingFactor(), content
				.getWidth(), perPage * scene.getScalingFactor());
	}

	public SceneEventHandler getEventHandler() {
		return scene.getEventHandler();
	}

	public Rectangle2D getFooterSize() {
		return scene.getFooterSize();
	}

	public Rectangle2D getHeaderSize() {
		return scene.getHeaderSize();
	}

	public Orientation getOrientation() {
		return scene.getOrientation();
	}

	public Origin getOrigin() {
		return scene.getOrigin();
	}

	/**
	 * Gets the number of pages with the specified start Y value in scene
	 * coordinates.
	 * 
	 * @return the number of pages.
	 */
	public int getPageCount() {
		Rectangle2D content = scene.getContentSize();
		double height = content.getMaxY() / scene.getScalingFactor() - start;
		return (int) Math.ceil(height / perPage);
	}

	public String getParameter(final String name, final String defaultValue) {
		return scene.getParameter(name, defaultValue);
	}

	public ImmutableMap<String, String> getParameters() {
		return scene.getParameters();
	}

	/**
	 * Gets the number of scene units visible per page.
	 * 
	 * @return the number of scene units visible per page.
	 */
	public double getPerPage() {
		return perPage;
	}

	public double getPreferredWidth() {
		return scene.getPreferredWidth();
	}

	public double getScalingFactor() {
		return scene.getScalingFactor();
	}

	/**
	 * Get the wrapped scene.
	 * 
	 * @return the scene.
	 */
	public Scene getScene() {
		return scene;
	}

	public Unit getSceneUnits() {
		return scene.getSceneUnits();
	}

	public Selection getSelection() {
		return scene.getSelection();
	}

	/**
	 * Gets the starting position to begin rendering pages at.
	 * 
	 * @return the starting position in scene units.
	 */
	public double getStart() {
		return start;
	}

	public String getTrackConstraints(final Track track) {
		return scene.getTrackConstraints(track);
	}

	public ImmutableList<Track> getTracks() {
		return scene.getTracks();
	}

	public void invalidate() {
		scene.invalidate();
	}

	public void removeChangeListener(final ChangeListener l) {
		scene.removeChangeListener(l);
	}

	public void removeSelectionListener(final SelectionListener l) {
		scene.removeSelectionListener(l);
	}

	public void renderContents(final GraphicsContext graphics, final Rectangle2D clip) {
		scene.renderContents(graphics, clip);
	}

	/**
	 * Render a page of the scene.
	 * 
	 * @param page
	 *            the page.
	 * @param graphics
	 *            the graphics.
	 */
	public void renderContents(final int page, final GraphicsContext graphics) {
		scene.setParameter("page", "" + page);
		Rectangle2D contents = scene.getContentSize();
		renderContents(graphics, new Rectangle2D.Double(contents.getX(), (start + (page - 1) * perPage)
				* scene.getScalingFactor(), contents.getWidth(), perPage * scene.getScalingFactor()));
		scene.setParameter("page", null);
	}

	public void renderFooter(final GraphicsContext graphics) {
		scene.renderFooter(graphics);
	}

	/**
	 * Render the footer for the specified page.
	 * 
	 * @param page
	 *            the page.
	 * @param graphics
	 *            the graphics.
	 */
	public void renderFooter(final int page, final GraphicsContext graphics) {
		scene.setParameter("page", "" + page);
		renderFooter(graphics);
		scene.setParameter("page", null);
	}

	public void renderHeader(final GraphicsContext graphics) {
		scene.renderHeader(graphics);
	}

	/**
	 * Render the header for the specified page.
	 * 
	 * @param page
	 *            the page.
	 * @param graphics
	 *            the graphics.
	 */
	public void renderHeader(final int page, final GraphicsContext graphics) {
		scene.setParameter("page", "" + page);
		renderHeader(graphics);
		scene.setParameter("page", null);
	}

	public void setCommandStack(final CommandStack commandStack) {
		scene.setCommandStack(commandStack);
	}

	public void setOrientation(final Orientation orientation) {
		scene.setOrientation(orientation);
	}

	public void setParameter(final String name, final String value) {
		scene.setParameter(name, value);
	}

	/**
	 * Sets the number of scene units visible per page.
	 * 
	 * @param perPage
	 *            the number of scene units visible per page.
	 */
	public void setPerPage(final double perPage) {
		this.perPage = perPage;
		double content = paper.getPrintableHeight();
		if (renderHeader) {
			content -= scene.getHeaderSize().getHeight();
		}
		if (renderFooter) {
			content -= scene.getFooterSize().getHeight();
		}
		scene.setScalingFactor(content / perPage);
	}

	public void setPreferredWidth(final double width) {
		scene.setPreferredWidth(width);
	}

	public void setScalingFactor(final double scale) {
		scene.setScalingFactor(scale);
	}

	public void setSceneUnits(final Unit unit) {
		scene.setSceneUnits(unit);
	}

	public void setSelection(final Selection selection) {
		scene.setSelection(selection);
	}

	/**
	 * Sets the starting position in scene units.
	 * 
	 * @param start
	 *            the starting position.
	 */
	public void setStart(final double start) {
		this.start = start;
	}

	public void validate() {
		scene.validate();
	}
}
