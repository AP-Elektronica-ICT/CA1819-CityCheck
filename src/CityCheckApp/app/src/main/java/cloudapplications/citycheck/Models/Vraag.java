package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Vraag {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("vraagZin")
    @Expose
    private String VraagZin;

    @SerializedName("antwoorden")
    @Expose
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
