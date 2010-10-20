package org.corewall.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.corewall.data.Project;
import org.corewall.data.Project.ManifestEntry;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.google.inject.internal.Nullable;

/**
 * A panel for displaying a project.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ProjectPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	protected JLabel label;
	protected EventList<Project.ManifestEntry> manifest;
	protected Project project = null;
	protected JTable table;

	/**
	 * Create a new ProjectPanel.
	 */
	public ProjectPanel() {
		setLayout(new BorderLayout());
		setBackground(Color.white);

		// create our label
		label = new JLabel("");
		label.setFont(new Font(label.getFont().getName(), Font.BOLD, 20));
		label.setBorder(new EmptyBorder(10, 10, 10, 10));
		add(label, BorderLayout.NORTH);

		// our list of manifest entries
		manifest = new BasicEventList<Project.ManifestEntry>();
		EventTableModel<Project.ManifestEntry> model = new EventTableModel<Project.ManifestEntry>(manifest,
				new TableFormat<Project.ManifestEntry>() {
					@Override
					public int getColumnCount() {
						return 4;
					}

					@Override
					public String getColumnName(final int column) {
						switch (column) {
							case 0:
								return "Name";
							case 1:
								return "Type";
							case 2:
								return "Format";
							case 3:
								return "Path";
							default:
								return null;
						}
					}

					@Override
					public Object getColumnValue(final ManifestEntry entry, final int column) {
						switch (column) {
							case 0:
								return entry.getName();
							case 1:
								return entry.getType();
							case 2:
								return entry.getFormat();
							case 3:
								return entry.getPath();
							default:
								return null;
						}
					}
				});
		table = MacWidgetFactory.createITunesTable(model);
		table.setFillsViewportHeight(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
	}

	/**
	 * Sets the selected project.
	 * 
	 * @param project
	 *            the project or null if no project selected.
	 */
	public void setProject(@Nullable final Project project) {
		this.project = project;
		if (project == null) {
			label.setText("");
			manifest.clear();
		} else {
			label.setText(project.getName());
			manifest.addAll(project.getManifest());
		}
	}
}
