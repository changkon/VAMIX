package worker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

public class AudioTrackWorker extends SwingWorker<Void, Integer> {
	private String videoFileInput;
	private String audioFileInput;
	private String videoFileOutput;
	private ProgressMonitor monitor;
	private int progressValue = 0;
	
	public AudioTrackWorker(String videoFileInput, String audioFileInput, String videoFileOutput, ProgressMonitor monitor) {
		this.videoFileInput = videoFileInput;
		this.audioFileInput = audioFileInput;
		this.videoFileOutput = videoFileOutput;
		this.monitor = monitor;
	}

	@Override
	protected Void doInBackground() throws Exception {
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i \'" + videoFileInput + "\' -i \'" + audioFileInput + 
				"\' -map 0 -map 1:a -c:v copy -c:a copy -y \'" + videoFileOutput + "\'");
		
		builder.redirectErrorStream(true);

		Process process = builder.start();
		
		InputStream stdout = process.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
		
		
		
		return null;
	}
	
}
