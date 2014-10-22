package worker;

import javax.swing.ProgressMonitor;

/**
 * Overlays video with existing audio and another video. Outputs a new video.
 */

public class OverlayWorker extends DefaultWorker {
	private String videoFileInput, audioFileInput, videoFileOutput;
	
	public OverlayWorker(String videoFileInput, String audioFileInput, String videoFileOutput, ProgressMonitor monitor) {
		super(monitor);
		
		this.videoFileInput = videoFileInput;
		this.audioFileInput = audioFileInput;
		this.videoFileOutput = videoFileOutput;
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		return "avconv -i \'" + videoFileInput + "\' -i \"" + audioFileInput + "\" -filter_complex" + 
				" \"[0:a][1:a]amix[out]\" -map \"[out]\" -map 0:v -c:v copy -strict experimental -y \'" + videoFileOutput + "\'";
	}

	@Override
	protected String getSuccessMessage() {
		return "Overlaying audio complete";
	}

	@Override
	protected String getCancelMesssage() {
		return "Overlaying audio cancelled";
	}
}
