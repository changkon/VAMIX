package panel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import operation.FileSelection;
import operation.MediaTimer;
import operation.VamixProcesses;
import res.MediaIcon;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.AudioReplaceWorker;
import worker.ExtractAudioWorker;
import worker.OverlayWorker;

import component.FileType;
import component.Playback;

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

	private JTextField startTimeInput = new JTextField(10);
	private JTextField lengthInput = new JTextField(10);
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
	private JButton extractButton = new JButton("Extract");

	private JButton extractFullButton = new JButton("Extract entire file");

	private JButton selectAudioReplaceFileButton = new JButton("Choose File");
	private JTextField selectedAudioReplaceFileTextField = new JTextField();
	private JButton audioReplaceButton = new JButton("Replace");

	private JButton selectAudioOverlayFileButton = new JButton("Choose File");
	private JTextField selectedAudioOverlayFileTextField = new JTextField();
	private JButton audioOverlayButton = new JButton("Overlay");

	private JPanel pageNavigationPanel = new JPanel(new MigLayout());
	private JButton rightButton;
	
	public static AudioFirstPagePanel getInstance() {
		if (theInstance == null) {
			theInstance = new AudioFirstPagePanel();
		}
		return theInstance;
	}

	private AudioFirstPagePanel() {
		setLayout(new MigLayout("fill"));

		title = BorderFactory.createTitledBorder("Audio First Page");
		setBorder(title);

		setAudioExtractionPanel();
		setAudioReplacePanel();
		setAudioOverlayPanel();
		setPageNavigationPanel();
		
		addListeners();

		add(audioExtractionPanel, "wrap 0px, pushx, growx");
		add(audioReplacePanel, "wrap 0px, pushx, growx");
		add(audioOverlayPanel, "pushx, growx, wrap 0px");
		add(pageNavigationPanel, "south");
	}

	// Initialise panel for extraction and its layout
	private void setAudioExtractionPanel() {
		JLabel extractionLabel = new JLabel("Extraction");
		
		JLabel timeLabel = new JLabel("Please input times in hh:mm:ss");
		JLabel startTimeLabel = new JLabel("Start Time:");
		
		JLabel lengthLabel = new JLabel("Length Time:");
		
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

	// Initialise panel for replace and its layout
	private void setAudioReplacePanel() {
		JLabel replaceAudioLabel = new JLabel("Replace Audio");
		
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
		JLabel audioOverlayLabel = new JLabel("Overlay Audio");
		
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

	private void setPageNavigationPanel() {
		MediaIcon mediaIcon = new MediaIcon(15, 15);
		rightButton = new JButton(mediaIcon.getIcon(Playback.RIGHT));
		
		rightButton.setToolTipText("Go to second page");
		rightButton.setContentAreaFilled(false);
		rightButton.setFocusPainted(false);
		rightButton.setBorderPainted(false);
		
		pageNavigationPanel.add(rightButton, "pushx, span, align right");
	}
	
	// Initialise listeners
	private void addListeners() {
		extractButton.addActionListener(this);
		extractFullButton.addActionListener(this);

		selectAudioReplaceFileButton.addActionListener(this);
		audioReplaceButton.addActionListener(this);

		selectAudioOverlayFileButton.addActionListener(this);
		audioOverlayButton.addActionListener(this);
		
		rightButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	rightButton.setBorderPainted(true);
	            } else {
	            	rightButton.setBorderPainted(false);
	            }
	        }
	    });
		
		rightButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == extractButton) {
			if (validateTime(startTimeInput.getText()) && validateTime(lengthInput.getText()) && VamixProcesses.validateMediaWithAudioTrack(mediaPlayer)) {
				String outputFilename = FileSelection.getOutputAudioFilename();

				if (outputFilename != null) {
					executeExtract(VamixProcesses.getFilename(mediaPlayer.mrl()), outputFilename, startTimeInput.getText(), lengthInput.getText());
				}

			}
		} else if (e.getSource() == extractFullButton) {
			if (VamixProcesses.validateMediaWithAudioTrack(mediaPlayer)) {
				String outputFilename = FileSelection.getOutputAudioFilename();

				if (outputFilename != null) {

					String length = MediaTimer.getFormattedTime(mediaPlayer.getLength());

					// if length comes back in the format, mm:ss. Have to add 00: so that it is in correct format.
					if (length.length() == 5) {
						length = "00:" + length;
					}

					executeExtract(VamixProcesses.getFilename(mediaPlayer.mrl()), outputFilename, "00:00:00", length);

				}
			}
		} else if (e.getSource() == selectAudioReplaceFileButton) {
			String filename = FileSelection.getInputAudioFilename();

			if (filename != null) {
				selectedAudioReplaceFileTextField.setText(filename);
			}

		} else if (e.getSource() == audioReplaceButton) {
			if (VamixProcesses.validateVideoWithAudioTrack(mediaPlayer) && VamixProcesses.validateTextfield(selectedAudioReplaceFileTextField.getText(), FileType.AUDIO)) {
				File audioFile = new File(selectedAudioReplaceFileTextField.getText());

				String audioPath = audioFile.getAbsolutePath();
				String videoPath = FileSelection.getOutputVideoFilename();

				if (videoPath != null) {
					executeReplace(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
				}
			}
		} else if (e.getSource() ==  selectAudioOverlayFileButton) {
			String filename = FileSelection.getInputAudioFilename();

			if (filename != null) {
				selectedAudioOverlayFileTextField.setText(filename);
			}

		} else if (e.getSource() == audioOverlayButton) {
			if (VamixProcesses.validateVideoWithAudioTrack(mediaPlayer) && VamixProcesses.validateTextfield(selectedAudioOverlayFileTextField.getText(), FileType.AUDIO)) {
				// This assumes that the audio selected is valid. No checking is done.
				// http://stackoverflow.com/questions/3140992/read-out-time-length-duration-of-an-mp3-song-in-java
				File audioFile = new File(selectedAudioOverlayFileTextField.getText());

				String audioPath = audioFile.getAbsolutePath();

				String videoPath = FileSelection.getOutputVideoFilename();

				if (videoPath != null) {
					executeOverlay(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
				}
			}
		} else if (e.getSource() == rightButton) {
			CardLayout card = MainPanel.getInstance().getAudioCard();
			
			JPanel audioPanels = MainPanel.getInstance().getAudioPanel();
			String secondPageString = MainPanel.getInstance().audioSecondPageString;
			
			card.show(audioPanels, secondPageString);
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
		int audioInputLength = VamixProcesses.probeDuration(audioInput);
		int mediaPlayerLength = (int)mediaPlayer.getLength() / 1000;
		
		System.out.println(audioInput);
		System.out.println(audioInputLength);
		System.out.println(mediaPlayerLength);
		
		ProgressMonitor monitor = new ProgressMonitor(null, "Replacing audio has started",
				"In progress..", 0, Math.max(audioInputLength, mediaPlayerLength));

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

}
