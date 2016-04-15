package blueinteraction.mamn01blueinteraction;

import android.content.Context;
import android.content.ContextWrapper;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Adrian on 2016-04-14.
 */
public class Feedback extends ContextWrapper{

    Context base;
    private long time, timeCalm, timeNotCalm, timeStressedOut, timeIntense; //time since last checkpoint was placed
    private double distanceTo;
    private MediaPlayer mp;
    private long time_start;

    public Feedback(Context base){
        // --- millis (test values) ---
        super(base);
        //this.base = base;
        timeCalm = 10000;
        timeNotCalm = 15000;
        timeStressedOut = 20000;
        timeIntense = 25000;
        time_start = System.currentTimeMillis();
    }

    public void setTime(long time){ //not used anymore
        this.time = time;
    }

    private double getElapsedTime(){ //not used anymore
        this.time = (System.currentTimeMillis() - time_start);
        return time;
    }

    public void setDistanceTo(double distanceTo){ //not used anymore
        this.distanceTo = distanceTo;
    }

    public void mediaCheck(long time) { //time comes from GPSActivity class, which in turn gets it from the Game-class. Kolla i "onLocationChanged"-metoden
        //while (true) {
        //time = getElapsedTime();
        //när den utkommenterade koden nedan är med kraschar appen, tror det är något med parametern "this"
        //för att koden funkar om jag lägger in den i testFeedback-metoden i MainActivity.java
        if (time < timeCalm) {
            mp = MediaPlayer.create(this, R.raw.calm);
            mp.setLooping(true);
            mp.start();
        } else if (time < timeNotCalm) {
            mp.setLooping(false);
            mp = MediaPlayer.create(this, R.raw.notcalm);
            mp.setLooping(true);
            mp.start();
        } else if (time < timeStressedOut) {
            mp.setLooping(false);
            mp = MediaPlayer.create(this, R.raw.stressedout);
            mp.setLooping(true);
            mp.start();
        } else if (time < timeIntense) {
            mp.setLooping(false);
            mp = MediaPlayer.create(this, R.raw.intense);
            mp.setLooping(true);
            mp.start();
        }

            //Add more ifs about proximity? How close to checkpoint??
            //After certain distance, scale volume of looping flag sound from 0% to 100%
        //}
    }
}