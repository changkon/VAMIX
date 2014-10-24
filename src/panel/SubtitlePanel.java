package panel;

import java.awt.Dimension;
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
import java.util.Comparator;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import operation.FileSelection;
import operation.MediaTimer;

@SuppressWarnings("serial")
public class SubtitlePanel extends JPanel implements ActionListener {
	private static SubtitlePanel theInstance = null;

	private JPanel menuPanel, tablePanel;

	private JButton importButton, startButton, endButton, addButton, editSaveButton, deleteButton, saveButton;

	private JSpinner startSpinnerSeconds, startSpinnerMinutes, startSpinnerHours, endSpinnerSeconds, endSpinnerMinutes, endSpinnerHours;

	private JTextArea textArea;

	private JTable table;

	private JScrollPane textScroll, tableScroll;

	private DefaultTableModel model;

	private final String[] EDITSAVE = {"Edit", "Change"};

	private Pattern p;

	private int rowToEdit;
	
	public static SubtitlePanel getInstance() {
		if (theInstance == null) {
			theInstance = new SubtitlePanel();
		}
		return theInstance;
	}

	private SubtitlePanel() {
		setLayout(new MigLayout());

		setMenuPanel();
		setTablePanel();

		add(menuPanel);
		add(tablePanel);

		addListeners();
	}

	private void setMenuPanel() {
		menuPanel = new JPanel(new MigLayout("debug"));

		importButton = new JButton("Import");
		startButton = new JButton("Start");
		endButton = new JButton("End");
		addButton = new JButton("Add");
		editSaveButton = new JButton(EDITSAVE[0]);
		deleteButton = new JButton("Delete");
		saveButton = new JButton("Save");
		
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
		textScroll.setSize(new Dimension(400, 400));

		p = Pattern.compile("\\d\\d:\\d\\d:\\d\\d");

		menuPanel.add(importButton, "wrap");
		menuPanel.add(startButton, "split 4");
		menuPanel.add(startSpinnerHours);
		menuPanel.add(startSpinnerMinutes, "gap 0");
		menuPanel.add(startSpinnerSeconds, "gap 0");
		menuPanel.add(endButton, "split 4");
		menuPanel.add(endSpinnerHours);
		menuPanel.add(endSpinnerMinutes, "gap 0");
		menuPanel.add(endSpinnerSeconds, "gap 0, wrap");
		menuPanel.add(textScroll, "span, push, grow, wrap");
		menuPanel.add(addButton, "span, split 4, align center");
		menuPanel.add(editSaveButton);
		menuPanel.add(deleteButton);
		menuPanel.add(saveButton);
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
		tableScroll = new JScrollPane(table);

		model.addColumn("Start Time");
		model.addColumn("End Time");
		model.addColumn("Text");

		tablePanel.add(tableScroll);
	}

	private void addListeners() {
		importButton.addActionListener(this);
		startButton.addActionListener(this);
		endButton.addActionListener(this);
		addButton.addActionListener(this);
		editSaveButton.addActionListener(this);
		deleteButton.addActionListener(this);
		saveButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == importButton) {
			String inputFilename = FileSelection.getInputSubtitleFilename();

			if (inputFilename != null) {
				addData(inputFilename);
			}

		} else if (e.getSource() == addButton) {
			Object[] data = getData();

			model.addRow(data);

			if (needSorting()) {
				sortData();
			}

		} else if (e.getSource() == editSaveButton) {
			int selection = table.getSelectedRow();
			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Please select a subtitle to edit");
			} else {
				if (editSaveButton.getText().equals(EDITSAVE[0])) {
					editSaveButton.setText(EDITSAVE[1]);
					rowToEdit = selection;
					table.setRowSelectionAllowed(false);
					addButton.setEnabled(false);
					deleteButton.setEnabled(false);
					saveButton.setEnabled(false);
					importButton.setEnabled(false);
					
					Object[] values = getVectorValue(model.getDataVector().get(selection));
					
					setStartTime(values[0].toString());
					setEndTime(values[1].toString());
					textArea.setText(values[2].toString());

				} else {
					editSaveButton.setText(EDITSAVE[0]);
					Object[] data = getData();
					
					model.setValueAt(data[0], rowToEdit, 0);
					model.setValueAt(data[1], rowToEdit, 1);
					model.setValueAt(data[2], rowToEdit, 2);
					
					table.setRowSelectionAllowed(true);
					addButton.setEnabled(true);
					deleteButton.setEnabled(true);
					saveButton.setEnabled(true);
					importButton.setEnabled(true);
				}
			}
		} else if (e.getSource() == deleteButton) {
			int selection = table.getSelectedRow();
			if (selection == -1) {
				JOptionPane.showMessageDialog(null, "Please select a row to delete");
			} else {
				model.removeRow(selection);
			}
		} else if (e.getSource() == saveButton) {
			String saveFilename = FileSelection.getOutputSubtitleFilename();
			
			if (saveFilename != null) {
				saveSubtitle(saveFilename);
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
	
	private Object[] getData() {
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
	
	/**
	 * Used to sort the data values in the JTable. It accepts strings in the format hh:mm:ss or else it will throw an exception.
	 * @author chang
	 *
	 */

	private class RowSort implements Comparator<Object> {

		@Override
		public int compare(Object o1, Object o2) {
			// Vector objects come in the form [..., ...., ...]
			String[] split1 = o1.toString().split(",");
			String[] split2 = o2.toString().split(",");

			int firstTime = 0, secondTime = 0;
			Matcher m;
			m = p.matcher(split1[0]);
			if (m.find()) {
				firstTime = MediaTimer.getSeconds(m.group());
			}

			m = p.matcher(split2[0]);
			if (m.find()) {
				secondTime = MediaTimer.getSeconds(m.group());
			}

			if (firstTime > secondTime) {
				return 1;
			} else if (secondTime > firstTime) {
				return -1;
			} else {

				m = p.matcher(split1[1]);

				if (m.find()) {
					firstTime = MediaTimer.getSeconds(m.group());
				}

				m = p.matcher(split2[1]);

				if (m.find()) {
					secondTime = MediaTimer.getSeconds(m.group());
				}

				if (firstTime > secondTime) {
					return 1;
				} else if (secondTime > firstTime) {
					return -1;
				}
				// else
				return 0;
			}
		}

	}
}
