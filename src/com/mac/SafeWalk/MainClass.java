package com.mac.SafeWalk;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 *
 */
public class MainClass extends Activity {


    // The location gets stored here.
    private String pickUpLocation;
    // Boolean to check if student is choosing from spinner or inputting address.
    private boolean isCustom;
    public final static String LOCATION = "com.mac.SafeWalk.LOCATION";



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // set up spinner
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.pick_up_choices,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
                    pickUpLocation = parent.getItemAtPosition(position).toString();
                }
            }

            // Leave this method; this has to be here.
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    // Retrieves location string from EditText.
    public String retrieveLocation(EditText editText)
    {
        String location;
        location = editText.getText().toString();
        return location;
    }

    /*
    Sends the pick-up location to the next activity so that the Sms Manager can send a text message.
    Accessed when "send" button is clicked. Retrieves data from EditText via retrieveLocation method.
     */
    public void sendClick(View view)
    {

        EditText customEdit = (EditText)findViewById(R.id.customLocationText);
        if (isCustom == true)
        {
            pickUpLocation = retrieveLocation(customEdit);
        }

        Intent intent = new Intent(this, SendMessageToSafeWalk.class);
        intent.putExtra(LOCATION, pickUpLocation);
        startActivity(intent);

    }



}
