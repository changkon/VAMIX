package panel;

import java.awt.CardLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class AudioFilterPanel extends JPanel {
	private static AudioFilterPanel theInstance = null;
	
	private JPanel audioFirstPagePanel, audioSecondPagePanel;
	
	public final String AUDIOFIRSTPAGESTRING = "First Page";
	public final String AUDIOSECONDPAGESTRING = "Second Page";
	
	public static AudioFilterPanel getInstance() {
		if (theInstance == null) {
			theInstance = new AudioFilterPanel();
		}
		return theInstance;
	}
	
	private AudioFilterPanel() {
		setLayout(new CardLayout());
		
		audioFirstPagePanel = AudioFirstPagePanel.getInstance();
		audioSecondPagePanel = AudioSecondPagePanel.getInstance();
		
		add(audioFirstPagePanel, AUDIOFIRSTPAGESTRING);
		add(audioSecondPagePanel, AUDIOSECONDPAGESTRING);
	}
}
