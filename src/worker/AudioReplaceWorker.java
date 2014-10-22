package worker;

import javax.swing.ProgressMonitor;

/**
 * Creates a new video and replaces that video's audio with a new audio.
 *
 */

public class AudioReplaceWorker extends DefaultWorker {
	private String videoFileInput;
	private String audioFileInput;
	private String videoFileOutput;
	
	public AudioReplaceWorker(String videoFileInput, String audioFileInput, String videoFileOutput, ProgressMonitor monitor) {
		super(monitor);
		
		this.videoFileInput = videoFileInput;
		this.audioFileInput = audioFileInput;
		this.videoFileOutput = videoFileOutput;
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		return "avconv -i \'" + videoFileInput + "\' -i \'" + audioFileInput + "\' -map 0:v -map 1:a " + "-c:v copy -c:a copy -y \'" + videoFileOutput + "\'";
	}

	@Override
	protected String getSuccessMessage() {
		return "Replacing audio complete";
	}

	@Override
	protected String getCancelMesssage() {
		return "Replacing audio cancelled";
	}

}
