package com.kd7uiy.trustedQsl;

/*
 * The MIT License (MIT)

Copyright (c) 2014 Ben Pearson, http://www.kd7uiy.com

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class WriteGabbi {

	private static final String MESSAGE_DIGEST_FORMAT = "SHA-1";
	private static final boolean PRETTY_MODE = true;
	private String mCallSign;
	private SimpleDateFormat mTimeFormat;
	private PrivateKey mKey;
	private static SimpleDateFormat mIso8601Format;
	private static SimpleDateFormat mDateFormat;

	abstract String getApplicationName(); // Returns the application name

	abstract Station[] getStations(); // Returns all possible stations.

	abstract QsoData[] getQsoData(Station station); // Returns all Qso Data for
													// a given station

	abstract void publishProgress(int numComplete); // Optional, publishes the
													// progress of results.

	public WriteGabbi(PrivateKey key) {
		mKey=key;
		mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		mTimeFormat = new SimpleDateFormat("HH:mm:ssZ");
		mIso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
		mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		mTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		mIso8601Format.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	// This function should be called to write the GAbbI file.
	public void write(OutputStream os) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(os);
		writeHeader(out);
		int stationUID = 1;
		Station[] stations = getStations();
		for (Station station : stations) {
			station.uid = stationUID;
			writeStation(out, station);
			stationUID++;
		}
		int complete = 0;
		for (Station station : stations) {
			for (QsoData data : getQsoData(station)) {
				writeQso(out, data, station.uid);
				publishProgress(++complete);
			}
		}
	}

	private void writeHeader(BufferedOutputStream os) throws IOException {
		writeTag(os, "REC_TYPE", "tHEADER");
		writeTag(os, "CATEGORY", "tQSL");
		writeTag(os, "GabbI_CREATED_BY", getApplicationName());
		writeTag(os, "GAbbI_CREATED_ON", mIso8601Format.format(new Date()));
		writeTag(os, "GabbI_MESSAGE_DIGEST", MESSAGE_DIGEST_FORMAT);
		writeTag(os, "GAbbI_SENDER", mCallSign); // This is the user's call sign
		writeTag(os, "GAbbI_SIGN_ALGORITHM", mKey.getAlgorithm()); // This should be
																// in the
																// certificate
		writeTag(os, "GAbbI_VERSION", "0.25");
		os.write("<EOR>\n".getBytes());
	}

	private void writeStation(BufferedOutputStream os, Station station)
			throws IOException {
		writeTag(os, "REC_TYPE", "tSTATION");
		writeTag(os, "CALL", mCallSign);
		writeTag(os, "CONT", station.continent); // Look up continent
		writeTag(os, "CQZ", station.cqz); // Look up CQ Zone
		writeTag(os, "DXCC", "" + station.dxcc); // Look this up, should be a
													// number
		writeTag(os, "EMAIL_ADDRESS", station.emailAddress);
		writeTag(os, "GRIDSQUARE", station.grid);
		writeTag(os, "IOTA", station.iota); // If/When implemented
		writeTag(os, "ITUZ", station.ituz); // ITU Zone
		writeTag(os, "POSTAL_CODE", station.zipCode); // Zip Code
		if (station.satName != null) {
			writeTag(os, "SAT_NAME", station.satName);
			writeTag(os, "SAT_MODE", station.mode);
		}
		writeTag(os, "STATION_UID", "" + station.uid);
		writeTag(os, "SUB_GOV1", station.state); // Look up to standard
		writeTag(os, "US_COUNTY", station.usCounty); // Look up to standard
		os.write("<EOR>\n".getBytes());
	}

	private void writeQso(BufferedOutputStream os, QsoData data, int stationUID)
			throws IOException {
		StringBuilder signatureData = new StringBuilder();

		// Note, these must remain in alphabetic order, except REC_TYPE and LoTW_SIGN
		writeTag(os, "REC_TYPE", "tCONTACT");
		signatureData.append(writeTag(os, "CALL", data.call));
		signatureData.append(writeTag(os, "BAND", HamBand.getText(HamBand.findBand(data.freq))));
		signatureData.append(writeTag(os, "FREQ", "" + data.freq));
		signatureData.append(writeTag(os, "MODE", data.mode));
		signatureData.append(writeTag(os, "QSO_DATE", mDateFormat.format(data.dateTime)));
		signatureData.append(writeTag(os, "QSO_TIME", mTimeFormat.format(data.dateTime)));
		signatureData.append(writeTag(os, "Station_GUID", "" + stationUID));
		writeTag(os, "LoTW_Sign", signQso(signatureData.toString()));
		os.write("<EOR>\n".getBytes());
	}

	// Verifies that the station data meets the desired file format
	public boolean verifyStation(Station station) {
		// TODO Fix this problem
		return true;
	}

	// Verifies that Qso data is of an acceptable format
	public boolean verifyQso(QsoData station) {
		// TODO Perform verification
		return true;
	}

	// Signs the individual Qso.
	private String signQso(String data) {
		String output=data;
		try {
			Signature signature = Signature.getInstance(MESSAGE_DIGEST_FORMAT);
			signature.initSign(mKey);
			signature.update(data.getBytes());
			output= new String(Base64Coder.encode(signature.sign()));
		} catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException e) {
			e.printStackTrace();
		}
		return output;
	}

	private static String writeTag(BufferedOutputStream os, String name,
			String data) throws IOException {
		if (data != null) {
			int length = data.length();
			if (length > 0) {
				String output = String.format(Locale.US, "<%s:%d>%s", name,
						length, data);
				os.write(output.getBytes());
				if (PRETTY_MODE) {
					os.write("\n".getBytes());
				}
			}
			return data;
		}
		return "";
	}
}
