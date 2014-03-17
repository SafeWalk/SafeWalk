package com.mac.SafeWalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * This activity sends the SMS to a predetermined SafeWalk number
 */
public class SendMessageActivity extends Activity {

    private static final long WAIT_TIME = 10000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        if (Settings.getSettings().getLastSendTime() <= System.currentTimeMillis() - WAIT_TIME) {
            Log.w("sent", "MESSAGE SENT");
            sendSms(Settings.getSafewalkPhoneNumber());
            Settings.getSettings().setLastSendTime(System.currentTimeMillis());
        } else {
            notifyTimeLimit();
            Log.w("sent", "MESSAGE NOT SENT");
        }
    }

    /**
     * This method creates a warning dialog to let the user know they have reached the maximum
     * number of messages in a given time.
     */
    private void notifyTimeLimit() {
        AlertDialog timeLimitDialog = new AlertDialog.Builder(this).create();
        timeLimitDialog.setTitle("Message limit reached");
        timeLimitDialog.setMessage("You can only send one message every 10 seconds");
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

        String sms = Settings.getSettings().getPickUpLocation()
                + "\nThe request has been placed by " + nameReturned
                + ". \nFor any additional information please contact the user at " + numberReturned +".";

        Log.w("sent", sms);

        try {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, sms, null, null);
        } catch (Exception e) {
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
     * Set fonts
     */
    private void setFontsAndText() {
        //Make font
        Typeface quicksand = Typeface.createFromAsset(getAssets(), "fonts/quicksand.otf");
        //Get Views
        TextView locationDisplay = (TextView) findViewById(R.id.location);
        TextView topMessage = (TextView) findViewById(R.id.safewalk_notified);
        Button callButton = (Button) findViewById(R.id.call_safewalk);
        //Set fonts
        locationDisplay.setTypeface(quicksand);
        topMessage.setTypeface(quicksand);
        callButton.setTypeface(quicksand);
        //Set text (pick up location)
        locationDisplay.setText(Settings.getSettings().getPickUpLocation());
    }

}
