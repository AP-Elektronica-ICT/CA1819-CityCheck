package cloudapplications.citycheck.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Antwoord {

    @SerializedName("id")
    @Expose
    private int Id;

    @SerializedName("antwoordzin")
    @Expose
    private String Antwoordzin;

    @SerializedName("correctBool")
    @Expose
    private boolean CorrectBool;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getAntwoordzin() {
        return Antwoordzin;
    }

    public void setAntwoordzin(String antwoordzin) {
        Antwoordzin = antwoordzin;
    }

    public boolean isCorrectBool() {
        return CorrectBool;
    }

    public void setCorrectBool(boolean correctBool) {
        CorrectBool = correctBool;
    }
}
