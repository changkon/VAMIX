package operation;

import java.util.ArrayList;

import component.FileType;

public class AudioFileSelection extends FileSelection {

	@SuppressWarnings("serial")
	private static ArrayList<String[]> filterList = new ArrayList<String[]>() {{
		add(new String[] {"MPEG/mp3", "mp3"});
	}};
	
	public AudioFileSelection() {
		super(filterList, filterList, FileType.AUDIO, " does not refer to a valid audio file.");
	}

}
