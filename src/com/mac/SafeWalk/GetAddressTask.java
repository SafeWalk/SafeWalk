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
 * Subclass of AsyncTask. Used to get the street address given the latitude and the longitude.
 */
public class GetAddressTask extends AsyncTask<Location, Void, String> {

    Context context;

    public GetAddressTask(Context context) {
        super();
        this.context = context;
    }

    /*
     * Do task in background so there is no interruption.
     */
    @Override
    protected String doInBackground(Location... params) {

        // Set up geocoder
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        // Get current location from parameter list
        Location location = params[0];

        // Create a list to contain the result address
        List<Address> addresses = null;
        try {
            Log.w(GetAddressTask.class.toString(), "Started getting address from server");
            // Get a list of street addresses using the geocoder.
            addresses = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            Settings.getSettings().setPickUpCoordinates(location.getLatitude(), location.getLongitude());
            Log.w(GetAddressTask.class.toString(), "Finished getting address from server");
        } catch (IOException e1) {
            Log.e("GetAddressTask", "IO Exception in getFromLocation");
            e1.printStackTrace();
        } catch (IllegalArgumentException e2) {
            // Make error string
            String errorString = "Illegal arguments " + Double.toString(location.getLatitude()) + " , " +
                    Double.toString(location.getLongitude()) + " passed to address services";
            Log.e("GetAddressTask" , errorString);
            e2.printStackTrace();
            return errorString;
        } catch (NullPointerException e3){
            // Waiting for connection
        }

        // Check if geocode returned an address
        if (addresses != null && addresses.size() > 0 && (location.getAccuracy() < 100)) {
            // Get first address from list
            Address address = addresses.get(0);
            // Format the address.
            String addressText = String.format("%s, %s, %s", address.getMaxAddressLineIndex() > 0 ?
                    address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
            return addressText;
        } else if (location.getAccuracy() >= 100) {
            return "Your location is not accurate enough\nTap on the arrow to try again.";
        } else {
            return "No address found\nTru using a different option";
        }
    }

    /*
     * Method called after the task is finished. The address is saved into settings.
     */
    @Override
    protected void onPostExecute(String address) {
        Settings.getSettings().setPickUpLocation(address);
        HomeScreenActivity.setGpsFinished(true);
    }
}




