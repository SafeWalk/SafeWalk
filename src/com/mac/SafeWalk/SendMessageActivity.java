package com.mac.SafeWalk;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.TextView;

/**
 * This activity should sends the SMS to a predetermined SafeWalk number
 */
public class SendMessageActivity extends Activity {

    private TextView contact_Info;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_sent);
        setFontsAndText();
        // Sends SMS to provided phone number.
        sendSms(Utils.getSafewalkPhoneNumber());

        load();
    }

    /**
     * Display contact information
     */


    public void load(){

        contact_Info = (TextView) findViewById(R.id.contact);

        //  Load Name
        Utils.getUtils().setNameData(getSharedPreferences(Utils.getUtils().filename, 0)); //= getSharedPreferences(Utils.getUtils().filename, 0);
        String nameReturned = Utils.getUtils().getNameData().getString("sharedName", "Couldn't load data");

        //  Load Phone Number
        Utils.getUtils().setPhoneData(getSharedPreferences(Utils.getUtils().phoneFile, 0)); //= getSharedPreferences(Utils.getUtils().phoneFile, 0);
        String numberReturned = Utils.getUtils().getPhoneData().getString("sharedNumber", "Couldn't load data");

        //Display
        contact_Info.setText("Name: " + nameReturned + "\nNumber: "+ numberReturned);
    }



    /**
     * Sends SMS to a phone number. If invalid number, a dialog informs the user.
     */
    public void sendSms(String phoneNumber) {

        //  Load Name
        Utils.getUtils().setNameData(getSharedPreferences(Utils.getUtils().filename, 0)); //= getSharedPreferences(Utils.getUtils().filename, 0);
        String nameReturned = Utils.getUtils().getNameData().getString("sharedName", "Couldn't load data");

        //  Load Phone Number
        Utils.getUtils().setPhoneData(getSharedPreferences(Utils.getUtils().phoneFile, 0));// = getSharedPreferences(Utils.getUtils().phoneFile, 0);
        String numberReturned = Utils.getUtils().getPhoneData().getString("sharedNumber", "Couldn't load data");

        String sms = Utils.getUtils().getPickUpLocation() + "\nThe request has been placed by " + nameReturned + ". \nFor any additional information please contact the user at " + numberReturned +".";

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
        callIntent.setData(Uri.parse("tel:" + Utils.getSafewalkPhoneNumber()));
        startActivity(callIntent);
    }

    /*
     * Changes the fonts and color of the fonts
     */
    private void setFontsAndText() {
        //Make font
        Typeface quicksand = Typeface.createFromAsset(getAssets(), "fonts/quicksand.otf");
        //Get Views
        TextView locationDisplay = (TextView) findViewById(R.id.location);
        TextView topMessage = (TextView) findViewById(R.id.safewalk_notified);
        //Set fonts
        locationDisplay.setTypeface(quicksand);
        topMessage.setTypeface(quicksand);
        //Set text (pick up location)
        locationDisplay.setText(Utils.getUtils().getPickUpLocation());
    }

}
