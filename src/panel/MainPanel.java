package panel;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	private static MainPanel theInstance = null;
	
	private MediaPanel mediaPanel = MediaPanel.getInstance();
	private AudioPanel audioPanel = AudioPanel.getInstance();
	private FilterPanel filterPanel = FilterPanel.getInstance();
	
	public static MainPanel getInstance() {
		if (theInstance == null) {
			theInstance = new MainPanel();
		}
		return theInstance;
	}
	
	private MainPanel() {
		setLayout(new MigLayout());
		
		add(mediaPanel);
		add(filterPanel, "span 1 2, pushy, growy, wrap");
		add(audioPanel);
	}
}
