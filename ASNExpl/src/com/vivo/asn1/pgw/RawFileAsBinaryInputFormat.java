package com.vivo.asn1.pgw;
import java.io.IOException;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.JobContext;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

/**
 *
 * Input Format for PGW CDR
 * Reads in entire ASN.1 file as key. 
 */

public class RawFileAsBinaryInputFormat extends FileInputFormat<Text, Text> {
	@Override
	protected boolean isSplitable(JobContext context, Path filename){
		return false;
	}

	@Override
	public RecordReader<Text, Text> createRecordReader(
			InputSplit split, TaskAttemptContext context) throws IOException,
			InterruptedException {
		return new RawFileRecordReader();
	} 
}
