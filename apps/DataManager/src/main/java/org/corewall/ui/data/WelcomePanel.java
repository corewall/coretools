package org.corewall.ui.data;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import com.google.common.io.Closeables;

/**
 * Displays a Welcome screen.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class WelcomePanel extends JEditorPane implements HyperlinkListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public WelcomePanel() {
		setEditable(false);
		setContentType("text/html");
		setText(getContent());
		addHyperlinkListener(this);
	}

	protected String getContent() {
		URL content = WelcomePanel.class.getResource("welcome.html");

		StringBuilder buffer = new StringBuilder();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(content.openStream()));
			String line = null;
			while ((line = in.readLine()) != null) {
				buffer.append(line);
			}
		} catch (IOException e) {

		} finally {
			Closeables.closeQuietly(in);
		}
		return buffer.toString();
	}

	public void hyperlinkUpdate(final HyperlinkEvent e) {
		if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			Action action = DataManager.getInstance().getActionMap().get(e.getURL().getFile());
			if (action != null) {
				action.actionPerformed(new ActionEvent(e, ActionEvent.ACTION_PERFORMED, e.getDescription()));
			}
		}
	}
}
