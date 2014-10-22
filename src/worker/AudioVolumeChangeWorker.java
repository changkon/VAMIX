package worker;

import javax.swing.ProgressMonitor;

/**
 * Changes the volume of audio from a media file.
 * @author chang
 *
 */

public class AudioVolumeChangeWorker extends DefaultWorker {
	private String inputFile, outputFile;
	private double volume;
	
	public AudioVolumeChangeWorker(String inputFile, String outputFile, double volume, ProgressMonitor monitor) {
		super(monitor);
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.volume = volume;
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		return "avconv -i \'" + inputFile + "\' -af volume=volume=" + volume + " -c:v copy " + "-strict experimental -q:a 1 -y \'" + outputFile + "\'";
	}

	@Override
	protected String getSuccessMessage() {
		return "Volume change complete";
	}

	@Override
	protected String getCancelMesssage() {
		return "Volume change was interrupted";
	}

}
