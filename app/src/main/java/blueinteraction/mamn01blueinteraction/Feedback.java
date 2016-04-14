package blueinteraction.mamn01blueinteraction;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Adrian on 2016-04-14.
 */
public class Feedback extends AppCompatActivity{

    private double time, timeCalm, timeNotCalm, timeStressedOut, timeIntense; //time since last checkpoint was placed
    private double distanceTo;
    private MediaPlayer mp;
    private double time_start;

    public Feedback(){
        // --- millis (test values) ---
        timeCalm = 10000;
        timeNotCalm = 15000;
        timeStressedOut = 20000;
        timeIntense = 25000;
        time_start = System.currentTimeMillis();
    }

    public void setTime(double time){
        this.time = time;
    }

    private double getElapsedTime(){
        this.time = (System.currentTimeMillis() - time_start);
        return time;
    }

    public void setDistanceTo(double distanceTo){
        this.distanceTo = distanceTo;
    }

    public void mediaLoop() {
       //while (true) {
           time = getElapsedTime();
           //när den utkommenterade koden nedan är med kraschar appen, tror det är något med parametern "this"
           //för att koden funkar om jag lägger in den i testFeedback-metoden i MainActivity.java
           if (time < timeCalm) {
            //mp = MediaPlayer.create(this, R.raw.calm);
            //mp.setLooping(true);
            //mp.start();
          } else if (time < timeNotCalm) {
            // mp.setLooping(false);
            // mp = MediaPlayer.create(this, R.raw.notcalm);
            // mp.setLooping(true);
            // mp.start();
            } else if (time < timeStressedOut) {
            //  mp.setLooping(false);
            // mp = MediaPlayer.create(this, R.raw.stressedout);
            // mp.setLooping(true);
            // mp.start();
            } else if (time < timeIntense) {
            //  mp.setLooping(false);
            // mp = MediaPlayer.create(this, R.raw.intense);
            // mp.setLooping(true);
            // mp.start();
            }

            //Add more ifs about proximity? How close to checkpoint??
            //After certain distance, scale volume of looping flag sound from 0% to 100%
        //}
    }
}