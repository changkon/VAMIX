package operation;

import java.util.ArrayList;

import component.FileType;

public class VideoFileSelection extends FileSelection {

	@SuppressWarnings("serial")
	private static ArrayList<String[]> filterList = new ArrayList<String[]>() {{
		add(new String[] {"MPEG-4/mp4", "mp4"});
		add(new String[] {"Audio Video Interleaved/avi", "avi"});
		add(new String[] {"Matroska/mkv", "mkv"});
	}};
	
	public VideoFileSelection() {
		super(filterList, filterList, FileType.VIDEO, " does not refer to a valid video file.");
	}

}
