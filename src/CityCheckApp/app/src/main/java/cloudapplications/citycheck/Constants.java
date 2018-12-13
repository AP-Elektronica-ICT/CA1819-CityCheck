package cloudapplications.citycheck;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cloudapplications.citycheck.Models.DoelLocatie;

public class Constants {

    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS = 12 * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 200000;

    public static final HashMap<String, LatLng> LANDMARKS = new HashMap<String, LatLng>();
    private List<DoelLocatie> TargetLocations = new ArrayList<>();

    static {
//        for (int i = 0; GameActivity.targetLocations.size() < ; i++) {
//
//        }
        // San Francisco International Airport.
        LANDMARKS.put("Moscone South", new LatLng(37.783888, -122.4009012));

        // Googleplex.
        LANDMARKS.put("Japantown", new LatLng(37.785281, -122.4296384));

        // Test
        LANDMARKS.put("SFO", new LatLng(37.621313, -122.378955));
    }

    public void setTargetLocations(ArrayList<DoelLocatie> targetLocations) {
        TargetLocations = targetLocations;
    }
}