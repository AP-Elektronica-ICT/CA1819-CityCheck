package cloudapplications.citycheck.Models;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Random;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Activities.GameActivity;

public class OtherTeams {

    private NetworkManager service = NetworkManager.getInstance();
    private int gameId;
    private GoogleMap kaart;
    private Activity activity;
    private String teamNaam;

    public OtherTeams(int gameId, String teamnaam, GoogleMap map, Activity activity){
        this.gameId=gameId;
        this.kaart=map;
        this.activity=activity;
        this.teamNaam=teamnaam;
    }


    public void getTeamsOnMap() {
        service.getAllTeamsFromGame(gameId, new NetworkResponseListener<List<Team>>() {
            @Override
            public void onResponseReceived(final List<Team> teams) {
                // Show teams on map
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (Team team : teams) {
                            Log.d("Teams", "Team name: " + team.getTeamNaam());
                            if (!team.getTeamNaam().equals(teamNaam)) {
                                Random rand = new Random();
                                float lat = (float) (rand.nextFloat() * (51.30 - 50.00) + 50.00);
                                float lon = (float) (rand.nextFloat() * (5.30 - 2.30) + 2.30);
                                // mMap.clear();
                                kaart.addMarker(new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        // .position(new LatLng(teams.get(i).getHuidigeLocatie().getLatitude(), teams.get(i).getHuidigeLocatie().getLongitude()))
                                        .title(team.getTeamNaam())
                                        .icon(getMarkerIcon(team.getKleur())));
                                Log.d("Retrofit", "Team marker added: #" + Integer.toHexString(team.getKleur()));
                            }
                        }
                    }
                });
            }

            @Override
            public void onError() {
                Toast.makeText(activity, "Error while trying to get the teams of the current game", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private BitmapDescriptor getMarkerIcon(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        return BitmapDescriptorFactory.defaultMarker(hsv[0]);
    }
}
