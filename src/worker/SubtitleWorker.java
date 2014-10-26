package worker;

import javax.swing.ProgressMonitor;

/**
 * Create a new video with a subtitle stream.
 * @author chang
 *
 */

public class SubtitleWorker extends DefaultWorker {
	private String inputVideo, inputSubtitle, outputVideo;
	
	public SubtitleWorker(String inputVideo, String inputSubtitle, String outputVideo, ProgressMonitor monitor) {
		super(monitor);
		
		this.inputVideo = inputVideo;
		this.inputSubtitle = inputSubtitle;
		this.outputVideo = outputVideo;
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		return "avconv -i \'" + inputVideo + "\' -i \'" + inputSubtitle + "\' -map 0 -map 1 -c:v copy -c:a copy -y \'" + outputVideo + "\'";
	}

	@Override
	protected String getSuccessMessage() {
		return "Subtitle has been successfully added.";
	}

	@Override
	protected String getCancelMesssage() {
		return "";
	}

}
