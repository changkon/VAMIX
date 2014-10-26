package panel;

import java.awt.Color;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.table.DefaultTableModel;

import component.RowSort;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * Panels used in VideoFilterPanel used very similar components. Added all the common components into this abstract class so less code reuse.
 * @author chang
 *
 */

@SuppressWarnings("serial")
public abstract class SpinnerTableTemplatePanel extends JPanel {
	protected JButton startButton, endButton, addButton, editChangeButton, deleteButton;
	
	protected JSpinner startSpinnerSeconds, startSpinnerMinutes, startSpinnerHours, endSpinnerSeconds, endSpinnerMinutes, endSpinnerHours;
	
	protected JScrollPane tableScroll;
	protected JTable table;
	protected DefaultTableModel model;
	
	protected String[] EDITCHANGE = {"Edit", "Change"};
	
	protected int rowToEdit;
	
	protected EmbeddedMediaPlayer mediaPlayer;
	
	public SpinnerTableTemplatePanel() {
		mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
		
		setButtons();
		setSpinners();
		setTable();
	}
	
	private void setButtons() {
		startButton = new JButton("Start");

		startButton.setForeground(Color.WHITE);
		startButton.setBackground(new Color(59, 89, 182)); // blue

		endButton = new JButton("End");

		endButton.setForeground(Color.WHITE);
		endButton.setBackground(new Color(59, 89, 182)); // blue
		
		addButton = new JButton("Add");
		addButton.setBackground(new Color(219, 219, 219)); // light grey

		editChangeButton = new JButton(EDITCHANGE[0]);
		editChangeButton.setBackground(new Color(219, 219, 219)); // light grey

		deleteButton = new JButton("Delete");
		deleteButton.setBackground(new Color(219, 219, 219)); // light grey
		
	}
	
	private void setSpinners() {
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
	}
	
	private void setTable() {
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
	}
	
	/**
	 * Sets the values for the start JSpinners. Input must be formatted time hh:mm:ss or exception will be thrown.
	 * @param formattedTime
	 */

	protected void setStartTime(String formattedTime) {
		String[] firstTime = formattedTime.split(":");
		startSpinnerHours.setValue(Integer.parseInt(firstTime[0]));
		startSpinnerMinutes.setValue(Integer.parseInt(firstTime[1]));
		startSpinnerSeconds.setValue(Integer.parseInt(firstTime[2]));
	}

	/**
	 * Sets the values for the end JSpinners. Input must be formatted time hh:mm:ss or exception will be thrown.
	 * @param formattedTime
	 */

	protected void setEndTime(String formattedTime) {
		String[] secondTime = formattedTime.split(":");

		endSpinnerHours.setValue(Integer.parseInt(secondTime[0]));
		endSpinnerMinutes.setValue(Integer.parseInt(secondTime[1]));
		endSpinnerSeconds.setValue(Integer.parseInt(secondTime[2]));
	}
	
	/**
	 * Checks if the table needs sorting.
	 * @return is sorting needed
	 */

	protected boolean needSorting() {
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
	protected void sortData() {
		Vector data = model.getDataVector();
		Collections.sort(data, new RowSort());
		model.fireTableDataChanged();
	}
	
}
