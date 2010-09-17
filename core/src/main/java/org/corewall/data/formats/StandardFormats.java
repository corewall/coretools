package org.corewall.data.formats;

import org.corewall.data.Format;
import org.corewall.data.Model;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;

/**
 * Registers the standard {@link Format}s.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class StandardFormats extends AbstractModule {

	@Override
	protected void configure() {
		@SuppressWarnings("rawtypes")
		Multibinder<Format> formats = Multibinder.newSetBinder(binder(), Format.class);

		// register raw formats
		formats.addBinding().toInstance(new CSVFormat<Model>("tsv:raw", '\t', null));
		formats.addBinding().toInstance(new CSVFormat<Model>("csv:raw", ',', null));
		formats.addBinding().toInstance(new ExcelFormat<Model>("excel:raw", 0, null));
		formats.addBinding().toInstance(new JSONFormat<Model>("json:raw", null, null));
	}
}
