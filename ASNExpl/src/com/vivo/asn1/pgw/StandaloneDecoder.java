package com.vivo.asn1.pgw;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.snmp4j.smi.OctetString;

import com.oss.asn1.Coder;
import com.oss.asn1.DecodeFailedException;
import com.oss.asn1.DecodeNotSupportedException;
import cdrf_r8_org_new.Cdrf_r8_org_new;
import cdrf_r8_org_new.cdrf_r8.CallEventRecord;
import cdrf_r8_org_new.cdrf_r8.ChangeOfCharCondition;
import cdrf_r8_org_new.cdrf_r8.IMEI;
import cdrf_r8_org_new.cdrf_r8.PGWRecord;

/**
 * 
 * A standalone decoder for the example ASN.1 specification "cdrf_r8_org_new"
 * So far only outputs IMEI and the traffic volumes available for each PDU
 * Author: Douglas Spadotto (06/20/2015)
 * 
 */
public class StandaloneDecoder {

	public static ByteArrayInputStream retrieveByteArrayInputStream(File file) {
		try {
			return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public StandaloneDecoder(String filename) throws IOException {

		File fin = new File(filename);
		ByteArrayInputStream source = retrieveByteArrayInputStream(fin);

		Coder coder = Cdrf_r8_org_new.getBERCoder();		
		
		CallEventRecord callEventRecord = null;
		PGWRecord pgwRecord = null;
		IMEI imei = null;
		ChangeOfCharCondition volumes = null;

		int j =0;
		
		while (source.available() > 0) { 
			try{
				callEventRecord = (CallEventRecord)coder.decode(source, new CallEventRecord());
				if (callEventRecord.hasPGWRecord()) {
					pgwRecord = callEventRecord.getPGWRecord();

					//prints IMEI
					if (pgwRecord.hasServedIMEISV()){
						imei = (IMEI)pgwRecord.getServedIMEISV();
						OctetString ostr = OctetString.fromByteArray(imei.byteArrayValue());
						System.out.println("IMEI: " + ostr.toString());
					}
					else
						System.out.println("NO IMEI!!!");

					//lists all traffic volumes
					while (j < pgwRecord.getListOfTrafficVolumes().getSize())
					{
						volumes = (ChangeOfCharCondition) pgwRecord.getListOfTrafficVolumes().getElement(j);
					
						System.out.println("UpLink: " + volumes.getDataVolumeGPRSUplink().longValue());
						System.out.println("Downlink: " + volumes.getDataVolumeGPRSDownlink().longValue());
						j++;
					}
					j = 0;
				}
				source.close();
			}
			
			catch (DecodeFailedException | DecodeNotSupportedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		if (args.length < 1 ) {
			System.out.println("Missing a filename. Exiting.");
			System.exit(1);
		}

		String filename = args[0];
		try {
			@SuppressWarnings("unused")
			StandaloneDecoder mainObj = new StandaloneDecoder(filename);
		} catch (IOException ioe) {
			String errmessage="ERROR. EXITING. Exception ( "+ioe.getClass().getName()+" ) : "+ioe.getMessage();
			System.out.println(errmessage);
			ioe.printStackTrace();
			System.exit(1);
		}
	}

}