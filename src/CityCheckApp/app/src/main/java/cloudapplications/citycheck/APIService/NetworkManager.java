package cloudapplications.citycheck.APIService;

import java.util.List;

import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.Team;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    // static variable single_instance of type Retrofit
    private static NetworkManager single_instance = null;
    private static final String BASE_URL = "http://84.197.96.121/api/citycheck/";
    private static Retrofit retrofit = null;
    private final CityCheckApiInterface api;

    // private constructor restricted to this class itself
    private NetworkManager() {
        if(retrofit ==null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        api= retrofit.create(CityCheckApiInterface.class);
    }

    // static method to create instance of Singleton class
    public static NetworkManager getInstance() {
        if (single_instance == null)
            single_instance = new NetworkManager();

        return single_instance;
    }


    //alle game calls
    public void getGames(NetworkResponseListener<List<Game>> listener){
        api.getAllGames().enqueue(new NetworkResponse<List<Game>>(listener));

    }

    public void getCurrentGame(int id, NetworkResponseListener<Game> listener) {
        api.getCurrentGame(id).enqueue(new NetworkResponse<Game>(listener));
    }

    public void createNewGame(Game game, NetworkResponseListener<Game> listener){
        api.createNewGame(game).enqueue(new NetworkResponse<Game>(listener));
    }

    public void startGame(int id, double millis, NetworkResponseListener<Double> listener){
        api.startGame(id, millis).enqueue(new NetworkResponse<Double>(listener));
    }

    public void deleteGame(int id, NetworkResponseListener<String> listener){
        api.deleteGame(id).enqueue(new NetworkResponse<String>(listener));
    }


    //alle team calls
    public void createTeam(int gameId, Team team, NetworkResponseListener<Team> listener){
        api.createNewTeam(gameId, team).enqueue(new NetworkResponse<Team>(listener));
    }

    public void getAllTeamsFromGame(int gameId, NetworkResponseListener<List<Team>> listener){
        api.getAllTeamsFromGame(gameId).enqueue(new NetworkResponse<List<Team>>(listener));
    }

    public void postHuidigeLocatie(int gameId, String teamNaam, Locatie huidigeLocatie, NetworkResponseListener<Team> listener){
        api.postHuidigeLocatie(gameId, teamNaam, huidigeLocatie).enqueue(new NetworkResponse<Team>(listener));
    }

    public void getAllTeamTraces(int gameId, NetworkResponseListener<List<Team>> listener){
        api.getAllTeamTraces(gameId).enqueue(new NetworkResponse<List<Team>>(listener));
    }

    public void setTeamScore(int gameId, String teamNaam, int score, NetworkResponseListener<Boolean> listener){
        api.setTeamScore(gameId, teamNaam, score).enqueue(new NetworkResponse<Boolean>(listener));
    }

    public void getScoreTeam(int gameId, String teamNaam, NetworkResponseListener<Integer> listener){
        api.getScoreTeam(gameId, teamNaam).enqueue(new NetworkResponse<Integer>(listener));
    }

    //alle doellocatie calls
}
