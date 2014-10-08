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
	
	public MediaPlayerListener(PlaybackPanel playbackPanel) {
		this.playbackPanel = playbackPanel;
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
					playbackPanel.timeSlider.setMinimum(0);
					playbackPanel.timeSlider.setMaximum((int)mediaPlayer.getLength()); // only accepts int.
					playbackPanel.startTimeLabel.setText(MediaTimer.getFormattedTime(mediaPlayer.getTime()));
					playbackPanel.muteButton.setIcon(MediaIcon.getIcon(Playback.UNMUTE));
					mediaPlayer.setTime(0);
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
					playbackPanel.playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
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
					playbackPanel.playButton.setIcon(MediaIcon.getIcon(Playback.PAUSE));
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
					playbackPanel.playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
					playbackPanel.startTimeLabel.setText(playbackPanel.initialTimeDisplay);
					playbackPanel.finishTimeLabel.setText(playbackPanel.initialTimeDisplay);
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
					playbackPanel.playButton.setIcon(MediaIcon.getIcon(Playback.PLAY));
					playbackPanel.startTimeLabel.setText(playbackPanel.initialTimeDisplay);
					playbackPanel.finishTimeLabel.setText(playbackPanel.initialTimeDisplay);
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
