package operation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Returns file path string from given mrl.
 * 
 */

public class MRLFilename {
	public static String getFilename(String mrl) {
		Pattern pattern = Pattern.compile("/[^/].*");
		Matcher p = pattern.matcher(mrl);
		
		if (p.find()) {
			return p.group();
		}
		return null;
	}
}
