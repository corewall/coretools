package org.corewall.geology.tracks;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Rectangle2D;

import org.corewall.data.Length;
import org.corewall.geology.models.Section;
import org.corewall.graphics.GraphicsContext;
import org.corewall.graphics.driver.Driver.LineStyle;
import org.corewall.scene.AbstractTrack;
import org.corewall.scene.Scene;

/**
 * A track for rendering {@link Section} models.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class SectionTrack extends AbstractTrack<Section> {
	private static final Color FG = new Color(0.2f, 0.2f, 0.2f, 0.8f);
	private static final Color BG = new Color(0.8f, 0.8f, 0.8f, 0.8f);
	private static final Font FONT = new Font("SanSerif", Font.PLAIN, 12);

	protected Length max = null;
	protected Length min = null;

	@Override
	public Rectangle2D getContentSize() {
		if ((max == null) || (min == null)) {
			return Scene.ZERO;
		} else {
			return new Rectangle2D.Double(0, toScreen(min), 36, scale(max.minus(min)));
		}
	}

	@Override
	protected void invalidate() {
		// calculate the max/min from our sections
		max = null;
		min = null;
		for (Section s : models) {
			Length t = s.getTop();
			Length b = s.getBase();
			if ((max == null) || (t.compareTo(max) > 0)) {
				max = t;
			}
			if ((max == null) || (b.compareTo(max) > 0)) {
				max = b;
			}
			if ((min == null) || (t.compareTo(min) < 0)) {
				min = t;
			}
			if ((min == null) || (b.compareTo(min) < 0)) {
				min = b;
			}
		}

		// invalidate the scene
		super.invalidate();
	}

	@Override
	public void renderContents(final GraphicsContext graphics, final Rectangle2D bounds) {
		graphics.setState(FG, 2, LineStyle.SOLID, BG);
		for (Section s : models) {
			double[] screen = toScreen(s.getTop(), s.getBase());
			Rectangle2D r = new Rectangle2D.Double(bounds.getMinX(), screen[0], bounds.getWidth(), screen[1] - screen[0]);
			if (r.intersects(bounds)) {
				graphics.fillRectangle(r);
				graphics.drawRectangle(r);
				graphics.drawVerticalStringCenter(r.createIntersection(bounds), FONT, s.getName());
			}
		}
	}
}
