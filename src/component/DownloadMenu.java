package component;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;

public class DownloadMenu extends JPanel implements ActionListener {
	private static DownloadMenu theInstance = null;
	
	private JPanel downloadPanel = new JPanel();
	private JPanel downloadTablePanel = new JPanel();
	
	private JLabel message = new JLabel();
	private JLabel downloadTableMessage = new JLabel("Multiple downloads possible!");
	
	private JTextField urlTextField = new JTextField();
	private JButton downloadButton = new JButton();
	private JButton cancelButton = new JButton();
	
	// Override cell editable.
	private DefaultTableModel model = new DefaultTableModel() {

		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	
	private JTable table = new JTable(model);
	private JScrollPane scrollPane = new JScrollPane(table);
	
	private ConcurrentHashMap<String, Download> downloadList = new ConcurrentHashMap<String, Download>();
	
	private String currentDir = System.getProperty("user.dir");
	
	// Singleton design pattern. I only want one downloadMenu panel at a time.
	public static DownloadMenu getInstance() {
		if (theInstance == null) {
			theInstance = new DownloadMenu();
		}
		return theInstance;
	}

	private DownloadMenu() {
		setLayout(new MigLayout());
		downloadPanel.setLayout(new MigLayout());
		downloadTablePanel.setLayout(new MigLayout());
		
		message.setText("Enter URL of mp3 to download:");
		urlTextField.setColumns(30);
		downloadButton.setText("Download");
		cancelButton.setText("Cancel");
		
		downloadPanel.add(message);
		downloadPanel.add(urlTextField);
		downloadPanel.add(downloadButton);
		downloadPanel.add(cancelButton);
		
		downloadTablePanel.add(downloadTableMessage, "wrap");
		downloadTablePanel.add(scrollPane, "span, push, grow");
		table.setFillsViewportHeight(true);
		table.setCellSelectionEnabled(false);
		
		model.addColumn("Filename");
		model.addColumn("Progress");
		model.addColumn("Time Remaining");
		
		table.getColumnModel().getColumn(1).setCellRenderer(new ProgressRenderer());
		table.setRowSelectionAllowed(true);
		
		add(downloadTablePanel, "span, grow, wrap");
		add(downloadPanel, "wrap");
		
		downloadButton.addActionListener(this);
		cancelButton.addActionListener(this);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Checks which button is pressed.
		if (e.getSource() == downloadButton) {
			String url = urlTextField.getText();
			String filename = Basename.getBasename(url);
			String path = currentDir + "/" + filename;
			Path pathName = Paths.get(path);

			// Confirmation that download is from open source website.
			int openSourceSelection = JOptionPane.showConfirmDialog(null, "Please confirm that " + filename + " is open source", "Are you sure?",
					JOptionPane.YES_NO_CANCEL_OPTION);

			// Checks the response to above question. Depending on response, different messages will show up.
			switch (openSourceSelection) {
				case JOptionPane.YES_OPTION:
					if (Files.exists(pathName)) {
						Object[] choices = {"Continue", "Overwrite", "Cancel"};
						int operationSelection = JOptionPane.showOptionDialog(
								null,
								"File already exists. Would you like to continue, overwrite or cancel download?",
								"Operation Query",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE,
								null,
								choices,
								choices[0]);

						if (operationSelection == JOptionPane.YES_OPTION) {
							executeDownload(url, filename);
						} else if (operationSelection == JOptionPane.NO_OPTION) {
							try {
								Files.delete(pathName);
								executeDownload(url, filename);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}

					} else {
						executeDownload(url, filename);
					}
					break;
				case JOptionPane.NO_OPTION:
					JOptionPane.showMessageDialog(null, "Download has been aborted.");
					break;
				case JOptionPane.CANCEL_OPTION:
					break;
			}
		} else if (e.getSource() == cancelButton) {
			int rowSelected = table.getSelectedRow();
			if (rowSelected != -1) {
				notifyProcesses((String)table.getValueAt(rowSelected, 0));
			}
		}
	}

	private void executeDownload(String url, String filename) {
		model.addRow(new Object[] {filename, 0, ""});
		Download download = new Download(url, filename);
		download.execute();
	}
	
	private void notifyProcesses(String filename) {
		// Cancels the appropriate Download instance. At the moment, I have only been able to create one download instance.
		Download download = downloadList.get(filename);
		download.cancel(true);
	}
	
	/*
	 * I used a private inner class because I believed that classes in Download uses a lot of components from DownloadMenu hence it is easier to
	 * have it as a inner class where inner classes can access the top level class.
	 */
	
	private class Download extends SwingWorker<Void, Integer[]> {
		private String url;
		private String filename;
		private int exitStatus;
		private RowListener rowListener;
		
		private Download(String url, String filename) {
			this.url = url;
			this.filename = filename;
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
					
					// Determines exit status and logs history if process completed successfully.
					if (exitStatus == 0) {
						Log log = Log.getInstance();
						log.appendLogHistory("DOWNLOAD");
					}
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
}
