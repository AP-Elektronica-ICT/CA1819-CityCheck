package cloudapplications.citycheck;

import android.content.DialogInterface;
import android.content.Intent;
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

import org.json.JSONObject;

import java.util.Objects;

public class JoinGameActivity extends AppCompatActivity {

    private Button btnPickColor;
    private Button btnJoinGame;
    private int currentColor = 0xffffffff;

    private TextView txt_teamName;
    private EditText edit_teamName;
    private EditText edit_gamecode;

    private String name;
    private int color;
    private int gamecode;
    private String gameTime;
    private boolean gameCreator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        btnPickColor = findViewById(R.id.button_pick_color);
        txt_teamName = findViewById(R.id.TV_TeamName);
        edit_teamName = findViewById(R.id.edit_text_team_name);
        edit_gamecode = findViewById(R.id.edit_text_game_code);
        btnJoinGame = findViewById(R.id.button_join_game);

        edit_teamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txt_teamName.setText("Your Name");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                txt_teamName.setText(edit_teamName.getText());
            }
        });

        gameCreator = !Objects.equals(Objects.requireNonNull(getIntent().getExtras()).getString("gameCode"), "-1");
        if (gameCreator) {
            gamecode = Integer.parseInt(getIntent().getExtras().getString("gameCode"));
            edit_gamecode.setText(Integer.toString(gamecode));
            edit_gamecode.setFocusable(false);
            edit_gamecode.setEnabled(false);
        }

        btnPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "kleur kiezen", Toast.LENGTH_LONG).show();

                //Kleurkiezer weergeven
                ColorPickerDialogBuilder
                        .with(JoinGameActivity.this)
                        .setTitle("Choose color")
                        .initialColor(currentColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //Toast.makeText(getApplicationContext(), "Gekozen kleur: "+selectedColor, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                currentColor = selectedColor;
                                //Toast.makeText(getApplicationContext(), "Bevestigde kleur: "+selectedColor, Toast.LENGTH_LONG).show();
                                txt_teamName.setTextColor(selectedColor);
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

        btnJoinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edit_teamName.getText().toString();
                color = currentColor;
                gamecode = Integer.parseInt(edit_gamecode.getText().toString());
                Log.d("JoinGameActivity", "team: " + name + " color: " + color + " gamecode: " + gamecode);
                addTeamToGame(name, color, gamecode);
            }
        });
    }

    private void addTeamToGame(String name, int color, final int gamecode) {
        /*Team newTeam = new Team();
        newTeam.setName(name);
        newTeam.setColour(color);*/
        OkHttpCall call = new OkHttpCall();
        call.post(getString(R.string.database_ip), "teams/" + Integer.toString(gamecode), "{'teamNaam':'" + name + "', 'kleur':'" + color + "'}");
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            startGame(gamecode);
        } else {
            Toast.makeText(this, "Error while trying to join the game", Toast.LENGTH_SHORT).show();
        }
    }

    private void startGame(final int gamecode) {
        OkHttpCall call = new OkHttpCall();
        call.get(getString(R.string.database_ip), "currentgame/" + Integer.toString(gamecode));
        while (call.status == OkHttpCall.RequestStatus.Undefined) ;
        if (call.status == OkHttpCall.RequestStatus.Successful) {
            JSONObject obj;
            try {
                // Converteer de response string naar een JSONObject en de tijd er uit halen
                obj = new JSONObject(call.responseStr);
                Log.d("JoinGameActivity", "JSON object response: " + obj.toString());
                gameTime = obj.getString("tijdsDuur");
            } catch (Throwable t) {
                Log.e("JoinGameActivity", "Could not parse malformed JSON: \"" + call.responseStr + "\"");
            }

            Intent i;
            if (gameCreator)
                i = new Intent(JoinGameActivity.this, GameCodeActivity.class);
            else
                i = new Intent(JoinGameActivity.this, GameActivity.class);
            i.putExtra("gameCode", Integer.toString(gamecode));
            i.putExtra("gameTime", gameTime);
            i.putExtra("teamNaam", name);
            startActivity(i);

        } else {
            Toast.makeText(this, "Error while trying to start the game", Toast.LENGTH_SHORT).show();
        }
    }
}
