package cs455.corpus.flesch;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import cs455.corpus.flesch.writable.CountWritable;
import cs455.corpus.flesch.writable.CountWritable.TYPE;

/**
 * The FleschCombiner reduces the intermediate output from a FleschMapper by
 * summing up all counts of each type it receives for a given key. Produces
 * intermediate output consisting of one CountWritable for each type of element
 * (paired with the key which it was passed). For example, if the values
 * contained {1,WORD 1,SENTENCE 1,WORD 1,WORD 1,SENTENCE}, the combiner will
 * output 3,WORD and 2,SENTENCE.
 * 
 * @author Kira Lindburg
 * @date Apr 17, 2014
 */
public class FleschCombiner extends
    Reducer<Text, CountWritable, Text, CountWritable> {
    @Override
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
        context.write(key, new CountWritable(sentences, TYPE.SENTENCE));
        context.write(key, new CountWritable(words, TYPE.WORD));
        context.write(key, new CountWritable(syllables, TYPE.SYLLABLE));
    }
}
