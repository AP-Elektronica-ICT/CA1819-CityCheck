package cloudapplications.citycheck;

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

public class GameCodeActivity extends AppCompatActivity {

    String currentGameCode;
    String currentGameTime;
    String lastResponseStr = "";
    Boolean gotTeams;
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
//        if (!getIntent().getExtras().getBoolean("gameCreator"))
//            startGameButton.setVisibility(View.GONE);

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGame();
            }
        });

        gotTeams = false;
        getTeams();
        handler.postDelayed(runnable, 3000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void getTeams() {
        OkHttpCall call = new OkHttpCall();
        call.get(getString(R.string.database_ip), "currentgame/" + currentGameCode);
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            JSONObject obj;
            JSONArray teamsArray;
            JSONObject teams;
            try {
                // Converteer de response string naar een JSONObject en de teams er uit halen
                if (!lastResponseStr.equals(call.responseStr)) {
                    lastResponseStr = call.responseStr;
                    obj = new JSONObject(lastResponseStr);
                    Log.d("GameCodeActivity", "JSON object response: " + obj.toString());
                    // Selecteer de "teams" veld
                    teamsArray = obj.getJSONArray("teams");
                    if (gotTeams) {
                        teams = teamsArray.getJSONObject(teamsArray.length() - 1);
                        teamsList.add(new Team(teams.getString("teamNaam"), teams.getInt("kleur"), -1));
                    } else {
                        for (int i = 0; i < teamsArray.length(); i++) {
                            teams = teamsArray.getJSONObject(i);
                            teamsList.add(new Team(teams.getString("teamNaam"), teams.getInt("kleur"), -1));
                        }
                    }
                    teamsListView.setAdapter(new TeamsAdapter(this, teamsList));
                } else
                    gotTeams = true;

                // TODO
//                obj = new JSONObject(call.responseStr);
//                if(obj.getBoolean("hasStarted"))
//                    startGame();
            } catch (Throwable t) {
                Log.e("GameCodeActivity", "Could not parse malformed JSON: \"" + call.responseStr + "\"");
            }
        } else {
            Toast.makeText(this, "Error while trying to get the teams", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame() {
        Intent i = new Intent(GameCodeActivity.this, GameActivity.class);
        i.putExtra("gameTime", currentGameTime);
        i.putExtra("gameCode", currentGameCode);
        i.putExtra("teamNaam", getIntent().getExtras().getString("teamNaam"));
        startActivity(i);
    }
}
