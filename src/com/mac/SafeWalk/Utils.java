package com.mac.SafeWalk;

import android.content.SharedPreferences;

/**
 * Utilities class that keeps track of global variables. It is a singleton so there
 * is only one instance of this class.
 */
public class Utils {

    private static final Utils UTILS = new Utils();

    // Phone and name data
    private SharedPreferences nameData;
    private SharedPreferences phoneData;

    // Where to save the name and phone data
    public static String filename = "MyName";
    public static String phoneFile = "MyPhoneNumber";

    private String pickUpLocation;
    private final static String SAFEWALK_PHONE_NUMBER = "6128393666"; //"6512420083";  //Currently Kohei's number

    private void Utils() {}

    public static Utils getUtils() {
        return UTILS;
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
}
