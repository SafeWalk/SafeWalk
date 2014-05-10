package com.mac.SafeWalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;

/**
 * Utilities class that keeps track of global variables. It is a singleton so there
 * is only one instance of this class.
 */
public class Settings extends Observable{

    private static final Settings settings = new Settings(); //singleton
    private Settings(){}
    private Context context;

    private String pickUpLocation = "";
    private double pickUpLat = 0;
    private double pickUpLon = 0;
    private final static String SAFEWALK_PHONE_NUMBER = "6123237668"; //"6512420083";  //Currently Kohei's number
    private String[] swLocations = {"Select", "Current Location", "Custom Location", "Wallace", "Doty", "Turck", "Dupre", "GDD",
                                    "30 Mac", "77 Mac", "Campus Center", "Library", "Leonard Center", "Janet Wallace",
                                    "Olin Rice", "Stadium", "Carnegie", "Neil Hall"};

    private String[] campusBuildings = {"Wallace", "Doty", "Turck", "Dupre", "GDD", "30 Mac", "77 Mac", "Campus Center",
                                        "Library", "Leonard Center", "Janet Wallace", "Olin Rice", "Stadium", "Carnegie", "Neil Hall"};

//    private List<String> campusBuildings= new ArrayList<String>();

    // Coordinates for each of the campus buildings
    HashMap<String, double[]> campusCoordinates = new HashMap<String, double[]>();


    // Phone and name data
    private SharedPreferences nameData;
    private SharedPreferences phoneData;
    private SharedPreferences lastSendTimeEditor;

    // Where to save the name and phone data
    private static String filename = "MyName";
    private static String phoneFile = "MyPhoneNumber";

    public void setObserver(Object o){
        settings.addObserver((java.util.Observer) o);
    }

    public static Settings getSettings() {
        return settings;
    }

    public String getPickUpLocation() {
        return pickUpLocation;
    }

    public void setPickUpLocation(String pickUpLocation) {
        this.pickUpLocation = pickUpLocation;
        setChanged();
        notifyObservers(pickUpLocation);
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

    public SharedPreferences getLastSendTimeEditor() {
        return this.lastSendTimeEditor;
    }

    public void setLastSendTimeEditor(SharedPreferences lastSendTimeEditor) {
        this.lastSendTimeEditor = lastSendTimeEditor;
    }

    public void setPickUpCoordinates(double lat, double lon) {
        this.pickUpLat = lat;
        this.pickUpLon = lon;
    }

    public double[] getPickUpCoordinates() {
        return new double[]{pickUpLat, pickUpLon};
    }
}
