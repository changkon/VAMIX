package worker;

import javax.swing.SwingWorker;

import operation.MediaTimer;
import panel.FadeFilterPanel;

/**
 * Preview the fade filter effects input by the user. {@link panel.FadeFilterPanel}
 * @author chang
 *
 */

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
		
		// Get the fade type. Either Fade In or Fade Out.
		String type = data[2].toString();
		FadeFilterPanel fadePanel = FadeFilterPanel.getInstance();
		
		// Determine which type to append.
		if (type.equals(fadePanel.fadeSelection[0])) {
			command.append("in:");
		} else {
			command.append("out:");
		}
		
		// data[0] and data[1] contains time value in hh:mm:ss format. Get the time for both.
		int startSeconds = MediaTimer.getSeconds(data[0].toString());
		int endSeconds = MediaTimer.getSeconds(data[1].toString());

		// Get the difference in times between start and second time. Get the difference in hh:mm:ss format.
		String formattedDifference = MediaTimer.getFormattedTime(Math.abs(startSeconds - endSeconds) * 1000);
		
		// Determine which frame to start fade effect.
		command.append(Math.round(MediaTimer.getCurrentFrame(data[0].toString(), mediaFrameRate)) + ":");
		// Append how long fade effect should last determined from the difference of the start and end times.
		command.append(Math.round((int)MediaTimer.getCurrentFrame(formattedDifference, mediaFrameRate)));
		command.append("\'");

		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", command.toString());
		Process process = builder.start();
		process.waitFor();
		
		return null;
	}
}
