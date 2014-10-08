package panel;

import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import frame.FullScreenMediaPlayer;
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
		
		mediaPlayerComponent.setPreferredSize(new Dimension(800, 450)); // 16:9 ratio
		
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
