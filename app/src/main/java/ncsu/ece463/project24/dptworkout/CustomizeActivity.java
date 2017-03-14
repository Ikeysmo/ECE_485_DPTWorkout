package ncsu.ece463.project24.dptworkout;

import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Vector;

/*
Description: This activity is where the user can add create workouts by selecting routines that are implemented!
Author: Isaiah Smoak
 */
public class CustomizeActivity extends AppCompatActivity {

    private Vector<Exercise> listExercises = new Vector<Exercise>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
    }


    public void createWorkout(View view){
//        EditText sets = (EditText) findViewById(R.id.editText3);
//        EditText reps = (EditText) findViewById(R.id.editText4);
        Spinner list = (Spinner) findViewById(R.id.spinner2);
        if(listExercises.isEmpty()){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Cannot Create Empty Workout!", Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        //create new Workout
        EditText gname = (EditText) findViewById(R.id.editText2);
        Workout createdWorkout = new Workout(gname.getText().toString(), "Custom Workout",
                listExercises, 0 );
        //save it to list of workouts!
        Workout.addCustomWorkout(createdWorkout);
        try {
            Workout.saveCustomWorkouts(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "Created Workout", Toast.LENGTH_SHORT).show();
        //Spinner sp = (Spinner) findViewById(R.id.selectWorkout);
        //ArrayAdapter d = (ArrayAdapter) sp.getAdapter();
        //d.notifyDataSetChanged();
        //create new exercise

    }
    public void addExercise(View view){
        //get data from GUI elements and add them as new excercise
        //EditText gname = (EditText) findViewById(R.id.editText2);
        Spinner gname = (Spinner) findViewById(R.id.spinner2);
        //gname.getSelectedItem().toString()
        EditText gsets = (EditText) findViewById(R.id.editText3);
        EditText greps = (EditText) findViewById(R.id.editText4);
        listExercises.add(new Exercise(gname.getSelectedItem().toString(), "whadd", Integer.parseInt(gsets.getText().toString()), Integer.parseInt(greps.getText().toString())));
        //update GUI component
        runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        TextView tv = (TextView) findViewById(R.id.exercise_listing);
                        tv.setTypeface(Typeface.MONOSPACE);
                        tv.setText(printExercises());
                    }
                }
        );
    }

    private String printExercises(){
        String all = "";
        //print all the exercises and sets/reps
        if(listExercises.isEmpty())
            return "(empty)";
        for(int i = 0; i < listExercises.size(); i++){
            all += String.format("%-20sSets: %s\t\tReps: %s\n", listExercises.elementAt(i).name, listExercises.elementAt(i).totalSets, listExercises.elementAt(i).totalReps);
            //all += listExercises.elementAt(i).name + "\t\t\t Sets: "+listExercises.elementAt(i).totalSets + "\tReps: " + listExercises.elementAt(i).totalReps+"\n";
        }
        return all;
    }
}
