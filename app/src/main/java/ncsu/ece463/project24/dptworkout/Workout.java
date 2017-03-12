package ncsu.ece463.project24.dptworkout;

import android.content.Context;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;

/**
 * Created by Ikeys on 1/21/2017.
 */

public class Workout implements Serializable {
    public String title;
    public String description;
    public long date; //or another storage method
    public boolean complete = false;
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

    //Improved version, only deal with ObjectInputStream
    public static Workout[] loadWorkouts(Context context) throws IOException, ClassNotFoundException {
        FileInputStream fis = context.openFileInput("workout.log");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Workout[] workouts = (Workout[]) ois.readObject();
        return workouts;
    }

    //Improved version, only deal with ObjectOutputStreams
    public static void saveWorkout(Workout wk, Context context) throws IOException, ClassNotFoundException {
        Vector<Workout> listworkouts = null;

        try{
            FileInputStream fis = context.openFileInput("workout.log");
            ObjectInputStream ois = new ObjectInputStream((fis));
            Workout[] oldWorkouts = (Workout[]) ois.readObject();
            ois.close();
            listworkouts = new Vector<Workout>(Arrays.asList(oldWorkouts));
        }
       catch (IOException d ){Log.d("DEBUG", "CREATING WORKOUT LOG");}

        //want the last 30 items, but for now, save them all!
        //now resave
        if(listworkouts == null)
            listworkouts = new Vector<Workout>();
        //either way, add list workouts!
        listworkouts.add(wk); //added latest workout

        FileOutputStream fos = context.openFileOutput("workout.log", Context.MODE_PRIVATE);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(listworkouts.toArray(new Workout[listworkouts.size()]));
        oos.close();
    }

    /*Older version that tried to use JSON */
//    public static void saveWorkout(Workout wk, Context context){ //only save the reoccuring ones!
//        try { //load file
//            FileInputStream fis = context.openFileInput("workout.log");
//            InputStreamReader isr = new InputStreamReader(fis);
//            BufferedReader br = new BufferedReader(isr);
//            String line = null;
//            String file = "";
//            //do bytes next time!
//            while((line = br.readLine()) != null){
//                file += line;
//                Log.d("DEBUG!", line);
//            }
//            Log.d("DEBUG", "TRYINGSON!");
//            Log.d("DEBUG22", file);
//            file = file.trim();
//            //JSONArray  jr = (JSONArray) new JSONTokener(file).nextValue();
//            //Log.d("OUTPUT", jr.toString());
////            jr.r;
////            String input = "";
////            while (fr.)
//            //read in all the workouts
//        }
//        catch (FileNotFoundException fe){
//            //create file
//            try {
//                Log.d("DEBUG", "CREATING FILE");
//                FileOutputStream fw = context.openFileOutput("workout.log", Context.MODE_PRIVATE);
//                OutputStreamWriter osw = new OutputStreamWriter(fw);
//                Vector<Workout> dis = new Vector<Workout>();
//                dis.add(new Workout("Band", "what a man", new Exercise[]{
//                        new Exercise("pushups", "didd", 3, 4)
//                }, 303));
//                dis.add(new Workout("Stand", "wandddd", new Exercise[]{
//                        new Exercise("pushups", "didd", 3, 4)
//                }, 4403));
//                JSONArray jsarray = new JSONArray();
//                jsarray.put(dis.elementAt(0).getJSON());
//                jsarray.put(dis.elementAt(1).getJSON());
//                Log.d("DEBUG", "Hey There!");
//                Log.d("DEBUG STHIS", jsarray.toString() + System.lineSeparator());
//                osw.write(jsarray.toString());
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        } catch (IOException e) { //if could not write/read to file
//            e.printStackTrace();
//        } //catch (JSONException e) {
//            //e.printStackTrace();}
//    }

}
