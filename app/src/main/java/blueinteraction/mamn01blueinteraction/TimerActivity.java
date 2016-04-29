package blueinteraction.mamn01blueinteraction;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class TimerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final TextView mTextField = (TextView) findViewById(R.id.timer);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new CountDownTimer(30000,1000){
            public void onTick(long millisUntilFinished){
                mTextField.setText("seconds remaining: "+ millisUntilFinished /1000);
            }
            public void onFinish(){
                mTextField.setText("Done!");
                highScore();
            }
        }.start();
    }

    public void highScore(){
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

}
