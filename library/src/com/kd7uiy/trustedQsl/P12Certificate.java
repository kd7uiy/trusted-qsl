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


import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

import javax.security.auth.x500.X500Principal;

public class P12Certificate {

	public static KeyStore getKeyStore(String filename, String password) {
		KeyStore p12 = null;
		try {
			p12 = KeyStore.getInstance("pkcs12");
			p12.load(new FileInputStream(filename), password.toCharArray());

		} catch (NoSuchAlgorithmException | CertificateException | IOException
				| KeyStoreException e1) {
			e1.printStackTrace();
		}
		return p12;
	}
	
	public static KeyStore getKeyStore(FileInputStream stream, String password) {
		KeyStore p12 = null;
		try {
			p12 = KeyStore.getInstance("pkcs12");
			p12.load(stream, password.toCharArray());

		} catch (NoSuchAlgorithmException | CertificateException | IOException
				| KeyStoreException e1) {
			e1.printStackTrace();
		}
		return p12;
	}

	public static String getCallSignFromP12(String filename, String password) throws CertificateException, IOException {
		KeyStore p12 = null;
		String call=null;
		try {
			p12 = KeyStore.getInstance("pkcs12");
			p12.load(new FileInputStream(filename), password.toCharArray());
			Enumeration<String> e = p12.aliases();
			while (e.hasMoreElements()) {
				String alias = (String) e.nextElement();
				X509Certificate c = (X509Certificate) p12.getCertificate(alias);
				
				//This verifies that the certificate is from LOTW, or else it throws an exception
				X500Principal issuer= c.getIssuerX500Principal();
				String issuerArray[] = issuer.toString().split(",");
				for (String s : issuerArray) {
					String[] str = s.trim().split("=");
					String key = str[0];
					String value = str[1];
					if (key.equals("OU") && !value.equals("Logbook of the World")) {
						throw new CertificateException("This certificate is not a Logbook of the World Certificate!");
					}
//					System.out.println(key + " - " + value);
				}

				//This gets the call sign.
				X500Principal subject = c.getSubjectX500Principal();
				String subjectArray[] = subject.toString().split(",");
				for (String s : subjectArray) {
					String[] str = s.trim().split("=");
					String key = str[0];
					String value = str[1];
					if (key.equals("OID.1.3.6.1.4.1.12348.1.1")) {
						call=value;
					}
//					System.out.println(key + " - " + value);
				}
			}
		} catch (KeyStoreException | NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return call;

	}
}