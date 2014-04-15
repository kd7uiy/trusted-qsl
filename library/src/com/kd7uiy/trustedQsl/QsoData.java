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

public class QsoData implements Serializable{
	private static final long serialVersionUID = 6225054585566017811L;
	public String call;		//Callsign
	public double freq;		//In MHz
	public Date dateTime;	//Time of QSO
	public String mode;		//Mode
	public String remarks; 	//Comments included
	public String rstSent;	//RST
	
	public void setBand(Band band) {
		freq=HamBand.findFreqInBand(band);
	}

	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		return sb.toString();
	}
}