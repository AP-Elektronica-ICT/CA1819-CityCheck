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

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Game;

public class CreateGameActivity extends AppCompatActivity {

    private int gameTime;
    private NetworkManager service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        service = NetworkManager.getInstance();

        Button createGameButton = findViewById(R.id.button_create_game);
        Spinner gameTimeSpinner = findViewById(R.id.spinner_game_time);

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
                createNewGame();
            }
        });
    }

    private void createNewGame() {
    service.createNewGame(new Game(gameTime), new NetworkResponseListener<Game>() {
        @Override
        public void onResponseReceived(Game game) {
            Log.d("retrofit", "resonse new game in listener: " + game.getGameCode());
            String gameCode = Integer.toString(game.getGameCode());
            
            Intent i = new Intent(CreateGameActivity.this, JoinGameActivity.class);
            i.putExtra("gameCode", gameCode);
            startActivity(i);
        }

        @Override
        public void onError() {
            Toast.makeText(CreateGameActivity.this.getBaseContext(), "Error while trying to create a new game", Toast.LENGTH_SHORT).show();
        }
    });

    }
    
}