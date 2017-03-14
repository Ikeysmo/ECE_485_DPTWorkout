package ncsu.ece463.project24.dptworkout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;

/*
Description: This activity provides interface to reaching other activities
Author: Isaiah Smoak
 */
public class MenuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static String tempHolder; //here for debugging purposes only

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle("DPT Workout");
        try{     //try parsing the Options/IP address if available
            OptionsActivity.IPAddress = getIPFromConfig();
            Config.IP_ADDRESS = OptionsActivity.IPAddress;
        }
       catch (Exception d){Log.d("DEBUG", "Options not found!");}
        //create workout w/ random date/time stamp
        try{
            Workout.loadCustomWorkouts(this);
        }
        catch (IOException fe){
            //if can't load custom, make custom and save
            Workout ws = new Workout("Intro 101", "Workout to test things", new Exercise[]{
                    new Exercise("Bicep Curls", "Bend arm at 45 degrees", 1, 5),
                    new Exercise("Squats", "Lower body until legs are parallel to ground", 1, 3),
                    new Exercise("Lateral Raises", "Blah blah", 1, 5)
            }, new Date().getTime());
            Workout.addCustomWorkout(ws);

        }

        //used for Workout Activity testing reading JSON from string that would be stored in a file,

        //get all the names of all the workouts that exist and add to spinner!
        Spinner sp = (Spinner) findViewById(R.id.selectWorkout);
        sp.setAdapter(new SpinAdapter(this, R.layout.support_simple_spinner_dropdown_item, Workout.getCustomWorkouts()));
        sp.setOnItemSelectedListener(this);
    }

    public void workoutLog(View view){ //goes to LOG_ACTIVITY
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    public void openContinueWorkout(View view){ //goes to WORKOUT_ACTVITY
        Spinner sp = (Spinner) findViewById(R.id.selectWorkout);
        Workout.currentWorkout = (Workout) sp.getSelectedItem();
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
    public String getIPFromConfig() throws IOException, ClassNotFoundException {
        Log.d("DEBUG", "Trying to read the file!");
        Config.loadSettings(this);
        //FileInputStream fis = openFileInput("config.txt");
        //ObjectInputStream oij = new ObjectInputStream(fis);
        //Config con = (Config) oij.readObject();
       // byte[] innerRd = new byte[64];
        //int size = fis.read(innerRd);
        //String rcv = "";
        //for(int i = 0; i < size; i++)
            //rcv += (char) innerRd[i];

        Log.d("DEBUG", "RESTORED THE SETTINGS!");
        //return rcv;
        return Config.IP_ADDRESS;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Spinner sp = (Spinner) findViewById(R.id.selectWorkout);
        sp.setAdapter(new SpinAdapter(this, R.layout.support_simple_spinner_dropdown_item, Workout.getCustomWorkouts()));

    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //do stuff here
        Workout myman = (Workout) adapterView.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class SpinAdapter extends ArrayAdapter<Workout>{
        private Context context;
        private Workout[] list;

        public SpinAdapter(Context context, int textViewResourseId, Workout[] values){
            super(context, textViewResourseId, values);
            this.context = context;
            this.list = values;
        }

        public int getCount(){
            return list.length;
        }

        public Workout getItem(int position){
            return list[position];
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            TextView label = new TextView(context);
            label.setTextColor(Color.BLUE);
            label.setTextSize(20);
            label.setText(list[position].title);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent){
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setTextSize(20);
            label.setText(list[position].title);
            return label;
        }
    }
}
