package worker;

import java.util.ArrayList;
import java.util.Iterator;

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
	private ArrayList<String> textList;
	
	public FilterSaveWorker(String inputFile, String outputFile, ArrayList<String> textList, ProgressMonitor monitor) {
		
		super(monitor);
		
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		
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
		
		this.textList = textList;
		
		filterOpeningLength = MediaSetting.getInstance().getOpeningFilterLength();
		filterClosingLength = MediaSetting.getInstance().getClosingFilterLength();
		lastSeconds = monitor.getMaximum() - filterClosingLength;
		
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		//detects the number of seconds to display for and what to display
		StringBuilder command = new StringBuilder("avconv -i \'" + inputFile + "\' -c:a copy -vf ");
		
		command.append("drawtext=\"fontfile=");
		
		for (Iterator<String> iter = textList.iterator(); iter.hasNext();) {
			String i = iter.next();
			// Arbitrary text pattern
			String[] splitText = i.split(",::,");
			
			command.append(splitText[0] + ": fontsize=");
			command.append(splitText[1] + ": fontcolor=");
			command.append(splitText[2] + ": x=");
			command.append(splitText[3] + ": y=");
			command.append(splitText[4] + ": text=\'");
			command.append(splitText[5] + "\': draw=\'lt(t,");
			command.append(splitText[6] + ")\'");
			
			if (iter.hasNext()) {
				command.append(":,drawtext=fontfile=");
			}
		}
		
		command.append("\" -y \'" + outputFile + "\'");

		return command.toString();
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
