package cloudapplications.citycheck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONObject;

public class CreateGameActivity extends AppCompatActivity {

    private int gameTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        Button createGameButton = findViewById(R.id.button_create_game);
        Spinner gameTimeSpinner = findViewById(R.id.spinner_game_time);

        String[] items = new String[]{"1", "2", "3", "10 seconds"};
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
                createNewGame();
            }
        });
    }

    private void createNewGame() {
        OkHttpCall call = new OkHttpCall();
        call.post(getString(R.string.database_ip), "newgame", "{'TijdsDuur':" + Integer.toString(gameTime) + "}");
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
            Intent i = new Intent(CreateGameActivity.this, JoinGameActivity.class);
            i.putExtra("gameCode", gameCode);
            startActivity(i);
        } else {
            Toast.makeText(this, "Error while trying to create a new game", Toast.LENGTH_SHORT).show();
        }
    }
}