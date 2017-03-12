package ncsu.ece463.project24.dptworkout;

import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Date;
/*
Description: Heart of the app, user workouts are used here
Author: Isaiah Smoak
 */

public class WorkoutActivity extends AppCompatActivity implements Runnable {

    private int seconds = 0; //time for workout
    private boolean running = false; //if workout is paused or running
    DataInputStream dis;
    DataOutputStream dos;
    MediaPlayer[] audiofiles;
    private final static int CORRECT = 0;
    private static final int CALIBRATING = 1;
    private final static int TRY_AGAIN = 2;
    private final static int GOOD_JOB = 3;
    private int currSet; //current set in workout
    private int currReps; //current rep in workout
    private Exercise currExercise; //current excercise in workout
    private Workout currWorkout; //current workout selected

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        try { //reads the workout from string (which will be a file)
            currWorkout = Workout.getWorkout((JSONObject) new JSONTokener(MenuActivity.tempHolder).nextValue());
            System.out.println(currWorkout);
        }
        catch (JSONException je){je.printStackTrace();} //need to handle this!!

        //initialize the GUI to proper sets, reps and time
        TextView setCounter = (TextView) findViewById(R.id.setCounter);
        setCounter.setText(String.valueOf(currWorkout.exercises.elementAt(0).totalSets));
        TextView repCounter = (TextView) findViewById(R.id.repCounter);
        repCounter.setText(String.valueOf(currWorkout.exercises.elementAt(0).totalReps));

        new Thread(this).start(); // starts the thread that handles the workout
        runTimer(); //begin timer to count down
    }

    public void startWorkout(View view) { //upon button pressed: starts or resumes the workout
        Button startStopButton = (Button) view.findViewById(R.id.startWorkout);
        if (running) {
            startStopButton.setText("Start!");
            startStopButton.setBackgroundColor(Color.GREEN);
            running = false;
        } else {
            startStopButton.setText("Stop!");
            startStopButton.setBackgroundColor(Color.RED);
            running = true;
        }
        startStopButton.invalidate();
    }

    private void runTimer() { //issue with the timer is that it only checks every second. Meaning at most 1 sec lag, not instantaneous
        final TextView timeView = (TextView) findViewById(R.id.timeText);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int minutes = (seconds / 60);
                int secs = seconds % 60;
                String currentTime = String.format("%d:%02d", minutes, secs);
                timeView.setText(currentTime);
                if (running) {
                    seconds++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    @Override
    public void run() {
        //initialize audio
        audiofiles = new MediaPlayer[11];
        audiofiles[CORRECT] = MediaPlayer.create(this, R.raw.correct);
        audiofiles[CALIBRATING] = MediaPlayer.create(this, R.raw.calibrating);
        audiofiles[TRY_AGAIN] = MediaPlayer.create(this, R.raw.try_again);
        audiofiles[GOOD_JOB] = MediaPlayer.create(this, R.raw.good_job);
        try { //connect to server
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(OptionsActivity.IPAddress, 11000), 500); //see if this works?
            dos = new DataOutputStream(clientSocket.getOutputStream());
            dis = new DataInputStream(clientSocket.getInputStream());
            //dos.write("Bicep Curls".getBytes("UTF-8"));
        } catch (IOException ie) { //was unable to connect. Send error and handle appropriately
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WorkoutActivity.this, "Unable to connect! Check IP address and try again!", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        byte[] input = new byte[100]; //array for socket receiving (doesn't expect more than 50 bytes)
        String response = "";
        try {
            for(int i = 0; i < currWorkout.exercises.size(); i++) {
                //send workout
                currExercise = currWorkout.exercises.elementAt(i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView excerciseName = (TextView) findViewById(R.id.textView9);
                        excerciseName.setText(currExercise.name);
                        TextView currentRep = (TextView) findViewById(R.id.repCounter);
                        currentRep.setText(String.valueOf(currExercise.totalReps));
                        TextView currentSet = (TextView) findViewById(R.id.setCounter);
                        currentSet.setText(String.valueOf(currExercise.totalSets));
                    }
                });
                dos.write(currWorkout.exercises.elementAt(i).name.getBytes()); //send name of workout
                dos.write("start".getBytes());
                Log.d("DEBUG", "DOING WORKOUT: " + currExercise.name);
                //int rd = dis.read(input); //wait for OK or confirmation
                currSet = currExercise.totalSets;
                currReps = currExercise.totalReps;

                while ( currSet > 0) { //while not yet zero
                    int readAmount = dis.read(input);
                    if (!running)
                        continue; //ignore the input here and await next command
                    response = "";
                    //convert the bytes into a string
                    for (int j = 0; j < readAmount; j++) {
                        response += (char) input[j];
                    }
                    Log.d("DEBUG", "RECV: " + response);
                    //make decisions on what was received
                    if (response.equalsIgnoreCase("ERROR") || response.equalsIgnoreCase("INCORRECT")) {
                        //handle it like a boss!
                        currWorkout.exercises.elementAt(i).errors++; //increment error counter!
                        runOnUiThread(new Runnable() { //update GUI to reflect new state
                            @Override
                            public void run() {
                                View view = (View) findViewById(R.id.activity_workout);
                                TextView errorCounter = (TextView) findViewById(R.id.textView8);
                                view.setBackgroundColor(getResources().getColor(R.color.DarkRed));
                                errorCounter.setText(String.valueOf(Integer.parseInt(errorCounter.getText().toString()) + 1));
                            }
                        });
                        if(Config.audio_enabled)
                            audiofiles[TRY_AGAIN].start(); //play try again through speakers
                    } else if (response.equalsIgnoreCase("CORRECT")) { //increment counter
                        --currReps; //decrement
                        if(currReps <= 0)
                            --currSet;
                        //play sound here
                        if (((int) (Math.random() * 2)) == 0 && Config.audio_enabled) //50% chance on saying either correct or good job
                            audiofiles[CORRECT].start();
                        else {
                            if(Config.audio_enabled)
                                audiofiles[GOOD_JOB].start();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                if(currReps <= 0) { //reset the reps and decrement currSet
//                                    //if currset == 0
//                                   --currSet;
                                if(currReps <= 0) {
                                    if (currSet > 0) //done with reps but more sets
                                        currReps = currExercise.totalReps;
                                    //Log.d("DEBUG", "NEW SET, GOING AT IT");
                                }
                                //}
                                View view = (View) findViewById(R.id.activity_workout);
                                view.setBackgroundColor(getResources().getColor(R.color.LightBlue));
                                TextView setCounter = (TextView) findViewById(R.id.setCounter);
                                setCounter.setText(String.valueOf(currSet));
                                TextView repCounter = (TextView) findViewById(R.id.repCounter);
                                repCounter.setText(String.valueOf(currReps));
                            }
                        }); //does runonUIthread executes before while loop continues??
                    }

                }
            }
            dos.write("stop".getBytes()); //write stop to prevent kinect to keep sending info
            dos.close();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WorkoutActivity.this, "Great workout! Returning to menu!", Toast.LENGTH_LONG).show();
                    View view = (View) findViewById(R.id.activity_workout);
                    view.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    //save workout!
                    try {
                        currWorkout.date = new Date().getTime();
                        Workout.saveWorkout(currWorkout, getApplicationContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) { //file corrupted!
                        e.printStackTrace();
                    }
                    TextView setCounter = (TextView) findViewById(R.id.setCounter);
                    setCounter.setText(String.valueOf(0)); //set everything to 0
                    TextView repCounter = (TextView) findViewById(R.id.repCounter);
                    repCounter.setText(String.valueOf(0)); //set everything to 0
                    //eventually want to try to reconnect!
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                            getApplicationContext().startActivity(intent);
                        }
                    }, 2000);
                }
            });
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WorkoutActivity.this, "Disconnected from server! Exit and restart!", Toast.LENGTH_LONG).show();
                    //eventually want to try to reconnect!
                }
            });
        }
    } //end run method

    @Override
    protected void onPause(){
        super.onPause();
        try {
            dos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
