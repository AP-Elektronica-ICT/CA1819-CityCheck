package cloudapplications.citycheck;

import java.util.List;

public class Team {

    // Properties van een team
    private int id;
    private int punten;
    private int kleur;
    private String teamNaam;
    private long huidigeLong;
    private long huidigeLat;
    private List<TeamTrace> teamTraces;

    Team(String teamNaam, int kleur, int punten) {
        this.teamNaam = teamNaam;
        this.kleur = kleur;
        this.punten = punten;
    }

    public int getId(){ return id;}

    public void setId(int id){this.id = id;}

    public String getTeamNaam() {
        return teamNaam;
    }

    public void setTeamNaam(String teamNaam) {
        this.teamNaam = teamNaam;
    }

    public int getKleur() {
        return kleur;
    }

    public void setKleur(int kleur) {
        this.kleur = kleur;
    }

    public int getPunten() {
        return punten;
    }

    public void setPunten(int punten) {
        this.punten = punten;
    }

    public long getLat() {
        return huidigeLat;
    }

    public void setLat(long lat) {
        this.huidigeLat = lat;
    }

    public long getLong(){return huidigeLong;}

    public void setLong(long lon){this.huidigeLong= lon;}
}