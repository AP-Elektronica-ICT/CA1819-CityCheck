package cloudapplications.citycheck.Models;

public class Antwoord {

    private int Id;
    private String Antwoordzin;
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
