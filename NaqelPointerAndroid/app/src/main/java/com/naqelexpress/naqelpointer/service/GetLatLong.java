package com.naqelexpress.naqelpointer.service;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;

public class GetLatLong {

	boolean flagthread = false;
	Location location;
	Handler handler;
	Context con;
	public static LocationManager lm;
	int callagain = 0;

	public GetLatLong() {
		super();
	}

	public GetLatLong(Context con) {
		this.con = con;
		try {

			lm = (LocationManager) con
					.getSystemService(Context.LOCATION_SERVICE);
			boolean gps_enabled = lm
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean network_enabled = lm
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

//			MainActivity.location = true;
//			if (!gps_enabled)
//				MainActivity.location = false;
//			if (!network_enabled) ¬
//				MainActivity.location = false;
			
			// if (gps_enabled == network_enabled) {
			// MainActivity.location = false;
			// } else {
			// MainActivity.location = true;
			// // getaddress();
			// }

		} catch (Exception e) {
			//InstalationDetails.latitude = 0;
			//InstalationDetails.longitude = 0;
		}

	}
}
