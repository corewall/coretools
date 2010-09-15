package org.corewall.graphics.internal;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.MapMaker;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Singleton;

/**
 * Loads and caches images at various levels of detail.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
@Singleton
public class ImageCache {
	private static final Logger LOGGER = LoggerFactory.getLogger(ImageCache.class);

	protected final Map<LoadTask.Params, Future<BufferedImage>> images;
	protected final Map<String, Future<File>> files;
	protected Map<String, Dimension> dimensions;
	protected final ExecutorService scaleQueue;
	protected final ExecutorService fetchQueue;
	protected final ExecutorService fastQueue;
	protected final File dir;

	ImageCache() {
		this(Files.createTempDir());
	}

	ImageCache(final File dir) {
		this.dir = dir;

		// create our thread pools
		int procs = Runtime.getRuntime().availableProcessors();
		fetchQueue = MoreExecutors
				.getExitingExecutorService(new ThreadPoolExecutor(0, 1 * procs, 10, SECONDS, new LinkedBlockingQueue<Runnable>()), 1, SECONDS);
		scaleQueue = MoreExecutors
				.getExitingExecutorService(new ThreadPoolExecutor(0, 1 * procs, 10, SECONDS, new LinkedBlockingQueue<Runnable>()), 1, SECONDS);
		fastQueue = MoreExecutors.getExitingExecutorService(new ThreadPoolExecutor(0, 2 * procs, 10, SECONDS, new LinkedBlockingQueue<Runnable>()), 1, SECONDS);

		// create our file cache
		files = new MapMaker().makeComputingMap(new Function<String, Future<File>>() {
			public Future<File> apply(final String path) {
				String name = path.substring(path.lastIndexOf('/') + 1);
				final File file = new File(dir, name);
				if (file.exists()) {
					return Futures.immediateFuture(file);
				}

				// download the file
				return fetchQueue.submit(new Callable<File>() {
					public File call() throws Exception {
						byte[] buffer = new byte[1024 * 1024];
						InputStream in = null;
						FileOutputStream out = null;
						try {
							// download the file
							File temp = File.createTempFile("image", ".tmp");
							out = new FileOutputStream(temp);
							in = new URL(path).openStream();
							int count = 0;
							while ((count = in.read(buffer)) != -1) {
								out.write(buffer, 0, count);
							}
							in.close();
							out.close();

							// move
							Files.move(temp, file);
						} catch (FileNotFoundException e) {
							// will never happen
							throw new AssertionError(e.getMessage());
						} catch (IOException e) {
							LOGGER.error("Unable to download and cache " + path, e);
						} finally {
							Closeables.closeQuietly(in);
							Closeables.closeQuietly(out);
						}
						return file;
					}
				});
			}
		});

		images = new MapMaker().softValues().makeComputingMap(new Function<LoadTask.Params, Future<BufferedImage>>() {
			public Future<BufferedImage> apply(final LoadTask.Params key) {
				// if we're interactive and the file is already fetched, submit
				// to the fast queue so it is displayed faster
				if ((key.component.get() != null) && key.file.isDone() && !key.file.isCancelled()) {
					return fastQueue.submit(new LoadTask(key));
				} else {
					return scaleQueue.submit(new LoadTask(key));
				}
			}
		});

		// our map to compute the dimensions of an image
		dimensions = new MapMaker().makeComputingMap(new Function<String, Dimension>() {
			public Dimension apply(final String path) {
				ImageInfo ii = new ImageInfo();
				InputStream is = null;
				try {
					is = new URL(path).openStream();
					ii.setInput(is);
					if (ii.check()) {
						if ((ii.getWidth() > 0) && (ii.getHeight() > 0)) {
							return new Dimension(ii.getWidth(), ii.getHeight());
						}
					}
				} catch (IOException e) {
					LOGGER.error("Unable to load image", e);
				} finally {
					Closeables.closeQuietly(is);
				}
				return new Dimension(1, 1);
			}
		});
	}

	/**
	 * Gets the specified image closest to the specified dimensions.
	 * 
	 * @param url
	 *            the image URL.
	 * @param dim
	 *            the dimensions.
	 * @param isVertical
	 *            true if vertical, false otherwise.
	 * @param interactive
	 *            the component rendering the image or null if headless
	 *            rendering.
	 * @return the image Future.
	 */
	public Future<BufferedImage> get(final URL url, final Dimension dim, final boolean isVertical, final JComponent interactive) {
		return get(url, getLevel(url, dim), isVertical, interactive);
	}

	/**
	 * Gets the specified image with the specified URL.
	 * 
	 * @param url
	 *            the URL.
	 * @param level
	 *            the decimation level.
	 * @param isVertical
	 *            true if vertical, false otherwise.
	 * @param component
	 *            the component rendering the image or null if headless
	 *            rendering.
	 * @return the image Future.
	 */
	public Future<BufferedImage> get(final URL url, final int level, final boolean isVertical, final JComponent component) {
		String path = url.toExternalForm();
		return images.get(new LoadTask.Params(path, files.get(path), level, isVertical, component));
	}

	/**
	 * Gets the closest fully-loaded image with the specified URL.
	 * 
	 * @param url
	 *            the image URL.
	 * @param dim
	 *            the desired dimension.
	 * @return the image Future or null.
	 */
	public Future<BufferedImage> getClosest(final URL url, final Dimension dim) {
		return getClosest(url, getLevel(url, dim));
	}

	/**
	 * Gets the closest fully-loaded image with the specified URL.
	 * 
	 * @param url
	 *            the image URL.
	 * @param level
	 *            the desired level.
	 * @return the image Future or null.
	 */
	public Future<BufferedImage> getClosest(final URL url, final int level) {
		final String path = url.toExternalForm();
		Future<BufferedImage> closest = null;
		int distance = Integer.MAX_VALUE;
		for (Entry<LoadTask.Params, Future<BufferedImage>> e : images.entrySet()) {
			if (path.equals(e.getKey().path) && e.getValue().isDone() && !e.getValue().isCancelled()) {
				int diff = Math.abs(e.getKey().level - level);
				if (diff < distance) {
					distance = diff;
					closest = e.getValue();
				}
			}
		}
		return closest;
	}

	/**
	 * Calculate the level for the specified image and dimensions.
	 * 
	 * @param url
	 *            the image URL.
	 * @param dim
	 *            the dimensions.
	 * @return the level.
	 */
	protected int getLevel(final URL url, final Dimension dim) {
		Dimension image = dimensions.get(url.toExternalForm());
		if ((dim.width <= 0) || (dim.height <= 0)) {
			return 10;
		} else {
			return Math.max(0, Math.min(10, Math.min(image.width / dim.width, image.height / dim.height)));
		}
	}
}
