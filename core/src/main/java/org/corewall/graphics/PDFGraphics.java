package org.corewall.graphics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import org.corewall.graphics.driver.PDFDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * A PDFGraphics renders graphics to a PDF file.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class PDFGraphics {
	private static final Logger LOGGER = LoggerFactory.getLogger(PDFGraphics.class);
	protected File file;
	protected Document document;
	protected Paper paper;
	protected PdfWriter writer;
	protected PdfContentByte content;

	// state
	protected PdfTemplate currentTemplate;
	protected GraphicsContext currentContext;

	/**
	 * Create a new PDFGraphics.
	 * 
	 * @param file
	 *            the file.
	 * @param width
	 *            the width.
	 * @param height
	 *            the height.
	 * @param margins
	 *            the margins.
	 */
	public PDFGraphics(final File file, final int width, final int height, final int margins) {
		this(file, new Paper(width, height, margins));
	}

	/**
	 * Create a new PDFGraphics.
	 * 
	 * @param file
	 *            the file.
	 * @param paper
	 *            the specified paper.
	 */
	public PDFGraphics(final File file, final Paper paper) {
		this.paper = paper;
		this.file = file;
		document = new Document(new Rectangle(paper.getWidth(), paper.getHeight()));
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(file));
			document.open();
		} catch (FileNotFoundException e) {
			LOGGER.error("Unable to create PDF document {}: {}", file.getName(), e.getMessage());
			throw new RuntimeException("Unable to create PDF document", e);
		} catch (DocumentException e) {
			LOGGER.error("Unable to create PDF document {}: {}", file.getName(), e.getMessage());
			throw new RuntimeException("Unable to create PDF document", e);
		}
		content = writer.getDirectContent();
	}

	/**
	 * Create a new page.
	 * 
	 * @return the new page.
	 */
	public GraphicsContext newPage() {
		if (currentContext != null) {
			currentContext.dispose();
		}
		if (currentTemplate != null) {
			content.addTemplate(currentTemplate, 0, 0);
			document.newPage();
		}
		currentTemplate = content.createTemplate(paper.getWidth(), paper.getHeight());
		currentContext = new GraphicsContext(new PDFDriver(content, paper));
		return currentContext;
	}

	/**
	 * Writes the PDF.
	 */
	public void write() {
		if (currentContext != null) {
			currentContext.dispose();
		}
		if (currentTemplate != null) {
			content.addTemplate(currentTemplate, 0, 0);
		}
		document.close();
		writer.close();
	}
}
