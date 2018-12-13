package cloudapplications.citycheck.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.OkHttpCall;
import cloudapplications.citycheck.R;
import cloudapplications.citycheck.TeamsAdapter;

public class EndGameActivity extends AppCompatActivity {

    ListView endListView;
    ArrayList<Team> teamsList = new ArrayList<>();
    private String gameCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_game);

        endListView = findViewById(R.id.end_list_view);
        gameCode = getIntent().getExtras().getString("gameCode");
        getTeams();
    }

    private void getTeams() {
        OkHttpCall call = new OkHttpCall();
        call.get(getString(R.string.database_ip), "currentgame/" + gameCode);
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            JSONObject obj;
            JSONArray teamsArray;
            JSONObject teams;
            try {
                // Converteer de response string naar een JSONObject en de teams er uit halen
                obj = new JSONObject(call.responseStr);
                Log.d("EndGameActivity", "JSON object response: " + obj.toString());
                // Selecteer de "teams" veld
                teamsArray = obj.getJSONArray("teams");

                for (int i = 0; i < teamsArray.length(); i++) {
                    teams = teamsArray.getJSONObject(i);
                    teamsList.add(new Team(teams.getString("teamNaam"), teams.getInt("kleur"), teams.getInt("punten")));
                }

                Collections.sort(teamsList, new Comparator<Team>() {
                    @Override
                    public int compare(Team a, Team b) {
                        return Integer.compare(b.getPunten(), a.getPunten());
                    }
                });
                endListView.setAdapter(new TeamsAdapter(this, teamsList));
//                deleteGame();
            } catch (Throwable t) {
                Log.e("EndGameActivity", "Could not parse malformed JSON: \"" + call.responseStr + "\"");
            }
        } else {
            Toast.makeText(this, "Error while trying to get the teams", Toast.LENGTH_SHORT).show();
        }
    }

//    private void deleteGame() {
//        OkHttpCall call = new OkHttpCall();
//        call.delete(getString(R.string.database_ip), "currentgame/" + gameCode);
//        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
//        if (call.status == OkHttpCall.RequestStatus.Unsuccessful) {
//            Toast.makeText(this, "Error while trying to delete the game from the database", Toast.LENGTH_SHORT).show();
//        }
//    }
}
