package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Game {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("gameCode")
    @Expose
    private Integer gameCode;

    @SerializedName("tijdsDuur")
    @Expose
    private Integer tijdsDuur;

    @SerializedName("hasStarted")
    @Expose
    private Boolean hasStarted;

    @SerializedName("millisStarted")
    @Expose
    private Long millisStarted;

    @SerializedName("teams")
    @Expose
    private ArrayList<Team> teams;

    @SerializedName("gameDoelen")
    @Expose
    private ArrayList<GameDoel> gameDoelen;

    public Game(Integer tijdsDuur) {
        this.tijdsDuur = tijdsDuur;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGameCode() {
        return gameCode;
    }

    public void setGameCode(Integer gameCode) {
        this.gameCode = gameCode;
    }

    public Integer getTijdsDuur() {
        return tijdsDuur;
    }

    public void setTijdsDuur(Integer tijdsDuur) {
        this.tijdsDuur = tijdsDuur;
    }

    public Boolean getHasStarted() {
        return hasStarted;
    }

    public void setHasStarted(Boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public Long getMillisStarted() {
        return millisStarted;
    }

    public void setMillisStarted(Long millisStarted) {
        this.millisStarted = millisStarted;
    }

    public ArrayList<Team> getTeams() {
        return teams;
    }

    public void setTeams(ArrayList<Team> teams) {
        this.teams = teams;
    }

    public ArrayList<GameDoel> getGameDoelen() {
        return gameDoelen;
    }

    public void setGameDoelen(ArrayList<GameDoel> gameDoelen) {
        this.gameDoelen = gameDoelen;
    }
}