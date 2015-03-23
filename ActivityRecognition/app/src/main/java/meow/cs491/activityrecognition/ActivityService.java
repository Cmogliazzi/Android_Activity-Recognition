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
    String provider;
    private Location location;

    public ActivityService() {

    }
    @Override
    public IBinder onBind(Intent intent) {
        locationmanager = (LocationManager) getApplicationContext().getSystemService(getApplicationContext().LOCATION_SERVICE);
        sensormanager = (SensorManager) getApplicationContext().getSystemService(getApplicationContext().SENSOR_SERVICE);
        accelerometer = sensormanager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensormanager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Criteria locationCriteria = new Criteria();
        locationCriteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationmanager.getBestProvider(locationCriteria, true);
        locationmanager.requestLocationUpdates(provider,0, 0, this);
        Log.d("DEBUG", "Provider is: " + provider);
        return bindService;
    }

   public DataPoint collectData(long time){
       if (location == null){
          location =  locationmanager.getLastKnownLocation(provider);
       }
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
        //Log.d("DEBUG", "NEW LOCATION: " );

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
