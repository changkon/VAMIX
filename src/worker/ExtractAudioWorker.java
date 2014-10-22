package worker;

import javax.swing.ProgressMonitor;

/**
 * 
 * Extracts audio from video file. Shows progress on progress monitor. Outputs different message dialog depending on how process terminated.
 */

public class ExtractAudioWorker extends DefaultWorker {
	private String inputFile, outputFile, startTime, lengthTime;
	
	public ExtractAudioWorker(String inputFile, String outputFile, String startTime, String lengthTime, ProgressMonitor monitor) {
		super(monitor);
		
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.startTime = startTime;
		this.lengthTime = lengthTime;
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		return "avconv -i \'" + inputFile + "\' -ss " + startTime + " -t " + lengthTime + " -vn -q:a 1 -y \'" + outputFile + "\'";
	}

	@Override
	protected String getSuccessMessage() {
		return "Extraction complete";
	}

	@Override
	protected String getCancelMesssage() {
		return "Extraction was interrupted";
	}
	
}
