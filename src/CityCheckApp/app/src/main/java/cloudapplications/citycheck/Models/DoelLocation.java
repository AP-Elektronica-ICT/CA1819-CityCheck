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

    @SerializedName("vragen")
    @Expose
    private List<Vraag> Vragen;

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

    public List<Vraag> getVragen() {
        return Vragen;
    }

    public void setVragen(List<Vraag> vragen) {
        Vragen = vragen;
    }

}
