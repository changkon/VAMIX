package worker;

import java.util.List;

import javax.swing.SwingWorker;

import component.Playback;

import panel.MediaPanel;
import setting.MediaSetting;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * 
 * Skips the media player some time depending on the playback mode it is given. <br />
 * 
 * Also check {@link component.Playback }
 *
 */

public class SkipWorker extends SwingWorker<Void, Integer> {
	private Playback mode;
	
	public SkipWorker(Playback mode) {
		this.mode = mode;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		EmbeddedMediaPlayer mediaPlayer = MediaPanel.getInstance().getMediaPlayer();
		MediaSetting mediaSetting = MediaSetting.getInstance();
		
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
		
		for(Integer i: chunks){
			if(i == 0){
				MediaPanel.getInstance().checkMediaState();
			}else{
				MediaPanel.getInstance().timeSlider.setValue(i);
			}
		}
	}
}