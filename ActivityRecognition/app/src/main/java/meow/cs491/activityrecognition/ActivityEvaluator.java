package meow.cs491.activityrecognition;

import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

/**
 * Created by kayla (ntokash) on 3/19/15.
 */
public class ActivityEvaluator{
    static private double NO_MOVEMENT_LIMIT = 5;
    static private double ORIENTATION_LIMIT = 5;
    static public float MOO2 = 0;

    static public enum PhysicalActivity {
        MOVING, STANDING, LAYING, UNKNOWN;
    }

    static public PhysicalActivity determineActivity(DataPoint ... data) throws Exception {
        if (data.length % 2 != 0) throw new Exception("Invalid array of data. There must be an even number of DataPoint values.");

        int moving = 0, standing = 0, laying = 0, unknown = 0;

        for(int i = 0; i < data.length; i+=2) {
            if (isMoving(data[i], data[i + 1])) {
                ++moving;
            } else if (isStanding(data[i], data[i + 1])) {
                ++standing;
            } else if (isLaying(data[i], data[i + 1])) {
                ++laying;
            } else {
                ++unknown;
            }
        }

        if (moving >= standing && moving >= laying && moving >= unknown) {
            return PhysicalActivity.MOVING;
        } else if (standing >= moving && standing >= laying && standing >= unknown) {
            return PhysicalActivity.STANDING;
        } else if (laying >= moving && laying >= standing && laying >= unknown) {
            return PhysicalActivity.LAYING;
        } else {
            return PhysicalActivity.UNKNOWN;
        }
    }

    // Accelerometer y-axis > 5
    // No change in Location
    static public boolean isLaying(DataPoint dataPoint1, DataPoint dataPoint2) {
        double avgAccel = (Math.abs(dataPoint1.yAcceleration) + Math.abs(dataPoint2.yAcceleration)) / 2;
        return dataPoint1.location.distanceTo(dataPoint2.location) <= NO_MOVEMENT_LIMIT && avgAccel <= ORIENTATION_LIMIT;
    }

    // Accelerometer y-axis <=5
    // No change in Location
    static public boolean isStanding(DataPoint dataPoint1, DataPoint dataPoint2) {
        double avgAccel = (Math.abs(dataPoint1.yAcceleration) + Math.abs(dataPoint2.yAcceleration)) / 2;
        return dataPoint1.location.distanceTo(dataPoint2.location) <= NO_MOVEMENT_LIMIT && avgAccel > ORIENTATION_LIMIT;
    }

    // Accelerometer y-axis doesn't matter
    // Location is different
    static public boolean isMoving(DataPoint dataPoint1, DataPoint dataPoint2) {
        //Log.d("DEBUG", dataPoint1.location.distanceTo(dataPoint2.location) + " = Distance");
        return dataPoint1.location.distanceTo(dataPoint2.location) > NO_MOVEMENT_LIMIT;
    }



}
