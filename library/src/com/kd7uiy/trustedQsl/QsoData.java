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

import java.io.Serializable;
import java.util.Date;

import com.kd7uiy.trustedQsl.HamBand.Band;

public class QsoData implements Serializable {
	public enum Mode {
		AM, AMTORFEC, ASCI, ATV, CHIP64, CHIP128, CLO, CONTESTI, CW, DSTAR, DOMINO, DOMINOF, FAX, FM, FMHELL, FSK31, FSK441, GTOR, HELL, HELL80, HFSK, ISCAT, JT44, JT4A, JT4B, JT4C, JT4D, JT4E, JT4F, JT4G, JT65, JT9, JT6M, MFSK8, MFSK16, MT63, OLIVIA, OPERA, PAC, PAC2, PAC3, PAX, PAX2, PCW, PKT, PSK10, PSK2K, PSK31, PSK63, PSK63F, PSK125, PSKAM10, PSKAM31, PSKAM50, PSKFEC31, PSKHELL, Q15, QPSK31, QPSK63, QPSK125, ROS, RTTY, RTTYM, SSB, SSTV, THRB, THOR, THRBX, TOR, V4, VOI, WINMOR, WSPR, PHONE
	};

	private static final long serialVersionUID = 6225054585566017811L;
	public Band band; // TX Band
	public Band bandRx; // RX Band
	public String call; // Callsign
	public double freq; // TX freq, In MHz
	public double freqRx; // RX freq, in MHz.
	public Mode mode; // Mode
	public String propMode;
	public String qsl; // PSE to request confirmation, THX indicates no response
						// necessary
	public Date qso_dateTime; // Time of QSO
	public String remarks; // Comments included
	public String rstSent; // RST
	public String satName;	//Satellite name
	public String satMode;	//Satellite Mode

	public void setBand(Band band) {
		freq = HamBand.findFreqInBand(band);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("(band:" + HamBand.getText(band));
		sb.append(",bandRx:" + HamBand.getText(bandRx));
		sb.append(",call:" + call);
		sb.append(",freq:" + freq);
		sb.append(",freqRx:" + freqRx);
		sb.append(",mode:" + mode);
		sb.append(",propMode:" + propMode);
		sb.append(",qsl:" + qsl);
		sb.append(",qso_dateTime:" + qso_dateTime);
		sb.append(",remarks:" + remarks);
		sb.append(",rstSent:" + rstSent);
		sb.append(",satName:" + satName);
		sb.append(",satMode:" + satMode);
		sb.append(")");
		return sb.toString();
	}
}