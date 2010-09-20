package org.corewall.ui.data;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import org.corewall.Platform;
import org.corewall.ProjectManager;
import org.corewall.data.Project;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.IAppWidgetFactory;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.SourceListSelectionListener;

/**
 * The Data Manager application.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class DataManager implements SourceListSelectionListener {

	protected static DataManager INSTANCE;

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
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// use the screen menu bar
					System.setProperty("apple.laf.useScreenMenuBar", "true");

					// start our platform
					Platform.start();

					// create the data manager
					DataManager window = new DataManager();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	protected BottomBar bottomBar;
	protected JScrollPane contentArea;
	protected JFrame frame;
	protected JMenuBar menu;
	protected SourceList projectList;
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
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("CoreWall Data Manager");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.setSize(800, 500);

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
		menu.add(new JMenu("File"));

		// create our welcome screen
		welcome = new WelcomePanel();
		contentArea.setViewportView(welcome);
	}

	public void refresh() {
		// load our projects
		ProjectManager projects = Platform.getService(ProjectManager.class);
		SourceListCategory local = new SourceListCategory("Local Projects");
		SourceListModel projectModel = projectList.getModel();
		projectModel.addCategory(local);
		int count = 0;
		for (Project project : projects.getProjects()) {
			projectModel.addItemToCategory(new SourceListItem(project.getId()), local);
			count++;
		}
		status.setText(count + " projects");
	}

	public void sourceListItemSelected(final SourceListItem project) {
		// TODO: show the project info panel
	}
}
