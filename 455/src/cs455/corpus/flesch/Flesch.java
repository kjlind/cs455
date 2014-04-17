package cs455.corpus.flesch;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import cs455.corpus.flesch.writable.CountWritable;

/**
 * Calculates Flesch Reading Ease and Flesch-Kincaid Grade Level scores for a
 * corpus of texts using MapReduce.
 * 
 * TODO: documentation about how to run it and what output looks like here
 * 
 * @author Kira Lindburg
 * @date Apr 16, 2014
 */
public class Flesch {
    public static void main(String[] args) throws IOException,
        ClassNotFoundException, InterruptedException {
        Job job = Job.getInstance(new Configuration());
        job.setJarByClass(Flesch.class);
        job.setJobName("Flesch");
        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.setMapperClass(FleschMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(CountWritable.class);
        // job.setNumReduceTasks(0);
        // job.setCombinerClass(FleschCombiner.class);
        job.setReducerClass(FleschCombiner.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(CountWritable.class);
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
