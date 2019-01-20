package cloudapplications.citycheck.APIService;

import java.util.List;

import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.GameDoel;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.StringReturn;
import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.Models.Vraag;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkManager {

    // Static variable single_instance of type Retrofit
    private static NetworkManager single_instance = null;
    private static final String BASE_URL = "http://84.197.96.121/api/citycheck/";
    private static Retrofit retrofit = null;
    private final CityCheckApiInterface api;

    // Private constructor restricted to this class itself
    private NetworkManager() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        api = retrofit.create(CityCheckApiInterface.class);
    }

    // Static method to create instance of Singleton class
    public static NetworkManager getInstance() {
        if (single_instance == null)
            single_instance = new NetworkManager();

        return single_instance;
    }

    // Alle game calls
    public void getGames(NetworkResponseListener<List<Game>> listener) {
        api.getAllGames().enqueue(new NetworkResponse<>(listener));
    }

    public void getCurrentGame(int id, NetworkResponseListener<Game> listener) {
        api.getCurrentGame(id).enqueue(new NetworkResponse<>(listener));
    }

    public void createNewGame(Game game, NetworkResponseListener<Game> listener) {
        api.createNewGame(game).enqueue(new NetworkResponse<>(listener));
    }


    public void createDemoGame(Game game, NetworkResponseListener<Game> listener) {
        api.createDemoGame(game).enqueue(new NetworkResponse<>(listener));
    }

    public void startGame(int id, double millis, NetworkResponseListener<Double> listener) {
        api.startGame(id, millis).enqueue(new NetworkResponse<>(listener));
    }

    public void deleteGame(int id, NetworkResponseListener<String> listener) {
        api.deleteGame(id).enqueue(new NetworkResponse<>(listener));
    }

    // Alle team calls
    public void createTeam(int gameId, Team team, NetworkResponseListener<Team> listener) {
        api.createNewTeam(gameId, team).enqueue(new NetworkResponse<>(listener));
    }

    public void getAllTeamsFromGame(int gameId, NetworkResponseListener<List<Team>> listener) {
        api.getAllTeamsFromGame(gameId).enqueue(new NetworkResponse<>(listener));
    }

    public void postHuidigeLocatie(int gameId, String teamNaam, Locatie huidigeLocatie, NetworkResponseListener<Team> listener) {
        api.postHuidigeLocatie(gameId, teamNaam, huidigeLocatie).enqueue(new NetworkResponse<>(listener));
    }

    public void getAllTeamTraces(int gameId, NetworkResponseListener<List<Team>> listener) {
        api.getAllTeamTraces(gameId).enqueue(new NetworkResponse<>(listener));
    }

    public void deleteTeamtraces(int gameId, NetworkResponseListener<Boolean> listener){
        api.deleteTraces(gameId).enqueue(new NetworkResponse<Boolean>(listener));
    }

    public void setTeamScore(int gameId, String teamNaam, int score, NetworkResponseListener<Integer> listener) {
        api.setTeamScore(gameId, teamNaam, score).enqueue(new NetworkResponse<>(listener));
    }

    public void getScoreTeam(int gameId, String teamNaam, NetworkResponseListener<Integer> listener) {
        api.getScoreTeam(gameId, teamNaam).enqueue(new NetworkResponse<>(listener));
    }

    //Doellocatie calls
    public void getDoelLocatieVraag(int doelLocatieId, NetworkResponseListener<Vraag> listener) {
        api.getDoelLocatieVraag(doelLocatieId).enqueue(new NetworkResponse<>(listener));
    }

    public void claimDoelLocatie(int gameId, int locId, NetworkResponseListener<StringReturn> listener) {
        api.claimDoelLocatie(gameId, locId).enqueue(new NetworkResponse<>(listener));
    }

    public void checkClaimed(int gameId, NetworkResponseListener listener){
        api.checkClaims(gameId).enqueue(new NetworkResponse<List<GameDoel>>(listener));
    }
}
