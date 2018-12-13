package cloudapplications.citycheck.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.OkHttpCall;
import cloudapplications.citycheck.R;

public class VorigeGameActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks, LocationListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    // Variabelen om teams op te halen uit database
    private List<Team> teams = new ArrayList<>();
    private String teamNaam;
    private int gamecode;

    //lokaallocaties huidige team opslaan
    private ArrayList<LatLng> LocationList = new ArrayList<>();

    // Gamescore
    private TextView scoreview;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        scoreview = (TextView) findViewById(R.id.txt_Points);
        score = 0;
        setScore(30);

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
                // method hier geplaasts om opgeroepen te worden waar nodig
                recordLocationCurrent(mLastLocation, millisUntilFinished);
            }

            public void onFinish() {
                Intent i = new Intent(VorigeGameActivity.this, EndGameActivity.class);
                i.putExtra("gameCode", Integer.toString(gamecode));
                startActivity(i);
            }
        }.start();

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

        //get team locations
        gamecode = Integer.parseInt(getIntent().getExtras().getString("gameCode"));
        Log.d("Mapmarker", "gamecode to call: " + gamecode);
        getTeamsOnMap(gamecode);

        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));
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
        Log.d("schildpad", "current location: "+ location);
        LatLng Currentlocation = new LatLng(location.getLatitude(), location.getLongitude());
        LocationList.add(Currentlocation);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(Currentlocation).title("MyTeam"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Currentlocation, ));
        //Toast.makeText(getApplicationContext(), "" + Currentlocation,
        //        Toast.LENGTH_SHORT).show();
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

    public void PostLocationCurrent(LatLng location, int gameId,String teamNaam){
        double Lat = location.latitude;
        double Long = location.longitude;

        OkHttpCall call = new OkHttpCall();
//        call.post(getString(R.string.database_ip),"teams/"+gameId+"/"+teamNaam+"/huidigeLocatie","{'Lat':'" + Lat + "', 'Long':'" + Long + "'}");

    }

    public void recordLocationCurrent(Location location, Long time) {
        //mLastLocation = location;

        //locatie doorsturen om de 5s
        int TimeCounter = (int) (time / 1000);
        boolean IsDivisibleTimer = TimeCounter % 5 == 0;

        LatLng PreviousLocation = new LatLng(1, 1);
        //if (location != null) {
        Random rand = new Random();
        LatLng Currentlocation = new LatLng((rand.nextFloat() * (51.30 - 50.00) + 50.00), (rand.nextFloat() * (5.30 - 2.30) + 2.30));
        try {
            if (IsDivisibleTimer == true) {

                //if(Currentlocation != PreviousLocation){
                //PostLocationCurrent(Currentlocation, gamecode,teamNaam);
//                    Toast.makeText(getApplicationContext(), "iets" + LocationList.get(i),
//                            Toast.LENGTH_SHORT).show();
                //}

            }
        } catch (Throwable t) {
            Log.e("LatLngRecording", "error: " + t);
        }
        //}
    }

    public void getTeamsOnMap(int gameId) {
        OkHttpCall call = new OkHttpCall();
        call.get(getString(R.string.database_ip), "currentgame/" + gameId);
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            JSONObject obj;
            JSONArray teamsArray;
            try {
                // Converteer de response string naar een JSONObject, JSONArray eruit halen en de inhoud omzetten naar JSONObjecten en dan bewaren als Team object in list
                obj = new JSONObject(call.responseStr);
                Log.d("Teams", "JSON object response: " + obj.toString());
                teamsArray = obj.getJSONArray("teams");
                Log.d("Teams", "Array of teams: " + teamsArray);
                for (int i = 0; i < teamsArray.length(); i++) {
                    JSONObject team = teamsArray.getJSONObject(i);
                    Log.d("Teams", "teamobject: " + team);
                    Team newTeam = new Team(team.getString("teamNaam"), team.getInt("kleur"), team.getInt("punten"));
                    //newTeam.setHuidigeLat(team.getLong("huidigeLat"));
                    //newTeam.setHuidigeLong(team.getLong("huidigeLong"));
                    teams.add(newTeam);
                }
                Log.d("Teams", "1 teams list: " + teams);
                // Show teams on map
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < teams.size(); i++) {
                            Log.d("Teams", "Team name: " + teamNaam + ", " + teams.get(i).getTeamNaam());
                            if (!teams.get(i).getTeamNaam().equals(teamNaam)) {
                                Random rand = new Random();
                                float lat = (float) (rand.nextFloat() * (51.30 - 50.00) + 50.00);
                                float lon = (float) (rand.nextFloat() * (5.30 - 2.30) + 2.30);
                                //mMap.clear();
                                mMap.addMarker(new MarkerOptions()
                                        //.position(new LatLng(teams.get(i).getHuidigeLat(), teams.get(i).getHuidigeLong()))
                                        .title(teams.get(i).getTeamNaam())
                                        .icon(getMarkerIcon(teams.get(i).getKleur())));
                                Log.d("Teams", "marker added: #" + Integer.toHexString(teams.get(i).getKleur()));
                            }
                        }
                    }
                });
            } catch (Throwable t) {
                Log.e("Teams", "error: " + t);
            }
        } else {
            Toast.makeText(this, "Error while trying to get the teams of the current game", Toast.LENGTH_SHORT).show();
        }
    }

    public BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    public void setScore(int newScore) {
        score = newScore;
        scoreview.setText("" + score);
    }
}
