package cloudapplications.citycheck.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.OkHttpCall;
import cloudapplications.citycheck.R;
import cloudapplications.citycheck.TeamsAdapter;

public class GameCodeActivity extends AppCompatActivity {

    String currentGameCode;
    String currentGameTime;
    Boolean gotTeams;
    NetworkManager service;
    ArrayList<Team> prevTeams= new ArrayList<>();

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            getTeams();
            handler.postDelayed(this, 3000);
        }
    };
    ArrayList<Team> teamsList = new ArrayList<>();
    ListView teamsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_code);

        service=NetworkManager.getInstance();

        Button startGameButton = findViewById(R.id.button_start_game);
        TextView codeTextView = findViewById(R.id.text_view_code);
        TextView timeTextView = findViewById(R.id.text_view_time);
        teamsListView = findViewById(R.id.teams_list_view);

        Log.d("Teams", "gamecode from intent: " + getIntent().getExtras().get("gameCode"));
        currentGameCode = getIntent().getExtras().getString("gameCode");
        currentGameTime = getIntent().getExtras().getString("gameTime");

        codeTextView.setText(currentGameCode);
        timeTextView.setText(currentGameTime + " hours");

        // If the game creator came to this view then he has the right to start the game
        if (!getIntent().getExtras().getBoolean("gameCreator"))
            startGameButton.setVisibility(View.GONE);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                creatorStartGame();
            }
        });

        gotTeams = false;
        getTeams();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void getTeams() {
        NetworkManager.getInstance().getCurrentGame(Integer.parseInt(currentGameCode), new NetworkResponseListener<Game>() {
            @Override
            public void onResponseReceived(Game game) {
                for(Team team: game.getTeams()){
                    Log.d("tag", "getTeams response" + team.getTeamNaam());
                }
                if(game.getHasStarted()){
                    startGame();
                }else{

                    if(game.getTeams().size() != prevTeams.size()){
                        prevTeams=game.getTeams();
                        if(gotTeams){
                            Team team= game.getTeams().get(game.getTeams().size() -1);
                            teamsList.add(team);
                        }else{
                            //Log.d("tag", "size array: " + game.getTeams().size());
                            for(int i=0; i<game.getTeams().size(); i++){
                                //Log.d("tag", "size array: " + game.getTeams().get(i));
                                Team team = game.getTeams().get(i);
                                team.setPunten(-1);
                                teamsList.add(team);
                            }
                        }
                        teamsListView.setAdapter(new TeamsAdapter(GameCodeActivity.this.getBaseContext(), teamsList));
                    }else{
                        gotTeams = true;
                    }

                }
            }

            @Override
            public void onError() {
                Toast.makeText(GameCodeActivity.this.getBaseContext(), "Error while trying to get the teams", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void startGame() {
        Intent i = new Intent(GameCodeActivity.this, GameActivity.class);
        i.putExtra("gameTime", currentGameTime);
        i.putExtra("gameCode", currentGameCode);
        i.putExtra("teamNaam", getIntent().getExtras().getString("teamNaam"));
        startActivity(i);
    }

    private void creatorStartGame() {
        service.startGame(Integer.parseInt(currentGameCode), System.currentTimeMillis(), new NetworkResponseListener<Double>() {
            @Override
            public void onResponseReceived(Double aDouble) {
                startGame();
            }

            @Override
            public void onError() {
                Toast.makeText(GameCodeActivity.this.getBaseContext(), "Error while trying to start the game", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
