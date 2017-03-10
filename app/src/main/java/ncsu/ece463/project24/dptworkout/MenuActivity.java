package ncsu.ece463.project24.dptworkout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/*
Description: This activity provides interface to reaching other activities
Author: Isaiah Smoak
 */
public class MenuActivity extends AppCompatActivity {
    public static String tempHolder; //here for debugging purposes only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        try{     //try parsing the Options/IP address if available
            OptionsActivity.IPAddress = getIPFromConfig();
        }
       catch (Exception d){Log.d("DEBUG", "Options not found!");}
        //create workout w/ random date/time stamp
        Workout ws = new Workout("Intro 101", "Workout to test things", new Exercise[]{
                new Exercise("Bicep Curls", "Bend arm at 45 degrees", 1, 5),
                new Exercise("Squats", "Lower body until legs are parallel to ground", 1, 3),
                new Exercise("Lateral Raises", "Blah blah", 1, 5)
        }, new Date().getTime());
        //used for Workout Activity testing reading JSON from string that would be stored in a file,
        try {
            tempHolder = ws.getJSON().toString(); //convert workout into JSON string
        }
        catch (JSONException e){e.printStackTrace();}
        //will eventually add in code to list all the workouts from a file below

        //get all the names of all the workouts that exist and add to spinner!
        Spinner sp = (Spinner) findViewById(R.id.selectWorkout);
       // Workout.saveWorkout(ws, this);
    }

    public void workoutLog(View view){ //goes to LOG_ACTIVITY
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    public void openContinueWorkout(View view){ //goes to WORKOUT_ACTVITY
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivity(intent);
    }

    public void editWorkout(View view){ //goes to CUSTOMIZE_ACTIVITY
        Intent intent = new Intent(this, CustomizeActivity.class);
        startActivity(intent);
    }

    public void gotoOptions(View view){ //goes to OPTIONS_ACTIVITY
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }

    public void tryBLE(View view){
        Intent intent = new Intent(this, BLEActivity.class);
        startActivity(intent);
    }
    /* Reads IP address from Config file... assumes only IP address is there for now */
    public String getIPFromConfig() throws IOException {
        Log.d("DEBUG", "Trying to read the file!");
        FileInputStream fis = openFileInput("config.txt");
        byte[] innerRd = new byte[64];
        int size = fis.read(innerRd);
        String rcv = "";
        for(int i = 0; i < size; i++)
            rcv += (char) innerRd[i];

        Log.d("DEBUG", "RESTORED THE SETTINGS!");
        return rcv;
    }
}
