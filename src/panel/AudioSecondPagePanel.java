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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import operation.FileSelection;
import operation.VamixProcesses;
import net.miginfocom.swing.MigLayout;
import res.MediaIcon;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.AudioReplaceWorker;
import worker.AudioTrackWorker;
import component.MediaType;
import component.Playback;

/**
 * 
 * @author changkon
 *
 */

@SuppressWarnings("serial")
public class AudioSecondPagePanel extends JPanel implements ActionListener {
	private static AudioSecondPagePanel theInstance = null;
	
	private EmbeddedMediaPlayer mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
	private TitledBorder title;
	
	private JPanel audioTrackPanel = new JPanel(new MigLayout());
	
	private JPanel pageNavigationPanel = new JPanel(new MigLayout());
	private JButton leftButton;
	
	private JButton selectAudioTrackFileButton = new JButton("Choose File");
	private JTextField selectedAudioTrackFileTextField = new JTextField();
	private JButton audioTrackButton = new JButton("Add Track");
	
	public static AudioSecondPagePanel getInstance() {
		if (theInstance == null) {
			theInstance = new AudioSecondPagePanel();
		}
		
		return theInstance;
	}
	
	private AudioSecondPagePanel() {
		setLayout(new MigLayout());
		
		title = BorderFactory.createTitledBorder("Audio Second Page");
		setBorder(title);
		
		setAudioTrackPanel();
		setPageNavigationPanel();

		add(audioTrackPanel, "pushx, growx, wrap 0px");
		add(pageNavigationPanel, "south");
		
		addListeners();
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
		
		leftButton.setContentAreaFilled(false);
		leftButton.setFocusPainted(false);
		leftButton.setBorderPainted(false);
		
		pageNavigationPanel.add(leftButton, "pushx, span, align left");
	}
	
	private void addListeners() {
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
		if (e.getSource() == selectAudioTrackFileButton) {
			
			String filename = FileSelection.getInputAudioFilename();

			if (filename != null) {
				selectedAudioTrackFileTextField.setText(filename);
			}
			
		} else if (e.getSource() == audioTrackButton) {
			
			if (VamixProcesses.validateVideoWithAudioTrack(mediaPlayer) && VamixProcesses.validateTextfield(selectedAudioTrackFileTextField.getText(), MediaType.AUDIO)) {
				File audioFile = new File(selectedAudioTrackFileTextField.getText());

				String audioPath = audioFile.getAbsolutePath();
				String videoPath = FileSelection.getOutputVideoFilename();

				if (videoPath != null) {
					executeAudioTrack(VamixProcesses.getFilename(mediaPlayer.mrl()), audioPath, videoPath);
				}
			}
			
		} else if (e.getSource() == leftButton) {
			CardLayout card = MainPanel.getInstance().getAudioCard();
			JPanel audioPanels = MainPanel.getInstance().getAudioPanel();
			String firstPageString = MainPanel.getInstance().audioFirstPageString;
			
			card.show(audioPanels, firstPageString);
		}
	}
	
	private void executeAudioTrack(String videoInput, String audioInput, String videoOutput) {
		ProgressMonitor monitor = new ProgressMonitor(null, "Adding audio track has started",
				"In progress..", 0, 100);

		AudioTrackWorker worker = new AudioTrackWorker(videoInput, audioInput, videoOutput, monitor);
		worker.execute();
	}
}
