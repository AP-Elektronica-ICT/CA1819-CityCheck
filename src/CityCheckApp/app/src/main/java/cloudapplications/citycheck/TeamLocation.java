package cloudapplications.citycheck;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

public class TeamLocation extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    private GoogleApiClient myGoogleApiClient;
    private LocationRequest myLocationRequest;
    private Location lastLocation;
    private GoogleMap map;
    private Marker Me;
    private static final String TAG = TeamLocation.class.getSimpleName();
    private Activity activity;

    //public methoden
    public TeamLocation(Activity activityIn, GoogleMap kaart) {
        activity = activityIn;
        map= kaart;
        myGoogleApiClient = new GoogleApiClient.Builder(activity.getBaseContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        myLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
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
        }
    }

    //private helpermethoden
    private void placeMarker(Location location){
        if(Me == null){
            Me = map.addMarker(new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .title("Marker")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.carrot)));
        }
        else{
            Me.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
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
        Location location = LocationServices.FusedLocationApi.getLastLocation(myGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(myGoogleApiClient, myLocationRequest, this);
        }
        else {
            handleNewLocation(location);
        }
    }
    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        placeMarker(location);
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
        if(location != lastLocation){
            handleNewLocation(location);
            lastLocation=location;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //marker wordt niet meteen getoond, waarom?
                getStartLocation();
            }
        }
    }
}
