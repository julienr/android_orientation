package net.fhtagn.orientation.orientation.sensors;

import net.fhtagn.orientation.orientation.math.Quaternion;

import de.greenrobot.event.EventBus;

public interface OrientationProvider {
    // This method should provide the current orientation.
    // It MUST be thread-safe
    public Quaternion getOrientation();

    // OrientationEvent will be posted on this EventBus whenever the orientation changes
    public EventBus getEventBus();
}
