package worker;

import java.util.List;

import javax.swing.SwingWorker;

import operation.MediaTimer;
import panel.PlaybackPanel;
import setting.MediaSetting;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import component.Playback;

/**
 * 
 * Skips the media player some time depending on the playback mode it is given. </br>
 * 
 * Also check {@link component.Playback }
 *
 */

public class SkipWorker extends SwingWorker<Void, Integer> {
	private Playback mode;
	private EmbeddedMediaPlayer mediaPlayer;
	private PlaybackPanel playbackPanel;
	private MediaSetting mediaSetting;
	
	public SkipWorker(Playback mode, EmbeddedMediaPlayer mediaPlayer, PlaybackPanel playbackPanel) {
		this.mode = mode;
		this.mediaPlayer = mediaPlayer;
		this.playbackPanel = playbackPanel;
		mediaSetting = MediaSetting.getInstance();
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		while(!isCancelled()) {
			switch(mode) {
				case FASTFORWARD:
					mediaPlayer.skip(mediaSetting.getSkipTime());
					break;
				case REWIND:
					mediaPlayer.skip(-mediaSetting.getSkipTime());
					break;
				default:
					break;
			}
			publish((int)mediaPlayer.getTime());
			Thread.sleep(725); // arbitrary value. tested and thought this value is good.
		}
		return null;
	}

	@Override
	protected void process(List<Integer> chunks){
		for (Integer i: chunks){
			playbackPanel.timeSlider.setValue(i);
			playbackPanel.startTimeLabel.setText(MediaTimer.getFormattedTime(i.longValue()));
		}
	}
}