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
 * Template for other swingworkers to subclass. Most avconv commands output progress. The progress is output to a Progress Monitor
 * by setting its value. The progress of the avconv progress is determined by finding the value for time.
 * @author chang
 *
 */

public abstract class DefaultWorker extends SwingWorker<Void, Integer> {
	protected String command, successMessage, cancelMessage;
	protected ProgressMonitor monitor;
	
	public DefaultWorker(ProgressMonitor monitor) {
		this.monitor = monitor;
	}

	@Override
	protected Void doInBackground() throws Exception {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command);
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
			JOptionPane.showMessageDialog(null, successMessage);
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, cancelMessage);
		}
	}
	
	/**
	 * Should be called after constructing the new variables for child classes.
	 */
	
	protected void initialiseVariables() {
		// Sets up the command, successMessage and cancelMessage
		command = getCommand();
		successMessage = getSuccessMessage();
		cancelMessage = getCancelMesssage();
	}
	
	abstract protected String getCommand();
	abstract protected String getSuccessMessage();
	abstract protected String getCancelMesssage();
}
