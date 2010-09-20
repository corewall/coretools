package org.corewall.geology.formats;

import org.corewall.data.Format;
import org.corewall.data.formats.CSVFormat;
import org.corewall.data.formats.ExcelFormat;
import org.corewall.data.formats.JSONFormat;
import org.corewall.geology.models.Image;
import org.corewall.geology.models.Section;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.multibindings.Multibinder;

/**
 * Geology-related formats and tracks.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class GeologyModule extends AbstractModule implements Module {

	@Override
	protected void configure() {

		// register Geology-related formats
		@SuppressWarnings("rawtypes")
		Multibinder<Format> formats = Multibinder.newSetBinder(binder(), Format.class);

		// tab-separated
		formats.addBinding().toInstance(new CSVFormat<Section>("tsv:Section", '\t', Section.factory()));
		formats.addBinding().toInstance(new CSVFormat<Image>("tsv:Image", '\t', Image.factory()));

		// comma-separated
		formats.addBinding().toInstance(new CSVFormat<Section>("csv:Section", ',', Section.factory()));
		formats.addBinding().toInstance(new CSVFormat<Image>("csv:Image", ',', Image.factory()));

		// excel
		formats.addBinding().toInstance(new ExcelFormat<Section>("excel:Section", "Sections", Section.factory()));
		formats.addBinding().toInstance(new ExcelFormat<Image>("excel:Image", "Images", Image.factory()));

		// json
		formats.addBinding().toInstance(new JSONFormat<Section>("json:Section", null, Section.factory()));
		formats.addBinding().toInstance(new JSONFormat<Image>("json:Image", null, Image.factory()));

		// coreref
		formats.addBinding().toInstance(new JSONFormat<Section>("coreref:Section", null, Section.factory()));
		formats.addBinding().toInstance(
				new JSONFormat<Image>("coreref:Image", null, Image.factory(ImmutableMap.of("url", "path"),
						ImmutableMap.of("orientation", "vertical"))));

		// cml
		formats.addBinding().toInstance(new CMLFormat("cml:Image"));
	}

}
