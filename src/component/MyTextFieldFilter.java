package component;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * 
 * Filtering only numbers
 * @see http://stackoverflow.com/questions/9477354/how-to-allow-introducing-only-digits-in-jtextfield
 *
 */

public class MyTextFieldFilter extends DocumentFilter {

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