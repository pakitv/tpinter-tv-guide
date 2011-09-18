package com.tpinter.android.tvguide.utility;

public class StringUtil {

	/**
	 * Return <code>true</code> if the text is <code>null</code> or empty
	 * string. Otherwise return <code>false</code>.
	 * 
	 * @param text
	 * @return <code>true</code> if the text is <code>null</code> or empty
	 *         string. Otherwise return <code>false</code>
	 */
	public static boolean isEmpty(String text) {
		if (text == null || text == "")
			return true;
		return false;
	}
}
