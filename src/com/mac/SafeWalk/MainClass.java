package com.mac.SafeWalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 *
 */
public class MainClass extends Activity {

    /*
     * Called when the activity is first created
     */
    private String pickUpLocation;
    public final static String EXTRA_MESSAGE = "com.mac.SafeWalk.MESSAGE";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pick_up_choices,
                android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        pickUpLocation = (String) parent.getItemAtPosition(pos);
    }

    /**
     * Called when the user clicks the send button.
     */
    public void sendClick(View view) {
        Intent intent = new Intent(this, SendMessageToSafeWalk.class);
        intent.putExtra(EXTRA_MESSAGE, pickUpLocation);
        startActivity(intent);
    }

}
