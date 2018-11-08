package cloudapplications.citycheck;

import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

public class JoinGameActivity extends AppCompatActivity {

    private Button btnPickColor;
    private int currentColor = 0xffffffff;

    private TextView txt_teamName;
    private EditText edit_teamName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        btnPickColor = (Button) findViewById(R.id.button_pick_color);
        txt_teamName = (TextView) findViewById(R.id.TV_TeamName);
        edit_teamName = (EditText) findViewById(R.id.edit_text_team_name);


        edit_teamName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                txt_teamName.setText("Your Name");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                txt_teamName.setText(edit_teamName.getText());
            }
        });





        btnPickColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "kleur kiezen", Toast.LENGTH_LONG).show();

                //Kleurkiezer weergeven
                ColorPickerDialogBuilder
                        .with(JoinGameActivity.this)
                        .setTitle("Choose color")
                        .initialColor(currentColor)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .density(12)
                        .setOnColorSelectedListener(new OnColorSelectedListener() {
                            @Override
                            public void onColorSelected(int selectedColor) {
                                //Toast.makeText(getApplicationContext(), "Gekozen kleur: "+selectedColor, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setPositiveButton("ok", new ColorPickerClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                                currentColor = selectedColor;
                                //Toast.makeText(getApplicationContext(), "Bevestigde kleur: "+selectedColor, Toast.LENGTH_LONG).show();
                                txt_teamName.setTextColor(selectedColor);
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .build()
                        .show();




            }
        });




    }
}
