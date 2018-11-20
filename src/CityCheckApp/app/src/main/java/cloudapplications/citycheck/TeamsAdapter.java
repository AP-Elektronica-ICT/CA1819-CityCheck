package cloudapplications.citycheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

public class TeamsAdapter extends ArrayAdapter<Team> {

    TeamsAdapter(Context context, ArrayList<Team> arr) {
        super(context, -1, arr);
    }

    @SuppressLint("InflateParams")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(R.layout.teams_list_item, null);
        }

        TextView teamNamesTextView = view.findViewById(R.id.teams_list_entry);
        TextView teamScoresTextView = view.findViewById(R.id.scores_list_entry);

        teamNamesTextView.setText(Objects.requireNonNull(getItem(position)).getTeamNaam());
        teamNamesTextView.setTextColor(Objects.requireNonNull(getItem(position)).getKleur());

        if (Objects.requireNonNull(getItem(position)).getPunten() != -1)
            teamScoresTextView.setText(Integer.toString(Objects.requireNonNull(getItem(position)).getPunten()));

        return view;
    }
}
