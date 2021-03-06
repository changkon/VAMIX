package component;

/**
 * Enum for media type. Each type contains valid keywords which are used to identify file type when called by file command.
 * @author chang
 *
 */

public enum FileType {
	AUDIO(new String[] {"audio", "Audio"}),
	VIDEO(new String[] {"video", "ISO Media", "Matroska", "AVI"}),
	TEXT(new String[] {"ASCII text", "UTF-8"});
	
	private String[] type;
	
	private FileType(String[] type) {
		this.type = type;
	}
	
	public String[] getSupportedFormats() {
		return type;
	}
}
