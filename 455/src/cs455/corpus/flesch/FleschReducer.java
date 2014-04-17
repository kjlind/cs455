package cs455.corpus.flesch;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import cs455.corpus.flesch.writable.CountWritable;
import cs455.corpus.flesch.writable.FleschScoresWritable;

/**
 * First pass reducer for calculating Flesch Reading Ease and Flesch-Kincaid
 * Grade Level scores for a corpus of texts. Takes inputs of CountWritables
 * which contain numbers of sentences, words, and syllables for a given file
 * (specified by the key). Sums the counts for each type to get the total number
 * of sentences, words, and syllables in the text, then calculates the reading
 * ease and grade level scores using these values. Outputs a single key, value
 * pair containing the filename and the scores.
 * 
 * @author Kira Lindburg
 * @date Apr 17, 2014
 */
public class FleschReducer extends
    Reducer<Text, CountWritable, Text, FleschScoresWritable> {
    public void reduce(Text key, Iterable<CountWritable> values, Context context)
        throws IOException, InterruptedException {
        int sentences = 0, words = 0, syllables = 0;
        for (CountWritable next : values) {
            switch (next.type()) {
            case SENTENCE:
                sentences += next.count();
            case WORD:
                words += next.count();
            case SYLLABLE:
                syllables += next.count();
            }
        }
        double readingEase = calculateReadingEase(sentences, words, syllables);
        double gradeLevel = calculateGradeLevel(sentences, words, syllables);
        context.write(key, new FleschScoresWritable(readingEase, gradeLevel));
    }

    /**
     * Calculates the Flesch Reading Ease score for some text based upon the
     * provided values.
     * 
     * @param sentences the number of sentences in the text
     * @param words the number of words in the text
     * @param syllables the number of syllables in the text
     * @return the calculated score
     */
    private double calculateReadingEase(double sentences, double words,
        double syllables) {
        return 206.835 - 1.015 * (words / sentences) - 84.6
            * (syllables / words);
    }

    /**
     * Calculates the Flesch-Kincaid Reading Level for some text based upon the
     * provided values.
     * 
     * @param sentences the number of sentences in the text
     * @param words the number of words in the text
     * @param syllables the number of syllables in the text
     * @return the calculated score
     */
    private double calculateGradeLevel(double sentences, double words,
        double syllables) {
        return 0.39 * (words / sentences) + 11.8 * (syllables / words) - 15.59;
    }
}
