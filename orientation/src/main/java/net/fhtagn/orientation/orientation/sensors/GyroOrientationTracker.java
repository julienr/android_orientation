package net.fhtagn.orientation.orientation.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import net.fhtagn.orientation.orientation.math.Mat3;
import net.fhtagn.orientation.orientation.math.Quaternion;
import net.fhtagn.orientation.orientation.math.Vec3;

public class GyroOrientationTracker extends OrientationTracker {
  private final static String TAG = "GyroOrientationTracker";
  
  private Sensor gyroSensor;
  
  // A text that will be updated each time orientation changes and that 
  // contain a textual representation of the rotation matrix. Can be null
  private String debugText = "";


  private long lastTimestamp = 0;
  private static final float NS2S = 1.0f/1000000000.0f;
  
  public GyroOrientationTracker(SensorManager sm) {
    gyroSensor = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
  }
  
  // This will reset the orientation tracker. This can be used to reset
  // the integrator. This means the current orientation will be considered
  // to be eye(3).
  @Override
  public void reset() {
    setOrientation(new Quaternion());
    lastTimestamp = 0;
  }
  
  // Must be call in owner Activity.onResume()
  @Override
  public void onResume(SensorManager sm) {
    sm.registerListener(this, gyroSensor,
        SensorManager.SENSOR_DELAY_GAME);
  }

  // Must be call in owner Activity.onPause()
  @Override
  public void onPause(SensorManager sm) {
    sm.unregisterListener(this);
  }
  
  public String getDebugText() {
    return debugText;
  }
  
  private void incrUpdate(float[] deltaRotVector) {
    float[] M = new float[9];
    SensorManager.getRotationMatrixFromVector(M, deltaRotVector);
    
    final Mat3 mat = Mat3.fromRowMajorArray(M);

    setOrientation(getOrientation().rightMult(Quaternion.fromMatrix(mat)));
  }

  @Override
  public void onSensorChanged(SensorEvent event) {
    if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
      if (lastTimestamp != 0) {
        final float dT = (event.timestamp - lastTimestamp)*NS2S;
        //Log.i(TAG, "lastTimestamp : " + lastTimestamp);
        //Log.i(TAG, "event.timestamp : " + event.timestamp);
        //Log.i(TAG, "dT : " + dT);
        // See http://developer.android.com/reference/android/hardware/SensorEvent.html
        // (Code sample for Sensor.TYPE_GYROSCOPE)
        Vec3 axis = new Vec3(event.values[0], event.values[1], event.values[2]);
        //Log.i(TAG, "axis : " + axis);
        float angularSpeed = axis.norm();
        axis = axis.getNormalized();
        
        float thetaOverTwo = angularSpeed*dT/2.0f;
        float sinThetaOverTwo = (float)Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float)Math.cos(thetaOverTwo);
        
        // TOOD: Use our quaternion class ?
        float [] deltaRotVec = new float[4];
        deltaRotVec[0] = sinThetaOverTwo*axis.x;
        deltaRotVec[1] = sinThetaOverTwo*axis.y;
        deltaRotVec[2] = sinThetaOverTwo*axis.z;
        deltaRotVec[3] = cosThetaOverTwo;
        
        synchronized (this) {
          incrUpdate(deltaRotVec);
        }
        
        debugText =
            "gyro axis : " + axis + "\n" +
            "gyro speed : " + angularSpeed + "\n" + 
            "rot mat : \n" + getOrientation().toMatrix();
      }
      lastTimestamp = event.timestamp;
    }
  }
}
