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
        name = (EditText) findViewById(R.id.name);
        phone = (EditText) findViewById(R.id.number);
        save.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch ((view.getId())){
            case R.id.saveButton:
                save();
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
}
