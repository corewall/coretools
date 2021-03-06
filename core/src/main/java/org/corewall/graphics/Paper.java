package org.corewall.graphics;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Paper sizes.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Paper {
	private static Map<String, Paper> PAGES = new HashMap<String, Paper>();
	private static final int DEFAULT_MARGIN = 36; // 0.5"

	/**
	 * A0 size paper.
	 */
	public static final Paper A0 = new Paper(2384, 3371, DEFAULT_MARGIN);
	/**
	 * A1 size paper.
	 */
	public static final Paper A1 = new Paper(1685, 2384, DEFAULT_MARGIN);
	/**
	 * A2 size paper.
	 */
	public static final Paper A2 = new Paper(1190, 1684, DEFAULT_MARGIN);
	/**
	 * A3 size paper.
	 */
	public static final Paper A3 = new Paper(842, 1190, DEFAULT_MARGIN);
	/**
	 * A4 size paper.
	 */
	public static final Paper A4 = new Paper(595, 842, DEFAULT_MARGIN);
	/**
	 * A5 size paper.
	 */
	public static final Paper A5 = new Paper(420, 595, DEFAULT_MARGIN);
	/**
	 * B4 size paper.
	 */
	public static final Paper B4 = new Paper(729, 1032, DEFAULT_MARGIN);
	/**
	 * B5 size paper.
	 */
	public static final Paper B5 = new Paper(516, 729, DEFAULT_MARGIN);
	/**
	 * Executive size paper.
	 */
	public static final Paper EXECUTIVE = new Paper(540, 720, DEFAULT_MARGIN);
	/**
	 * Folio size paper.
	 */
	public static final Paper FOLIO = new Paper(612, 936, DEFAULT_MARGIN);
	/**
	 * Ledger size paper.
	 */
	public static final Paper LEDGER = new Paper(1224, 792, DEFAULT_MARGIN);
	/**
	 * Legal size paper.
	 */
	public static final Paper LEGAL = new Paper(612, 1008, DEFAULT_MARGIN);
	/**
	 * Letter size paper.
	 */
	public static final Paper LETTER = new Paper(612, 792, DEFAULT_MARGIN);
	/**
	 * Quarto size paper.
	 */
	public static final Paper QUARTO = new Paper(610, 780, DEFAULT_MARGIN);
	/**
	 * Statement size paper.
	 */
	public static final Paper STATEMENT = new Paper(396, 612, DEFAULT_MARGIN);
	/**
	 * Tabloid size paper.
	 */
	public static final Paper TABLOID = new Paper(792, 1224, DEFAULT_MARGIN);

	static {
		PAGES.put("letter", LETTER);
		PAGES.put("tabloid", TABLOID);
		PAGES.put("ledger", LEDGER);
		PAGES.put("legal", LEGAL);
		PAGES.put("statement", STATEMENT);
		PAGES.put("executive", EXECUTIVE);
		PAGES.put("a0", A0);
		PAGES.put("a1", A1);
		PAGES.put("a2", A2);
		PAGES.put("a3", A3);
		PAGES.put("a4", A4);
		PAGES.put("a5", A5);
		PAGES.put("b4", B4);
		PAGES.put("b5", B5);
		PAGES.put("folio", FOLIO);
		PAGES.put("quarto", QUARTO);
	}

	/**
	 * Gets the page associated with the specified name.
	 * 
	 * @param name
	 *            the name.
	 * @return the page.
	 */
	public static Paper get(final String name) {
		// no name specified
		if ((name == null) || "".equals(name.trim())) {
			return getDefault();
		}

		// try looking up by name
		final Paper page = PAGES.get(name.trim().toLowerCase());
		if (page == null) {
			// try parsing the paper format
			String[] split = name.replaceAll("[\\[\\]x\\+]", " ").split(" ");
			if (split.length == 6) {
				int w = Integer.parseInt(split[0]);
				int h = Integer.parseInt(split[1]);
				int pw = Integer.parseInt(split[2]);
				int ph = Integer.parseInt(split[3]);
				int px = Integer.parseInt(split[4]);
				int py = Integer.parseInt(split[5]);
				return new Paper(w, h, pw, ph, px, py);
			} else {
				return getDefault();
			}
		} else {
			return page;
		}
	}

	/**
	 * Gets the default paper size based on the current locale.
	 * 
	 * @return the default paper size.
	 */
	public static Paper getDefault() {
		final String country = Locale.getDefault().getCountry();
		if ("US".equals(country) || "CA".equals(country)) {
			return LETTER;
		} else {
			return A4;
		}
	}

	private final int px, py, pw, ph;
	private final int width, height;

	/**
	 * Create a new page with the specified width, height, and uniform margin
	 * all measured in points.
	 * 
	 * @param width
	 *            the width.
	 * @param height
	 *            the height.
	 * @param margin
	 *            the margin.
	 */
	public Paper(final int width, final int height, final int margin) {
		this.width = width;
		this.height = height;
		px = margin;
		py = margin;
		pw = width - 2 * margin;
		ph = height - 2 * margin;
	}

	/**
	 * Create a new page with the specified width, height, and margins all
	 * measured in points.
	 * 
	 * @param width
	 *            the width.
	 * @param height
	 *            the height.
	 * @param printableWidth
	 *            the printable width.
	 * @param printableHeight
	 *            the printable height.
	 * @param left
	 *            the left margin.
	 * @param top
	 *            the top margin.
	 */
	public Paper(final int width, final int height, final int printableWidth, final int printableHeight,
			final int left, final int top) {
		this.width = width;
		this.height = height;
		px = left;
		py = top;
		pw = printableWidth;
		ph = printableHeight;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Paper other = (Paper) obj;
		if (height != other.height) {
			return false;
		}
		if (ph != other.ph) {
			return false;
		}
		if (pw != other.pw) {
			return false;
		}
		if (px != other.px) {
			return false;
		}
		if (py != other.py) {
			return false;
		}
		if (width != other.width) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the page height in points.
	 * 
	 * @return the height.
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Gets the printable height in points.
	 * 
	 * @return the printable height.
	 */
	public int getPrintableHeight() {
		return ph;
	}

	/**
	 * Gets the printable width in points.
	 * 
	 * @return the printable width.
	 */
	public int getPrintableWidth() {
		return pw;
	}

	/**
	 * Gets the printable x in points.
	 * 
	 * @return the printable x.
	 */
	public int getPrintableX() {
		return px;
	}

	/**
	 * Gets the printable y in points.
	 * 
	 * @return the printable y.
	 */
	public int getPrintableY() {
		return py;
	}

	/**
	 * Gets the page width in points.
	 * 
	 * @return the page width.
	 */
	public int getWidth() {
		return width;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + height;
		result = prime * result + ph;
		result = prime * result + pw;
		result = prime * result + px;
		result = prime * result + py;
		result = prime * result + width;
		return result;
	}

	@Override
	public String toString() {
		// find existing paper
		for (Entry<String, Paper> entry : PAGES.entrySet()) {
			if (entry.getValue().equals(this)) {
				return entry.getKey();
			}
		}
		// otherwise return a string representation
		return width + "x" + height + "[" + pw + "x" + ph + "+" + px + "+" + py + "]";
	}
}
