package blueinteraction.mamn01blueinteraction;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class PlaySetupActivity extends AppCompatActivity {

    private EditText time, radius;
    private TextView timeText, radiusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_setup);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radius = (EditText) findViewById((R.id.editTextRadius));
        time = (EditText) findViewById((R.id.editTextTime));
        radiusText = (TextView) findViewById((R.id.textRadius));
        radiusText.setTextColor(Color.BLACK);
        timeText = (TextView) findViewById((R.id.textTime));
        timeText.setTextColor(Color.BLACK);
    }

    public void onClickGo(View view){
        if(time.length() == 0){
            timeText.setTextColor(Color.RED);
        }
        else{
            timeText.setTextColor(Color.BLACK);
        }

        if(radius.length() == 0){
            radiusText.setTextColor(Color.RED);
        }
        else{
            radiusText.setTextColor(Color.BLACK);
        }

        if((time.length() != 0) && (radius.length() != 0)){ //Then we are good to go
            finish();
            Intent intent = new Intent(this, GPSActivity.class);
            intent.putExtra("time", Integer.parseInt((time.getText()).toString())); //send time to GPS activity
            intent.putExtra("radius", Integer.parseInt((radius.getText()).toString())); //send radius to GPS activity
            startActivity(intent);
        }
    }

}
