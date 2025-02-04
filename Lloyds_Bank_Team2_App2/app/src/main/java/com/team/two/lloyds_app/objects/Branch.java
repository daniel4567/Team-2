package com.team.two.lloyds_app.objects;

/**
 * Author: Michael Edwards
 * Date: 07/04/2015
 * Purpose: Hold data about a particular branch
 * Modified By Daniel Smith on 12/04/2015
 */

public class Branch{
	private String name;
	private double latitude;
	private double longitude;
	private Address address;
	private String phoneNumber;
	private String[] openingTimes;

	//Single constructor
	public Branch(String name, double latitude, double longitude, Address address, String phoneNumber, String[] openingTimes){
		this.name = name;
		this.latitude = latitude;
		this.longitude = longitude;
		this.address = address;
		this.phoneNumber = phoneNumber;
		this.openingTimes = openingTimes;
	}

	//getters for fields
	public String getName(){
		return name;
	}

	public double getLatitude(){
		return latitude;
	}

	public double getLongitude(){
		return longitude;
	}

	public Address getAddress(){
		return address;
	}

	public String getPhoneNumber(){
		return phoneNumber;
	}

	public String[] getOpeningTimes(){
		return openingTimes;
	}
}
