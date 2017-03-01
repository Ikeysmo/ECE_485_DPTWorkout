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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;

/*
Description: OptionsActivity is for user to be able to select what options he/she wants and configure IP address
Author: Isaiah Smoak
 */
public class OptionsActivity extends AppCompatActivity {
    public static String IPAddress = "192.168.1.117";
    public static int port = 11000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(IPAddress != null){ //if IP address already exists, copy it to textview
            EditText ed = (EditText) findViewById(R.id.ipAddressField);
            ed.setText(IPAddress);
        }
        else
            OptionsActivity.IPAddress = "192.168.1.117"; //default value --> this assumes MenuActivity ran and no file was found!
    }

    /*Sets the IP address upon Save button being pressed!   */
    public void setIPAddress(View view){
        EditText ed = (EditText) findViewById(R.id.ipAddressField);
        IPAddress = ed.getText().toString();
        Toast.makeText(this, "Saved IP Address!", Toast.LENGTH_SHORT).show();
        //save the file!!
        try {
            FileOutputStream fos = openFileOutput("config.txt", MODE_PRIVATE);
            fos.write(IPAddress.getBytes());
        }
        catch (Exception d){
            d.printStackTrace();
        }
    }

}
