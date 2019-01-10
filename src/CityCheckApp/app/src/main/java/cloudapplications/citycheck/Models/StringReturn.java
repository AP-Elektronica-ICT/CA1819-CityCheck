package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StringReturn {

    @SerializedName("returnWaarde")
    @Expose
    private String returnWaarde;

    public String getWaarde() {
        return returnWaarde;
    }

    public void setWaarde(String waarde) {
        returnWaarde = waarde;
    }
}