package org.corewall.geology.tracks;

import java.awt.geom.Rectangle2D;

import org.corewall.data.models.Length;
import org.corewall.data.models.Unit;
import org.corewall.geology.models.Image;
import org.corewall.graphics.GraphicsContext;
import org.corewall.scene.AbstractTrack;
import org.corewall.scene.Orientation;
import org.corewall.scene.Scene;

/**
 * A track for drawing images.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ImageTrack extends AbstractTrack<Image> {
	protected Length max = null;
	protected Length min = null;
	protected Length width = Length.valueOf(1, Unit.INCH);

	@Override
	public Rectangle2D getContentSize() {
		if ((max == null) || (min == null)) {
			return Scene.ZERO;
		} else {
			return new Rectangle2D.Double(0, toScreen(min), scale(width), scale(max.minus(min)));
		}
	}

	@Override
	protected void invalidate() {
		// calculate the max/min from our images
		max = null;
		min = null;
		for (Image image : models) {
			Length t = image.getTop();
			Length b = image.getBase();
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
		Length top = Length.valueOf(toScene(bounds.getMinY()), scene.getSceneUnits());
		Length bot = Length.valueOf(toScene(bounds.getMaxY()), scene.getSceneUnits());
		for (Image image : models) {
			if ((top.compareTo(image.getBase()) < 0) && (bot.compareTo(image.getTop()) > 0)) {
				// check the width
				Length w;
				if (image.getOrientation() == Orientation.VERTICAL) {
					w = Length.valueOf((image.getWidth()) / image.getDpiX(), Unit.INCH);
				} else {
					w = Length.valueOf((image.getHeight()) / image.getDpiY(), Unit.INCH);
				}
				if ((width == null) || (w.compareTo(width) > 0)) {
					width = w;
					scene.invalidate();
				}

				// draw the image
				double[] screen = toScreen(image.getTop(), image.getBase());
				Rectangle2D r = new Rectangle2D.Double(bounds.getMinX(), screen[0], scale(w), screen[1] - screen[0]);
				graphics.drawImage(r, image.getPath(), image.getOrientation() == Orientation.VERTICAL);
			}
		}
	}
}
