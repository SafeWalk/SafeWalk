package com.mac.SafeWalk;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * This class extends ArrayAdapter to create a custom spinner for the app.
 * Created by guillermovera on 4/16/14.
 */
public class SafewalkArrayAdapter extends ArrayAdapter<String> {

    private String[] pickUpLocations;
    private LayoutInflater inflater;

    public SafewalkArrayAdapter(Context context, int resource, String[] array, LayoutInflater inflater) {
        super(context, resource, array);
        pickUpLocations = array;
        this.inflater = inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, false);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getCustomView(position, convertView, parent, true);
    }

    public View getCustomView(int position, View convertView, ViewGroup parent, Boolean black) {
        View mySpinner = inflater.inflate(R.layout.spinner_style, parent, false);
        TextView spinnerOption = (TextView) mySpinner.findViewById(R.id.spinnerView);
        spinnerOption.setText(pickUpLocations[position]);
        if (black) {
            spinnerOption.setTextColor(Color.BLACK);
            spinnerOption.setTypeface(Settings.getSettings().getQuicksandBold());
            spinnerOption.setHeight(90);
            spinnerOption.setWidth(300);
            spinnerOption.setBackgroundResource(R.drawable.spinner_select);
        } else {
            spinnerOption.setTypeface(Settings.getSettings().getQuicksand());
        }
        return mySpinner;
    }
}
