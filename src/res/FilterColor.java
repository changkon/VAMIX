package res;

import java.awt.Color;

/**
 * Enum of colors used for filterPanel.
 *
 */

public enum FilterColor {
	BLACK(Color.BLACK),
	RED(Color.RED),
	BLUE(Color.BLUE),
	GREEN(Color.GREEN),
	YELLOW(Color.YELLOW),
	ORANGE(Color.ORANGE);
	
	private Color color;
	
	private FilterColor(Color color) {
		this.color = color;
	}
	
	public Color getColor() {
		return color;
	}
	
	public static FilterColor toFilterColor(String str){
		switch(str) {
		case "BLACK":
			return FilterColor.BLACK;
		case "RED":
			return FilterColor.RED;
		case "BLUE":
			return FilterColor.BLUE;
		case "GREEN":
			return FilterColor.GREEN;
		case "YELLOW":
			return FilterColor.YELLOW;
		case "ORANGE":
			return FilterColor.ORANGE;
		default:
			return null;
		}
	}
}
