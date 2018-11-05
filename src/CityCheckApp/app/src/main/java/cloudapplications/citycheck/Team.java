package cloudapplications.citycheck;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class Team {
    public String Name;
    public String Colour;
    public LatLng CurrentLocation;
    public int Id;
    public int Punten;
    public List<LatLng> Traces;
}
