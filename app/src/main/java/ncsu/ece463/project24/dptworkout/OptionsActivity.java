package ncsu.ece463.project24.dptworkout;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;

/*
Description: OptionsActivity is for user to be able to select what options he/she wants and configure IP address
Author: Isaiah Smoak
 */
public class OptionsActivity extends AppCompatActivity {
    public static String IPAddress;
    public static int port = 11000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        IPAddress = Config.IP_ADDRESS;
        if(IPAddress != null){ //if IP address already exists, copy it to textview
            EditText ed = (EditText) findViewById(R.id.ipAddressField);
            ed.setText(IPAddress);

        }
        else { //default, if Config is empty!
            OptionsActivity.IPAddress = Config.DEFAULT_IP_ADDRESS; //default value --> this assumes MenuActivity ran and no file was found!
            EditText ed = (EditText) findViewById(R.id.ipAddressField);
            ed.setText(Config.DEFAULT_IP_ADDRESS);
        }
    }

    /*Sets the IP address upon Save button being pressed!   */
    public void saveSettings(View view){
        EditText ed = (EditText) findViewById(R.id.ipAddressField);
        IPAddress = ed.getText().toString();
        Config.IP_ADDRESS = IPAddress;
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

}
