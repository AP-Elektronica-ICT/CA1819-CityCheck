package cloudapplications.citycheck.Activities;

import android.Manifest;
import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import cloudapplications.citycheck.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createGameWindowButton = findViewById(R.id.button_create_game_window);
        Button joinGameWindowButton = findViewById(R.id.button_join_game_window);

        createGameWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), CreateGameActivity.class);
                startActivity(i);
            }
        });

        joinGameWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), JoinGameActivity.class);
                i.putExtra("gameCode", "-1");
                startActivity(i);
            }
        });

        // Ask permission for gps
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }
}
