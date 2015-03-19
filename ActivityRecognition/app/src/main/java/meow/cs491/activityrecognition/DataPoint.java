package meow.cs491.activityrecognition;

import android.location.Location;
/**
 * Created by meow on 3/19/15.
 */
public class DataPoint {

    final public long time;
    final public Location location;
    final public double yAcceleration;

    public DataPoint(long t, Location l, double y){
        time = t;
        location = l;
        yAcceleration = y;
    }

}
