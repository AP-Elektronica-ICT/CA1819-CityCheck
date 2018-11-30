package cloudapplications.citycheck.Models;

import java.util.List;

public class DoelLocation {

    private int Id;
    private String Titel;
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
