package cloudapplications.citycheck.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import android.os.Handler;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.DoelLocatie;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.Models.Vraag;
import cloudapplications.citycheck.OkHttpCall;
import cloudapplications.citycheck.R;
import cloudapplications.citycheck.TeamLocation;


public class GameActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private GoogleMap kaart;
    private TeamLocation myTeam;
    private NetworkManager service;

    // Variabelen om teams op te halen uit database
    private List<Team> teams = new ArrayList<>();
    private String teamNaam;
    private int gamecode;

    // Gamescore
    private TextView scoreview;
    private int score;
    private TextView teamNameTXT;

    // Vragen beantwoorden
    String[] antwoorden;
    String vraag;
    int correctAntwoordIndex;
    int gekozenAntwoordIndex;

    private TextView timerTextView;

    // Doellocaties
    // private List<LatLng> currentDoelLocaties;
    private List<DoelLocatie> targetLocations = new ArrayList<>();
    // Opzetten geofencing voor doellocaties te kunnen activeren.
    protected ArrayList<Geofence> mGeofenceList;
    protected GoogleApiClient mGoogleApiClient;

    // Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        service = NetworkManager.getInstance();

        gamecode = Integer.parseInt(getIntent().getExtras().getString("gameCode"));

        // Score
        scoreview = findViewById(R.id.txt_Points);
        score = 0;
        setScore(30);

        // Teamnaam txt view
        teamNameTXT = findViewById(R.id.txt_TeamName);

        // LocationsArray (testwaardes voor doellocaties)
        // currentDoelLocaties = new ArrayList<>();

        timerTextView = findViewById(R.id.text_view_timer);
        teamNaam = getIntent().getExtras().getString("teamNaam");
        gameTimer();

        // Teamnaam tonen op het game scherm
        teamNameTXT.setText(teamNaam);

        // Een vraag stellen als ik op de naam klik (Dit is tijdelijk om een vraag toch te kunnen tonen)
        teamNameTXT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askQuestion();
            }
        });

        // Ophalen doellocaties
        getTargetLocations();
        //TO-DELETE
        //Even hardcoded 3 doellocaties adden
        //mogen weg
        /*currentDoelLocaties.add(new LatLng(51.2289238, 4.4026316));
        currentDoelLocaties.add(new LatLng(51.2183305, 4.4204524));
        currentDoelLocaties.add(new LatLng(51.2202678, 4.399327));*/
        //na het ready zijn van de map onderaan plaatsen we nieuwe markers

        // Geofencing
        mGeofenceList = new ArrayList<Geofence>();
        populateGeoFenceList();
        buildGoogleApiClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myTeam != null)
            myTeam.startConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop werkt pas onDestroy en niet onPause, opzich wel goed want als ze de app dan even naar de achtergrond brengen dan blijven de lijnen wel tekenen
        if (myTeam != null)
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

        // Move the camera to Antwerp
        LatLng Antwerpen = new LatLng(51.2194, 4.4025);
        kaart.moveCamera(CameraUpdateFactory.newLatLngZoom(Antwerpen, 15));

        // Alles ivm locatie van het eigen team
        myTeam = new TeamLocation(this, kaart, gamecode, teamNaam);
        myTeam.startConnection();

        // Get other team's locations
        Log.d("Mapmarker", "Game code to call: " + gamecode);
        getTeamsOnMap(gamecode);

        // Eerste doellocatie markers tonen
        // Inconsistentie ivm latlng en locatie gebruik...
        showDoelLocaties(targetLocations);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                Toast.makeText(this, "You can't play without location permissions", Toast.LENGTH_LONG).show();
            }
        }
    }
    // Private helper methoden

    // TODO: Een betere call gebruiken (niet alle doellocaties GETen maar enkel de 3 nodige) + Omzetten naar Retrofit
    // Testen voor implementatie in geofencing
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

    // Doorgeven van TargetLocations waardes voor geofencing
    cloudapplications.citycheck.Constants constants = new cloudapplications.citycheck.Constants();

    private void getTeamsOnMap(int gameId) {
        service.getAllTeamsFromGame(gameId, new NetworkResponseListener<List<Team>>() {
            @Override
            public void onResponseReceived(final List<Team> teams) {
                // Show teams on map
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Team team : teams) {
                            Log.d("Teams", "Team name: " + team.getTeamNaam());
                            if (!team.getTeamNaam().equals(teamNaam)) {
                                Random rand = new Random();
                                float lat = (float) (rand.nextFloat() * (51.30 - 50.00) + 50.00);
                                float lon = (float) (rand.nextFloat() * (5.30 - 2.30) + 2.30);
                                // mMap.clear();
                                kaart.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        // .position(new LatLng(teams.get(i).getHuidigeLocatie().getLatitude(), teams.get(i).getHuidigeLocatie().getLongitude()))
                                        .title(team.getTeamNaam())
                                        .icon(getMarkerIcon(team.getKleur())));
                                Log.d("Retrofit", "Team marker added: #" + Integer.toHexString(team.getKleur()));
                            }
                        }
                    }
                });
            }

            @Override
            public void onError() {
                Toast.makeText(GameActivity.this, "Error while trying to get the teams of the current game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }

    private void showDoelLocaties(List<DoelLocatie> newDoelLocaties) {
        // Place a marker on the locations
        for (int i = 0; i < newDoelLocaties.size(); i++) {
            DoelLocatie doellocatie = newDoelLocaties.get(i);
            LatLng Locatie = new LatLng(doellocatie.getLat(), doellocatie.getLong());
            kaart.addMarker(new MarkerOptions().position(Locatie));
        }
    }

    private void everythingThatNeedsToHappenEvery3s(Long time) {
        // Locatie doorsturen om de 3s
        int TimeCounter = (int) (time / 1000);
        if (TimeCounter % 3 == 0) {
            if (myTeam.newLocation != null) {
                myTeam.handleNewLocation(new Locatie(myTeam.newLocation.getLatitude(), myTeam.newLocation.getLongitude()));
            }
        }
    }

    private void setMultiChoice(final String[] antwoorden, int CorrectIndex, String vraag) {
        // Alertdialog aanmaken
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        // String array for alert dialog multi choice items
//        String[] antwoorden = new String[]{
//                "Antwoord1",
//                "Antwoord2",
//                "Antwoord3"
//        };

        // Boolean array for initial selected items
        final boolean[] checkedAntw = new boolean[]{
                false, // Antwoord1
                false, // Antwoord2
                false, // Antwoord3
        };

        // Convert the color array to list
        // final List<String> AntwList = Arrays.asList(antwoorden);

        /*
        builder.setMultiChoiceItems(antwoorden, checkedAntw, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                // Update the current focused item's checked status
                checkedAntw[which] = isChecked;

                // Get the current focused item
                String currentItem = AntwList.get(which);

                // Notify the current action
                Toast.makeText(getApplicationContext(),
                        currentItem + " " + isChecked, Toast.LENGTH_SHORT).show();
            }
        });
        */

        builder.setSingleChoiceItems(antwoorden, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Notify the current action
                Toast.makeText(GameActivity.this, "Antwoord: " + antwoorden[i], Toast.LENGTH_LONG).show();

                gekozenAntwoordIndex = i;
            }
        });


        // Specify the dialog is not cancelable
        builder.setCancelable(true);

        // Set a title for alert dialog
        builder.setTitle(vraag);

        // Set the positive/yes button click listener
        builder.setPositiveButton("Kies!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do something when click positive button
                // Toast.makeText(GameActivity.this, "data: "+gekozenAntwoordIndex, Toast.LENGTH_LONG).show();

                // Antwoord controleren
                checkAnswer(gekozenAntwoordIndex, correctAntwoordIndex);
            }
        });

        // Set the neutral/cancel button click listener
        //builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
        //@Override
        //public void onClick(DialogInterface dialog, int which) {
        // Do something when click the neutral button
        //}
        //});

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }


    private void checkAnswer(int gekozenInd, int correctInd) {
        // Klopt de gekozen index met het correcte antwoord index
        if (gekozenInd == correctInd) {
            Toast.makeText(GameActivity.this, "Correct!", Toast.LENGTH_LONG).show();

            // X aantal punten toevoegen bij de gebruiker
            score += 10;

            // Nieuwe score tonen en doorpushen naar de db
            setScore(score);
        } else {
            Toast.makeText(GameActivity.this, "Helaas! Volgende keer beter", Toast.LENGTH_LONG).show();
        }
    }

    // TODO: call wordt niet gebruikt
    private void getRandomQuestion(int id) {
        // Id is de doellocatie id
        service.getDoelLocatieVraag(id, new NetworkResponseListener<Vraag>() {
            @Override
            public void onResponseReceived(Vraag vraag) {
                // TODO: response verwerken
            }

            @Override
            public void onError() {
                Toast.makeText(GameActivity.this, "Error while trying to get the questions", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // TODO: deze call werkt niet (ligt aan de backend)
    private void setScore(int newScore) {
        score = newScore;
        scoreview.setText(String.valueOf(score));

        // TODO: Nieuwe score doorpushen naar de API
        service.setTeamScore(gamecode, teamNaam, score, new NetworkResponseListener<Boolean>() {
            @Override
            public void onResponseReceived(Boolean aBoolean) {
                // Score ok
            }

            @Override
            public void onError() {
                Toast.makeText(GameActivity.this, "Error while trying to set the new score", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Opzetten van geofences, nu gebruik van voorbeeldcode voor test
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

    private void askQuestion() {
        // Instellen van een vraag en deze stellen + controleren
        // -------------------------------------------------------------------------------------
        // Antwoorden instellen
        antwoorden = new String[]{
                "10",
                "20",
                "30"
        };
        // Vraag instellen
        vraag = "Hoeveel bla bla?";

        // Antwoord instellen 0,1 of 2
        correctAntwoordIndex = 2;
        // Vraag tonen
        setMultiChoice(antwoorden, correctAntwoordIndex, vraag);
        // -------------------------------------------------------------------------------------
        // Instellen van een vraag en deze stellen + controleren
    }

    void gameTimer() {
        String chosenGameTime = getIntent().getExtras().getString("gameTime");
        long millisStarted = Long.parseLong(getIntent().getExtras().getString("millisStarted"));
        int gameTimeInMillis = Integer.parseInt(chosenGameTime) * 3600000;
        // Game die 10 seconden duurt om de EndGameActivity te testen
        assert chosenGameTime != null;
        if (chosenGameTime.equals("4")) {
            gameTimeInMillis = 10000;
        }
        long timerMillis = gameTimeInMillis - (System.currentTimeMillis() - millisStarted);
        if (timerMillis > 0) {
            new CountDownTimer(timerMillis, 1000) {
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                    timerTextView.setText("Time remaining: " + hours + ":" + minutes + ":" + seconds);
                    everythingThatNeedsToHappenEvery3s(millisUntilFinished);
                }

                public void onFinish() {
                    endGame();
                }
            }.start();
        } else {
            endGame();
        }
    }

    void endGame() {
        Intent i = new Intent(GameActivity.this, EndGameActivity.class);
        i.putExtra("gameCode", Integer.toString(gamecode));
        startActivity(i);
    }
}
