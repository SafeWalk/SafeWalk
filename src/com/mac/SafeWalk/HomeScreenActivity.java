package com.mac.SafeWalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.mac.SafeWalk.GPSFeature;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 *
 */
public class HomeScreenActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener {


    // Boolean to check if student is choosing from spinner or inputting address.
    private boolean isCustom;
    private Button sendButton;
    private String swStatus;

    // Location vars
    private LocationClient mLocationClient;
    private Location mCurrentLocation;


    // GPS stuff
    private TextView gpsText;
    private TextView locationText;
    private String mAddress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        If no name and phone number saved, go to settingsActivity
        String name = loadName();
        String phoneNumber = loadNumber();
        if (name.equals("No name") && phoneNumber.equals("No number")){
            Intent welcome = new Intent(this, WelcomeActivity.class);
            startActivity(welcome);
        } else {
            setContentView(R.layout.main);
            // set up locationSpinner
            Spinner locationSpinner = setSpinner();
            onSelectedInSpinner(locationSpinner);
            sendButton = (Button) findViewById(R.id.send);
            checkAvailability();
            setFonts();
        }

        // TESTING TESTING TESTING
        gpsText = (TextView) findViewById(R.id.GPSText);
        locationText = (TextView) findViewById(R.id.locationText);

        // set up locationClient
        mLocationClient = new LocationClient(this, this, this);

    }





    /**
     * Checks the availability of Safewalk though Firebase
     */
    private void checkAvailability() {
        Firebase ref = new Firebase("https://safewalk.firebaseio.com/Status");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snap) {
                setSendButton(snap.getValue(String.class));
                swStatus = snap.getValue(String.class);
                Log.w("Status", ">>>>>>>>>>>>>" + swStatus);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }


    /**
     * Retrieve address typed in the Edit Text that appears when "Other" is selected on the spinner
     * @param editText Edit Text field of the UI
     * @return String of the typed address
     */
    private String retrieveLocation(EditText editText) {
        return editText.getText().toString();
    }

    /**
     * Sends the pick-up location to the next activity so that the Sms Manager can send a text message if
     * the safewalk status is available. Else it warns the user of the current state.
     * Accessed when "send" button is clicked. Retrieves data from EditText via retrieveLocation method.
     */
    public void sendClick(View view) {
        if (swStatus == null) {
            AlertDialog safewalkBusyDialog = new AlertDialog.Builder(this).create();
            safewalkBusyDialog.setTitle("Safewalk is unreachable");
            safewalkBusyDialog.setMessage("Unable to get Safewalk status. Check you internet connection.");
            safewalkBusyDialog.show();
        } else if (swStatus.equals("yes")){
            EditText customEdit = (EditText) findViewById(R.id.customLocationText);
            Intent intent = new Intent(this, SendMessageActivity.class);
            if (retrieveLocation(customEdit).equals("") && isCustom) {
                AlertDialog emptyLocationAlert = new AlertDialog.Builder(this).create();
                emptyLocationAlert.setTitle("Empty Location");
                emptyLocationAlert.setMessage("You must input a valid pickup location");
                emptyLocationAlert.show();
            } else if (isCustom) {
                Settings.getSettings().setPickUpLocation(retrieveLocation(customEdit));
                startActivity(intent);
            } else {
                startActivity(intent);
            }
        } else if (swStatus.equals("busy")){
            AlertDialog safewalkBusyDialog = new AlertDialog.Builder(this).create();
            safewalkBusyDialog.setTitle("Safewalk is busy");
            safewalkBusyDialog.setMessage("We're sorry, all our workers are currently busy");
            safewalkBusyDialog.show();
        } else {
            AlertDialog safewalkBusyDialog = new AlertDialog.Builder(this).create();
            safewalkBusyDialog.setTitle("Safewalk is not available");
            safewalkBusyDialog.setMessage("We're sorry, Safewalk is not available at this time");
            safewalkBusyDialog.show();
        }

    }

    /**
     * Go to settings when the setting button is pressed
     */
    public void openSetting (View view){
        Intent settings = new Intent(this, SettingsActivity.class);
        startActivity(settings);
    }

    /**
     * This method sets up the spinner wheel for the different pickup choices of the main UI
     * @return spinner
     */
    private Spinner setSpinner(){
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.pick_up_choices,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        return spinner;
    }

    /**
     * Sets the pick up location of the variable pickUpLocation on the Settings class depending on the
     * selection
     * @param spinner Spinner with all options and "Other" option
     */
    private void onSelectedInSpinner(final Spinner spinner){

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText customEdit = (EditText)findViewById(R.id.customLocationText);
                if (spinner.getItemAtPosition(position).toString().equalsIgnoreCase("Other")) {
                    // If student chooses the option "Other" from spinner, an EditText magically appears.
                    customEdit.setVisibility(View.VISIBLE);
                    isCustom = true;
                } else {
                    // Otherwise the pick-up location is whatever the user chooses from spinner.
                    Settings.getSettings().setPickUpLocation(parent.getItemAtPosition(position).toString());
                    customEdit.setVisibility(View.INVISIBLE);
                    isCustom = false;
                }
            }
            // Leave this method; this has to be here.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /*
     * Changes the fonts and color of the fonts
     */
    private void setFonts() {
        //Make fonts from assets
        Typeface quicksand = Typeface.createFromAsset(getAssets(), "fonts/quicksand.otf");
        Typeface quicksandBold = Typeface.createFromAsset(getAssets(), "fonts/quicksand_bold.otf");
        //Get Views
        TextView title = (TextView) findViewById(R.id.title);
        TextView otherAddress = (TextView) findViewById(R.id.customLocationText);
        //Set Fonts
        title.setTypeface(quicksand);
        otherAddress.setTypeface(quicksandBold);
        sendButton.setTypeface(quicksand);

    }

//    Load Shared Preferences
    public String loadName(){
        //  Load Name
        Settings.getSettings().setNameData(getSharedPreferences(Settings.getFilename(), 0));
        String nameReturned = Settings.getSettings().getNameData().getString("sharedName", "No name");
        return nameReturned;
    }
    public String loadNumber(){
        //  Load Phone Number
        Settings.getSettings().setNameData(getSharedPreferences(Settings.getPhoneFile(), 0));
        String numberReturned = Settings.getSettings().getNameData().getString("sharedPhone", "No number");
        return numberReturned;
    }

    /**
     * setSendButton changes the color of the button depending on the status of Safewalk
     * @param status String containing the status of Safewalk in real time
     */
    private void setSendButton(String status) {
        if (status.equals("yes")){
            sendButton.setBackgroundResource(R.drawable.available_button);
            sendButton.setText("Send");
            sendButton.setTextSize(36);
        } else if (status.equals("busy")){
            sendButton.setBackgroundResource(R.drawable.busy_button);
            sendButton.setText("Busy");
            sendButton.setTextSize(36);
        } else {
            sendButton.setBackgroundResource(R.drawable.not_available_button);
            sendButton.setText("Not Available");
            sendButton.setTextSize(22);
        }
    }


    //----------------- GPS STUFF --------------------------------------

    public void locationClick(View view) {
        mCurrentLocation = getLocation(mLocationClient);
        Double lat = mCurrentLocation.getLatitude();
        Double lng = mCurrentLocation.getLongitude();
        locationText.setText(lat.toString() + " " + lng.toString());
    }

    public void gpsClick(View view) {
        mCurrentLocation = getLocation(mLocationClient);
        getAddress(mCurrentLocation, this);
        gpsText.setText(mAddress);
    }

    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failure : " +
                connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }


    public Location getLocation(LocationClient locationClient) {
        return locationClient.getLastLocation();
    }

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
