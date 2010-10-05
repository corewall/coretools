package org.corewall.ui.data;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.corewall.Platform;
import org.corewall.ProjectManager;
import org.corewall.data.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;
import com.google.common.collect.ImmutableList;

/**
 * The Data Manager application.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class DataManager implements SourceListSelectionListener {
	protected static DataManager INSTANCE;
	protected static final Logger LOG = LoggerFactory.getLogger(DataManager.class);

	/**
	 * Gets the instance of the DataManager.
	 * 
	 * @return the instance.
	 */
	public static DataManager getInstance() {
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
			System.setProperty("com.apple.mrj.application.apple.menu.about.name", "CoreWall Data Manager");
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
		Platform.start();

		// start the data manager
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				DataManager window = new DataManager();
				window.frame.setVisible(true);
			}
		});
	}

	protected ActionMap actionMap;
	protected BottomBar bottomBar;
	protected JScrollPane contentArea;
	protected JFrame frame;
	protected JMenuBar menu;
	protected SourceList projectList;
	protected ImmutableList<Project> projects = null;
	protected Project selected = null;
	protected JLabel status;
	protected WelcomePanel welcome;

	/**
	 * Create the application.
	 */
	public DataManager() {
		INSTANCE = this;
		initialize();
		refresh();
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
	 * Gets the selected project.
	 * 
	 * @return the selected project.
	 */
	public Project getSelectedProject() {
		return selected;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		// create the frame
		frame = new JFrame("CoreWall Data Manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setSize(800, 500);

		// setup the action map
		actionMap = new ActionMap();

		// setup the bottom bar
		bottomBar = new BottomBar(BottomBarSize.SMALL);
		frame.getContentPane().add(bottomBar.getComponent(), BorderLayout.SOUTH);
		status = new JLabel("");
		bottomBar.addComponentToCenter(status);

		// create the content area
		contentArea = new JScrollPane();
		IAppWidgetFactory.makeIAppScrollPane(contentArea);
		contentArea.setBorder(null);

		// create the projects list
		projectList = new SourceList(new SourceListModel());
		projectList.useIAppStyleScrollBars();
		projectList.addSourceListSelectionListener(this);

		// add the list and content
		JSplitPane split = MacWidgetFactory.createSplitPaneForSourceList(projectList, contentArea);
		split.setDividerLocation(200);
		frame.getContentPane().add(split, BorderLayout.CENTER);

		// setup the menu
		menu = new JMenuBar();
		frame.setJMenuBar(menu);
		JMenu fileMenu = new JMenu("File");
		{
			NewProjectAction action = new NewProjectAction();
			JMenuItem item = new JMenuItem(action);
			item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Toolkit.getDefaultToolkit()
					.getMenuShortcutKeyMask()));
			fileMenu.add(item);
			actionMap.put("project/new", action);
		}
		menu.add(fileMenu);

		// create our welcome screen
		welcome = new WelcomePanel();
		contentArea.setViewportView(welcome);
	}

	/**
	 * Refreshes the project list.
	 */
	public void refresh() {
		// remove all projects
		SourceListModel projectModel = projectList.getModel();
		while (projectModel.getCategories().size() > 0) {
			projectModel.removeCategoryAt(0);
		}
		selected = null;

		// load all local projects
		ProjectManager projectManager = Platform.getService(ProjectManager.class);
		projects = projectManager.getProjects();
		SourceListCategory local = new SourceListCategory("Local");
		projectModel.addCategory(local);
		for (Project project : projects) {
			projectModel.addItemToCategory(new SourceListItem(project.getId()), local);
		}

		// set our status
		status.setText(projects.size() + " project" + (projects.size() == 1 ? "" : "s"));
	}

	public void sourceListItemSelected(final SourceListItem project) {
		selected = null;
		if (project == null) {
			return;
		}
		for (Project p : projects) {
			if (p.getId() == project.getText()) {
				selected = p;
			}
		}
	}
}
