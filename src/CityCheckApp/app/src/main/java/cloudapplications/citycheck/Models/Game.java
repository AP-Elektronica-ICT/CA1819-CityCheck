package cloudapplications.citycheck.Models;

public class Game {

    private Integer id;

    private Integer gameCode;

    private Integer tijdsDuur;

    private Object teams;

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

    public Object getTeams() {
        return teams;
    }

    public void setTeams(Object teams) {
        this.teams = teams;
    }

}