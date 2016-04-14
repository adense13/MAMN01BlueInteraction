package blueinteraction.mamn01blueinteraction;

/**
 * Created by Adrian on 2016-04-14.
 */
public class Feedback {

    private double time, timeCalm, timeNotCalm, timeStressedOut, timeIntense, timeCrazy; //time since last checkpoint was placed
    private double distanceTo;

    public Feedback() {
        mediaLoop();
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setDistanceTo(double distanceTo){
        this.distanceTo = distanceTo;
    }

    public void mediaLoop() {
        while (true) {
            if (time < timeCalm) {
                //mediaplayer play loop and interrupt previous loop if in new if

            } else if (time < timeCalm) {

            } else if (time < timeNotCalm) {


            } else if (time < timeStressedOut) {


            } else if (time < timeIntense) {


            } else if (time < timeCrazy) {


            }

            //Add more ifs about proximity? How close to checkpoint??
            //After certain distance, scale volume of looping flag sound from 0% to 100%
        }
    }
}