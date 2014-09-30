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
 * 
 * Extracts audio from video file. Shows progress on progress monitor. Outputs different message dialog depending on how process terminated.
 */

public class ExtractAudioWorker extends SwingWorker<Void, Integer> {
	private String inputFile;
	private String outputFile;
	private String startTime;
	private String lengthTime;
	private ProgressMonitor monitor;
	
	public ExtractAudioWorker(String inputFile, String outputFile, String startTime, String lengthTime, ProgressMonitor monitor) {
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.startTime = startTime;
		this.lengthTime = lengthTime;
		this.monitor = monitor;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		//avconv command for extracting audio
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avconv -i \'" + inputFile + "\' -ss " + startTime + " -t " + lengthTime + " -vn -q:a 1 -y \'" + outputFile + "\'");
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
			JOptionPane.showMessageDialog(null, "Extraction complete");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Extraction was interrupted");
		}
	}
	
}
