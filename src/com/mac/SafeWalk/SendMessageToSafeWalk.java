package com.mac.SafeWalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

import static com.mac.SafeWalk.MainClass.*;

/**
 * This activity should send the SMS to a predetermined SafeWalk number
 */
public class SendMessageToSafeWalk extends Activity {

    private boolean isCustom = false;
    private String location;
    public final static String PHONE_NUMBER = "6512420083";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        location = intent.getStringExtra(CUSTOM_LOCATION);


        //Bundle extras = getIntent().getExtras();

//        isCustom = extras.getBoolean(MainClass.IS_CUSTOM);
//        if (isCustom == true)
//        {
//            location = extras.getString(MainClass.CUSTOM_LOCATION);
//        } else {
//            location = extras.getString(MainClass.SPINNER_LOCATION);
//        }

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(location);

        setContentView(textView);



        try
        {
            SmsManager.getDefault().sendTextMessage(PHONE_NUMBER, null, location, null, null);
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
