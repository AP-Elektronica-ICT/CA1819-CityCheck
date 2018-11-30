package cloudapplications.citycheck.Models;

public class Team {

    private int Id;
    private int Punten;
    private String Kleur;
    private String TeamNaam;
    private long HuidigeLong;
    private long HuidigeLat;
    private TeamTrace TeamTrace;

    public Team(String teamNaam, int kleur, int punten) {
        this.TeamNaam = teamNaam;
        this.Kleur = Integer.toString(kleur);
        this.Punten = punten;
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

    public long getHuidigeLong() {
        return HuidigeLong;
    }

    public void setHuidigeLong(long huidigeLong) {
        HuidigeLong = huidigeLong;
    }

    public long getHuidigeLat() {
        return HuidigeLat;
    }

    public void setHuidigeLat(long huidigeLat) {
        HuidigeLat = huidigeLat;
    }

    public TeamTrace getTeamTrace() {
        return TeamTrace;
    }

    public void setTeamTrace(TeamTrace teamTrace) {
        TeamTrace = teamTrace;
    }

}