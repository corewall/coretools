package org.corewall.geology.tracks;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;

import org.corewall.data.Model;
import org.corewall.graphics.GraphicsContext;
import org.corewall.graphics.driver.Driver.LineStyle;
import org.corewall.scene.AbstractTrack;

/**
 * A track to draw a ruler.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class RulerTrack extends AbstractTrack<Model> {
	private static final Font FONT = new Font("SanSerif", Font.PLAIN, 12);

	protected double calculateSpacing(final double screen) {
		double scale = scene.getScalingFactor();
		double multiplier = Math.pow(10, Math.floor(Math.log10(screen / scale)));
		double hash;
		if (1 * scale * multiplier > screen) {
			hash = 1 * multiplier;
		} else if (2 * scale * multiplier > screen) {
			hash = 2 * multiplier;
		} else if (5 * scale * multiplier > screen) {
			hash = 5 * multiplier;
		} else {
			hash = 10 * multiplier;
		}
		return hash;
	}

	@Override
	public Rectangle2D getContentSize() {
		return new Rectangle(0, 0, 72, 0);
	}

	@Override
	public void renderContents(final GraphicsContext graphics, final Rectangle2D bounds) {
		graphics.setState(graphics.getLineColor(), 1, LineStyle.SOLID, graphics.getFill());

		// calculate our label/hash spacing
		double hashStep = calculateSpacing(10);
		double labelStep = calculateSpacing(40);

		// calculate our start/end values
		double start = Math.max(0, toScene(bounds.getY()));
		double end = toScene(bounds.getMaxY());

		// draw hashes
		for (double i = Math.floor(start / hashStep) * hashStep; i < end; i += hashStep) {
			graphics.drawLine(bounds.getMinX(), toScreen(i), bounds.getMinX() + 5, toScreen(i));
			graphics.drawLine(bounds.getMaxX() - 5, toScreen(i), bounds.getMaxX(), toScreen(i));
		}

		// draw labels
		NumberFormat f = NumberFormat.getInstance();
		int digits = (int) Math.ceil(-Math.log10(labelStep));
		f.setMinimumFractionDigits(labelStep < 1 ? digits : 0);
		f.setMaximumFractionDigits(labelStep < 1 ? digits : 0);
		for (double i = Math.floor(start / labelStep) * labelStep; i < end; i += labelStep) {
			graphics.drawLine(bounds.getMinX(), toScreen(i), bounds.getMinX() + 10, toScreen(i));
			graphics.drawLine(bounds.getMaxX() - 10, toScreen(i), bounds.getMaxX(), toScreen(i));
			graphics.drawStringCenter(bounds.getMinX(), toScreen(i) - labelStep / 2, bounds.getWidth(), labelStep,
					FONT, f.format(i));
		}

		// draw the edges
		graphics.drawLine(bounds.getX(), Math.max(0, bounds.getY()), bounds.getX(), bounds.getMaxY());
		graphics.drawLine(bounds.getMaxX(), Math.max(0, bounds.getY()), bounds.getMaxX(), bounds.getMaxY());
	}

	@Override
	public void renderFooter(final GraphicsContext graphics, final Rectangle2D bounds) {
		graphics.drawStringCenter(bounds, FONT, scene.getSceneUnits().getAbbr());
	}

	@Override
	public void renderHeader(final GraphicsContext graphics, final Rectangle2D bounds) {
		graphics.drawStringCenter(bounds, FONT, scene.getSceneUnits().getAbbr());
	}
}
