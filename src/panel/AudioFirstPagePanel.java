package panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;
import operation.MediaTimer;
import operation.VamixProcesses;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.AudioReplaceWorker;
import worker.ExtractAudioWorker;
import worker.OverlayWorker;

import component.MediaType;

/**
 * First page of audio panel. Contains extraction, replace and overlay features.
 * @author changkon
 *
 */

@SuppressWarnings("serial")
public class AudioFirstPagePanel extends JPanel implements ActionListener {
	private static AudioFirstPagePanel theInstance = null;
	
	private EmbeddedMediaPlayer mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
	private TitledBorder title;

	private JPanel audioExtractionPanel = new JPanel(new MigLayout());
	private JPanel audioReplacePanel = new JPanel(new MigLayout());
	private JPanel audioOverlayPanel = new JPanel(new MigLayout());

	private JLabel extractionLabel = new JLabel("Extraction");

	private JLabel timeLabel = new JLabel("Please input times in hh:mm:ss");
	private JLabel startTimeLabel = new JLabel("Start Time:");
	private JTextField startTimeInput = new JTextField(10);
	private JLabel lengthLabel = new JLabel("Length Time:");
	private JTextField lengthInput = new JTextField(10);
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private JButton extractButton = new JButton("Extract");

	private JButton extractFullButton = new JButton("Extract entire Video");

	private JLabel replaceAudioLabel = new JLabel("Replace Audio");

	private JButton selectAudioReplaceFileButton = new JButton("Choose File");
	private JTextField selectedAudioReplaceFileTextField = new JTextField();
	private JButton audioReplaceButton = new JButton("Replace");

	private JLabel audioOverlayLabel = new JLabel("Overlay audio");

	private JButton selectAudioOverlayFileButton = new JButton("Choose File");
	private JTextField selectedAudioOverlayFileTextField = new JTextField();
	private JButton audioOverlayButton = new JButton("Overlay");

	public static AudioFirstPagePanel getInstance() {
		if (theInstance == null) {
			theInstance = new AudioFirstPagePanel();
		}
		return theInstance;
	}
	
	private AudioFirstPagePanel() {
		setLayout(new MigLayout());

		title = BorderFactory.createTitledBorder("Audio");
		setBorder(title);

		setAudioExtractionPanel();
		setAudioReplacePanel();
		setAudioOverlayPanel();

		addListeners();

		add(audioExtractionPanel, "wrap, pushx, growx");
		add(audioReplacePanel, "wrap, pushx, growx");
		add(audioOverlayPanel, "pushx, growx");
	}

	//initialise panel for extraction and its layout
	private void setAudioExtractionPanel() {
		Font font = extractionLabel.getFont().deriveFont(Font.ITALIC + Font.BOLD, 16f); // Default is 12.

		JLabel orLabel = new JLabel("OR");
		
		extractionLabel.setFont(font);

		extractButton.setForeground(Color.WHITE);
		extractButton.setBackground(new Color(183, 183, 183));
		
		extractFullButton.setForeground(Color.WHITE);
		extractFullButton.setBackground(new Color(183, 183, 183));
		
		audioExtractionPanel.add(extractionLabel, "wrap");
		audioExtractionPanel.add(timeLabel, "wrap");
		audioExtractionPanel.add(startTimeLabel, "split 4");
		audioExtractionPanel.add(startTimeInput);
		audioExtractionPanel.add(lengthLabel);
		audioExtractionPanel.add(lengthInput);
		audioExtractionPanel.add(extractButton);

		audioExtractionPanel.add(orLabel, "gapleft 30");

		audioExtractionPanel.add(extractFullButton, "gapleft 30, wrap");
	}

	//initialise panel for replace nad its layout
	private void setAudioReplacePanel() {
		Font font = replaceAudioLabel.getFont().deriveFont(Font.ITALIC + Font.BOLD, 16f); // Default is 12.

		replaceAudioLabel.setFont(font);

		// Custom coloured button
		selectAudioReplaceFileButton.setForeground(Color.WHITE);
		selectAudioReplaceFileButton.setBackground(new Color(99, 184, 255)); // blue
		
		audioReplaceButton.setForeground(Color.WHITE);
		audioReplaceButton.setBackground(new Color(183, 183, 183));
		
		audioReplacePanel.add(replaceAudioLabel, "wrap");
		audioReplacePanel.add(selectAudioReplaceFileButton);
		audioReplacePanel.add(selectedAudioReplaceFileTextField, "pushx, growx, wrap");
		audioReplacePanel.add(audioReplaceButton);
	}

	private void setAudioOverlayPanel() {
		Font font = audioOverlayLabel.getFont().deriveFont(Font.ITALIC + Font.BOLD, 16f);

		audioOverlayLabel.setFont(font);

		selectAudioOverlayFileButton.setForeground(Color.WHITE);
		selectAudioOverlayFileButton.setBackground(new Color(99, 184, 255)); // blue
		
		audioOverlayButton.setForeground(Color.WHITE);
		audioOverlayButton.setBackground(new Color(183, 183, 183));
		
		audioOverlayPanel.add(audioOverlayLabel, "wrap");
		audioOverlayPanel.add(selectAudioOverlayFileButton);
		audioOverlayPanel.add(selectedAudioOverlayFileTextField, "pushx, growx, wrap");
		audioOverlayPanel.add(audioOverlayButton);
	}
	
	//initialise listeners
	private void addListeners() {
		extractButton.addActionListener(this);
		extractFullButton.addActionListener(this);

		selectAudioReplaceFileButton.addActionListener(this);
		audioReplaceButton.addActionListener(this);

		selectAudioOverlayFileButton.addActionListener(this);
		audioOverlayButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == extractButton) {
			try {
				if (validateTime(startTimeInput.getText()) && validateTime(lengthInput.getText()) && validateMedia()) {
					String outputFilename = getOutputAudioFilename();

					if (outputFilename != null) {
						executeExtract(VamixProcesses.getFilename(mediaPlayer.mrl()), outputFilename, startTimeInput.getText(), lengthInput.getText());
					}

				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == extractFullButton) {
			try {
				if (validateMedia()) {
					String outputFilename = getOutputAudioFilename();

					if (outputFilename != null) {

						String length = MediaTimer.getFormattedTime(mediaPlayer.getLength());

						// if length comes back in the format, mm:ss. Have to add 00: so that it is in correct format.
						if (length.length() == 5) {
							length = "00:" + length;
						}

						executeExtract(VamixProcesses.getFilename(mediaPlayer.mrl()), outputFilename, "00:00:00", length);

					}
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == selectAudioReplaceFileButton) {
			String filename = getInputFilename();

			if (filename != null) {
				selectedAudioReplaceFileTextField.setText(filename);
			}

		} else if (e.getSource() == audioReplaceButton) {

			try {
				if (validateMedia() && validateTextfieldHasAudio(selectedAudioReplaceFileTextField.getText())) {
					File audioFile = new File(selectedAudioReplaceFileTextField.getText());

					String audioPath = audioFile.getPath();
					String videoPath = getOutputVideoFilename();
					
					if (videoPath != null) {
						executeReplace(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
					}
				}
			} catch (HeadlessException | IOException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() ==  selectAudioOverlayFileButton) {
			String filename = getInputFilename();

			if (filename != null) {
				selectedAudioOverlayFileTextField.setText(filename);
			}

		} else if (e.getSource() == audioOverlayButton) {
			try {
				if (validateMedia() && validateTextfieldHasAudio(selectedAudioOverlayFileTextField.getText())) {
					// This assumes that the audio selected is valid. No checking is done.
					// http://stackoverflow.com/questions/3140992/read-out-time-length-duration-of-an-mp3-song-in-java
					File audioFile = new File(selectedAudioOverlayFileTextField.getText());

					String audioPath = audioFile.getPath();
					
					String videoPath = getOutputVideoFilename();
					
					if (videoPath != null) {
						executeOverlay(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
					}
				}
			} catch (HeadlessException | IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	/**
	 * {@link worker.AudioReplaceWorker} <br />
	 * Calls AudioReplaceWorker. Opens ProgressMonitor.
	 * 
	 * @param videoInput
	 * @param audioInput
	 * @param videoOutput
	 */
	
	private void executeReplace(String videoInput, String audioInput, String videoOutput) {
		ProgressMonitor monitor = new ProgressMonitor(null, "Replacing audio has started",
				"In progress..", 0, 100);
		
		AudioReplaceWorker worker = new AudioReplaceWorker(videoInput, audioInput, videoOutput, monitor);
		worker.execute();
	}

	/**
	 * {@link worker.OverlayWorker} <br />
	 * Calls OverlayWorker. Opens ProgressMonitor.
	 * 
	 * @param videoInput
	 * @param audioInput
	 * @param videoOutput
	 */
	
	private void executeOverlay(String videoInput, String audioInput, String videoOutput) {
		
		ProgressMonitor monitor = new ProgressMonitor(null, "Overlaying audio has started",
				"In progress..", 0, 100);
		
		OverlayWorker worker = new OverlayWorker(videoInput, audioInput, videoOutput, monitor);
		worker.execute();
	}
	
	/**
	 * Returns the selected audio (mp3) file from JFileChooser.
	 * @return String
	 */

	private String getInputFilename() {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		// Adds mp3 as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG/mp3", "mp3"));

		int selection = chooser.showOpenDialog(this);

		if (selection == JFileChooser.APPROVE_OPTION) {
			File saveFile = chooser.getSelectedFile();

			String inputFilename = saveFile.getPath();

			// Checks that the audio file is an audio file or else it displays an error message.
			if (!VamixProcesses.validContentType(MediaType.AUDIO, inputFilename)) {
				JOptionPane.showMessageDialog(null, inputFilename + " does not refer to a valid audio file.");
				return null;
			}


			return inputFilename;
		}
		return null;
	}

	/**
	 * Returns the output filename of the video (mp4). Asks user if overwrite is desired if same file exists.
	 * @return String
	 * @throws IOException
	 */
	
	private String getOutputVideoFilename() {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		// Adds mp4 as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG-4", "mp4"));

		int selection = chooser.showSaveDialog(null);

		if (selection == JOptionPane.OK_OPTION) {
			File saveFile = chooser.getSelectedFile();
			String outputFilename = saveFile.getPath();

			String extensionType = chooser.getFileFilter().getDescription();

			/*
			 * Even though the extension type is listed below, sometimes users still add .mp4 to the end of the file so this
			 * makes sure that when I add extension type to the filename, I only add .mp4 if it's not already there.
			 * 
			 * At the moment, I only have .mp4 has possible extension.
			 */

			if (extensionType.contains("MPEG-4") && !saveFile.getPath().contains(".mp4")) {
				outputFilename = outputFilename + ".mp4";
			}

			// Checks to see if the filename the user wants to save already exists so it asks if it wants to overwrite or not.
			if (Files.exists(Paths.get(outputFilename))) {
				int overwriteSelection = JOptionPane.showConfirmDialog(null, "File already exists, do you want to overwrite?",
						"Select an option", JOptionPane.YES_NO_OPTION);

				// Overwrite if yes.
				if (overwriteSelection == JOptionPane.OK_OPTION) {
					return outputFilename;
				}
			} else {
				return outputFilename;
			}
		}
		
		return null;
	}
	
	
	/**
	 * Returns the output filename of the audio(mp3). Asks user if overwrite is desired if same file exists.
	 * @return String
	 * @throws IOException
	 */

	private String getOutputAudioFilename() throws IOException {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		// Adds mp3 as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG/mp3", "mp3"));

		int selection = chooser.showSaveDialog(this);

		if (selection == JFileChooser.APPROVE_OPTION) {
			File saveFile = chooser.getSelectedFile();

			String extensionType = chooser.getFileFilter().getDescription();
			String outputFilename = saveFile.getPath();

			/*
			 * Even though the extension type is listed below, sometimes users still add .mp3 to the end of the file so this
			 * makes sure that when I add extension type to the filename, I only add .mp3 if it's not already there.
			 * 
			 * At the moment, I only have .mp3 has possible extension.
			 */

			if (extensionType.contains("MPEG/mp3") && !saveFile.getPath().contains(".mp3")) {
				outputFilename = outputFilename + ".mp3";
			}

			// Checks to see if the filename the user wants to save already exists so it asks if it wants to overwrite or not.
			if (Files.exists(Paths.get(outputFilename))) {
				int overwriteSelection = JOptionPane.showConfirmDialog(null, "File already exists, do you want to overwrite?",
						"Select an option", JOptionPane.YES_NO_OPTION);

				// Overwrite if yes.
				if (overwriteSelection == JOptionPane.OK_OPTION) {
					return outputFilename;
				}
			} else {
				return outputFilename;
			}
		}

		return null;
	}

	/**
	 * {@link worker.ExtractAudioWorker }
	 * 
	 * @param inputFilename
	 * @param outputFilename
	 * @param startTime
	 * @param lengthTime
	 */

	private void executeExtract(String inputFilename, String outputFilename, String startTime, String lengthTime) {
		int lengthOfAudio = MediaTimer.getSeconds(lengthTime);

		ProgressMonitor monitor = new ProgressMonitor(null, "Extraction has started", "", 0, lengthOfAudio);

		ExtractAudioWorker worker = new ExtractAudioWorker(inputFilename, outputFilename, startTime, lengthTime, monitor);
		worker.execute();
	}

	/**
	 * Determines if the user input a valid time in the specified format. hh:mm:ss <br />
	 * 
	 * @param time
	 * @return
	 */

	private boolean validateTime(String time) {
		Date inputTime = null;
		try {
			// Checks that you can formulate date from given input.
			inputTime = timeFormat.parse(time);
		} catch (ParseException e) {
			// The time that was input does not match our given time format.
			JOptionPane.showMessageDialog(null, time + " is in the wrong time format");
			return false;
		}

		// Time can be rounded so ensure input time is correct. eg 61 seconds automatically becomes 1min 1sec.
		if (!timeFormat.format(inputTime).equals(time)) {
			JOptionPane.showMessageDialog(null, "Invalid time");
			return false;
		}

		// If we reach this statement, time has been validated.
		return true;
	}

	/**
	 * Determines if there is a media loaded onto player which is a video and contains an audio track.
	 * @return
	 * @throws IOException 
	 */

	private boolean validateMedia() throws IOException {
		if (mediaPlayer.isPlayable()) {
			String inputFilename = VamixProcesses.getFilename(mediaPlayer.mrl());

			if (inputFilename == null) {
				JOptionPane.showMessageDialog(null, "Incorrect file directory");
				return false;
			}

			if (VamixProcesses.validContentType(MediaType.VIDEO, inputFilename)) {

				if (mediaPlayer.getAudioTrackCount() == 0) {
					JOptionPane.showMessageDialog(null, "No audio track exists in video");
					return false;
				}
				
			} else {
				JOptionPane.showMessageDialog(null, "Media is not video file");
				return false;
			}
		} else {
			JOptionPane.showMessageDialog(null, "No media recognized");
			return false;
		}

		return true;
	}

	/**
	 * Validates that textfield contains a valid file. Furthermore, it contains an audio.
	 * @param path
	 * @return
	 * @throws IOException
	 */
	
	private boolean validateTextfieldHasAudio(String path) throws IOException {
		File f = new File(path);

		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, path + " is not a valid path");
			return false;
		}

		if (!VamixProcesses.validContentType(MediaType.AUDIO, path)) {
			JOptionPane.showMessageDialog(null, path + " is not an audio file");
			return false;
		}
		return true;
	}

}
