package operation;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import component.FileType;

/**
 * Class which deals with returning and getting strings of file selection (audio/video) by JFileChooser.
 * @author chang
 *
 */

public abstract class FileSelection {
	
	protected ArrayList<String[]> inputFilterList, outputFilterList;
	protected FileType fileType;
	protected String warningMessage;
	
	public FileSelection(ArrayList<String[]> inputFilterList, ArrayList<String[]> outputFilterList, FileType fileType, String warningMessage) {
		this.inputFilterList = inputFilterList;
		this.outputFilterList = outputFilterList;
		this.fileType = fileType;
		this.warningMessage = warningMessage;
	}
	
	/**
	 * Returns the selected file.
	 * @return file of specific type
	 */
	
	public String getInputFilename() {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		
		// Adds appropriate filters to filechooser.
		for (String[] element : inputFilterList) {
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(element[0], element[1]));
		}
		
		int selection = chooser.showOpenDialog(null);

		if (selection == JFileChooser.APPROVE_OPTION) {
			File saveFile = chooser.getSelectedFile();

			String inputFilename = saveFile.getAbsolutePath();

			// Checks that the audio file is an audio file or else it displays an error message.
			if (!VamixProcesses.validContentType(fileType, inputFilename)) {
				JOptionPane.showMessageDialog(null, inputFilename + warningMessage);
				return null;
			}

			return inputFilename;
		}
		return null;
	}
	
	/**
	 * Returns the output filename. Asks user if overwrite is desired if same file exists. </br>
	 * Returns null if user cancels selection or does not want to overwrite.
	 * @return String
	 */

	public String getOutputFilename() {
		JFileChooser chooser = new JFileChooser();

		// Removes the accept all filter.
		chooser.setAcceptAllFileFilterUsed(false);
		
		for (String[] element : outputFilterList) {
			chooser.addChoosableFileFilter(new FileNameExtensionFilter(element[0], element[1]));	
		}
		
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
			
			for (String[] element : outputFilterList) {
				if (extensionType.contains(element[0]) && !saveFile.getAbsolutePath().contains(element[1])) {
					outputFilename = outputFilename + element[1];
					break;
				}
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
	
	protected void setInputFilterList(ArrayList<String[]> inputFilterList) {
		this.inputFilterList = inputFilterList;
	}
	
	protected void setOutputFilterList(ArrayList<String[]> outputFilterList) {
		this.outputFilterList = outputFilterList;
	}
	
}
