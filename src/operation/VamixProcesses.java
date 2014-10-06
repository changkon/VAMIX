package operation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import component.MediaType;

/**
 * Responsible for returning common processes required for the running of VAMIX.
 * @author changkon
 *
 */

public class VamixProcesses {

	/**
	 * This method returns the basename of any string file using a linux commands
	 * by creating a new process.
	 * @param filename
	 * @return
	 */
	
	public static String getBasename(String filename) {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "basename \'" + filename + "\'");
		
		String basename = "";
		
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
	
	/**
	 * Returns file path string from given mrl.
	 * @param mrl
	 * @return
	 */
	
	public static String getFilename(String mrl) {
		Pattern pattern = Pattern.compile("/[^/].*");
		Matcher p = pattern.matcher(mrl);
		
		if (p.find()) {
			return p.group();
		}
		return null;
	}
	
	/**
	 * Checks content type and returns if its valid.
	 * @param type
	 * @param path
	 * @return
	 */
	
	public static boolean validContentType(MediaType type, String path) {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "file --mime-type \'" + path + "\'");
		builder.redirectErrorStream(true);
		
		try {
			Process process = builder.start();
			
			InputStream stdout = process.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
			
			String line = buffer.readLine();
			
			if (line.contains(type.toString())) {
				return true;
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}
