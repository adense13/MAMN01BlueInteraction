package blueinteraction.mamn01blueinteraction;

import android.location.Location;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by STX Admin on 2016-04-11.
 */
public class Game {

    private Location startLocation, checkpointLocation, currentLocation;
    private double minDistance, radius;
    private ArrayList<Location> checkpointList;
    long timeStart;

    /*
     * Create a new Game object
     * @param startLocation sent by the Activity
     * @param radius, the radius aka size of game field
     */
    public Game(Location startLocation, double radius, double minDistance){
        this.startLocation = startLocation;
        this.radius = radius;
        this.minDistance = minDistance;
        checkpointList = new ArrayList<Location>();
        timeStart = System.currentTimeMillis();
        createCheckpoint();
    }

    /*
     * Create a new checkpoint random distance and angel from startLocation, also put old checkpoint in list to be saved
     */
    public void createCheckpoint(){
        Random rand = new Random();

        //Calculate random distance and angle from game center
        double distance = radius * rand.nextDouble();
        double angleDeg = 360 * rand.nextDouble();

        //Trig, calculating long and lat distances from game center
        double opposite = Math.sin(angleDeg)*distance;
        double adjacent = Math.cos(angleDeg)*distance;

        //Calculating absolute position of checkpoint and setting it
        checkpointList.add(checkpointLocation);
        checkpointLocation = new Location("");
        checkpointLocation.setLatitude(startLocation.getLatitude()+adjacent);
        checkpointLocation.setLatitude(startLocation.getLongitude()+opposite);

        //Set game to running
        //running = true;
    }

    /*
     * Returns true if within minimum distance of checkpoint, returns false otherwise
     */
    public boolean checkIfClose(Location location){
        if((location.distanceTo(checkpointLocation))<minDistance){
            return true;
        }
        return false;
    }

    public Location getCheckpointLocation(){
        return checkpointLocation;
    }

    public void updateLocation(Location currentLocation){
        this.currentLocation = currentLocation;
    }

    /*
     * Return arraylist with saved checkpoints
     */
    public ArrayList<Location> getCheckpointList(){
        return checkpointList;
    }

    public long getTimeStart(){
        return timeStart;
    }
}
