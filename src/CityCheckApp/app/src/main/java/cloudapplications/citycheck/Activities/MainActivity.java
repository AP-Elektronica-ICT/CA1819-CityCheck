package cloudapplications.citycheck.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import cloudapplications.citycheck.R;

public class MainActivity extends AppCompatActivity {

    ImageView helpImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createGameWindowButton = findViewById(R.id.button_create_game_window);
        Button joinGameWindowButton = findViewById(R.id.button_join_game_window);

        createGameWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusCheck()) {
                    Intent i = new Intent(view.getContext(), CreateGameActivity.class);
                    startActivity(i);
                }
            }
        });

        joinGameWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (statusCheck()) {
                    Intent i = new Intent(view.getContext(), JoinGameActivity.class);
                    i.putExtra("gameCode", "-1");
                    startActivity(i);
                }
            }
        });

        // Help button setten
        helpImageView = findViewById(R.id.image_view_help);
        helpImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(view.getContext(), HelpActivity.class);
                startActivity(i);
            }
        });

        // Vraag GPS toestemming
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
    }

    public boolean statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        assert manager != null;
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }


        if (!isStoragePermissionGranted()) {
            Toast.makeText(this, "You have to grant storage permissions to play the game.", Toast.LENGTH_LONG).show();
            return false;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.System.canWrite(this)){
                buildAlertMessageWriteSettings();
                return false;
            }

        }
        return true;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, you need to enable it to play the game.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageWriteSettings(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("To keep the screen on while playing, you need to grant WRITE_SETTINGS permission.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // Toestemming is in orde
                return true;
            } else {
                // Toestemming is niet in orde
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                return false;
            }
        } else {
            // Toestemming automatisch in orde op SDK < 23
            return true;
        }
    }
}