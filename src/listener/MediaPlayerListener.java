package listener;

import java.lang.reflect.InvocationTargetException;

import javax.swing.SwingUtilities;

import model.TimeBoundedRangeModel;
import operation.MediaTimer;
import panel.PlaybackPanel;
import res.MediaIcon;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

import component.Playback;

/**
 * Updates media frame when media frame state has changed. Because this is not in the EDT, it must call on invokeAndWait
 * to get GUI to be updated thread safe. Updates the GUI components when media is being played, such as button icons and label.
 * Extends MediaPlayerEventAdapter
 */

public class MediaPlayerListener extends MediaPlayerEventAdapter {
	private PlaybackPanel playbackPanel;
	private MediaIcon mediaIcon;
	
	public MediaPlayerListener(PlaybackPanel playbackPanel) {
		this.playbackPanel = playbackPanel;
		mediaIcon = playbackPanel.mediaIcon;
	}
	
	// executed synchronously on the AWT event dispatching thread. Call is blocked until all processing AWT events have been
	// processed.
	
	@Override
	public void mediaParsedChanged(final MediaPlayer mediaPlayer, int newStatus) {
		super.mediaParsedChanged(mediaPlayer, newStatus);

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					playbackPanel.finishTimeLabel.setText(MediaTimer.getFormattedTime(mediaPlayer.getLength()));
					
					/*
					 * When setting the minimum value, it calls its change listeners. As our timeslider forcibly changes
					 * time, we want to disable setting time to zero in case we need to start from a different time.
					 */
					((TimeBoundedRangeModel)playbackPanel.timeSlider.getModel()).setActive(false);
					playbackPanel.timeSlider.setMinimum(0);
					playbackPanel.timeSlider.setMaximum((int)mediaPlayer.getLength()); // only accepts int.
					playbackPanel.startTimeLabel.setText(MediaTimer.getFormattedTime(mediaPlayer.getTime()));
					playbackPanel.muteButton.setIcon(mediaIcon.getIcon(Playback.UNMUTE));
					mediaPlayer.mute(false);
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paused(MediaPlayer mediaPlayer) {
		super.paused(mediaPlayer);

		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					playbackPanel.playButton.setIcon(mediaIcon.getIcon(Playback.PLAY));
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void playing(MediaPlayer mediaPlayer) {

		super.playing(mediaPlayer);
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					playbackPanel.playButton.setIcon(mediaIcon.getIcon(Playback.PAUSE));
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void stopped(MediaPlayer mediaPlayer) {
		super.stopped(mediaPlayer);
		
		// Prepares media to be played when play button is pressed.
		mediaPlayer.prepareMedia(mediaPlayer.mrl());
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					playbackPanel.playButton.setIcon(mediaIcon.getIcon(Playback.PLAY));
					playbackPanel.startTimeLabel.setText(PlaybackPanel.initialTimeDisplay);
					playbackPanel.finishTimeLabel.setText(PlaybackPanel.initialTimeDisplay);
					playbackPanel.timeSlider.setValue(0);
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void finished(MediaPlayer mediaPlayer) {
		super.finished(mediaPlayer);
		
		// Prepares media to be played when play button is pressed.
		mediaPlayer.prepareMedia(mediaPlayer.mrl());
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					playbackPanel.playButton.setIcon(mediaIcon.getIcon(Playback.PLAY));
					playbackPanel.startTimeLabel.setText(PlaybackPanel.initialTimeDisplay);
					playbackPanel.finishTimeLabel.setText(PlaybackPanel.initialTimeDisplay);
					playbackPanel.timeSlider.setValue(0);
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void timeChanged(MediaPlayer mediaPlayer, final long newTime) {
		super.timeChanged(mediaPlayer, newTime);
		
		try {
			SwingUtilities.invokeAndWait(new Runnable() {

				@Override
				public void run() {
					playbackPanel.startTimeLabel.setText(MediaTimer.getFormattedTime(newTime));
					
					// Turn "off" the change listener. So that when it calls statechanged, it does set time because the boolean is set to false.
					((TimeBoundedRangeModel)playbackPanel.timeSlider.getModel()).setActive(false);
					playbackPanel.timeSlider.setValue((int)newTime); // only accepts int.
				}
				
			});
		} catch (InvocationTargetException | InterruptedException e) {
			e.printStackTrace();
		}
	}
}
