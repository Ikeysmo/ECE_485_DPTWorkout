package ncsu.ece463.project24.dptworkout;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Ikeys on 1/21/2017.
 */

public class Exercise { //make a text file for default workouts!
    public String name;
    public String instructions; //
    public int totalSets;
    public int totalReps;
    public int totalSecs = 0; //if not zero, then timer counts from value
    public int correct; //may not be used
    public int errors;

    public Exercise(String name, String instructions, int set, int reps){
        this.totalSets = set;
        this.name = name;
        this.instructions = instructions;
        this.totalReps = reps;
    }
    public Exercise(String name, String instructions, int set, int reps, int secs ){
        this.totalSets = set;
        this.name = name;
        this.instructions = instructions;
        this.totalReps = reps;
        this.totalSecs = secs;
    }

    public JSONObject getJSON() throws JSONException {
        return new JSONObject().put("Name", name ).put("Instructions", instructions).put("Sets", totalSets).put("Reps", totalReps).put("Time", totalSecs);
    }

    public String toString(){
        return name + " : set = " + totalSets + "; reps = " + totalReps+ "; ";
    }
}
