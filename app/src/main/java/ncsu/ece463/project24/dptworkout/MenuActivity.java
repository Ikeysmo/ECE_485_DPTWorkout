package ncsu.ece463.project24.dptworkout;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileReader;

public class MenuActivity extends AppCompatActivity {
    public static String tempHolder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        try{//Options
            Log.d("DEBUG", "Trying to read the file!");
            FileInputStream fis = openFileInput("config.txt");
            byte[] innerRd = new byte[64];
            int size = fis.read(innerRd);
            String rcv = "";
            for(int i = 0; i < size; i++)
                rcv += (char) innerRd[i];
            OptionsActivity.IPAddress = rcv;
            Log.d("DEBUG", "RESTORED THE SETTINGS!");
        }
        catch (Exception d){Log.d("DEBUG", "Options not found!");}

        //create workout
        Workout ws = new Workout("Intro 101", "Workout to test things", new Exercise[]{
                new Exercise("Bicep Curls", "Bend arm at 45 degrees", 1, 5),
                new Exercise("Squats", "Lower body until legs are parallel to ground", 2, 3)
        }, 1020203);
        try {
            tempHolder = ws.getJSON().toString();
        }
        catch (JSONException e){e.printStackTrace();}
    }

    public void workoutLog(View view){
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    public void openContinueWorkout(View view){
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivity(intent);
    }

    public void editWorkout(View view){
        Intent intent = new Intent(this, CustomizeActivity.class);
        startActivity(intent);
    }

    public void gotoOptions(View view){
        Intent intent = new Intent(this, OptionsActivity.class);
        startActivity(intent);
    }
}
