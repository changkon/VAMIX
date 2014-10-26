package setting;

/**
 * Contains the media settings for the VAMIX.
 * 
 */
public class MediaSetting {
	private static MediaSetting theInstance = null;
	private long skipTime;
	private int textEditMaxWords;
	
	public static MediaSetting getInstance() {
		if (theInstance == null) {
			theInstance = new MediaSetting();
		}
		return theInstance;
	}
	
	private MediaSetting() {
		skipTime = 5000;
		textEditMaxWords = 20;
	}
	
	public long getSkipTime() {
		return skipTime;
	}
	
	public void setSkipTime(long time) {
		skipTime = time;
	}
	
	public int getTextEditMaxWords() {
		return textEditMaxWords;
	}
	
	public void setTextEditMaxWords(int max) {
		textEditMaxWords = max;
	}
}
