package panel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

/**
 * 
 * Singleton design pattern. Responsible for displaying media player and the playbackPanel for the mainPanel.
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
		setLayout(new BorderLayout());
		
		mediaPlayerComponentPanel = new MediaPlayerComponentPanel();
		playbackPanel = new PlaybackPanel(mediaPlayerComponentPanel.getMediaPlayer());

		add(mediaPlayerComponentPanel, BorderLayout.CENTER);
		add(playbackPanel, BorderLayout.SOUTH);
	}

	public MediaPlayerComponentPanel getMediaPlayerComponentPanel() {
		return mediaPlayerComponentPanel;
	}
	
	public PlaybackPanel getPlaybackPanel() {
		return playbackPanel;
	}
}
