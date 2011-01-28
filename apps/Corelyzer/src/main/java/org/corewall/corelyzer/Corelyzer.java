package org.corewall.corelyzer;

import java.awt.BorderLayout;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.corewall.Platform;
import org.corewall.corelyzer.internal.CorelyzerModule;
import org.corewall.geology.tracks.RulerTrack;
import org.corewall.scene.DefaultScene;
import org.corewall.scene.Orientation;
import org.corewall.scene.Origin;
import org.corewall.scene.Scene;
import org.corewall.ui.FreeformPanel;
import org.corewall.ui.app.MenuBuilder;
import org.corewall.ui.app.MenuContribution;
import org.corewall.ui.app.MenuRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Corelyzer application.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class Corelyzer {

	/**
	 * The Corelyzer application id.
	 */
	public static final String APPLICATION_ID = "Corelyzer";
	protected static Corelyzer INSTANCE;
	protected static final Logger LOG = LoggerFactory.getLogger(Corelyzer.class);

	/**
	 * Gets the instance of the Corelyzer.
	 * 
	 * @return the instance.
	 */
	public static Corelyzer getInstance() {
		return INSTANCE;
	}

	/**
	 * Launch the application.
	 * 
	 * @param args
	 *            the program arguments.
	 */
	public static void main(final String[] args) {
		// tweak for better Mac integration
		String os = System.getProperty("os.name").toLowerCase();
		if (os.startsWith("mac os x")) {
			System.setProperty("apple.laf.useScreenMenuBar", "true");
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "CoreWall Corelyzer");
		}

		// set the native look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			LOG.warn("Unable to set native look and feel", e);
		} catch (InstantiationException e) {
			LOG.warn("Unable to set native look and feel", e);
		} catch (IllegalAccessException e) {
			LOG.warn("Unable to set native look and feel", e);
		} catch (UnsupportedLookAndFeelException e) {
			LOG.warn("Unable to set native look and feel", e);
		}

		// start our platform
		Platform.start(true, true, new CorelyzerModule());

		// start the data manager
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				Corelyzer window = new Corelyzer();
				window.frame.setVisible(true);
			}
		});
	}

	protected ActionMap actionMap;
	protected JScrollPane contentArea;
	protected JFrame frame;
	protected JMenuBar menu;
	protected FreeformPanel panel;
	protected Scene scene;

	/**
	 * Create the application.
	 */
	public Corelyzer() {
		INSTANCE = this;
		initialize();
	}

	/**
	 * Gets the action map for this application.
	 * 
	 * @return the action map.
	 */
	public ActionMap getActionMap() {
		return actionMap;
	}

	/**
	 * Gets the application window.
	 * 
	 * @return
	 */
	public JFrame getApplicationWindow() {
		return frame;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// create the frame
		frame = new JFrame("CoreWall Corelyzer");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setSize(800, 500);

		// setup the action map
		actionMap = new ActionMap();

		// build our menus
		// @formatter:off
		menu = new MenuBuilder(APPLICATION_ID)
			.menu("File")
				.section("New")
				.section("Open")
				.separator()
				.section("Import")
				.section("Export")
				.separator()
				.section("Other")
		.build(new JMenuBar());
		frame.setJMenuBar(menu);
		// @formatter:on

		// setup action map
		for (MenuContribution c : Platform.getService(MenuRegistry.class).getMenuContributions(APPLICATION_ID)) {
			Action action = c.getAction();
			String link = (String) action.getValue(Action.ACTION_COMMAND_KEY);
			if (link != null) {
				getActionMap().put(link, action);
			}
		}

		// add our freeform scene panel
		scene = new DefaultScene(Origin.TOP);
		panel = new FreeformPanel(scene, Orientation.HORIZONTAL);
		frame.getContentPane().add(panel, BorderLayout.CENTER);

		scene.addTrack(new RulerTrack(), null);
	}
}
