package com.mac.SafeWalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

/**
 *
 */
public class MainClass extends Activity {

    /*
     * Called when the activity is first created
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.pick_up_choices,
                android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }
}
