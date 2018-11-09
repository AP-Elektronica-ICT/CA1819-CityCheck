package cloudapplications.citycheck;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;


public class Team {

    //properties van een team
    private String Name;
    private int Colour;
    private long CurrentLong;
    private long CurrentLat;
    private int Id;
    private int Punten;
    private List<TeamTrace> Traces;

    public int getId() {
        return Id;
    }

    public int getPunten() {
        return Punten;
    }

    public long getCurrentLat() {
        return CurrentLat;
    }

    public int getColour() {
        return Colour;
    }

    public String getName() {
        return Name;
    }

    public long getCurrentLong() {
        return CurrentLong;
    }

    public List<TeamTrace> getTraces() {
        return Traces;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setColour(int colour) {
        Colour = colour;
    }
}
