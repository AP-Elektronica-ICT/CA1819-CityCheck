package cloudapplications.citycheck.APIService;

import java.util.List;

import cloudapplications.citycheck.Models.Game;
import cloudapplications.citycheck.Models.Locatie;
import cloudapplications.citycheck.Models.Team;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CityCheckApiInterface {

//game calls
    @POST("newgame")
    Call<Game>createNewGame(@Body Game newGame);

    @GET("allgames")
    Call<List<Game>>getAllGames();

    @GET("currentgame/{id}")
    Call<Game>getCurrentGame(@Path("id") int id);

    @POST("startgame/{id}/{milli}")
    Call<Double>startGame(@Path("id") int id,
                          @Path("milli") double milli);

    //team calls
    @POST("teams/{gameId}")
    Call<Team>createNewTeam(@Path("gameId") int gameId, @Body Team newTeam);

    @GET("currentgame/teams/{gameId}")
    Call<List<Team>>getAllTeamsFromGame(@Path("gameId") int gameId);

    @POST("teams/{id}/{teamname}/huidigeLocatie")
    Call<Team>postHuidigeLocatie(@Path("id") int id, @Path("teamname") String teamName, @Body Locatie huidigeLocatie);

    @GET("teams/{id}/trace")
    Call<List<Team>>getAllTeamTraces(@Path("id") int id);

    @GET("teams/{id}/{teamname}/myscore")
    Call<Integer>getScoreTeam(@Path("id") int id, @Path("teamname") String teamname);

    @POST("teams/{id}/{teamname}/setmyscore")
    Call<Boolean>setTeamScore(@Path("id") int id, @Path("teamname") String teamname, @Body int score);

    //doellocatie calls

}
