package operation;

import java.util.ArrayList;

import component.FileType;

public class SubtitleFileSelection extends FileSelection {
	
	@SuppressWarnings("serial")
	private static ArrayList<String[]> filterList = new ArrayList<String[]>() {{
		add(new String[] {"SubRip Text/srt", "srt"});
	}};
	
	@SuppressWarnings("serial")
	private static ArrayList<String[]> outputVideoFilterList = new ArrayList<String[]>() {{
		add(new String[] {"Matroska/mkv", "mkv"});
	}};
	
	public SubtitleFileSelection() {
		super(filterList, filterList, FileType.SUBTITLE, " does not refer to a valid srt file.");
	}
	
	@Override
	public String getOutputFilename() {
		setOutputFilterList(filterList);
		return super.getOutputFilename();
	}



	public String getOutputVideoFilename() {
		setOutputFilterList(outputVideoFilterList);
		return getOutputFilename();
	}
	
}
