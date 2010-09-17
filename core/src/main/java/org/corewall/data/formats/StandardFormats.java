package org.corewall.data.formats;

import org.corewall.data.Format;
import org.corewall.data.Model;
import org.corewall.geology.models.Image;
import org.corewall.geology.models.Section;

import com.google.common.collect.ImmutableMap;
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
		Multibinder<Format> formats = Multibinder.newSetBinder(binder(), Format.class);

		// tab-separated
		formats.addBinding().toInstance(new CSVFormat<Model>("tsv:raw", '\t', null));
		formats.addBinding().toInstance(new CSVFormat<Section>("tsv:Section", '\t', Section.factory()));
		formats.addBinding().toInstance(new CSVFormat<Image>("tsv:Image", '\t', Image.factory()));

		// comma-separated
		formats.addBinding().toInstance(new CSVFormat<Model>("csv:raw", ',', null));
		formats.addBinding().toInstance(new CSVFormat<Section>("csv:Section", ',', Section.factory()));
		formats.addBinding().toInstance(new CSVFormat<Image>("csv:Image", ',', Image.factory()));

		// excel
		formats.addBinding().toInstance(new ExcelFormat<Model>("excel:raw", 0, null));
		formats.addBinding().toInstance(new ExcelFormat<Section>("excel:Section", "Sections", Section.factory()));
		formats.addBinding().toInstance(new ExcelFormat<Image>("excel:Image", "Images", Image.factory()));

		// json
		formats.addBinding().toInstance(new JSONFormat<Model>("json:raw", null, null));
		formats.addBinding().toInstance(new JSONFormat<Section>("json:Section", null, Section.factory()));
		formats.addBinding().toInstance(new JSONFormat<Image>("json:Image", null, Image.factory()));

		// coreref
		formats.addBinding().toInstance(new JSONFormat<Section>("coreref:Section", null, Section.factory()));
		formats.addBinding().toInstance(
				new JSONFormat<Image>("coreref:Image", null, Image.factory(ImmutableMap.of("url", "path"),
						ImmutableMap.of("orientation", "vertical"))));
	}
}
