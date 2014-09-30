package component;

import java.awt.Component;

import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class ProgressRenderer extends JProgressBar implements TableCellRenderer {

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, 
			int column) {
		
		int progress = (int)value;
		
		setValue(progress);
		setString(progress + "%");
		setStringPainted(true);

		return this;
	}

}
