package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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

    @SerializedName("teams")
    @Expose
    private Object teams;

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

    public Object getTeams() {
        return teams;
    }

    public void setTeams(Object teams) {
        this.teams = teams;
    }

}