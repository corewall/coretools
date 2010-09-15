package org.corewall.graphics;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.corewall.Platform;
import org.corewall.graphics.GraphicsContext;
import org.corewall.graphics.PDFGraphics;
import org.corewall.graphics.Paper;
import org.corewall.graphics.driver.PDFDriver;
import org.junit.Test;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfWriter;

/**
 * Tests for {@link PDFGraphics} and {@link PDFDriver}.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class PDFTest extends TestCase {
	static {
		Platform.start();
		(new File("build")).mkdirs();
	}

	/**
	 * Tests the experimental PDFDriver.
	 * 
	 * @throws IOException
	 *             indicates a problem with the test.
	 * @throws DocumentException
	 *             indicates a problem with the test.
	 */
	@Test
	public void testDriver() throws IOException, DocumentException {
		Paper paper = Paper.getDefault();
		Document document = new Document(new Rectangle(paper.getWidth(), paper.getHeight()));
		PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("build/pdf-driver.pdf"));
		document.open();
		GraphicsHarness.test(new GraphicsContext(new PDFDriver(writer.getDirectContent(), paper)));
		document.close();
		writer.close();
	}

	/**
	 * Tests the PDFGraphics.
	 * 
	 * @throws IOException
	 *             indicates a problem with the test.
	 */
	@Test
	public void testGraphics() throws IOException {
		PDFGraphics pdf = new PDFGraphics(new File("build/pdf-default.pdf"), Paper.getDefault());
		GraphicsHarness.test(pdf.newPage());
		pdf.write();
	}
}
