package com.vivo.asn1.pgw;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.FileUtils;
import org.snmp4j.smi.OctetString;

import cdrf_r8_org_new.cdrf_r8.CallEventRecord;
import cdrf_r8_org_new.cdrf_r8.ChangeOfCharCondition;
import cdrf_r8_org_new.cdrf_r8.IMEI;
import cdrf_r8_org_new.cdrf_r8.PGWRecord;

/**
 * 
 * A decoder for the PGW record within the CallEventRecord PDU from the ASN.1 specification "cdrf_r8_org_new"
 * So far only outputs IMEI and the traffic volumes when present
 * 
 * Author: Douglas Spadotto (06/20/2015)
 * 
 */
public class PGWDecoder {

	public static ByteArrayInputStream retrieveByteArrayInputStream(File file) {
		try {
			return new ByteArrayInputStream(FileUtils.readFileToByteArray(file));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	public static String returnLine (CallEventRecord cer) throws IOException {

		PGWRecord pgwRecord = null;
		IMEI imei = null;
		ChangeOfCharCondition volumes = null;

		int j =0;
		String strLine = null;
		String strIMEI = null;
		String toOut = "";

		if (cer.hasPGWRecord()) {
			pgwRecord = cer.getPGWRecord();

			//gets IMEI
			if (pgwRecord.hasServedIMEISV()){
				imei = (IMEI)pgwRecord.getServedIMEISV();
				OctetString ostr = OctetString.fromByteArray(imei.byteArrayValue());
				strIMEI = ostr.toString();
			}
			else
				strIMEI = "NO_IMEI";

			//gets all traffic volumes, outputs a line with IMEI and volumes
			while (j < pgwRecord.getListOfTrafficVolumes().getSize())
			{
				volumes = (ChangeOfCharCondition) pgwRecord.getListOfTrafficVolumes().getElement(j);

				strLine+= 	strIMEI + "|" + 
						volumes.getDataVolumeGPRSUplink().longValue() + "|" +
						volumes.getDataVolumeGPRSDownlink().longValue();
				j++;
				toOut += strLine + "\n";
				strLine = null;
			}
			j = 0;

		}
		
		return toOut;
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