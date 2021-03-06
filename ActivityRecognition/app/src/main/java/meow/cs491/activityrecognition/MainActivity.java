package meow.cs491.activityrecognition;

import android.content.Context;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import android.view.View;
import android.os.IBinder;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;


import meow.cs491.activityrecognition.ActivityService.MyLocalBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {


    ActivityService serviceBind;
    boolean isBound = false, isTimerRunning = false;
    TextView timer,status;
    DataPoint [] data = new DataPoint[4];
    TextView [] TV = new TextView[10];
    String [] activities = new String[10];
    int textViewCount = 0;
    Button btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = (TextView)findViewById(R.id.tvTimer);
        status = (TextView)findViewById(R.id.tvStatus);
         btn = (Button)findViewById(R.id.btnStart);


            TV[0]=(TextView)findViewById(R.id.tv1);
            TV[1]=(TextView)findViewById(R.id.tv2);
            TV[2]=(TextView)findViewById(R.id.tv3);
            TV[3]=(TextView)findViewById(R.id.tv4);
            TV[4]=(TextView)findViewById(R.id.tv5);
            TV[5]=(TextView)findViewById(R.id.tv6);
            TV[6]=(TextView)findViewById(R.id.tv7);
            TV[7]=(TextView)findViewById(R.id.tv8);
            TV[8]=(TextView)findViewById(R.id.tv9);
            TV[9]=(TextView)findViewById(R.id.tv10);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTimerRunning){
                    stopTimer();
                }
                else{
                    startTimer();
                }

            }
        });
    }


    public void stopTimer(){
        unbindService(bindToService);
        status.setText("Select 'Start Service' to restart");
        timer.setText("20:00");
        isTimerRunning = false;
        btn.setText("Start Timer");
        cdTimer.onFinish();
        cdTimer.cancel();
        writeToFile("ActivityOutput.txt", activities);
    }

    public void startTimer(){
        Intent i = new Intent(this, ActivityService.class);
        bindService(i, bindToService, Context.BIND_AUTO_CREATE);
//        serviceBind.turnOnServices(getApplicationContext());
        status.setText("Collecting data...");
        isTimerRunning = true;
        cdTimer.start();
        btn.setText("Stop Timer");


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


    private boolean writeToFile(String filename, Object ... data) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            Log.d("Yip", "Checking for directory. Creating if needed.");
            // Creating directory if it doesn't exist
            File dir = new File(Environment.getExternalStorageDirectory(), "cs491");
            Log.d("DEBUG", "Writing to: " + Environment.getExternalStorageDirectory());
            if (!dir.exists()) dir.mkdir();

            Log.d("Yip", "Checking for file. Creating if needed.");
            // Writing data to file
            File outputFile = new File(dir, filename);
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    Log.e("Yip", e.getMessage());
                    return false;
                }
            }

            Log.d("Yip", "Writing to file: " + outputFile.toString());
            FileOutputStream fOut;
            try {
                fOut = new FileOutputStream(outputFile);
                for (Object o:data) {
                    if (o != null)
                    fOut.write((o.toString() + "\n").getBytes());
                }
                fOut.flush();
                fOut.close();
            } catch (FileNotFoundException e) {
                Log.e("Yip", e.getMessage());
                return false;
            } catch (IOException e) {
                Log.e("Yip", e.getMessage());
                return false;
            }
            Log.d("DEBUG","File Created");
            return true;

        } else {
            Log.e("Yip","External storage not mounted.");
            return false;
        }


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

    CountDownTimer cdTimer = new CountDownTimer(1200000,1000) {
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
            }

            if(count == 4){
                count = 0;
                try{
                    Log.d("DEBUG", ActivityEvaluator.determineActivity(data) + "");
                    printActivity(ActivityEvaluator.determineActivity(data));
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        @Override
        public void onFinish() {
            if (count == 3){
                try{
                    data[count] = serviceBind.collectData(0);
                    printActivity(ActivityEvaluator.determineActivity(data));
                    stopTimer();
                } catch(Exception e){
                    e.printStackTrace();
                }

            }

        }
    };


    public void printActivity(ActivityEvaluator.PhysicalActivity activity){
        long currentTime = System.currentTimeMillis();
        Date currentDate = new Date(currentTime );
        String enddate  = new SimpleDateFormat("hh:mm a").format(currentDate );
        currentDate.setTime(currentTime - 119900);
        String startdate = new SimpleDateFormat("hh:mm a").format(currentDate);

        TV[textViewCount % 10].setText(startdate + " - " + enddate + " " + activity);
        activities[textViewCount % 10] = startdate + " - " + enddate + " " + activity;
        textViewCount++;
    }
}
