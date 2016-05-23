package blueinteraction.mamn01blueinteraction;

/**
 * Copyright 2014 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;

/**
 * Constants used in this sample.
 */
public final class Constants {

    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.Geofence";
    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES_NAME";
    public static final String GEOFENCES_ADDED_KEY = PACKAGE_NAME + ".GEOFENCES_ADDED_KEY";


    //WE USE THIS
    public static final double GAME_RADIUS = 0.005; //0.01; //1km approx
    public static final double GAME_CHECKPOINT_MINDISTANCE = 0.00035; //0.001; //110m approx
    public static final int GAME_CHECKPOINT_MINDISTANCE_METERS = 100; //original = 100
    public static final int GAME_CHECKPOINT_MAX_SPAWN_DISTANCE = 550; //original = 550
    public static final long SOUND_TIME_LVL_2 = 10000;
    public static final long SOUND_TIME_LVL_3 = 20000;
    public static final long[] VIBRATION_PATTERN = {0, Constants.VIBRATION_LENGTH, 2*Constants.VIBRATION_LENGTH}; //wait 0ms, vibrate 500ms, wait 1000ms, (insert a repeat of "0" into vibrate command to cause repeating vibrate)
    public static final long VIBRATION_LENGTH = 500;
    //END WE USE THIS







    /**
     * Used to set an expiration time for a geofence. After this amount of time Location Services
     * stops tracking the geofence.
     */
    public static final long GEOFENCE_EXPIRATION_IN_HOURS = 12;

    /**
     * For this sample, geofences expire after twelve hours.
     */
    public static final long GEOFENCE_EXPIRATION_IN_MILLISECONDS =
            GEOFENCE_EXPIRATION_IN_HOURS * 60 * 60 * 1000;
    public static final float GEOFENCE_RADIUS_IN_METERS = 100; // 1 mile, 1.6 km

    /**
     * Map for storing information about airports in the San Francisco bay area.
     */
    public static final HashMap<String, LatLng> BAY_AREA_LANDMARKS = new HashMap<String, LatLng>();
    static {
        // San Francisco International Airport.
        BAY_AREA_LANDMARKS.put("KH", new LatLng(55.7124056, 13.2090622));

        // Googleplex.
        BAY_AREA_LANDMARKS.put("MA", new LatLng(55.711246, 13.207704));

        //Vildanden
        BAY_AREA_LANDMARKS.put("Vildis", new LatLng(55.710694, 13.169511));

        //Tobbz
        BAY_AREA_LANDMARKS.put("Tobbes hus", new LatLng(55.718162, 13.198172));

        //IKDC
        BAY_AREA_LANDMARKS.put("IKDC", new LatLng(55.715098, 13.212961));

        //E HOUSE
        BAY_AREA_LANDMARKS.put("E HOUSE", new LatLng(55.711040, 13.210316));
    }
}