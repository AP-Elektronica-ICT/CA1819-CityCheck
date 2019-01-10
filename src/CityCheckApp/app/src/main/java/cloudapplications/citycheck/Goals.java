package cloudapplications.citycheck;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import cloudapplications.citycheck.APIService.NetworkManager;
import cloudapplications.citycheck.APIService.NetworkResponseListener;
import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.GameDoel;

public class Goals {
    private NetworkManager service;
    private int gameId;
    private ArrayList<GameDoel> goals;
    public ArrayList<GameDoel> currentGoals;
    private String TAG = "Goals";
    private GoogleMap map;
    private SparseArray<Marker> markers;
    //private  Random r;

    public Goals(int gameId, GoogleMap kaart) {
        this.gameId = gameId;
        this.map = kaart;

        //r = new Random();
        markers = new SparseArray<>();
        service = NetworkManager.getInstance();
        getGoals();
    }

    public void getNewGoals(int Time, int Interval) {

            // Bepalen welke locaties getoond moeten worden adhv de verstreken tijd
            int interval = (Time / Interval);
            Log.d(TAG, "Interval nummer " + interval);

            if (goals != null) {
                if (!(interval == 0 && markers.size() > 0)) {
                    removePreviousMarkers();
                    // 3 nieuwe locaties toevoegen
                    for (int i = (interval * 3); i < ((interval * 3) + 3); i++) {
                        if (i < goals.size()) {
                            currentGoals.add(goals.get(i));
                            placeMarker(i);
                        }
                    }
                }

            } else {
                Log.d(TAG, "There are no goals to show. New request");
                getGoals();
            }

    }

    private void getGoals() {
        service.getCurrentGame(gameId, new NetworkResponseListener<Game>() {
            @Override
            public void onResponseReceived(Game game) {
                // Log.d(TAG, ""+ Arrays.toString(game.getGameDoelen().toArray()));
                goals = game.getGameDoelen();
                Log.d(TAG, "Size: " + goals.size());

                // Huidige doelen instellen
                currentGoals = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    currentGoals.add(goals.get(i));
                }
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
