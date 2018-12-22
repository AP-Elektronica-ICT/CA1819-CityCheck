package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TeamTrace {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("trace")
    @Expose
    private Locatie locatie;

    public TeamTrace(Locatie locatie) {
        this.locatie = locatie;
    }

    public int getId() {
        return Id;
    }

    public Locatie getLocatie() {
        return locatie;
    }

    public void setLocatie(Locatie locatie) {
        this.locatie = locatie;
    }
}
