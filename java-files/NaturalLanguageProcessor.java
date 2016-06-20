package Indexer;

/**
 * This class is responsible for performing the entire natural language processing of a string input that 
 * includes stemming, stop word removal and special characters removal.
 * @author Samarth
 */
public class NaturalLanguageProcessor {

	private String text;
	
	public NaturalLanguageProcessor(String content) {
		text = content;
	}
	
	public String process() {

		Stemmer stemmer = new Stemmer();
		StopWordsRemover stopWordRemover = new StopWordsRemover();
		SpecialCharRemover specialCharRemover = new SpecialCharRemover();
		
		String processedText = specialCharRemover.remove(text);
		/* String stopWordRemovedText = stopWordRemover.remove(processedText); */
		String stemmedString = stemmer.performStem(processedText);
		
		return stemmedString;
	}
	
	
	public static void main(String args[]) {
		
		String testString = "he she With google.com ... 27/04/2016 sam. U.S 6.5 玩具及個人愛好 > 洋娃娃及配件 | eBay 請啟用 JavaScript http://google.com a The core issue here is that stemming algorithms operate on a phonetic " +
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
				"texts, with the primary drawback being the space required, which is generally not an issue these days. ";

//   		String testString2 = "File1	Hadoop is the Elephant King File1	A yellow and elegant thing " +
//   				"File1	He never forgets " +
//   				"File1	Useful data or lets " +
//   				"File1	An extraneous element cling ";
   		
   		NaturalLanguageProcessor nlp = new NaturalLanguageProcessor(testString);
   		System.out.println(nlp.process());
	}
}
