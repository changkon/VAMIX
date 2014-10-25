package operation;

import java.util.ArrayList;

import component.FileType;

public class TextFileSelection extends FileSelection {

	@SuppressWarnings("serial")
	private static ArrayList<String[]> filterList = new ArrayList<String[]>() {{
		add(new String[] {"Text/txt", "txt"});
	}};
	
	public TextFileSelection() {
		super(filterList, filterList, FileType.TEXT, " does not refer to a valid txt file.");
	}
	
}
