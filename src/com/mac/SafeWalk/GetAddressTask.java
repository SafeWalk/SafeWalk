package com.mac.SafeWalk;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Subclass of AsyncTask used to get the address given the latitude and the longitude.
 */
public class GetAddressTask extends AsyncTask<Location, Void, String> {

    Context mContext;

    public GetAddressTask(Context context) {
        super();
        mContext = context;
    }


    /*
     * Do task in background so there is no interruption.
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
            Log.w("Started try", "NOW <<<<<<");
            Log.w("GPS Accuracy", Float.toString(location.getAccuracy()));
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
        } catch (IOException e1) {
            Log.e("GPS Feature", "IO Exception in getFromLocation");
            e1.printStackTrace();
        } catch (IllegalArgumentException e2) {
            String errorString = "Illegal arguments " +
                    Double.toString(location.getLatitude()) +
                    " , " +
                    Double.toString(location.getLongitude()) +
                    " passed to address services";
            Log.e("GPS Feature" , errorString);
            e2.printStackTrace();
            return errorString;
        } catch (NullPointerException e3){
            //Waiting for connection
        }
        Log.w("Ended try", "NOW<<<<<<");

        // Check if geocode returned an address
        if (addresses != null && addresses.size() > 0 && (location.getAccuracy() < 100)) {
            // Get first address from list
            Address address = addresses.get(0);

            // Format the address
            String addressText = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ?
                    address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
            return addressText;
        } else if (location.getAccuracy() >= 100) {
            return "Location not accurate";
        } else {
            return "No address found";
        }
    }

    /*
     * Method called after the task is finished.
     */
    @Override
    protected void onPostExecute(String address) {
        Settings.getSettings().setPickUpLocation(address);
        HomeScreenActivity.setGpsFinished(true);
    }
}




