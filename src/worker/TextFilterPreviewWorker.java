package worker;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;

import operation.MediaTimer;

/**
 * Shows preview of filter options. Opens a JFrame and shows the video with filters.
 */

public class TextFilterPreviewWorker extends SwingWorker<Void, Void> {
	private String inputFile;
	private Object[] data;


	public TextFilterPreviewWorker(String inputFile, Object[] data) {
		this.inputFile = inputFile;
		this.data = data;
	}

	@Override
	protected Void doInBackground() throws Exception {

		StringBuilder command = new StringBuilder();
		
		command.append("avplay -i \'" + inputFile + "\' ");
		command.append("-ss " + data[0] + " -vf ");
		command.append("drawtext=\"fontfile=" + data[3]);
		command.append(": fontsize=" + data[4]);
		command.append(": fontcolor=" + data[5]);
		command.append(": x=" + data[6]);
		command.append(": y=" + data[7]);
		command.append(": text=\'" + data[2] + "\': draw=\'lt(t," + MediaTimer.getSeconds(data[1].toString()) + ")\'\"");

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

				if (currentTime == MediaTimer.getSeconds(data[1].toString())) {

					// Sleep for 1 second in case video is fraction of a second longer.
					Thread.sleep(1000);
					process.destroy();
					break;
				}
			}
		}

		process.waitFor();

		return null;
	}

}
