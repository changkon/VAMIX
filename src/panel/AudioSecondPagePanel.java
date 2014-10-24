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
import operation.FileSelection;
import operation.VamixProcesses;
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
	
	private EmbeddedMediaPlayer mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
	private TitledBorder title;
	
	private JPanel audioTrackPanel = new JPanel(new MigLayout());
	private JPanel audioVolumePanel = new JPanel(new MigLayout());
	
	private JPanel pageNavigationPanel = new JPanel(new MigLayout());
	private JButton leftButton;
	
	private JButton selectAudioTrackFileButton = new JButton("Choose File");
	private JTextField selectedAudioTrackFileTextField = new JTextField();
	private JButton audioTrackButton = new JButton("Add Track");
	
	private JSpinner audioVolumeSpinner;
	private JButton audioVolumeButton = new JButton("Change Volume");
	
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
		
		setAudioVolumePanel();
		setAudioTrackPanel();
		setPageNavigationPanel();

		add(audioVolumePanel, "pushx, growx, wrap 0px");
		add(audioTrackPanel, "pushx, growx, wrap 0px");
		add(pageNavigationPanel, "south");
		
		addListeners();
	}

	private void setAudioVolumePanel() {
		JLabel audioVolumeLabel = new JLabel("Change Volume");
		
		Font font = audioVolumeLabel.getFont().deriveFont(Font.ITALIC + Font.BOLD, 16f);

		audioVolumeLabel.setFont(font);
		
		// Default value/minimum/maximum/step value
		SpinnerModel model = new SpinnerNumberModel(1, 0, 5, 0.1);
		audioVolumeSpinner = new JSpinner(model);
		audioVolumeSpinner.setToolTipText("Set volume of output file. 0 - 5");
		
		JComponent editor = audioVolumeSpinner.getEditor();
		
		if (editor instanceof DefaultEditor) {
			((DefaultEditor)editor).getTextField().setColumns(3);
			((DefaultEditor)editor).getTextField().setEditable(false);
		}
		
		audioVolumeButton.setForeground(Color.WHITE);
		audioVolumeButton.setBackground(new Color(183, 183, 183));
		
		audioVolumePanel.add(audioVolumeLabel, "wrap");
		audioVolumePanel.add(audioVolumeButton, "split 2, gap right 30");
		audioVolumePanel.add(audioVolumeSpinner);

	}
	
	private void setAudioTrackPanel() {
		JLabel audioTrackLabel = new JLabel("Add Track");
		
		Font font = audioTrackLabel.getFont().deriveFont(Font.ITALIC + Font.BOLD, 16f);

		audioTrackLabel.setFont(font);
		
		selectAudioTrackFileButton.setForeground(Color.WHITE);
		selectAudioTrackFileButton.setBackground(new Color(99, 184, 255)); // blue

		audioTrackButton.setForeground(Color.WHITE);
		audioTrackButton.setBackground(new Color(183, 183, 183));

		audioTrackPanel.add(audioTrackLabel, "wrap");
		audioTrackPanel.add(selectAudioTrackFileButton);
		audioTrackPanel.add(selectedAudioTrackFileTextField, "pushx, growx, wrap");
		audioTrackPanel.add(audioTrackButton);
	}
	
	private void setPageNavigationPanel() {
		MediaIcon mediaIcon = new MediaIcon(15, 15);
		leftButton = new JButton(mediaIcon.getIcon(Playback.LEFT));
		
		leftButton.setToolTipText("Go to first page");
		leftButton.setContentAreaFilled(false);
		leftButton.setFocusPainted(false);
		leftButton.setBorderPainted(false);
		
		pageNavigationPanel.add(leftButton, "pushx, span, align left");
	}
	
	private void addListeners() {
		audioVolumeButton.addActionListener(this);
		selectAudioTrackFileButton.addActionListener(this);
		audioTrackButton.addActionListener(this);
		
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
		
		leftButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == audioVolumeButton) {
			if (VamixProcesses.validateMediaWithAudioTrack(mediaPlayer)) {
				String absolutePath = VamixProcesses.getFilename(mediaPlayer.mrl());
				
				// Check if media player file is video or audio and call appropriate JFileChooser.
				if (VamixProcesses.validContentType(FileType.VIDEO, absolutePath)) {
					String outputVideoFilename = FileSelection.getOutputVideoFilename();
					
					if (outputVideoFilename != null) {
						executeAudioVolumeChange(absolutePath, outputVideoFilename);
					}
					
				} else if (VamixProcesses.validContentType(FileType.AUDIO, absolutePath)) {
					String outputAudioFilename = FileSelection.getOutputAudioFilename();
					
					if (outputAudioFilename != null) {
						executeAudioVolumeChange(absolutePath, outputAudioFilename);
					}
					
				} else {
					JOptionPane.showMessageDialog(null, "Error reading file");
				}
				
			}
		} else if (e.getSource() == selectAudioTrackFileButton) {
			
			String filename = FileSelection.getInputAudioFilename();

			if (filename != null) {
				selectedAudioTrackFileTextField.setText(filename);
			}
			
		} else if (e.getSource() == audioTrackButton) {
			
			if (VamixProcesses.validateVideoWithAudioTrack(mediaPlayer) && VamixProcesses.validateTextfield(selectedAudioTrackFileTextField.getText(), FileType.AUDIO)) {
				File audioFile = new File(selectedAudioTrackFileTextField.getText());

				String audioPath = audioFile.getAbsolutePath();
				String videoPath = FileSelection.getOutputVideoFilename();

				if (videoPath != null) {
					executeAudioTrack(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
				}
			}
			
		} else if (e.getSource() == leftButton) {
			// Switch to first panel.
			CardLayout card = MainPanel.getInstance().getAudioCard();
			JPanel audioPanels = MainPanel.getInstance().getAudioPanel();
			String firstPageString = MainPanel.getInstance().audioFirstPageString;
			
			card.show(audioPanels, firstPageString);
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
