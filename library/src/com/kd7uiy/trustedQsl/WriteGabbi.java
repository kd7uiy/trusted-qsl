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
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.zip.GZIPOutputStream;

import com.kd7uiy.trustedQsl.HamBand.Band;
import com.kd7uiy.trustedQsl.MultipartUtility.OutputStreamer;

public abstract class WriteGabbi {
	private static final boolean PRETTY_MODE = true;
	private String mMessageDigestFormat = "SHA1withRSA";
	private SimpleDateFormat mTimeFormat;
	private PrivateKey mKey;
	private X509Certificate mCertificate;
	private static SimpleDateFormat mIso8601Format;
	private static SimpleDateFormat mDateFormat;

	protected boolean mValidate = true; // If false, won't validate any
										// Qso/Station data

	// Returns the application name
	// protected abstract String getApplicationName();

	// Returns all possible stations.
	protected abstract Station[] getStations();

	// Returns all Qso Data for a given station
	protected abstract QsoData[] getQsoData(Station station);

	// Optional, publishes the progress of results.
	protected abstract void publishProgress(int numComplete);

	public WriteGabbi(KeyStore keystore, char[] password, String alias)
			throws KeyStoreException, NoSuchAlgorithmException,
			UnrecoverableEntryException {
		init(keystore, password, alias);
	}
	
	public WriteGabbi(KeyStore keystore, char[] password)
			throws KeyStoreException, NoSuchAlgorithmException,
			UnrecoverableEntryException {
		Enumeration<String> e = keystore.aliases();
		init(keystore, password, e.nextElement());
	}

	private void init(KeyStore keystore, char[] password, String alias)
			throws KeyStoreException, NoSuchAlgorithmException,
			UnrecoverableEntryException {
		mCertificate = (X509Certificate) keystore.getCertificate(alias);
		KeyStore.ProtectionParameter protParam = new KeyStore.PasswordProtection(
				password);
		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keystore
				.getEntry(alias, protParam);
		mKey = (PrivateKey) pkEntry.getPrivateKey();
		// mMessageDigestFormat= mCertificate.getSigAlgName();
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

	public boolean writeToLotw(String filename) throws IOException {
		if (getStations().length<=0) {
			//No stations, so no need to do an upload.
			return true;
		}
		MultipartUtility multipart = new MultipartUtility(
				"https://p1k.arrl.org/lotw/upload", "UTF-8");
		multipart.addHeaderField("User-Agent", "TrustedQslJava");
		multipart.addHeaderField("Test-Header", "Header-Value");

		multipart.addFilePartOutputStream("upfile", filename,
				new OutputStreamer() {
					@Override
					public void writeStream(OutputStream outputStream)
							throws IOException {
						write(new GZIPOutputStream(outputStream));
					}
				},"text/plain");

		List<String> response = multipart.finish();
		for (String line : response) {
			if (line.startsWith("<!-- .UPL.")) {
				if (line.contains("accepted")) {
					return true;
				} else {
					return false;
				}
			}
//			System.out.println(line);
		}

		return false;
	}

	// This function should be called to write the GAbbI file.
	public void write(OutputStream os) throws IOException {
		BufferedOutputStream out = new BufferedOutputStream(os);
		out.write("Produced by KD7UIY's Trusted QSL Java Library\n\n"
				.getBytes());
		writeHeader(out);
		int stationUID = 1;
		Station[] stations = getStations();
		for (Station station : stations) {
			if (mValidate && !isValid(station)) {
				throw new IllegalArgumentException("Invalid Station " + station);
			}
			station.uid = stationUID;
			station.baseSig = writeStation(out, station);
			stationUID++;
		}
		int complete = 0;
		for (Station station : stations) {
			for (QsoData qso : getQsoData(station)) {
				if (mValidate && !isValid(qso)) {
					throw new IllegalArgumentException("Invalid Qso " + qso);
				}
				writeQso(out, qso, station);
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
		sb.append(writeTag(os, "CA_PROVIDENCE", station.caProvidence));
		writeTag(os, "CALL", station.call);
		writeTag(os, "CL_CITY", "" + station.clCity);
		writeTag(os, "CONT", station.continent);
		sb.append(writeTag(os, "CQZ", "" + station.cqz));
		writeTag(os, "CZ_DISTRICT", station.czDistrict);
		writeTag(os, "DIG", "" + station.dig);
		writeTag(os, "DOK", station.dok);
		writeTag(os, "DXCC", "" + station.dxcc);
		writeTag(os, "EMAIL_ADDRESS", station.emailAddress);
		sb.append(writeTag(os, "GRIDSQUARE", station.gridSquare));
		sb.append(writeTag(os, "IOTA", station.iota));
		sb.append(writeTag(os, "ITUZ", "" + station.ituz));
		writeTag(os, "JAG", "" + station.jag);
		if (station.clCity > 0) {
			writeTag(os, "JP_CITY", String.format("%04i", station.clCity));
		}
		if (station.jpGun > 0) {
			writeTag(os, "JP_GUN", String.format("%05i", station.jpGun));
		}
		writeTag(os, "LOCATION", station.location);
		writeTag(os, "MAILING_ADDRESS", station.mailingAddr);
		writeTag(os, "NZ_COUNTY", station.nzCounty);
		writeTag(os, "OPERATOR", station.operator);
		writeTag(os, "POSTAL_CODE", station.postalCode);
		writeTag(os, "REPEATER", station.repeater);
		writeTag(os, "RIG", station.rig);
		writeTag(os, "SDOK", station.sdok);
		writeTag(os, "CL_CITY", "" + station.clCity);
		writeTag(os, "SK_DISTRICT", station.skDistrict);
		writeTag(os, "SUB_GOV1", station.subGov1);
		writeTag(os, "SUB_GOV2", station.subGov2);
		writeTag(os, "SUB_GOV3", station.subGov3);
		writeTag(os, "STATION_TYPE", station.stationType);
		if (station.txPwr > 0) {
			sb.append(writeTag(os, "TX_PWR",
					String.format("%.3f", station.txPwr)));
		}
		writeTag(os, "URL", station.url);
		sb.append(writeTag(os, "US_COUNTY", station.usCounty));
		sb.append(writeTag(os, "US_STATE", station.usState));
		writeTag(os, "WAE", station.wae);
		eor(os);
		return sb.toString();
	}

	private void eor(BufferedOutputStream os) throws IOException {
		os.write("<EOR>\n".getBytes());
		if (PRETTY_MODE) {
			os.write("\n".getBytes());
		}
	}

	private void writeQso(BufferedOutputStream os, QsoData qso, Station station)
			throws IOException {
		StringBuilder signatureData = new StringBuilder(station.baseSig);

		// Note, these must remain in alphabetic order, except REC_TYPE,
		// SIGNDATA, and SIGN_LOTW_V1.0
		writeTag(os, "REC_TYPE", "tCONTACT");
		writeTag(os, "STATION_UID", "" + station.uid);
		signatureData.append(writeTag(os, "BAND", HamBand.getText(qso.band)));
		signatureData.append(writeTag(os, "BAND_RX",
				HamBand.getText(qso.bandRx)));
		signatureData.append(writeTag(os, "CALL", qso.call));
		signatureData.append(writeTag(os, "FREQ", "" + (qso.freq)));
		signatureData.append(writeTag(os, "FREQ_RX", "" + (qso.freqRx)));
		if (qso.mode != null) {
			signatureData.append(writeTag(os, "MODE", "" + qso.mode));
		}
		signatureData.append(writeTag(os, "PROP_MODE", qso.propMode));
		writeTag(os, "QSL", qso.qsl);
		signatureData.append(writeTag(os, "QSO_DATE",
				mDateFormat.format(qso.qso_dateTime)));
		signatureData.append(writeTag(os, "QSO_TIME",
				mTimeFormat.format(qso.qso_dateTime)));
		if (qso.satName != null) {
			signatureData.append(writeTag(os, "SAT_NAME", qso.satName));
			signatureData.append(writeTag(os, "SAT_MODE", qso.satMode));
		}
		writeTag(os, "REMARKS", qso.remarks);
		writeTag(os, "RST_SENT", qso.rstSent);
		writeTag(os, "SIGN_LOTW_V1.0", signQso(signatureData.toString()), "6");
		writeTag(os, "SIGNDATA", signatureData.toString());
		eor(os);
	}

	// Verifies that the station data meets the desired file format
	public boolean isValid(Station station) {
		if (station.dxcc == 0) {
			return false;
		}
		// TODO Verify each part works as expected.
		return true;
	}

	// Verifies that Qso data is of an acceptable format
	public boolean isValid(QsoData qso) {
		if (qso.band == Band.UNKNOWN || qso.call == null || qso.mode == null
				|| qso.qso_dateTime == null) {
			return false;
		}
		if (!isValidCallsign(qso.call)) {
			return false;
		}
		return true;
	}

	protected static boolean isValidCallsign(String call) {
		return call.matches("[a-zA-Z0-9/]*");
	}

	// Signs the individual Qso.
	private String signQso(String data) {
		String output = data;
		try {
			Signature signature = Signature.getInstance(mMessageDigestFormat);
			signature.initSign(mKey);
			signature.update(data.getBytes());
			byte[] signed = signature.sign();
			output = new String(Base64Coder.encode(signed));

		} catch (NoSuchAlgorithmException | InvalidKeyException
				| SignatureException e) {
			e.printStackTrace();
		}
		return output;
	}

	private static String writeTag(BufferedOutputStream os, String name,
			String data) throws IOException {
		return writeTag(os, name, data, null);
	}

	private static String writeTag(BufferedOutputStream os, String name,
			String data, String end) throws IOException {
		if (data != null && !data.equals("0") && !data.equals("")) {
			data = blockString(data, 64);
			int length = data.length();
			if (length > 0) {
				String output;
				if (end == null) {
					output = String.format(Locale.US, "<%s:%d>%s", name,
							length, data);
				} else {
					output = String.format(Locale.US, "<%s:%d:%s>%s", name,
							length, end, data);
				}
				os.write(output.getBytes());
				if (PRETTY_MODE && output.charAt(output.length() - 1) != '\n') {
					os.write("\n".getBytes());
				}
				return data.toUpperCase(Locale.US);
			}
		}
		return "";
	}

	private static String blockString(String data, int blockSize) {
		if (data.length() < blockSize) {
			return data;
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.length(); i += blockSize) {
			sb.append(data.substring(i, min(i + blockSize, data.length())));
			sb.append("\n");
		}
		data = sb.toString();
		return data;
	}

	private static int min(int a, int b) {
		if (a > b) {
			return b;
		}
		return a;
	}
}
