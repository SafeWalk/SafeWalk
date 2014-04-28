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
import android.widget.*;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

import java.util.Observable;
import java.util.Observer;

/**
 *
 */
public class HomeScreenActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener, Observer {


    // Boolean to check if student is choosing from spinner or inputting address.
    private boolean isCustom;
    private boolean useGPS = false;
    private Button sendButton;
    private TextView gpsAddress;
    private String swStatus;
    private static boolean gpsFinished = false;

    // Location vars
    private LocationClient mLocationClient;
    private LocationManager locationManager;

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
            onSelectedInSpinner(locationSpinner, this);
            sendButton = (Button) findViewById(R.id.send);
            gpsAddress = (TextView) findViewById(R.id.GPSText);
            checkAvailability();
            setFonts();
        }
        // set up locationClient
        mLocationClient = new LocationClient(this, this, this);
        Settings.getSettings().setObserver(this);
        //Reset pick-up location
    }

    @Override
    public void onResume(){
        super.onResume();
        Settings.getSettings().setPickUpLocation("");
        gpsFinished = false;
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
        } else if (swStatus.equals("Available")){
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
            } else if (useGPS) {
                if (gpsFinished && Settings.getSettings().getPickUpLocation().equals("No address found")){
                    AlertDialog emptyLocationAlert = new AlertDialog.Builder(this).create();
                    emptyLocationAlert.setTitle("The GPS couldn't find you");
                    emptyLocationAlert.setMessage("Sorry, you should try another option");
                    emptyLocationAlert.show();
                } else if (gpsFinished) {
                    startActivity(intent);
                } else {
                    AlertDialog emptyLocationAlert = new AlertDialog.Builder(this).create();
                    emptyLocationAlert.setTitle("GPS hasn't finished");
                    emptyLocationAlert.setMessage("GPS will be done locating you shortly");
                    emptyLocationAlert.show();
                }
            } else if (!Settings.getSettings().getPickUpLocation().equals("Select")) {
                startActivity(intent);
            }
        } else if (swStatus.equals("Busy")){
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
                if (spinner.getItemAtPosition(position).toString().equalsIgnoreCase("Custom Location")) {
                    customEdit.setVisibility(View.VISIBLE);
                    gpsAddress.setVisibility(View.INVISIBLE);
                    isCustom = true;
                    useGPS = false;
                } else if (spinner.getItemAtPosition(position).toString().equalsIgnoreCase("Current Location")){
                    getAddress(getLocation(mLocationClient), context);
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


    //----------------- GPS STUFF --------------------------------------
    @Override
    public void onStart() {
        super.onStart();
        mLocationClient.connect();

        // Checks if GPS is enabled on device
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "GPS disabled. For best accuracy please enable GPS on device.",
                            Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
        mLocationClient.disconnect();
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
}
