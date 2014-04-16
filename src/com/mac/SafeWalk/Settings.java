package com.mac.SafeWalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

/**
 * Utilities class that keeps track of global variables. It is a singleton so there
 * is only one instance of this class.
 */
public class Settings {

    private static final Settings settings = new Settings();
    private Context context;

    private String pickUpLocation;
    private long lastSendTime = 0;
    private final static String SAFEWALK_PHONE_NUMBER = "6123237668"; //"6512420083";  //Currently Kohei's number
    private String[] swLocations = {"Wallace", "Doty", "Wallace", "Doty", "Turck", "Dupre", "Campus Center", "Library",
                                    "Old Main", "Janet Wallace", "Olin Rice", "Stadium", "Carnegie", "30 Mac",
                                     "77 Mac", "Neil Hall", "Other"};

    // Phone and name data
    private SharedPreferences nameData;
    private SharedPreferences phoneData;

    // Where to save the name and phone data
    private static String filename = "MyName";
    private static String phoneFile = "MyPhoneNumber";

    private void Settings() {}

    public static Settings getSettings() {
        return settings;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
    }

    public static String getSafewalkPhoneNumber() {
        return SAFEWALK_PHONE_NUMBER;
    }

    public SharedPreferences getNameData(){
        return nameData;
    }

    public void setNameData(SharedPreferences content) {
        this.nameData = content;
    }

    public SharedPreferences getPhoneData(){
        return this.phoneData;
    }

    public void setPhoneData(SharedPreferences content) {
        this.phoneData = content;
    }

    public static String getFilename() {
        return filename;
    }

    public static String getPhoneFile() {
        return phoneFile;
    }

    public long getLastSendTime() {
        return lastSendTime;
    }

    public void setLastSendTime(long lastSendTime) {
        this.lastSendTime = lastSendTime;
    }

    public Typeface getQuicksand() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/quicksand.otf");
    }

    public Typeface getQuicksandBold() {
        return Typeface.createFromAsset(context.getAssets(), "fonts/quicksand_bold.otf");
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String[] swLocations() {
        return swLocations;
    }
}
