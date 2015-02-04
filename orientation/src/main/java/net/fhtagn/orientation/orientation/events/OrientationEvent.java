package net.fhtagn.orientation.orientation.events;

import net.fhtagn.orientation.orientation.sensors.OrientationTracker;

public class OrientationEvent {
    // The tracker that triggered this event
    public final OrientationTracker tracker;

    public OrientationEvent(OrientationTracker tracker) {
        this.tracker = tracker;
    }
}
