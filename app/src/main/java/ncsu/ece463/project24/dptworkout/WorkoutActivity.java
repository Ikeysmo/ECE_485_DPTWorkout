package ncsu.ece463.project24.dptworkout;

import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
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

public class WorkoutActivity extends AppCompatActivity implements Runnable {

    private int seconds = 0;
    private boolean running = false;
    DataInputStream dis;
    DataOutputStream dos;
    MediaPlayer[] audiofiles;
    private final static int CORRECT = 0;
    private static final int CALIBRATING = 1;
    private final static int TRY_AGAIN = 2;
    private final static int GOOD_JOB = 3;
    private int currSet;
    private int currReps;
    private Exercise currExercise;
    private Workout currWorkout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);
        try {
            currWorkout = Workout.getWorkout((JSONObject) new JSONTokener(MenuActivity.tempHolder).nextValue());
            System.out.println(currWorkout);
        }
        catch (JSONException je){je.printStackTrace();} //need to handle this!!
        //set up connection to kinect and bluetooth
        TextView tv = (TextView) findViewById(R.id.textView10);
        tv.setText(String.valueOf(currWorkout.exercises.elementAt(0).totalSets));
        TextView tv1 = (TextView) findViewById(R.id.textView6);
        tv1.setText(String.valueOf(currWorkout.exercises.elementAt(0).totalReps));

        new Thread(this).start(); // handles the receive
        runTimer();
    }

    public void startWorkout(View view) { //begin timer and waiting for connection
        Button d = (Button) view.findViewById(R.id.startWorkout);
        if (running) {
            d.setText("Start!");
            d.setBackgroundColor(Color.GREEN);
            running = false;
        } else {
            d.setText("Stop!");
            d.setBackgroundColor(Color.RED);
            running = true;
        }

        Log.d("DEBUG", "WAHADDIDH");

        //d.setBackgroundColor(Color.RED);
        //view.invalidate();
        d.invalidate();

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
                /*new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if(dos != null)
                                dos.write("Bicep Curls".getBytes("UTF-8"));
                        }
                        catch (IOException d){;}
                    }
                }).start();*/

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
        try {
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(OptionsActivity.IPAddress, 11000), 500); //see if this works?
            dos = new DataOutputStream(clientSocket.getOutputStream());
            dis = new DataInputStream(clientSocket.getInputStream());
            //dos.write("Bicep Curls".getBytes("UTF-8"));
        } catch (IOException ie) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WorkoutActivity.this, "Unable to connect! Check IP address and try again!", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }
        byte[] input = new byte[100];
        String resp = "";
        try {
            for(int i = 0; i < currWorkout.exercises.size(); i++) {
                //send workout
                currExercise = currWorkout.exercises.elementAt(i);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        TextView tv3 = (TextView) findViewById(R.id.textView9);
                        tv3.setText(currExercise.name);
                        TextView tv4 = (TextView) findViewById(R.id.textView6);
                        tv4.setText(String.valueOf(currExercise.totalReps));
                        TextView tv5 = (TextView) findViewById(R.id.textView10);
                        tv5.setText(String.valueOf(currExercise.totalSets));
                    }
                });
                dos.write(currWorkout.exercises.elementAt(i).name.getBytes()); //send name of workout
                dos.write("start".getBytes());
                Log.d("DEBUG", "DOING WORKOUT: " + currExercise.name);
                //int rd = dis.read(input); //wait for OK or something

                currSet = currExercise.totalSets;
                currReps = currExercise.totalReps;

                while ( currSet > 0) { //while not yet zero
                    int rd = dis.read(input);
                    if (!running)
                        continue; //ignore the input here!
                    resp = "";
                    for (int j = 0; j < rd; j++) {
                        resp += (char) input[j];
                    }
                    Log.d("DEBUG", "RECV: " + resp);
                    if (resp.equalsIgnoreCase("ERROR") || resp.equalsIgnoreCase("INCORRECT")) {
                        //handle it
                        currWorkout.exercises.elementAt(i).errors++;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                View view = (View) findViewById(R.id.activity_workout);
                                TextView error = (TextView) findViewById(R.id.textView8);
                                view.setBackgroundColor(getResources().getColor(R.color.DarkRed));
                                error.setText(String.valueOf(Integer.parseInt(error.getText().toString()) + 1));
                            }
                        });
                        audiofiles[TRY_AGAIN].start(); //play try again
                    } else if (resp.equalsIgnoreCase("CORRECT")) { //increment counter
                        currReps--; //decrement
                        //play sound here
                        if (((int) (Math.random() * 2)) == 0)
                            audiofiles[CORRECT].start();
                        else
                            audiofiles[GOOD_JOB].start();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(currReps <= 0) { //reset the reps and currSet
                                    currSet--;
                                    if(currSet != 0)
                                        currReps = currExercise.totalReps;
                                    Log.d("DEBUG", "NEW SET, GOING AT IT");
                                }
                                View view = (View) findViewById(R.id.activity_workout);
                                view.setBackgroundColor(getResources().getColor(R.color.LightBlue));
                                TextView tv = (TextView) findViewById(R.id.textView10);
                                tv.setText(String.valueOf(currSet));
                                TextView tv2 = (TextView) findViewById(R.id.textView6);
                                tv2.setText(String.valueOf(currReps));
                            }
                        }); //I'm hoping runonUIthread executes before while loop continues
                    }
                }
            }
            dos.write("stop".getBytes());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(WorkoutActivity.this, "Finished workout! Exit and return to menu!", Toast.LENGTH_LONG).show();
                    View view = (View) findViewById(R.id.activity_workout);
                    view.setBackgroundColor(Color.parseColor("#aaaaaa"));
                    TextView tv = (TextView) findViewById(R.id.textView10);
                    tv.setText(String.valueOf(0));
                    TextView tv2 = (TextView) findViewById(R.id.textView6);
                    tv2.setText(String.valueOf(0));
                    //eventually want to try to reconnect!
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
}
