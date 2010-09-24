package org.corewall.ui.data;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

import org.corewall.data.Project;
import org.corewall.data.Project.Attr;
import org.corewall.internal.DefaultProject;

/**
 * A dialog for creating a new project.
 * 
 * @author Josh Reed (jareed@andrill.org)
 */
public class NewProjectDialog extends JDialog implements ActionListener, DocumentListener {
	private static final long serialVersionUID = 1L;

	/**
	 * Shows the New Project dialog.
	 * 
	 * @param parent
	 *            the parent.
	 * @return the new {@link Project} or null if the dialog was canceled.
	 */
	public static Project showDialog(final Frame parent) {
		NewProjectDialog dialog = new NewProjectDialog(parent);
		dialog.setVisible(true);
		return dialog.getProject();
	}

	private final JPanel contentPanel = new JPanel();
	private JTextArea descriptionField;
	private JTextField expeditionField;
	private JTextField holeField;
	private JTextField identifierField;
	private JTextField latitudeField;
	private JTextField longitudeField;
	private JTextField nameField;
	private JButton okButton;
	private JTextField programField;
	private DefaultProject project = null;
	private JTextField siteField;

	/**
	 * Create the dialog.
	 * 
	 * @param frame
	 *            the parent frame.
	 */
	public NewProjectDialog(final Frame frame) {
		super(frame);
		setTitle("New Project");
		setModal(true);
		setBounds(100, 100, 500, 355);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new MigLayout("fill, wrap 4", "[right][grow,fill][right][grow,fill]",
				"[][][grow][][][][][]"));

		// add our fields
		{
			JLabel identifierLabel = new JLabel("Identifier");
			Font font = identifierLabel.getFont();
			identifierLabel.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
			contentPanel.add(identifierLabel, "");
			identifierField = new JTextField();
			identifierField.getDocument().addDocumentListener(this);
			identifierLabel.setLabelFor(identifierField);
			contentPanel.add(identifierField, "wrap");
		}
		{
			JLabel nameLabel = new JLabel("Name");
			Font font = nameLabel.getFont();
			nameLabel.setFont(new Font(font.getFontName(), Font.BOLD, font.getSize()));
			contentPanel.add(nameLabel, "");
			nameField = new JTextField();
			nameField.getDocument().addDocumentListener(this);
			nameLabel.setLabelFor(nameField);
			contentPanel.add(nameField, "span");
		}
		{
			JLabel descriptionLabel = new JLabel("Description");
			contentPanel.add(descriptionLabel, "aligny top");
			descriptionField = new JTextArea();
			descriptionField.setLineWrap(true);
			descriptionLabel.setLabelFor(descriptionField);
			JScrollPane scroll = new JScrollPane(descriptionField);
			scroll.setBorder(nameField.getBorder());
			contentPanel.add(scroll, "span, grow, hmin 50px");
		}
		{
			JSeparator separator = new JSeparator();
			contentPanel.add(separator, "span, growx");
		}
		{
			JLabel latitudeLabel = new JLabel("Latitude");
			contentPanel.add(latitudeLabel, "");
			latitudeField = new JTextField();
			contentPanel.add(latitudeField, "");
		}
		{
			JLabel longitudeLabel = new JLabel("Longitude");
			contentPanel.add(longitudeLabel, "");
			longitudeField = new JTextField();
			contentPanel.add(longitudeField, "");
		}
		{
			JSeparator separator = new JSeparator();
			contentPanel.add(separator, "span, growx");
		}
		{
			JLabel programLabel = new JLabel("Program");
			contentPanel.add(programLabel, "");
			programField = new JTextField();
			contentPanel.add(programField, "");
		}
		{
			JLabel expeditionLabel = new JLabel("Expedition");
			contentPanel.add(expeditionLabel, "gapleft 10px");
			expeditionField = new JTextField();
			contentPanel.add(expeditionField, "");
		}
		{
			JLabel siteLabel = new JLabel("Site");
			contentPanel.add(siteLabel, "");
			siteField = new JTextField();
			contentPanel.add(siteField, "");
		}
		{
			JLabel holeLabel = new JLabel("Hole");
			contentPanel.add(holeLabel, "");
			holeField = new JTextField();
			contentPanel.add(holeField, "");
		}
		// button panels
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new MigLayout("fillx"));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				okButton.addActionListener(this);
				buttonPane.add(okButton, "split, alignx right, tag ok");
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				cancelButton.addActionListener(this);
				buttonPane.add(cancelButton, "alignx right, tag cancel");
			}
		}
		updateButtonState();
	}

	public void actionPerformed(final ActionEvent e) {
		if ("OK".equals(e.getActionCommand())) {
			project = new DefaultProject();
			project.setId(identifierField.getText());
			project.setName(nameField.getText());
			setProjectProperty(project, Attr.DESCRIPTION, descriptionField.getText());
			setProjectProperty(project, Attr.LATITUDE, latitudeField.getText());
			setProjectProperty(project, Attr.LONGITUDE, longitudeField.getText());
			setProjectProperty(project, Attr.PROGRAM, programField.getText());
			setProjectProperty(project, Attr.EXPEDITION, expeditionField.getText());
			setProjectProperty(project, Attr.SITE, siteField.getText());
			setProjectProperty(project, Attr.HOLE, holeField.getText());
		} else if ("Cancel".equals(e.getActionCommand())) {
			project = null;
		}
		setVisible(false);
		dispose();
	}

	public void changedUpdate(final DocumentEvent e) {
		updateButtonState();
	}

	/**
	 * Gets the new project.
	 * 
	 * @return the project or null if canceled.
	 */
	public Project getProject() {
		return project;
	}

	public void insertUpdate(final DocumentEvent e) {
		updateButtonState();
	}

	public void removeUpdate(final DocumentEvent e) {
		updateButtonState();
	}

	private void setProjectProperty(final DefaultProject project, final Project.Attr attr, final String text) {
		if (!"".equals(text.trim())) {
			project.setAttribute(attr, text);
		}
	}

	private void updateButtonState() {
		okButton.setEnabled(!"".equals(identifierField.getText().trim()) && !"".equals(nameField.getText().trim()));
	}
}
