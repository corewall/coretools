package org.corewall.graphics.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A multi fill.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class MultiFill extends Fill {
	private final List<Fill> fills = new ArrayList<Fill>();

	/**
	 * Create a new multi fill.
	 * 
	 * @param fills
	 *            the various fills.
	 */
	public MultiFill(final Fill... fills) {
		super(Style.MULTI);
		this.fills.addAll(Arrays.asList(fills));
	}

	/**
	 * Gets the fills.
	 * 
	 * @return the fills.
	 */
	public List<Fill> getFills() {
		return fills;
	}
}