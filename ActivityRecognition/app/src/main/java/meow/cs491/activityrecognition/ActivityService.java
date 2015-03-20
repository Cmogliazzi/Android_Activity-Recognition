package meow.cs491.activityrecognition;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Binder;
import android.util.Log;

public class ActivityService extends Service implements SensorEventListener, LocationListener{

    private final IBinder bindService = new MyLocalBinder();
    private Sensor accelerometer;
    private SensorManager sensormanager;
    private LocationManager locationmanager;
    private float yVal;
    private Location location;

    public ActivityService() {}
    public void turnOnServices(Context c){
       locationmanager = (LocationManager) c.getSystemService(c.LOCATION_SERVICE);
       sensormanager = (SensorManager) c.getSystemService(c.SENSOR_SERVICE);
        accelerometer = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sensormanager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        locationmanager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0, Criteria.ACCURACY_FINE, this);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return bindService;
    }

   public DataPoint collectData(long time){
       return new DataPoint(time,location,yVal);
   }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            yVal = event.values[1];
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }
    @Override
    public void onProviderEnabled(String provider) {

    }
    @Override
    public void onProviderDisabled(String provider) {

    }


    public class MyLocalBinder extends Binder{
        ActivityService getService(){
            return ActivityService.this;
        }
    }


}
