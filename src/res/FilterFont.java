package res;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

import operation.VamixProcesses;

/**
 * 
 * Contains a directory to the font file.
 */

public enum FilterFont {
	FREESERIF("FreeSerif.ttf"),
	UBUNTU_LIGHT("Ubuntu-L.ttf"),
	UBUNTU_REGULAR("Ubuntu-R.ttf"),
	UBUNTU_MEDIUM("Ubuntu-M.ttf"),
	UBUNTU_CONDENSED("Ubuntu-C.ttf");
	
	private Font font;
	private String path;
	
	private FilterFont(String fontName) {
		try {
			this.path = VamixProcesses.getFontDirectory(fontName);
			
			if (path != null) {
				font = Font.createFont(Font.TRUETYPE_FONT, new File(path));
			}
		} catch (FontFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Font getFont() {
		return font;
	}

	public String getPath() {
		return path;
	}
	
	@Override
	public String toString() {
		switch(this) {
			case FREESERIF:
				return "FreeSerif";
			case UBUNTU_LIGHT:
				return "Ubuntu Light";
			case UBUNTU_REGULAR:
				return "Ubuntu Regular";
			case UBUNTU_MEDIUM:
				return "Ubuntu Medium";
			case UBUNTU_CONDENSED:
				return "Ubuntu Condensed";
			default:
				return "";
		}
	}
}
