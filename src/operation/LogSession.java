package operation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JOptionPane;

public class LogSession {
	// Arbitrary separator value. Selected as it is an unlikely pattern.
	private static final String SEPARATORVALUE = ",;;,";
	
	/**
	 * Saves the data given to a text file. Separates each value by the separator value.
	 * @param filename
	 * @param type
	 * @param data
	 */
	
	public static void saveLog(String outputFilename, String type, Vector data) {
		File file = new File(outputFilename);
		
		if (file.exists()) {
			file.delete();
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilename));
			
			// Write the type of the saved file.
			writer.append(type + "\n");
			for (Object element : data) {
				
				Vector v = (Vector)element;
				Object[] values = v.toArray();
				
				for (Object i : values) {
					writer.append(i + SEPARATORVALUE);
				}
				
				writer.append("\n");
			}
			writer.close();

			JOptionPane.showMessageDialog(null, "Log has been recorded.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Reads the text file and returns the values back. If the text file is incorrect log file, return null.
	 * @param filename
	 * @param type
	 * @return
	 */
	
	public static ArrayList<Object[]> getLog(String inputFilename, String type) {
		try {
			BufferedReader buffer = new BufferedReader(new FileReader(inputFilename));
			
			String line = buffer.readLine();
			
			if (line.equals(type)) {
				
				ArrayList<Object[]> list = new ArrayList<Object[]>();
				
				while ((line = buffer.readLine()) != null) {
					String[] split = line.split(SEPARATORVALUE);
					list.add(split);
				}
				
				buffer.close();
				
				return list;
			} else {
				
				buffer.close();
				JOptionPane.showMessageDialog(null, "Incorrect log text. Cannot load work");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
}
