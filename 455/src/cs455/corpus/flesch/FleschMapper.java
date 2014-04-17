package cs455.corpus.flesch;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import cs455.corpus.flesch.writable.CountWritable;
import cs455.corpus.flesch.writable.CountWritable.TYPE;
import cs455.corpus.util.TextUtils;

/**
 * The first pass mapper for calculating Flesch Reading Ease and Flesch-Kincaid
 * Grade Level scores for a corpus. Takes a portion of text as input. Begins
 * counting the number of sentences, words, and syllables in a particular file.
 * Outputs key-value pairs of the form Text (filename), CountWritable (count and
 * type of element counted).
 * 
 * @author Kira Lindburg
 * @date Apr 16, 2014
 */
public class FleschMapper extends
    Mapper<LongWritable, Text, Text, CountWritable> {
    String filename;

    /**
     * Stores the name of the current file we're working on.
     */
    @Override
    protected void setup(Context context) throws java.io.IOException,
        java.lang.InterruptedException {
        filename = ((FileSplit) context.getInputSplit()).getPath().toString();

    }

    /**
     * Outputs a key-value pair for each word, sentence, and number of syllables
     * per word found.
     */
    @Override
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer tok = new StringTokenizer(line);
        while (tok.hasMoreTokens()) {
            String nextWord = tok.nextToken();

            // count sentences
            if (TextUtils.containsSentencePunctuation(nextWord)) {
                context.write(new Text(filename), new CountWritable(1,
                    TYPE.SENTENCE));
            }

            // count words
            context.write(new Text(filename), new CountWritable(1, TYPE.WORD));

            // count syllables
            int syllables = TextUtils.syllableCount(nextWord);
            context.write(new Text(filename), new CountWritable(syllables,
                TYPE.SYLLABLE));
        }
    }
}
