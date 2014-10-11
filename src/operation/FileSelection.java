package operation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import component.MediaType;

/**
 * Class which deals with returning and getting strings of file selection (audio/video) by JFileChooser.
 * @author chang
 *
 */

public class FileSelection {
	/**
	 * Returns the selected audio (mp3) file from JFileChooser.
	 * @return String
	 */

	public static String getInputAudioFilename() {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		// Adds mp3 as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG/mp3", "mp3"));
		
		int selection = chooser.showOpenDialog(null);

		if (selection == JFileChooser.APPROVE_OPTION) {
			File saveFile = chooser.getSelectedFile();

			String inputFilename = saveFile.getAbsolutePath();

			// Checks that the audio file is an audio file or else it displays an error message.
			if (!VamixProcesses.validContentType(MediaType.AUDIO, inputFilename)) {
				JOptionPane.showMessageDialog(null, inputFilename + " does not refer to a valid audio file.");
				return null;
			}


			return inputFilename;
		}
		return null;
	}
	
	/**
	 * Returns the output filename of the audio(mp3). Asks user if overwrite is desired if same file exists.
	 * @return String
	 */

	public static String getOutputAudioFilename() {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		// Adds mp3 as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG/mp3", "mp3"));
		
		int selection = chooser.showSaveDialog(null);

		if (selection == JFileChooser.APPROVE_OPTION) {
			File saveFile = chooser.getSelectedFile();

			String extensionType = chooser.getFileFilter().getDescription();
			String outputFilename = saveFile.getAbsolutePath();

			/*
			 * Even though the extension type is listed below, sometimes users still add .mp3 to the end of the file so this
			 * makes sure that when I add extension type to the filename, I only add .mp3 if it's not already there.
			 * 
			 */

			if (extensionType.contains("MPEG/mp3") && !saveFile.getAbsolutePath().contains(".mp3")) {
				outputFilename = outputFilename + ".mp3";
			}

			// Checks to see if the filename the user wants to save already exists so it asks if it wants to overwrite or not.
			if (Files.exists(Paths.get(outputFilename))) {
				int overwriteSelection = JOptionPane.showConfirmDialog(null, "File already exists, do you want to overwrite?",
						"Select an option", JOptionPane.YES_NO_OPTION);

				// Overwrite if yes.
				if (overwriteSelection == JOptionPane.OK_OPTION) {
					return outputFilename;
				}
			} else {
				return outputFilename;
			}
		}

		return null;
	}
	
	public static String getInputVideoFilename() {
		JFileChooser chooser = new JFileChooser();
		
		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		// Adds mp4 as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG-4", "mp4"));
		// Adds avi as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Audio Video Interleaved/avi", "avi"));
		// Adds mkv as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Matroska/mkv", "mkv"));

		int selection = chooser.showOpenDialog(null);

		if (selection == JFileChooser.APPROVE_OPTION) {
			File saveFile = chooser.getSelectedFile();

			String inputFilename = saveFile.getAbsolutePath();

			// Checks that the video file is an video file or else it displays an error message.
			if (!VamixProcesses.validContentType(MediaType.VIDEO, inputFilename)) {
				JOptionPane.showMessageDialog(null, inputFilename + " does not refer to a valid video file.");
				return null;
			}


			return inputFilename;
		}
		return null;
	}
	
	/**
	 * Returns the output filename of the video. Asks user if overwrite is desired if same file exists. </br>
	 * If the user cancels or the user does not want to overwrite, it returns null.
	 * @return String
	 */
	
	public static String getOutputVideoFilename() {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		// Adds mp4 as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("MPEG-4", "mp4"));
		// Adds avi as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Audio Video Interleaved/avi", "avi"));
		// Adds mkv as filter.
		chooser.addChoosableFileFilter(new FileNameExtensionFilter("Matroska/mkv", "mkv"));

		int selection = chooser.showSaveDialog(null);

		if (selection == JOptionPane.OK_OPTION) {
			File saveFile = chooser.getSelectedFile();
			String outputFilename = saveFile.getAbsolutePath();

			String extensionType = chooser.getFileFilter().getDescription();

			/*
			 * Even though the extension type is listed below, sometimes users still add .mp4 to the end of the file so this
			 * makes sure that when I add extension type to the filename, I only add .mp4 if it's not already there.
			 * 
			 * Compatible with other filters.
			 */

			if (extensionType.contains("MPEG-4") && !saveFile.getAbsolutePath().contains(".mp4")) {
				outputFilename = outputFilename + ".mp4";
			} else if (extensionType.contains("Audio Video Interleaved/avi") && !saveFile.getAbsolutePath().contains(".avi")) {
				outputFilename = outputFilename + ".avi";
			} else if (extensionType.contains("Matroska/mkv") && !saveFile.getAbsolutePath().contains(".mkv")) {
				outputFilename = outputFilename + ".mkv";
			}

			// Checks to see if the filename the user wants to save already exists so it asks if it wants to overwrite or not.
			if (Files.exists(Paths.get(outputFilename))) {
				int overwriteSelection = JOptionPane.showConfirmDialog(null, "File already exists, do you want to overwrite?",
						"Select an option", JOptionPane.YES_NO_OPTION);

				// Overwrite if yes.
				if (overwriteSelection == JOptionPane.OK_OPTION) {
					return outputFilename;
				}
			} else {
				return outputFilename;
			}
		}
		
		return null;
	}
}
