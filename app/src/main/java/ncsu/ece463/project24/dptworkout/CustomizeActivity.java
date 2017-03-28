package ncsu.ece463.project24.dptworkout;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Vector;

/*
Description: This activity is where the user can add create workouts by selecting routines that are implemented!
Author: Isaiah Smoak
 */
public class CustomizeActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Vector<Exercise> listExercises = new Vector<Exercise>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customize);
    }

    @Override
    public void onResume(){
        super.onResume();
        Spinner createdWorkouts = (Spinner) findViewById(R.id.spinner);
        createdWorkouts.setAdapter(new SpinAdapter(this, R.layout.support_simple_spinner_dropdown_item, Workout.getCustomWorkouts()));
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
        Spinner createdWorkouts = (Spinner) findViewById(R.id.spinner);
        createdWorkouts.setAdapter(new SpinAdapter(this, R.layout.support_simple_spinner_dropdown_item, Workout.getCustomWorkouts()));
        createdWorkouts.setSelection(Workout.getCustomWorkouts().length-1);
        TextView exerciselist = (TextView) findViewById(R.id.exercise_listing);
        exerciselist.setText("");

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
         TextView tv = (TextView) findViewById(R.id.exercise_listing);
         tv.setTypeface(Typeface.MONOSPACE);
         tv.setText(printExercises());

    }

    public void deleteWorkout(View view){
        Spinner createdWorkouts = (Spinner) findViewById(R.id.spinner);
        int index = createdWorkouts.getSelectedItemPosition();
        if(index != AdapterView.INVALID_POSITION) {
            Workout.deleteCustomWorkout(index);
            try {
                Workout.saveCustomWorkouts(getApplicationContext());
            }
            catch (IOException e){;}
        }
        createdWorkouts.setAdapter(new SpinAdapter(this, R.layout.support_simple_spinner_dropdown_item, Workout.getCustomWorkouts()));

        if(index > 0)
            createdWorkouts.setSelection(--index);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class SpinAdapter extends ArrayAdapter<Workout> {
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
            label.setTextSize(16);
            label.setText(list[position].title);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent){
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setTextSize(16);
            label.setText(list[position].title);
            return label;
        }
    }
}
