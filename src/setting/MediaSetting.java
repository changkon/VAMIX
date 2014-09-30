package setting;

/**
 * Contains the media settings for the VAMIX. Users can edit media settings through gui.
 * 
 */
public class MediaSetting {
	private static MediaSetting theInstance = null;
	private long skipTime;
	
	private final static int DEFAULT_OPENING_CLOSING_LENGTH = 10;
	
	private int openingFilterLength;
	private int closingFilterLength;
	
	public static MediaSetting getInstance() {
		if (theInstance == null) {
			theInstance = new MediaSetting();
		}
		return theInstance;
	}
	
	private MediaSetting() {
		skipTime = 5000;
		openingFilterLength = 10;
		closingFilterLength = 10;
	}
	
	public long getSkipTime() {
		return skipTime;
	}
	
	public void setSkipTime(long time) {
		skipTime = time;
	}
	
	/** Returns the length of the opening filter length
	 * 
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
	/** Returns the length of the closing filter length
	 * 
	 * 
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
	
	/** Sets the length of the opening filter length
	 * 
	 * @param str
	 */
	public void setOpeningFilterLength(String str){
		openingFilterLength = MediaSetting.stringToInt(str);
	}
	
	/** Sets the length of the closing filter length
	 * 
	 * @param str
	 */
	public void setClosingFilterLength(String str){
		closingFilterLength = MediaSetting.stringToInt(str);
	}
	
	/** Converter
	 * @return int
	 * 
	 */
	private static int stringToInt(String temp){
		int seconds = 0;
		
		switch(temp){
			case "1 second":
				seconds = 1;
				break;
			case "2 seconds":
				seconds = 2;
				break;
			case "3 seconds":
				seconds = 3;
				break;
			case "4 seconds":
				seconds = 4;
				break;
			case "5 seconds":
				seconds = 5;
				break;
			case "6 seconds":
				seconds = 6;
				break;
			case "7 seconds":
				seconds = 7;
				break;
			case "8 seconds":
				seconds = 8;
				break;
			case "9 sceonds":
				seconds = 9;
				break;
			case "10 seconds":
				seconds = 10;
				break;
		}
		return seconds;
	}
	
	/** Converter
	 * 
	 * 
	 * @param temp
	 * @return string
	 */
	public static String intToString(int temp){
		String seconds = "";
		
		switch(temp){
			case 1:
				seconds = "1 second";
				break;
			case 2:
				seconds = "2 seconds";
				break;
			case 3:
				seconds = "3 seconds";
				break;
			case 4:
				seconds = "4 seconds";
				break;
			case 5:
				seconds = "5 seconds";
				break;
			case 6:
				seconds = "6 seconds";
				break;
			case 7:
				seconds = "7 seconds";
				break;
			case 8:
				seconds = "8 seconds";
				break;
			case 9:
				seconds = "9 sceonds";
				break;
			case 10:
				seconds = "10 seconds";
				break;
		}
		return seconds;
	}
	
}
