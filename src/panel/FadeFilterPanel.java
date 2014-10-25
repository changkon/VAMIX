package panel;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ProgressMonitor;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import operation.FileSelection;
import operation.LogSession;
import operation.MediaTimer;
import operation.TextFileSelection;
import operation.VamixProcesses;
import operation.VideoFileSelection;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.FadeFilterPreviewWorker;
import worker.FadeFilterSaveWorker;
import component.RowSort;

public class FadeFilterPanel extends JPanel implements ActionListener {
	private static FadeFilterPanel theInstance = null;
	
	public String[] fadeSelection = {"Fade In", "Fade Out"};
	
	private JComboBox<String> fadeCombo;
	
	private JLabel fadeLabel;
	
	private JSpinner startSpinnerSeconds, startSpinnerMinutes, startSpinnerHours, endSpinnerSeconds, endSpinnerMinutes, endSpinnerHours;
	
	private JButton previewButton, saveButton, saveWorkButton, loadWorkButton, addButton, editChangeButton, deleteButton, startButton, endButton;
	
	private JScrollPane tableScroll;
	private JTable table;
	private DefaultTableModel model;
	
	private FileSelection videoFileSelection, textFileSelection;
	
	private String[] EDITCHANGE = {"Edit", "Change"};
	private int rowToEdit;
	
	private EmbeddedMediaPlayer mediaPlayer;
	
	public static FadeFilterPanel getInstance() {
		if (theInstance == null) {
			theInstance = new FadeFilterPanel();
		}
		return theInstance;
	}
	
	private FadeFilterPanel() {
		setLayout(new MigLayout());
		
		setPanel();
		addListeners();
		
		add(fadeLabel, "wrap");
		add(fadeCombo, "wrap");
		add(startButton, "split 8");
		add(startSpinnerHours);
		add(startSpinnerMinutes, "gap 0");
		add(startSpinnerSeconds, "gap 0");
		add(endButton);
		add(endSpinnerHours);
		add(endSpinnerMinutes, "gap 0");
		add(endSpinnerSeconds, "gap 0, wrap");
		add(addButton, "split 3");
		add(editChangeButton);
		add(deleteButton, "wrap");
		add(tableScroll, "wrap");
		add(previewButton, "split 4");
		add(saveButton);
		add(saveWorkButton);
		add(loadWorkButton);
	}
	
	private void setPanel() {
		mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
		
		fadeCombo = new JComboBox<String>(fadeSelection);
		
		fadeLabel = new JLabel("Fade");
		// Sets new font for fadeLabel.
		Font font = fadeLabel.getFont().deriveFont(Font.BOLD + Font.ITALIC, 14f);
		fadeLabel.setFont(font);
		
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
		
		previewButton = new JButton("Preview Fade");
		saveButton = new JButton("Save Fade");
		addButton = new JButton("Add Fade");
		editChangeButton = new JButton(EDITCHANGE[0]);
		deleteButton = new JButton("Delete Fade");
		startButton = new JButton("Start");
		endButton = new JButton("End");
		saveWorkButton = new JButton("Save Work");
		loadWorkButton = new JButton("Load Work");
		
		videoFileSelection = new VideoFileSelection();
		textFileSelection = new TextFileSelection();
		
		// Override cell editable.
		model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(model);
		
		model.addColumn("Start Time");
		model.addColumn("End Time");
		model.addColumn("Type");
		
		tableScroll = new JScrollPane(table);
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
	
	private Object[] getFadeData() {
		Object[] data = new Object[3];
		
		data[0] = MediaTimer.getFormattedTime((int)startSpinnerHours.getValue(), (int)startSpinnerMinutes.getValue(), (int)startSpinnerSeconds.getValue());
		data[1] = MediaTimer.getFormattedTime((int)endSpinnerHours.getValue(), (int)endSpinnerMinutes.getValue(), (int)endSpinnerSeconds.getValue());
		data[2] = fadeCombo.getSelectedItem();
		
		return data;
	}
	
	private void executeFadeFilterPreview() {
		FadeFilterPreviewWorker worker = new FadeFilterPreviewWorker(VamixProcesses.getFilename(mediaPlayer.mrl()), getFadeData(), mediaPlayer.getFps());
		worker.execute();
	}
	
	private void executeFadeFilterSave(String outputFilename) {
		int videoLength = (int)(mediaPlayer.getLength() / 1000);

		ProgressMonitor monitor = new ProgressMonitor(null, "Filtering has started", "", 0, videoLength);

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
			int selection = table.getSelectedRow();

			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Select fade filter to preview");
			} else {
				executeFadeFilterPreview();
			}
		} else if (e.getSource() == saveButton) {
			String outputFilename = videoFileSelection.getOutputFilename();

			if (outputFilename != null) {
				executeFadeFilterSave(outputFilename);
			}
		} else if (e.getSource() == addButton) {
			Object[] data = getFadeData();
			
			model.addRow(data);

			if (needSorting()) {
				sortData();
			}
			
		} else if (e.getSource() == editChangeButton) {
			int selection = table.getSelectedRow();
			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Press fade filter to edit");
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
					
					Object[] data = getFadeData();
					
					model.setValueAt(data[0], rowToEdit, 0);
					model.setValueAt(data[1], rowToEdit, 1);
					model.setValueAt(data[2], rowToEdit, 2);
					
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
		}
	}
}
