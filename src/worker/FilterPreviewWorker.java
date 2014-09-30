package worker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import operation.MediaTimer;
import panel.MediaPanel;
import res.FilterColor;
import res.FilterFont;
import setting.MediaSetting;

/**
 * 
 * Shows preview of filter options. Opens a JFrame and shows the video with filters.
 *
 */

public class FilterPreviewWorker extends SwingWorker<Void, Void> {
	private String inputFilename;
	private String openingText;
	private String closingText;
	private String openingX;
	private String openingY;
	private String closingX;
	private String closingY;
	private String openingORclosing;
	
	private FilterFont openingFont;
	private FilterFont closingFont;
	private int openingFontSize;
	private int closingFontSize;
	private FilterColor openingFontColor;
	private FilterColor closingFontColor;
	private int lengthOfVideo;

	
	public FilterPreviewWorker(String openingORclosing, String inputFilename, String openingText, String closingText, String openingX, String closingX, String openingY, String closingY, 
			FilterFont openingFont, FilterFont closingFont, int openingFontSize, int closingFontSize, FilterColor openingFontColor, FilterColor closingFontColor, int lengthOfVideo) {
		this.openingORclosing = openingORclosing;
		this.inputFilename = inputFilename;
		this.openingText = openingText;
		this.closingText = closingText;
		
		// Set openingX/openingY and closingX/closingY. If the value is empty, give it determinded values.
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
		this.lengthOfVideo = lengthOfVideo;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		int filterOpeningLength = MediaSetting.getInstance().getOpeningFilterLength();
		int filterClosingLength = MediaSetting.getInstance().getClosingFilterLength();
		int lastSeconds = lengthOfVideo - filterClosingLength;
		int filterOpeningSceneLen = 0;
		int filterClosingSceneLen = 0;
		//set the time frame of playing AVPLAY
		StringBuilder command = null;
		//If opening, play from beginning
		if(openingORclosing.equals("Opening")){
			filterOpeningSceneLen = filterOpeningLength + 2;
			command = new StringBuilder("avplay -i \'" + inputFilename + "\' -t "+ filterOpeningSceneLen +" -vf ");
		}else if(openingORclosing.equals("Closing")){
			//If closing, play x number of seconds from the end
			filterClosingSceneLen = (int) (MediaPanel.getInstance().mediaPlayer.getLength() - 1000*filterClosingLength - 1000*2);

			String filterClosingSceneString = MediaTimer.getFormattedTime(filterClosingSceneLen);
			if(filterClosingSceneString.length() == 5){
				filterClosingSceneString = "00:" + filterClosingSceneString;
			}

			command = new StringBuilder("avplay -i \'" + inputFilename + "\' -ss "+ filterClosingSceneString + " -vf ");
		}
		
		
		boolean hasOpeningText = !openingText.equals("");
		boolean hasClosingText = !closingText.equals("");
		
		//if there is opening and closing
		if (hasOpeningText && hasClosingText) {
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + ": y=" 
					+ openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterOpeningLength + ")\':,drawtext=fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + 
					": fontcolor=" + closingFontColor.toString() + ": x=" +	closingX + ": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'\"");
		} else if (hasOpeningText) { //if there is only opening
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + 
					": y=" + openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterOpeningLength + ")\'\"");
		} else {// if there is only closing
			command.append("drawtext=\"fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + ": fontcolor=" + closingFontColor.toString() + ": x=" + closingX + 
					": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'\"");
		}
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		
		builder.redirectErrorStream(true);
		
		Process process = builder.start();
		
		InputStream stdout = process.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
		
		String line = "";
		int currentTime = 0;
		
		// Get strings which have this format which contains the current time and extract only seconds.
		Pattern p = Pattern.compile("\\d+[.]\\d\\d A-V");
		Matcher m;
		
		while((line = buffer.readLine()) != null) {
			m = p.matcher(line);

			if (m.find()) {
				String[] splitPattern = m.group().split("\\.");
				
				currentTime = Integer.parseInt(splitPattern[0]); // Only get the first number.
				
				if (openingORclosing.equals("Opening")) {
					if (currentTime == filterOpeningSceneLen) {
						process.destroy();
						break;
					}
				} else {
					if (currentTime == lengthOfVideo) {
						
						// Sleep for 1 second in case video is fraction of a second longer.
						Thread.sleep(1000);
						process.destroy();
						break;
					}
				}
			}
		}
		
		process.waitFor();

		return null;
	}

}
