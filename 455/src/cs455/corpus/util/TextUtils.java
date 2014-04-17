package cs455.corpus.util;

/**
 * Contains a number of static utility methods which are helpful for analyzing
 * elements of text. Includes methods for purposes such as counting the number
 * of syllables in a word, stripping punctuation from a string, etc.
 * 
 * @author Kira Lindburg
 * @date Apr 16, 2014
 */
public class TextUtils {
    /**
     * Strips non-alphanumeric characters from a word.
     * 
     * @param word the string to strip
     * @return the string with all non-alphanumeric characters removed
     */
    public static String stripPunctuation(String word) {
        return word.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * Checks for the presence of sentence concluding punctuation (.!?) in the
     * word.
     * 
     * @param word the string to check
     * @return true if word contained at least one of . ! or ?, false otherwise
     */
    public static boolean containsSentencePunctuation(String word) {
        return word.contains(".") || word.contains("?") || word.contains("!");
    }

    /**
     * Counts the number of syllables in the given word.
     * 
     * @return an approximation of the number of syllables in the provided
     * string
     */
    // Adopted from function developed by Joe Basirico on StackOverflow here
    // http://stackoverflow.com/questions/405161/detecting-syllables-in-a-word
    public static int syllableCount(String word) {
        char[] vowels = { 'a', 'e', 'i', 'o', 'u', 'y' };
        String currentWord = word;
        int numVowels = 0;
        boolean lastWasVowel = false;
        for (int i = 0; i < currentWord.length(); ++i) {
            char wc = currentWord.charAt(i);
            boolean foundVowel = false;
            for (char v : vowels) {
                // don't count diphthongs
                if (v == wc && lastWasVowel) {
                    foundVowel = true;
                    lastWasVowel = true;
                    break;
                } else if (v == wc && !lastWasVowel) {
                    numVowels++;
                    foundVowel = true;
                    lastWasVowel = true;
                    break;
                }
            }

            // if full cycle and no vowel found, set lastWasVowel to false;
            if (!foundVowel)
                lastWasVowel = false;
        }
        // remove es, it's _usually? silent
        if (currentWord.length() > 2
            && currentWord.substring(currentWord.length() - 2) == "es")
            numVowels--;
        // remove silent e
        else if (currentWord.length() > 1
            && currentWord.substring(currentWord.length() - 1) == "e")
            numVowels--;

        return numVowels;
    }
}
