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

import component.FileType;

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
	 * Checks content type and returns if its valid. <br/>
	 * {@link component.FileType}
	 * @param type
	 * @param path
	 * @return if file is valid type
	 */

	public static boolean validContentType(FileType type, String path) {
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
			JOptionPane.showMessageDialog(null, "Media must be playing before starting process");
			return false;
		}

		return true;
	}

	/**
	 * Determines if there is a media loaded onto player which is a video and contains an audio track. <br/>
	 * {@link panel.AudioFirstPagePanel} <br/>
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

			if (VamixProcesses.validContentType(FileType.VIDEO, inputFilename)) {

				if (mediaPlayer.getAudioTrackCount() == 0) {
					JOptionPane.showMessageDialog(null, "No audio track exists in video");
					return false;
				}

			} else {
				JOptionPane.showMessageDialog(null, "Media is not video file");
				return false;
			}
		} else {
			JOptionPane.showMessageDialog(null, "Media must be playing to start process");
			return false;
		}

		return true;
	}

	/**
	 * Validates that textfield contains a valid file. Furthermore, it contains correct content.
	 * @param path
	 * @param FileType
	 * @return
	 */

	public static boolean validateTextfield(String path, FileType fileType) {
		File f = new File(path);

		if (!f.exists()) {
			JOptionPane.showMessageDialog(null, path + " is not a valid path");
			return false;
		}

		if (fileType == FileType.AUDIO) {
			if (!VamixProcesses.validContentType(FileType.AUDIO, path)) {
				JOptionPane.showMessageDialog(null, path + " is not an audio file");
				return false;
			}

		} else if (fileType == FileType.VIDEO) {

			if (!VamixProcesses.validContentType(FileType.VIDEO, path)) {
				JOptionPane.showMessageDialog(null, path + " is not an video file");
				return false;
			}
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

	/**
	 * Returns the directory of the font file in the computer. If not found, returns null.
	 * {@link res.FilterFont}
	 * @param font
	 * @return absolute path of Font
	 */
	
	public static String getFontDirectory(String font) {
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", "fc-list | grep \'" + font + "\'");
		builder.redirectErrorStream(true);
		try {
			Process process = builder.start();
			
			InputStream stdout = process.getInputStream();
			BufferedReader buffer = new BufferedReader(new InputStreamReader(stdout));
			
			String line = "";
			
			if ((line = buffer.readLine()) != null) {
				String[] split = line.split(":");
				return split[0];
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
