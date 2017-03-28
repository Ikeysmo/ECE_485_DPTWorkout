package ncsu.ece463.project24.dptworkout;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

public class CalibrateActivity extends AppCompatActivity implements Runnable{

    private boolean leftpadmin_calibrated;
    private boolean leftpadmax_calibrated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibrate);
        Button mbutton = (Button) findViewById(R.id.button5);
        mbutton.setClickable(false);
        new Thread(this).start();
    }

    @Override
    public void run() {
        try { //connect to server
            Socket clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(Config.IP_ADDRESS, 11000), 500); //see if this works?
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            String response = "";
            while(true){
                dos.write("Calibration".getBytes());
                //wait for the angles coming back... see if left arm or right arm approaches 90 deg or 0 deg
                while(true){
                    byte[] input = new byte[100];
                    for(int i = 0; i < input.length; i++){
                        input[i] = 0;
                    }
                    int readAmount = dis.read(input);
                    response = "";
                    //convert the bytes into a string
                    for (int j = 0; j < readAmount; j++) {
                        response += (char) input[j];
                    }
                    int countspaces = 0;
                    for(int i = 0; i < response.length(); i++){
                        if(response.charAt(i) == ' ')
                            countspaces++;
                    }
                    if(countspaces > 1)
                        continue;
                    Log.d("DEBUG", "total response: " + response + "\n");
                    int left_ang = 0;
                    if(response.contains("L"))
                    {
                        String left_angle  = response.substring(response.indexOf(" "), response.length()).trim();
                        left_ang = Integer.parseInt(left_angle);
                    }
                    //Log.d("DEBUG", "Recv: " + left_angle);
                    //Log.d("DEBUG", "Recv: " + right_angle);

                   // int right_ang = Integer.parseInt(right_angle);
                    if(left_ang < 95 && left_ang > 85){ //min angle
                        Log.d("DEBUG", "sending calibration for min");
                        leftpadmin_calibrated = true;
                        BluetoothGattService bgs = MenuActivity.leftpad.getService(UUID.fromString("713d0000-503e-4c75-ba94-3148f18d941e"));
                        BluetoothGattCharacteristic bgc = bgs.getCharacteristic(UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e"));
                        bgc.setValue(new byte[]{(byte) 0xfe}); //if this is to calibrate
                        MenuActivity.leftpad.writeCharacteristic(bgc);
                    }

                    if(left_ang < 185 && left_ang > 175){ //max angle
                        Log.d("DEBUG", "sending calibration for max");
                        leftpadmax_calibrated = true;
                        BluetoothGattService bgs = MenuActivity.leftpad.getService(UUID.fromString("713d0000-503e-4c75-ba94-3148f18d941e"));
                        BluetoothGattCharacteristic bgc = bgs.getCharacteristic(UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e"));
                        bgc.setValue(new byte[]{(byte) 0x1b}); //if this is to calibrate min angle
                        MenuActivity.leftpad.writeCharacteristic(bgc);
                    }
                    if(leftpadmax_calibrated && leftpadmin_calibrated){
                        Button mbutton = (Button) findViewById(R.id.button5);
                        mbutton.setClickable(true);
                    }
                }
                //leftpad ready to calibrate min angle


                //rightpad ready to calibrate


            }
        }
        catch (IOException ie){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CalibrateActivity.this, "Unable to connect! Check IP address and try again!", Toast.LENGTH_LONG).show();
                    }
                });
                return;
            }
    }

    public void gotoWorkout(View view){
        Intent intent = new Intent(this, WorkoutActivity.class);
        startActivity(intent);
    }
}
