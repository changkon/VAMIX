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
import operation.AudioFileSelection;
import operation.FileSelection;
import operation.MediaTimer;
import operation.VamixProcesses;
import operation.VideoFileSelection;
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
	
	private FileSelection audioFileSelection, videoFileSelection;
	
	private JButton rightButton;
	private JPanel navigationPanel;
	
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
		setNavigationPanel();
		
		audioFileSelection = new AudioFileSelection();
		videoFileSelection = new VideoFileSelection();
		
		addListeners();

		add(audioExtractionPanel, "wrap 0px, pushx, growx");
		add(audioReplacePanel, "wrap 0px, pushx, growx");
		add(audioOverlayPanel, "pushx, growx, wrap 0px");
		add(navigationPanel, "south");
	}

	// Initialise panel for extraction and its layout
	private void setAudioExtractionPanel() {
		JLabel extractionLabel = new JLabel("Extraction");
		
		JLabel timeLabel = new JLabel("Please input times in hh:mm:ss");
		JLabel startTimeLabel = new JLabel("Start Time:");
		
		JLabel lengthLabel = new JLabel("Length Time:");
		
		Font font = extractionLabel.getFont().deriveFont(Font.BOLD, 16f); // Default is 12.

		JLabel orLabel = new JLabel("OR");

		extractionLabel.setFont(font);

		extractButton.setForeground(Color.WHITE);
		extractButton.setBackground(new Color(59, 89, 182)); // blue

		extractFullButton.setForeground(Color.WHITE);
		extractFullButton.setBackground(new Color(59, 89, 182)); // blue

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
		
		Font font = replaceAudioLabel.getFont().deriveFont(Font.BOLD, 16f); // Default is 12.

		replaceAudioLabel.setFont(font);

		// Custom coloured button
		selectAudioReplaceFileButton.setForeground(Color.WHITE);
		selectAudioReplaceFileButton.setBackground(new Color(59, 89, 182)); // blue
		
		audioReplaceButton.setBackground(new Color(219, 219, 219)); // light grey

		audioReplacePanel.add(replaceAudioLabel, "wrap");
		audioReplacePanel.add(selectAudioReplaceFileButton);
		audioReplacePanel.add(selectedAudioReplaceFileTextField, "pushx, growx, wrap");
		audioReplacePanel.add(audioReplaceButton);
	}

	private void setAudioOverlayPanel() {
		JLabel audioOverlayLabel = new JLabel("Overlay Audio");
		
		Font font = audioOverlayLabel.getFont().deriveFont(Font.BOLD, 16f);

		audioOverlayLabel.setFont(font);
		
		selectAudioOverlayFileButton.setForeground(Color.WHITE);
		selectAudioOverlayFileButton.setBackground(new Color(59, 89, 182)); // blue
		
		audioOverlayButton.setBackground(new Color(219, 219, 219)); // light grey

		audioOverlayPanel.add(audioOverlayLabel, "wrap");
		audioOverlayPanel.add(selectAudioOverlayFileButton);
		audioOverlayPanel.add(selectedAudioOverlayFileTextField, "pushx, growx, wrap");
		audioOverlayPanel.add(audioOverlayButton);
	}
	
	private void setNavigationPanel() {
		navigationPanel = new JPanel(new MigLayout());
		
		MediaIcon mediaIcon = new MediaIcon(15, 15);
		rightButton = new JButton(mediaIcon.getIcon(Playback.RIGHT));
		
		rightButton.setToolTipText("Go to next page");
		rightButton.setBorderPainted(false);
		rightButton.setFocusPainted(false);
		rightButton.setContentAreaFilled(false);
		
		navigationPanel.add(rightButton, "pushx, align right");
	}
	
	// Initialise listeners
	private void addListeners() {
		extractButton.addActionListener(this);
		extractFullButton.addActionListener(this);

		selectAudioReplaceFileButton.addActionListener(this);
		audioReplaceButton.addActionListener(this);

		selectAudioOverlayFileButton.addActionListener(this);
		audioOverlayButton.addActionListener(this);
		
		rightButton.addActionListener(this);
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
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == extractButton) {
			if (validateTime(startTimeInput.getText()) && validateTime(lengthInput.getText()) && VamixProcesses.validateMediaWithAudioTrack(mediaPlayer)) {
				
				String outputFilename = audioFileSelection.getOutputFilename();

				if (outputFilename != null) {
					executeExtract(VamixProcesses.getFilename(mediaPlayer.mrl()), outputFilename, startTimeInput.getText(), lengthInput.getText());
				}

			}
		} else if (e.getSource() == extractFullButton) {
			if (VamixProcesses.validateMediaWithAudioTrack(mediaPlayer)) {
				String outputFilename = audioFileSelection.getOutputFilename();

				if (outputFilename != null) {

					String length = MediaTimer.getFormattedTime(mediaPlayer.getLength());

					executeExtract(VamixProcesses.getFilename(mediaPlayer.mrl()), outputFilename, "00:00:00", length);

				}
			}
		} else if (e.getSource() == selectAudioReplaceFileButton) {
			String filename = audioFileSelection.getInputFilename();

			if (filename != null) {
				selectedAudioReplaceFileTextField.setText(filename);
			}

		} else if (e.getSource() == audioReplaceButton) {
			if (VamixProcesses.validateVideoWithAudioTrack(mediaPlayer) && VamixProcesses.validateTextfield(selectedAudioReplaceFileTextField.getText(), FileType.AUDIO)) {
				File audioFile = new File(selectedAudioReplaceFileTextField.getText());

				String audioPath = audioFile.getAbsolutePath();
				String videoPath = videoFileSelection.getOutputFilename();

				if (videoPath != null) {
					executeReplace(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
				}
			}
		} else if (e.getSource() ==  selectAudioOverlayFileButton) {
			String filename = audioFileSelection.getInputFilename();

			if (filename != null) {
				selectedAudioOverlayFileTextField.setText(filename);
			}

		} else if (e.getSource() == audioOverlayButton) {
			if (VamixProcesses.validateVideoWithAudioTrack(mediaPlayer) && VamixProcesses.validateTextfield(selectedAudioOverlayFileTextField.getText(), FileType.AUDIO)) {
				// This assumes that the audio selected is valid. No checking is done.
				File audioFile = new File(selectedAudioOverlayFileTextField.getText());

				String audioPath = audioFile.getAbsolutePath();

				String videoPath = videoFileSelection.getOutputFilename();

				if (videoPath != null) {
					executeOverlay(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
				}
			}
		} else if (e.getSource() == rightButton) {
			AudioFilterPanel audioFilterPanel = AudioFilterPanel.getInstance();
			CardLayout card = (CardLayout)audioFilterPanel.getLayout();
			card.show(audioFilterPanel, audioFilterPanel.AUDIOSECONDPAGESTRING);
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
		int mediaPlayerLength = (int)(mediaPlayer.getLength() / 1000);
		
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
		int audioInputLength = VamixProcesses.probeDuration(audioInput);
		int videoInputLength = (int)(mediaPlayer.getLength() / 1000);
		
		ProgressMonitor monitor = new ProgressMonitor(null, "Overlaying audio has started",
				"In progress..", 0, Math.max(audioInputLength, videoInputLength));

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
