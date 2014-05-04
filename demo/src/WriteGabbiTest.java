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

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.util.Date;

import com.kd7uiy.trustedQsl.HamBand.Band;
import com.kd7uiy.trustedQsl.P12Certificate;
import com.kd7uiy.trustedQsl.QsoData;
import com.kd7uiy.trustedQsl.QsoData.Mode;
import com.kd7uiy.trustedQsl.Station;
import com.kd7uiy.trustedQsl.WriteGabbi;


public class WriteGabbiTest extends WriteGabbi {

	public WriteGabbiTest(KeyStore keystore, char[] password, String alias) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableEntryException {
		super(keystore,password,alias);
	}

	@Override
	protected Station[] getStations() {
		Station station=new Station();
		station.dxcc=291;
		return new Station[]{station};
	}

	@Override
	protected QsoData[] getQsoData(Station station) {
		if (station.dxcc==291) {
			QsoData data=new QsoData();
			data.band=Band.W2M;
			data.call="KD7UIY";
			data.freq=146.580;
			data.mode=Mode.SSB;
			data.qso_dateTime=new Date();
			return new QsoData[]{data};
		} else {
			return null;
		}
	}

	@Override
	protected void publishProgress(int numComplete) {
		System.out.println("Have written "+ numComplete +" records");
		
	}
	
	//First argument is the location of your .p12 file
	public static void main(String[] args) {
		final String alias="trustedqsl user certificate";
		KeyStore keystore=P12Certificate.getKeyStore(args[0],"");
		try {
			WriteGabbiTest writeGabbi= new WriteGabbiTest(keystore,new char[]{},alias);
			if (writeGabbi.writeToLotw("test.tq8")==true) {
				System.out.println("File uploaded successfully");
			} else {
				System.out.println("File uploaded failed");
			}
		} catch (KeyStoreException
				| NoSuchAlgorithmException | IOException | UnrecoverableEntryException e) {
			e.printStackTrace();
		}
	}

}
