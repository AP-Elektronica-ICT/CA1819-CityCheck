package cloudapplications.citycheck;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Team {
    //properties van een team
    public String Name;
    public String Colour;
    public long CurrentLong;
    public long CurrentLat;
    public int Id;
    public int Punten;
    public List<LatLng> Traces;

    //variabelen om teams op te halen uit database
    OkHttpCall call = new OkHttpCall();
    private List<Team> teams;

    public List<Team> getTeams(String teamName, int gameId){

        Call response = call.get(String.format("http://84.197.102.107/api/citycheck/currentgame/teams/%d", gameId), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Teams", "getTeams: failed");
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response.isSuccessful()) {
                    try {
                        JSONArray array = new JSONArray(response.body().toString());
                        for(int i=0; i<array.length(); i++){
                            teams.add((Team)array.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Teams", "getTeams: " + teams);
                    // Do what you want to do with the response.

                } else {
                    // Request not successful
                }
            }
        });

        return teams;
    }
}
