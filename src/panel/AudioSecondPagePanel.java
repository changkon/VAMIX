package panel;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import operation.AudioFileSelection;
import operation.FileSelection;
import operation.VamixProcesses;
import operation.VideoFileSelection;
import res.MediaIcon;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.AudioTrackWorker;
import worker.AudioVolumeChangeWorker;
import component.FileType;
import component.Playback;

/**
 * Second page of audio panel. Contains adding audio track and volume change.
 * @author changkon
 *
 */

@SuppressWarnings("serial")
public class AudioSecondPagePanel extends JPanel implements ActionListener {
	private static AudioSecondPagePanel theInstance = null;
	
	private EmbeddedMediaPlayer mediaPlayer;
	private TitledBorder title;
	
	private JPanel audioTrackPanel = new JPanel(new MigLayout());
	private JPanel audioVolumePanel = new JPanel(new MigLayout());
	
	private JButton selectAudioTrackFileButton = new JButton("Choose File");
	private JTextField selectedAudioTrackFileTextField = new JTextField();
	private JButton audioTrackButton = new JButton("Add Track");
	
	private JSpinner audioVolumeSpinner;
	private JButton audioVolumeButton = new JButton("Change Volume");
	
	private FileSelection audioFileSelection, videoFileSelection;
	
	private JPanel navigationPanel;
	private JButton leftButton;
	
	public static AudioSecondPagePanel getInstance() {
		if (theInstance == null) {
			theInstance = new AudioSecondPagePanel();
		}
		
		return theInstance;
	}
	
	private AudioSecondPagePanel() {
		setLayout(new MigLayout("fill"));
		
		title = BorderFactory.createTitledBorder("Audio Second Page");
		setBorder(title);
		
		mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
		
		setAudioVolumePanel();
		setAudioTrackPanel();
		setNavigationPanel();
		
		audioFileSelection = new AudioFileSelection();
		videoFileSelection = new VideoFileSelection();
		
		add(audioVolumePanel, "pushx, growx, wrap 0px");
		add(audioTrackPanel, "pushx, growx, wrap 0px");
		add(navigationPanel, "south");
		
		addListeners();
	}

	private void setAudioVolumePanel() {
		JLabel audioVolumeLabel = new JLabel("Change Volume");
		
		Font font = audioVolumeLabel.getFont().deriveFont(Font.BOLD, 16f);

		audioVolumeLabel.setFont(font);
		
		// Default value/minimum/maximum/step value
		SpinnerModel model = new SpinnerNumberModel(1, 0, 5, 0.1);
		audioVolumeSpinner = new JSpinner(model);
		audioVolumeSpinner.setToolTipText("Set volume of output file. 0 - 5");
		
		JComponent editor = audioVolumeSpinner.getEditor();
		
		// set the size of the spinner.
		if (editor instanceof DefaultEditor) {
			((DefaultEditor)editor).getTextField().setColumns(3);
			((DefaultEditor)editor).getTextField().setEditable(false);
		}
		
		audioVolumeButton.setForeground(Color.WHITE);
		audioVolumeButton.setBackground(new Color(59, 89, 182)); // blue
		
		audioVolumePanel.add(audioVolumeLabel, "wrap");
		audioVolumePanel.add(audioVolumeButton, "split 2, gap right 30");
		audioVolumePanel.add(audioVolumeSpinner);

	}
	
	private void setAudioTrackPanel() {
		JLabel audioTrackLabel = new JLabel("Add Track");
		
		Font font = audioTrackLabel.getFont().deriveFont(Font.BOLD, 16f);

		audioTrackLabel.setFont(font);
		
		selectAudioTrackFileButton.setForeground(Color.WHITE);
		selectAudioTrackFileButton.setBackground(new Color(59, 89, 182)); // blue

		audioTrackButton.setBackground(new Color(219, 219, 219)); // light grey

		audioTrackPanel.add(audioTrackLabel, "wrap");
		audioTrackPanel.add(selectAudioTrackFileButton);
		audioTrackPanel.add(selectedAudioTrackFileTextField, "pushx, growx, wrap");
		audioTrackPanel.add(audioTrackButton);
	}
	
	private void setNavigationPanel() {
		navigationPanel = new JPanel(new MigLayout());
		
		MediaIcon mediaIcon = new MediaIcon(15, 15);
		leftButton = new JButton(mediaIcon.getIcon(Playback.LEFT));
		
		leftButton.setToolTipText("Go to previous page");
		leftButton.setBorderPainted(false);
		leftButton.setFocusPainted(false);
		leftButton.setContentAreaFilled(false);
		
		navigationPanel.add(leftButton, "pushx, align left");
	}
	
	private void addListeners() {
		audioVolumeButton.addActionListener(this);
		selectAudioTrackFileButton.addActionListener(this);
		audioTrackButton.addActionListener(this);
		
		leftButton.addActionListener(this);
		leftButton.getModel().addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	            ButtonModel model = (ButtonModel) e.getSource();
	            if (model.isRollover()) {
	            	leftButton.setBorderPainted(true);
	            } else {
	            	leftButton.setBorderPainted(false);
	            }
	        }
	    });
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == audioVolumeButton) {
			if (VamixProcesses.validateMediaWithAudioTrack(mediaPlayer)) {
				String absolutePath = VamixProcesses.getFilename(mediaPlayer.mrl());
				
				// Check if media player file is video or audio and call appropriate JFileChooser.
				if (VamixProcesses.validContentType(FileType.VIDEO, absolutePath)) {
					String outputVideoFilename = videoFileSelection.getOutputFilename();
					
					if (outputVideoFilename != null) {
						executeAudioVolumeChange(absolutePath, outputVideoFilename);
					}
					
				} else if (VamixProcesses.validContentType(FileType.AUDIO, absolutePath)) {
					String outputAudioFilename = audioFileSelection.getOutputFilename();
					
					if (outputAudioFilename != null) {
						executeAudioVolumeChange(absolutePath, outputAudioFilename);
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Error reading file");
				}
				
			}
		} else if (e.getSource() == selectAudioTrackFileButton) {
			
			String filename = audioFileSelection.getInputFilename();

			if (filename != null) {
				selectedAudioTrackFileTextField.setText(filename);
			}
			
		} else if (e.getSource() == audioTrackButton) {
			
			if (VamixProcesses.validateVideoWithAudioTrack(mediaPlayer) && VamixProcesses.validateTextfield(selectedAudioTrackFileTextField.getText(), FileType.AUDIO)) {
				File audioFile = new File(selectedAudioTrackFileTextField.getText());

				String audioPath = audioFile.getAbsolutePath();
				String videoPath = videoFileSelection.getOutputFilename();

				if (videoPath != null) {
					executeAudioTrack(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
				}
			}
			
		} else if (e.getSource() == leftButton) {
			AudioFilterPanel audioFilterPanel = AudioFilterPanel.getInstance();
			CardLayout card = (CardLayout)audioFilterPanel.getLayout();
			card.show(audioFilterPanel, audioFilterPanel.AUDIOFIRSTPAGESTRING);
		}
	}
	
	/**
	 * Execute volume change. Calls AudioVolumeChangeWorker. </br>
	 * {@link worker.AudioVolumeChangeWorker}
	 * @param inputFile
	 * @param outputFilename
	 */
	
	private void executeAudioVolumeChange(String inputFile, String outputFilename) {
		int lengthOfFile = (int)(mediaPlayer.getLength() / 1000);

		ProgressMonitor monitor = new ProgressMonitor(null, "Changing audio volume has started",
				"", 0, lengthOfFile);
		double volume = (double)audioVolumeSpinner.getModel().getValue();
		
		AudioVolumeChangeWorker worker = new AudioVolumeChangeWorker(inputFile, outputFilename, volume, monitor);
		worker.execute();
		
	}
	
	/**
	 * Adds extra track to video file. Calls AudioTrackWorker </br>
	 * {@link worker.AudioTrackWorker}
	 * @param videoInput
	 * @param audioInput
	 * @param videoOutput
	 */
	
	private void executeAudioTrack(String videoInput, String audioInput, String videoOutput) {
		int videoLength = (int)(mediaPlayer.getLength() / 1000);
		int audioLength = VamixProcesses.probeDuration(audioInput);
		
		ProgressMonitor monitor = new ProgressMonitor(null, "Adding audio track has started",
				"In progress..", 0, Math.max(videoLength, audioLength));

		AudioTrackWorker worker = new AudioTrackWorker(videoInput, audioInput, videoOutput, monitor);
		worker.execute();
	}
}
