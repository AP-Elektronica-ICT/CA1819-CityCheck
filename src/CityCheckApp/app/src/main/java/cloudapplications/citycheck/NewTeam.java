package cloudapplications.citycheck;

import java.util.List;

public class NewTeam {

    // Properties van een team
    private int id;
    private int punten;
    private int kleur;
    private String teamNaam;
    private long huidigeLong;
    private long huidigeLat;
    private List<TeamTrace> teamTraces;

    NewTeam(String teamNaam, int kleur, int punten) {
        this.teamNaam = teamNaam;
        this.kleur = kleur;
        this.punten = punten;
    }

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
}