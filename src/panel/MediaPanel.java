package panel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

import component.Playback;

import listener.MediaPlayerListener;
import model.TimeBoundedRangeModel;
import net.miginfocom.swing.MigLayout;
import res.MediaIcon;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.SkipWorker;

/**
 * 
 * Singleton design pattern. Responsible for displaying anything media player related, such as the media player component
 * and buttons, jsliders etc.
 *
 */

@SuppressWarnings("serial")
public class MediaPanel extends JPanel implements ActionListener, ChangeListener{
	private static MediaPanel theInstance = null;
	
	public static final String initialTimeDisplay = "--:--";
	
	private JPanel playbackPanel = new JPanel(new MigLayout());
	private JPanel timePanel = new JPanel(new MigLayout());
	private JPanel buttonPanel = new JPanel(new MigLayout());
	
	private DownloadPanel downloadPanel = new DownloadPanel();
	
	public JButton playButton = new JButton(MediaIcon.getIcon(Playback.PLAY));
	public JButton stopButton = new JButton(MediaIcon.getIcon(Playback.STOP));
	public JButton fastforwardButton = new JButton(MediaIcon.getIcon(Playback.FASTFORWARD));
	public JButton rewindButton = new JButton(MediaIcon.getIcon(Playback.REWIND));
	public JButton muteButton = new JButton(MediaIcon.getIcon(Playback.UNMUTE));
	public JButton maxVolumeButton = new JButton(MediaIcon.getIcon(Playback.MAXVOLUME));
	public JButton openButton = new JButton(MediaIcon.getIcon(Playback.OPEN));
	public JButton downloadButton = new JButton(MediaIcon.getIcon(Playback.DOWNLOAD));
	
	public JLabel startTimeLabel = new JLabel(initialTimeDisplay); // Initial labels
	public JLabel finishTimeLabel = new JLabel(initialTimeDisplay);
	
	public JSlider timeSlider = new JSlider(new TimeBoundedRangeModel());
	
	private final static int minVolume = 0;
	private final static int maxVolume = 200; // The max volume of VLC
	
	public JSlider volumeSlider = new JSlider(JSlider.HORIZONTAL, minVolume, maxVolume, 100); // 100 is arbitrary value.
	
	public EmbeddedMediaPlayerComponent mediaPlayerComponent;
	public EmbeddedMediaPlayer mediaPlayer;
	
	private SkipWorker skipWorker = new SkipWorker(Playback.FASTFORWARD); // arbitrary. value doesn't matter.
	
	public static MediaPanel getInstance() {
		if (theInstance == null) {
			theInstance = new MediaPanel();
		}
		return theInstance;
	}
	
	private MediaPanel() {
		setLayout(new MigLayout());
		
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
		mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		
		mediaPlayerComponent.setPreferredSize(new Dimension(800, 450)); // 16:9 ratio
	
		setTimePanel();
		setButtonPanel();
		setPlaybackPanel();
		
		add(mediaPlayerComponent, "wrap");
		add(playbackPanel, "pushx, growx");
		
		addListeners();
	}
	
	private void setTimePanel() {
		// Initially set value to 0
		timeSlider.setValue(0);
		
		timeSlider.setToolTipText("Time Bar. Shows elapsed and total time");
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

		buttonPanel.add(openButton, "align right, pushx");
		
		downloadButton.setToolTipText("Download media file");
		downloadButton.setBorderPainted(false);
		downloadButton.setFocusPainted(false);
		downloadButton.setContentAreaFilled(false);

		buttonPanel.add(downloadButton);
	}
	
	private void setPlaybackPanel() {
		playbackPanel.add(timePanel, "north, pushx, growx, wrap 0px");
		playbackPanel.add(buttonPanel, "pushx, growx");
		
		playbackPanel.add(downloadPanel, "south, pushx, growx");
		downloadPanel.setVisible(false);
		
	}
	
	// Adds any listeners onto component.
	private void addListeners() {
		// Add button listeners
		playButton.addActionListener(this);
		stopButton.addActionListener(this);
		rewindButton.addActionListener(this);
		fastforwardButton.addActionListener(this);
		muteButton.addActionListener(this);
		maxVolumeButton.addActionListener(this);
		openButton.addActionListener(this);
		downloadButton.addActionListener(this);
		
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
		
		downloadButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	downloadButton.setBorderPainted(true);
	            } else {
	            	downloadButton.setBorderPainted(false);
	            }
	        }
	    });
		
		// video surface responds when double click.
		mediaPlayerComponent.getVideoSurface().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// Double click.
				if (e.getClickCount() == 2) {
					// Gets what "state" the JFrame is in. State is if the screen is maximised or not etc.
					Component component = SwingUtilities.getWindowAncestor(MediaPanel.getInstance());
					if (component != null && component instanceof JFrame) {
						JFrame jframe = (JFrame)component;
						
						int state = jframe.getExtendedState();
						switch(state) {
							case JFrame.MAXIMIZED_BOTH:
								jframe.setExtendedState(JFrame.NORMAL);
								break;
							default:
								jframe.setExtendedState(JFrame.MAXIMIZED_BOTH);
								break;
						}
					}
				}
			}
		});
		
		// Add media player event listener.
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerListener(this));
		
		
		//http://stackoverflow.com/questions/518471/jslider-question-position-after-leftclick
		//click to change time on the time slider solution by ninesided on Feb 6, '09.
		timeSlider.setUI(new MetalSliderUI(){
			protected void scrollDueToClickInTrack(int direction) {
		        
		        int value = timeSlider.getValue(); 

		        if (timeSlider.getOrientation() == JSlider.HORIZONTAL) {
		            value = this.valueForXPosition(timeSlider.getMousePosition().x);
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
			timeSlider.setValue(0);
		} else if (e.getSource() == fastforwardButton) {
			// time in milliseconds
			fastforwardButton.setSelected(true);
			playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
			mediaPlayer.pause();
			if (mediaPlayer.isPlayable()) {
				switch(skipWorker.getState()) {
					case PENDING:
						skipWorker = new SkipWorker(Playback.FASTFORWARD);
						skipWorker.execute();
						break;
					default: // STARTED, DONE
						skipWorker.cancel(true);
						skipWorker = new SkipWorker(Playback.FASTFORWARD);
						skipWorker.execute();
						break;
				}
			}
			

		} else if (e.getSource() == rewindButton) {
			// time in milliseconds
			rewindButton.setSelected(true);
			playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
			mediaPlayer.pause();
			if (mediaPlayer.isPlayable()) {
				switch(skipWorker.getState()) {
					case PENDING:
						skipWorker = new SkipWorker(Playback.REWIND);
						skipWorker.execute();
						break;
					default:
						skipWorker.cancel(true);
						skipWorker = new SkipWorker(Playback.REWIND);
						skipWorker.execute();
						break;
				}
			}
		} else if (e.getSource() == muteButton) {
			
			// Toggles mute. Cannot update in event listener because mute doesn't toggle event listener.
			if (mediaPlayer.isMute()) {
				muteButton.setIcon(MediaIcon.getIcon(Playback.UNMUTE));
				mediaPlayer.mute(false);
			} else {
				muteButton.setIcon(MediaIcon.getIcon(Playback.MUTE));
				mediaPlayer.mute(true);
			}
			
		} else if (e.getSource() == maxVolumeButton) {
			// 200 is the max volume setting.
			mediaPlayer.setVolume(200);
		} else if (e.getSource() == openButton) {
			playFile();
		} else if (e.getSource() == downloadButton){
			if (downloadPanel.isVisible()) {
				downloadPanel.setVisible(false);
			} else {
				downloadPanel.setVisible(true);
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
	
	/**
	 * Plays the media file selected by user from JFileChooser.
	 */
	
	public void playFile() {
		JFileChooser chooser = new JFileChooser();
		int selection = chooser.showOpenDialog(null);
		boolean media = false;
		
		if (selection == JFileChooser.APPROVE_OPTION) {
			File selectedFile = chooser.getSelectedFile();
			try {
				String cmd = "file -b " + selectedFile.toString();
				ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
				builder.redirectErrorStream(true);
				Process process = null;
				process = builder.start();
				InputStream stdout = process.getInputStream();
				BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));
				String temp = stdoutBuffered.readLine();
				while(temp != null && !(temp.length() == 0) && !(temp.equals(""))){
					if(temp.contains("Audio") || temp.contains("MPEG") || temp.contains("video")){
						media = true;
					}
					temp = stdoutBuffered.readLine();
				}
			}catch(IOException e1) {
				e1.printStackTrace();
			}
			if(media){	
				mediaPlayer.playMedia(selectedFile.getPath());
				FilterPanel.getInstance().checkLog(selectedFile.toString());
			}else{
				JOptionPane.showMessageDialog(null, "Not a valid media file! Please choose another file.");
			}
		}
	}
	
	public EmbeddedMediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	
	public void checkMediaState(){
		if (mediaPlayer.isPlaying()) {
			playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
		} else {
			playButton.setIcon(MediaIcon.getIcon(Playback.PAUSE));
		}
	}	
}
