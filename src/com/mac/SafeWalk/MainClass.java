package com.mac.SafeWalk;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 *
 */
public class MainClass extends Activity {


    // Boolean to check if student is choosing from spinner or inputting address.
    private boolean isCustom;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        // set up spinner
        Spinner spinner = setSpinner();
        onSelectedInSpinner(spinner);
        setFonts();

        //code to update safewalk users in realtime

        // Create a reference to a Firebase location
        Firebase ref = new Firebase("https://safewalk.firebaseio.com/");
        ref.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot snap) {
                TextView Avail = (TextView) findViewById(R.id.Availability);
                Avail.setText(snap.getValue(String.class));

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
     * Sends the pick-up location to the next activity so that the Sms Manager can send a text message.
     * Accessed when "send" button is clicked. Retrieves data from EditText via retrieveLocation method.
     */
    public void sendClick(View view) {
        EditText customEdit = (EditText)findViewById(R.id.customLocationText);
        if (isCustom) {
            Utils.getUtils().setPickUpLocation(retrieveLocation(customEdit));
        }
        Intent intent = new Intent(this, SendMessageToSafeWalk.class);
        startActivity(intent);

    }

    /**
     * Go to settings when the setting button is pressed
     */
    public void openSetting (View view){
        Intent settings = new Intent(this, Settings.class);
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
     * Sets the pick up location of the variable pickUpLocation on the Utils class depending on the
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
                    Utils.getUtils().setPickUpLocation(parent.getItemAtPosition(position).toString());
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
    }

}
