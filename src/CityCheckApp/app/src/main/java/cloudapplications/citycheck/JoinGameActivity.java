package cloudapplications.citycheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class JoinGameActivity extends AppCompatActivity {

    private Button btnPickColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        btnPickColor = (Button) findViewById(R.id.button_pick_color);

        btnPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "kleur kiezen", Toast.LENGTH_LONG).show();

                //Kleurkiezer weergeven




            }
        });




    }
}
