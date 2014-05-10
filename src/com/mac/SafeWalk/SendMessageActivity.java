package com.mac.SafeWalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.firebase.client.Firebase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * This activity sends the SMS to a predetermined SafeWalk number.
 */
public class SendMessageActivity extends Activity {

    // Time in milliseconds of how long user must wait after sending a request to safewalk.
    private static final long WAIT_TIME = 10000; //1200000;

    // The time of when user sent last request.
    private static final String LAST_TIME = "lastSendTime";

    // Stores the location of the pickup location until it is the appropriate time to change it.
    private static String tempLocation;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Settings.getSettings().setLastSendTimeEditor(getSharedPreferences(LAST_TIME, 0));

        // Set view
        setContentView(R.layout.message_sent);
        setFontsAndText();

        // Sends SMS to provided phone number.
        checkTimeBeforeSend();
    }

    /**
     * This method makes sure the user waited the specified time before sending another message to safewalk
     * The waiting time is given by the constant WAIT_TIME in milliseconds
     */
    private void checkTimeBeforeSend() {
        SharedPreferences.Editor lastSendTimeEditor;
        long lastSendTime = getSharedPreferences(LAST_TIME, 0).getLong(LAST_TIME, -1);
        TextView locationDisplay = (TextView) findViewById(R.id.location);

        // Send sms message if the user does not have any waiting time left.
        if (lastSendTime <= System.currentTimeMillis() - WAIT_TIME) {

            // Send sms to designated phone number.
            sendSms(Settings.getSafewalkPhoneNumber());
            // log if sms message was sent.
            Log.w("SendMessageActivity", "Location SENT");

            lastSendTimeEditor = Settings.getSettings().getLastSendTimeEditor().edit();
            lastSendTimeEditor.putLong("lastSendTime", System.currentTimeMillis());
            lastSendTimeEditor.commit();

            // Set View (pick up location)
            locationDisplay.setText(Settings.getSettings().getPickUpLocation());
            // Set temp location
            tempLocation = Settings.getSettings().getPickUpLocation();
        } else {
            notifyTimeLimit(lastSendTime);
            // log if message was not sent.
            Log.w("SendMessageActivity", "Location NOT SENT");
            //Set View (previous pick up location)
            locationDisplay.setText(tempLocation);
        }
    }

    /**
     * This method creates a warning dialog to let the user know they have reached the maximum
     * number of messages in a given time.
     */
    private void notifyTimeLimit(long lastSendTime) {
        long timeBetweenAttempts = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - lastSendTime);
        AlertDialog timeLimitDialog = new AlertDialog.Builder(this).create();
        timeLimitDialog.setTitle("Unable to send message");
        timeLimitDialog.setMessage("Safewalk was notified " + timeBetweenAttempts + " minutes ago to pick you up.");
        timeLimitDialog.show();
    }

    /**
     * Sends SMS to a phone number. If invalid number, a dialog informs the user.
     */
    public void sendSms(String phoneNumber) {
        //  Load Name
        Settings.getSettings().setNameData(getSharedPreferences(Settings.getFilename(), 0));
        String nameReturned = Settings.getSettings().getNameData().getString("sharedName", "Couldn't load data");

        //  Load Phone Number
        Settings.getSettings().setPhoneData(getSharedPreferences(Settings.getPhoneFile(), 0));
        String numberReturned = Settings.getSettings().getPhoneData().getString("sharedPhone", "Couldn't load data");

        // Format text message. Uncomment if using SMS as communication method
//        String sms = Settings.getSettings().getPickUpLocation()
//                + "\nThe request has been placed by " + nameReturned
//                + ". \nFor any additional information please contact the user at " + numberReturned +".";

        // Try sending sms
        try {
            Firebase listRef = new Firebase("https://safewalk.firebaseio.com/Notifications");
            Firebase listRefPush = listRef.push();
            HashMap<String, String> infoToPush = new HashMap<String, String>();
            infoToPush.put("Name", nameReturned);
            infoToPush.put("Number", numberReturned);
            infoToPush.put("Location", Settings.getSettings().getPickUpLocation());
            infoToPush.put("Latitude", Double.toString(Settings.getSettings().getPickUpCoordinates()[0]));
            infoToPush.put("Longitude", Double.toString(Settings.getSettings().getPickUpCoordinates()[1]));
//            if (getIntent().getExtras().getBoolean("gps")){
//                infoToPush.put("Latitude", Double.toString(Settings.getSettings().getPickUpCoordinates()[0]));
//                infoToPush.put("Longitude", Double.toString(Settings.getSettings().getPickUpCoordinates()[1]));
//            }
            listRefPush.setValue(infoToPush);
           //SmsManager.getDefault().sendTextMessage(phoneNumber, null, sms, null, null); // Uncomment if using SMS as communication method
        } catch (Exception e) {
            // Show alert dialog if unable to send sms
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            AlertDialog dialog = alertDialogBuilder.create();

            dialog.setMessage(e.getMessage());
            dialog.show();
        }
    }

    /**
     * This method allows the user to call Safewalk from the "Safewalk notified" screen
     * in case they want to cancel or have a special request.
     * @param view Call Safewalk button.
     */
    public void callSafewalk(View view) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + Settings.getSafewalkPhoneNumber()));
        startActivity(callIntent);
    }

    /**
     * Set fonts for the view
     */
    private void setFontsAndText() {

        // Make font
        Settings.getSettings().setContext(this);

        // Get Views
        TextView locationDisplay = (TextView) findViewById(R.id.location);
        TextView topMessage = (TextView) findViewById(R.id.safewalk_notified);
        Button callButton = (Button) findViewById(R.id.call_safewalk);

        // Set fonts
        locationDisplay.setTypeface(Settings.getSettings().getQuicksand());
        topMessage.setTypeface(Settings.getSettings().getQuicksand());
        callButton.setTypeface(Settings.getSettings().getQuicksand());
    }

}
