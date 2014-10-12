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
 * Changes the volume of audio from a media file.
 * @author chang
 *
 */

public class AudioVolumeChangeWorker extends SwingWorker<Void, Integer> {
	private String inputFile;
	private String outputFile;
	private double volume;
	private ProgressMonitor monitor;
	
	public AudioVolumeChangeWorker(String inputFile, String outputFile, double volume, ProgressMonitor monitor) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.volume = volume;
		this.monitor = monitor;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i \'" + inputFile + "\' -af volume=volume=" + volume + " -c:v copy " +
		"-strict experimental -q:a 1 -y \'" + outputFile + "\'");

		builder.redirectErrorStream(true);

		Process process = builder.start();
		
		InputStream stdout = process.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
		
		Pattern p = Pattern.compile("\\btime=\\b\\d+.\\d+");
		Matcher m;
		String line = "";
		
		while ((line = buffer.readLine()) != null) {
			if (monitor.isCanceled()) {
				process.destroy();
				break;
			}

			m = p.matcher(line);
			
			if (m.find()) {
				// greedy solution. We know if a string matches pattern, it must start with time=
				publish((int)Double.parseDouble(m.group().substring(5)));
			}
		}
		process.waitFor();

		if (monitor.isCanceled()) {
			this.cancel(true);
		}
		
		return null;
	}

	@Override
	protected void process(List<Integer> chunks) {
		if (!isDone()) {
			for (Integer element : chunks) {
				System.out.println(element);
				String format = String.format("Completed : %2d%%", (int)(((double)element / monitor.getMaximum()) * 100));
				monitor.setNote(format);
				monitor.setProgress(element);
			}
		}
	}
	
	@Override
	protected void done() {
		try {
			monitor.close();
			get();
			JOptionPane.showMessageDialog(null, "Volume change complete");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Volume change was interrupted");
		}
	}

}
