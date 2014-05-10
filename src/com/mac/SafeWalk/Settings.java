package com.mac.SafeWalk;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;

import java.util.HashMap;
import java.util.Observable;

/**
 * Utilities class that keeps track of global variables. It is a singleton so there
 * is only one instance of this class.
 */
public class Settings extends Observable {

    private static final Settings settings = new Settings(); //singleton
    private Settings(){ setCampusCoordinates(); }
    private Context context;

    private String pickUpLocation = "";
    private double pickUpLat = 0;
    private double pickUpLon = 0;
    private final static String SAFEWALK_PHONE_NUMBER = "6123237668"; //"6512420083";  //Currently Kohei's number
    private String[] swLocations = {"Select", "Current Location", "Custom Location", "Wallace", "Doty", "Turck", "Dupre", "GDD",
                                    "30 Mac", "77 Mac", "Campus Center", "Library", "Leonard Center", "Janet Wallace",
                                    "Olin Rice", "Stadium", "Carnegie", "Neil Hall"};

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

    private void setCampusCoordinates(){
            campusCoordinates.put(swLocations[3], new double[]{44.940858, -93.169073});
            campusCoordinates.put(swLocations[4], new double[]{44.94082, -93.168579});
            campusCoordinates.put(swLocations[5], new double[]{44.940342, -93.16859});
            campusCoordinates.put(swLocations[6], new double[]{44.941025, -93.167839});
            campusCoordinates.put(swLocations[7], new double[]{44.940813, -93.170414});
            campusCoordinates.put(swLocations[8], new double[]{44.940661, -93.169108});
            campusCoordinates.put(swLocations[9], new double[]{44.940461, -93.169907});
            campusCoordinates.put(swLocations[10], new double[]{44.939385, -93.167689});
            campusCoordinates.put(swLocations[11], new double[]{44.938489, -93.168236});
            campusCoordinates.put(swLocations[12], new double[]{44.937091, -93.167807});
            campusCoordinates.put(swLocations[13], new double[]{44.937589, -93.169561});
            campusCoordinates.put(swLocations[14], new double[]{44.936708, -93.168976});
            campusCoordinates.put(swLocations[15], new double[]{44.935371, -93.168172});
            campusCoordinates.put(swLocations[16], new double[]{44.93872, -93.169164});
            campusCoordinates.put(swLocations[17], new double[]{44.937388, -93.169207});
    }
}
