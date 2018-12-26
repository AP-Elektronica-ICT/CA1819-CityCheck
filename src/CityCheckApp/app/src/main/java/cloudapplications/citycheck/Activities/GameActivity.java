package cloudapplications.citycheck.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.IntersectCalculator;
import cloudapplications.citycheck.Models.DoelLocation;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.TeamTrace;
import cloudapplications.citycheck.OtherTeams;
import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.Models.Vraag;
import cloudapplications.citycheck.R;
import cloudapplications.citycheck.MyTeam;


public class GameActivity extends FragmentActivity implements OnMapReadyCallback /*,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status>*/ {

    private GoogleMap kaart;
    private MyTeam myTeam;
    private OtherTeams otherTeams;
    private NetworkManager service;
    private IntersectCalculator calc;

    // Variabelen om teams op te halen uit database
    private String teamNaam;
    private int gamecode;

    // Gamescore
    private TextView scoreTextView;
    private int score;
    private TextView teamNameTextView;

    // Vragen beantwoorden
    String[] antwoorden;
    String vraag;
    int correctAntwoordIndex;
    int gekozenAntwoordIndex;

    private TextView timerTextView;
    private ProgressBar timerProgressBar;
    private int progress;


    // Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        calc = new IntersectCalculator();
        service = NetworkManager.getInstance();

        IntersectCalculator calc = new IntersectCalculator();
        Team team = new Team("Bello", 456);
        Locatie start = new Locatie(1f, 1f);
        Locatie einde = new Locatie(3.8f, 3.5f);
        ArrayList<TeamTrace> traces = new ArrayList<>();
        traces.add(new TeamTrace(new Locatie(2f, 1f)));
        traces.add(new TeamTrace(new Locatie(0f, 3f)));
        traces.add(new TeamTrace(new Locatie(0.8f, 4.5f)));
        traces.add(new TeamTrace(new Locatie(2f, 5f)));
        traces.add(new TeamTrace(new Locatie(2f, 3f)));
        traces.add(new TeamTrace(new Locatie(2f, 2f)));
        traces.add(new TeamTrace(new Locatie(4f, 2f)));
        team.setTeamTrace(traces);
        for (int i = 0; i < team.getTeamTrace().size(); i++) {
            Log.d("intersect", "" + i);
            if ((i + 1) < team.getTeamTrace().size()) {
                if (calc.doLineSegmentsIntersect(start, einde, team.getTeamTrace().get(i).getLocatie(), team.getTeamTrace().get(i + 1).getLocatie())) {
                    Log.d("intersect", "de lijnen kruisen");
                } else
                    Log.d("intersect", "de lijnen kruisen niet");
            }

        }

        gamecode = Integer.parseInt(getIntent().getExtras().getString("gameCode"));

        // Score
        scoreTextView = findViewById(R.id.text_view_points);
        score = 0;
        setScore(30);

        // Teamnaam txt view
        teamNameTextView = findViewById(R.id.text_view_team_name);

        timerTextView = findViewById(R.id.text_view_timer);
        timerProgressBar = findViewById(R.id.progress_bar_timer);
        teamNaam = getIntent().getExtras().getString("teamNaam");
        gameTimer();

        // Teamnaam tonen op het game scherm
        teamNameTextView.setText(teamNaam);

        // Een vraag stellen als ik op de naam klik (Dit is tijdelijk om een vraag toch te kunnen tonen)
        teamNameTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askQuestion();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (myTeam != null)
            myTeam.startConnection();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        kaart = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        // Alles ivm locatie van het eigen team
        myTeam = new MyTeam(this, kaart, gamecode, teamNaam);
        myTeam.startConnection();

        // Move the camera to Antwerp
        LatLng Antwerpen = new LatLng(51.2194, 4.4025);
        kaart.moveCamera(CameraUpdateFactory.newLatLngZoom(Antwerpen, 15));

        //locaties van andere teams
        otherTeams = new OtherTeams(gamecode, teamNaam, kaart, GameActivity.this);
        otherTeams.getTeamsOnMap();

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
    private void showDoelLocaties(List<DoelLocation> newDoelLocaties) {
        // Place a marker on the locations
        for (int i = 0; i < newDoelLocaties.size(); i++) {
            DoelLocation doellocatie = newDoelLocaties.get(i);
            LatLng Locatie = new LatLng(doellocatie.getLocatie().getLat(), doellocatie.getLocatie().getLong());
            kaart.addMarker(new MarkerOptions().position(Locatie).title("Naam locatie").snippet("500").icon(BitmapDescriptorFactory.fromResource(R.drawable.coin_small)));
        }
    }

    private void everythingThatNeedsToHappenEvery3s(Long time) {

        int TimeCounter = (int) (time / 1000);
        if (TimeCounter % 3 == 0) {
            if (myTeam.newLocation != null) {
                myTeam.handleNewLocation(new Locatie(myTeam.newLocation.getLatitude(), myTeam.newLocation.getLongitude()));
                calculateIntersect();
                LatLng positie = new LatLng(myTeam.newLocation.getLatitude(), myTeam.newLocation.getLongitude());
                kaart.moveCamera(CameraUpdateFactory.newLatLng(positie));
            }

            otherTeams.getTeamsOnMap();
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
            // Nieuwe score tonen en doorpushen naar de db
            setScore(10);
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
        score += newScore;
        scoreTextView.setText(String.valueOf(score));

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

    private void gameTimer() {
        String chosenGameTime = getIntent().getExtras().getString("gameTime");
        long millisStarted = Long.parseLong(getIntent().getExtras().getString("millisStarted"));
        int gameTimeInMillis = Integer.parseInt(chosenGameTime) * 3600000;
        // Game die 10 seconden duurt om de EndGameActivity te testen
        assert chosenGameTime != null;
        if (chosenGameTime.equals("4")) {
            gameTimeInMillis = 10000;
        }
        final long timerMillis = gameTimeInMillis - (System.currentTimeMillis() - millisStarted);
        progress = 0;
        timerProgressBar.setProgress(progress);
        if (timerMillis > 0) {
            new CountDownTimer(timerMillis, 1000) {
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                    timerTextView.setText("Time remaining: " + hours + ":" + minutes + ":" + seconds);
                    everythingThatNeedsToHappenEvery3s(millisUntilFinished);
                    progress++;
                    timerProgressBar.setProgress((int) (progress * 100 / (timerMillis / 1000)));
                }

                public void onFinish() {
                    progress++;
                    timerProgressBar.setProgress(100);
                    endGame();
                }
            }.start();
        } else {
            endGame();
        }
    }

    private void endGame() {
        Intent i = new Intent(GameActivity.this, EndGameActivity.class);
        if(myTeam != null)
            myTeam.stopConnection();
        i.putExtra("gameCode", Integer.toString(gamecode));
        startActivity(i);
    }

    private void calculateIntersect() {
        if(myTeam.Traces.size()>4){

            NetworkManager.getInstance().getAllTeamTraces(gamecode, new NetworkResponseListener<List<Team>>() {
                @Override
                public void onResponseReceived(List<Team> teams) {
                    Locatie start = myTeam.Traces.get(myTeam.Traces.size() - 2);
                    Locatie einde = myTeam.Traces.get(myTeam.Traces.size() - 1);

                    for (Team team : teams) {
                        if (!team.getTeamNaam().equals(teamNaam)){
                            Log.d("intersect", "size: "+team.getTeamTrace().size());
                            for (int i = 0; i < team.getTeamTrace().size(); i++) {
                                if ((i + 1) < team.getTeamTrace().size()) {
                                    if (calc.doLineSegmentsIntersect(start, einde, team.getTeamTrace().get(i).getLocatie(), team.getTeamTrace().get(i + 1).getLocatie())) {
                                        Log.d("intersect", team.getTeamNaam()+ " kruist");
                                        setScore(-5);
                                    } //else
                                        //Log.d("intersect", team.getTeamNaam()+ " kruist niet");
                                }

                            }
                        }
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(GameActivity.this,"Er ging iets mis bij het opvragen van de teamtraces", Toast.LENGTH_SHORT);
                }
            });
        }
    }


}
