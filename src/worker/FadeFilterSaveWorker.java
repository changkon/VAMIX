package worker;

import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.ProgressMonitor;

import operation.MediaTimer;
import panel.FadeFilterPanel;

public class FadeFilterSaveWorker extends DefaultWorker {
	private String inputFile, outputFile;
	private ArrayList<Object[]> fadeList;
	private float mediaFrameRate;
	
	public FadeFilterSaveWorker(String inputFile, String outputFile, ArrayList<Object[]> fadeList, float mediaFrameRate, ProgressMonitor monitor) {
		super(monitor);
		
		this.inputFile = inputFile;
		this.outputFile = outputFile;
		this.fadeList = fadeList;
		this.mediaFrameRate = mediaFrameRate;
		initialiseVariables();
	}

	@Override
	protected String getCommand() {
		StringBuilder command = new StringBuilder();
		
		String type = "";
		command.append("avconv -i \'" + inputFile + "\' -c:a copy -vf \'fade=");
		
		for (Iterator<Object[]> iter = fadeList.iterator(); iter.hasNext();) {
			Object[] i = iter.next();
			type = i[2].toString();
			
			FadeFilterPanel fadePanel = FadeFilterPanel.getInstance();
			
			if (type.equals(fadePanel.fadeSelection[0])) {
				command.append("in:");
			} else {
				command.append("out:");
			}
			
			int startSeconds = MediaTimer.getSeconds(i[0].toString());
			int endSeconds = MediaTimer.getSeconds(i[1].toString());
			
			String formattedDifference = MediaTimer.getFormattedTime(Math.abs(startSeconds - endSeconds) * 1000);
			
			command.append(Math.round(MediaTimer.getCurrentFrame(i[0].toString(), mediaFrameRate)) + ":");
			command.append(Math.round((int)MediaTimer.getCurrentFrame(formattedDifference, mediaFrameRate)));
			
			if (iter.hasNext()) {
				command.append(", fade=");
			}
		}

		command.append("\' -y \'" + outputFile + "\'");

		return command.toString();
	}

	@Override
	protected String getSuccessMessage() {
		return "Adding fade filters complete";
	}

	@Override
	protected String getCancelMesssage() {
		return "Adding fade filter was interrupted";
	}

}
