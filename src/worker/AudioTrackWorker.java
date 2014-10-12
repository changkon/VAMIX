package worker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

/**
 * Adds another audio track to a video file.
 * @author chang
 *
 */

public class AudioTrackWorker extends SwingWorker<Void, Integer[]> {
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
		
		Pattern p = Pattern.compile("\\d*kB");
		Matcher m;
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
			
			m = p.matcher(line);
			
			if (m.find()) {
				int endLength = m.group().length() - 2;
				Integer size = Integer.parseInt(m.group().substring(0, endLength));
				Integer[] values = {size, progressValue};
				
				publish(values);
			}	
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
	protected void process(List<Integer[]> chunks) {
		if (!isDone()) {
			for (Integer[] i : chunks) {
				monitor.setNote("In progress. Completed " + i[0] + "kb");
				monitor.setProgress(i[1]);
			}
		}
	}

	@Override
	protected void done() {
		try {
			monitor.close();
			get();
			JOptionPane.showMessageDialog(null, "Adding audio track complete");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Adding audio track cancelled");
		}
	}
	
}
