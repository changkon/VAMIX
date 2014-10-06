package panel;

import javax.swing.JPanel;

public class VideoPlaybackPanel extends JPanel {
	private static VideoPlaybackPanel theInstance = null;
	
	public static VideoPlaybackPanel getInstance() {
		if (theInstance == null) {
			theInstance = new VideoPlaybackPanel();
		}
		return theInstance;
	}
}
