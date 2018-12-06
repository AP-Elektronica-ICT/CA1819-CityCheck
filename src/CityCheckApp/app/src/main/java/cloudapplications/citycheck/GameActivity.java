package cloudapplications.citycheck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.os.Handler;


public class GameActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private GoogleMap kaart;
    private TeamLocation myTeam;

    // Variabelen om teams op te halen uit database
    private List<Team> teams = new ArrayList<>();
    private String teamNaam;
    private int gamecode;


    // Gamescore
    private TextView scoreview;
    private int score;
    private TextView teamNameTXT;

    //doellocaties
    //private List<LatLng> currentDoelLocaties;
    private List<DoelLocatie> targetLocations = new ArrayList<>();
    //opzetten geofencing voor doellocaties te kunnen activeren.
    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;


    //callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //score
        scoreview = (TextView) findViewById(R.id.txt_Points);
        score = 0;
        setScore(30);

        //teamnaam txt view
        teamNameTXT = (TextView) findViewById(R.id.txt_TeamName);


        //locationsarray (testwaardes voor doellocaties)
        //currentDoelLocaties = new ArrayList<>();

        final TextView timerTextView = findViewById(R.id.text_view_timer);

        teamNaam = getIntent().getExtras().getString("teamNaam");
        String chosenGameTime = getIntent().getExtras().getString("gameTime");
        int gameTimeInMillis = Integer.parseInt(chosenGameTime) * 3600000;
        // Game die 10 seconden duurt om de EndGameActivity te testen
        assert chosenGameTime != null;
        if (chosenGameTime.equals("4")) {
            gameTimeInMillis = 5000;
        }
        new CountDownTimer(gameTimeInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                timerTextView.setText("Time remaining: " + hours + ":" + minutes + ":" + seconds);
                everythingThatNeedsToHappenEvery3s(millisUntilFinished);

            }

            public void onFinish() {
                Intent i = new Intent(GameActivity.this, EndGameActivity.class);
                i.putExtra("gameCode", Integer.toString(gamecode));
                startActivity(i);
            }
        }.start();

        //teamnaam tonen op het game scherm
        teamNameTXT.setText(teamNaam);

        //ophalen doellocaties
        getTargetLocations();
        //TO-DELETE
        //Even hardcoded 3 doellocaties adden
        //mogen weg
        /*currentDoelLocaties.add(new LatLng(51.2289238, 4.4026316));
        currentDoelLocaties.add(new LatLng(51.2183305, 4.4204524));
        currentDoelLocaties.add(new LatLng(51.2202678, 4.399327));*/
        //na het ready zijn van de map onderaan plaatsen we nieuwe markers

        //geofencing
        mGeofenceList = new ArrayList<Geofence>();
        populateGeoFenceList();
        buildGoogleApiClient();


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myTeam != null) {
            myTeam.startConnection();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //stop werkt pas onDestroy en niet onPause, opzich wel goed want als ze de app dan even naar de achtergrond brengen dan blijven de lijnen wel tekenen
        myTeam.stopConnection();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        kaart = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        // move the camera to Antwerp
        LatLng Antwerpen = new LatLng(51.2194, 4.4025);
        kaart.moveCamera(CameraUpdateFactory.newLatLngZoom(Antwerpen, 15));

        //alles ivm locatie van het eigen team
        myTeam = new TeamLocation(this, kaart);
        myTeam.startConnection();

        //get other team's locations
        gamecode = Integer.parseInt(getIntent().getExtras().getString("gameCode"));
        Log.d("Mapmarker", "gamecode to call: " + gamecode);
        getTeamsOnMap(gamecode);

        //eerste doellocatie markers tonen
        //inconsistentie ivm latlng en locatie gebruik...
        showDoelLocaties(targetLocations);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                Toast.makeText(this, "Zonder toegang tot locatie kan je niet spelen", Toast.LENGTH_LONG).show();
            }
        }
    }


    //private helper methoden

    //testen voor implementatie in geofencing
    private void getTargetLocations() {
        OkHttpCall call = new OkHttpCall();
        call.get(getString(R.string.database_ip), "allDoelLocs");
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            JSONArray obj;

            try {
                obj = new JSONArray(call.responseStr);

                Log.d("Locations", "JSON object response: " + obj.toString());
                Log.d("Locations", "Array of targetlocations: " + obj);
                for (int i = 0; i < obj.length(); i++) {
                    JSONObject target = obj.getJSONObject(i);
                    //maken van targetlocation class?
                    int id = target.getInt("id");
                    String name = target.getString("titel");
                    Double lat = target.getJSONObject("locatie").getDouble("lat");
                    Double lng = target.getJSONObject("locatie").getDouble("long");
                    String vragen = target.getString("vragen");
                    //Location loc = new Location(id,lat,lng);
                    //tonen alle opgehaalde waardes
                    //werkt
                    //Toast.makeText(this, ""+lat+" "+lng+" "+name, Toast.LENGTH_SHORT).show();
                    //geen zekerheid ivm formaat locatie
                    DoelLocatie newtargetlocation = new DoelLocatie(id, name, lat, lng, null, vragen);
                    //heir ben ik iets aan het foutdoen,...
                    targetLocations.add(newtargetlocation);

                    //Toast.makeText(this, ""+newtargetlocation, Toast.LENGTH_LONG).show();

                }
                //Log.d("Doel", "1 teams list: " + currentDoelLocaties);
                //public cloudapplications.citycheck.Constants setTargetLocations (targetLocations);
                Toast.makeText(this, targetLocations.size(), Toast.LENGTH_SHORT).show();
            } catch (Throwable t) {
                Log.e("doelen", "error: " + t);
            }
        }
    }

    //doorgeven van TargetLocations waardes voor geofencing
    cloudapplications.citycheck.Constants constants = new cloudapplications.citycheck.Constants();


    private void getTeamsOnMap(int gameId) {
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
                    //newTeam.setHuidigeLocatie((Location)team.getJSONObject("huidigeLocatie"));
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
                                kaart.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        //.position(new LatLng(teams.get(i).getHuidigeLocatie().getLatitude(), teams.get(i).getHuidigeLocatie().getLongitude()))
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

    private BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private void showDoelLocaties(List<DoelLocatie> newDoelLocaties) {

        // place a marker on the locations
        for (int i = 0; i < newDoelLocaties.size(); i++) {
            DoelLocatie doellocatie = newDoelLocaties.get(i);
            LatLng Locatie = new LatLng(doellocatie.getLat(), doellocatie.getLong());
            kaart.addMarker(new MarkerOptions().position(Locatie));
        }
    }

    private void setScore(int newScore) {
        score = newScore;
        scoreview.setText("" + score);
    }

    private void everythingThatNeedsToHappenEvery3s(Long time) {
        //locatie doorsturen om de 3s
        int TimeCounter = (int) (time / 1000);
        if (TimeCounter % 3 == 0) {
            if (myTeam.newLocation != null) {
                myTeam.handleNewLocation(new LatLng(myTeam.newLocation.getLatitude(), myTeam.newLocation.getLongitude()));
            }

        }

    }

    //opzetten van geofences, nu gebruik van voorbeeldcode voor test
    public void populateGeoFenceList() {
        for (Map.Entry<String, LatLng> entry : cloudapplications.citycheck.Constants.LANDMARKS.entrySet()) {
            mGeofenceList.add(new Geofence.Builder()
                    .setRequestId(entry.getKey())
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            cloudapplications.citycheck.Constants.GEOFENCE_RADIUS_IN_METERS
                    )
                    .setExpirationDuration(cloudapplications.citycheck.Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                    .build());

        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mGoogleApiClient.isConnected() || !mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnecting() || mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public void onConnected(Bundle connectonHint) {

    }

    @Override
    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(@NonNull Status status) {

    }
}
