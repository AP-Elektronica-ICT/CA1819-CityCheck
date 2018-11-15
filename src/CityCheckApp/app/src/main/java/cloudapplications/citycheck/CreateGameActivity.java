package cloudapplications.citycheck;

import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;

import okhttp3.Call;
import okhttp3.Callback;

public class CreateGameActivity extends AppCompatActivity {

    private int gameTime;
    private EditText edit_team_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        Button createGameButton = findViewById(R.id.button_create_game);
        Spinner gameTimeSpinner = findViewById(R.id.spinner_game_time);
        edit_team_name = findViewById(R.id.edit_text_team_name);

        String[] items = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gameTimeSpinner.setAdapter(adapter);

        gameTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                gameTime = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        createGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGameToDatabase();
            }
        });
    }

    private void saveGameToDatabase() {
        OkHttpCall call = new OkHttpCall();
        call.post("newgame", "{'TijdsDuur':" + Integer.toString(gameTime) + "}");
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            JSONObject obj;
            String gameCode = "";
            try {
                // Converteer de response string naar een JSONObject en de code er uit halen
                obj = new JSONObject(call.responseStr);
                Log.d("CreateGameActivity", "JSON object response: " + obj.toString());
                gameCode = obj.getString("gameCode");
            } catch (Throwable t) {
                Log.e("CreateGameActivity", "Could not parse malformed JSON: \"" + call.responseStr + "\"");
            }
            Log.d("CreateGameActivity", "Game code: " + gameCode);
            addTeamToGame(edit_team_name.getText().toString(), Integer.parseInt(gameCode));
        }
    }

    private void addTeamToGame(final String name, final int gamecode) {
        OkHttpCall call = new OkHttpCall();
        call.post("teams/" + Integer.toString(gamecode), "{'teamNaam':'" + name + "'}");
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            Log.d("testest", "addTeamToGame: iets");
            Intent i = new Intent(CreateGameActivity.this, GameCodeActivity.class);
            i.putExtra("gameCode", Integer.toString(gamecode));
            i.putExtra("gameTime", Integer.toString(gameTime));
            i.putExtra("teamNaam", name);
            startActivity(i);
        }
    }
}