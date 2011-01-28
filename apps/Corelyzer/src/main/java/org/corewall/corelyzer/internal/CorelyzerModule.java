package org.corewall.corelyzer.internal;

import org.corewall.ui.app.MenuContribution;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Standard menu contributions for the Corelyzer app.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CorelyzerModule extends AbstractModule {

	@Override
	protected void configure() {
		// register standard menu contributions
		Multibinder<MenuContribution> menu = Multibinder.newSetBinder(binder(), MenuContribution.class);
	}
}
