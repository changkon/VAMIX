package worker;

import javax.swing.ProgressMonitor;

/**
 * Adds another audio track to a video file.
 * @author chang
 *
 */

public class AudioTrackWorker extends DefaultWorker {
	private String videoFileInput;
	private String audioFileInput;
	private String videoFileOutput;
	
	public AudioTrackWorker(String videoFileInput, String audioFileInput, String videoFileOutput, ProgressMonitor monitor) {
		super(monitor);
		
		this.videoFileInput = videoFileInput;
		this.audioFileInput = audioFileInput;
		this.videoFileOutput = videoFileOutput;
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		return "avconv -i \'" + videoFileInput + "\' -i \'" + audioFileInput + "\' -map 0 -map 1:a -c:v copy -c:a copy -y \'" + videoFileOutput + "\'";
	}

	@Override
	protected String getSuccessMessage() {
		return "Adding audio track complete";
	}

	@Override
	protected String getCancelMesssage() {
		return "Adding audio track cancelled";
	}
	
}
