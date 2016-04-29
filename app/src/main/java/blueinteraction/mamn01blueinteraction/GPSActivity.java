package blueinteraction.mamn01blueinteraction;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class GPSActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, SensorEventListener {

    //VIEWS
    //private TextView tvHeading, direction, textAngleToLocation, latitude_check_textview, longitude_check_textview, time_elasped;
    private TextView time_elasped;
    private ImageView mCompass;
    //private TextView mLatitudeTextView;
    //private TextView mLongitudeTextView;

    //SENSORS AND API
    private SensorManager mSensorManager;
    private Sensor mRotation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    GoogleMap mMap;

    //COMPASS
    private double minAngle = 10;
    private float mPrevDegree = 0f;
    Vibrator v;

    //TIME
    private Long timeStart, checkpointSpawnTime;
    boolean soundLvl1 = false;
    boolean soundLvl2 = false;

    //FEEDBACK
    Feedback feedback;
    MediaPlayer mp;
    boolean isVibrating = false;

    //GAME VARIABLES
    private int game_time;
    private int game_radius;

    ArrayList<Location> oldCheckpoints;


    private boolean locationPermissionGoodToGo = false;

    //GPS AND LOCATION
    public double latitude, longitude = 50;
    Location checkpointLocation, checkpointSpawnPlayerLocation, ourLocation, startLocation;

    private String mLastUpdateTime;

    //SHARED PREFERENCES
    protected static final String TAG = "MainActivity";
    SharedPreferences highscore;
    SharedPreferences.Editor editor;
    Set<String> highscoreSet;

    private CountDownTimer timer;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        //MAPS-----------------------------------------------------------------------------------
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //END MAPS-----------------------------------------------------------------------------------

        initViews();

        //GET PUT EXTRAS--------------------------
        Intent intent = getIntent();
        game_time = intent.getIntExtra("time", 20); //fetch the game_time extra
        game_radius = intent.getIntExtra("radius", 2); //Fetch the game_radius extra

        //GPS------------------------------------
        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();

        //SENSORS----------------------------------
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

        ourLocation = new Location("");
        ourLocation.setLatitude(0);
        ourLocation.setLongitude(0); //switch place on long and lat?

        //GAME----------------------------------
        oldCheckpoints = new ArrayList<Location>();
        feedback = new Feedback(this);
        resetTimer(); //I think :P
        mp = new MediaPlayer();
        //END GAME

        //Highscore
        highscore = this.getSharedPreferences("CheckPointHighScore", Context.MODE_PRIVATE);
        editor = highscore.edit();
        highscoreSet = new HashSet<>();
        score =0;

        //Timer
        time_elasped = (TextView) findViewById(R.id.time_elasped);
        timer = new CountDownTimer(game_time *60*1000,1000){
        //timer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                time_elasped.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                time_elasped.setText("Score: " + score);
                highscoreSet.add(Integer.toString(score) + " " + "          Tobias");
                editor.putStringSet("Highscore", highscoreSet);
                editor.commit();
                highScore();
            }
        }.start();

    }

    public void highScore(){
        Intent intent = new Intent(this, HighScoreActivity.class);
        startActivity(intent);
    }

    public void initViews() {
//        mLatitudeTextView = (TextView) findViewById((R.id.latitude_textview));
//        mLongitudeTextView = (TextView) findViewById((R.id.longitude_textview));
//        longitude_check_textview = (TextView) findViewById((R.id.longitude_check_textview))
//       latitude_check_textview = (TextView) findViewById((R.id.latitude_check_textview));
        time_elasped = (TextView) findViewById((R.id.time_elasped));

        //COMPASS
//        textAngleToLocation = (TextView) findViewById(R.id.textAngleToLocation);
//        tvHeading = (TextView) findViewById(R.id.headingText);
//        direction = (TextView) findViewById(R.id.direction);
        mCompass = (ImageView) findViewById(R.id.compassImg);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mRotation, SensorManager.SENSOR_DELAY_UI);
        //mp.start();
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mRotation);
        if (v != null) {
            v.cancel();
        }
        //mp.stop();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection Suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        //mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //ourLocation = location;
        //score += 1;
        ourLocation.setLatitude(location.getLatitude());
        ourLocation.setLongitude(location.getLongitude());
        if (startLocation == null) {
            startLocation = ourLocation;
            LatLng startLatLng = new LatLng(startLocation.getLatitude(), startLocation.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startLatLng,17.0f));
            mMap.addCircle((new CircleOptions()).center(startLatLng).radius(game_radius*1000));
            checkpointSpawnPlayerLocation = ourLocation;
            checkpointSpawnTime = System.currentTimeMillis();

            //HARDCODED HIGHSCORE STUFF
            highscoreSet.add("123" + " " + "          20/4 16:00:04");
            highscoreSet.add("789" + " " + "          20/4 16:30:04");
            highscoreSet.add("1234" + " " + "          20/4 16:45:45");
            highscoreSet.add("567" + " " + "          20/4 15:54:13");
            highscoreSet.add("456" + " " + "          20/4 17:14:12");
            editor.putStringSet("Highscore", highscoreSet);
            editor.commit();
            checkpointLocation = newCheckpoint();

        }
        //latitude_check_textview.setText(String.valueOf(checkpointLocation.getLatitude()));
        //longitude_check_textview.setText(String.valueOf(checkpointLocation.getLongitude()));
        Toast.makeText(this, "Distance: " + (int) ourLocation.distanceTo(checkpointLocation) + "m", Toast.LENGTH_LONG).show();//show checkpoint

        Float distance = ourLocation.distanceTo(checkpointLocation);
        if (distance < Constants.GAME_CHECKPOINT_MINDISTANCE_METERS) {
            Toast.makeText(this, "Checkpoint reached", Toast.LENGTH_LONG).show();
            sendNotification("Checkpoint reached!");
            mp.stop();
            mp = MediaPlayer.create(this, R.raw.alarm);
            mp.start();
            //oldCheckpoints.add(checkpointLocation); //This is done inside the newCheckpoint() method, just before returning the value and overwriting checkpointLocation
            checkpointLocation = newCheckpoint();
        }

        //SOUND
        checkSoundEffects();

//        float volume = calculateVolumePercentage(ourLocation.distanceTo(checkpointLocation));
//        mp.setVolume(volume, volume);
//        Toast.makeText(this, "Vol: " + String.valueOf(volume), Toast.LENGTH_SHORT);
        //END SOUND
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed. Error: " + connectionResult.getErrorCode());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    locationPermissionGoodToGo = true;
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                    //setCoordinates();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    locationPermissionGoodToGo = false;
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        //----TYPE_ROTATION_VECTOR--------------------------------------------
        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            float[] r = new float[9];
            float[] mOrientation2 = new float[3];
            SensorManager.getRotationMatrixFromVector(r, event.values);
            SensorManager.getOrientation(r, mOrientation2);
            float azimuthInDegress = (float) (Math.toDegrees(mOrientation2[0]) + 360) % 360; //idea: (int)
            int nbr = Math.round(azimuthInDegress);
            String str = Integer.toString(nbr);
            //direction.setText(getDirection(nbr));
            //tvHeading.setText(str + "°");

            if ((ourLocation != null) && (checkpointLocation != null)) {
                double angle = Math.abs(angelToLocation(ourLocation) - nbr);
                if (angle > 360) {
                    angle = angle - 360;
                }

//                textAngleToLocation.setText(Double.toString((int) angle));

                //CHECK ANGLE TO LOCATION
                if ((angle < minAngle) || (angle > (360 - minAngle))) {
                    if (!isVibrating) { //if it's not already vibrating
                        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                        v.cancel();
                        v.vibrate(Constants.VIBRATION_PATTERN, 0);
                        isVibrating = true;
                    }
                } else {
                    isVibrating = false;
                    v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    v.cancel();
                }
            }

            //----ANIMATION FOR ImageView-----
            RotateAnimation ra = new RotateAnimation(mPrevDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(100);
            ra.setFillAfter(true);
            mCompass.startAnimation(ra);
            mPrevDegree = -azimuthInDegress;
        }
    }

    public double angelToLocation(Location ourLocation) {
        return ourLocation.bearingTo(checkpointLocation);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    //SOURCE: http://stackoverflow.com/questions/153724/how-to-round-a-number-to-n-decimal-places-in-java
    public String round(float nbr) {
        String nbr2 = Float.toString(nbr);
        Double nbr3 = Double.valueOf(nbr2);
        return Double.toString(Math.round(nbr3 * 100.0) / 100.0);
    }

    /*
     * Creates a new checkpoint and saves away the data of the old one
     */
    public Location newCheckpoint() {
        if (checkpointLocation != null) {
            score += calculatePoints(checkpointSpawnPlayerLocation.distanceTo(checkpointLocation), System.currentTimeMillis() - checkpointSpawnTime); //calculate points for taking prev checkpoint (if it exists)
            mMap.addMarker(new MarkerOptions().position(new LatLng(checkpointLocation.getLatitude(), checkpointLocation.getLongitude())).title((Integer.toString(oldCheckpoints.size()+1))));
            //TODO: ^actually add that to the database?
        }
        checkpointSpawnPlayerLocation = ourLocation; //where we are when the checkpoint spawns
        checkpointSpawnTime = System.currentTimeMillis(); //what game_time it is when the checkpoint spawns

        Random rand = new Random();
        Location l = new Location("");

        while (true) { //loop for infinity, will break it from within if new checkpoint is okay
            //Calculate random distance and angle from game center
            double distance = (game_radius * 0.008983) * rand.nextDouble(); //random game_radius from start location. PRev: Constants.GAME_RADIUS
            double angleDeg = 360 * rand.nextDouble(); //random angle from start location
            //Trig, calculating long and lat distances from game center
            double opposite = Math.sin(angleDeg) * distance;
            double adjacent = Math.cos(angleDeg) * distance;
            //Calculating absolute position of checkpoint and setting it
            l.setLatitude(startLocation.getLatitude() + adjacent);
            l.setLongitude(startLocation.getLongitude() + opposite);

            //if checkpoints spawn within permitted distance from currentLocation AND they don't spawn inside us, we break the loop. Otherwise, keep spinnin
            if ((l.distanceTo(ourLocation) <= Constants.GAME_CHECKPOINT_MAX_SPAWN_DISTANCE) && (l.distanceTo(ourLocation) > Constants.GAME_CHECKPOINT_MINDISTANCE_METERS)) {
                break;//we are satisfied with distance between new checkpoint and current player location
            }
        }

        resetTimer(); // sets game_time of checkpoint spawn to current game_time
        //calculatePoints(400, timeStart);
        if (checkpointLocation != null) { //TODO: should we even have this?
            oldCheckpoints.add(checkpointLocation);
        }

        return l;
    }

    public int calculatePoints(double distance, long time) {
        return (int) (10 * distance / Long.valueOf(time).doubleValue()); //Points = 10*distance/game_time in seconds
    }

    public void resetTimer() {
        timeStart = System.currentTimeMillis();
    }

    public long getElapsedTime() {
        long timeMillis = System.currentTimeMillis() - timeStart;
        // time_elasped.setText(Long.toString(timeMillis/1000)+" seconds");
        return timeMillis;
    }

    public void mediaCheck(long timeElapsed) { //game_time comes from GPSActivity class, which in turn gets it from the Game-class. Kolla i "onLocationChanged"-metoden
        //Toast.makeText(this, Long.toString(timeElapsed), Toast.LENGTH_SHORT).show();

        if (timeElapsed < Constants.SOUND_TIME_LVL_2) {
            //Toast.makeText(this, "SOUND TIME LVL 1", Toast.LENGTH_SHORT).show();
            if (!soundLvl1) {
                soundLvl1 = true;
                soundLvl2 = false;
//                mp.setLooping(false);
//                mp.stop();
//                mp = MediaPlayer.create(this, R.raw.shortpiano1);
//                mp.setLooping(true);
//                mp.start();
            }
        } else if ((timeElapsed > Constants.SOUND_TIME_LVL_2)) {
            //playRandomSound(); //plays silly random noises.. but it was annoying so it's commented out :P
            if (!soundLvl2) {
                soundLvl1 = false;
                soundLvl2 = true;
                //THE STUFF BELOW WAS BUGGY SO IT'S COMMENTED OUT FOR NOW
//                mp.setLooping(false);
//                mp.stop();
//                mp = MediaPlayer.create(this, R.raw.epicsong);
//                mp.setLooping(true);
//                mp.start();
            }
            //Toast.makeText(this, "SOUND TIME LVL 2", Toast.LENGTH_SHORT).show();
        }
//        else if (timeElapsed > 30000){
//            soundLvl1 = false;
//            soundLvl2 = false;
//            timeStart = System.currentTimeMillis();
//        }
        //playRandomSound();
    }

    public void initCheckpointLoopSound() {
        mp = MediaPlayer.create(this, R.raw.shortpiano1);
        mp.setLooping(true);
        mp.start();
        mp.setVolume(0, 0);
    }

//    public void playRandomSound(){
//        double chance = (new Random()).nextDouble();
//        if(chance > 0.5){ //then play a random sound
//            int nbr = (new Random()).nextInt(5);
//            if(nbr == 0){
//                mp = MediaPlayer.create(this, R.raw.rage);
//                mp.start();
//            }
//            if(nbr == 1){
//                mp = MediaPlayer.create(this, R.raw.i_see_you_voice);
//                mp.start();
//            }
//            if(nbr == 2){
//                mp = MediaPlayer.create(this, R.raw.scream_horror1);
//                mp.start();
//            }
//            if(nbr == 3){
//                mp = MediaPlayer.create(this, R.raw.zombie_pain);
//                mp.start();
//            }
//            if(nbr == 4){
//                mp = MediaPlayer.create(this, R.raw.manhurt);
//                mp.start();
//            }
//
//        }
//    }

    public void checkSoundEffects() {
        //feedback.mediaCheck(getElapsedTime());
        mediaCheck(getElapsedTime());
    }

    public String getDirection(int nbr) {
        if ((nbr < 30) || (nbr >= 330)) {
            return ("North");
        } else if ((nbr >= 30) && (nbr < 60)) {
            return ("Northeast");
        } else if ((nbr >= 60) && (nbr < 120)) {
            return ("East");
        } else if ((nbr >= 120) && (nbr < 150)) {
            return ("Southeast");
        } else if ((nbr >= 150) && (nbr < 210)) {
            return ("South");
        } else if ((nbr >= 210) && (nbr < 240)) {
            return ("Southwest");
        } else if ((nbr >= 240) && (nbr < 300)) {
            return ("West");
        } else if ((nbr >= 300) && (nbr < 330)) {
            return ("Northwest");
        }
        return "";
    }

    public float calculateVolumePercentage(float dist) {
        float temp;
        if (dist < 300) {
            temp = dist - Constants.GAME_CHECKPOINT_MINDISTANCE_METERS;
            if (temp < 0) {
                return 1; //full volume
            } else {
                float volume = 1 - (temp / 200);
                if (volume < 0) {
                    return 0;
                } else {
                    return volume;
                }
            }

        }
        return 0;
    }

    /**
     * Posts a notification in the notification bar when a transition is detected.
     * If the user clicks the notification, control goes to the MainActivity.
     */
    private void sendNotification(String notificationDetails) {
        // Create an explicit content Intent that starts the main Activity.
        Intent notificationIntent = new Intent(getApplicationContext(), GPSActivity.class);

        // Construct a task stack.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        // Add the main Activity to the task stack as the parent.
        stackBuilder.addParentStack(GPSActivity.class);

        // Push the content Intent onto the stack.
        stackBuilder.addNextIntent(notificationIntent);

        // Get a PendingIntent containing the entire back stack.
        PendingIntent notificationPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        // Get a notification builder that's compatible with platform versions >= 4
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

        // Define the notification settings.
        builder.setSmallIcon(R.drawable.ic_launcher)
                // In a real app, you may want to use a library like Volley
                // to decode the Bitmap.
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),
                        R.drawable.ic_launcher))
                .setColor(Color.RED)
                .setContentTitle(notificationDetails)
                .setContentText(getString(R.string.geofence_transition_notification_text))
                .setContentIntent(notificationPendingIntent);

        // Dismiss notification once the user touches it.
        builder.setAutoCancel(true);

        // Get an instance of the Notification manager
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Issue the notification
        mNotificationManager.notify(0, builder.build());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng lund = new LatLng(55.7122, 13.20894);
        LatLng gameStartLc = new LatLng(ourLocation.getLatitude(), ourLocation.getLongitude());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        //mMap.addMarker(new MarkerOptions().position(lund).title("Marker in Lund"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(gameStartLc,17.0f));
    }
}