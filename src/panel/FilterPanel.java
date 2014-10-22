package panel;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ProgressMonitor;
import javax.swing.border.TitledBorder;

import component.MediaType;

import net.miginfocom.swing.MigLayout;
import operation.FileSelection;
import operation.VamixProcesses;
import res.FilterColor;
import res.FilterFont;
import setting.MediaSetting;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import worker.FilterPreviewWorker;
import worker.FilterSaveWorker;

/**
 * Singleton design pattern. Panel contains anything related to filter editing of video.
 * 
 */

@SuppressWarnings("serial")
public class FilterPanel extends JPanel implements ActionListener {
	private static FilterPanel theInstance = null;
	private TitledBorder title;

	private JLabel textLabel;

	private JButton saveButton, saveWorkButton;

	private EmbeddedMediaPlayer mediaPlayer;
	
	public static FilterPanel getInstance() {
		if (theInstance == null) {
			theInstance = new FilterPanel();
		}
		return theInstance;
	}

	private FilterPanel() {
		setLayout(new MigLayout("gap rel 0", "grow"));

		title = BorderFactory.createTitledBorder("Text Editing");
		setBorder(title);	

		int maxWords = MediaSetting.getInstance().getTextEditMaxWords();
		mediaPlayer = MediaPanel.getInstance().getMediaPlayerComponentPanel().getMediaPlayer();
		
		saveButton = new JButton("Save Video");
		saveWorkButton = new JButton("Save current work");
		
		textLabel = new JLabel("<html>Text (" + maxWords + " words max for each selection). X and Y <br /> co ordinates for the video is optional</html>");
		// Sets new font for textLabel.
		Font font = textLabel.getFont().deriveFont(Font.BOLD + Font.ITALIC, 14f);
		textLabel.setFont(font);

		add(textLabel, "wrap");

		saveButton.setForeground(Color.WHITE);
		saveButton.setBackground(new Color(255, 106, 106));

		saveWorkButton.setForeground(Color.WHITE);
		saveWorkButton.setBackground(new Color(255, 106, 106));

		add(saveButton, "split 2, pushx, align center");
		add(saveWorkButton);
		
		addListeners();	
	}

	private void addListeners() {
		saveButton.addActionListener(this);
		saveWorkButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == saveButton) {
			if (verifyInput()) {
				String outputFilename = FileSelection.getOutputVideoFilename();
				
				if (outputFilename != null) {
					executeFilterSave(outputFilename);
				}

			}
		} else if (e.getSource() == previewButton1) {
			if (verifyInput()) { //preview the opening scene
				MediaSetting.getInstance().setClosingFilterLength((String)closingTimeLength.getSelectedItem());
				MediaSetting.getInstance().setOpeningFilterLength((String)openingTimeLength.getSelectedItem());				
				int lengthOfVideo = (int)(mediaPlayer.getLength() / 1000);

				FilterPreviewWorker worker = new FilterPreviewWorker(
						"Opening",
						VamixProcesses.getFilename(mediaPlayer.mrl()),
						openingTextArea.getText(),
						closingTextArea.getText(),
						openingXTextField.getText(),
						closingXTextField.getText(),
						openingYTextField.getText(),
						closingYTextField.getText(),
						(FilterFont)openingFontCombo.getSelectedItem(),
						(FilterFont)closingFontCombo.getSelectedItem(),
						(Integer)openingFontSizeCombo.getSelectedItem(),
						(Integer)closingFontSizeCombo.getSelectedItem(), 
						(FilterColor)openingFontColorCombo.getSelectedItem(),
						(FilterColor)closingFontColorCombo.getSelectedItem(),
						lengthOfVideo
						);


				worker.execute();
			}
		}else if (e.getSource() == previewButton2) {
			if (verifyInput()) { //preview the closing scene
				MediaSetting.getInstance().setClosingFilterLength((String)closingTimeLength.getSelectedItem());
				MediaSetting.getInstance().setOpeningFilterLength((String)openingTimeLength.getSelectedItem());
				int lengthOfVideo = (int)(mediaPlayer.getLength() / 1000);

				FilterPreviewWorker worker = new FilterPreviewWorker(
						"Closing",
						VamixProcesses.getFilename(mediaPlayer.mrl()),
						openingTextArea.getText(),
						closingTextArea.getText(),
						openingXTextField.getText(),
						closingXTextField.getText(),
						openingYTextField.getText(),
						closingYTextField.getText(),
						(FilterFont)openingFontCombo.getSelectedItem(),
						(FilterFont)closingFontCombo.getSelectedItem(),
						(Integer)openingFontSizeCombo.getSelectedItem(),
						(Integer)closingFontSizeCombo.getSelectedItem(), 
						(FilterColor)openingFontColorCombo.getSelectedItem(),
						(FilterColor)closingFontColorCombo.getSelectedItem(),
						lengthOfVideo
						);


				worker.execute();
			}
		}else if (e.getSource() == saveWorkButton){
			//log the changes
			if(verifyInput()){
				PrintWriter writer = null;
				try {
					File home = new File(System.getProperty("user.home"));
					File logFile = new File(home + "/.vamix/log.txt");
					writer = new PrintWriter(new FileWriter(logFile, true));
					//write to the file the details of this session

					//didnt use comma seperators due to the possibility of commas being used in the actual text for opening and closing scenes
					String txtline = currentFileName + ",::," + openingTextArea.getText() + ",::," +closingTextArea.getText() + ",::," +
							openingXTextField.getText() + ",::," + closingXTextField.getText()+ ",::," + openingYTextField.getText() + ",::,"
							+ closingYTextField.getText() + ",::,"+ openingFontCombo.getSelectedItem() + ",::," + closingFontCombo.getSelectedItem() + ",::,"
							+ openingFontSizeCombo.getSelectedItem()+ ",::," + closingFontSizeCombo.getSelectedItem() + ",::," +
							openingFontColorCombo.getSelectedItem()+ ",::," + closingFontColorCombo.getSelectedItem() + ",::," + 
							openingTimeLength.getSelectedItem() + ",::," + closingTimeLength.getSelectedItem();
					writer.println(txtline);
					JOptionPane.showMessageDialog(null, "Saved session for this video. Press okay!");
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					try {
						writer.close();
					} catch (Exception x) {
						x.printStackTrace();
					}
				}
			}
		} else if (e.getSource() == openingTimeLength){
			MediaSetting.getInstance().setOpeningFilterLength((String)openingTimeLength.getSelectedItem());
		} else if (e.getSource() == closingTimeLength){
			MediaSetting.getInstance().setClosingFilterLength((String)closingTimeLength.getSelectedItem());
		}
	}

	/**
	 * {@link worker.FilterSaveWorker} <br />
	 * Executes FilterSaveWorker and shows progress monitor.
	 * 
	 * @param outputFilename
	 */

	private void executeFilterSave(String outputFilename) {

		int lengthOfVideo = (int)(mediaPlayer.getLength() / 1000);

		ProgressMonitor monitor = new ProgressMonitor(null, "Filtering has started", "", 0, lengthOfVideo);

		FilterSaveWorker worker = new FilterSaveWorker(
				VamixProcesses.getFilename(mediaPlayer.mrl()),
				outputFilename,
				openingTextArea.getText(),
				closingTextArea.getText(),
				openingXTextField.getText(),
				closingXTextField.getText(),
				openingYTextField.getText(),
				closingYTextField.getText(),
				(FilterFont)openingFontCombo.getSelectedItem(),
				(FilterFont)closingFontCombo.getSelectedItem(),
				(Integer)openingFontSizeCombo.getSelectedItem(),
				(Integer)closingFontSizeCombo.getSelectedItem(), 
				(FilterColor)openingFontColorCombo.getSelectedItem(),
				(FilterColor)closingFontColorCombo.getSelectedItem(),
				monitor,
				lengthOfVideo
				);

		worker.execute();
	}
	
	/**
	 * Verifies media is parsed. Also makes sure media is a video type.
	 * @return
	 */

	private boolean verifyInput() {

		// If media is not parsed, return false;
		if (!mediaPlayer.isPlayable()) {
			JOptionPane.showMessageDialog(null, "Please parse media");
			return false;
		}

		String inputFilename = VamixProcesses.getFilename(mediaPlayer.mrl());


		if (!VamixProcesses.validContentType(MediaType.VIDEO, inputFilename)) {
			JOptionPane.showMessageDialog(null, "This is not a video file");
			return false;
		}

		String text = textArea.getText();

		// Return false if both textArea are empty
		if (text.equals("")) {
			JOptionPane.showMessageDialog(null, "Please input some text to text area");
			return false;
		}


		return true;
	}
}
