package cloudapplications.citycheck;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.Team;

public class MyTeam extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener{

    private static final String TAG = MyTeam.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private NetworkManager service;
    private GoogleApiClient myGoogleApiClient;
    private LocationRequest myLocationRequest;
    private GoogleMap map;
    private Marker Me;
    private Activity activity;
    private List<Polyline> polylines;
    private int GameCode;
    private String TeamNaam;

    public Location newLocation;
    public List<Locatie> Traces;

    // Public methoden
    public MyTeam(Activity activityIn, GoogleMap kaart, int gameCode, String teamNaam) {
        activity = activityIn;
        map = kaart;
        Traces = new ArrayList<>();
        polylines = new ArrayList<Polyline>();
        GameCode = gameCode;
        TeamNaam = teamNaam;
        myGoogleApiClient = new GoogleApiClient.Builder(activity.getBaseContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        myLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(3 * 1000)        // 3 seconds, in milliseconds
                .setFastestInterval(3 * 1000); // 3 second, in milliseconds
    }

    public void StartConnection() {
        if (!myGoogleApiClient.isConnected()) {
            myGoogleApiClient.connect();
            Log.d(TAG, "connect to google api client");
        }
    }

    public void StopConnection() {
        if (myGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(myGoogleApiClient, this);
            myGoogleApiClient.disconnect();
            Log.d(TAG, "connection & updates stopped");
        }
    }

    public void HandleNewLocation(Locatie location, int time) {
        // Test data
        // location= new LatLng((r.nextDouble()*(51.2500 - 50.1800) + 50.1800),(r.nextDouble()* (4.8025 - 4.0000) + 4.0000));

        Log.d(TAG, "handle new location: " + location.getLat() + ", " + location.getLong());
        placeMarker(location);
        //tijd om het spel te starten, pas na 1 minuut zal een trace achtergelaten worden
        if(time>59){
            Log.d(TAG, "pad tekenen: " + time);
            Traces.add(new Locatie(location.getLat(), location.getLong()));
            //Log.d(TAG, Integer.toString(Traces.size()));
            drawPath();
            sendLocationToDatabase(location);
        }

    }

    public void ClearTraces(){
        Traces.clear();
        for(Polyline line: polylines){
            //lijn van map verwijderen
            line.remove();
        }
        //lijnen uit array verwijderen
        polylines.clear();
    }

    public boolean CheckLocationPermission() {
        Log.d(TAG, "check if permissions are granted");
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(activity)
                        .setTitle("Permission to access location")
                        .setMessage("Access to your location is needed to play the game.")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(activity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            Log.d(TAG, "permissions granted in check permissions");
            return true;
        }
    }

    // Private helpermethoden
    private void placeMarker(Locatie location) {
        Log.d(TAG, "marker on my position");
        LatLng loc = new LatLng(location.getLat(), location.getLong());
        if (Me == null) {
            Me = map.addMarker(new MarkerOptions()
                    .position(loc)
                    .title("Me")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carrot)));
        } else {
            Me.setPosition(loc);
        }
    }

    private void drawPath() {
        // Elke keer het traject tussen de laatste locatie en de huidige locatie als polyline tekenen
        if (Traces.size() > 1) { // er moeten minstens 2 locaties in de traces zitten om een trace te kunnen tekenen

            polylines.add(map.addPolyline(new PolylineOptions()
                    .add(
                            new LatLng(Traces.get(Traces.size() - 2).getLat(), Traces.get(Traces.size() - 2).getLong()),
                            new LatLng(Traces.get(Traces.size() - 1).getLat(), Traces.get(Traces.size() - 1).getLong()))
                    .width(7f)));
        }
    }

    private void sendLocationToDatabase(Locatie location) {
        service = NetworkManager.getInstance();
        service.postHuidigeLocatie(GameCode, TeamNaam, location, new NetworkResponseListener<Team>() {
            @Override
            public void onResponseReceived(Team team) {
                //Log.d(TAG, "location send to database: " + team.getLocatie().getLat() + team.getLocatie().getLong());
            }

            @Override
            public void onError() {
                Log.d(TAG, "error in sending location to database");
            }
        });
    }


    // Callbacks
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        if(CheckLocationPermission()){
            Log.d(TAG, "permissions ok, start location updates");
            LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "connection failed");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onlocationchanged" + location.toString());
        newLocation = location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                        StartConnection();
                        Log.d(TAG, "permissions granted after asking again");
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG, "permissions denied after asking");
                    Toast.makeText(activity, "No permissions to access location", Toast.LENGTH_SHORT);

                }
                return;
            }

        }
    }
}
