package cloudapplications.citycheck.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import java.util.Objects;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.R;

public class JoinGameActivity extends AppCompatActivity {

    private int currentColor = 0xffffffff;

    private TextView teamNameTextView;
    private EditText teamNameEditText;
    private EditText gameCodeEditText;

    private String name;
    private int color;
    private int gamecode;
    private String gameTime;
    private boolean gameCreator;
    private NetworkManager service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        service = NetworkManager.getInstance();

        Button pickColorButton = findViewById(R.id.button_pick_color);
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.button);
        teamNameTextView = findViewById(R.id.text_view_team_name);
        teamNameEditText = findViewById(R.id.edit_text_team_name);
        gameCodeEditText = findViewById(R.id.edit_text_game_code);
        Button joinGameButton = findViewById(R.id.button_join_game);

        teamNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                teamNameTextView.setText("Your Name");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                teamNameTextView.setText(teamNameEditText.getText());
            }
        });

        gameCreator = !Objects.equals(Objects.requireNonNull(getIntent().getExtras()).getString("gameCode"), "-1");
        if (gameCreator) {
            gamecode = Integer.parseInt(Objects.requireNonNull(getIntent().getExtras().getString("gameCode")));
            gameCodeEditText.setText(Integer.toString(gamecode));
            gameCodeEditText.setFocusable(false);
            gameCodeEditText.setEnabled(false);
        }

        pickColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                // Kleurkiezer weergeven
                ColorPickerDialogBuilder
                        .with(JoinGameActivity.this)
                        .setTitle("Choose color")
                        .initialColor(currentColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                            }
                        })
                        .setPositiveButton("OK", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                currentColor = selectedColor;
                                teamNameTextView.setTextColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();
            }
        });

        joinGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.start();
                name = teamNameEditText.getText().toString();
                if (name.matches("")) {
                    Toast.makeText(JoinGameActivity.this, "You must enter a team name", Toast.LENGTH_SHORT).show();
                } else {
                    if (gameCodeEditText.getText().toString().matches("\\d{4}")) {
                        gamecode = Integer.parseInt(gameCodeEditText.getText().toString());
                        color = currentColor;
                        Log.d("JoinGameActivity", "team: " + name + " color: " + color + " gamecode: " + gamecode);
                        addTeamToGame(name, color, gamecode);
                    } else {
                        Toast.makeText(JoinGameActivity.this, "You must enter a valid game code (4 digits)", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void addTeamToGame(String name, int color, final int gamecode) {
        service.createTeam(gamecode, new Team(name, color), new NetworkResponseListener<Team>() {
            @Override
            public void onResponseReceived(Team team) {
                startGame(gamecode);
            }

            @Override
            public void onError() {
                Toast.makeText(JoinGameActivity.this, "Error while trying to join the game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startGame(final int gamecode) {
        service.getCurrentGame(gamecode, new NetworkResponseListener<Game>() {
            @Override
            public void onResponseReceived(Game game) {
                if (game != null) {
                    gameTime = Integer.toString(game.getTijdsDuur());

                    Intent i = new Intent(JoinGameActivity.this, GameCodeActivity.class);
                    if (gameCreator)
                        i.putExtra("gameCreator", true);
                    else
                        i.putExtra("gameCreator", false);

                    i.putExtra("gameCode", Integer.toString(gamecode));
                    i.putExtra("gameTime", gameTime);
                    i.putExtra("teamNaam", name);
                    startActivity(i);
                } else
                    Toast.makeText(JoinGameActivity.this, "The game doesn't exist", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError() {
                Toast.makeText(JoinGameActivity.this, "Error while trying to start the game", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
