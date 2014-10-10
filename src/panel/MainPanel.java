package panel;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import frame.FullScreenMediaPlayerFrame;

@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	private static MainPanel theInstance = null;
	
	private MediaPanel mediaPanel = MediaPanel.getInstance();
	private AudioPanel audioPanel = AudioPanel.getInstance();
	private FilterPanel filterPanel = FilterPanel.getInstance();
	
	public static MainPanel getInstance() {
		if (theInstance == null) {
			theInstance = new MainPanel();
		}
		return theInstance;
	}
	
	private MainPanel() {
		setLayout(new MigLayout());
		
		add(mediaPanel, "push, grow");
		add(filterPanel, "span 1 2, pushy, growy, wrap");
		add(audioPanel);
		
		EmbeddedMediaPlayerComponent mediaPlayerComponent = mediaPanel.getMediaPlayerComponentPanel().getMediaPlayerComponent();
		
		// Go to full screen when double clicked.
		mediaPlayerComponent.getVideoSurface().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				JFrame vamixFrame = (JFrame)SwingUtilities.getWindowAncestor(MediaPanel.getInstance());
				if (e.getClickCount() == 2) {
					FullScreenMediaPlayerFrame fullScreen = new FullScreenMediaPlayerFrame(vamixFrame);
					fullScreen.setFullScreen();
				}
			}
			
		});
	}
}
