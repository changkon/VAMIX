package component;

/**
 * Enum for media type.
 * @author chang
 *
 */

public enum MediaType {
	AUDIO("audio"),
	VIDEO("video");
	
	private String type;
	
	private MediaType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}
