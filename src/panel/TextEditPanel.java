package panel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.AbstractDocument;

import net.miginfocom.swing.MigLayout;
import operation.FileSelection;
import operation.LogSession;
import operation.MediaTimer;
import operation.TextFileSelection;
import operation.VamixProcesses;
import operation.VideoFileSelection;
import res.FilterColor;
import res.FilterFont;
import res.MediaIcon;
import setting.MediaSetting;
import worker.TextFilterPreviewWorker;
import worker.TextFilterSaveWorker;

import component.FileType;
import component.MyStyledDocument;
import component.MyTextFieldFilter;
import component.Playback;

/**
 * Singleton design pattern. Panel contains anything related to filter editing of video.
 * 
 */

@SuppressWarnings("serial")
public class TextEditPanel extends SpinnerTableTemplatePanel implements ActionListener {
	private static TextEditPanel theInstance = null;
	private TitledBorder title;

	private JPanel tablePanel, optionPanel, buttonPanel, navigationPanel;
	private JTextArea textArea;
	private JScrollPane textScroll;

	private Integer[] fontSizeSelection = {10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40};

	private JComboBox<FilterFont> fontCombo;
	private JComboBox<Integer> fontSizeCombo;
	private JComboBox<FilterColor> fontColorCombo;

	private JLabel xLabel, yLabel, textLabel;

	private JTextField xTextField, yTextField;

	private JButton previewButton, saveButton, saveWorkButton, loadWorkButton, rightButton;

	private FileSelection videoFileSelection, textFileSelection;

	public static TextEditPanel getInstance() {
		if (theInstance == null) {
			theInstance = new TextEditPanel();
		}
		return theInstance;
	}

	private TextEditPanel() {
		setLayout(new MigLayout("", "", "[][][]push"));

		title = BorderFactory.createTitledBorder("Text Editing");
		setBorder(title);

		videoFileSelection = new VideoFileSelection();
		textFileSelection = new TextFileSelection();

		setOptionPanel();
		setTablePanel();
		setButtonPanel();
		setNavigationPanel();
		
		add(optionPanel, "pushx, growx, wrap");
		add(tablePanel, "pushx, growx, height 300px, wrap");
		add(buttonPanel, "pushx, growx, wrap");
		add(navigationPanel, "dock south");
		addListeners();	
	}

	private void setOptionPanel() {
		optionPanel = new JPanel(new MigLayout());

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

		textLabel = new JLabel("<html>Text (" + maxWords + " words max for each selection).<br/> Choose text style and set X and Y co-ordinates. " + 
				"The x co-ordinate go from top left to right and y is top to down.</html>");

		// Sets new font for textLabel.
		Font font = textLabel.getFont().deriveFont(Font.BOLD, 14f);
		textLabel.setFont(font);

		optionPanel.add(textLabel, "wrap 20px");
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
		optionPanel.add(xLabel, "split 4");
		optionPanel.add(xTextField);
		optionPanel.add(yLabel);
		optionPanel.add(yTextField, "wrap");
		optionPanel.add(textScroll, "pushx, growx");
	}

	private void setTablePanel() {
		tablePanel = new JPanel(new MigLayout());

		model.addColumn("Start");
		model.addColumn("End");
		model.addColumn("Text");
		model.addColumn("Font");
		model.addColumn("Size");
		model.addColumn("Colour");
		model.addColumn("x");
		model.addColumn("y");

		tablePanel.add(tableScroll, "pushx, growx");	
	}

	private void setButtonPanel() {
		buttonPanel = new JPanel(new MigLayout());

		previewButton = new JButton("Preview");
		previewButton.setBackground(new Color(219, 219, 219)); // light grey

		saveButton = new JButton("Save Video");
		saveButton.setBackground(new Color(219, 219, 219)); // light grey

		saveWorkButton = new JButton("Save Work");
		saveWorkButton.setBackground(new Color(219, 219, 219)); // light grey

		loadWorkButton = new JButton("Load Work");
		loadWorkButton.setBackground(new Color(219, 219, 219)); // light grey

		buttonPanel.add(addButton, "split 3, pushx, align center");
		buttonPanel.add(editChangeButton);
		buttonPanel.add(deleteButton, "wrap 20px");
		buttonPanel.add(previewButton, "split 4, pushx, align center");
		buttonPanel.add(saveButton);
		buttonPanel.add(saveWorkButton);
		buttonPanel.add(loadWorkButton);
	}

	private void setNavigationPanel() {
		navigationPanel = new JPanel(new MigLayout());
		
		MediaIcon mediaIcon = new MediaIcon(15, 15);
		rightButton = new JButton(mediaIcon.getIcon(Playback.RIGHT));
		
		rightButton.setToolTipText("Go to next page");
		rightButton.setBorderPainted(false);
		rightButton.setFocusPainted(false);
		rightButton.setContentAreaFilled(false);
		
		navigationPanel.add(rightButton, "pushx, align right");
	}
	
	private void addListeners() {
		fontCombo.addActionListener(this);
		fontSizeCombo.addActionListener(this);
		fontColorCombo.addActionListener(this);

		// Sets the preferred index of font size. It also calls event listener which is important for displaying correct font.
		fontSizeCombo.setSelectedIndex(3);

		saveButton.addActionListener(this);
		previewButton.addActionListener(this);
		addButton.addActionListener(this);
		editChangeButton.addActionListener(this);
		deleteButton.addActionListener(this);
		saveWorkButton.addActionListener(this);
		loadWorkButton.addActionListener(this);
		startButton.addActionListener(this);
		endButton.addActionListener(this);
		
		rightButton.addActionListener(this);
		
		rightButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	rightButton.setBorderPainted(true);
	            } else {
	            	rightButton.setBorderPainted(false);
	            }
	        }
	    });
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fontCombo || e.getSource() == fontColorCombo || e.getSource() == fontSizeCombo) {
			// Changes font and font size.
			Font font = ((FilterFont)fontCombo.getSelectedItem()).getFont();
			font = font.deriveFont((float)((Integer)fontSizeCombo.getSelectedItem()));

			textArea.setFont(font);
			textArea.setForeground(((FilterColor)fontColorCombo.getSelectedItem()).getColor());
		} else if (e.getSource() == saveButton) {
			if (verifyMedia()) {
				String outputFilename = videoFileSelection.getOutputFilename();

				if (outputFilename != null) {
					executeFilterSave(outputFilename);
				}
			}

		} else if (e.getSource() == previewButton) {
			if (verifyMedia()) {
				int selection = table.getSelectedRow();

				if (selection == -1) {
					JOptionPane.showMessageDialog(null, "Select text filter to preview");
				} else {
					executeFilterPreview(selection);
				}
			}
		} else if (e.getSource() == addButton) {
			if (verifyData()) {
				Object[] data = getFilterData();

				model.addRow(data);

				if (needSorting()) {
					sortData();
				}
			}

		} else if (e.getSource() == editChangeButton) {
			int selection = table.getSelectedRow();
			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Select text filter to edit");
			} else {
				if (editChangeButton.getText().equals(EDITCHANGE[0])) {
					editChangeButton.setText(EDITCHANGE[1]);

					rowToEdit = selection;

					addButton.setEnabled(false);
					deleteButton.setEnabled(false);
					previewButton.setEnabled(false);
					saveButton.setEnabled(false);
					saveWorkButton.setEnabled(false);
					loadWorkButton.setEnabled(false);

				} else {
					if (verifyData()) {
						editChangeButton.setText(EDITCHANGE[0]);

						addButton.setEnabled(true);
						deleteButton.setEnabled(true);
						previewButton.setEnabled(true);
						saveButton.setEnabled(true);
						saveWorkButton.setEnabled(true);
						loadWorkButton.setEnabled(true);

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
		} else if (e.getSource() == saveWorkButton) {
			String outputfilename = textFileSelection.getOutputFilename();

			if (outputfilename != null) {
				LogSession.saveLog(outputfilename, "Text Editing", model.getDataVector());
			}

		} else if (e.getSource() == loadWorkButton) {
			String inputFilename = textFileSelection.getInputFilename();

			if (inputFilename != null) {
				ArrayList<Object[]> list = LogSession.getLog(inputFilename, "Text Editing");

				if (list != null) {
					model.setRowCount(0);

					for (Object[] element : list) {
						model.addRow(element);
					}
				}
			}
		} else if (e.getSource() == rightButton) {
			VideoFilterPanel videoFilterPanel = VideoFilterPanel.getInstance();
			CardLayout card = (CardLayout)videoFilterPanel.getLayout();
			
			card.show(videoFilterPanel, videoFilterPanel.FADEFILTERSTRING);
		}
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
	 * {@link worker.FilterSaveWorker} <br />
	 * Executes FilterSaveWorker and shows progress monitor.
	 * 
	 * @param outputFilename
	 */

	private void executeFilterSave(String outputFilename) {
		int videoLength = (int)(mediaPlayer.getLength() / 1000);

		ProgressMonitor monitor = new ProgressMonitor(null, "Text Filtering has started", "", 0, videoLength);

		ArrayList<Object[]> textList = new ArrayList<Object[]>();
		for (Object element : model.getDataVector()) {
			@SuppressWarnings("rawtypes")
			Vector v = (Vector)element;
			textList.add(v.toArray());
		}

		TextFilterSaveWorker worker = new TextFilterSaveWorker(VamixProcesses.getFilename(mediaPlayer.mrl()), outputFilename, textList, monitor);
		worker.execute();
	}

	/**
	 * Executes FilterPreviewWorker. Previews the selected text edit.
	 */

	private void executeFilterPreview(int selection) {
		Object[] data = new Object[8];
		// Start Time
		data[0] = model.getValueAt(selection, 0);
		// End Time
		data[1] = model.getValueAt(selection, 1);
		// Text
		data[2] = model.getValueAt(selection, 2);
		// Font
		data[3] = model.getValueAt(selection, 3);
		// Font Size
		data[4] = model.getValueAt(selection, 4);
		// Font Colour
		data[5] = model.getValueAt(selection, 5);
		// x
		data[6] = model.getValueAt(selection, 6);
		// y
		data[7] = model.getValueAt(selection, 7);
		
		TextFilterPreviewWorker worker = new TextFilterPreviewWorker(VamixProcesses.getFilename(mediaPlayer.mrl()), data);
		worker.execute();
	}

	/**
	 * Verifies correct information is input before adding to table.
	 * @return
	 */

	private boolean verifyData() {
		String startTime = MediaTimer.getFormattedTime((int)startSpinnerHours.getValue(), (int)startSpinnerMinutes.getValue(), (int)startSpinnerSeconds.getValue());
		String endTime = MediaTimer.getFormattedTime((int)endSpinnerHours.getValue(), (int)endSpinnerMinutes.getValue(), (int)endSpinnerSeconds.getValue());
		int difference = MediaTimer.getSeconds(endTime) - MediaTimer.getSeconds(startTime);

		if (!xTextField.getText().equals("") && !yTextField.getText().equals("") && !textArea.getText().equals("") && (difference > 0)) {
			return true;
		} else {
			JOptionPane.showMessageDialog(null, "Please make sure all information is input or check that end time is after start time.");
			return false;
		}
	}

	/**
	 * Verifies media is parsed. Also makes sure media is a video type.
	 * @return
	 */

	private boolean verifyMedia() {

		// If media is not parsed, return false;
		if (!mediaPlayer.isPlayable()) {
			JOptionPane.showMessageDialog(null, "Please make sure a media file is playing.");
			return false;
		}

		String inputFilename = VamixProcesses.getFilename(mediaPlayer.mrl());

		if (!VamixProcesses.validContentType(FileType.VIDEO, inputFilename)) {
			JOptionPane.showMessageDialog(null, "This is not a video file");
			return false;
		}

		return true;
	}
}
