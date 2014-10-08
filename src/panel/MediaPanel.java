package panel;

import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import listener.MediaPlayerListener;
import model.TimeBoundedRangeModel;
import net.miginfocom.swing.MigLayout;
import operation.VamixProcesses;
import res.MediaIcon;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.SkipWorker;

import component.MediaType;
import component.Playback;

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
