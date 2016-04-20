package blueinteraction.mamn01blueinteraction;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.widget.Toast;

/**
 * Created by Adrian on 2016-04-14.
 */
public class Feedback extends ContextWrapper{

    Context base;
    private MediaPlayer mp;
    private boolean onLvl1, onLvl2;

    public Feedback(Context base){
        super(base);
        onLvl1 = false;
        onLvl2 = false;
    }

    public void mediaCheck(long timeElapsed) { //time comes from GPSActivity class, which in turn gets it from the Game-class. Kolla i "onLocationChanged"-metoden
        Toast.makeText(this, Long.toString(timeElapsed), Toast.LENGTH_SHORT).show();

        if (timeElapsed < Constants.SOUND_TIME_LVL_2) {
            if(!onLvl1){
                onLvl1 = true;
                onLvl2 = false;
                mp = MediaPlayer.create(this, R.raw.shortpiano1);
                mp.setLooping(true);
                mp.start();
            }
        } else if (timeElapsed > Constants.SOUND_TIME_LVL_2){ //(timeElapsed < Constants.SOUND_TIME_LVL_3) {
            if(!onLvl2) {
                onLvl2 = true;
                onLvl1 = false;
                mp.setLooping(false);
                //mp = MediaPlayer.create(this, R.raw.epicsong);
                //mp.setLooping(true);
                //mp.start();
            }
        }
    }
}