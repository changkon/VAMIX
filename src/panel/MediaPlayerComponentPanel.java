package panel;

import java.awt.Dimension;

import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

@SuppressWarnings("serial")
public class MediaPlayerComponentPanel extends JPanel {
	private EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private EmbeddedMediaPlayer mediaPlayer;
	
	public MediaPlayerComponentPanel() {
		setLayout(new MigLayout());
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		
		add(mediaPlayerComponent, "push, grow");
	}
	
	public void resizeMediaPlayerComponent(Dimension d) {
		mediaPlayerComponent.setPreferredSize(d);
	}
	
	public EmbeddedMediaPlayerComponent getMediaPlayerComponent() {
		return mediaPlayerComponent;
	}
	
	public EmbeddedMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
}
