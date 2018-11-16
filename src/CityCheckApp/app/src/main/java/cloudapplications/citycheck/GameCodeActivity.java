package cloudapplications.citycheck;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class GameCodeActivity extends AppCompatActivity {

    String currentGameCode;
    String currentGameTime;
    TextView teamsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_code);

        Button startGameButton = findViewById(R.id.button_start_game);
        TextView codeTextView = findViewById(R.id.text_view_code);
        TextView timeTextView = findViewById(R.id.text_view_time);
        teamsTextView = findViewById(R.id.text_view_teams);

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
                Intent i = new Intent(view.getContext(), GameActivity.class);
                i.putExtra("gameTime", currentGameTime);
                i.putExtra("gameCode", currentGameCode);
                i.putExtra("teamNaam", getIntent().getExtras().getString("teamNaam"));
                startActivity(i);
            }
        });

        getTeams();
//        handler.postDelayed(runnable, 1000);
    }

//    private Handler handler = new Handler();
//    private Runnable runnable = new Runnable() {
//        @Override
//        public void run() {
//            getTeams();
//            handler.postDelayed(this, 1000);
//        }
//    };

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
                obj = new JSONObject(call.responseStr);
                Log.d("GameCodeActivity", "JSON object response: " + obj.toString());
                teamsArray = obj.getJSONArray("teams");
                for (int i = 0; i < teamsArray.length(); i++) {
                    teams = teamsArray.getJSONObject(i);
                    teamsTextView.append("\n" + teams.getString("teamNaam"));
                }
            } catch (Throwable t) {
                Log.e("GameCodeActivity", "Could not parse malformed JSON: \"" + call.responseStr + "\"");
            }
        } else {
            Toast.makeText(this, "Error while trying to get the teams", Toast.LENGTH_SHORT).show();
        }
    }
}
