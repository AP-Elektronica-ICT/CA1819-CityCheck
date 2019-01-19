package cloudapplications.citycheck.APIService;

import java.util.List;

import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.GameDoel;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.StringReturn;
import cloudapplications.citycheck.Models.Team;
import cloudapplications.citycheck.Models.Vraag;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CityCheckApiInterface {

    // Game calls
    @POST("newgame")
    Call<Game> createNewGame(@Body Game newGame);

    @POST("newdemo")
    Call<Game> createDemoGame(@Body Game newGame);

    @GET("allgames")
    Call<List<Game>> getAllGames();

    @GET("currentgame/{id}")
    Call<Game> getCurrentGame(@Path("id") int id);

    @POST("startgame/{id}/{milli}")
    Call<Double> startGame(@Path("id") int id, @Path("milli") double milli);

    @DELETE("currentgame/{id}")
    Call<String> deleteGame(@Path("id") int id);

    // Team calls
    @POST("teams/{gameId}")
    Call<Team> createNewTeam(@Path("gameId") int gameId, @Body Team newTeam);

    @GET("currentgame/teams/{gameId}")
    Call<List<Team>> getAllTeamsFromGame(@Path("gameId") int gameId);

    @POST("teams/{id}/{teamname}/huidigeLocatie")
    Call<Team> postHuidigeLocatie(@Path("id") int id, @Path("teamname") String teamName, @Body Locatie huidigeLocatie);

    @GET("teams/{id}/trace")
    Call<List<Team>> getAllTeamTraces(@Path("id") int id);

    @DELETE("teamtraces/{id}/clear")
    Call<Boolean> deleteTraces(@Path("id") int id);

    @GET("teams/{id}/{teamname}/myscore")
    Call<Integer> getScoreTeam(@Path("id") int id, @Path("teamname") String teamname);

    @POST("teams/{id}/{teamname}/setmyscore/{newscore}")
    Call<Integer> setTeamScore(@Path("id") int id, @Path("teamname") String teamname, @Path("newscore") int newscore);

    // Doellocatie calls
    @GET("locquest/{id}")
    Call<Vraag> getDoelLocatieVraag(@Path("id") int id);

    @POST("claimDoelLoc/{id}/{locid}")
    Call<StringReturn> claimDoelLocatie(@Path("id") int id, @Path("locid") int locid);

    @GET("checkClaims/{id}")
    Call<List<GameDoel>> checkClaims(@Path("id") int id);
}
