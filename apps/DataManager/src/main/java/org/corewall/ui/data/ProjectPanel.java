package org.corewall.ui.data;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.net.URL;
import java.util.Comparator;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;

import org.corewall.data.Project;
import org.corewall.data.Project.ManifestEntry;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.gui.TableFormat;
import ca.odell.glazedlists.swing.EventTableModel;

import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.widgets.TableUtils;
import com.explodingpixels.widgets.TableUtils.SortDirection;
import com.google.inject.internal.Nullable;

/**
 * A panel for displaying a project.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class ProjectPanel extends JPanel {
	private static class ManifestEntryComparator implements Comparator<ManifestEntry> {
		private final boolean ascending;
		private final String field;

		private ManifestEntryComparator(final String field, final boolean ascending) {
			this.field = field;
			this.ascending = ascending;
		}

		@Override
		public int compare(final ManifestEntry o1, final ManifestEntry o2) {
			String f1, f2;
			if ("name".equals(field)) {
				f1 = o1.getName();
				f2 = o2.getName();
			} else if ("type".equals(field)) {
				f1 = o1.getType();
				f2 = o2.getType();
			} else if ("format".equals(field)) {
				f1 = o1.getFormat();
				f2 = o2.getFormat();
			} else {
				URL u1 = o1.getPath();
				f1 = (u1 == null ? "" : u1.toExternalForm());
				URL u2 = o2.getPath();
				f2 = (u2 == null ? "" : u2.toExternalForm());
			}
			return (ascending ? 1 : -1) * f1.compareTo(f2);
		}
	}

	private static final long serialVersionUID = 1L;

	protected JLabel label;
	protected SortedList<ManifestEntry> manifest;
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
		manifest = new SortedList<ManifestEntry>(new BasicEventList<ManifestEntry>(), new ManifestEntryComparator(
				"type", true));
		EventTableModel<ManifestEntry> model = new EventTableModel<ManifestEntry>(manifest,
				new TableFormat<ManifestEntry>() {
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
		TableUtils.SortDelegate sortDelegate = new TableUtils.SortDelegate() {
			public void sort(final int column, final TableUtils.SortDirection sortDirection) {
				switch (column) {
					case 0:
						manifest.setComparator(new ManifestEntryComparator("name",
								sortDirection == SortDirection.ASCENDING));
						break;
					case 1:
						manifest.setComparator(new ManifestEntryComparator("type",
								sortDirection == SortDirection.ASCENDING));
						break;
					case 2:
						manifest.setComparator(new ManifestEntryComparator("format",
								sortDirection == SortDirection.ASCENDING));
						break;
					case 3:
						manifest.setComparator(new ManifestEntryComparator("path",
								sortDirection == SortDirection.ASCENDING));
						break;
				}
			}
		};
		TableUtils.makeSortable(table, sortDelegate);
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
