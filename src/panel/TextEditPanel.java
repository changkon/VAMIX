package panel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.DocumentFilter;

import operation.VamixProcesses;

import component.MediaType;

import net.miginfocom.swing.MigLayout;
import res.FilterColor;
import res.FilterFont;
import setting.MediaSetting;

@SuppressWarnings("serial")
public class TextEditPanel extends JPanel implements ActionListener {
	
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
	
	private JLabel xLabel, yLabel;
	
	private JTextField xTextField, yTextField;
	
	private JButton previewButton;
	
	public TextEditPanel() {
		setLayout(new MigLayout());
		
		int maxWords = MediaSetting.getInstance().getTextEditMaxWords();
		
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
		
		setTextPanel();
		
		add(previewButton, "wrap 30px, pushx, align center");
		
		addListeners();
	}
	
	public void checkLog(String fileName) {
		boolean found = false;

		//set up the folder if it doesnt exist

		String currentFileName = fileName;
		File home = new File(System.getProperty("user.home"));
		File vamixdir = new File(home + "/.vamix");
		if (!vamixdir.exists()) {
			try{
				vamixdir.mkdir();
			}catch(SecurityException se){

			}
		}
		//set up the file if it doesnt exist
		File log = new File(home + "/.vamix/log.txt");
		try{
			if(!log.exists()){
				log.createNewFile();
			}
		}catch(IOException e){
		}
		try{
			//read the session for this video
			//only loads if it is available
			@SuppressWarnings("resource")
			BufferedReader in = new BufferedReader(new FileReader(log));
			String line = in.readLine();
			while(line != null && line.length() != 0){
				String[] words = line.split(",::,");
				if(words[0].equals(fileName)){	
					textArea.setText(words[1]);
					xTextField.setText(words[2]);
					yTextField.setText(words[3]);
					fontCombo.setSelectedItem(FilterFont.toFilterFont(words[4]));
					fontSizeCombo.setSelectedItem(Integer.parseInt(words[5]));
					fontColorCombo.setSelectedItem(FilterColor.toFilterColor(words[6]));
					timeLength.setSelectedItem(words[7]);
					found = true;
					//read the file and set it accordingly
				}
				line = in.readLine();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		if(found == false){
			textArea.setText("Put text here");
			xTextField.setText("");
			yTextField.setText("");
			fontCombo.setSelectedIndex(0);
			fontSizeCombo.setSelectedIndex(3);
			fontColorCombo.setSelectedIndex(0);
			timeLength.setSelectedIndex(0);
		}
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
		fontCombo.addActionListener(this);
		fontSizeCombo.addActionListener(this);
		fontColorCombo.addActionListener(this);
		timeLength.addActionListener(this);

		// Sets the preferred index of font size. It also calls event listener which is important for displaying correct font.
		fontSizeCombo.setSelectedIndex(3);

		previewButton.addActionListener(this);
	}
	
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

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == fontCombo || e.getSource() == fontColorCombo) {
			// Changes font and font size.
			Font font = ((FilterFont)fontCombo.getSelectedItem()).getFont();
			font = font.deriveFont((float)((Integer)fontSizeCombo.getSelectedItem()));

			textArea.setFont(font);
			textArea.setForeground(((FilterColor)fontColorCombo.getSelectedItem()).getColor());
		} else if (e.getSource() == previewButton) {
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
