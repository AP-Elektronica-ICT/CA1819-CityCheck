package cloudapplications.citycheck.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.R;
import cloudapplications.citycheck.TeamsAdapter;

public class EndGameActivity extends AppCompatActivity {

    ListView endListView;
    ArrayList<Team> teamsList = new ArrayList<>();
    private String gameCode;
    NetworkManager service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        service = NetworkManager.getInstance();

        endListView = findViewById(R.id.end_list_view);
        gameCode = getIntent().getExtras().getString("gameCode");
        getTeams();
    }

    private void getTeams() {
        service.getCurrentGame(Integer.parseInt(gameCode), new NetworkResponseListener<Game>() {
            @Override
            public void onResponseReceived(Game game) {
                teamsList.addAll(game.getTeams());
                Collections.sort(teamsList, new Comparator<Team>() {
                    @Override
                    public int compare(Team a, Team b) {
                        return Integer.compare(b.getPunten(), a.getPunten());
                    }
                });
                endListView.setAdapter(new TeamsAdapter(getApplicationContext(), teamsList));
                deleteGame();
            }

            @Override
            public void onError() {
                Toast.makeText(EndGameActivity.this, "Error while trying to get the teams", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteGame() {
        service.deleteGame(Integer.parseInt(gameCode), new NetworkResponseListener<String>() {
            @Override
            public void onResponseReceived(String s) {
                Log.d("Retrofit", "Game with code " + gameCode + " deleted");
            }

            @Override
            public void onError() {
                Toast.makeText(EndGameActivity.this, "Error while trying to delete the game", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
