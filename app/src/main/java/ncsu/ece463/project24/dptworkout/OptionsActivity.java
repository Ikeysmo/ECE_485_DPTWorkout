package ncsu.ece463.project24.dptworkout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.util.Date;

/*
Description: OptionsActivity is for user to be able to select what options he/she wants and configure IP address
Author: Isaiah Smoak
 */
public class OptionsActivity extends AppCompatActivity {
    //public static String IPAddress = Config.IP_ADDRESS;
    public static int port = 11000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Settings");
        if(Config.IP_ADDRESS != null){ //if IP address already exists, copy it to textview
            EditText ed = (EditText) findViewById(R.id.ipAddressField);
            ed.setText(Config.IP_ADDRESS);

        }
        else { //default, if Config is empty!
            Config.IP_ADDRESS = Config.DEFAULT_IP_ADDRESS; //default value --> this assumes MenuActivity ran and no file was found!
            EditText ed = (EditText) findViewById(R.id.ipAddressField);
            ed.setText(Config.IP_ADDRESS);
        }
    }

    /*Sets the IP address upon Save button being pressed!   */
    public void saveSettings(View view){
        EditText ed = (EditText) findViewById(R.id.ipAddressField);
        Config.IP_ADDRESS = ed.getText().toString();
        Config.saveSettings(this);
        Toast.makeText(this, "Saved IP Address!", Toast.LENGTH_SHORT).show();
        //save the file!!
    }

    public void toggleAudio(View view){
        ToggleButton tgbutt = (ToggleButton) findViewById(R.id.toggleButton);
        if(tgbutt.getText().toString().equalsIgnoreCase("ON")) {
            Config.audio_enabled = true;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Set to on", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else
            Config.audio_enabled = false;
    }

    public void exportWorkout(View view){
        Button exportButton = (Button) findViewById(R.id.button3);
        exportButton.setClickable(false);
        exportButton.setText("Exporting...");
        Log.d("Debug", "exporting to: " + Config.IP_ADDRESS + " at port 11001");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    String buildCSV = "";
                    Workout[] allWorkouts = Workout.loadWorkouts(getApplicationContext());
                    String date = DateFormat.getInstance().format(new Date());
                    date = date.substring(0, date.indexOf(" "));
                    date = date.replace('/', '_');
                    System.out.println(date);
                    buildCSV += "Workouts_"+date +".csv" +"\r\r";
                    buildCSV += buildCSVTitle();
                    for(Workout wk : allWorkouts){
                        buildCSV += buildCSVWorkout(wk);
                    }
                    //send it out
                    Socket exportSocket = new Socket();
                    exportSocket.connect(new InetSocketAddress(Config.IP_ADDRESS, 11001), 1000);
                    DataOutputStream dos = new DataOutputStream(exportSocket.getOutputStream());
                    dos.write(buildCSV.getBytes());
                    dos.close();
                }
                catch (IOException ioe){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Unable to export to server!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        exportButton.setClickable(true);
        exportButton.setText("Export Workout");

    }

    private String buildCSVTitle() {

        return "Title, Description, Date/Time, Status, Total Errors, Time to Complete\n";
    }

    public String buildCSVWorkout(Workout wk){
        String csv = "";
        csv+= wk.title + ",";
        csv+= wk.description + ",";
        csv +=  DateFormat.getInstance().format(wk.date) +",";
        csv += (wk.complete ? "Done": "N/A") + ",";
        csv+= wk.totalErrors + ",";
        csv+= (wk.totaltime/60) + " minutes " + (wk.totaltime%60) + " seconds\n";
        return csv;
    }
}
