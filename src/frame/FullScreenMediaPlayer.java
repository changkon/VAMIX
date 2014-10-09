package frame;

import java.awt.Canvas;
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

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.Timer;

import operation.MediaTimer;
import operation.VamixProcesses;
import panel.MediaPanel;
import panel.PlaybackPanel;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.FullScreenStrategy;
import component.MediaCountFSM;

@SuppressWarnings("serial")
public class FullScreenMediaPlayer extends JFrame implements ActionListener, ComponentListener {
	private JFrame vamixFrame;

	private PlaybackPanel playbackPanel;

	private JLayeredPane layeredPane;
	
	private EmbeddedMediaPlayer mediaPanelMediaPlayer;
	private EmbeddedMediaPlayer mediaPlayer;

	private Canvas canvas;

	private Timer t;
	private MediaCountFSM currentState = MediaCountFSM.ZERO; // initial state.

	public FullScreenMediaPlayer(JFrame vamixFrame) {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		this.vamixFrame = vamixFrame;

		// Update counter every second.
		t = new Timer(1000, this);

		// Get the media player from mediaPanel
		mediaPanelMediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();

		// Create a fullscreen embedded media player using mediaPlayerFactory and FullScreenFactory.
		canvas = new Canvas();
		setUndecorated(true);

		MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
		FullScreenStrategy fullScreenStrategy = new DefaultFullScreenStrategy(this);
		mediaPlayer = mediaPlayerFactory.newEmbeddedMediaPlayer(fullScreenStrategy);
		mediaPlayer.setVideoSurface(mediaPlayerFactory.newVideoSurface(canvas));

		playbackPanel = new PlaybackPanel(mediaPlayer);

		// We don't want to feature to open media files in fullscreen mode.
		playbackPanel.openButton.setVisible(false);
		
		// Initially make it invisible
		playbackPanel.setVisible(false);

		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice g = ge.getDefaultScreenDevice();

		// Set sizes of panel through absolute values. Determined by screen size of monitor.
		Dimension screenSize = new Dimension(g.getDisplayMode().getWidth(), g.getDisplayMode().getHeight());

		canvas.setBounds(0, 0, screenSize.width, screenSize.height);
		playbackPanel.setBounds(0, g.getDisplayMode().getHeight() - 100, g.getDisplayMode().getWidth(), 100);
		
		// smaller the position number, the higher the component within its depth. The higher number is at the front.
		// use jlayeredpane to get layering effect.
		layeredPane = new JLayeredPane();

		layeredPane.add(canvas, new Integer(0));
		layeredPane.add(playbackPanel, new Integer(1));
		
		add(layeredPane);
		setFullScreen();

		addListeners();
	}

	private void setFullScreen() {		
		if (!mediaPanelMediaPlayer.isPlayable()) {
			JOptionPane.showMessageDialog(null, "Please parse media");
			return;
		}

		if (mediaPanelMediaPlayer.isPlaying()) {
			mediaPanelMediaPlayer.pause();
		}

		// hide the vamixFrame.
		vamixFrame.setVisible(false);

		String mediaPath = VamixProcesses.getFilename(mediaPanelMediaPlayer.mrl());

		// play media file.
		mediaPlayer.setFullScreen(true);
		
		// Play the media with the correct time and volume.
		mediaPlayer.playMedia(mediaPath, ":start-time=" + mediaPanelMediaPlayer.getTime() / 1000);
		playbackPanel.volumeSlider.setValue(mediaPanelMediaPlayer.getVolume());
		
		// If the mediaPanel media player is not playing then pause on the full screen media player.
		if (!mediaPanelMediaPlayer.isPlaying()) {
			mediaPlayer.pause();
		}
		
		System.out.println(MediaPanel.getInstance().getPlaybackPanel().timeSlider.getHeight());
		System.out.println(playbackPanel.timeSlider.getHeight());
	}

	private void exitFullScreen() {
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
		canvas.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					exitFullScreen();
				}
			}

		});

		canvas.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				t.stop();
				t.start();
				currentState = MediaCountFSM.next(MediaCountFSM.RESET);

				playbackPanel.setVisible(true);
			}

		});

		canvas.addKeyListener(new KeyAdapter() {

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

}
