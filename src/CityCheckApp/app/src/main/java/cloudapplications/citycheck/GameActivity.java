package cloudapplications.citycheck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GameActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    //variabelen om teams op te halen uit database
    OkHttpCall call = new OkHttpCall();
    private List<Team> teams = new ArrayList<>();
    private String teamNaam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final TextView timerTextView = findViewById(R.id.text_view_timer);

        teamNaam = getIntent().getExtras().getString("teamNaam");
        String chosenGameTime = getIntent().getExtras().getString("gameTime");
        int gameTimeInMillis = Integer.parseInt(chosenGameTime) * 3600000;
        new CountDownTimer(gameTimeInMillis, 1000) {

            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                timerTextView.setText("Time remaining: " + hours + ":" + minutes + ":" + seconds);
            }

            public void onFinish() {
                Intent i = new Intent(GameActivity.this, EndGameActivity.class);
                startActivity(i);
            }
        }.start();

        //get team locations
        int gamecode = Integer.parseInt(getIntent().getExtras().getString("gameCode"));
        Log.d("Mapmarker", "gamecode to call: " + gamecode);
        getTeamsOnMap(gamecode);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    //map openzetten
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        buildGoogleApiClient();
        mMap.setMyLocationEnabled(true);


    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        LatLng Currentlocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(Currentlocation).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Currentlocation, ));
        Toast.makeText(getApplicationContext(), "" + Currentlocation,
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void getTeamsOnMap(int gameId) {

        Call response = call.get(String.format("http://84.197.102.107/api/citycheck/currentgame/%d", gameId), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Teams", "getTeams: failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Als de request gelukt is
                    String responseStr = response.body().string();
                    JSONObject obj;
                    JSONArray teamsArray;
                    try {
                        // Converteer de response string naar een JSONObject, JSONArray eruit halen en de inhoud omzetten naar JSONObjecten en dan bewaren als Team object in list
                        obj = new JSONObject(responseStr);
                        Log.d("Teams", "JSON object response: " + obj.toString());
                        teamsArray = obj.getJSONArray("teams");
                        Log.d("Teams", "array of teams: " + teamsArray);
                        for (int i = 0; i < teamsArray.length(); i++) {
                            JSONObject team = teamsArray.getJSONObject(i);
                            Log.d("Teams", "teamobject: " + team);
                            Team newTeam = new Team();
                            newTeam.id = team.getInt("id");
                            newTeam.kleur = team.getInt("kleur");
                            newTeam.teamNaam = team.getString("teamNaam");
                            newTeam.huidigdeLat = team.getLong("huidigeLat");
                            newTeam.huidigeLong = team.getLong("huidigeLong");
                            teams.add(newTeam);

                        }
                        Log.d("Teams", "1 teams list: " + teams);
                        //show teams on map
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(new Runnable(){
                            @Override
                            public void run() {
                                for(int i=0; i<teams.size(); i++){
                                    Log.d("Teams", "teamnaam: "+teamNaam +", "+ teams.get(i).teamNaam);
                                    if(!teams.get(i).teamNaam.equals(teamNaam)){
                                        mMap.addMarker(new MarkerOptions()
                                                .position(new LatLng(50.85, 4.34))
                                                .title(teams.get(i).teamNaam)
                                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                                        Log.d("Teams", "marker added: "+i);
                                    }

                                }

                            }

                        });

                    } catch (Throwable t) {
                        Log.e("Teams", "error: " + t);
                    }

                } else {
                    // Request not successful
                    Log.d("Teams", "getTeams: bad request");
                }
            }

        });
    }
}