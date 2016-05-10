package Indexer;

import java.util.regex.Pattern;

/**
 * This class is responsible for removal of all the characters except characters, numerals, decimals
 * and abbreviations
 * @author Samarth
 *
 */
public class SpecialCharRemover {

	Pattern p1 = Pattern.compile("can't");
	Pattern p2 = Pattern.compile("n't");
	Pattern p3 = Pattern.compile("'m");
	Pattern p4 = Pattern.compile("'ll");
	Pattern p5 = Pattern.compile("'ve");
	Pattern p6 = Pattern.compile("'s");
	Pattern p7 = Pattern.compile("'d");
	Pattern p8 = Pattern.compile("\\.\\s+");
	Pattern p9 = Pattern.compile("\\.\\.+");
	Pattern p10 = Pattern.compile("[^a-zA-Z0-9. ]");
	
	public String remove(String inputString) {
		
		// Removing contractions
		inputString = p1.matcher(inputString).replaceAll("cannot");
		inputString = p2.matcher(inputString).replaceAll(" not");
		inputString = p3.matcher(inputString).replaceAll("");
		inputString = p4.matcher(inputString).replaceAll(" will");
		inputString = p5.matcher(inputString).replaceAll(" have");
		inputString = p6.matcher(inputString).replaceAll("");
		inputString = p7.matcher(inputString).replaceAll("");
		inputString = p8.matcher(inputString).replaceAll(" ");
		inputString = p9.matcher(inputString).replaceAll(" ");
		inputString = p10.matcher(inputString).replaceAll(" ");
		inputString = inputString.trim();
		
		return inputString;
	}
	
	public static void main(String args[]) {
		
		SpecialCharRemover scr = new SpecialCharRemover();
		String testString = "...... google.com sam. U.S 6.5 can't aren't a The core issue here is that stemming algorithms operate on a phonetic " +
				"basis purely based on the language's spelling rules with no actual understanding of the language " +
				"they're working with. To produce real words, you'll probably have to merge the stemmer's output with " +
				"some form of lookup function to convert the stems back to real words. I can basically see two potential " +
				"ways to do this: Locate or create a large dictionary which maps each possible stem back to an actual " +
				"word. (e.g., communiti -> community) Create a function which compares each stem to a list of the " +
				"words that were reduced to that stem and attempts to determine which is most similar. " +
				"(e.g., comparing \"communiti\" against \"community\" and \"communities\" in such a way that " +
				"\"community\" will be recognized as the more similar option) Personally, I think the way I would do " +
				"it would be a dynamic form of #1, building up a custom dictionary database by recording every word " +
				"examined along with what it stemmed to and then assuming that the most common word is the one that " +
				"should be used. (e.g., If my body of source text uses \"communities\" more often than \"community\", " +
				"then map communiti -> communities.) A dictionary-based approach will be more accurate in " +
				"general and building it based on the stemmer input will provide results customized to your " +
				"texts, with the primary drawback being the space required, which is generally not an issue these days.";
		System.out.println(scr.remove(testString));
	}
}
