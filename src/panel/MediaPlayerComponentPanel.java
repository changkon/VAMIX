package panel;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * Panel containing the mediaPlayer and mediaPlayerComponent. It uses BorderLayout so that it is automatically resized.
 * @author changkon
 *
 */

@SuppressWarnings("serial")
public class MediaPlayerComponentPanel extends JPanel {
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	
	public MediaPlayerComponentPanel() {
		setLayout(new BorderLayout());
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		
		add(mediaPlayerComponent, BorderLayout.CENTER);
	}
	
	public EmbeddedMediaPlayerComponent getMediaPlayerComponent() {
		return mediaPlayerComponent;
	}
	
	public EmbeddedMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
}
