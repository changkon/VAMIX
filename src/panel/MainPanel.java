package panel;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	private static MainPanel theInstance = null;
	
	private JPanel mediaPanel, audioFilterPanel, videoFilterPanel;
	
	public static MainPanel getInstance() {
		if (theInstance == null) {
			theInstance = new MainPanel();
		}
		return theInstance;
	}
	
	private MainPanel() {
		setLayout(new MigLayout());
		
		mediaPanel = MediaPanel.getInstance();
		audioFilterPanel = AudioFilterPanel.getInstance();
		videoFilterPanel = VideoFilterPanel.getInstance();
		
		add(mediaPanel, "push, grow");
		add(videoFilterPanel, "span 1 2, width 550px, pushy, growy, wrap");
		add(audioFilterPanel, "pushx, growx, height 354px");
		
	}
}
