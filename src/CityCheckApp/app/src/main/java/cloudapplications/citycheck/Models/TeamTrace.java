package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

class TeamTrace {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("lat")
    @Expose
    private long Lat;

    @SerializedName("long")
    @Expose
    private long Long;


    public int getId() {
        return Id;
    }

    public long getLat() {
        return Lat;
    }

    public long getLong() {
        return Long;
    }
}
