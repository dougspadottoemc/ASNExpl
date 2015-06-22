package com.vivo.asn1.pgw;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;

import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;

import cdrf_r8_org_new.Cdrf_r8_org_new;
import cdrf_r8_org_new.cdrf_r8.CallEventRecord;


/**
 *
 * Input Format for Call Event Record
 * 
 * Reads in entire ASN.1 file as key.
 * Outputs IMEI|DataVolumeGPRSUplink|DataVolumeGPRSDownlink
 *   
 */

public class RawFileRecordReader  extends RecordReader<Text, Text>  {
	private Path path;
	private InputStream is;
	private FSDataInputStream fsin;
	
	private Text currentKey;
	private Text currentValue;
	private boolean isProcessed = false;
	
	@Override
	public boolean nextKeyValue() throws IOException, InterruptedException {
		if (isProcessed) return false;

		currentKey = new Text( path.getName() );		

		Coder coder = Cdrf_r8_org_new.getBERCoder();
		CallEventRecord callEventRecord = null;


		while (fsin.available() > 0) { 
			try{
				callEventRecord = (CallEventRecord)coder.decode(fsin, new CallEventRecord());
				currentValue.set(PGWDecoder.returnLine (callEventRecord));
			}

			catch (DecodeFailedException | DecodeNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
		return true;
	}

	@Override
	public Text getCurrentKey() throws IOException, InterruptedException {
		return currentKey;
	}

	@Override
	public Text getCurrentValue() throws IOException, InterruptedException {
		return currentValue;
	}

	@Override
	public float getProgress() throws IOException, InterruptedException {
		return isProcessed ? 1 : 0;
	}

	@Override
	public void initialize(InputSplit split, TaskAttemptContext context)
			throws IOException, InterruptedException {
		Configuration conf = context.getConfiguration();
		path = ((FileSplit) split).getPath();
		FileSystem fs = path.getFileSystem(conf);
		fsin = fs.open(path);

	}

	@Override
	public void close() throws IOException {
		is.close();
		if (fsin!=null) fsin.close();
	}

} 
