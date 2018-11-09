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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        btnPickColor = (Button) findViewById(R.id.button_pick_color);
        txt_teamName = (TextView) findViewById(R.id.TV_TeamName);
        edit_teamName = (EditText) findViewById(R.id.edit_text_team_name);
        edit_gamecode = (EditText) findViewById(R.id.edit_text_game_code);
        btnJoinGame = (Button) findViewById(R.id.button_join_game);


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
                Log.d("JoinGameActivity","team: " +name + " color: " + color + " gamecode: " + gamecode);
                addTeamToGame(name, color, gamecode);
            }
        });

    }
    private void addTeamToGame(String name, int color, final int gamecode) {
        /*Team newTeam = new Team();
        newTeam.setName(name);
        newTeam.setColour(color);*/
        OkHttpCall call = new OkHttpCall();
        Call response = call.post(String.format("http://84.197.102.107/api/citycheck/teams/%d", gamecode), "{'teamNaam':'" + name+"'}", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Als de request gelukt is
                    String responseStr = response.body().string();
                    Log.d("JoinGameActivity", "JSON object response: " + responseStr);


                    //get game info
                    startGame(gamecode);

                } else {
                    // Als er een fout is bij de request
                    Log.d("JoinGameActivity", "new team error response: " + response.message());
                }
            }
        });
    }

    private void startGame(final int gamecode){

        OkHttpCall getGame = new OkHttpCall();
        Call response = getGame.get(String.format("http://84.197.102.107/api/citycheck/currentgame/%d", gamecode), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("JoinGameActivity", "gamecode: "+ gamecode);
                if (response.isSuccessful()) {
                    // Als de request gelukt is
                    String responseStr = response.body().string();
                    JSONObject obj;

                    try {
                        // Converteer de response string naar een JSONObject en de code er uit halen
                        obj = new JSONObject(responseStr);
                        Log.d("JoinGameActivity", "JSON object response: " + obj.toString());
                        gameTime = obj.getString("tijdsDuur");
                    } catch (Throwable t) {
                        Log.e("JoinGameActivity", "Could not parse malformed JSON: \"" + responseStr + "\"");
                    }

                    Log.d("JoinGameActivity", "Gametime for intent is " + gameTime);
                    Intent i = new Intent(JoinGameActivity.this, GameActivity.class);
                    i.putExtra("gameCode", gamecode);
                    i.putExtra("gameTime", gameTime);
                    startActivity(i);
                } else {
                    // Als er een fout is bij de request
                    Log.d("JoinGameActivity", "start game error response: " + response.message());
                }
            }
        });
    }
}
