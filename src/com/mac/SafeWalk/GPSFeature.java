package com.mac.SafeWalk;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by koheihisakuni on 3/29/14.
 * implements Google play serves client connection callbacks
 * and on connection failed listener.
 * Got a lot of help from this site:
 * http://www.tutorialspoint.com/android/android_location_based_services.htm
 */

public class GPSFeature {



    /*
    Instance variables
     */
    Location mCurrentLocation;              // holds current location
    LocationClient mLocationClient;         // google location client
    String mAddress;

    public GPSFeature() {

    }

    /*
    Gets the location by first retrieving the coordinates and then doing reverse geocoding.
     */
    public Location getLocation(LocationClient locationClient) {
        return locationClient.getLastLocation();
    }

    /*
    Uses location to get the address
     */
    public void getAddress(Location location, Context context) {
        (new GetAddressTask(context)).execute(location);
    }

    /*
    Subclass of AsyncTask used to get the address given the latitude and the longitude.
     */
    private class GetAddressTask extends AsyncTask<Location, Void, String> {

        Context mContext;

        /*
        Constructor
         */
        public GetAddressTask(Context context) {
            super();
            mContext = context;
        }

        /*
        Do task in background so there is no interruption.
         */
        @Override
        protected String doInBackground(Location... params) {

            // Set up geocoder
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());

            // Get current location from parameter list
            Location location = params[0];

            //Create a list to contain the result address
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(location.getLatitude(),
                        location.getLongitude(), 1);
            } catch (IOException e1) {
                Log.e("GPS Feature", "IO Exception in getFromLocation");
            } catch (IllegalArgumentException e2) {
                String errorString = "Illegal arguments " +
                        Double.toString(location.getLatitude()) +
                        " , " +
                        Double.toString(location.getLongitude()) +
                        " passed to address services";
                Log.e("GPS Feature" , errorString);
                e2.printStackTrace();
                return errorString;
            }

            // Check if geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get first address from list
                Address address = addresses.get(0);

                // Format the address
                String addressText = String.format(
                        "%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());
                return addressText;
            } else {
                return "No address found";
            }
        }

        /*
        Method is called after the task is finished.
         */
        @Override
        protected void onPostExecute(String address) {
            mAddress = address;
        }
    }
}
