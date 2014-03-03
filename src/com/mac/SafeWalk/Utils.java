package com.mac.SafeWalk;

/**
 * Utilities class that keeps track of global variables. It is a singleton so there
 * is only one instance of this class.
 */
public class Utils {

    private static final Utils UTILS = new Utils();

    private String pickUpLocation;
    private final static String SAFEWALK_PHONE_NUMBER = "6123237668"; //"6512420083";  //Currently Kohei's number

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
}
