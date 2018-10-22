package cloudapplications.citycheck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                startActivity(i);
            }
        });
    }
}
