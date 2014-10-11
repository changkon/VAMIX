package panel;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * 
 * @author changkon
 *
 */

public class AudioSecondPagePanel extends JPanel {
	private static AudioSecondPagePanel theInstance = null;
	
	private EmbeddedMediaPlayer mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
	private TitledBorder title;
	
	public static AudioSecondPagePanel getInstance() {
		if (theInstance == null) {
			theInstance = new AudioSecondPagePanel();
		}
		
		return theInstance;
	}
	
	private AudioSecondPagePanel() {
		title = BorderFactory.createTitledBorder("Audio");
		setBorder(title);
	}
}
