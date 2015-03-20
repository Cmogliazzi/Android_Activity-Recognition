package meow.cs491.activityrecognition;

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.os.CountDownTimer;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.view.View;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import meow.cs491.activityrecognition.ActivityService.MyLocalBinder;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {


    ActivityService serviceBind;
    boolean isBound = false;
    TextView timer;
    DataPoint [] data = new DataPoint[4];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = (TextView)findViewById(R.id.tvTimer);
        Button btn = (Button)findViewById(R.id.btnStart);



        Intent i = new Intent(this, ActivityService.class);
        bindService(i, bindToService, Context.BIND_AUTO_CREATE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            serviceBind.turnOnServices(getApplicationContext());
            startTimer();
            }
        });
    }

    public void startTimer(){
        new CountDownTimer(600000,1000) {
            int count = 0;
            @Override
            public void onTick(long millisUntilFinished) {
                timer.setText("" + String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
                ));
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                if(seconds == 0 || seconds == 30){
                        data[count] = serviceBind.collectData(millisUntilFinished);
                        count++;
                        timer.setText("Minute has passed!");
                }

                if(count == 4){
                    timer.setText("2 Minutes has passed!");
                    count = 0;
                    try{
                        ActivityEvaluator.determineActivity(data);
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFinish() {
                //Save one last time, and call Activity Eval again
                //SAVE TO FILE
            }
        }.start();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection bindToService = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyLocalBinder binder = (MyLocalBinder) service;
            serviceBind = binder.getService();
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };
}
