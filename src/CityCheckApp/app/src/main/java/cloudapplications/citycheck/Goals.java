package cloudapplications.citycheck;

import android.app.Activity;
import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.GameDoel;

public class Goals {
    private NetworkManager service;
    private int gameId;
    private String TAG = "Goals";

    private ArrayList<GameDoel> goals;
    public ArrayList<GameDoel> currentGoals;
    private SparseArray<Marker> markers;

    private GoogleMap map;
    private Activity activity;

    //public methoden
    public Goals(int gameId, GoogleMap kaart, Activity activityIn) {
        this.gameId = gameId;
        this.map = kaart;
        this.activity = activityIn;

        //r = new Random();
        markers = new SparseArray<>();
        service = NetworkManager.getInstance();
        getGoals();
    }

    public void GetNewGoals(int Time, int Interval) {
        if (Time < Interval || Time % Interval == 0) {
            // Bepalen welke locaties getoond moeten worden adhv de verstreken tijd
            int interval = (Time / Interval);
            //Log.d(TAG, "Interval nummer " + interval);

            if (goals != null) {
                if (!(interval == 0 && markers.size() > 0)) {
                    removePreviousMarkers();
                    // 3 nieuwe locaties toevoegen
                    // Huidige doelen instellen
                    currentGoals = new ArrayList<>();
                    for (int i = (interval * 3); i < ((interval * 3) + 3); i++) {
                        if (i < goals.size()) {
                            currentGoals.add(goals.get(i));
                            placeMarker(i);
                        }
                    }
                }
                //Log.d(TAG, "currentgoals: " + currentGoals.size());

            } else {
                Log.d(TAG, "There are no goals to show. New request");
                getGoals();
            }
        }
    }

    public void RemoveCaimedLocations(){
        if(goals != null) {
            //getGoals();
            service.checkClaimed(gameId, new NetworkResponseListener<List<GameDoel>>() {
                @Override
                public void onResponseReceived(List<GameDoel> claimedList) {
                    int index = 0;
                    for (GameDoel doel: claimedList) {
                        if(doel.getClaimed()){
                            if(markers.get(doel.getId()) != null){
                                markers.get(doel.getId()).remove();
                                Log.d(TAG, "doelmarkers op de kaart removed: " + markers.size());
                                markers.delete(doel.getId());
                                Log.d(TAG, "doelmarkers in array removed: " + markers.size());

                            }

                        }
                        //booleans in current goals gelijk zetten met remote
                        if(goals.get(index).getClaimed() != doel.getClaimed()){
                            if(goals.get(index).getId() == doel.getId()){
                                //Log.d(TAG, goals.get(index).getId() + ":"+ goals.get(index).getClaimed() + " en " + doel.getId() + ":" + doel.getClaimed());
                                goals.get(index).setClaimed(doel.getClaimed());
                                //Toast.makeText(activity, goals.get(index).getDoel().getTitel() + " is claimed", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if(currentGoals != null){
                            for(GameDoel current: currentGoals){
                                if(current.getId() == doel.getId()){
                                    //Log.d(TAG, current.getId() + ":"+ current.getClaimed() + " en " + doel.getId() + ":" + doel.getClaimed());
                                    current.setClaimed(doel.getClaimed());
                                    //Log.d(TAG, current.getId() + ":"+ current.getClaimed() + " en " + doel.getId() + ":" + doel.getClaimed());
                                }
                            }
                        }


                        index++;
                    }
                }

                @Override
                public void onError() {

                }
            });
        }
    }

    //private methoden
    private void getGoals() {
        service.getCurrentGame(gameId, new NetworkResponseListener<Game>() {
            @Override
            public void onResponseReceived(Game game) {
                // Log.d(TAG, ""+ Arrays.toString(game.getGameDoelen().toArray()));
                goals = game.getGameDoelen();
                Log.d(TAG, "Size: " + goals.size());

                /* Huidige doelen instellen
                currentGoals = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    currentGoals.add(goals.get(i));
                }*/
            }

            @Override
            public void onError() {
                Log.d(TAG, "Something went wrong while getting the goals");
            }
        });
    }

    private void placeMarker(int i) {
        // LatLng loc = new LatLng((r.nextDouble()*(51.2500 - 50.1800) + 50.1800),(r.nextDouble()* (4.8025 - 4.0000) + 4.0000));
        Log.d(TAG, "Add new location: " + goals.get(i).getDoel().getTitel());
        GameDoel locatie = goals.get(i);
        Log.d(TAG, "" + locatie.getDoel().getLocatie().getLat() + "; " + locatie.getDoel().getLocatie().getLong());

        markers.append(locatie.getId(), map.addMarker(
                new MarkerOptions()
                        .title(locatie.getDoel().getTitel())
                        // .position(loc)
                        .position(new LatLng(locatie.getDoel().getLocatie().getLat(), locatie.getDoel().getLocatie().getLong()))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.coin_small))
        ));
    }

    private void removePreviousMarkers() {
        // Vorige marker locaties verwijderen
        if (markers.size() > 0) {
            Log.d(TAG, "Size markers to delete: " + markers.size());
            // Eerst size opslaan want die veranderd als je een element delete uit de array
            int size = markers.size();
            for (int i = 0; i < size; i++) {
                Log.d(TAG, "Marker to delete: " + i);
                int key = markers.keyAt(i);
                // Marker van kaart verwijderen
                markers.get(key).remove();
            }
        }

        // Voor de zekerheid array volledig clearen
        markers.clear();
    }
}
