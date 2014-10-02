package panel;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import net.miginfocom.swing.MigLayout;
import operation.Basename;
import res.MediaIcon;
import worker.DownloadWorker;

import component.Playback;
import component.ProgressRenderer;

@SuppressWarnings("serial")
public class DownloadPanel extends JPanel implements ActionListener {
	private static DownloadPanel theInstance = null;
	
	private JPanel downloadPanel = new JPanel();
	private JPanel downloadTablePanel = new JPanel();
	
	private JLabel message = new JLabel();
	private JLabel downloadTableMessage = new JLabel("Multiple downloads possible!");
	
	private JTextField urlTextField = new JTextField();
	private JButton downloadButton = new JButton(MediaIcon.getIcon(Playback.DOWNLOAD));
	private JButton cancelButton = new JButton(MediaIcon.getIcon(Playback.STOP));
	
	// Override cell editable.
	private DefaultTableModel model = new DefaultTableModel() {
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
	};
	
	private JTable table = new JTable(model);
	private JScrollPane scrollPane = new JScrollPane(table);
	
	private ConcurrentHashMap<String, DownloadWorker> downloadList = new ConcurrentHashMap<String, DownloadWorker>();
	
	private String currentDir = System.getProperty("user.dir");
	
	// Singleton design pattern. I only want one downloadMenu panel at a time.
	public static DownloadPanel getInstance() {
		if (theInstance == null) {
			theInstance = new DownloadPanel();
		}
		return theInstance;
	}

	private DownloadPanel() {
		setLayout(new MigLayout());
		downloadPanel.setLayout(new MigLayout());
		downloadTablePanel.setLayout(new MigLayout());
		
		setDownloadPanel();
		
		setDownloadTablePanel();

		add(downloadTablePanel, "span, push, grow, wrap");
		add(downloadPanel, "span, pushx, growx, wrap");
		
		addListeners();
	}
	
	private void setDownloadPanel() {
		message.setText("Enter URL of mp3 to download:");
		
		downloadButton.setContentAreaFilled(false);
		downloadButton.setBorderPainted(false);
		downloadButton.setFocusPainted(false);
		downloadButton.setToolTipText("Download audio");
		
		cancelButton.setContentAreaFilled(false);
		cancelButton.setBorderPainted(false);
		cancelButton.setFocusPainted(false);
		cancelButton.setToolTipText("Cancel download");

		downloadPanel.add(message);
		downloadPanel.add(urlTextField, "pushx, growx");
		downloadPanel.add(downloadButton);
		downloadPanel.add(cancelButton);
	}
	
	private void setDownloadTablePanel() {
		downloadTablePanel.add(downloadTableMessage, "span, wrap");
		downloadTablePanel.add(scrollPane, "span, push, grow");
		table.setFillsViewportHeight(true);
		table.setCellSelectionEnabled(false);

		model.addColumn("Filename");
		model.addColumn("Progress");
		model.addColumn("Time Remaining");
		
		DefaultTableCellRenderer timeRemainingRenderer = new DefaultTableCellRenderer();
		timeRemainingRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		
		table.getColumnModel().getColumn(1).setCellRenderer(new ProgressRenderer());
		table.getColumnModel().getColumn(2).setCellRenderer(timeRemainingRenderer);
		table.setRowSelectionAllowed(true);
	}
	
	private void addListeners() {
		downloadButton.addActionListener(this);
		cancelButton.addActionListener(this);
		
		downloadButton.getModel().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel)e.getSource();
	            if (model.isRollover()) {
	            	downloadButton.setBorderPainted(true);
	            } else {
	            	downloadButton.setBorderPainted(false);
	            }
			}
			
		});
		
		cancelButton.getModel().addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				ButtonModel model = (ButtonModel)e.getSource();
				if (model.isRollover()) {
					cancelButton.setBorderPainted(true);
				} else {
					cancelButton.setBorderPainted(false);
				}
			}
			
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// Checks which button is pressed.
		if (e.getSource() == downloadButton) {
			String url = urlTextField.getText();
			
			// Check that url is given.
			if (url.equals("")) {
				JOptionPane.showMessageDialog(null, "Please input link");
				return;
			}
			
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
			if (rowSelected == -1) {
				JOptionPane.showMessageDialog(null, "Please select a download to cancel");
			} else {
				notifyProcesses((String)table.getValueAt(rowSelected, 0));
			}
		}
	}

	private void executeDownload(String url, String filename) {
		try {
			if (validURLCheck(url)) {
				model.addRow(new Object[] {filename, 0, ""});
				DownloadWorker download = new DownloadWorker(url, filename);
				download.execute();
			} else {
				JOptionPane.showMessageDialog(null, "Not a valid url");
			}
		} catch (HeadlessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void notifyProcesses(String filename) {
		// Cancels the appropriate Download instance. At the moment, I have only been able to create one download instance.
		DownloadWorker download = downloadList.get(filename);
		download.cancel(true);
	}
	
	/**
	 * Validates URL before downloading. Reference: my 206 a03 partner.
	 * @return
	 * @throws IOException
	 */
	
	private boolean validURLCheck(String url) throws IOException {
		//check if the URL is vlaid
		//does not download only returns the URL informaiton
		ProcessBuilder builder = new ProcessBuilder("/bin/bash","-c","wget --spider -v \'" + url + "\'");
		Process process = builder.start();		
		String stdOutput = null;
		String lastOutput = null;
		
		builder.redirectErrorStream(true);
		
		process = builder.start();
		InputStream out = process.getInputStream();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
		
		stdOutput = stdout.readLine();
		while(stdOutput != null && !stdOutput.equals("") && !(stdOutput.length() == 0)){
			lastOutput = stdOutput;
			stdOutput = stdout.readLine();
		}
		
		//if the last line is "Remote file exists." the file is a valid media file
		if(lastOutput.equals("Remote file exists.")){
			return true;
		} else {
			return false;
		}
	}
	
	public DefaultTableModel getModel() {
		return model;
	}
	
	public ConcurrentHashMap<String, DownloadWorker> getDownloadList() {
		return downloadList;
	}
}
