package ncsu.ece463.project24.dptworkout;

import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WorkoutActivity extends AppCompatActivity {
    private int seconds = 0;
    private boolean running = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        runTimer();
    }

    public void startWorkout(View view){ //toggles workout
        Button d = (Button) view.findViewById(R.id.startWorkout);
        if(running) {
            d.setText("Start!");
            d.setBackgroundColor(Color.GREEN);
            running = false;
        }
        else {
            d.setText("Stop!");
            d.setBackgroundColor(Color.RED);
            running = true;
        }

        Log.d("DEBUG", "WAHADDIDH");

        //d.setBackgroundColor(Color.RED);
        //view.invalidate();
        d.invalidate();

    }

    private void runTimer(){ //issue with the timer is that it only checks every second. Meaning at most 1 sec lag, not instantaneous
        final TextView timeView = (TextView) findViewById(R.id.timeText);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds/60);
                int secs = seconds%60;
                String currentTime = String.format("%d:%02d", minutes, secs);
                timeView.setText(currentTime);
                if(running){
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}
