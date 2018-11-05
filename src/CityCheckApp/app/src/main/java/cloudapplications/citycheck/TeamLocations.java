package cloudapplications.citycheck;

import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class TeamLocations extends AppCompatActivity {

    OkHttpCall call = new OkHttpCall();

    public String getMyLocation(int id) throws IOException {
       return call.get(String.format("http://84.197.102.107/api/citycheck/teams/%d/huidigeLocatie", id));
    }



}
