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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        // Displays pick-up location.
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(Utils.getUtils().getPickUpLocation());
        setContentView(textView);

        // Sends SMS to provided phone number.
        sendSms(Utils.getSafewalkPhoneNumber());
    }

    /*
    Sends SMS to a phone number. If invalid number, a dialog informs the user.
     */
    public void sendSms(String phoneNumber) {
        try
        {
            SmsManager.getDefault().sendTextMessage(phoneNumber, null, Utils.getUtils().getPickUpLocation(), null, null);
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
