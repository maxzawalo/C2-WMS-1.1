package maxzawalo.c2.base.utils;

import java.awt.FontMetrics;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Globally available utility classes, mostly for string manipulation.
 * 
 * @author Jim Menard, <a href="mailto:jimm@io.com">jimm@io.com</a>
 */
public class StringUtils {
	/**
	 * Returns an array of strings, one for each line in the string after it has
	 * been wrapped to fit lines of <var>maxWidth</var>. Lines end with any of
	 * cr, lf, or cr lf. A line ending at the end of the string will not output
	 * a further, empty string.
	 * <p>
	 * This code assumes <var>str</var> is not <code>null</code>.
	 * 
	 * @param str
	 *            the string to split
	 * @param fm
	 *            needed for string width calculations
	 * @param maxWidth
	 *            the max line width, in points
	 * @return a non-empty list of strings
	 */
	public static List wrap(String str, FontMetrics fm, int maxWidth) {
		List lines = splitIntoLines(str);
		if (lines.size() == 0)
			return lines;

		ArrayList strings = new ArrayList();
		for (Iterator iter = lines.iterator(); iter.hasNext();)
			wrapLineInto((String) iter.next(), strings, fm, maxWidth);
		return strings;
	}

	/**
	 * Given a line of text and font metrics information, wrap the line and add
	 * the new line(s) to <var>list</var>.
	 * 
	 * @param line
	 *            a line of text
	 * @param list
	 *            an output list of strings
	 * @param fm
	 *            font metrics
	 * @param maxWidth
	 *            maximum width of the line(s)
	 */
	public static void wrapLineInto(String line, List list, FontMetrics fm, int maxWidth) {
		int len = line.length();
		int width;
		int maxIteration = 3;
		while (len > 0 && (width = fm.stringWidth(line)) > maxWidth) {
			// Guess where to split the line. Look for the next space before
			// or after the guess.
			int guess = len * maxWidth / width;
			String before = line.substring(0, guess).trim();

			width = fm.stringWidth(before);
			int pos;
			if (width > maxWidth) // Too long
				pos = findBreakBefore(line, guess);
			else { // Too short or possibly just right
				pos = findBreakAfter(line, guess);
				if (pos != -1) { // Make sure this doesn't make us too long
					before = line.substring(0, pos).trim();
					if (fm.stringWidth(before) > maxWidth)
						pos = findBreakBefore(line, guess);
				}
			}
			if (pos == -1)
				pos = guess; // Split in the middle of the word

			list.add(line.substring(0, pos).trim());
			line = line.substring(pos).trim();
			len = line.length();

			maxIteration--;
			if (maxIteration <= 0) {
				break;

			}
		}
		if (len > 0)
			list.add(line);
	}

	/**
	 * Returns the index of the first whitespace character or '-' in
	 * <var>line</var> that is at or before <var>start</var>. Returns -1 if no
	 * such character is found.
	 * 
	 * @param line
	 *            a string
	 * @param start
	 *            where to star looking
	 */
	public static int findBreakBefore(String line, int start) {
		for (int i = start; i >= 0; --i) {
			char c = line.charAt(i);
			if (Character.isWhitespace(c) || c == '-')
				return i;
		}
		return -1;
	}

	/**
	 * Returns the index of the first whitespace character or '-' in
	 * <var>line</var> that is at or after <var>start</var>. Returns -1 if no
	 * such character is found.
	 * 
	 * @param line
	 *            a string
	 * @param start
	 *            where to star looking
	 */
	public static int findBreakAfter(String line, int start) {
		int len = line.length();
		for (int i = start; i < len; ++i) {
			char c = line.charAt(i);
			if (Character.isWhitespace(c) || c == '-')
				return i;
		}
		return -1;
	}

	/**
	 * Returns an array of strings, one for each line in the string. Lines end
	 * with any of cr, lf, or cr lf. A line ending at the end of the string will
	 * not output a further, empty string.
	 * <p>
	 * This code assumes <var>str</var> is not <code>null</code>.
	 * 
	 * @param str
	 *            the string to split
	 * @return a non-empty list of strings
	 */
	public static List splitIntoLines(String str) {
		ArrayList strings = new ArrayList();

		int len = str.length();
		if (len == 0) {
			strings.add("");
			return strings;
		}

		int lineStart = 0;

		for (int i = 0; i < len; ++i) {
			char c = str.charAt(i);
			if (c == '\r') {
				int newlineLength = 1;
				if ((i + 1) < len && str.charAt(i + 1) == '\n')
					newlineLength = 2;
				strings.add(str.substring(lineStart, i));
				lineStart = i + newlineLength;
				if (newlineLength == 2) // skip \n next time through loop
					++i;
			} else if (c == '\n') {
				strings.add(str.substring(lineStart, i));
				lineStart = i + 1;
			}
		}
		if (lineStart < len)
			strings.add(str.substring(lineStart));

		return strings;
	}

	public static double LevenshteinDistance(String s1, String s2) {
		if (s1 == null) {
			throw new NullPointerException("s1 must not be null");
		}

		if (s2 == null) {
			throw new NullPointerException("s2 must not be null");
		}

		if (s1.equals(s2)) {
			return 0;
		}

		if (s1.length() == 0) {
			return s2.length();
		}

		if (s2.length() == 0) {
			return s1.length();
		}

		// create two work vectors of integer distances
		int[] v0 = new int[s2.length() + 1];
		int[] v1 = new int[s2.length() + 1];
		int[] vtemp;

		// initialize v0 (the previous row of distances)
		// this row is A[0][i]: edit distance for an empty s
		// the distance is just the number of characters to delete from t
		for (int i = 0; i < v0.length; i++) {
			v0[i] = i;
		}

		for (int i = 0; i < s1.length(); i++) {
			// calculate v1 (current row distances) from the previous row v0
			// first element of v1 is A[i+1][0]
			// edit distance is delete (i+1) chars from s to match empty t
			v1[0] = i + 1;

			// use formula to fill in the rest of the row
			for (int j = 0; j < s2.length(); j++) {
				int cost = 1;
				if (s1.charAt(i) == s2.charAt(j)) {
					cost = 0;
				}
				v1[j + 1] = Math.min(v1[j] + 1, // Cost of insertion
						Math.min(v0[j + 1] + 1, // Cost of remove
								v0[j] + cost)); // Cost of substitution
			}

			// copy v1 (current row) to v0 (previous row) for next iteration
			// System.arraycopy(v1, 0, v0, 0, v0.length);

			// Flip references to current and previous row
			vtemp = v0;
			v0 = v1;
			v1 = vtemp;
		}

		double dist = (s2.length() + s1.length()) / v0[s2.length()] / 2;

		System.out.println(s1 + "|" + s2 + "|" + dist);
		return dist;
	}

	/**
	   * Get text between two strings. Passed limiting strings are not 
	   * included into result.
	   *
	   * @param text     Text to search in.
	   * @param textFrom Text to start cutting from (exclusive).
	   * @param textTo   Text to stop cuutting at (exclusive).
	   */
	  public static String getBetweenStrings(
	    String text,
	    String textFrom,
	    String textTo) {

	    String result = "";

	    // Cut the beginning of the text to not occasionally meet a      
	    // 'textTo' value in it:
	    result =
	      text.substring(
	        text.indexOf(textFrom) + textFrom.length(),
	        text.length());

	    // Cut the excessive ending of the text:
	    result =
	      result.substring(
	        0,
	        result.indexOf(textTo));

	    return result;
	  }
	
}
