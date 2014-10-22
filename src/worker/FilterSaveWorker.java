package worker;

import javax.swing.ProgressMonitor;

import res.FilterColor;
import res.FilterFont;
import setting.MediaSetting;

/**
 * Encodes filter options to file. Progress is shown on progress monitor.
 */

public class FilterSaveWorker extends DefaultWorker {
	private String inputFile, outputFile, openingText, closingText, openingX, openingY, closingX, closingY;
	private FilterFont openingFont, closingFont;
	private int openingFontSize, closingFontSize, filterOpeningLength, filterClosingLength, lastSeconds;
	private FilterColor openingFontColor, closingFontColor;
	
	public FilterSaveWorker(String inputFile, String outputFile, String openingText, String closingText, String openingX, String closingX, String openingY, String closingY, FilterFont openingFont, 
			FilterFont closingFont, int openingFontSize, int closingFontSize, FilterColor openingFontColor, FilterColor closingFontColor, ProgressMonitor monitor, int lengthOfVideo) {
		
		super(monitor);
		
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.openingText = openingText;
		this.closingText = closingText;
		
		// Set openingX/openingY and closingX/closingY. If the value is empty, give it determined values.
		if (openingX.equals("")) {
			this.openingX = "(W/2)-(w/2)"; // Sets to middle of screen. W = main input width. w = text width.
		} else {
			this.openingX = openingX;
		}
		
		if (closingX.equals("")) {
			this.closingX = "(W/2)-(w/2)";
		} else {
			this.closingX = closingX;
		}
		
		if (openingY.equals("")) {
			this.openingY = "(H/1.1)"; // Sets near the bottom of the screen. H = main input height.
		} else {
			this.openingY = openingY;
		}
		
		if (closingY.equals("")) {
			this.closingY = "(H/1.1)";
		} else {
			this.closingY = closingY;
		}		
		
		
		this.openingFont = openingFont;
		this.closingFont = closingFont;
		this.openingFontSize = openingFontSize;
		this.closingFontSize = closingFontSize;
		this.openingFontColor = openingFontColor;
		this.closingFontColor = closingFontColor;
		
		filterOpeningLength = MediaSetting.getInstance().getOpeningFilterLength();
		filterClosingLength = MediaSetting.getInstance().getClosingFilterLength();
		lastSeconds = monitor.getMaximum() - filterClosingLength;
		
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		//detects the number of seconds to display for and what to display
		StringBuilder command = new StringBuilder("avconv -i \'" + inputFile + "\' -c:a copy -vf ");
		
		boolean hasOpeningText = !openingText.equals("");
		boolean hasClosingText = !closingText.equals("");
		
		command.append("drawtext=\"fontfile=");
		
		//if there is opening and closing
		if (hasOpeningText && hasClosingText) {
			command.append(getOpeningCommand());
			command.append(":,drawtext=fontfile=");
			command.append(getClosingCommand());
		} else if (hasOpeningText) { //if there only is opening
			command.append(getOpeningCommand());
		} else {//if there is only closing 
			command.append(getClosingCommand());
		}
		
		command.append("\" -y \'" + outputFile + "\'");

		return command.toString();
	}

	private String getOpeningCommand() {
		return openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + ": y=" 
				+ openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterOpeningLength + ")\'";
	}
	
	private String getClosingCommand() {
		return closingFont.getPath() + ": fontsize=" + closingFontSize + ": fontcolor=" + closingFontColor.toString() + ": x=" + closingX + 
				": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'";
	}
	
	@Override
	protected String getSuccessMessage() {
		return "Filtering has completed";
	}

	@Override
	protected String getCancelMesssage() {
		return "Filtering was interrupted";
	}
}
