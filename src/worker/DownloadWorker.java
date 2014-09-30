package worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import panel.DownloadPanel;
import listener.RowListener;

public class DownloadWorker extends SwingWorker<Void, Integer[]> {
	private String url;
	private String filename;
	private int exitStatus;
	private RowListener rowListener;
	private DefaultTableModel model;
	private ConcurrentHashMap<String, DownloadWorker> downloadList;
	
	public DownloadWorker(String url, String filename) {
		this.url = url;
		this.filename = filename;
		model = DownloadPanel.getInstance().getModel();
		downloadList = DownloadPanel.getInstance().getDownloadList();
		rowListener = new RowListener(filename, model);
		model.addTableModelListener(rowListener);
	}

	@Override
	protected Void doInBackground() {

		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "wget -c " + url);
			// Note: wget normally outputs log messages onto stderr.
			builder.redirectErrorStream(true);
			Process process = builder.start();
			
			// Add this to hash map with filename as key and download as value.
			downloadList.put(filename, this);
			
			InputStream stdout = process.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
			String line = null;

			// Regex patterns to extract percentage and time left on screen.
			Pattern percentage = Pattern.compile("\\d?\\d?\\d{1}[%]{1}");
			Pattern timeRemaining = Pattern.compile("\\d+[s]{1}");
			
			Integer[] displayValues = new Integer[2];
			
			Matcher p, tR;
			
			// Reads from buffer and stops either when there is no more input or cancel button has been clicked.
			while((line = buffer.readLine()) != null) {
				if (isCancelled()) {
					process.destroy();
					break;
				}
				
				p = percentage.matcher(line);
				tR = timeRemaining.matcher(line);
				
				// Stores Integer values into Integer array by removing non integer values from the string. Auto boxing.
				if (p.find() && tR.find()) {
					displayValues[0] = Integer.parseInt(p.group().replace("%", ""));
					displayValues[1] = Integer.parseInt(tR.group().replace("s", ""));
					publish(displayValues);
				}
			}
			/*
			 * Execute when download has not been cancelled yet. This is important because we don't want to continue download
			 * when cancel button has been clicked.
			 */
			if (!isCancelled()) {
				exitStatus = process.waitFor();
			}
			
			// Reached end of doInBackground(), remove this value from hash map.
			downloadList.remove(filename);
			
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		return null;
	}

	// Invoked on ED thread.
	@Override
	protected void process(List<Integer[]> chunks) {
		// Update row index if it has been changed.
		// Only update JTable if the process is not done. If it is, then don't update JTable as it will throw an exception.
		if (!isDone()) {
			// Update the jprogressbar and jlabel to show correct information to user.
			for (Integer[] element : chunks) {
				model.setValueAt(element[0], rowListener.getRow(), 1);
				model.setValueAt(element[1] + "s", rowListener.getRow(), 2);
			}
		}
	}
	
	// Invoked on the ED thread after doInBackground() completes execution.
	@Override
	protected void done() {
		try {
			model.removeRow(rowListener.getRow());
			get();
			/*
			 * Done can be called even when the download hasn't finished so determine exit status and display appropriate message.
			 * Most of the messages were copied from wget manual.
			 */
			switch (exitStatus) {
				case 0:
					JOptionPane.showMessageDialog(null, filename + " has completed downloading.");
					break;
				case 1:
					JOptionPane.showMessageDialog(null, "Generic error code.");
					break;
				case 2:
					JOptionPane.showMessageDialog(null, "Parse error—for instance, when parsing command-line options, the ‘.wgetrc’ or ‘.netrc’...");
					break;
				case 3:
					JOptionPane.showMessageDialog(null, "File I/O error.");
					break;
				case 4:
					JOptionPane.showMessageDialog(null, "Network failure.");
					break;
				case 5:
					JOptionPane.showMessageDialog(null, "SSL verification failure.");
					break;
				case 6:
					JOptionPane.showMessageDialog(null, "Username/password authentication failure.");
					break;
				case 7:
					JOptionPane.showMessageDialog(null, "Protocol errors.");
					break;
				case 8:
					JOptionPane.showMessageDialog(null, "Server issued an error response.");
					break;
			}
			
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Process interrupted when trying to download " + filename);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}
}