package net.fhtagn.orientation.orientation.sensors;

import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import net.fhtagn.orientation.orientation.math.Quaternion;

// An orientation tracker proxy that provides orientation relative to a
// base rotation.
// The base rotation is set when :
// - The first update has been received by the underlying orientation tracker
// - reset() is called. The base rotation is the rotation of the underlying tracker
//   at the time of the call

public class RelativeOrientationTracker extends OrientationTracker {
  private OrientationTracker realTracker;
  
  private Quaternion baseRot = null;
  
  public RelativeOrientationTracker(OrientationTracker tracker) {
    realTracker = tracker;
  }

  public void onSensorChanged(SensorEvent event) {
    realTracker.onSensorChanged(event);
    if (baseRot == null && realTracker.hasReceivedFirstUpdate()) {
      baseRot = realTracker.getOrientation();
    }

    if (baseRot != null) {
      final Quaternion relRot = baseRot.getConjugate().rightMult(realTracker.getOrientation());
      setOrientation(relRot);
    }
  }

  @Override
  public String getDebugText() {
    return realTracker.getDebugText();
  }

  @Override
  public void reset() {
    realTracker.reset();
    baseRot = realTracker.getOrientation();
  }

  @Override
  public void onResume(SensorManager sm) {
    realTracker.onResume(sm);
  }

  @Override
  public void onPause(SensorManager sm) {
    realTracker.onPause(sm);
  }
}
