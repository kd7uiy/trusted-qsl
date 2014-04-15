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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.TimeZone;

public abstract class WriteGabbi {

	private static final String MESSAGE_DIGEST_FORMAT = "MD5withRSA";
	private static final boolean PRETTY_MODE = true;
	private String mCallSign;
	private SimpleDateFormat mTimeFormat;
	private PrivateKey mKey;
	private Certificate mCertificate;
	private static SimpleDateFormat mIso8601Format;
	private static SimpleDateFormat mDateFormat;

	protected abstract String getApplicationName(); // Returns the application
													// name

	protected abstract Station[] getStations(); // Returns all possible
												// stations.

	protected abstract QsoData[] getQsoData(Station station); // Returns all Qso
																// Data for
	// a given station

	protected abstract void publishProgress(int numComplete); // Optional,
																// publishes the
	// progress of results.

	public static KeyStore getKeystore(String filename, String password) {
		KeyStore p12 = null;
		try {
			p12 = KeyStore.getInstance("pkcs12");
			p12.load(new FileInputStream(filename), password.toCharArray());

//			Enumeration<String> e = p12.aliases();
//			while (e.hasMoreElements()) {
//				String alias = (String) e.nextElement();
//				System.out.println("Alias- " + alias);
//				X509Certificate c = (X509Certificate) p12.getCertificate(alias);
//				System.out.println(c.toString());
//
//				Principal subject = c.getSubjectDN();
//				String subjectArray[] = subject.toString().split(",");
//				for (String s : subjectArray) {
//					String[] str = s.trim().split("=");
//					String key = str[0];
//					String value = str[1];
//					System.out.println(key + " - " + value);
//				}
//			}
		} catch (NoSuchAlgorithmException | CertificateException | IOException
				| KeyStoreException e1) {
			e1.printStackTrace();
		}
		return p12;
	}

	public WriteGabbi(PrivateKey key, Certificate certificate) {
		mCertificate = certificate;
		mKey = key;
		setUpSimpleDateTime();
	}

	private void setUpSimpleDateTime() {
		if (mDateFormat == null) {
			mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			mTimeFormat = new SimpleDateFormat("HH:mm:ss'Z'");
			mIso8601Format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS'Z'");
			mDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			mTimeFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
			mIso8601Format.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
	}

	// This function should be called to write the GAbbI file.
	public void write(OutputStream os) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(os);
		out.write("Produced by KD7UIY's Trusted QSL Java Library\n\n".getBytes());
		writeHeader(out);
		int stationUID = 1;
		Station[] stations = getStations();
		for (Station station : stations) {
			station.uid = stationUID;
			station.baseSig = writeStation(out, station);
			stationUID++;
		}
		int complete = 0;
		for (Station station : stations) {
			for (QsoData data : getQsoData(station)) {
				writeQso(out, data, station);
				publishProgress(++complete);
			}
		}
		out.close();
	}

	private void writeHeader(BufferedOutputStream os) throws IOException {
		writeTag(os, "REC_TYPE", "tCERT");
		writeTag(os, "CERT_UID", "1"); // Assuming only 1 certificate per file
		writeTag(os, "CERTIFICATE", getCertificate());
		eor(os);

		writeTag(os, "REC_TYPE", "tHEADER");
		writeTag(os, "CATEGORY", "tQSL");
		writeTag(os, "GabbI_CREATED_BY", getApplicationName());
		writeTag(os, "GAbbI_CREATED_ON", mIso8601Format.format(new Date()));
		writeTag(os, "GabbI_MESSAGE_DIGEST", MESSAGE_DIGEST_FORMAT);
		writeTag(os, "GAbbI_SENDER", mCallSign); // This is the user's call sign
		writeTag(os, "GAbbI_SIGN_ALGORITHM", mKey.getAlgorithm()); // This
																	// should be
																	// in the
																	// certificate
		writeTag(os, "GAbbI_VERSION", "0.25");
		eor(os);
	}

	private String getCertificate() {
		try {
			return new String(Base64Coder.encode(mCertificate.getEncoded()));
		} catch (CertificateEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private String writeStation(BufferedOutputStream os, Station station)
			throws IOException {
		writeTag(os, "REC_TYPE", "tSTATION");
		writeTag(os, "STATION_UID", "" + station.uid);
		writeTag(os, "CERT_UID", "1");
		StringBuilder sb = new StringBuilder();
		// Preserve alphabetic order from this point forward!
		sb.append(writeTag(os, "CALL", mCallSign));
		sb.append(writeTag(os, "CONT", station.continent));
		sb.append(writeTag(os, "CQZ", station.cqz));
		sb.append(writeTag(os, "DXCC", "" + station.dxcc));
		sb.append(writeTag(os, "EMAIL_ADDRESS", station.emailAddress));
		sb.append(writeTag(os, "GRIDSQUARE", station.grid));
		sb.append(writeTag(os, "IOTA", station.iota));
		sb.append(writeTag(os, "ITUZ", station.ituz));
		sb.append(writeTag(os, "POSTAL_CODE", station.zipCode));
		if (station.satName != null) {
			sb.append(writeTag(os, "SAT_NAME", station.satName));
			sb.append(writeTag(os, "SAT_MODE", station.mode));
		}
		sb.append(writeTag(os, "US_COUNTY", station.usCounty));
		sb.append(writeTag(os, "US_STATE", station.usState));
		eor(os);
		return sb.toString();
	}
	private void eor(BufferedOutputStream os) throws IOException {
		os.write("<EOR>\n".getBytes());
		if (PRETTY_MODE) {
			os.write("\n".getBytes());
		}
	}

	private void writeQso(BufferedOutputStream os, QsoData data, Station station)
			throws IOException {
		StringBuilder signatureData = new StringBuilder(station.baseSig);

		// Note, these must remain in alphabetic order, except REC_TYPE and
		// LoTW_SIGN
		writeTag(os, "REC_TYPE", "tCONTACT");
		writeTag(os, "STATION_UID", "" + station.uid);
		signatureData.append(writeTag(os, "CALL", data.call));
		signatureData.append(writeTag(os, "BAND",
				HamBand.getText(HamBand.findBand(data.freq * 1000))));
		signatureData.append(writeTag(os, "FREQ", "" + (data.freq * 1000)));
		signatureData.append(writeTag(os, "MODE", data.mode));
		signatureData.append(writeTag(os, "QSO_DATE",
				mDateFormat.format(data.dateTime)));
		signatureData.append(writeTag(os, "QSO_TIME",
				mTimeFormat.format(data.dateTime)));
		writeTag(os, "LoTW_Sign", signQso(signatureData.toString()));
		writeTag(os, "SIGNDATA", signatureData.toString());
		eor(os);
	}

	// Verifies that the station data meets the desired file format
	public boolean verifyStation(Station station) {
		// TODO Add implementation
		return true;
	}

	// Verifies that Qso data is of an acceptable format
	public boolean verifyQso(QsoData station) {
		// TODO Perform verification
		return true;
	}

	// Signs the individual Qso.
	private String signQso(String data) {
		String output = data;
		try {
			Signature signature = Signature.getInstance(MESSAGE_DIGEST_FORMAT);
			signature.initSign(mKey);
			signature.update(data.getBytes());
			output = new String(Base64Coder.encode(signature.sign()));
		} catch (NoSuchAlgorithmException | InvalidKeyException
				| SignatureException e) {
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
