package cloudapplications.citycheck;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;

public class GameCodeActivity extends AppCompatActivity {

    private int gameTime;
    private int randomCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_code);

        Spinner gameTimeSpinner = findViewById(R.id.spinner_game_time);
        Button startGameButton = findViewById(R.id.button_start_game);
        TextView codeTextView = findViewById(R.id.text_view_code);

        randomCode = new Random().nextInt(10000 - 1000) + 1000;
        codeTextView.setText(Integer.toString(randomCode));
        String[] items = new String[]{"1", "2", "3"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        gameTimeSpinner.setAdapter(adapter);

        gameTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                gameTime = position + 1;
                Toast.makeText(GameCodeActivity.this, Integer.toString(gameTime), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveGameToDatabase();

//                Intent i = new Intent(view.getContext(), GameActivity.class);
//                startActivity(i);
            }
        });
    }

    private void saveGameToDatabase() {
        OkHttpCall call = new OkHttpCall();
        Call response = call.post("http://84.197.102.107/api/citycheck/newgame", "{'TijdsDuur':2}", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d("GameCodeActivity", "saveGameToDatabase: " + responseStr);
                    // Do what you want to do with the response.
                } else {
                    // Request not successful
                }
            }
        });
    }

    private void getExample() {
        OkHttpCall call = new OkHttpCall();
        Call response = call.get("http://84.197.102.107/api/citycheck/allgames", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d("GameCodeActivity", "saveGameToDatabase: " + responseStr);
                    // Do what you want to do with the response.
                } else {
                    String responseStr = response.message();
                    Log.d("GameCodeActivity", "ERROR: " + responseStr);
                }
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
