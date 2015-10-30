package com.ko.lct.ws.utils;

public class GeocodeTransform {

	public static double kmToLongitude(double latitude, double dist) {
		double oneDegreeLongDistInKm = longitudeDistKm(latitude);
		return dist / oneDegreeLongDistInKm;
	}

	public static double miToLongitude(double latitude, double distInMi) {
		double oneDegreeLongDistInMi = longitudeDistMi(latitude);
		return distInMi / oneDegreeLongDistInMi;
	}
	
	public static double kmToLatitude(double latitude, double dist) {
		double oneDegreeLatDistKm = latitudeDistKm(latitude);
		return dist / oneDegreeLatDistKm;
	}

	public static double miToLatitude(double latitude, double distInMi) {
		double oneDegreeLatDistMi = latitudeDistMi(latitude);
		return distInMi / oneDegreeLatDistMi;
	}

	public static double longitudeDistKm(double latitude) {
		double theta = Math.toRadians(latitude);
		return 111.41288 * Math.cos(theta) - 0.09350 * Math.cos(3 * theta) + 0.00012 * Math.cos(5 * theta);
	}

	public static double longitudeDistMi(double latitude) {
		double theta = Math.toRadians(latitude);
		return 69.22875407619502 * Math.cos(theta) - 0.0580982064741907 * Math.cos(3 * theta) + 7.456454306848008e-5 * Math.cos(5 * theta);
	}

	public static double latitudeDistKm(double latitude) {
		double theta = Math.toRadians(latitude);
		return 111.13295 - 0.55982 * Math.cos(2 * theta) + 0.00117 * Math.cos(4 * theta);
	}
	
	public static double latitudeDistMi(double latitude) {
		double theta = Math.toRadians(latitude);
		return 69.05481363835203 - 0.3478560208383043 * Math.cos(2 * theta) + 7.270042949176808e-4 * Math.cos(4 * theta);
	}

	public static double kmToMiles(double distanceInKm) {
		return distanceInKm * 0.621371192237334;
	}
	
	public static double milesToKm(double distanceInMiles) {
		return distanceInMiles * 1.609344;
	}

}
