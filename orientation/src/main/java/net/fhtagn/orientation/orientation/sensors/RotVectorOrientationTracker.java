package net.fhtagn.orientation.orientation.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import net.fhtagn.orientation.orientation.math.IO;
import net.fhtagn.orientation.orientation.math.Mat3;
import net.fhtagn.orientation.orientation.math.Quaternion;

// An orientation tracker that uses Android's ROTATION_VECTOR sensor which should rely on
// sensor fusion
public class RotVectorOrientationTracker extends OrientationTracker {
  private Sensor rotVectorSensor;
  private String debugText = "";
  
  public RotVectorOrientationTracker(SensorManager sm) {
    rotVectorSensor = sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
  }
  
  public void reset() {}
  
  public String getDebugText() {
    return debugText;
  }
  
  // Must be call in owner Activity.onResume()
  @Override
  public void onResume(SensorManager sm) {
    sm.registerListener(this, rotVectorSensor,
        SensorManager.SENSOR_DELAY_GAME);
  }

  // Must be call in owner Activity.onPause()
  @Override
  public void onPause(SensorManager sm) {
    sm.unregisterListener(this);
  }
  
  private void update(float[] rotationVector) {
    float[] M = new float[9];
    SensorManager.getRotationMatrixFromVector(M, rotationVector);
    
    final Mat3 mat = Mat3.fromRowMajorArray(M);
    setOrientation(Quaternion.fromMatrix(mat));
  }
  
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
      final float [] rotationVector = event.values.clone();

      debugText = "rotV : " + IO.vectorToString(rotationVector) + "\n";
      synchronized (this) {
        update(rotationVector);
      }
      
      debugText += "rot matrix :\n" + getOrientation().toMatrix();
      float[] mat44 = new float[16];
      getOrientation().toMatrix().toColumnMajorArray(mat44);
      debugText += "44 rot :\n" + IO.matrixToString(mat44);
      // Log.i(TAG, debugText);
    }
  }
}
