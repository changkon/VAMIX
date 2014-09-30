package worker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

/**
 * Creates a new video and replaces that video's audio with a new audio.
 *
 */

public class AudioReplaceWorker extends SwingWorker<Void, Integer> {
	private String videoFileInput;
	private String audioFileInput;
	private String videoFileOutput;
	private ProgressMonitor monitor;
	private int progressValue = 0;
	
	public AudioReplaceWorker(String videoFileInput, String audioFileInput, String videoFileOutput, ProgressMonitor monitor) {
		this.videoFileInput = videoFileInput;
		this.audioFileInput = audioFileInput;
		this.videoFileOutput = videoFileOutput;
		this.monitor = monitor;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		//replace avconv command 
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i \'" + videoFileInput + "\' -i \'" + audioFileInput + "\' -map 0:v -map 1:a " +
												"-c:v copy -c:a copy -y \'" + videoFileOutput + "\'");
		builder.redirectErrorStream(true);

		Process process = builder.start();
		
		InputStream stdout = process.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
		
		@SuppressWarnings("unused")
		String line = "";
		
		//run the process until the monitor is cancelled
		
		while((line = buffer.readLine()) != null) {
			if(monitor.isCanceled()) {
				process.destroy();
				break;
			}
			if (progressValue == 100) {
				progressValue = 0;
			}
			publish(progressValue);
			progressValue += 10;
		}
		
		process.waitFor();
		
		//if monitor is cancelled, cancel this swingworker
		if (monitor.isCanceled()) {
			this.cancel(true);
		}
		
		return null;
	}
	
	@Override
	protected void process(List<Integer> chunks) {
		if (!isDone()) {
			for (Integer i : chunks) {
				monitor.setProgress(i);
			}
		}
	}

	@Override
	protected void done() {
		try {
			monitor.close();
			get();
			JOptionPane.showMessageDialog(null, "Replacing audio complete");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Replacing audio cancelled");
		}
	}

}
