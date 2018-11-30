package cloudapplications.citycheck.Models;

import java.util.List;

public class Vraag {

    private int Id;
    private String VraagZin;
    private List<Antwoord> Antwoorden;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getVraagZin() {
        return VraagZin;
    }

    public void setVraagZin(String vraagZin) {
        VraagZin = vraagZin;
    }

    public List<Antwoord> getAntwoorden() {
        return Antwoorden;
    }

    public void setAntwoorden(List<Antwoord> antwoorden) {
        Antwoorden = antwoorden;
    }

}
