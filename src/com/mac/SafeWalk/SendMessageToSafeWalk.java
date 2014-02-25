package com.mac.SafeWalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

import static com.mac.SafeWalk.MainClass.*;

/**
 * This activity should sends the SMS to a predetermined SafeWalk number
 */
public class SendMessageToSafeWalk extends Activity {


    // This is the pick-up location
    private String location;
    // This is the predetermined SafeWalk number. Currently it is Kohei's number.
    public final static String PHONE_NUMBER = "6512420083";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        // Gets the pick-up location from previous activity.
        location = intent.getStringExtra(LOCATION);

        // Displays pick-up location.
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(location);
        setContentView(textView);

        // Sends SMS to provided phone number.
        sendSms(PHONE_NUMBER);
    }



    /*
    Sends SMS to a phone number. If invalid number, a dialog informs the user.
     */
    public void sendSms(String phoneNumber)
    {
        try
        {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, location, null, null);
        }
        catch (Exception e)
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            AlertDialog dialog = alertDialogBuilder.create();

            dialog.setMessage(e.getMessage());
            dialog.show();
        }
    }
}
