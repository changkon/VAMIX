package panel;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.io.File;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.metal.MetalSliderUI;

import listener.MediaPlayerListener;
import model.TimeBoundedRangeModel;
import net.miginfocom.swing.MigLayout;
import operation.MediaTimer;
import operation.VamixProcesses;
import res.MediaIcon;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.SkipWorker;

import component.FileType;
import component.Playback;

import frame.FullScreenMediaPlayerFrame;

/**
 * The panel responsible to control playback for mediaPlayer. Because it controls some mediaPlayer, it must be linked to some mediaPlayer during constructor. <br/>
 * {@link panel.MediaPlayerComponentPanel}
 * @author changkon
 *
 */

@SuppressWarnings("serial")
public class PlaybackPanel extends JPanel implements ActionListener, ChangeListener {
	private EmbeddedMediaPlayer mediaPlayer;
	
	private SkipWorker skipWorker; // arbitrary. value doesn't matter.
	
	// Gui components
	public static final String initialTimeDisplay = "--:--";
	
	private JPanel playbackPanel = new JPanel(new MigLayout());
	private JPanel timePanel = new JPanel(new MigLayout());
	private JPanel buttonPanel = new JPanel(new MigLayout());
	
	public MediaIcon mediaIcon = new MediaIcon(20, 20);
	
	public JButton playButton = new JButton(mediaIcon.getIcon(Playback.PLAY));
	public JButton stopButton = new JButton(mediaIcon.getIcon(Playback.STOP));
	public JButton fastforwardButton = new JButton(mediaIcon.getIcon(Playback.FASTFORWARD));
	public JButton rewindButton = new JButton(mediaIcon.getIcon(Playback.REWIND));
	public JButton muteButton = new JButton(mediaIcon.getIcon(Playback.UNMUTE));
	public JButton maxVolumeButton = new JButton(mediaIcon.getIcon(Playback.MAXVOLUME));
	public JButton openButton = new JButton(mediaIcon.getIcon(Playback.OPEN));
	public JButton fullScreenButton = new JButton(mediaIcon.getIcon(Playback.FULLSCREEN));
	
	public JLabel startTimeLabel = new JLabel(initialTimeDisplay); // Initial labels
	public JLabel finishTimeLabel = new JLabel(initialTimeDisplay);
	
	public JSlider timeSlider = new JSlider(new TimeBoundedRangeModel());
	
	private final static int minVolume = 0;
	private final static int maxVolume = 200; // The max volume of VLC
	
	public JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, minVolume, maxVolume, 100); // 100 is arbitrary value.
	
	public PlaybackPanel(EmbeddedMediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
		
		// Value doesn't matter but is necessary to initialise because we need to know the STATE of the skipworker.
		skipWorker = new SkipWorker(Playback.FASTFORWARD, mediaPlayer, this);
		
		setLayout(new MigLayout("insets 0px, gap 0px"));
		
		setTimePanel();
		setButtonPanel();
		setPlaybackPanel();
		
		add(playbackPanel, "pushx, growx, wrap");

		addListeners();
	}

	private void setTimePanel() {
		// Initially set value to 0
		timeSlider.setValue(0);

		timePanel.add(startTimeLabel);
		timePanel.add(timeSlider, "pushx, growx");
		timePanel.add(finishTimeLabel);
	}
	
	// Places buttons onto the button panel.
	private void setButtonPanel() {
		//give button the flat UI feel
		
		playButton.setToolTipText("Play/Pause media file");
		playButton.setBorderPainted(false);
		playButton.setFocusPainted(false);
		playButton.setContentAreaFilled(false);

		buttonPanel.add(playButton);
		
		stopButton.setToolTipText("Stop media");
		stopButton.setBorderPainted(false);
		stopButton.setFocusPainted(false);
		stopButton.setContentAreaFilled(false);

		buttonPanel.add(stopButton);
		
		rewindButton.setToolTipText("Rewind some time");
		rewindButton.setBorderPainted(false);
		rewindButton.setFocusPainted(false);
		rewindButton.setContentAreaFilled(false);

		buttonPanel.add(rewindButton, "gapleft 15");
		
		fastforwardButton.setToolTipText("Fast forward some time");
		fastforwardButton.setBorderPainted(false);
		fastforwardButton.setFocusPainted(false);
		fastforwardButton.setContentAreaFilled(false);

		buttonPanel.add(fastforwardButton);
		
		muteButton.setToolTipText("Mute/unmute media file");
		muteButton.setBorderPainted(false);
		muteButton.setFocusPainted(false);
		muteButton.setContentAreaFilled(false);

		buttonPanel.add(muteButton, "gapleft 15");
		
		volumeSlider.setToolTipText("Adjust Volume (100)");
		buttonPanel.add(volumeSlider);
		
		maxVolumeButton.setToolTipText("Set to max volume");
		maxVolumeButton.setBorderPainted(false);
		maxVolumeButton.setFocusPainted(false);
		maxVolumeButton.setContentAreaFilled(false);

		buttonPanel.add(maxVolumeButton);
		
		openButton.setToolTipText("Open media file");
		openButton.setBorderPainted(false);
		openButton.setFocusPainted(false);
		openButton.setContentAreaFilled(false);

		buttonPanel.add(openButton, "split 2, align right, pushx");
		
		fullScreenButton.setToolTipText("Toggle fullscreen");
		fullScreenButton.setBorderPainted(false);
		fullScreenButton.setFocusPainted(false);
		fullScreenButton.setContentAreaFilled(false);
		
		buttonPanel.add(fullScreenButton, "pushx");
	}
	
	private void setPlaybackPanel() {
		playbackPanel.add(timePanel, "pushx, growx, wrap 0px");
		playbackPanel.add(buttonPanel, "pushx, growx, wrap 0px");
	}
	
	private void addListeners() {
		// Add media player event listener.
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerListener(this));
		
		// Add button listeners
		playButton.addActionListener(this);
		stopButton.addActionListener(this);
		rewindButton.addActionListener(this);
		fastforwardButton.addActionListener(this);
		muteButton.addActionListener(this);
		maxVolumeButton.addActionListener(this);
		openButton.addActionListener(this);
		fullScreenButton.addActionListener(this);
		
		// Jslider change listeners
		volumeSlider.addChangeListener(this);
		timeSlider.addChangeListener(this);
		
		// Add change listeners.
		playButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	playButton.setBorderPainted(true);
	            } else {
	            	playButton.setBorderPainted(false);
	            }
	        }
	    });
		
		stopButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	stopButton.setBorderPainted(true);
	            } else {
	            	stopButton.setBorderPainted(false);
	            }
	        }
	    });
		
		rewindButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	rewindButton.setBorderPainted(true);
	            } else {
	            	rewindButton.setBorderPainted(false);
	            }
	        }
	    });
		
		fastforwardButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	fastforwardButton.setBorderPainted(true);
	            } else {
	            	fastforwardButton.setBorderPainted(false);
	            }
	        }
	    });
		
		muteButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	muteButton.setBorderPainted(true);
	            } else {
	            	muteButton.setBorderPainted(false);
	            }
	        }
	    });
		
		maxVolumeButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	maxVolumeButton.setBorderPainted(true);
	            } else {
	            	maxVolumeButton.setBorderPainted(false);
	            }
	        }
	    });
		
		openButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	openButton.setBorderPainted(true);
	            } else {
	            	openButton.setBorderPainted(false);
	            }
	        }
	    });

		fullScreenButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	fullScreenButton.setBorderPainted(true);
	            } else {
	            	fullScreenButton.setBorderPainted(false);
	            }
	        }
	    });
		
		//http://stackoverflow.com/questions/518471/jslider-question-position-after-leftclick
		//click to change time on the time slider solution by ninesided on Feb 6, '09.
		timeSlider.setUI(new MetalSliderUI(){
			protected void scrollDueToClickInTrack(int direction) {

		        int value = timeSlider.getValue(); 
		        if (timeSlider.getOrientation() == JSlider.HORIZONTAL) {
		            value = this.valueForXPosition(timeSlider.getMousePosition().x);
		            startTimeLabel.setText(MediaTimer.getMediaTime(value));
		        } else if (timeSlider.getOrientation() == JSlider.VERTICAL) {
		            value = this.valueForYPosition(timeSlider.getMousePosition().y);
		        }
		        timeSlider.setValue(value);
		        
		    }
		});
		
		volumeSlider.setUI(new MetalSliderUI(){
			protected void scrollDueToClickInTrack(int direction) {
		        
		        int value = volumeSlider.getValue(); 

		        if (volumeSlider.getOrientation() == JSlider.HORIZONTAL) {
		            value = this.valueForXPosition(volumeSlider.getMousePosition().x);
		        } else if (volumeSlider.getOrientation() == JSlider.VERTICAL) {
		            value = this.valueForYPosition(volumeSlider.getMousePosition().y);
		        }
		        volumeSlider.setValue(value);
		    }
			
		});
		
		// When user hovers over time slider, show the time at that position.
		timeSlider.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if (mediaPlayer.isPlayable()) {
					// We know the SliderUI is MetalSliderUI
					MetalSliderUI model = (MetalSliderUI)timeSlider.getUI();
					int valueAtMousePosition = model.valueForXPosition(e.getPoint().x);
					timeSlider.setToolTipText(MediaTimer.getFormattedTime(valueAtMousePosition));
				}
			}
			
		});
		
	}
	
	/**
	 * Plays the media file selected by user from JFileChooser.
	 */
	
	public void playFile() {
		JFileChooser chooser = new JFileChooser();
		int selection = chooser.showOpenDialog(null);
		
		if (selection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			
			if (VamixProcesses.validContentType(FileType.VIDEO, selectedFile.getPath()) || VamixProcesses.validContentType(FileType.AUDIO, selectedFile.getPath())) {
				// Start files from the start.
				mediaPlayer.playMedia(selectedFile.getPath(), ":start-time=0");
			} else {
				JOptionPane.showMessageDialog(null, "Not a valid media file! Please choose another file.");
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == playButton) {			
			// Possible states: PENDING, STARTED, DONE
			
			switch(skipWorker.getState()) {
				case STARTED:
					skipWorker.cancel(true);
					break;
				default: // PENDING, DONE
					break;
			}
			
			// Pause if current media is playing or play if it can be played and is currently paused.
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			} else {
				mediaPlayer.play();
			}
		} else if (e.getSource() == stopButton) {
			mediaPlayer.stop();
		} else if (e.getSource() == fastforwardButton) {
			// time in milliseconds
			fastforwardButton.setSelected(true);
			playButton.setIcon(mediaIcon.getIcon(Playback.PLAY));
			
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
			
			if (mediaPlayer.isPlayable()) {
				switch(skipWorker.getState()) {
					case PENDING:
						skipWorker = new SkipWorker(Playback.FASTFORWARD, mediaPlayer, this);
						skipWorker.execute();
						break;
					default: // STARTED, DONE
						skipWorker.cancel(true);
						skipWorker = new SkipWorker(Playback.FASTFORWARD, mediaPlayer, this);
						skipWorker.execute();
						break;
				}
			}
			

		} else if (e.getSource() == rewindButton) {
			// time in milliseconds
			rewindButton.setSelected(true);
			playButton.setIcon(mediaIcon.getIcon(Playback.PLAY));
			
			if (mediaPlayer.isPlaying()) {
				mediaPlayer.pause();
			}
			
			if (mediaPlayer.isPlayable()) {
				switch(skipWorker.getState()) {
					case PENDING:
						skipWorker = new SkipWorker(Playback.REWIND, mediaPlayer, this);
						skipWorker.execute();
						break;
					default:
						skipWorker.cancel(true);
						skipWorker = new SkipWorker(Playback.REWIND, mediaPlayer, this);
						skipWorker.execute();
						break;
				}
			}
		} else if (e.getSource() == muteButton) {
			
			// Toggles mute. Cannot update in event listener because mute doesn't toggle event listener.
			if (mediaPlayer.isMute()) {
				muteButton.setIcon(mediaIcon.getIcon(Playback.UNMUTE));
				mediaPlayer.mute(false);
			} else {
				muteButton.setIcon(mediaIcon.getIcon(Playback.MUTE));
				mediaPlayer.mute(true);
			}
			
		} else if (e.getSource() == maxVolumeButton) {
			// 200 is the max volume setting.
			volumeSlider.setValue(200);
		} else if (e.getSource() == openButton) {
			playFile();
		} else if (e.getSource() == fullScreenButton) {
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			GraphicsDevice g = ge.getDefaultScreenDevice();
			JFrame frame = (JFrame)SwingUtilities.getWindowAncestor(this);
			
			if (g.getFullScreenWindow() == null) {
				FullScreenMediaPlayerFrame fullScreen = new FullScreenMediaPlayerFrame(frame);
				fullScreen.setFullScreen();
			} else {
				FullScreenMediaPlayerFrame fullScreen = (FullScreenMediaPlayerFrame)frame;
				fullScreen.exitFullScreen();
			}	
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {		
		JSlider source = (JSlider)e.getSource();
		if (e.getSource() == volumeSlider) {
			if(!source.getValueIsAdjusting()){
				int volumeTemp = source.getValue();
				mediaPlayer.setVolume(volumeTemp);
				volumeSlider.setToolTipText("Adjust Volume " + "(" + volumeTemp + ")");
			}
		} else if (e.getSource() == timeSlider  && ((TimeBoundedRangeModel)timeSlider.getModel()).getActive()) {
			int time = source.getValue();
			mediaPlayer.setTime(time);
		}
	}
}
