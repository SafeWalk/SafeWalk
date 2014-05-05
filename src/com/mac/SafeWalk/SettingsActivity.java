package com.mac.SafeWalk;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SettingsActivity extends Activity implements View.OnClickListener {

    private EditText name;
    private EditText phone;
    private Button save;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        setupVariables();

        Settings.getSettings().setNameData(getSharedPreferences(Settings.getFilename(), 0));
        Settings.getSettings().setPhoneData(getSharedPreferences(Settings.getPhoneFile(), 0));

        setFonts();

    }

    private void setFonts() {
        Settings.getSettings().setContext(this);
        name.setTypeface(Settings.getSettings().getQuicksandBold());
        phone.setTypeface(Settings.getSettings().getQuicksandBold());
        save.setTypeface(Settings.getSettings().getQuicksand());
    }


    //  Introduce all the variables used in the activity and link it to the layout button
    public void setupVariables(){
        save = (Button) findViewById(R.id.saveButton);
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.number);
        save.setOnClickListener(this);
    }

    public void onClick(View view) {
        //  Save name (if valid name)
        String enteredName = name.getText().toString();
        if (enteredName.equals("")){
            Toast.makeText(getApplicationContext(), "Please enter your name", Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences.Editor nameEditor = Settings.getSettings().getNameData().edit();
            nameEditor.putString("sharedName", enteredName);
            nameEditor.commit();
        }


        //  Save phone number (if valid number)
        String enteredNumber = phone.getText().toString();
        if (enteredNumber.length()==10){
            SharedPreferences.Editor phoneEditor = Settings.getSettings().getPhoneData().edit();
            phoneEditor.putString("sharedPhone", enteredNumber);
            phoneEditor.commit();
        }
        else {
            Toast.makeText(getApplicationContext(), "Invalid phone number", Toast.LENGTH_LONG).show();
        }

        if (checkForName(enteredName) && enteredNumber.length()==10){
//            Go back to main activity if the data is saved
            Intent homeScreen = new Intent(this, HomeScreenActivity.class);
            startActivity(homeScreen);
//            Display a message
            Toast.makeText(getApplicationContext(), "Your settings have been successfully saved", Toast.LENGTH_SHORT).show();
        }
    }

    //    Return false if no name is entered
    public boolean checkForName (String s){
        if (s.equals("")){
            return false;
        } else {
            return true;
        }
    }
}
