package org.corewall.ui.app.internal;

import java.util.Set;

import org.corewall.ui.app.MenuContribution;
import org.corewall.ui.app.MenuRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Default implementation of the {@link MenuRegistry} interface.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@Singleton
public class DefaultMenuRegistry implements MenuRegistry {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultMenuRegistry.class);
	protected Multimap<String, MenuContribution> contributions;

	DefaultMenuRegistry() {
		LOG.debug("Initialized");
		contributions = HashMultimap.create();
	}

	@Override
	public ImmutableList<MenuContribution> getMenuContributions(final String application) {
		return ImmutableList.copyOf(contributions.get(application));
	}

	@Inject(optional = true)
	void inject(final Set<MenuContribution> injected) {
		for (MenuContribution m : injected) {
			contributions.put(m.getApplication(), m);
		}
	}

	@Override
	public void register(final MenuContribution contribution) {
		contributions.put(contribution.getApplication(), contribution);
	}
}
