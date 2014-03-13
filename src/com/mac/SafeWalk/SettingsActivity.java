package com.mac.SafeWalk;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SettingsActivity extends Activity implements View.OnClickListener {

    private EditText name;
    private EditText phone;
    private TextView nameResult;
    private TextView phoneResult;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        setupVariables();

        Settings.getSettings().setNameData(getSharedPreferences(Settings.getFilename(), 0));
        Settings.getSettings().setPhoneData(getSharedPreferences(Settings.getPhoneFile(), 0));

    }

    //  Introduce all the variables used in the activity and link it to the layout button
    public void setupVariables(){

        Button save = (Button) findViewById(R.id.saveButton);
        Button load = (Button) findViewById(R.id.loadButton);

        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.number);
        nameResult = (TextView) findViewById(R.id.displayName);
        phoneResult = (TextView) findViewById(R.id.displayNumber);

        save.setOnClickListener(this);
        load.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch ( (view.getId())){
            case R.id.saveButton:
                save();
                break;

            case R.id.loadButton:
                load();
                break;

        }
    }

    public void save(){
        //  Save name
        String stringData1 = name.getText().toString();
        SharedPreferences.Editor nameEditor = Settings.getSettings().getNameData().edit();
        nameEditor.putString("sharedName", stringData1);
        nameEditor.commit();
        //  Save phone number
        String stringData2 = phone.getText().toString();
        SharedPreferences.Editor phoneEditor = Settings.getSettings().getPhoneData().edit();
        phoneEditor.putString("sharedPhone", stringData2);
        phoneEditor.commit();
    }

    public void load(){
        //  Load Name and display
        Settings.getSettings().setNameData(getSharedPreferences(Settings.getFilename(), 0));
        String nameReturned = Settings.getSettings().getNameData().getString("sharedName", "Couldn't load data");
        nameResult.setText("Your name is: " + nameReturned);
        //  Load Phone Number and display
        Settings.getSettings().setNameData(getSharedPreferences(Settings.getPhoneFile(), 0));
        String numberReturned = Settings.getSettings().getNameData().getString("sharedPhone", "Couldn't load data");
        phoneResult.setText("Your phone number is: " + numberReturned);
    }

}
