package cloudapplications.citycheck.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import java.util.Objects;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Goals;
import cloudapplications.citycheck.IntersectCalculator;
import cloudapplications.citycheck.Models.Antwoord;
import cloudapplications.citycheck.Models.DoelLocation;
import cloudapplications.citycheck.Models.GameDoel;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.StringReturn;
import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.Models.Vraag;
import cloudapplications.citycheck.MyTeam;
import cloudapplications.citycheck.OtherTeams;
import cloudapplications.citycheck.R;


public class GameActivity extends FragmentActivity implements OnMapReadyCallback {

    // Kaart vars
    private GoogleMap kaart;
    private MyTeam myTeam;
    private OtherTeams otherTeams;
    private Goals goals;
    private NetworkManager service;
    private IntersectCalculator calc;

    // Variabelen om teams op te halen uit database
    private String teamNaam;
    private int gamecode;

    // Gamescore
    private TextView scoreTextView;
    private int score;

    // Vragen beantwoorden
    String[] antwoorden;
    String vraag;
    int correctAntwoordIndex;
    int gekozenAntwoordIndex;
    boolean isClaiming;

    // Timer vars
    private TextView timerTextView;
    private ProgressBar timerProgressBar;
    private int progress;

    //screen time out
    int defTimeOut=0;

    // Afstand
    float[] afstandResult;
    float treshHoldAfstand = 50; //(meter)

    // Geluiden
    MediaPlayer mpTrace;
    MediaPlayer mpClaimed;
    MediaPlayer mpBonusCorrect;
    MediaPlayer mpBonusWrong;
    MediaPlayer mpGameStarted;

    // Callbacks
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Objects.requireNonNull(mapFragment).getMapAsync(this);

        calc = new IntersectCalculator();
        service = NetworkManager.getInstance();

        gamecode = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).getString("gameCode")));

        // AfstandTreshold
        treshHoldAfstand = 50; //(meter)

        // Claiming naar false
        isClaiming = false;

        // Teamnaam txt view
        TextView teamNameTextView = findViewById(R.id.text_view_team_name);

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
                claimLocatie(1, 1);
            }
        });

        // Score
        scoreTextView = findViewById(R.id.text_view_points);
        score = 0;
        setScore(30);

        // Geluiden
        mpTrace = MediaPlayer.create(this, R.raw.trace_crossed);
        mpClaimed = MediaPlayer.create(this, R.raw.claimed);
        mpBonusCorrect = MediaPlayer.create(this, R.raw.bonus_correct);
        mpBonusWrong = MediaPlayer.create(this, R.raw.bonus_wrong);
        mpGameStarted = MediaPlayer.create(this, R.raw.game_started);

        mpGameStarted.start();

        ImageView pointsImageView = findViewById(R.id.image_view_points);
        pointsImageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                endGame();
                return false;
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
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        // Alles ivm locatie van het eigen team
        myTeam = new MyTeam(this, kaart, gamecode, teamNaam);
        myTeam.startConnection();

        // Move the camera to Antwerp
        LatLng Antwerpen = new LatLng(51.2194, 4.4025);
        kaart.moveCamera(CameraUpdateFactory.newLatLngZoom(Antwerpen, 15));

        // Locaties van andere teams
        otherTeams = new OtherTeams(gamecode, teamNaam, kaart, GameActivity.this);
        otherTeams.getTeamsOnMap();

        // Alles ivm doellocaties
        goals = new Goals(gamecode, kaart, GameActivity.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
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

    private void everythingThatNeedsToHappenEvery3s(long verstrekentijd) {
        int tijd = (int) (verstrekentijd / 1000);
        if (tijd % 3 == 0) {
            goals.removeCaimedLocations();
            if (myTeam.newLocation != null) {
                myTeam.handleNewLocation(new Locatie(myTeam.newLocation.getLatitude(), myTeam.newLocation.getLongitude()), tijd);
                calculateIntersect();
                LatLng positie = new LatLng(myTeam.newLocation.getLatitude(), myTeam.newLocation.getLongitude());
                //kaart.moveCamera(CameraUpdateFactory.newLatLng(positie));
            }
            otherTeams.getTeamsOnMap();
        }

        // Controleren op doellocatie triggers om te kunnen claimen

        // Huidige locaties van de doelen ophalen
        if (goals.currentGoals != null && myTeam.Traces.size() > 0 && !isClaiming) {
            Locatie loc1 = goals.currentGoals.get(0).getDoel().getLocatie();
            Locatie loc2 = goals.currentGoals.get(1).getDoel().getLocatie();
            Locatie loc3 = goals.currentGoals.get(2).getDoel().getLocatie();
            Locatie[] locs = {loc1, loc2, loc3};

            // Mijn huidige locatie ophalen
            int tempTraceSize = myTeam.Traces.size();
            double tempLat = myTeam.Traces.get(tempTraceSize - 1).getLat();
            double tempLong = myTeam.Traces.get(tempTraceSize - 1).getLong();

            // Kijken of er een hit is met een locatie
            int tempIndex = 0;
            for (Locatie loc : locs) {
                berekenAfstand(loc, tempLat, tempLong, goals.currentGoals.get(tempIndex));
                tempIndex++;
            }
        }
    }

    private void berekenAfstand(Locatie doelLoc, double tempLat, double tempLong, GameDoel goal) {
        afstandResult = new float[1];
        Location.distanceBetween(doelLoc.getLat(), doelLoc.getLong(), tempLat, tempLong, afstandResult);
        if (afstandResult[0] < treshHoldAfstand && !goal.getClaimed()) {
            // GameDoelID en doellocID ophalen
            int GD = goal.getId();
            int LC = goal.getDoel().getId();

            // Locatie claim triggeren
            claimLocatie(GD, LC);
            goal.setClaimed(true);
            // Claimen instellen zolang we bezig zijn met claimen
            isClaiming = true;
        }
    }

    private void setMultiChoice(final String[] antwoorden, int CorrectIndex, String vraag) {
        // Alertdialog aanmaken
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

        // Single choice dialog met de antwoorden
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

        AlertDialog dialog = builder.create();
        // Display the alert dialog on interface
        dialog.show();
    }

    private void checkAnswer(int gekozenInd, int correctInd) {
        // Klopt de gekozen index met het correcte antwoord index
        if (gekozenInd == correctInd) {
            mpBonusCorrect.start();
            Toast.makeText(GameActivity.this, "Correct!", Toast.LENGTH_LONG).show();

            // X aantal punten toevoegen bij de gebruiker
            // Nieuwe score tonen en doorpushen naar de db
            setScore(20);
            isClaiming = false;
        } else {
            mpBonusWrong.start();
            Toast.makeText(GameActivity.this, "Helaas!", Toast.LENGTH_LONG).show();
            setScore(5);
            isClaiming = false;
        }
    }

    private void setScore(int newScore) {
        score += newScore;
        scoreTextView.setText(String.valueOf(score));

        service.setTeamScore(gamecode, teamNaam, score, new NetworkResponseListener<Integer>() {
            @Override
            public void onResponseReceived(Integer score) {
                // Score ok
            }

            @Override
            public void onError() {
                Toast.makeText(GameActivity.this, "Error while trying to set the new score", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void claimLocatie(final int locId, final int doellocID) {
        mpClaimed.start();
        // locid => gamelocaties ID, doellocID => id van de daadwerkelijke doellocatie

        // Een team een locatie laten claimen als ze op deze plek zijn.
        service.claimDoelLocatie(gamecode, locId, new NetworkResponseListener<StringReturn>() {
            @Override
            public void onResponseReceived(StringReturn rtrn) {
                //response verwerken
                try {
                    String waarde = rtrn.getWaarde();
                    Toast.makeText(GameActivity.this, waarde, Toast.LENGTH_SHORT).show();
                } catch (Throwable err) {
                    Toast.makeText(GameActivity.this, "Error while trying to claim the location", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(GameActivity.this, "Error while trying to claim the location", Toast.LENGTH_SHORT).show();
            }
        });

        // Dialog tonen met de vraag claim of bonus vraag
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Claimen of bonus vraag(risico) oplossen?")
                // Niet cancel-baar
                .setCancelable(false)
                .setPositiveButton("Claim", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mpBonusCorrect.start();
                        // De location alleen claimen zonder bonusvraag
                        setScore(10);
                        isClaiming = false;
                    }
                })
                .setNegativeButton("Bonus vraag", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Random vraag bij deze locatie ophalen uit de backend
                        service.getDoelLocatieVraag(doellocID, new NetworkResponseListener<Vraag>() {
                            @Override
                            public void onResponseReceived(Vraag newVraag) {
                                // Response verwerken

                                // Vraagtitel bewaren
                                vraag = newVraag.getVraagZin();
                                //3 Antwoorden bewaren
                                ArrayList<Antwoord> allAnswers = newVraag.getAntwoorden();
                                antwoorden = new String[3];
                                for (int i = 0; i < 3; i++) {
                                    antwoorden[i] = allAnswers.get(i).getAntwoordzin();
                                    if (allAnswers.get(i).isCorrectBool()) {
                                        correctAntwoordIndex = i;
                                    }
                                }

                                //Vraag stellen
                                askQuestion();
                            }

                            @Override
                            public void onError() {
                                Toast.makeText(GameActivity.this, "Error while trying to get the question", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void askQuestion() {
        // Instellen van een vraag en deze stellen + controleren

        // Vraag tonen
        setMultiChoice(antwoorden, correctAntwoordIndex, vraag);
    }

    private void gameTimer() {
        String chosenGameTime = Objects.requireNonNull(getIntent().getExtras()).getString("gameTime");
        long millisStarted = Long.parseLong(Objects.requireNonNull(getIntent().getExtras().getString("millisStarted")));
        int gameTimeInMillis = Integer.parseInt(Objects.requireNonNull(chosenGameTime)) * 3600000;

        // Het verschil tussen de tijd van nu en de tijd van wanneer de game is gestart
        long differenceFromMillisStarted = System.currentTimeMillis() - millisStarted;

        // Hoe lang dat de timer moet doorlopen
        long timerMillis = gameTimeInMillis - differenceFromMillisStarted;

        //screen time out
        if(Settings.System.canWrite(this)){
            defTimeOut = Settings.System.getInt(getContentResolver(),Settings.System.SCREEN_OFF_TIMEOUT, 0);
            android.provider.Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, (int)timerMillis);
        }

        // De progress begint op de juiste plek, dus niet altijd vanaf 0
        progress = (int) ((gameTimeInMillis - timerMillis) / 1000);

        timerProgressBar.setProgress(progress);
        if (timerMillis > 0) {
            final int finalGameTimeInMillis = gameTimeInMillis;
            new CountDownTimer(timerMillis, 1000) {
                public void onTick(long millisUntilFinished) {
                    int seconds = (int) (millisUntilFinished / 1000) % 60;
                    int minutes = (int) ((millisUntilFinished / (1000 * 60)) % 60);
                    int hours = (int) ((millisUntilFinished / (1000 * 60 * 60)) % 24);
                    timerTextView.setText("Time remaining: " + hours + ":" + minutes + ":" + seconds);
                    everythingThatNeedsToHappenEvery3s(finalGameTimeInMillis - millisUntilFinished);
                    getNewGoalsAfterInterval((finalGameTimeInMillis - millisUntilFinished), 900);
                    progress++;
                    timerProgressBar.setProgress(progress * 100 / (finalGameTimeInMillis / 1000));
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
        if(Settings.System.canWrite(this)) {
            Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, defTimeOut);
        }
        Intent i = new Intent(GameActivity.this, EndGameActivity.class);
        if (myTeam != null)
            myTeam.stopConnection();

        if (Objects.requireNonNull(getIntent().getExtras()).getBoolean("gameCreator"))
            i.putExtra("gameCreator", true);
        else
            i.putExtra("gameCreator", false);

        i.putExtra("gameCode", Integer.toString(gamecode));
        startActivity(i);
    }

    private void calculateIntersect() {
        if (myTeam.Traces.size() > 2) {
            service.getAllTeamTraces(gamecode, new NetworkResponseListener<List<Team>>() {
                @Override
                public void onResponseReceived(List<Team> teams) {
                    if(myTeam.Traces.size() > 2) {
                        Locatie start = myTeam.Traces.get(myTeam.Traces.size() - 2);
                        Locatie einde = myTeam.Traces.get(myTeam.Traces.size() - 1);

                        for (Team team : teams) {
                            if (!team.getTeamNaam().equals(teamNaam)) {
                                Log.d("intersect", "size: " + team.getTeamTrace().size());
                                for (int i = 0; i < team.getTeamTrace().size(); i++) {
                                    if ((i + 1) < team.getTeamTrace().size()) {
                                        if (calc.doLineSegmentsIntersect(start, einde, team.getTeamTrace().get(i).getLocatie(), team.getTeamTrace().get(i + 1).getLocatie())) {
                                            mpTrace.start();
                                            Log.d("intersect", team.getTeamNaam() + " kruist");
                                            setScore(-5);
                                            Toast.makeText(GameActivity.this, "Oh oohw you crossed another team's path, bye bye 5 points", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onError() {
                    Toast.makeText(GameActivity.this, "Er ging iets mis bij het opvragen van de teamtraces", Toast.LENGTH_SHORT);
                }
            });
        }
    }

    private void getNewGoalsAfterInterval(Long verstrekenTijd, int interval) {
        int tijd = (int) (verstrekenTijd / 1000);

        // interval meegeven in seconden
        goals.getNewGoals(tijd, interval);

        //traces op map clearen bij elk interval
        if (tijd % interval == 0) {

            service.deleteTeamtraces(gamecode, new NetworkResponseListener<Boolean>() {
                @Override
                public void onResponseReceived(Boolean cleared) {
                    Log.d("clearTraces", "cleared: " +cleared);
                    myTeam.clearTraces();
                    otherTeams.clearTraces();
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
    }
}
