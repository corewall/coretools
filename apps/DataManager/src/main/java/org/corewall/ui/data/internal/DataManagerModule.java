package org.corewall.ui.data.internal;

import org.corewall.ui.app.DefaultMenuContribution;
import org.corewall.ui.app.MenuContribution;
import org.corewall.ui.data.DataManager;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

public class DataManagerModule extends AbstractModule {

	@Override
	protected void configure() {
		// register standard menu contributions
		@SuppressWarnings("rawtypes")
		Multibinder<MenuContribution> menu = Multibinder.newSetBinder(binder(), MenuContribution.class);

		menu.addBinding().toInstance(
				new DefaultMenuContribution<DataManager>(DataManager.APPLICATION_ID, "File", new NewProjectAction()));
	}
}
