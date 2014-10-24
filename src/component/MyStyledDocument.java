package component;

import javax.swing.JOptionPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

/**
 * 
 * Document which limits amount the amount of words in document. Words are recognised when they are separated by space
 *
 */

@SuppressWarnings("serial")
public class MyStyledDocument extends DefaultStyledDocument {
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