package com.mac.SafeWalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.*;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.Observable;
import java.util.Observer;

/**
 *
 */
public class HomeScreenActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, Observer, LocationListener {


    // Boolean to check if student is choosing from spinner or inputting address.
    private boolean isCustom;
    private boolean useGPS = false;
    private Button sendButton;
    private TextView gpsAddress;
    private ImageView arrow;
    private String swStatus;
    private static boolean gpsFinished = false;

    // Location vars
    private LocationClient locationClient;
    private LocationManager locationManager;
    LocationRequest locationRequest;

    private static final int MILLISECONDS_PER_SECOND = 1000;
    private static final int UPDATE_INERVAL_IN_SECONDS = 5;
    private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //If no name and phone number saved, go to WelcomeActivity
        //This happens when the user opens the app for the first time
        String name = loadName();
        String phoneNumber = loadNumber();
        if (name.equals("No name") && phoneNumber.equals("No number")){
            Intent welcome = new Intent(this, WelcomeActivity.class);
            startActivity(welcome);
        } else {
            // if user information is already saved, go to the main activity
            setContentView(R.layout.main);
            // set up locationSpinner
            Spinner locationSpinner = setSpinner();
            onSelectedInSpinner(locationSpinner, this);
            sendButton = (Button) findViewById(R.id.send);
            gpsAddress = (TextView) findViewById(R.id.GPSText);
            checkAvailability();
            setFonts();
        }

        // set up locationClient
        locationClient = new LocationClient(this, this, this);
        Settings.getSettings().setObserver(this);
        arrow = (ImageView) findViewById(R.id.arrow);

        // Location updates
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    // Disable the "back" button for the HomeScreen Activity
    // Prevent the app to back back to the welcome screen
    @Override
    public void onBackPressed() {
        onResume();
    }

    @Override
    public void onResume(){
        super.onResume();
        Settings.getSettings().setPickUpLocation("");
        gpsFinished = false;
        if (!locationClient.isConnected()) {
            locationClient.connect();
            Log.w(HomeScreenActivity.class.toString(), "Location client connected");
        }
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
                Log.w("Main Activity", "Safewalk status: " + swStatus);
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
     * Executed when "send" button is clicked. It checks for the current status of safewalk and either executes
     * swAvailble if available or creates an alert dialog to inform the user why their request was unsuccessful.
     */
    public void sendClick(View view) {
        if (swStatus == null) {
            createAlertDialog("Safewalk is unreachable", "Unable to get Safewalk status. Check you internet connection.");
        } else if (swStatus.equals("Available")){
            swAvailable();
        } else if (swStatus.equals("Busy")){
            createAlertDialog("Safewalk is busy", "We're sorry, all our workers are currently busy");
        } else {
            createAlertDialog("Safewalk is not available", "We're sorry, Safewalk is not available at this time");
        }

    }

    /**
     * This method is called after the user clicks send and safewalk's status is available. It then checks what
     * kind of location the user: from the dropdown menu, custom written or GPS. It does the necessary checks
     * before sending the location to SendMessageActivity.
     */
    private void swAvailable() {
        EditText customEdit = (EditText) findViewById(R.id.customLocationText);
        Intent intent = new Intent(this, SendMessageActivity.class);
        intent.putExtra("gps", false);
        if (retrieveLocation(customEdit).equals("") && isCustom) {
            createAlertDialog("Empty Location", "Please enter a valid pickup location");
        } else if (isCustom) {
            Settings.getSettings().setPickUpLocation(retrieveLocation(customEdit));
            startActivity(intent);
        } else if (useGPS) {
            if (gpsFinished && Settings.getSettings().getPickUpLocation().contains("No address found")){
                createAlertDialog("The GPS couldn't find you", "Sorry, you should try another option");
            } else if (gpsFinished && Settings.getSettings().getPickUpLocation().contains("Your location is not")) {
                createAlertDialog("Sorry, the GPS can't find you", "Try moving to a more open area or use another option");
            } else if (gpsFinished) {
                intent.putExtra("gps", true);
                startActivity(intent);
            } else {
                createAlertDialog("GPS hasn't finished", "GPS will be done locating you shortly");
            }
        } else if (!Settings.getSettings().getPickUpLocation().equals("Select")) {
            startActivity(intent);
        }
    }
    /**
     * This method generates a generic alert dialog from the two input strings and displays it.
     * @param title of the alert dialog
     * @param body of the alert dialog
     */

    public void createAlertDialog(String title, String body){
        AlertDialog genericDialog = new AlertDialog.Builder(this).create();
        genericDialog.setTitle(title);
        genericDialog.setMessage(body);
        genericDialog.show();
    }

    public static void setGpsFinished(boolean gpsFinished) {
        HomeScreenActivity.gpsFinished = gpsFinished;
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
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        LayoutInflater inflater = getLayoutInflater();
        spinner.setAdapter(new SafewalkArrayAdapter(this, R.layout.spinner_style, Settings.getSettings().swLocations(), inflater));
        return spinner;
    }

    /**
     * Sets the pick up location of the variable pickUpLocation on the Settings class depending on the
     * selection
     * @param spinner Spinner with all options and "Other" option
     */
    private void onSelectedInSpinner(final Spinner spinner, final Context context){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EditText customEdit = (EditText) findViewById(R.id.customLocationText);
                Button retryButton = (Button) findViewById(R.id.retry);
                if (spinner.getItemAtPosition(position).toString().equalsIgnoreCase("Custom Location")) {
                    customEdit.setVisibility(View.VISIBLE);
                    gpsAddress.setVisibility(View.INVISIBLE);
                    retryButton.setVisibility(View.INVISIBLE);
                    arrow.setVisibility(View.INVISIBLE);
                    isCustom = true;
                    useGPS = false;
                } else if (spinner.getItemAtPosition(position).toString().equalsIgnoreCase("Current Location")){
                    getAddress(getLocation(locationClient), context);
                    retryButton.setVisibility(View.VISIBLE);
                    arrow.setVisibility(View.VISIBLE);
                    customEdit.setVisibility(View.INVISIBLE);
                    gpsAddress.setVisibility(View.VISIBLE);
                    gpsAddress.setText("Getting current Location");
                    isCustom = false;
                    useGPS = true;
                } else {
                    // Otherwise the pick-up location is whatever the user chooses from spinner.
                    Settings.getSettings().setPickUpLocation(parent.getItemAtPosition(position).toString());
                    customEdit.setVisibility(View.INVISIBLE);
                    gpsAddress.setVisibility(View.INVISIBLE);
                    retryButton.setVisibility(View.INVISIBLE);
                    arrow.setVisibility(View.INVISIBLE);
                    isCustom = false;
                    useGPS = false;
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
        Settings.getSettings().setContext(this);
        //Get Views
        TextView title = (TextView) findViewById(R.id.title);
        TextView otherAddress = (TextView) findViewById(R.id.customLocationText);
        //Set Fonts
        title.setTypeface(Settings.getSettings().getQuicksand());
        otherAddress.setTypeface(Settings.getSettings().getQuicksandBold());
        sendButton.setTypeface(Settings.getSettings().getQuicksand());
        gpsAddress.setTypeface(Settings.getSettings().getQuicksand());

    }

    @Override
    public void update(Observable observable, Object data) {
        if (gpsAddress != null)
            gpsAddress.setText(Settings.getSettings().getPickUpLocation());
    }

    //    Load Shared Preferences
    public String loadName(){
        //  Load Name
        Settings.getSettings().setNameData(getSharedPreferences(Settings.getFilename(), 0));
        return Settings.getSettings().getNameData().getString("sharedName", "No name");
    }
    public String loadNumber(){
        //  Load Phone Number
        Settings.getSettings().setNameData(getSharedPreferences(Settings.getPhoneFile(), 0));
        return Settings.getSettings().getNameData().getString("sharedPhone", "No number");
    }

    /**
     * setSendButton changes the color of the button depending on the status of Safewalk
     * @param status String containing the status of Safewalk in real time
     */
    private void setSendButton(String status) {
        if (status.equals("Available")){
            sendButton.setBackgroundResource(R.drawable.available_button);
            sendButton.setText("Send");
            sendButton.setTextSize(36);
        } else if (status.equals("Busy")){
            sendButton.setBackgroundResource(R.drawable.busy_button);
            sendButton.setText("Busy");
            sendButton.setTextSize(36);
        } else {
            sendButton.setBackgroundResource(R.drawable.not_available_button);
            sendButton.setText("Not Available");
            sendButton.setTextSize(22);
        }
    }


    //----------------- GPS --------------------------------------
    @Override
    public void onStart() {
        super.onStart();
        if (!locationClient.isConnected()) {
            locationClient.connect();
            Log.w(HomeScreenActivity.class.toString(), "Location client connected");
        }
        // Checks if GPS is enabled on device
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS disabled. For best accuracy please enable GPS on device.",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (locationClient.isConnected()) {
            locationClient.disconnect();
            Log.w(HomeScreenActivity.class.toString(), "Location client disconnected");
        }
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

        // Start location updates.
        locationClient.requestLocationUpdates(locationRequest, this);
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this, "Connection Failure : " +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        String message = "Updated Location";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public Location getLocation(LocationClient locationClient) {
        Log.w("GPS connection", Boolean.toString(locationClient.isConnected()));
        try {
            return locationClient.getLastLocation();
        } catch (IllegalStateException e){
            e.printStackTrace();
            return new Location("none");
        }

    }

    public void getAddress(Location location, Context context) {
        if (!location.getProvider().equals("none")) {
            Log.w(HomeScreenActivity.class.toString(), "Location is null");
            (new GetAddressTask(context)).execute(location);
        }
    }

    public void retryLocation(View view) {
        RotateAnimation rotateAnimation = new RotateAnimation(0f, 350f, 50f, 50f);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(1);
        rotateAnimation.setDuration(700);
        arrow.startAnimation(rotateAnimation);
        getAddress(getLocation(locationClient), this);
    }
}
