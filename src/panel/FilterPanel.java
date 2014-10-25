package panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

import net.miginfocom.swing.MigLayout;
import operation.AudioFileSelection;
import operation.FileSelection;
import operation.MediaTimer;
import operation.VamixProcesses;
import operation.VideoFileSelection;
import res.FilterColor;
import res.FilterFont;
import setting.MediaSetting;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.FilterPreviewWorker;
import worker.FilterSaveWorker;

import component.FileType;
import component.MyStyledDocument;
import component.MyTextFieldFilter;
import component.RowSort;
/**
 * Singleton design pattern. Panel contains anything related to filter editing of video.
 * 
 */
/**
 * Singleton design pattern. Panel contains anything related to filter editing of video.
 * 
 */

@SuppressWarnings("serial")
public class FilterPanel extends JPanel implements ActionListener {
	private static FilterPanel theInstance = null;
	private TitledBorder title;

	private JPanel tablePanel, optionPanel, buttonPanel;
	private JTextArea textArea;
	private JScrollPane textScroll, tableScroll;
	private JTable table;
	private DefaultTableModel model;

	private Integer[] fontSizeSelection = {10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40};

	private JComboBox<FilterFont> fontCombo;
	private JComboBox<Integer> fontSizeCombo;
	private JComboBox<FilterColor> fontColorCombo;

	private JLabel xLabel, yLabel, textLabel;

	private JTextField xTextField, yTextField;

	private JButton previewButton, saveButton, addButton, editChangeButton, deleteButton, startButton, endButton;

	private JSpinner startSpinnerSeconds, startSpinnerMinutes, startSpinnerHours, endSpinnerSeconds, endSpinnerMinutes, endSpinnerHours;

	private EmbeddedMediaPlayer mediaPlayer;

	private FileSelection audioFileSelection, videoFileSelection;

	private String[] EDITCHANGE = {"Edit", "Change"};

	private int rowToEdit;

	public static FilterPanel getInstance() {
		if (theInstance == null) {
			theInstance = new FilterPanel();
		}
		return theInstance;
	}

	private FilterPanel() {
		setLayout(new MigLayout("gap rel 0", "grow"));

		title = BorderFactory.createTitledBorder("Text Editing");
		setBorder(title);	

		mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();

		audioFileSelection = new AudioFileSelection();
		videoFileSelection = new VideoFileSelection();

		setOptionPanel();
		setTablePanel();
		setButtonPanel();

		add(optionPanel, "pushx, growx, wrap");
		add(tablePanel, "width 375px, height 150px, wrap");
		add(buttonPanel, "pushx, growx");
		addListeners();	
	}

	private void setOptionPanel() {
		optionPanel = new JPanel(new MigLayout("debug"));

		fontCombo = new JComboBox<FilterFont>(FilterFont.values());
		fontColorCombo = new JComboBox<FilterColor>(FilterColor.values());
		fontSizeCombo = new JComboBox<Integer>(fontSizeSelection);

		xLabel = new JLabel("x:");
		yLabel = new JLabel("y:");

		xTextField = new JTextField(5);
		xTextField.setText("0");
		yTextField = new JTextField(5);
		yTextField.setText("0");

		// Sets filter for textfields.
		((AbstractDocument)xTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());
		((AbstractDocument)yTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());

		int maxWords = MediaSetting.getInstance().getTextEditMaxWords();
		textArea = new JTextArea(new MyStyledDocument(maxWords));
		textScroll = new JScrollPane(textArea);

		textScroll.setPreferredSize(new Dimension(350, 150)); // arbitrary value.

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText("Opening Scene Text");

		textLabel = new JLabel("<html>Text (" + maxWords + " words max for each selection). X and Y <br /> co ordinates for the video is optional</html>");
		// Sets new font for textLabel.
		Font font = textLabel.getFont().deriveFont(Font.BOLD + Font.ITALIC, 14f);
		textLabel.setFont(font);

		startButton = new JButton("Start");
		endButton = new JButton("End");

		// http://stackoverflow.com/questions/972194/zero-padding-a-spinner-in-java
		startSpinnerSeconds = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		startSpinnerSeconds.setEditor(new JSpinner.NumberEditor(startSpinnerSeconds, "00"));
		JComponent editor = startSpinnerSeconds.getEditor();

		if (editor instanceof DefaultEditor) {
			((DefaultEditor)editor).getTextField().setEditable(false);
		}

		startSpinnerMinutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		startSpinnerMinutes.setEditor(new JSpinner.NumberEditor(startSpinnerMinutes, "00"));

		editor = startSpinnerMinutes.getEditor();

		if (editor instanceof DefaultEditor) {
			((DefaultEditor)editor).getTextField().setEditable(false);
		}

		startSpinnerHours = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
		startSpinnerHours.setEditor(new JSpinner.NumberEditor(startSpinnerHours, "00"));

		editor = startSpinnerHours.getEditor();

		if (editor instanceof DefaultEditor) {
			((DefaultEditor)editor).getTextField().setEditable(false);
		}

		endSpinnerSeconds = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		endSpinnerSeconds.setEditor(new JSpinner.NumberEditor(endSpinnerSeconds, "00"));

		editor = endSpinnerSeconds.getEditor();

		if (editor instanceof DefaultEditor) {
			((DefaultEditor)editor).getTextField().setEditable(false);
		}

		endSpinnerMinutes = new JSpinner(new SpinnerNumberModel(0, 0, 59, 1));
		endSpinnerMinutes.setEditor(new JSpinner.NumberEditor(endSpinnerMinutes, "00"));

		editor = endSpinnerMinutes.getEditor();

		if (editor instanceof DefaultEditor) {
			((DefaultEditor)editor).getTextField().setEditable(false);
		}

		endSpinnerHours = new JSpinner(new SpinnerNumberModel(0, 0, 99, 1));
		endSpinnerHours.setEditor(new JSpinner.NumberEditor(endSpinnerHours, "00"));

		editor = endSpinnerHours.getEditor();

		if (editor instanceof DefaultEditor) {
			((DefaultEditor)editor).getTextField().setEditable(false);
		}

		optionPanel.add(textLabel, "wrap");
		optionPanel.add(fontCombo, "split 3"); // split the cell in 3. this is so 3 components go into same cell
		optionPanel.add(fontSizeCombo);
		optionPanel.add(fontColorCombo, "wrap");
		optionPanel.add(startButton, "split 8");
		optionPanel.add(startSpinnerHours);
		optionPanel.add(startSpinnerMinutes, "gap 0");
		optionPanel.add(startSpinnerSeconds, "gap 0");
		optionPanel.add(endButton);
		optionPanel.add(endSpinnerHours);
		optionPanel.add(endSpinnerMinutes, "gap 0");
		optionPanel.add(endSpinnerSeconds, "gap 0, wrap");
		optionPanel.add(xLabel, "split 4"); // split the cell in 5. this is so 5 components go into same cell
		optionPanel.add(xTextField);
		optionPanel.add(yLabel);
		optionPanel.add(yTextField, "wrap");
		optionPanel.add(textScroll, "pushx, growx");
	}

	private void setTablePanel() {
		tablePanel = new JPanel(new MigLayout("debug"));

		// Override cell editable.
		model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(model);
		tableScroll = new JScrollPane(table);

		model.addColumn("Start Time");
		model.addColumn("End Time");
		model.addColumn("Text");
		model.addColumn("Font");
		model.addColumn("Size");
		model.addColumn("Colour");
		model.addColumn("x");
		model.addColumn("y");

		tablePanel.add(tableScroll);	
	}

	private void setButtonPanel() {
		buttonPanel = new JPanel(new MigLayout("debug"));

		previewButton = new JButton("Preview");

		previewButton.setForeground(Color.WHITE);
		previewButton.setBackground(new Color(183, 183, 183));

		saveButton = new JButton("Save Video");

		saveButton.setForeground(Color.WHITE);
		saveButton.setBackground(new Color(255, 106, 106));

		addButton = new JButton("Add");
		editChangeButton = new JButton(EDITCHANGE[0]);
		deleteButton = new JButton("Delete");

		buttonPanel.add(addButton, "split 3, pushx, align center");
		buttonPanel.add(editChangeButton);
		buttonPanel.add(deleteButton, "wrap");
		buttonPanel.add(previewButton, "split 2, pushx, align center");
		buttonPanel.add(saveButton);
	}

	private void addListeners() {
		saveButton.addActionListener(this);

		fontCombo.addActionListener(this);
		fontSizeCombo.addActionListener(this);
		fontColorCombo.addActionListener(this);

		// Sets the preferred index of font size. It also calls event listener which is important for displaying correct font.
		fontSizeCombo.setSelectedIndex(3);

		previewButton.addActionListener(this);
		addButton.addActionListener(this);
		editChangeButton.addActionListener(this);
		deleteButton.addActionListener(this);
		startButton.addActionListener(this);
		endButton.addActionListener(this);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fontCombo || e.getSource() == fontColorCombo || e.getSource() == fontSizeCombo) {
			// Changes font and font size.
			Font font = ((FilterFont)fontCombo.getSelectedItem()).getFont();
			font = font.deriveFont((float)((Integer)fontSizeCombo.getSelectedItem()));

			textArea.setFont(font);
			textArea.setForeground(((FilterColor)fontColorCombo.getSelectedItem()).getColor());
		} else if (e.getSource() == saveButton) {
			if (verifyInput()) {
				String outputFilename = videoFileSelection.getOutputFilename();

				if (outputFilename != null) {
					executeFilterSave(outputFilename);
				}

			}
		} else if (e.getSource() == previewButton) {
			int selection = table.getSelectedRow();

			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Select text filter to preview");
			} else {

				FilterPreviewWorker worker = new FilterPreviewWorker(VamixProcesses.getFilename(mediaPlayer.mrl()), getFilterData());

				worker.execute();

			}

		} else if (e.getSource() == addButton) {
			Object[] data = getFilterData();

			model.addRow(data);

			if (needSorting()) {
				sortData();
			}

		} else if (e.getSource() == editChangeButton) {
			int selection = table.getSelectedRow();
			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Show text filter to edit");
			} else {
				if (editChangeButton.getText().equals(EDITCHANGE[0])) {
					editChangeButton.setText(EDITCHANGE[1]);

					rowToEdit = selection;

					table.setRowSelectionAllowed(false);
					addButton.setEnabled(false);
					deleteButton.setEnabled(false);
					previewButton.setEnabled(false);
					saveButton.setEnabled(false);

				} else {
					editChangeButton.setText(EDITCHANGE[0]);

					table.setRowSelectionAllowed(true);
					addButton.setEnabled(true);
					deleteButton.setEnabled(true);
					previewButton.setEnabled(true);
					saveButton.setEnabled(true);

					Object[] data = getFilterData();

					model.setValueAt(data[0], rowToEdit, 0);
					model.setValueAt(data[1], rowToEdit, 1);
					model.setValueAt(data[2], rowToEdit, 2);
					model.setValueAt(data[3], rowToEdit, 3);
					model.setValueAt(data[4], rowToEdit, 4);
					model.setValueAt(data[5], rowToEdit, 5);
					model.setValueAt(data[6], rowToEdit, 6);
					model.setValueAt(data[7], rowToEdit, 7);
				}
			}
		} else if (e.getSource() == deleteButton) {
			int selection = table.getSelectedRow();
			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Please select a row to delete");
			} else {
				model.removeRow(selection);
			}
		} else if (e.getSource() == startButton) {
			if (mediaPlayer.isPlayable()) {
				String formattedTime = MediaTimer.getFormattedTime(mediaPlayer.getTime());
				setStartTime(formattedTime);
			} else {
				JOptionPane.showMessageDialog(null, "Media must be playing");
			}
		} else if (e.getSource() == endButton) {
			if (mediaPlayer.isPlayable()) {
				String formattedTime = MediaTimer.getFormattedTime(mediaPlayer.getTime());
				setEndTime(formattedTime);
			} else {
				JOptionPane.showMessageDialog(null, "Media must be playing");
			}
		}
	}

	/**
	 * Sets the values for the start JSpinners. Input must be formatted time hh:mm:ss or exception will be thrown.
	 * @param formattedTime
	 */

	private void setStartTime(String formattedTime) {
		String[] firstTime = formattedTime.split(":");
		startSpinnerHours.setValue(Integer.parseInt(firstTime[0]));
		startSpinnerMinutes.setValue(Integer.parseInt(firstTime[1]));
		startSpinnerSeconds.setValue(Integer.parseInt(firstTime[2]));
	}

	/**
	 * Sets the values for the end JSpinners. Input must be formatted time hh:mm:ss or exception will be thrown.
	 * @param formattedTime
	 */

	private void setEndTime(String formattedTime) {
		String[] secondTime = formattedTime.split(":");

		endSpinnerHours.setValue(Integer.parseInt(secondTime[0]));
		endSpinnerMinutes.setValue(Integer.parseInt(secondTime[1]));
		endSpinnerSeconds.setValue(Integer.parseInt(secondTime[2]));
	}

	private Object[] getFilterData() {
		Object[] data = new Object[8];

		// Start Time
		data[0] = MediaTimer.getFormattedTime((int)startSpinnerHours.getValue(), (int)startSpinnerMinutes.getValue(), (int)startSpinnerSeconds.getValue());
		// End Time
		data[1] = MediaTimer.getFormattedTime((int)endSpinnerHours.getValue(), (int)endSpinnerMinutes.getValue(), (int)endSpinnerSeconds.getValue());
		// Text
		data[2] = textArea.getText();
		// Font
		data[3] = ((FilterFont)fontCombo.getSelectedItem()).getPath();
		// Font Size
		data[4] = ((Integer)fontSizeCombo.getSelectedItem()).toString();
		// Font Colour
		data[5] = ((FilterColor)fontColorCombo.getSelectedItem()).toString();
		// x
		data[6] = xTextField.getText();
		// y
		data[7] = yTextField.getText();

		return data;
	}

	/**
	 * Checks if the table needs sorting.
	 * @return is sorting needed
	 */

	private boolean needSorting() {
		if (model.getRowCount() > 1) {
			// Determines if the row needs to be changed.
			RowSort sorter = new RowSort();
			Object o1 = model.getDataVector().get(model.getRowCount() - 1);
			Object o2 = model.getDataVector().get(model.getRowCount() - 2);

			if (sorter.compare(o1, o2) == -1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Sorts the rows to be sorted and then calls fireTableDataChanged to notify all listeners that data has been changed.
	 */

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void sortData() {
		Vector data = model.getDataVector();
		Collections.sort(data, new RowSort());
		model.fireTableDataChanged();
	}

	/**
	 * {@link worker.FilterSaveWorker} <br />
	 * Executes FilterSaveWorker and shows progress monitor.
	 * 
	 * @param outputFilename
	 */

	private void executeFilterSave(String outputFilename) {
		int videoLength = (int)(mediaPlayer.getLength() / 1000);

		ProgressMonitor monitor = new ProgressMonitor(null, "Filtering has started", "", 0, videoLength);

		ArrayList<Object[]> textList = new ArrayList<Object[]>();
		for (Object element : model.getDataVector()) {
			Vector v = (Vector)element;
			textList.add(v.toArray());
		}


		FilterSaveWorker worker = new FilterSaveWorker(VamixProcesses.getFilename(mediaPlayer.mrl()), outputFilename, textList, monitor);
		worker.execute();
	}

	/**
	 * Verifies media is parsed. Also makes sure media is a video type.
	 * @return
	 */

	private boolean verifyInput() {

		// If media is not parsed, return false;
		if (!mediaPlayer.isPlayable()) {
			JOptionPane.showMessageDialog(null, "Please parse media");
			return false;
		}

		String inputFilename = VamixProcesses.getFilename(mediaPlayer.mrl());


		if (!VamixProcesses.validContentType(FileType.VIDEO, inputFilename)) {
			JOptionPane.showMessageDialog(null, "This is not a video file");
			return false;
		}

		String text = textArea.getText();

		// Return false if both textArea are empty
		if (text.equals("")) {
			JOptionPane.showMessageDialog(null, "Please input some text to text area");
			return false;
		}


		return true;
	}
}
