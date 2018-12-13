package cloudapplications.citycheck.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.R;

public class CreateGameActivity extends AppCompatActivity {

    private int gameTime;
    private Button timeButton;
    private TextView timeTextView;
    private NetworkManager service;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);
        service = NetworkManager.getInstance();

        Button createGameButton = findViewById(R.id.button_create_game);
        timeButton = findViewById(R.id.button_time_pick);
        timeTextView = findViewById(R.id.text_view_time);
        gameTime = 1;
        timeTextView.setText(Integer.toString(gameTime));

        timeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // We gaan een tijd selecteren
                timePick();
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
                Log.d("Retrofit", "Game code in CreateGameActivity: " + game.getGameCode());
                String gameCode = Integer.toString(game.getGameCode());

                Intent i = new Intent(CreateGameActivity.this, JoinGameActivity.class);
                i.putExtra("gameCode", gameCode);
                startActivity(i);
            }

            @Override
            public void onError() {
                Toast.makeText(CreateGameActivity.this, "Error while trying to create a new game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void timePick() {
        final Dialog d = new Dialog(CreateGameActivity.this);
        d.setTitle("NumberPicker");
        d.setContentView(R.layout.timepick_dialog);
        Button b1 = d.findViewById(R.id.button_time_set);
        Button b2 = d.findViewById(R.id.button_time_cancel);
        final NumberPicker np = d.findViewById(R.id.number_picker);
        np.setMaxValue(4);
        np.setMinValue(1);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int old, int newTijd) {
                // Toast.makeText(getApplicationContext(), "Time: "+Integer.toString(newTijd), Toast.LENGTH_SHORT).show();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // De gekozen tijd ophalen
                gameTime = np.getValue();
                // Toast.makeText(getApplicationContext(), "Gekozen tijdsduur: "+Integer.toString(gameTime), Toast.LENGTH_LONG).show();
                timeTextView.setText(Integer.toString(gameTime));
                // Dialog sluiten
                d.dismiss();
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Annuleren
                d.dismiss();
            }
        });
        d.show();
    }
}