package cloudapplications.citycheck;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import android.os.Handler;


public class GameActivity extends FragmentActivity implements OnMapReadyCallback {

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
    private List<LatLng> currentDoelLocaties;

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


        //locationsarray
        currentDoelLocaties = new ArrayList<>();

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
        currentDoelLocaties.add(new LatLng(51.2289238, 4.4026316));
        currentDoelLocaties.add(new LatLng(51.2183305, 4.4204524));
        currentDoelLocaties.add(new LatLng(51.2202678, 4.399327));
        //na het ready zijn van de map onderaan plaatsen we nieuwe markers


    }

    @Override
    protected void onResume(){
        super.onResume();
        if(myTeam != null){
            myTeam.startConnection();
        }
    }

    @Override
    protected void onPause(){
        super.onPause();
        myTeam.stopConnection();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        kaart = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

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
        showDoelLocaties(currentDoelLocaties);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults) {
        if (requestCode == 1) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                return;
            } else {
                //toast is niet zichtbaar
                Toast.makeText(this, "Zonder toegang tot locatie kan je niet spelen",Toast.LENGTH_LONG);
            }
        }
    }


    //private helper methoden
    private void getTargetLocations(){
        OkHttpCall call = new OkHttpCall();
        call.get(getString(R.string.database_ip), "allDoelLocs");
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            JSONObject obj;
            JSONArray TargetLocationsArray;
            try {
                obj = new JSONObject(call.responseStr);
                //Waarom telt deze niet verder naar de volgende functie achter bovenstaande? waardes komen goed binnen.
                //om een of andere redenen is onderstaande lijn "non executable code"?
                Log.d("Locations", "JSON object response: " + obj.toString());
                TargetLocationsArray = obj.getJSONArray("doellocaties");
                Log.d("Locations", "Array of targetlocations: " + TargetLocationsArray);
                for(int i = 0; i<TargetLocationsArray.length();i++){
                    JSONObject target= TargetLocationsArray.getJSONObject(i);
                    //maken van targetlocation class?
                    Double lat = target.getDouble("Lat");
                    Toast.makeText(this, ""+lat, Toast.LENGTH_SHORT).show();

                }
            }catch (Throwable t) {
                Log.e("doelen", "error: " + t);
            }
        }
    }

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

    private void  showDoelLocaties(List<LatLng> newDoelLocaties){

        // place a marker on the locations
        for (int i=0;i<newDoelLocaties.size();i++) {
            LatLng locCoordinaat = newDoelLocaties.get(i);
            kaart.addMarker(new MarkerOptions().position(locCoordinaat));
        }
    }

    private void setScore(int newScore) {
        score = newScore;
        scoreview.setText("" + score);
    }

    private void everythingThatNeedsToHappenEvery3s(Long time){
        //locatie doorsturen om de 3s
        int TimeCounter = (int) (time / 1000);
        if(TimeCounter % 3 == 0){
            myTeam.handleNewLocation();
        }


    }
}
