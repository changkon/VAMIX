package frame;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import operation.MediaTimer;
import operation.VamixProcesses;
import panel.MediaPanel;
import panel.MediaPlayerComponentPanel;
import panel.PlaybackPanel;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import component.MediaCountFSM;

/**
 * Opens media in <b>fullscreen</b>. It loads the previous playback settings. It has enter fullscreen and exit fullscreen methods. </br> 
 * Also look at the following classes. </br>
 * {@link panel.MediaPlayerComponentPanel} </br>
 * {@link panel.PlaybackPanel} </br>
 * {@link component.MediaCountFSM}
 * @author changkon
 *
 */

@SuppressWarnings("serial")
public class FullScreenMediaPlayerFrame extends JFrame implements ActionListener, ComponentListener {

	private JFrame vamixFrame;

	private PlaybackPanel playbackPanel;

	private JLayeredPane layeredPane;
	
	private EmbeddedMediaPlayer mediaPanelMediaPlayer;
	private EmbeddedMediaPlayer mediaPlayer;

	private EmbeddedMediaPlayerComponent mediaPlayerComponent;

	private Timer t;
	private MediaCountFSM currentState = MediaCountFSM.ZERO; // initial state.

	private GraphicsDevice g;
	
	public FullScreenMediaPlayerFrame(JFrame vamixFrame) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.vamixFrame = vamixFrame;

		// Update counter every second.
		t = new Timer(1000, this);

		// Get the media player from mediaPanel
		mediaPanelMediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
		
		MediaPlayerComponentPanel canvasPanel = new MediaPlayerComponentPanel();
		// Default background.
		mediaPlayerComponent = canvasPanel.getMediaPlayerComponent();
		mediaPlayer = canvasPanel.getMediaPlayer();
		
		playbackPanel = new PlaybackPanel(mediaPlayer);

		// We don't want the feature to open media files in fullscreen mode.
		playbackPanel.openButton.setVisible(false);
		
		// Initially make it invisible
		playbackPanel.setVisible(false);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		g = ge.getDefaultScreenDevice();

		// Set sizes of panel through absolute values. Determined by screen size of monitor.
		Dimension screenSize = new Dimension(g.getDisplayMode().getWidth(), g.getDisplayMode().getHeight());
		Dimension playbackSize = MediaPanel.getInstance().getPlaybackPanel().getSize();
		
		// Set the bounds for the components in fullscreen.
		canvasPanel.setBounds(0, 0, screenSize.width, screenSize.height);
		playbackPanel.setBounds(0, g.getDisplayMode().getHeight() - playbackSize.height, g.getDisplayMode().getWidth(), playbackSize.height);
		
		// smaller the position number, the higher the component within its depth. The higher number is at the front.
		// use jlayeredpane to get layering effect.
		layeredPane = new JLayeredPane();

		layeredPane.add(canvasPanel, new Integer(0));
		layeredPane.add(playbackPanel, new Integer(1));
		
		add(layeredPane);
		
		addListeners();
		
		// Add key binding. 0 means no modfier
		// Got help from http://stackoverflow.com/questions/15422488/java-keybindings
		canvasPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "EXIT");
		canvasPanel.getActionMap().put("EXIT", new FullScreenAction("EXIT"));
		canvasPanel.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "PLAY/PAUSE");
		canvasPanel.getActionMap().put("PLAY/PAUSE", new FullScreenAction("PLAY/PAUSE"));
	}

	public void setFullScreen() {
		if (g.isFullScreenSupported()) {
			if (!mediaPanelMediaPlayer.isPlayable()) {
				JOptionPane.showMessageDialog(null, "Please parse media");
				return;
			}

			if (mediaPanelMediaPlayer.isPlaying()) {
				mediaPanelMediaPlayer.pause();
			}
			
			// hide the vamixFrame.
			vamixFrame.setVisible(false);

			// Remove border.
			setUndecorated(true);
			
			String mediaPath = VamixProcesses.getFilename(mediaPanelMediaPlayer.mrl());
	
			// set fullscreen
			g.setFullScreenWindow(this);
			
			// Play the media with the correct time and volume.
			mediaPlayer.playMedia(mediaPath, ":start-time=" + mediaPanelMediaPlayer.getTime() / 1000);
			
			playbackPanel.volumeSlider.setValue(mediaPanelMediaPlayer.getVolume());
		} else {
			JOptionPane.showMessageDialog(null, "Fullscreen is not supported");
		}
	}

	public void exitFullScreen() {
		vamixFrame.setVisible(true);
		
		// Sets the correct time for the mediaPanel media player when exiting full screen and volume.
		PlaybackPanel mediaPanelPlayback = MediaPanel.getInstance().getPlaybackPanel();
		
		mediaPanelPlayback.startTimeLabel.setText(MediaTimer.getFormattedTime(mediaPlayer.getTime()));
		mediaPanelPlayback.timeSlider.setValue((int)mediaPlayer.getTime());
		mediaPanelPlayback.volumeSlider.setValue(mediaPlayer.getVolume());
		
		if (mediaPlayer.isPlaying()) {
			mediaPanelMediaPlayer.play();
		}
		mediaPlayer.release();
		dispose();
	}

	private void addListeners() {
		mediaPlayerComponent.getVideoSurface().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					exitFullScreen();
				}
			}

		});

		mediaPlayerComponent.getVideoSurface().addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				t.stop();
				t.start();
				currentState = MediaCountFSM.next(MediaCountFSM.RESET);

				playbackPanel.setVisible(true);
			}

		});

		mediaPlayerComponent.getVideoSurface().addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
					exitFullScreen();
				}
			}

		});

		playbackPanel.addComponentListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		currentState = MediaCountFSM.next(currentState);
		if (currentState == MediaCountFSM.THREE) {
			playbackPanel.setVisible(false);
		}
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		t.stop();
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentResized(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
	}

	/**
	 * Class responsible for executing exitFullScreen method. It closes the current JFrame.
	 * @author changkon
	 *
	 */
	
	private class FullScreenAction extends AbstractAction {
		private String cmd;
		
		public FullScreenAction(String cmd) {
			this.cmd = cmd;
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			if (cmd.equals("EXIT")) {
				exitFullScreen();
			} else if (cmd.equals("PLAY/PAUSE")) {
				mediaPlayer.pause();
			}
		}
		
	}
}
