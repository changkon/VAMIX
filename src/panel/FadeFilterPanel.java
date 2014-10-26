package panel;

import java.awt.CardLayout;
import java.awt.Color;
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
import javax.swing.ProgressMonitor;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import operation.FileSelection;
import operation.LogSession;
import operation.MediaTimer;
import operation.TextFileSelection;
import operation.VamixProcesses;
import operation.VideoFileSelection;
import res.MediaIcon;
import worker.FadeFilterPreviewWorker;
import worker.FadeFilterSaveWorker;

import component.FileType;
import component.Playback;

@SuppressWarnings("serial")
public class FadeFilterPanel extends SpinnerTableTemplatePanel implements ActionListener {
	private static FadeFilterPanel theInstance = null;
	
	private TitledBorder title;
	public String[] fadeSelection = {"Fade In", "Fade Out"};
	
	private JComboBox<String> fadeCombo;
	
	private JLabel fadeLabel;
	
	private JButton previewButton, saveButton, saveWorkButton, loadWorkButton, leftButton, rightButton;
	
	private JPanel optionPanel, tablePanel, buttonPanel, navigationPanel;
	
	private FileSelection videoFileSelection, textFileSelection;
	
	public static FadeFilterPanel getInstance() {
		if (theInstance == null) {
			theInstance = new FadeFilterPanel();
		}
		return theInstance;
	}
	
	private FadeFilterPanel() {
		setLayout(new MigLayout("", "", "[][][]push"));
		
		title = BorderFactory.createTitledBorder("Fade Filter");
		setBorder(title);
		
		videoFileSelection = new VideoFileSelection();
		textFileSelection = new TextFileSelection();
		
		setOptionPanel();
		setTablePanel();
		setButtonPanel();
		setNavigationPanel();
		addListeners();
		
		add(optionPanel, "pushx, growx, wrap");
		add(tablePanel, "pushx, growx, wrap");
		add(buttonPanel, "pushx, growx, wrap");
		add(navigationPanel, "south");
	}
	
	private void setOptionPanel() {
		optionPanel = new JPanel(new MigLayout());
		
		fadeLabel = new JLabel("<html>Choose fade in or fade out option to add to video. <br/> Note: Start time is when the fade effect starts and end time" + 
				" is when fade effect finishes.</html>");
		// Sets new font for fadeLabel.
		Font font = fadeLabel.getFont().deriveFont(Font.BOLD, 14f);
		fadeLabel.setFont(font);
		
		fadeCombo = new JComboBox<String>(fadeSelection);
		
		optionPanel.add(fadeLabel, "wrap 20px");
		optionPanel.add(startButton, "split 8");
		optionPanel.add(startSpinnerHours);
		optionPanel.add(startSpinnerMinutes, "gap 0");
		optionPanel.add(startSpinnerSeconds, "gap 0");
		optionPanel.add(endButton);
		optionPanel.add(endSpinnerHours);
		optionPanel.add(endSpinnerMinutes, "gap 0");
		optionPanel.add(endSpinnerSeconds, "gap 0, wrap 15px");
		optionPanel.add(fadeCombo);
	}
	
	private void setTablePanel() {
		tablePanel = new JPanel(new MigLayout());
		
		model.addColumn("Start Time");
		model.addColumn("End Time");
		model.addColumn("Type");
		
		tablePanel.add(tableScroll, "height 100px, pushx, growx, wrap 30px");
		tablePanel.add(addButton, "split 3, align center");
		tablePanel.add(editChangeButton);
		tablePanel.add(deleteButton);
	}
	
	private void setButtonPanel() {
		buttonPanel = new JPanel(new MigLayout());
		
		previewButton = new JButton("Preview Fade");
		previewButton.setBackground(new Color(219, 219, 219)); // light grey
		
		saveButton = new JButton("Save Fade");
		saveButton.setBackground(new Color(219, 219, 219)); // light grey
		
		saveWorkButton = new JButton("Save Work");
		saveWorkButton.setBackground(new Color(219, 219, 219)); // light grey
		
		loadWorkButton = new JButton("Load Work");
		loadWorkButton.setBackground(new Color(219, 219, 219)); // light grey
		
		buttonPanel.add(previewButton, "pushx, split 4, align center");
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
		
		leftButton = new JButton(mediaIcon.getIcon(Playback.LEFT));
		
		leftButton.setToolTipText("Go to previous page");
		leftButton.setBorderPainted(false);
		leftButton.setFocusPainted(false);
		leftButton.setContentAreaFilled(false);
		
		navigationPanel.add(leftButton, "pushx, align left");
		navigationPanel.add(rightButton, "pushx, align right");
	}
	
	private void addListeners() {
		previewButton.addActionListener(this);
		saveButton.addActionListener(this);
		addButton.addActionListener(this);
		editChangeButton.addActionListener(this);
		deleteButton.addActionListener(this);
		startButton.addActionListener(this);
		endButton.addActionListener(this);
		saveWorkButton.addActionListener(this);
		loadWorkButton.addActionListener(this);
		
		rightButton.addActionListener(this);
		leftButton.addActionListener(this);
		
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
		
		leftButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	leftButton.setBorderPainted(true);
	            } else {
	            	leftButton.setBorderPainted(false);
	            }
	        }
	    });
	}
	
	/**
	 * Gets the values currently on the panel (currently selected comboBox) and start and end times.
	 * @return the data from the panel
	 */
	
	private Object[] getFadeData() {
		Object[] data = new Object[3];
		
		data[0] = MediaTimer.getFormattedTime((int)startSpinnerHours.getValue(), (int)startSpinnerMinutes.getValue(), (int)startSpinnerSeconds.getValue());
		data[1] = MediaTimer.getFormattedTime((int)endSpinnerHours.getValue(), (int)endSpinnerMinutes.getValue(), (int)endSpinnerSeconds.getValue());
		data[2] = fadeCombo.getSelectedItem();
		
		return data;
	}
	
	/**
	 * Get the data from the selected row and then preview the fade effect. {@link worker.FadeFilterPreviewWorker}
	 * @param selection
	 */
	
	private void executeFadeFilterPreview(int selection) {
		Object[] data = new Object[3];
		data[0] = model.getValueAt(selection, 0);
		data[1] = model.getValueAt(selection, 1);
		data[2] = model.getValueAt(selection, 2);
		FadeFilterPreviewWorker worker = new FadeFilterPreviewWorker(VamixProcesses.getFilename(mediaPlayer.mrl()), data, mediaPlayer.getFps());
		worker.execute();
	}
	
	/**
	 * Get all the values in the table and produce a video with the fade effects. {@link worker.FadeFilterSaveWorker}
	 * @param outputFilename
	 */
	
	private void executeFadeFilterSave(String outputFilename) {
		int videoLength = (int)(mediaPlayer.getLength() / 1000);

		ProgressMonitor monitor = new ProgressMonitor(null, "Fade effect filtering has started", "", 0, videoLength);

		ArrayList<Object[]> fadeList = new ArrayList<Object[]>();
		for (Object element : model.getDataVector()) {
			@SuppressWarnings("rawtypes")
			Vector v = (Vector)element;
			fadeList.add(v.toArray());
		}
		
		FadeFilterSaveWorker worker = new FadeFilterSaveWorker(
				VamixProcesses.getFilename(mediaPlayer.mrl()),
				outputFilename,
				fadeList,
				mediaPlayer.getFps(),
				monitor
				);
		
		worker.execute();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == previewButton) {
			if (verifyMedia()) {
				int selection = table.getSelectedRow();
	
				if (selection == -1) {
					JOptionPane.showMessageDialog(null, "Select fade filter to preview");
				} else {
					executeFadeFilterPreview(selection);
				}
			}
		} else if (e.getSource() == saveButton) {
			if (verifyMedia()) {
				String outputFilename = videoFileSelection.getOutputFilename();
	
				if (outputFilename != null) {
					executeFadeFilterSave(outputFilename);
				}
			}
		} else if (e.getSource() == addButton) {
			if (verifyData()) {
				Object[] data = getFadeData();
				
				model.addRow(data);
	
				if (needSorting()) {
					sortData();
				}
			}
			
		} else if (e.getSource() == editChangeButton) {
			int selection = table.getSelectedRow();
			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Select fade filter to edit");
			} else {
				if (editChangeButton.getText().equals(EDITCHANGE[0])) {
					editChangeButton.setText(EDITCHANGE[1]);
					
					rowToEdit = selection;
					
					fadeCombo.setSelectedItem(model.getValueAt(selection, 2));
					
					addButton.setEnabled(false);
					deleteButton.setEnabled(false);
					previewButton.setEnabled(false);
					saveButton.setEnabled(false);
					saveWorkButton.setEnabled(false);
					loadWorkButton.setEnabled(false);
					fadeCombo.setEnabled(false);
				} else {
					if (verifyData()) {
						editChangeButton.setText(EDITCHANGE[0]);
						
						addButton.setEnabled(true);
						deleteButton.setEnabled(true);
						previewButton.setEnabled(true);
						saveButton.setEnabled(true);
						saveWorkButton.setEnabled(true);
						loadWorkButton.setEnabled(true);
						fadeCombo.setEnabled(true);
						
						Object[] data = getFadeData();
						
						model.setValueAt(data[0], rowToEdit, 0);
						model.setValueAt(data[1], rowToEdit, 1);
						model.setValueAt(data[2], rowToEdit, 2);
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
				LogSession.saveLog(outputfilename, "Fade", model.getDataVector());
			}
		} else if (e.getSource() == loadWorkButton) {
			String inputFilename = textFileSelection.getInputFilename();
			
			if (inputFilename != null) {
				ArrayList<Object[]> list = LogSession.getLog(inputFilename, "Fade");
				
				if (list != null) {
					model.setRowCount(0);

					for (Object[] element : list) {
						model.addRow(element);
					}
				}
			}
		} else if (e.getSource() == leftButton) {
			VideoFilterPanel videoFilterPanel = VideoFilterPanel.getInstance();
			CardLayout card = (CardLayout)videoFilterPanel.getLayout();
			card.show(videoFilterPanel, videoFilterPanel.TEXTEDITSTRING);
			
		} else if (e.getSource() == rightButton) {
			VideoFilterPanel videoFilterPanel = VideoFilterPanel.getInstance();
			CardLayout card = (CardLayout)videoFilterPanel.getLayout();
			card.show(videoFilterPanel, videoFilterPanel.SUBTITLESTRING);
		}
		
		
	}
	
	/**
	 * Determines if data is valid to add to table. Checks two conditions, firstly, the end time is after the start time and that duplicate
	 * fade effects are not added.
	 * @return valid data
	 */
	
	private boolean verifyData() {
		String startTime = MediaTimer.getFormattedTime((int)startSpinnerHours.getValue(), (int)startSpinnerMinutes.getValue(), (int)startSpinnerSeconds.getValue());
		String endTime = MediaTimer.getFormattedTime((int)endSpinnerHours.getValue(), (int)endSpinnerMinutes.getValue(), (int)endSpinnerSeconds.getValue());
		int difference = MediaTimer.getSeconds(endTime) - MediaTimer.getSeconds(startTime);
		
		if (difference <= 0) {
			JOptionPane.showMessageDialog(null, "Please make sure that the end time is later than the start time.");
			return false;
		}
		
		// Checking arbitrary button to see if this should be checked. I check if button is enabled to make sure if value is being edited or not.
		// If the value is being edited, this should be ignored as same fade type will be added.
		if (saveWorkButton.isEnabled()) {
			String fadeType = (String)fadeCombo.getSelectedItem();
			
			if (model.getRowCount() > 0) {
				for (Object element : model.getDataVector()) {
					Vector v = (Vector)element;
					Object value = v.get(2);
					if (value.equals(fadeType)) {
						JOptionPane.showMessageDialog(null, "Cannot add duplicate fade types to a video");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * Verifies media is parsed. Also makes sure media is a video type as fade effects cannot be added to an audio file.
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
