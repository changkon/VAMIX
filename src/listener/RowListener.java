package listener;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class RowListener implements TableModelListener {
	private int row;
	private String filename;
	private TableModel model;
	
	public RowListener(String filename, TableModel model) {
		this.filename = filename;
		this.model = model;
		row = getRow(filename);
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		if (e.getType() == TableModelEvent.DELETE) {
			row = getRow(filename);
		}
	}

	private int getRow(String filename) {
		for (int i = 0; i < model.getRowCount(); i++) {
			if (filename.equals((String)model.getValueAt(i, 0))) {
				row = i;
				break;
			}
		}
		return row;
	}
	
	public int getRow() {
		return row;
	}
}
