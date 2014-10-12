package component;

/**
 * Enum for media type. Each type contains valid keywords which are used to identify file type when called by file command.
 * @author chang
 *
 */

public enum MediaType {
	AUDIO(new String[] {"audio", "Audio"}),
	VIDEO(new String[] {"video", "ISO Media", "Matroska", "AVI"});
	
	private String[] type;
	
	private MediaType(String[] type) {
		this.type = type;
	}
	
	public String[] getSupportedFormats() {
		return type;
	}
}
