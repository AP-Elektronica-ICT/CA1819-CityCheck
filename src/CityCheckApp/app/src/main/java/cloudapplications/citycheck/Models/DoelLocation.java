package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DoelLocation {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("titel")
    @Expose
    private String Titel;

    @SerializedName("locatie")
    @Expose
    private Locatie Locatie;

    @SerializedName("vragen")
    @Expose
    private List<Vraag> Vragen;

    public DoelLocation(String titel, Locatie locatie, List<Vraag> vragen) {
        Titel = titel;
        Locatie = locatie;
        Vragen = vragen;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitel() {
        return Titel;
    }

    public void setTitel(String titel) {
        Titel = titel;
    }

    public Locatie getLocatie() {
        return Locatie;
    }

    public void setLocatie(Locatie locatie) {
        Locatie = locatie;
    }

    public List<Vraag> getVragen() {
        return Vragen;
    }

    public void setVragen(List<Vraag> vragen) {
        Vragen = vragen;
    }

}
