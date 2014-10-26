package panel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import operation.MediaTimer;
import operation.SubtitleFileSelection;
import operation.VamixProcesses;
import res.MediaIcon;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.SubtitleWorker;

import component.Playback;
import component.RowSort;

@SuppressWarnings("serial")
public class SubtitlePanel extends JPanel implements ActionListener {
	private static SubtitlePanel theInstance = null;
	private TitledBorder title;
	
	private JPanel menuPanel, tablePanel, buttonPanel, navigationPanel;

	private JButton importButton, addButton, editChangeButton, deleteButton, startButton, endButton, saveSubtitleButton, addSubtitleToVideoButton, leftButton;

	private JSpinner startSpinnerSeconds, startSpinnerMinutes, startSpinnerHours, endSpinnerSeconds, endSpinnerMinutes, endSpinnerHours;

	private JTextArea textArea;

	private JTable table;

	private JScrollPane textScroll, tableScroll;

	private JLabel subtitleLabel, textLabel;
	
	private DefaultTableModel model;

	private final String[] EDITCHANGE = {"Edit", "Change"};

	private Pattern p;

	private int rowToEdit;
	
	private SubtitleFileSelection subtitleFileSelection;
	
	private EmbeddedMediaPlayer mediaPlayer;
	
	public static SubtitlePanel getInstance() {
		if (theInstance == null) {
			theInstance = new SubtitlePanel();
		}
		return theInstance;
	}

	private SubtitlePanel() {
		setLayout(new MigLayout("", "", "[][][]push"));

		title = BorderFactory.createTitledBorder("Subtitle");
		setBorder(title);
		
		mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
		
		setMenuPanel();
		setTablePanel();
		setButtonPanel();
		setNavigationPanel();
		
		subtitleFileSelection = new SubtitleFileSelection();
		
		add(menuPanel, "pushx, growx, wrap");
		add(tablePanel, "pushx, growx, wrap");
		add(buttonPanel, "pushx, growx, wrap");
		add(navigationPanel, "south");

		addListeners();
	}

	private void setMenuPanel() {
		menuPanel = new JPanel(new MigLayout());

		subtitleLabel = new JLabel("Add desired text to subtitle.");
		// Sets new font for subtitleLabel.
		Font subtitleFont = subtitleLabel.getFont().deriveFont(Font.BOLD, 14f);
		subtitleLabel.setFont(subtitleFont);
		
		textLabel = new JLabel("Put subtitle text in the text box");
		// Sets new font for textLabel.
		Font textFont = textLabel.getFont().deriveFont(Font.BOLD, 14f);
		textLabel.setFont(textFont);
		
		importButton = new JButton("Import");
		
		importButton.setForeground(Color.WHITE);
		importButton.setBackground(new Color(59, 89, 182)); // blue
		
		startButton = new JButton("Start");
		
		startButton.setForeground(Color.WHITE);
		startButton.setBackground(new Color(59, 89, 182)); // blue
		
		endButton = new JButton("End");
		
		endButton.setForeground(Color.WHITE);
		endButton.setBackground(new Color(59, 89, 182)); // blue
		
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
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textScroll = new JScrollPane(textArea);
		textScroll.setPreferredSize(new Dimension(400, 100));

		p = Pattern.compile("\\d\\d:\\d\\d:\\d\\d");

		menuPanel.add(subtitleLabel, "wrap 20px");
		menuPanel.add(importButton, "wrap");
		menuPanel.add(startButton, "split 8");
		menuPanel.add(startSpinnerHours);
		menuPanel.add(startSpinnerMinutes, "gap 0");
		menuPanel.add(startSpinnerSeconds, "gap 0");
		menuPanel.add(endButton);
		menuPanel.add(endSpinnerHours);
		menuPanel.add(endSpinnerMinutes, "gap 0");
		menuPanel.add(endSpinnerSeconds, "gap 0, wrap 20px");
		menuPanel.add(textLabel, "wrap");
		menuPanel.add(textScroll, "push, grow, wrap");
	}

	private void setTablePanel() {
		tablePanel = new JPanel(new MigLayout());

		// Override cell editable.
		model = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(model);
		table.setFillsViewportHeight(true);
		tableScroll = new JScrollPane(table);

		model.addColumn("Start Time");
		model.addColumn("End Time");
		model.addColumn("Text");

		tablePanel.add(tableScroll, "pushx, growx");
	}

	private void setButtonPanel() {
		buttonPanel = new JPanel(new MigLayout());
		
		addButton = new JButton("Add");
		addButton.setBackground(new Color(219, 219, 219)); // light grey
		
		editChangeButton = new JButton(EDITCHANGE[0]);
		editChangeButton.setBackground(new Color(219, 219, 219)); // light grey
		
		deleteButton = new JButton("Delete");
		deleteButton.setBackground(new Color(219, 219, 219)); // light grey
		
		saveSubtitleButton = new JButton("Save Subtitle");
		saveSubtitleButton.setBackground(new Color(219, 219, 219)); // light grey
		
		addSubtitleToVideoButton = new JButton("Add Subtitle to Video");
		addSubtitleToVideoButton.setBackground(new Color(219, 219, 219)); // light grey
		
		buttonPanel.add(addButton, "pushx, split 3, align center");
		buttonPanel.add(editChangeButton);
		buttonPanel.add(deleteButton, "wrap 30px");
		buttonPanel.add(saveSubtitleButton, "split 2, pushx, align center");
		buttonPanel.add(addSubtitleToVideoButton);
	}
	
	private void setNavigationPanel() {
		navigationPanel = new JPanel(new MigLayout());
		
		MediaIcon mediaIcon = new MediaIcon(15, 15);
		leftButton = new JButton(mediaIcon.getIcon(Playback.LEFT));
		
		leftButton.setToolTipText("Go to previous page");
		leftButton.setBorderPainted(false);
		leftButton.setFocusPainted(false);
		leftButton.setContentAreaFilled(false);
		
		navigationPanel.add(leftButton, "pushx, align left");
	}
	
	private void addListeners() {
		importButton.addActionListener(this);
		addButton.addActionListener(this);
		editChangeButton.addActionListener(this);
		deleteButton.addActionListener(this);
		saveSubtitleButton.addActionListener(this);
		addSubtitleToVideoButton.addActionListener(this);
		
		startButton.addActionListener(this);
		endButton.addActionListener(this);
		
		leftButton.addActionListener(this);
		
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == importButton) {
			String inputFilename = subtitleFileSelection.getInputFilename();

			if (inputFilename != null) {
				addData(inputFilename);
			}

		} else if (e.getSource() == addButton) {
			if (verifyData()) {
				Object[] data = getSubtitleData();
	
				model.addRow(data);
	
				if (needSorting()) {
					sortData();
				}
			}

		} else if (e.getSource() == editChangeButton) {
			int selection = table.getSelectedRow();
			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Please select a subtitle to edit");
			} else {
				if (editChangeButton.getText().equals(EDITCHANGE[0])) {
					editChangeButton.setText(EDITCHANGE[1]);
					rowToEdit = selection;

					addButton.setEnabled(false);
					deleteButton.setEnabled(false);
					saveSubtitleButton.setEnabled(false);
					addSubtitleToVideoButton.setEnabled(false);
					importButton.setEnabled(false);
					
					Object[] values = getVectorValue(model.getDataVector().get(selection));
					
					setStartTime(values[0].toString());
					setEndTime(values[1].toString());
					textArea.setText(values[2].toString());

				} else {
					if (verifyData()) {
						editChangeButton.setText(EDITCHANGE[0]);
						Object[] data = getSubtitleData();
						
						model.setValueAt(data[0], rowToEdit, 0);
						model.setValueAt(data[1], rowToEdit, 1);
						model.setValueAt(data[2], rowToEdit, 2);
						
						addButton.setEnabled(true);
						deleteButton.setEnabled(true);
						saveSubtitleButton.setEnabled(true);
						addSubtitleToVideoButton.setEnabled(true);
						importButton.setEnabled(true);
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
		} else if (e.getSource() == saveSubtitleButton) {
			String saveFilename = subtitleFileSelection.getOutputFilename();
			
			if (saveFilename != null) {
				saveSubtitle(saveFilename);
			}
			
		} else if (e.getSource() == leftButton) {
			VideoFilterPanel videoFilterPanel = VideoFilterPanel.getInstance();
			CardLayout card = (CardLayout)videoFilterPanel.getLayout();
			
			card.show(videoFilterPanel, videoFilterPanel.FADEFILTERSTRING);
		} else if (e.getSource() == addSubtitleToVideoButton) {
			if (mediaPlayer.isPlayable()) {
				executeAddSubtitle();
			} else {
				JOptionPane.showMessageDialog(null, "Media must be playing before trying to add subtitles to a video file");
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
	 * 
	 */
	
	private void executeAddSubtitle() {
		int videoLength = (int)(mediaPlayer.getLength() / 1000);

		ProgressMonitor monitor = new ProgressMonitor(null, "Adding subtitles has started", "", 0, videoLength);
		
		String inputFilename = subtitleFileSelection.getInputFilename();
		
		if (inputFilename != null) {
		
			String outputFilename = subtitleFileSelection.getOutputVideoFilename();
			
			if (outputFilename != null) {
				SubtitleWorker worker = new SubtitleWorker(
						VamixProcesses.getFilename(mediaPlayer.mrl()),
						inputFilename,
						outputFilename,
						monitor
						);
				
				worker.execute();
			}
			
		}
	}
	
	private void addData(String inputFilename) {
		model.setNumRows(0);
		try {
			InputStream in = new FileInputStream(inputFilename);
			BufferedReader buffer = new BufferedReader(new InputStreamReader(in));

			String line = "";
			String firstTime = "", secondTime = "";
			String[] split;
			Matcher m;

			while ((line = buffer.readLine()) != null) {
				if (isInteger(line)) {
					Object[] data = new Object[3];

					line = buffer.readLine();

					split = line.split("-->");

					m = p.matcher(split[0]);

					if (m.find()) {
						firstTime = m.group();
					}

					m = p.matcher(split[1]);

					if (m.find()) {
						secondTime = m.group();
					}

					data[0] = firstTime;
					data[1] = secondTime;

					line = buffer.readLine();
					data[2] = line;

					model.addRow(data);
				}
			}

			buffer.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
	
	/**
	 * Returns the data values from the starttime, endtime and textarea.
	 * @return Object[]
	 */
	
	private Object[] getSubtitleData() {
		Object[] data = new Object[3];
		
		data[0] = MediaTimer.getFormattedTime((int)startSpinnerHours.getValue(), (int)startSpinnerMinutes.getValue(), (int)startSpinnerSeconds.getValue());
		data[1] = MediaTimer.getFormattedTime((int)endSpinnerHours.getValue(), (int)endSpinnerMinutes.getValue(), (int)endSpinnerSeconds.getValue());
		data[2] = textArea.getText();
		
		return data;
	}
	
	/**
	 * Returns value from a vector.
	 * @param v
	 * @return
	 */
	
	private Object[] getVectorValue(Object o) {
		Object[] values = new Object[3];
		// Vector values [..., ..., ...]
		String[] split = o.toString().split(",");
		
		Matcher m;
		
		String firstTime = "", secondTime = "", text = "";
		
		m = p.matcher(split[0]);
		
		if (m.find()) {
			firstTime = m.group();
		}
		
		m = p.matcher(split[1]);
		
		if (m.find()) {
			secondTime = m.group();
		}
		
		text = split[2].substring(1, split[2].length() - 1);
		
		values[0] = firstTime;
		values[1] = secondTime;
		values[2] = text;
		
		return values;
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
	 * Determines if the line being read is an integer.
	 * @param s
	 * @return
	 */

	private boolean isInteger(String s) {
		try {
			Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	/**
	 * Determines if data is valid to add to table.
	 * @return valid data
	 */
	
	private boolean verifyData() {
		String startTime = MediaTimer.getFormattedTime((int)startSpinnerHours.getValue(), (int)startSpinnerMinutes.getValue(), (int)startSpinnerSeconds.getValue());
		String endTime = MediaTimer.getFormattedTime((int)endSpinnerHours.getValue(), (int)endSpinnerMinutes.getValue(), (int)endSpinnerSeconds.getValue());
		int difference = MediaTimer.getSeconds(endTime) - MediaTimer.getSeconds(startTime);
		
		if (difference > 0) {
			return true;
		}
		
		JOptionPane.showMessageDialog(null, "Please make sure that the end time is later than the start time.");
		
		return false;
	}
	
	private void saveSubtitle(String filename) {
		File file = new File(filename);
		
		if (file.exists()) {
			file.delete();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
			int counter = 1;
			
			for (Object element : model.getDataVector()) {
				Object[] values = getVectorValue(element);
				
				writer.append(counter + "\n");
				counter++;
				
				writer.append(values[0] + ",000" + " --> " + values[1] + ",000\n");
				writer.append(values[2] + "\n\n");
				
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}
