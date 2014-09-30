package res;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;

/**
 * 
 * Contains a directory to the font file.
 */

public enum FilterFont {
	FREESERIF(System.getProperty("user.dir") + "/res/FreeSerif.ttf"),
	UBUNTU_LIGHT(System.getProperty("user.dir") + "/res/Ubuntu-L.ttf"),
	UBUNTU_REGULAR(System.getProperty("user.dir") + "/res/Ubuntu-R.ttf"),
	UBUNTU_MEDIUM(System.getProperty("user.dir") + "/res/Ubuntu-M.ttf"),
	UBUNTU_CONDENSED(System.getProperty("user.dir") + "/res/Ubuntu-C.ttf");
	
	private Font font;
	private String path;
	
	private FilterFont(String path) {
		try {
			this.path = path;
			font = Font.createFont(Font.TRUETYPE_FONT, new File(path)); // may need to edit.
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
	
	public static FilterFont toFilterFont(String str){
		switch(str) {
		case "FreeSerif":
			return FilterFont.FREESERIF;
		case "Ubuntu Light":
			return FilterFont.UBUNTU_LIGHT;
		case "Ubuntu Regular":
			return FilterFont.UBUNTU_REGULAR;
		case "Ubuntu Medium":
			return FilterFont.UBUNTU_MEDIUM;
		case "Ubuntu Condensed":
			return FilterFont.UBUNTU_CONDENSED;
		default:
			return null;
	}
		
	}
}
