package panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;

import net.miginfocom.swing.MigLayout;
import operation.FileSelection;
import operation.VamixProcesses;
import res.FilterColor;
import res.FilterFont;
import setting.MediaSetting;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

import component.MediaType;

/**
 * Singleton design pattern. Panel contains anything related to filter editing of video.
 * 
 */

@SuppressWarnings("serial")
public class FilterPanel extends JPanel implements ActionListener {
	private static FilterPanel theInstance = null;
	private TitledBorder title;

	private JPanel optionPanel;
	private JTextArea textArea;
	private JScrollPane textScroll;
	
	private Integer[] fontSizeSelection = {10, 12, 14, 16, 18, 20, 22, 24, 26, 28, 30, 32, 34, 36, 38, 40};
	private String[] timeLengthSelection = {"1 second","2 seconds","3 seconds","4 seconds","5 seconds",
			"6 seconds", "7 seconds", "8 seconds", "9 seconds", "10 seconds"};
	
	private JComboBox<FilterFont> fontCombo;
	private JComboBox<Integer> fontSizeCombo;
	private JComboBox<FilterColor> fontColorCombo;
	
	private JComboBox<String> timeLength;
	
	private JLabel xLabel, yLabel, textLabel;
	
	private JTextField xTextField, yTextField;
	
	private JButton previewButton, saveButton;

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
		
		optionPanel = new JPanel(new MigLayout());
		textArea = new JTextArea(new MyStyledDocument(maxWords));
		textScroll = new JScrollPane(textArea);
		
		fontCombo = new JComboBox<FilterFont>(FilterFont.values());
		fontColorCombo = new JComboBox<FilterColor>(FilterColor.values());
		fontSizeCombo = new JComboBox<Integer>(fontSizeSelection);
		
		timeLength = new JComboBox<String>(timeLengthSelection);
		
		xLabel = new JLabel("x:");
		yLabel = new JLabel("y:");
		
		xTextField = new JTextField(5);
		yTextField = new JTextField(5);
		
		previewButton = new JButton("Preview");
		
		previewButton.setForeground(Color.WHITE);
		previewButton.setBackground(new Color(183, 183, 183));
		
		add(previewButton, "wrap 30px, pushx, align center");
		
		saveButton = new JButton("Save Video");
		
		textLabel = new JLabel("<html>Text (" + maxWords + " words max for each selection). X and Y <br /> co ordinates for the video is optional</html>");
		// Sets new font for textLabel.
		Font font = textLabel.getFont().deriveFont(Font.BOLD + Font.ITALIC, 14f);
		textLabel.setFont(font);

		add(textLabel, "wrap");

		setTextPanel();
		
		saveButton.setForeground(Color.WHITE);
		saveButton.setBackground(new Color(255, 106, 106));

		add(saveButton, "split 2, pushx, align center");
		
		addListeners();	
	}

	private void setTextPanel() {
		// Sets filter for textfields.
		((AbstractDocument)xTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());
		((AbstractDocument)yTextField.getDocument()).setDocumentFilter(new MyTextFieldFilter());

		optionPanel.add(fontCombo, "split 3"); // split the cell in 3. this is so 3 components go into same cell
		optionPanel.add(fontSizeCombo);
		optionPanel.add(fontColorCombo, "wrap");
		optionPanel.add(xLabel, "split 5"); // split the cell in 5. this is so 5 components go into same cell
		optionPanel.add(xTextField);
		optionPanel.add(yLabel);
		optionPanel.add(yTextField);
		optionPanel.add(timeLength);

		textScroll.setPreferredSize(new Dimension(400, 200)); // arbitrary value.

		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
		textArea.setText("Opening Scene Text");

		add(optionPanel, "wrap");
		add(textScroll);
	}
	
	private void addListeners() {
		saveButton.addActionListener(this);
		
		fontCombo.addActionListener(this);
		fontSizeCombo.addActionListener(this);
		fontColorCombo.addActionListener(this);
		timeLength.addActionListener(this);

		// Sets the preferred index of font size. It also calls event listener which is important for displaying correct font.
		fontSizeCombo.setSelectedIndex(3);

		previewButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fontCombo || e.getSource() == fontColorCombo) {
			// Changes font and font size.
			Font font = ((FilterFont)fontCombo.getSelectedItem()).getFont();
			font = font.deriveFont((float)((Integer)fontSizeCombo.getSelectedItem()));

			textArea.setFont(font);
			textArea.setForeground(((FilterColor)fontColorCombo.getSelectedItem()).getColor());
		} else if (e.getSource() == saveButton) {
			if (verifyInput()) {
				String outputFilename = FileSelection.getOutputVideoFilename();
				
				if (outputFilename != null) {
//					executeFilterSave(outputFilename);
				}

			}
		} else if (e.getSource() == previewButton) {
//			if (verifyInput()) { //preview the opening scene
//				MediaSetting.getInstance().setClosingFilterLength((String)closingTimeLength.getSelectedItem());
//				MediaSetting.getInstance().setOpeningFilterLength((String)openingTimeLength.getSelectedItem());				
//				int lengthOfVideo = (int)(mediaPlayer.getLength() / 1000);
//
//				FilterPreviewWorker worker = new FilterPreviewWorker(
//						"Opening",
//						VamixProcesses.getFilename(mediaPlayer.mrl()),
//						openingTextArea.getText(),
//						closingTextArea.getText(),
//						openingXTextField.getText(),
//						closingXTextField.getText(),
//						openingYTextField.getText(),
//						closingYTextField.getText(),
//						(FilterFont)openingFontCombo.getSelectedItem(),
//						(FilterFont)closingFontCombo.getSelectedItem(),
//						(Integer)openingFontSizeCombo.getSelectedItem(),
//						(Integer)closingFontSizeCombo.getSelectedItem(), 
//						(FilterColor)openingFontColorCombo.getSelectedItem(),
//						(FilterColor)closingFontColorCombo.getSelectedItem(),
//						lengthOfVideo
//						);
//
//
//				worker.execute();
//			}
		}
	}

	/**
	 * {@link worker.FilterSaveWorker} <br />
	 * Executes FilterSaveWorker and shows progress monitor.
	 * 
	 * @param outputFilename
	 */

//	private void executeFilterSave(String outputFilename) {
//
//		int lengthOfVideo = (int)(mediaPlayer.getLength() / 1000);
//
//		ProgressMonitor monitor = new ProgressMonitor(null, "Filtering has started", "", 0, lengthOfVideo);
//
//		FilterSaveWorker worker = new FilterSaveWorker(
//				VamixProcesses.getFilename(mediaPlayer.mrl()),
//				outputFilename,
//				openingTextArea.getText(),
//				closingTextArea.getText(),
//				openingXTextField.getText(),
//				closingXTextField.getText(),
//				openingYTextField.getText(),
//				closingYTextField.getText(),
//				(FilterFont)openingFontCombo.getSelectedItem(),
//				(FilterFont)closingFontCombo.getSelectedItem(),
//				(Integer)openingFontSizeCombo.getSelectedItem(),
//				(Integer)closingFontSizeCombo.getSelectedItem(), 
//				(FilterColor)openingFontColorCombo.getSelectedItem(),
//				(FilterColor)closingFontColorCombo.getSelectedItem(),
//				monitor,
//				lengthOfVideo
//				);
//
//		worker.execute();
//	}
	
	/**
	 * 
	 * Document which limits amount the amount of words in document. Words are recognised when they are separated by space
	 *
	 */

	private class MyStyledDocument extends DefaultStyledDocument {
		private int maxWords;

		public MyStyledDocument(int maxWords) {
			this.maxWords = maxWords;
		}

		// Override insertString method. Only add strings if less than 20 words. Words are counted if they are separated by space.
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			String text = getText(0, getLength());
			int count = 0;

			for (char c : text.toCharArray()) {
				if (c == ' ') {
					count++;
				}
			}

			if (count >= maxWords - 1 && str.equals(" ")) {
				JOptionPane.showMessageDialog(null, "Exceeded word limit!");
				return;
			}

			super.insertString(offs, str, a);
		}

	}
	
	/**
	 * 
	 * Filtering only numbers
	 * @see http://stackoverflow.com/questions/9477354/how-to-allow-introducing-only-digits-in-jtextfield
	 *
	 */

	private class MyTextFieldFilter extends DocumentFilter {

		// Called when insertString method is called on document. eg textField.getDocument().insertString(..);
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {

			boolean isDigits = true;

			for(char c : string.toCharArray()) {
				if (!Character.isDigit(c)) {
					isDigits = false;
					break;
				}
			}

			if (isDigits) {
				super.insertString(fb, offset, string, attr);
			}
		}

		// Invoked whenever text is input into textfield
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {

			boolean isDigits = true;

			for(char c : text.toCharArray()) {
				if (!Character.isDigit(c)) {
					isDigits = false;
					break;
				}
			}

			if (isDigits) {
				super.replace(fb, offset, length, text, attrs);
			}
		}

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
