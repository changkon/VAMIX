package operation;

import java.util.concurrent.TimeUnit;

/**
 * 
 * Responsible for converting time into formatted version and vice versa.
 */

public class MediaTimer {
	
	/**
	 * Returns formatted time in hh:mm:ss format. Input is in milliseconds
	 * @param time
	 * @return
	 */
	
	public static String getFormattedTime(long time) {
		int hours = (int)TimeUnit.MILLISECONDS.toHours(time) % 24;
		int minutes = (int)TimeUnit.MILLISECONDS.toMinutes(time) % 60;
		int seconds = (int)TimeUnit.MILLISECONDS.toSeconds(time) % 60;
		return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	}
	
	/**
	 * Returns a formatted string for the time to display correct time in media player. hh:mm:ss or mm:ss
	 * @param time
	 * @return
	 */
	
	public static String getMediaTime(long time) {
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
	
	/**
	 * Receive two formatted times, hh:mm:ss and return the difference in hh:mm:ss
	 * @param formattedTime1
	 * @param formattedTime2
	 * @return difference in time formatted in hh:mm:ss
	 */
	
	public static String getDifferenceInTimeFormatted(String formattedTime1, String formattedTime2) {
		int difference = Math.abs(getSeconds(formattedTime1) - getSeconds(formattedTime2));
		return getFormattedTime((long)difference);
	}
	
	/**
	 * Receive two formatted times, hh:mm:ss and return the difference in seconds
	 * @param formattedTime1
	 * @param formattedTime2
	 * @return difference in time in seconds
	 */
	
	public static int getDifferenceInTimeSeconds(String formattedTime1, String formattedTime2) {
		return Math.abs(getSeconds(formattedTime1) - getSeconds(formattedTime2));
	}
	
	/**
	 * Returns the current frame of the video.
	 * @param videoSeconds
	 * @param frameRate
	 * @return
	 */
	
	public static float getCurrentFrame(String formattedTime, float frameRate) {
		return getSeconds(formattedTime) * frameRate;
	}
	
}
