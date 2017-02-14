package ncsu.ece463.project24.dptworkout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by Ikeys on 1/21/2017.
 */

public class Workout {
    public String title;
    public String description;
    public long date; //or another storage method
    public Vector<Exercise> exercises = new Vector<Exercise>();

    public Workout(String name, String description, Exercise[] list, long date){
        this.title = name;
        this.description = description;
        for(Exercise e: list){
            exercises.add(e);
        }
        this.date = date;
    }

    public Workout(String name, String description, Vector<Exercise> lsExcer, long date){
        this.title = name;
        this.description = description;
        this.exercises = lsExcer;
        this.date = date;
    }


    public void addExcercise(String name, String description, int set, int reps, int secs){
        //String name, String instructions, int set, int reps, int secs
        exercises.add(new Exercise(name, description, set, reps, secs));
    }

    public Exercise[] getWorkoutList(){
        return exercises.toArray(new Exercise[exercises.size()]);
    }

    public JSONObject getJSON() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        for(int i = 0; i < exercises.size(); i++){
            jsonArray.put(exercises.elementAt(i).getJSON());
        }

        return new JSONObject().put("Name", title ).put("Description", description).put("Date", date).put("Exercises", jsonArray);
    }

    public static Workout getWorkout(JSONObject je) throws JSONException{
        String tname = je.getString("Name");
        String tdescr = je.getString("Description");
        long date = je.getLong("Date");
        JSONArray jsonArray = je.getJSONArray("Exercises");
        Vector<Exercise> lsExcercises = new Vector<Exercise>();
        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject js = jsonArray.getJSONObject(i);
            lsExcercises.add(new Exercise(js.getString("Name"), js.getString("Instructions"), js.getInt("Sets"), js.getInt("Reps"), js.getInt("Time")));
        }
        return new Workout(tname, tdescr, lsExcercises, date);

    }

    public String toString(){
        return "Workout Title: " + title + "\n Workout Description: " + description + Arrays.toString(exercises.toArray(new Exercise[exercises.size()]));

    }

}
