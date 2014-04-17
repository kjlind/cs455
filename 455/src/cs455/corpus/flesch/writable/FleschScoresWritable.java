package cs455.corpus.flesch.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * A FleschScoresWritable is a Writable for a pair of associated scores
 * (presumably for the same text). It contains a Flesch Reading Ease score and a
 * Flesch-Kincaid Grade Level score.
 * 
 * @author Kira Lindburg
 * @date Apr 16, 2014
 */
public class FleschScoresWritable implements Writable {
    private double readingEase;
    private double gradeLevel;

    /**
     * Creates a new flesch scores writable which will store the provided
     * values.
     * 
     * @param readingEase the desired Flesch Reading Ease score
     * @param gradeLevel the desired Flesch-Kincaid Grade Level
     */
    public FleschScoresWritable(double readingEase, double gradeLevel) {
        this.readingEase = readingEase;
        this.gradeLevel = gradeLevel;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        readingEase = in.readDouble();
        gradeLevel = in.readDouble();
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeDouble(readingEase);
        out.writeDouble(gradeLevel);
    }
}
