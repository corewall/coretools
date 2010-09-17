package org.corewall.geology.io;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

import org.corewall.data.Filter;
import org.corewall.data.Filters;
import org.corewall.data.io.JSONModelReader;
import org.corewall.data.io.ModelReader.Factory;
import org.corewall.geology.models.Factories;
import org.corewall.geology.models.Image;
import org.corewall.geology.models.Project;
import org.corewall.geology.models.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * A project hosted at http://coreref.org
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class CoreRefProject implements Project {
	private static final Logger LOG = LoggerFactory.getLogger(CoreRefProject.class);
	protected Properties properties = new Properties();
	protected String base;
	protected String id;
	protected ImmutableList<Section> sections;
	protected ImmutableList<Image> images;
	private static final Map<String, String> IMAGE_REWRITE = ImmutableMap.of("url", "path");
	private static final Map<String, String> IMAGE_DEFAULTS = ImmutableMap.of("orientation", "vertical");

	/**
	 * Create a new project for the specified id at CoreRef.org
	 * 
	 * @param id
	 *            the project id.
	 */
	public CoreRefProject(final String id) {
		this("http://coreref.org", id);
	}

	/**
	 * Create a new project for the specified id and base URL.
	 * 
	 * @param base
	 *            the base URL.
	 * @param id
	 *            the id.
	 */
	public CoreRefProject(final String base, final String id) {
		this.base = base;
		this.id = id;

		// strip any trailing slashes
		while (this.base.endsWith("/")) {
			this.base = this.base.substring(0, this.base.length() - 1);
		}
	}

	public ImmutableList<Image> getImages() {
		if (images == null) {
			images = parse(base + "/services/" + id + "/search/type/Image", Filters.property("type", "split"), Factories.image(IMAGE_REWRITE, IMAGE_DEFAULTS));
		}
		return images;
	}

	public String getName() {
		return id;
	}

	public Properties getProperties() {
		return properties;
	}

	public ImmutableList<Section> getSections() {
		if (sections == null) {
			sections = parse(base + "/services/" + id + "/search/type/Section", Filters.all(), Factories.section());
		}
		return sections;
	}

	protected <E> ImmutableList<E> parse(final String url, final Filter filter, final Factory<E> factory) {
		JSONModelReader json;
		try {
			json = new JSONModelReader(new URL(url).openStream());
			return ImmutableList.copyOf(json.getModels(filter, factory));
		} catch (MalformedURLException e) {
			LOG.error("Unable to parse JSON from " + url, e);
		} catch (IOException e) {
			LOG.error("Unable to parse JSON from " + url, e);
		}
		return ImmutableList.of();
	}
}
