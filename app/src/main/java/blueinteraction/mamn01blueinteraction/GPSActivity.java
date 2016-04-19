package blueinteraction.mamn01blueinteraction;

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
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Random;

public class GPSActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,SensorEventListener {

    //COMPASS
    private SensorManager mSensorManager;
    private Sensor mRotation;
    private float mPrevDegree = 0f;
    private TextView tvHeading, direction, textAngleToLocation, latitude_check_textview, longitude_check_textview;
    private ImageView mCompass;
    private double minAngle = 10;
    //END COMPASS

    //GAME
    Game game;
    Feedback feedback;
    Location checkpointLocation, ourLocation, startLocation;
    ArrayList<Location> oldCheckpoints;
    //END GAME

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private String mLastUpdateTime;
    private TextView mLatitudeTextView;
    private TextView mLongitudeTextView;
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGoodToGo = false;
    public double latitude, longitude = 50;

    protected static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        initViews();

        //GPS----------------------------------
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
        //END GAME

    }

    public void initViews(){
        mLatitudeTextView = (TextView) findViewById((R.id.latitude_textview));
        mLongitudeTextView = (TextView) findViewById((R.id.longitude_textview));
        longitude_check_textview = (TextView) findViewById((R.id.longitude_check_textview));
        latitude_check_textview = (TextView) findViewById((R.id.latitude_check_textview));

        //COMPASS
        textAngleToLocation = (TextView) findViewById(R.id.textAngleToLocation);
        tvHeading = (TextView) findViewById(R.id.headingText);
        direction = (TextView) findViewById(R.id.direction);
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
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mRotation);
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(3000);
        if ( ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        else{
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
        mLatitudeTextView.setText(String.valueOf(location.getLatitude()));
        mLongitudeTextView.setText(String.valueOf(location.getLongitude()));
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        //ourLocation = location;

        ourLocation.setLatitude(location.getLatitude());
        ourLocation.setLongitude(location.getLongitude());
        if(startLocation == null){
            startLocation = ourLocation;
            checkpointLocation = createCheckpoint();

        }
        latitude_check_textview.setText(String.valueOf(checkpointLocation.getLatitude()));
        longitude_check_textview.setText(String.valueOf(checkpointLocation.getLongitude()));
        Toast.makeText(this, "Distance: " + (int)ourLocation.distanceTo(checkpointLocation) +"m", Toast.LENGTH_LONG).show();//show checkpoint

        Float distance = ourLocation.distanceTo(checkpointLocation);
        if(distance < Constants.GAME_CHECKPOINT_MINDISTANCE_METERS){
            Toast.makeText(this, "Checkpoint reached", Toast.LENGTH_LONG).show();
            sendNotification("Checkpoint reached!");
            oldCheckpoints.add(checkpointLocation);
            checkpointLocation = createCheckpoint();
        }

        //TEST!!!
        //feedback.mediaCheck( (System.currentTimeMillis()) - (game.getTimeStart()) ); //temporary solution for time-based sound feedback

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
            direction.setText(getDirection(nbr));
            tvHeading.setText(str + "°");

            if((ourLocation != null) && (checkpointLocation != null)) {
                double angle = Math.abs(angelToLocation(ourLocation) - nbr);
                if (angle > 360) {
                    angle = angle - 360;
                }

                textAngleToLocation.setText(Double.toString((int) angle));

                //CHECK ANGLE TO LOCATION
                if ((angle < minAngle) || (angle > (360 - minAngle))) {
                    Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                }
            }

            //----ANIMATION FOR ImageView-----
            RotateAnimation ra = new RotateAnimation(mPrevDegree, -azimuthInDegress, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            ra.setDuration(100);
            ra.setFillAfter(true);
            mCompass.startAnimation(ra);
            mPrevDegree = -azimuthInDegress;
            //String location = lm.getLastKnownLocation();
        }
    }

    public double angelToLocation(Location ourLocation){
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

    public Location createCheckpoint(){
        Random rand = new Random();

        //Calculate random distance and angle from game center
        double distance = Constants.GAME_RADIUS * rand.nextDouble();
        double angleDeg = 360 * rand.nextDouble();

        //Trig, calculating long and lat distances from game center
        double opposite = Math.sin(angleDeg)*distance;
        double adjacent = Math.cos(angleDeg)*distance;

        //Calculating absolute position of checkpoint and setting it
        if(checkpointLocation != null) {
            oldCheckpoints.add(checkpointLocation);
        }
        Location l = new Location("");
        l.setLatitude(startLocation.getLatitude()+adjacent);
        l.setLongitude(startLocation.getLongitude()+opposite);
       // Toast.makeText(this, "Lat: "+l.getLatitude()+", Long: "+l.getLongitude(), Toast.LENGTH_LONG).show();//show checkpoint
        return l;
    }

    public String getDirection(int nbr){
        if((nbr<30) || (nbr>=330)){
            return ("North");
        }
        else if((nbr>=30) && (nbr<60)){
            return ("Northeast");
        }
        else if((nbr>=60) && (nbr<120)){
            return ("East");
        }
        else if((nbr>=120) && (nbr<150)){
            return ("Southeast");
        }
        else if((nbr>=150) && (nbr<210)){
            return ("South");
        }
        else if((nbr>=210) && (nbr<240)){
            return ("Southwest");
        }
        else if((nbr>=240) && (nbr<300)){
            return ("West");
        }
        else if((nbr>=300) && (nbr<330)){
            return ("Northwest");
        }
        return "";
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
}