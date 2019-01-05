package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Vraag {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("vraagZin")
    @Expose
    private String VraagZin;

    @SerializedName("antwoorden")
    @Expose
    private ArrayList<Antwoord> Antwoorden;

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

    public ArrayList<Antwoord> getAntwoorden() {
        return Antwoorden;
    }

    public void setAntwoorden(ArrayList<Antwoord> antwoorden) {
        Antwoorden = antwoorden;
    }
}
