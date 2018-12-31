package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GameDoel {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("doel")
    @Expose
    private DoelLocation Doel;

    @SerializedName("claimed")
    @Expose
    private boolean Claimed;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public DoelLocation getDoel() {
        return Doel;
    }

    public void setDoel(DoelLocation doel) {
        Doel = doel;
    }

    public boolean getClaimed() {
        return Claimed;
    }

    public void setClaimed(boolean claimed) {
        Claimed = claimed;
    }
}
