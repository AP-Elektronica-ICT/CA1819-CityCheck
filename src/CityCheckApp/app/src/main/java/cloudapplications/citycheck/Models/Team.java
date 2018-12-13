package cloudapplications.citycheck.Models;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Team {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("punten")
    @Expose
    private int Punten;

    @SerializedName("kleur")
    @Expose
    private String Kleur;

    @SerializedName("teamNaam")
    @Expose
    private String TeamNaam;

    @SerializedName("huidigeLocatie")
    @Expose
    private Locatie Locatie;

    @SerializedName("teamTraces")
    @Expose
    private ArrayList<TeamTrace> TeamTrace;

    public Team(String teamNaam, int kleur) {
        this.TeamNaam = teamNaam;
        this.Kleur = Integer.toString(kleur);
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getPunten() {
        return Punten;
    }

    public void setPunten(int punten) {
        Punten = punten;
    }

    public int getKleur() {
        return Integer.parseInt(Kleur);
    }

    public void setKleur(String kleur) {
        Kleur = kleur;
    }

    public String getTeamNaam() {
        return TeamNaam;
    }

    public void setTeamNaam(String teamNaam) {
        TeamNaam = teamNaam;
    }

    public ArrayList<TeamTrace> getTeamTrace() {
        return TeamTrace;
    }

    public void setTeamTrace(ArrayList<TeamTrace> teamTrace) {
        TeamTrace = teamTrace;
    }

    public Locatie getLocatie() {
        return Locatie;
    }

    public void setLocatie(Locatie locatie) {
        this.Locatie = locatie;
    }

}