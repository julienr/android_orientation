package net.fhtagn.orientation.orientation.sensors;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import net.fhtagn.orientation.orientation.events.OrientationEvent;
import net.fhtagn.orientation.orientation.math.Quaternion;

import de.greenrobot.event.EventBus;

public abstract class OrientationTracker implements SensorEventListener, OrientationProvider {
  private EventBus eventBus = new EventBus();

  // Toggled to true after first update
  private boolean updated = false;
  private Quaternion orientation = new Quaternion();

  public abstract String getDebugText();
  public abstract void reset();
  public abstract void onResume(SensorManager sm);
  public abstract void onPause(SensorManager sm);

  public boolean hasReceivedFirstUpdate() {
    return updated;
  }

  public final synchronized Quaternion getOrientation() {
    return orientation;
  }

  protected final synchronized void setOrientation(Quaternion orientation) {
    this.updated = true;
    this.orientation = orientation;
    this.eventBus.post(new OrientationEvent(this));
  }

  @Override
  public void onAccuracyChanged(Sensor sensor, int accuracy) {}

  // OrientationEvent will be posted on this eventBus whenever the orientation changes
  public EventBus getEventBus() { return eventBus; }
}
