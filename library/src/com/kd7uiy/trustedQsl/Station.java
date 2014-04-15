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

/* All of the data on a station. Note that none of these are required For more information, see
 * http://trustedqsl.cvs.sourceforge.net/viewvc/trustedqsl/tqsl/html/trustedqsl/GAbbI/gabbi.htm?revision=HEAD
 */
public class Station implements Serializable {
	private static final long serialVersionUID = 7168577228088510232L;
	public String call;			//Call sign used by the recording station
	public int clCity;			//Chilean Municipality ("Comuna"). 5 or 6 digit code for municipality
	public String continent;	//Continent
	public String cqz;			//CQ Zone
	public String czDistrict;	//Czech District ("okres"), 3 letter abbreviation
	public int dxcc;			//DXCC number, as ARRL records
	public String emailAddress;	//Email Address
	public String gridsquare;	//Maidenhead Grid Square (4 or 6 character)
	public String iota;			//Island On The Air number
	public String ituz;			//ITU Zone
	public int jag;				//Japan Award Hunter's Group member number
	public int jpCity;			//Japanese City ("shi").
	public String location;		//User provided geographical location
	public String mailingAddr;	//Mailing Address, should be internationalized
	public String nzCounty;		//New Zealand county, FIPS Pub. 10-4. 
	public String operator;		//Operator, if not "call"
	public String postalCode;	//Postal Code
	public String repeater;		//Terrestrial repeater callsign
	public String rig;			//Description of station
	public String satName;		//Satellite Name 
	public String satMode;		//Satellite Mode
	public String sdok;			//DARC Sonder- DOK, string up to 8 characters
	public String skDistrict;	//Slovak District ("okres"). 3 letter abbreviation
	public String subGov1;		//State/Providence level
	public String subGov2;		//Subordinate to subGov1, Count, Gun, Parish, District, etc.
	public String subGov3;		//Subordinate to subGov2.
	public String stationType;	//Possible station types
	public float txPwr;			//Output in W. Will only be included to the mW accuracy
	public String usCounty;		//US County
	public String usState;		//US State
	public String wae;			//Worked All Europe entity
	public int uid;				//UID, the id of the station for the file. Don't set this manually!
	public String baseSig;		//Data to sign for the station.

	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("(call:"+call);
		sb.append(",Continent:"+continent);
		sb.append(",cqz:"+cqz);
		sb.append(",dxcc:"+dxcc);
		sb.append(",emailAddress:"+emailAddress);
		sb.append(",gridsquare:"+gridsquare);
		sb.append(",iota:"+iota);
		sb.append(",ituz:"+ituz);
		sb.append(",postalCode:"+postalCode);
		sb.append(",satName:"+satName);
		sb.append(",satMode:"+satMode);
		sb.append(",usCounty:"+usCounty);
		sb.append(",usState:"+usState);
		sb.append(",uid:"+uid);
		sb.append(",baseSig:"+baseSig);
		sb.append(")");
		return sb.toString();
	}
}