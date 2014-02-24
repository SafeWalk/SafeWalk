package com.mac.SafeWalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

/**
 *
 */
public class MainClass extends Activity implements AdapterView.OnItemSelectedListener {

    /*
     * Called when the activity is first created
     */
    private String pickUpLocation;
    private String customPickUpLocation;
    private boolean isCustom = false;
    public final static String SPINNER_LOCATION = "com.mac.SafeWalk.SPINNER_MESSAGE";
    public final static String CUSTOM_LOCATION = "com.mac.SafeWalk.CUSTOM_MESSAGE";
    public final static String IS_CUSTOM = "com.mac.SafeWalk.IS_CUSTOM";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.pick_up_choices,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        pickUpLocation = parent.getItemAtPosition(pos).toString();
    }
    public void onNothingSelected(AdapterView<?> parent)
    {
        EditText customEdit = (EditText)findViewById(R.id.customLocationText);
        pickUpLocation = customEdit.getText().toString();

    }


    /**
     * Called when the user clicks the send button.
     */
    public void sendClick(View view) {

        Intent intent = new Intent(this, SendMessageToSafeWalk.class);
        //Bundle extras = new Bundle();
        //extras.putString(SPINNER_LOCATION, pickUpLocation);
        // Retrieve address if user entered it.
//        EditText customEdit = (EditText)findViewById(R.id.customLocationText);
//        if (customEdit.getText().toString() != null)
//        {
//            isCustom = true;
//            customPickUpLocation = customEdit.getText().toString();
//            extras.putString(CUSTOM_LOCATION, customPickUpLocation);
//        };
        //extras.putBoolean(IS_CUSTOM, isCustom);
        //intent.putExtras(extras);
        intent.putExtra(CUSTOM_LOCATION, pickUpLocation);
        startActivity(intent);
    }

}
