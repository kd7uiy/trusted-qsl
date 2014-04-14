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

import java.util.Locale;

public class HamBand {
	
	public enum Band {W2190M,W630M,W560M,W160M,W80M,W60M,W40M,W30M,W20M,W17M,
		W15M,W12M,W10M,W6M,W4M,W2M,W1_25M,W70CM,W33CM,W23CM,
		W13CM,W9CM,W5CM,W3CM,W1_2CM,W6MM,W4MM,W2_5MM,W2MM,W1MM,LIGHT}
	//Frequency in kHz
	public static Band findBand(double freq) {
		if (freq>= 135.9 && freq<=137.1) {
			return Band.W2190M;
		}
		if (freq>= 471.9 && freq<=479.1) {
			return Band.W630M;
		}
		if (freq>= 500 && freq<=504.1) {
			return Band.W560M;
		}
		if (freq>=1799.9 && freq<=2000.1) {
			return Band.W160M;
		}
		if (freq>=3500 && freq<=4000) {
			return Band.W80M;
		}
		if (freq>=5250 && freq<=5450) {
			return Band.W60M;
		}
		if (freq>=7000 && freq<=7300) {
			return Band.W40M;
		}
		if (freq>=10100 && freq<=10150) {
			return Band.W30M;
		}
		if (freq>= 14000 && freq<=14350) {
			return Band.W20M;
		}
		if (freq>=18068 && freq<=18168) {
			return Band.W17M;
		}
		if (freq>=21000 && freq<=21450) {
			return Band.W15M;
		}
		if (freq>=24890 && freq<=24990) {
			return Band.W12M;
		}
		if (freq>=28000 && freq<=29700) {
			return Band.W10M;
		}
		if (freq>=50000 && freq<=54000) {
			return Band.W6M;
		}
		if (freq>=69900 && freq<=70500) {
			return Band.W4M;
		}
		if (freq>= 144000 && freq<=148000) {
			return Band.W2M;
		}
		if (freq>=219000 && freq<=220000) {
			return Band.W1_25M;
		}
		if (freq>=222000 && freq<=225000) {
			return Band.W1_25M;
		}
		if (freq>=420000 && freq<=450000) {
			return Band.W70CM;
		}
		if (freq>=902000 && freq<=928000) {
			return Band.W33CM;
		}
		if (freq>=1240000 && freq<=1300000) {
			return Band.W23CM;
		}
		if (freq>=2300000 && freq<=2450000) {
			return Band.W13CM;
		}
		if (freq>=3300000 && freq<=3500000) {
			return Band.W9CM;
		}
		if (freq>=5650000 && freq<=5925000) {
			return Band.W5CM;
		}
		if (freq>=10000000 && freq<=10500000) {
			return Band.W3CM;
		}
		if (freq>=24000000 && freq<=24250000) {
			return Band.W1_2CM;
		}
		if (freq>=47000000 && freq<=47200000) {
			return Band.W6MM;
		}
		if (freq>=75500000 && freq<=81000000) {
			return Band.W4MM;
		}
		if (freq>=122250000 && freq<=123000000) {
			return Band.W2_5MM;
		}
		if (freq>=134000000 && freq<=141000000) {
			return Band.W2MM;
		}
		if (freq>=241000000 && freq<=250000000) {
			return Band.W1MM;
		}
		if (freq>=10500000000l) {
			return Band.LIGHT;
		}
		throw new IllegalArgumentException("No band found for frequency "+freq);
	}
	
	public static double frequencyUnitToMultiplier(String unit) {
		if (unit.equals("Hz")) {
			return 1e-3;
		} else if (unit.equals("kHz")) {
			return 1;
		} else if (unit.equals("MHz")) {
			return 1e3;
		} else if (unit.equals("GHz")) {
			return 1e6;
		}
		throw new IllegalArgumentException("Invalid unit "+unit+" detected.");
	}
	
	public static boolean isValidFrequency(double freq) {
		try {
			findBand(freq);
			return true;
		} catch (IllegalArgumentException ex) { }
		return false;
	}

	public static double findFreqInBand(String text) {
		Band band=findBand(text.toUpperCase(Locale.US));
		return findFreqInBand(band);
		
	}

	public static double findFreqInBand(Band band) {
		switch (band) {
		case W2190M:
			return 136;
		case W630M:
			return 472;
		case W560M:
			return 501;
		case W160M:
			return 1800;
		case W80M:
			return 3500;
		case W60M:
			return 5250;
		case W40M:
			return 7000;
		case W30M:
			return 10100;
		case W20M:
			return 14000;
		case W17M:
			return 18068;
		case W15M:
			return 21000;
		case W12M:
			return 24890;
		case W10M:
			return 28000;
		case W6M:
			return 50000;
		case W4M:
			return 70000;
		case W2M:
			return 144000;
		case W1_25M:
			return 220000;
		case W70CM:
			return 440000;
		case W33CM:
			return 902000;
		case W23CM:
			return 1240000;
		case W13CM:
			return 2400000;
		case W9CM:
			return 3300000;
		case W5CM:
			return 5650000;
		case W3CM:
			return 10000000;
		case W1_2CM:
			return 24000000;
		case W6MM:
			return 47000000;
		case W4MM:
			return 75500000;
		case W2_5MM:
			return 122250000;
		case W2MM:
			return 134000000;
		case W1MM:
			return 241000000;
		case LIGHT:
			return  4.4678e+11;
		default:
			break;
		}
		return 0;
	}

	public static Band findBand(String raw) {
		String text=raw.toUpperCase(Locale.US);
		if (text.equals("2190M")) {
			return Band.W2190M;
		}
		if (text.equals("630M")) {
			return Band.W630M;
		}
		if (text.equals("560M")) {
			return Band.W560M;
		}
		if (text.equals("2190M")) {
			return Band.W160M;
		}
		if (text.equals("80M")) {
			return Band.W80M;
		}
		if (text.equals("60M")) {
			return Band.W60M;
		}
		if (text.equals("40M")) {
			return Band.W40M;
		}
		if (text.equals("30M")) {
			return Band.W30M;
		}
		if (text.equals("20M")) {
			return Band.W20M;
		}
		if (text.equals("17M")) {
			return Band.W17M;
		}
		if (text.equals("15M")) {
			return Band.W15M;
		}
		if (text.equals("12M")) {
			return Band.W12M;
		}
		if (text.equals("10M")) {
			return Band.W10M;
		}
		if (text.equals("6M")) {
			return Band.W6M;
		}
		if (text.equals("4M")) {
			return Band.W4M;
		}
		if (text.equals("2M")) {
			return Band.W2M;
		}
		if (text.equals("1.25M")) {
			return Band.W1_25M;
		}
		if (text.equals("70CM")) {
			return Band.W70CM;
		}
		if (text.equals("33CM")) {
			return Band.W33CM;
		}
		if (text.equals("23CM")) {
			return Band.W23CM;
		}
		if (text.equals("13CM")) {
			return Band.W13CM;
		}
		if (text.equals("9CM")) {
			return Band.W9CM;
		}
		if (text.equals("5CM")) {
			return Band.W5CM;
		}
		if (text.equals("3CM")) {
			return Band.W3CM;
		}
		if (text.equals("1.2CM")) {
			return Band.W1_2CM;
		}
		if (text.equals("6MM")) {
			return Band.W6MM;
		}
		if (text.equals("4MM")) {
			return Band.W4MM;
		}
		if (text.equals("2.5MM")) {
			return Band.W2_5MM;
		}
		if (text.equals("2MM")) {
			return Band.W2MM;
		}
		if (text.equals("1MM")) {
			return Band.W1MM;
		}
		if (text.equals("LIGHT")) {
			return Band.LIGHT;
		}
		throw new IllegalArgumentException ("Invalid text for a band");
	}

	public static String getText(Band band) {
		switch (band) {
		case W2190M:
			return "2190m";
		case W630M:
			return "630m";
		case W560M:
			return "560m";
		case W160M:
			return "160m";
		case W80M:
			return "80m";
		case W60M:
			return "60m";
		case W40M:
			return "40m";
		case W30M:
			return "30m";
		case W20M:
			return "20m";
		case W17M:
			return "17m";
		case W15M:
			return "15m";
		case W12M:
			return "12m";
		case W10M:
			return "10m";
		case W6M:
			return "6m";
		case W4M:
			return "4m";
		case W2M:
			return "2m";
		case W1_25M:
			return "1.25m";
		case W70CM:
			return "70cm";
		case W33CM:
			return "33cm";
		case W23CM:
			return "23cm";
		case W13CM:
			return "13cm";
		case W9CM:
			return "9cm";
		case W5CM:
			return "5cm";
		case W3CM:
			return "3cm";
		case W1_2CM:
			return "2cm";
		case W6MM:
			return "6mm";
		case W4MM:
			return "4mm";
		case W2_5MM:
			return "2.5mm";
		case W2MM:
			return "2mm";
		case W1MM:
			return "1mm";
		case LIGHT:
			return  "light";
		default:
			break;
		}
		return null;
	}

}
