package cloudapplications.citycheck;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class CreateGameActivity extends AppCompatActivity {

    private int gameTime;
    private Button btnTime;
    private TextView txtTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game);

        Button createGameButton = findViewById(R.id.button_create_game);
        btnTime = (Button) findViewById(R.id.button_timePick);
        txtTime = (TextView) findViewById(R.id.txt_Time);
        gameTime = 1;
        txtTime.setText(""+gameTime);

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //We gaan een tijd selecteren
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



        public void timePick(){

            final Dialog d = new Dialog(CreateGameActivity.this);
            d.setTitle("NumberPicker");
            d.setContentView(R.layout.timepick_dialog);
            Button b1 = (Button) d.findViewById(R.id.btn_timeSet);
            Button b2 = (Button) d.findViewById(R.id.btn_timeCancel);
            final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker);
            np.setMaxValue(3);
            np.setMinValue(1);
            np.setWrapSelectorWheel(false);
            np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker numberPicker, int old, int newTijd) {
                    //Toast.makeText(getApplicationContext(), "Time: "+Integer.toString(newTijd), Toast.LENGTH_SHORT).show();
                };
            });
            b1.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    //de gekozen tijd ophalen
                    gameTime = np.getValue();
                    //Toast.makeText(getApplicationContext(), "Gekozen tijdsduur: "+Integer.toString(gameTime), Toast.LENGTH_LONG).show();
                    txtTime.setText(""+gameTime);
                    //Dialog sluiten
                    d.dismiss();
                }
            });
            b2.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    //Annuleren
                    d.dismiss();
                }
            });
            d.show();


    }


}