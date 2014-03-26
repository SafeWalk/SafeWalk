package com.mac.SafeWalk;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

/**
 *
 */
public class HomeScreenActivity extends Activity {


    // Boolean to check if student is choosing from spinner or inputting address.
    private boolean isCustom;
    private Button sendButton;
    private String swStatus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        If no name and phone number saved, go to settingsActivity
        String name = loadName();
        String phoneNumber = loadNumber();
        if (name.equals("No name") && phoneNumber.equals("No number")){
            Intent settings = new Intent(this, SettingsActivity.class);
            startActivity(settings);
        } else {
            setContentView(R.layout.main);
            // set up locationSpinner
            Spinner locationSpinner = setSpinner();
            onSelectedInSpinner(locationSpinner);
            sendButton = (Button) findViewById(R.id.send);
            checkAvailability();
            setFonts();
        }
    }

    /**
     * Checks the availability of Safewalk though Firebase
     */
    private void checkAvailability() {
        Firebase ref = new Firebase("https://safewalk.firebaseio.com/");
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
            EditText customEdit = (EditText)findViewById(R.id.customLocationText);
            if (isCustom) {
                Settings.getSettings().setPickUpLocation(retrieveLocation(customEdit));
            }
            Intent intent = new Intent(this, SendMessageActivity.class);
            startActivity(intent);
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

}
