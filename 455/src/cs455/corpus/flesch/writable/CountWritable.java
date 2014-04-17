package cs455.corpus.flesch.writable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.Writable;

/**
 * A CountWritable is a Writable which stores a count of some type of element
 * within a text (eg. the number of words in a book). Encapsulates both the
 * value of the count and the type of element with which the count is
 * associated. The possible element types are sentence, word, and syllable.
 * 
 * @author Kira Lindburg
 * @date Apr 16, 2014
 */
public class CountWritable implements Writable {
    public static enum TYPE {
        SENTENCE, WORD, SYLLABLE
    };

    private int count;
    private TYPE type;

    /**
     * Default constructor; count will be 0 and type will be null... This is
     * only here because Hadoop wanted it. Otherwise pretty useless.
     */
    public CountWritable() {
    }

    /**
     * Creates a new CountWritable with the given count and associated type.
     */
    public CountWritable(int count, TYPE type) {
        this.count = count;
        this.type = type;
    }

    /**
     * @return the value of count
     */
    public int count() {
        return count;
    }

    /**
     * @return the value of type
     */
    public TYPE type() {
        return type;
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        count = in.readInt();
        String typeStr = in.readUTF();
        type = TYPE.valueOf(typeStr);
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeInt(count);
        String typeStr = type.name();
        out.writeUTF(typeStr);
    }

    @Override
    public String toString() {
        return count + "," + type.name();
    }
}
