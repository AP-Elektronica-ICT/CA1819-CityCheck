package cloudapplications.citycheck;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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

public class TeamLocation extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    private GoogleApiClient myGoogleApiClient;
    private LocationRequest myLocationRequest;
    public Location newLocation;
    private GoogleMap map;
    private Marker Me;
    private static final String TAG = TeamLocation.class.getSimpleName();
    private Activity activity;
    public List<LatLng> Traces;
    Random r;
    LatLng location;

    //public methoden
    public TeamLocation(Activity activityIn, GoogleMap kaart) {
        activity = activityIn;
        map= kaart;
        Traces = new ArrayList<LatLng>();
        r= new Random();
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
    public void startConnection() {
        if(!myGoogleApiClient.isConnected()){
            myGoogleApiClient.connect();
        }
    }
    public void stopConnection() {
        if (myGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(myGoogleApiClient, this);
            myGoogleApiClient.disconnect();
            Log.d(TAG, "connection & updates stopped");
        }
    }

    //private helpermethoden
    private void placeMarker(LatLng location){
        if(Me == null){
            Me = map.addMarker(new MarkerOptions()
                    .position(location)
                    .title("Me")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carrot)));
        }
        else{
            Me.setPosition(new LatLng(location.latitude, location.longitude));
        }
    }
    private void drawPath(){
        //elke keer het traject tussen de laatste locatie en de huidige locatie als polyline tekenen
        if(Traces.size()>4){ //4 intervals wachten voor het tekenen van een lijn om zo collision aan de start te vermijden
            Polyline polyline1 = map.addPolyline(new PolylineOptions()
                    .add(
                            new LatLng(Traces.get(Traces.size()-2).latitude, Traces.get(Traces.size()-2).longitude),
                            new LatLng(Traces.get(Traces.size()-1).latitude, Traces.get(Traces.size()-1).longitude)));
        }

    }
    private void sendLocationToDatabase(LatLng location, int gameId,String teamNaam){
        double Lat = location.latitude;
        double Long = location.longitude;

        OkHttpCall call = new OkHttpCall();
        call.post(getString(R.string.database_ip),"teams/"+gameId+"/"+teamNaam+"/huidigeLocatie","{'Lat':'" + Lat + "', 'Long':'" + Long + "'}");

    }

    private void getStartLocation(){
        //permissies worden gecheckt, warning negeren!
        Location startLocation = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
        if (startLocation == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);
            Log.d(TAG, "updates started");
        }
        else {
            handleNewLocation(new LatLng(startLocation.getLatitude(), startLocation.getLongitude()));
        }
    }

    public void handleNewLocation(LatLng location) {

        //testdata
        //location= new LatLng((r.nextDouble()*(51.2500 - 50.1800) + 50.1800),(r.nextDouble()* (4.8025 - 4.0000) + 4.0000));

        Log.d(TAG, location.toString());
        Traces.add(new LatLng(location.latitude, location.longitude));
        Log.d(TAG, Integer.toString(Traces.size()));
        placeMarker(location);
        drawPath();


        //sendLocationToDatabase(location);
    }

    //callbacks
    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");

        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        }else {
            //LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);
            //Log.d(TAG, "updates started");
            getStartLocation();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onlocationchanged" + location.toString());
        newLocation = location;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getStartLocation();
            } else {
                Toast.makeText(activity.getBaseContext(), "Zonder toegang tot locatie kan je niet spelen",Toast.LENGTH_LONG).show();
            }
        }
    }
}
