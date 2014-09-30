package worker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import res.FilterColor;
import res.FilterFont;
import setting.MediaSetting;

/**
 * 
 * Encodes filter options to file. Progress is shown on progress monitor.
 *
 */

public class FilterSaveWorker extends SwingWorker<Void, Integer> {
	private String inputFilename;
	private String outputFilename;
	private String openingText;
	private String closingText;
	private String openingX;
	private String openingY;
	private String closingX;
	private String closingY;
	private FilterFont openingFont;
	private FilterFont closingFont;
	private int openingFontSize;
	private int closingFontSize;
	private FilterColor openingFontColor;
	private FilterColor closingFontColor;
	private ProgressMonitor monitor;
	private int lengthOfVideo;
	
	public FilterSaveWorker(String inputFilename, String outputFilename, String openingText, String closingText, String openingX, String closingX, String openingY, String closingY, FilterFont openingFont, 
			FilterFont closingFont, int openingFontSize, int closingFontSize, FilterColor openingFontColor, FilterColor closingFontColor, ProgressMonitor monitor, int lengthOfVideo) {
		
		this.inputFilename = inputFilename;
		this.outputFilename = outputFilename;
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
		this.monitor = monitor;
		this.lengthOfVideo = lengthOfVideo;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		//detects the number of seconds to display for and what to display
		StringBuilder command = new StringBuilder("avconv -i \'" + inputFilename + "\' -c:a copy -vf ");
		int filterOpeningLength = MediaSetting.getInstance().getOpeningFilterLength();
		int filterClosingLength = MediaSetting.getInstance().getClosingFilterLength();
		int lastSeconds = lengthOfVideo - filterClosingLength;
		
		boolean hasOpeningText = !openingText.equals("");
		boolean hasClosingText = !closingText.equals("");
		
		//if there is opening and closing
		if (hasOpeningText && hasClosingText) {
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + ": y=" 
					+ openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterOpeningLength + ")\':,drawtext=fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + 
					": fontcolor=" + closingFontColor.toString() + ": x=" +	closingX + ": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'\" \'"
					+ outputFilename + "\'");
		} else if (hasOpeningText) { //if there only is opening
			command.append("drawtext=\"fontfile=" + openingFont.getPath() + ": fontsize=" + openingFontSize + ": fontcolor=" + openingFontColor.toString() + ": x=" + openingX + 
					": y=" + openingY + ": text=\'" + openingText + "\': draw=\'lt(t," + filterOpeningLength + ")\'\" \'" + outputFilename + "\'");
		} else {//if there is only closing 
			command.append("drawtext=\"fontfile=" + closingFont.getPath() + ": fontsize=" + closingFontSize + ": fontcolor=" + closingFontColor.toString() + ": x=" + closingX + 
					": y=" + closingY + ": text=\'" + closingText + "\': draw=\'gt(t," + lastSeconds + ")\'\" \'" + outputFilename + "\'");
		}
		//call the process
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		builder.redirectErrorStream(true);
		Process process = builder.start();
		
		InputStream stdout = process.getInputStream();
		BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
				
		Pattern p = Pattern.compile("\\btime=\\b\\d+.\\d+");
		Matcher m;
		String line = "";
		
		while ((line = buffer.readLine()) != null) {
			
			if (monitor.isCanceled()) {
				process.destroy();
				break;
			}
			
			m = p.matcher(line);
			
			if (m.find()) {
				// greedy solution. We know if a string matches pattern, it must start with time=
				publish((int)Double.parseDouble(m.group().substring(5)));
			}
		}
		
		process.waitFor();
		
		if (monitor.isCanceled()) {
			this.cancel(true);
		}
		
		return null;
	}

	@Override
	protected void process(List<Integer> chunks) {
		if (!isDone()) {
			for (Integer element : chunks) {
				String format = String.format("Completed : %2d%%", (int)(((double)element / lengthOfVideo) * 100));
				monitor.setNote(format);
				monitor.setProgress(element);
			}
		}
	}
	
	@Override
	protected void done() {
		try {
			monitor.close();
			get();
			JOptionPane.showMessageDialog(null, "Filtering has completed");
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} catch (CancellationException e) {
			JOptionPane.showMessageDialog(null, "Filtering was interrupted");
		}
	}
	
}
