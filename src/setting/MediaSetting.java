package setting;

/**
 * Contains the media settings for the VAMIX. Users can edit media settings through gui.
 * 
 */
public class MediaSetting {
	private static MediaSetting theInstance = null;
	private long skipTime;
	private int textEditMaxWords;
<<<<<<< HEAD
=======
	
	private final static int DEFAULT_OPENING_CLOSING_LENGTH = 10;
	
	private int openingFilterLength;
	private int closingFilterLength;
>>>>>>> 68437b4ea3a68b7c66448ae99c2ef63c1743df7b
	
	public static MediaSetting getInstance() {
		if (theInstance == null) {
			theInstance = new MediaSetting();
		}
		return theInstance;
	}
	
	private MediaSetting() {
		skipTime = 5000;
<<<<<<< HEAD
=======
		openingFilterLength = 10;
		closingFilterLength = 10;
>>>>>>> 68437b4ea3a68b7c66448ae99c2ef63c1743df7b
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
<<<<<<< HEAD
=======
	}
	
	public void setTextEditMaxWords(int max) {
		textEditMaxWords = max;
	}
	
	/**
	 * Returns the length of the opening filter length
	 * @return int
	 */
	
	public int getOpeningFilterLength(){
		if(openingFilterLength < 1 || openingFilterLength > 10){
			return DEFAULT_OPENING_CLOSING_LENGTH;
		}
		else{
			return openingFilterLength;
		}
	}
	
	/** 
	 * Returns the length of the closing filter length
	 * @return int
	 */
	
	public int getClosingFilterLength(){
		if(closingFilterLength < 1 || closingFilterLength > 10){
			return DEFAULT_OPENING_CLOSING_LENGTH;
		}
		else{
			return closingFilterLength;
		}
	}
	
	/** 
	 * Sets the length of the opening filter length
	 * @param str
	 */
	
	public void setOpeningFilterLength(String str){
		openingFilterLength = MediaSetting.stringToInt(str);
	}
	
	/** 
	 * Sets the length of the closing filter length
	 * @param str
	 */
	
	public void setClosingFilterLength(String str){
		closingFilterLength = MediaSetting.stringToInt(str);
>>>>>>> 68437b4ea3a68b7c66448ae99c2ef63c1743df7b
	}
	
	public void setTextEditMaxWords(int max) {
		textEditMaxWords = max;
	}
}
