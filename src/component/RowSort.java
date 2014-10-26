package component;

import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import operation.MediaTimer;

/**
 * Used to sort the data values in the JTable. It accepts strings in the format hh:mm:ss or else it will throw an exception. <br/>
 * User in tables found in : {@link panel.TextEditPanel} {@link panel.VideoFilterPanel} {@link panel.SubtitlePanel}
 * @author chang
 *
 */

public class RowSort implements Comparator<Object> {

	@Override
	public int compare(Object o1, Object o2) {
		// Vector objects come in the form [..., ...., ...]
		String[] split1 = o1.toString().split(",");
		String[] split2 = o2.toString().split(",");

		int firstTime = 0, secondTime = 0;
		Matcher m;
		Pattern p = Pattern.compile("\\d\\d:\\d\\d:\\d\\d");
		m = p.matcher(split1[0]);
		
		// Check the start times.
		
		if (m.find()) {
			firstTime = MediaTimer.getSeconds(m.group());
		}

		m = p.matcher(split2[0]);
		if (m.find()) {
			secondTime = MediaTimer.getSeconds(m.group());
		}

		if (firstTime > secondTime) {
			return 1;
		} else if (secondTime > firstTime) {
			return -1;
		} else {
			// Start times were the same. Check the end times.
			m = p.matcher(split1[1]);

			if (m.find()) {
				firstTime = MediaTimer.getSeconds(m.group());
			}

			m = p.matcher(split2[1]);

			if (m.find()) {
				secondTime = MediaTimer.getSeconds(m.group());
			}

			if (firstTime > secondTime) {
				return 1;
			} else if (secondTime > firstTime) {
				return -1;
			}
			// else
			return 0;
		}
	}

}
