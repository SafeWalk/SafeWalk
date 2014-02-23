package com.mac.SafeWalk;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * This activity should send the SMS to a predetermined SafeWalk number
 */
public class SendMessageToSafeWalk extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String location = intent.getStringExtra(MainClass.EXTRA_MESSAGE);

        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(location);

        setContentView(textView);
    }
}
