package operation;

import java.util.concurrent.TimeUnit;

/**
 * 
 * Responsible for converting time into formatted version and vice versa.
 */

public class MediaTimer {
	
	/**
	 * Returns a formatted string for the time to display correct time in media player. hh:mm:ss
	 * @param time
	 * @return
	 */
	
	public static String getFormattedTime(long time) {
		int hours = (int)TimeUnit.MILLISECONDS.toHours(time) % 24;
		int minutes = (int)TimeUnit.MILLISECONDS.toMinutes(time) % 60;
		int seconds = (int)TimeUnit.MILLISECONDS.toSeconds(time) % 60;
		if (hours == 0) {
			return String.format("%02d:%02d", minutes, seconds);
		}
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	/**
	 * Returns the formatted string when given the hours, minutes and seconds.
	 * @param hours
	 * @param minutes
	 * @param seconds
	 * @return
	 */
	
	public static String getFormattedTime(int hours, int minutes, int seconds) {
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	/**
	 * Returns amount of seconds from hh:mm:ss format.
	 * @param formattedTime
	 * @return
	 */
	
	public static int getSeconds(String formattedTime) {
		
		int seconds = 0;
		
		String[] times = formattedTime.split(":");
		
		seconds = Integer.parseInt(times[2]); // times[2] seconds
		
		seconds += Integer.parseInt(times[1]) * 60; // times[1] minutes
		
		seconds += Integer.parseInt(times[0]) * 3600; // times[0] hours
		
		return seconds;
	}
}
