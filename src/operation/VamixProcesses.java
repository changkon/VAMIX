package operation;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
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
	 * @return basename
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
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "file -b \'" + path + "\'");
		builder.redirectErrorStream(true);
		
		try {
			Process process = builder.start();
			
			InputStream stdout = process.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
			
			String line = "";
			String[] supportedFormats = type.getSupportedFormats();
			
			while ((line = buffer.readLine()) != null) {
				for (String element : supportedFormats) {
					if (line.contains(element)) {
						return true;
					}
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Determines if audio track exists in media player.
	 * @param mediaPlayer
	 * @return
	 */
	
	public static boolean validateMediaWithAudioTrack(EmbeddedMediaPlayer mediaPlayer) {
		if (mediaPlayer.isPlayable()) {
			String inputFilename = VamixProcesses.getFilename(mediaPlayer.mrl());

			if (inputFilename == null) {
				JOptionPane.showMessageDialog(null, "Incorrect file directory");
				return false;
			}
				if (mediaPlayer.getAudioTrackCount() == 0) {
					JOptionPane.showMessageDialog(null, "No audio track exists");
					return false;
				}
		} else {
			JOptionPane.showMessageDialog(null, "No media recognized");
			return false;
		}

		return true;
	}
	
	/**
	 * Determines if there is a media loaded onto player which is a video and contains an audio track. </br>
	 * {@link panel.AudioFirstPagePanel} </br>
	 * {@link panel.AudioSecondPagePanel}
	 * @param mediaPlayer
	 * @return boolean
	 */
	
	public static boolean validateVideoWithAudioTrack(EmbeddedMediaPlayer mediaPlayer) {
		if (mediaPlayer.isPlayable()) {
			String inputFilename = VamixProcesses.getFilename(mediaPlayer.mrl());

			if (inputFilename == null) {
				JOptionPane.showMessageDialog(null, "Incorrect file directory");
				return false;
			}

			if (VamixProcesses.validContentType(MediaType.VIDEO, inputFilename)) {

				if (mediaPlayer.getAudioTrackCount() == 0) {
					JOptionPane.showMessageDialog(null, "No audio track exists in video");
					return false;
				}
				
			} else {
				JOptionPane.showMessageDialog(null, "Media is not video file");
				return false;
			}
		} else {
			JOptionPane.showMessageDialog(null, "No media recognized");
			return false;
		}

		return true;
	}
	
	/**
	 * Validates that textfield contains a valid file. Furthermore, it contains correct content.
	 * @param path
	 * @param mediaType
	 * @return
	 */
	
	public static boolean validateTextfield(String path, MediaType mediaType) {
		File f = new File(path);

		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, path + " is not a valid path");
			return false;
		}

		switch(mediaType) {
			case AUDIO:
				
				if (!VamixProcesses.validContentType(MediaType.AUDIO, path)) {
					JOptionPane.showMessageDialog(null, path + " is not an audio file");
					return false;
				}
				
				break;
			case VIDEO:
				
				if (!VamixProcesses.validContentType(MediaType.VIDEO, path)) {
					JOptionPane.showMessageDialog(null, path + " is not an video file");
					return false;
				}
				
				break;
		}

		return true;
	}
	
	/**
	 * Probes the content of media file and return the duration of the file in seconds. If the media file does not exist, return -1.
	 * @param path
	 * @return time of media file in seconds
	 */
	
	public static int probeDuration(String path) {
		// Redirect error stream to output stream.
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "avprobe \'" + path + "\' 2>&1 | grep Duration");
		
		try {
			Process process = builder.start();
			
			InputStream stdout = process.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
			
			String line = "";
			
			// Should only be one line printed.
			if ((line = buffer.readLine()) != null) {
				// Extract just the duration. Ignore the milliseconds. Format, hh:mm:ss
				Pattern p = Pattern.compile("\\d{2}:\\d{2}:\\d{2}");
				Matcher m = p.matcher(line);
				
				if (m.find()) {
					return MediaTimer.getSeconds(m.group());
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return -1;
	}
	
}
