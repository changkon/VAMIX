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
 * Overlays video with existing audio and another video. Outputs a new video.
 */

public class OverlayWorker extends SwingWorker<Void, Integer[]> {
	private String videoFileInput;
	private String audioFileInput;
	private String videoFileOutput;
	private ProgressMonitor monitor;
	private int progressValue = 0;
	
	public OverlayWorker(String videoFileInput, String audioFileInput, String videoFileOutput, ProgressMonitor monitor) {
		this.videoFileInput = videoFileInput;
		this.audioFileInput = audioFileInput;
		this.videoFileOutput = videoFileOutput;
		this.monitor = monitor;
	}

	@Override
	protected Void doInBackground() throws Exception {

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i \'" + videoFileInput + "\' -i \"" + audioFileInput + "\" -filter_complex" +
				" \"[0:a][1:a]amix[out]\" -map \"[out]\" -map 0:v -c:v copy -strict experimental -y \'" + videoFileOutput + "\'");
		builder.redirectErrorStream(true);
		
		Process process = builder.start();
		
		InputStream stdout = process.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
		
		Pattern p = Pattern.compile("\\d*kB");
		Matcher m;
		String line = "";
		
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
		
		if (monitor.isCanceled()) {
			this.cancel(true);
		}
		
		process.waitFor();

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
			JOptionPane.showMessageDialog(null, "Overlaying audio complete");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Overlaying audio cancelled");
		}
		
	}
}
