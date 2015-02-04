package net.fhtagn.orientation.orientation.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import net.fhtagn.orientation.orientation.math.Mat3;
import net.fhtagn.orientation.orientation.math.Quaternion;

import java.text.DecimalFormat;

// Orientation tracker using magnetic field and accelerometer sensor
public class MagnAccelOrientationTracker extends OrientationTracker {
  private static final DecimalFormat decimalFormat = new DecimalFormat(" * 0.00;-#");
  
  private Sensor magneticSensor;
  private Sensor accelSensor;
  private Sensor orienSensor;
  
  private String debugText = "";
  
  private float[] accelValues = null;
  private float[] magnValues = null;
  
  private float[] orienValues = null;
  
  public MagnAccelOrientationTracker(SensorManager sm) {
    magneticSensor = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    accelSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    orienSensor = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION);
  }

  @Override
  public String getDebugText() {
    return debugText;
  }

  @Override
  public void reset() {}

  @Override
  public void onResume(SensorManager sm) {
    sm.registerListener(this, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
    sm.registerListener(this, accelSensor, SensorManager.SENSOR_DELAY_GAME);
    sm.registerListener(this, orienSensor, SensorManager.SENSOR_DELAY_GAME);
  }

  @Override
  public void onPause(SensorManager sm) {
    sm.unregisterListener(this);
  }

  private void update(Mat3 device2World) {
    setOrientation(Quaternion.fromMatrix(device2World));
  }
  
  public void onSensorChanged(SensorEvent event) {
    switch(event.sensor.getType()) {
      case Sensor.TYPE_ACCELEROMETER:
        accelValues = event.values.clone();
        break;
      case Sensor.TYPE_MAGNETIC_FIELD:
        magnValues = event.values.clone();
        break;
      case Sensor.TYPE_ORIENTATION:
        orienValues = event.values.clone();
        break;
    }
    
    // TODO: Angles seems quite noisy, but somehow correct. Maybe
    // We should average (this will reduce dynamic response, but when
    // you take a picture, you're gonna hold the camera still anyway)
    
    if (accelValues != null && magnValues != null && orienValues != null) {
      float[] R = new float[9];
      SensorManager.getRotationMatrix(R, null, accelValues, magnValues);
      synchronized(this) {
        update(Mat3.fromRowMajorArray(R));
      }
      
      float[] angles = new float[3];
      SensorManager.getOrientation(R, angles);
      debugText = "AccelMagn\n";
      debugText += "azimuth: " + decimalFormat.format((float)Math.toDegrees(angles[0])) + "\n";
      debugText += "pitch: " + decimalFormat.format((float)Math.toDegrees(angles[1])) + "\n";
      debugText += "roll: " + decimalFormat.format((float)Math.toDegrees(angles[2])) + "\n";
      
      debugText += "rot : \n" + getOrientation().toMatrix();
      
      debugText += "\nOrien\n";
      debugText += "azimuth: " + decimalFormat.format(orienValues[0]) + "\n";
      debugText += "pitch: " + decimalFormat.format(orienValues[1]) + "\n";
      debugText += "roll: " + decimalFormat.format(orienValues[2]) + "\n";
    }
  }
}
