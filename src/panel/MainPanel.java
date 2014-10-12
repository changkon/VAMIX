package panel;

import java.awt.CardLayout;
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
	
	private JPanel audioPanels = new JPanel(new CardLayout());
	private JPanel videoPanels = new JPanel(new CardLayout());
	
	// AudioPanels
	private AudioFirstPagePanel audioFirstPagePanel = AudioFirstPagePanel.getInstance();
	public final String audioFirstPageString = "First Page";
	private AudioSecondPagePanel audioSecondPagePanel = AudioSecondPagePanel.getInstance();
	public final String audioSecondPageString = "Second Page";
	
	private FilterPanel filterPanel = FilterPanel.getInstance();
	
	public static MainPanel getInstance() {
		if (theInstance == null) {
			theInstance = new MainPanel();
		}
		return theInstance;
	}
	
	private MainPanel() {
		setLayout(new MigLayout());
		
		setVideoPanels();
		setAudioPanels();
		
		add(mediaPanel, "push, grow");
		add(videoPanels, "span 1 2, pushy, growy, wrap");
		add(audioPanels, "pushx, growx");
		
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
	
	private void setVideoPanels() {
		videoPanels.add(filterPanel);
	}
	
	private void setAudioPanels() {
		audioPanels.add(audioFirstPagePanel, audioFirstPageString);
		audioPanels.add(audioSecondPagePanel, audioSecondPageString);
	}
	
	public CardLayout getAudioCard() {
		return (CardLayout)audioPanels.getLayout();
	}
	
	public JPanel getAudioPanel() {
		return audioPanels;
	}
	
	public CardLayout getVideoCard() {
		return (CardLayout)videoPanels.getLayout();
	}

	public JPanel getVideoPanel() {
		return videoPanels;
	}
	
}
