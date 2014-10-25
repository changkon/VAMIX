package worker;

import javax.swing.SwingWorker;

import operation.MediaTimer;
import panel.FadeFilterPanel;

public class FadeFilterPreviewWorker extends SwingWorker<Void, Void> {
	private String inputFile;
	private Object[] data;
	private float mediaFrameRate;
	
	public FadeFilterPreviewWorker(String inputFile, Object[] data, float mediaFrameRate) {
		this.inputFile = inputFile;
		this.data = data;
		this.mediaFrameRate = mediaFrameRate;
	}
	
	@Override
	protected Void doInBackground() throws Exception {
		
		StringBuilder command = new StringBuilder();
		
		command.append("avplay -i \'" + inputFile + "\' -vf \'fade=");
		
		String type = data[2].toString();
		FadeFilterPanel fadePanel = FadeFilterPanel.getInstance();
		
		if (type.equals(fadePanel.fadeSelection[0])) {
			command.append("in:");
		} else {
			command.append("out:");
		}
		
		int startSeconds = MediaTimer.getSeconds(data[0].toString());
		int endSeconds = MediaTimer.getSeconds(data[1].toString());
		
		String formattedDifference = MediaTimer.getFormattedTime(Math.abs(startSeconds - endSeconds) * 1000);
		
		command.append(Math.round(MediaTimer.getCurrentFrame(data[0].toString(), mediaFrameRate)) + ":");
		command.append(Math.round((int)MediaTimer.getCurrentFrame(formattedDifference, mediaFrameRate)));
		command.append("\'");
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		Process process = builder.start();
		process.waitFor();
		
		return null;
	}
}
