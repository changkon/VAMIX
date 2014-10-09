package panel;

import java.awt.Color;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

/**
 * 
 * Singleton design pattern. Responsible for displaying anything media player related, such as the media player component
 * and buttons, jsliders etc.
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
