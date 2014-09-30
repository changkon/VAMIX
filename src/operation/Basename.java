package operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Basename {
	
	/*
	 * This class contains a static method which returns the basename of any string file using a linux commands
	 * by creating a new process.
	 */
	
	private static String basename;
	
	public static String getBasename(String filename) {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "basename " + filename);
		try {
			builder.redirectErrorStream(true);
			Process process = builder.start();
			InputStream stdout = process.getInputStream();

			BufferedReader stdoutBuffered = new BufferedReader(new InputStreamReader(stdout));

			if (process.waitFor() == 0) {
				basename = stdoutBuffered.readLine();
			}
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
		return basename;
	}
}
