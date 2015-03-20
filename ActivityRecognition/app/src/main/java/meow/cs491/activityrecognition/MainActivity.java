package meow.cs491.activityrecognition;

import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.DigitalClock;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


public class MainActivity extends ActionBarActivity {

    TextView timer;

    CountDownTimer CDT = new CountDownTimer(3000000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            timer.setText("" + String.format("%d:%d",
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))
            ));


        }

        @Override
        public void onFinish() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         timer = (TextView)findViewById(R.id.tvTimer);

        CDT.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
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

            return true;

        } else {
            Log.e("Yip","External storage not mounted.");
            return false;
        }
    }
}
