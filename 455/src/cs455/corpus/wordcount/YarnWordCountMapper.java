package cs455.corpus.wordcount;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class YarnWordCountMapper extends
    Mapper<LongWritable, Text, Text, IntWritable> {
    public void map(LongWritable key, Text value, Context context)
        throws IOException, InterruptedException {
        String line = value.toString();
        StringTokenizer tok = new StringTokenizer(line);
        while (tok.hasMoreTokens()) {
            context.write(new Text(tok.nextToken()), new IntWritable(1));
        }
    } // end map
} // end mapper class
