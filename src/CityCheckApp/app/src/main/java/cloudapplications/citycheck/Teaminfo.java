package cloudapplications.citycheck;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class Teaminfo {

    //variabelen om teams op te halen uit database
    OkHttpCall call = new OkHttpCall();
    private List<Team> teams = new ArrayList<>();

    public List<Team> getTeams(int gameId){

        Call response = call.get(String.format("http://84.197.102.107/api/citycheck/currentgame/%d", gameId), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("Teams", "getTeams: failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // Als de request gelukt is
                    String responseStr = response.body().string();
                    JSONObject obj;
                    JSONArray teamsArray;
                    try {
                        // Converteer de response string naar een JSONObject en de code er uit halen
                        obj = new JSONObject(responseStr);
                        Log.d("Teams", "JSON object response: " + obj.toString());
                        teamsArray = obj.getJSONArray("teams");
                        Log.d("Teams", "array of teams: "+ teamsArray);
                        for(int i=0; i<teamsArray.length(); i++){
                            JSONObject team = teamsArray.getJSONObject(i);
                            Log.d("Teams", "teamobject: "+ team);
                            Team newTeam = new Team();
                            newTeam.id = team.getInt("id");
                            newTeam.kleur = team.getInt("kleur");
                            newTeam.teamNaam = team.getString("teamNaam");
                            newTeam.huidigdeLat = team.getLong("huidigeLat");
                            newTeam.huidigeLong = team.getLong("huidigeLong");
                            teams.add(newTeam);
                        }
                        Log.d("Teams", "teams list: "+ teams.get(1).teamNaam);
                    } catch (Throwable t) {
                        Log.e("Teams", "error: " +t);
                    }
                    /*try {
                        JSONArray array = new JSONArray(response.body().toString());
                        Log.d("Teams", "array: "+ array);
                        for(int i=0; i<array.length(); i++){
                            teams.add((Team)array.get(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d("Teams", "getTeams: " + teams);
                    // Do what you want to do with the response.*/

                } else {
                    // Request not successful
                    Log.d("Teams", "getTeams: bad request");
                }
            }
        });

        return teams;
    }
}
