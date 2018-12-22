package cloudapplications.citycheck;

import android.app.Activity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Activities.GameActivity;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.Team;

public class OtherTeams {

    private NetworkManager service = NetworkManager.getInstance();
    private int gameId;
    private GoogleMap kaart;
    private Activity activity;
    private String teamNaam;
    private SparseArray<Marker> markers;
    public List<Locatie> Traces;

    public OtherTeams(int gameId, String teamnaam, GoogleMap map, Activity activity){
        this.gameId=gameId;
        this.kaart=map;
        this.activity=activity;
        this.teamNaam=teamnaam;
        markers = new SparseArray<Marker>();
        Traces = new ArrayList<Locatie>();
    }


    public void getTeamsOnMap() {
        service.getAllTeamTraces(gameId, new NetworkResponseListener<List<Team>>() {
            @Override
            public void onResponseReceived(final List<Team> teams) {
                if (teams != null) {
                    // Show teams on map
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            for (Team team : teams) {
                                Log.d("Teams", "Team name: " + team.getTeamNaam());
                                if (!team.getTeamNaam().equals(teamNaam)) {
                                /*Random rand = new Random();
                                float lat = (float) (rand.nextFloat() * (51.30 - 50.00) + 50.00);
                                float lon = (float) (rand.nextFloat() * (5.30 - 2.30) + 2.30);
                                Locatie loc = new Locatie(lat, lon);
                                Traces.add(loc);*/
                                    placeMarker(team.getLocatie(), team);
                                    drawPath(team);
                                }
                            }
                        }
                    });
                }
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

    private void placeMarker(Locatie locatie, Team team){
        if(markers.get(team.getId()) == null){
            markers.append(team.getId(), kaart.addMarker(
                    new MarkerOptions()
                            .title(team.getTeamNaam())
                            .position(new LatLng(locatie.getLat(),locatie.getLong()))
                            .icon(getMarkerIcon(team.getKleur()))));
            Log.d("marker", " new marker in markers array: " + markers.size());
        }
        else{
            markers.get(team.getId()).setPosition(new LatLng(locatie.getLat(),locatie.getLong()));
            Log.d("marker", "update marker: " + markers.size());
        }
    }

    private void drawPath(Team team) {
        if(team.getTeamTrace() != null && team.getTeamTrace().size() >4){
            Polyline polyline1 = kaart.addPolyline(new PolylineOptions()
                    .add(
                            new LatLng(Traces.get(Traces.size() - 2).getLat(), Traces.get(Traces.size() - 2).getLong()),
                            new LatLng(Traces.get(Traces.size() - 1).getLat(), Traces.get(Traces.size() - 1).getLong()))
                    .color(team.getKleur()));
        }
    }
}
