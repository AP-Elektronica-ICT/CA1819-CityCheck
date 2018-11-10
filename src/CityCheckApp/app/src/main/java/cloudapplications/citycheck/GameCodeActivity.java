package cloudapplications.citycheck;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GameCodeActivity extends AppCompatActivity {

    String currentGameCode;
    String currentGameTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_code);

        Button startGameButton = findViewById(R.id.button_start_game);
        TextView codeTextView = findViewById(R.id.text_view_code);
        TextView timeTextView = findViewById(R.id.text_view_time);

        Log.d("Teams", "gamecode from intent: "+ getIntent().getExtras().get("gameCode"));
        currentGameCode = getIntent().getExtras().getString("gameCode");
        currentGameTime = getIntent().getExtras().getString("gameTime");

        codeTextView.setText(currentGameCode);
        timeTextView.setText(currentGameTime + " hours");

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), GameActivity.class);
                i.putExtra("gameTime", currentGameTime);
                i.putExtra("gameCode", currentGameCode);
                startActivity(i);
            }
        });
    }

//    private void saveGameToDatabaseVolley() {
//        RequestQueue queue = Volley.newRequestQueue(GameCodeActivity.this);
//        String saveURL = "http://127.0.0.1:3000/api/citycheck/newgame";
//        StringRequest postRequest = new StringRequest(Request.Method.POST, saveURL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        // Response
//                        Log.d("GameCodeActivity", "Database response: " + response);
//                        Toast.makeText(GameCodeActivity.this, "Database updated successfully", Toast.LENGTH_SHORT).show();
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // Error
//                        Log.d("GameCodeActivity", "Database error response: " + error);
//                        Toast.makeText(GameCodeActivity.this, "Error! Couldn't update the database", Toast.LENGTH_SHORT).show();
//                    }
//                }
//        ) {
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<>();
//                params.put("GameCode", Integer.toString(randomCode));
//                params.put("TijdsDuur", Integer.toString(gameTime));
//                return params;
//            }
//        };
//        queue.add(postRequest);
//    }
}
