package blueinteraction.mamn01blueinteraction;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickToGPS(View view){
            Intent intent = new Intent(this, GPSActivity.class);
            startActivity(intent);
    }

    public void testVibration(View view){
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(500);
    }

    public void testSound(View view){
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.sound1);
        mp.start();
    }

    public void testCompass(View view){
        Intent intent = new Intent(this, CompassActivity.class);
        startActivity(intent);
    }

    public void testFeedback(View view){
        //Feedback fb = new Feedback();
        //fb.mediaLoop();
    }

    public void highScore(View view){
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }
}
