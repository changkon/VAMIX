package panel;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * Singleton design pattern. Responsible for displaying media player and the playbackPanel.
 *
 */

@SuppressWarnings("serial")
public class MediaPanel extends JPanel {
	private static MediaPanel theInstance = null;
	private MediaPlayerComponentPanel mediaPlayerComponentPanel;
	private PlaybackPanel playbackPanel;
	
	public static MediaPanel getInstance() {
		if (theInstance == null) {
			theInstance = new MediaPanel();
		}
		return theInstance;
	}
	
	private MediaPanel() {
		setLayout(new MigLayout());
		
		mediaPlayerComponentPanel = new MediaPlayerComponentPanel();
		playbackPanel = new PlaybackPanel(mediaPlayerComponentPanel.getMediaPlayer());

		add(mediaPlayerComponentPanel, "push, grow, wrap");
		add(playbackPanel, "pushx, growx");
	}

	public MediaPlayerComponentPanel getMediaPlayerComponentPanel() {
		return mediaPlayerComponentPanel;
	}
	
	public PlaybackPanel getPlaybackPanel() {
		return playbackPanel;
	}
}
