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

public class Station implements Serializable {
	private static final long serialVersionUID = 7168577228088510232L;
	public String call;
	public String continent;
	public String cqz;
	public int dxcc;
	public String emailAddress;
	public String grid;
	public String iota;
	public String ituz;
	public String zipCode;
	public String satName;
	public String mode;
	public String usCounty;
	public String state;
	public int uid;

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("(call:"+call);
		sb.append(",Continent:"+continent);
		sb.append(",cqz:"+cqz);
		sb.append(",dxcc:"+dxcc);
		sb.append(",emailAddress:"+emailAddress);
		sb.append(",grid:"+grid);
		sb.append(",iota:"+iota);
		sb.append(",ituz:"+ituz);
		sb.append(",zipCode:"+zipCode);
		sb.append(",satName:"+satName);
		sb.append(",mode:"+mode);
		sb.append(",usCounty:"+usCounty);
		sb.append(",state:"+state);
		sb.append(",uid:"+uid);
		sb.append(")");
		return sb.toString();
	}
}