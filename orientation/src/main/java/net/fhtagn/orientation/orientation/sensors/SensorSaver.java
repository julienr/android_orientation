package net.fhtagn.orientation.orientation.sensors;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

// Track accel, gyro, magn and write raw measurements to file
public class SensorSaver extends Fragment implements SensorEventListener {
  private final static String TAG = "SensorSaver";
  private static final int TWO_MINUTES = 1000 * 60 * 2;
  private List<Sensor> sensors = new ArrayList<Sensor>();
  
  private LocationManager locationManager;
  private LocationListener locationListener;
  private Location currentLocation;
  
  private SensorManager sensorManager;

  private BufferedWriter accelWriter, gyroWriter, magnWriter, locWriter;
  private Activity activity;
  
  // Factory method for sensor tracker
  public static SensorSaver newInstance(String outdir) {
    SensorSaver s = new SensorSaver();
    Bundle args = new Bundle();
    args.putString("outdir", outdir);
    s.setArguments(args);
    return s;
  }
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    Log.i(TAG, "onAttach");
    this.activity = activity;
    sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
    
    sensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
    sensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    sensors.add(sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    
    locationManager = (LocationManager)activity.getSystemService(
        Context.LOCATION_SERVICE);
    
    locationListener = new LocationListener() {
      //@Override
      public void onLocationChanged(Location location) {
        locationFound(location);
      }

      //@Override
      public void onProviderDisabled(String provider) {}

      //@Override
      public void onProviderEnabled(String provider) {}

      //@Override
      public void onStatusChanged(String provider, int status, Bundle extras) {}
    };
    
    final String outdir = getArguments().getString("outdir");
    try {
      this.accelWriter = new BufferedWriter(new FileWriter(new File(outdir, "accel.txt")));
      this.gyroWriter = new BufferedWriter(new FileWriter(new File(outdir, "gyro.txt")));
      this.magnWriter = new BufferedWriter(new FileWriter(new File(outdir, "magn.txt")));
      this.locWriter = new BufferedWriter(new FileWriter(new File(outdir, "location.txt")));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private synchronized void locationFound(Location location) {
    if (isBetterLocation(location, currentLocation)) {
      currentLocation = location;
      
      Log.i(TAG, "new location : lat= " + currentLocation.getLatitude() + ", lng=" + currentLocation.getLongitude());
      Log.i(TAG, "height : " + currentLocation.getAltitude());
      Log.i(TAG, "precision : " + currentLocation.getAccuracy());
      
      // Accuracy is in meter.. 20km is kind of arbitrary, but this location
      // is used for geomagnetic field estimation
      if (currentLocation.getAccuracy() < 20000) {
        Log.i(TAG, "Precise location found, stopping updates");
        // TODO: Should we continue with periodic updates every 30 seconds or so ?
        // (So if we get one bad measurement, we can average)
        try {
          locWriter.write("timestamp\tlatitude\tlongitude\taltitude\n");
          locWriter.write(String.valueOf(location.getTime()) + "\t" + location.getLatitude()
              + "\t" + location.getLongitude() + "\t" + location.getAltitude());
          locWriter.flush();
        } catch (IOException e) {
          e.printStackTrace();
        }
        locationManager.removeUpdates(locationListener);
      }
    }
  }
  
  @Override
  public void onDestroy() {
    super.onDestroy();
    try {
      accelWriter.close();
      gyroWriter.close();
      magnWriter.close();
      locWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  @Override
  public void onPause() {
    super.onPause();
    sensorManager.unregisterListener(this);
    locationManager.removeUpdates(locationListener);
  }
  
  @Override
  public void onResume() {
    super.onResume();
    Log.i(TAG, "Sensor saver resuming");

    for (Sensor s : sensors) {
      sensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_GAME);
    }
    if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
      Log.i(TAG, "Network provider enabled");
      locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
    } else {
      Log.i(TAG, "No network provider");
    }
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
  }
 
  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {}
  
  private static void writeTabSeparatedValues(BufferedWriter wr,
                                              long time,
                                              float[] vals) {
    try {
      wr.write(String.valueOf(time) + "\t");
      for (int i = 0; i < vals.length; ++i) {
        wr.write(String.valueOf(vals[i]));
        if (i < vals.length - 1) {
          wr.write("\t");
        }
      }
      wr.write("\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    // SensorEvent.timestamp is time since boot, NOT since epoch => convert
    // to milliseconds since epoch
    // http://stackoverflow.com/questions/5500765/accelerometer-sensorevent-timestamp
    final long timeInMillis = (new Date()).getTime() 
        + (event.timestamp - System.nanoTime()) / 1000000L;
    
    switch(event.sensor.getType()) {
      case Sensor.TYPE_ACCELEROMETER:
        writeTabSeparatedValues(accelWriter, timeInMillis, event.values);
        break;
      case Sensor.TYPE_MAGNETIC_FIELD:
        writeTabSeparatedValues(magnWriter, timeInMillis, event.values);
        break;
      case Sensor.TYPE_GYROSCOPE: {
        writeTabSeparatedValues(gyroWriter, timeInMillis, event.values);
        break;
      }
    }
  }
  
  // http://developer.android.com/guide/topics/location/obtaining-user-location.html
  /** Determines whether one Location reading is better than the current Location fix
   * @param location  The new Location that you want to evaluate
   * @param currentBestLocation  The current Location fix, to which you want to compare the new one
   */
  protected static boolean isBetterLocation(Location location,
      Location currentBestLocation) {
    if (currentBestLocation == null) {
      // A new location is always better than no location
      return true;
    }

    // Check whether the new location fix is newer or older
    long timeDelta = location.getTime() - currentBestLocation.getTime();
    boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean isNewer = timeDelta > 0;

    // If it's been more than two minutes since the current location, use the
    // new location
    // because the user has likely moved
    if (isSignificantlyNewer) {
      return true;
      // If the new location is more than two minutes older, it must be worse
    } else if (isSignificantlyOlder) {
      return false;
    }

    // Check whether the new location fix is more or less accurate
    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation
        .getAccuracy());
    boolean isLessAccurate = accuracyDelta > 0;
    boolean isMoreAccurate = accuracyDelta < 0;
    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

    // Check if the old and new location are from the same provider
    boolean isFromSameProvider = isSameProvider(location.getProvider(),
        currentBestLocation.getProvider());

    // Determine location quality using a combination of timeliness and accuracy
    if (isMoreAccurate) {
      return true;
    } else if (isNewer && !isLessAccurate) {
      return true;
    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
      return true;
    }
    return false;
  }

  /** Checks whether two providers are the same */
  private static boolean isSameProvider(String provider1, String provider2) {
    if (provider1 == null) {
      return provider2 == null;
    }
    return provider1.equals(provider2);
  }
}
