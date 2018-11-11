package cloudapplications.citycheck;
import com.google.android.gms.maps.model.LatLng;
import java.util.List;


public class Team {

    //properties van een team
    public int id;
    public int punten;
    public int kleur;
    public String teamNaam;
    public long huidigeLong;
    public long huidigdeLat;
    public List<TeamTrace> teamTraces;

}
