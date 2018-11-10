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

    public void getTeams(int gameId){

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
                        // Converteer de response string naar een JSONObject, JSONArray eruit halen en de inhoud omzetten naar JSONObjecten en dan bewaren als Team object in list
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
                        Log.d("Teams", "1 teams list: "+ teams);
                    } catch (Throwable t) {
                        Log.e("Teams", "error: " +t);
                    }
                    Log.d("Teams", "2 response is sucessfull teams list: "+ teams);

                } else {
                    // Request not successful
                    Log.d("Teams", "getTeams: bad request");
                }
                Log.d("Teams", "3 on response teams list: "+ teams);
            }

        });
    }
}
