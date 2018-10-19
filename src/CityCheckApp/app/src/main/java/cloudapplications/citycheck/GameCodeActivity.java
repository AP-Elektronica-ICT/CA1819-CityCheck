package cloudapplications.citycheck;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class GameCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_code);

        Spinner gameTimeSpinner = findViewById(R.id.spinner_game_time);
        Button startGameButton = findViewById(R.id.button_start_game);
        TextView codeTextView = findViewById(R.id.text_view_code);

        int randomCode = new Random().nextInt(10000 - 1000) + 1000;
        codeTextView.setText(Integer.toString(randomCode));
        String[] items = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gameTimeSpinner.setAdapter(adapter);

        gameTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                switch (position) {
                    case 0:
                        Toast.makeText(GameCodeActivity.this, "First item", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(GameCodeActivity.this, "Second item", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        Toast.makeText(GameCodeActivity.this, "Third item", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GameCodeActivity.this, "Start game", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
