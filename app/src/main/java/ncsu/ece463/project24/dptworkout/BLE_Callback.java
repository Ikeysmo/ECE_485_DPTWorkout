package ncsu.ece463.project24.dptworkout;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Ikeys on 3/13/2017.
 */

public class BLE_Callback extends BluetoothGattCallback {
    public final static boolean LEFTPAD = false; //constant
    public final static boolean RIGHTPAD = true; //constant
    public  boolean padOrientation = LEFTPAD; //ID of current pad
    private static boolean lpadConnected = false;
    private static boolean rpadConnected = false;
    public static boolean isCalibrated = false;
    public static byte[] ldata;
    public static byte[] rdata;
    //private boolean bothPadsOnline = false;
    private Context context;
    private volatile static boolean rpad_gotdata = false;
    private volatile static boolean lpad_gotdata = false;

    public BLE_Callback(Context context, boolean isRight){
        padOrientation = isRight;
        this.context = context;
    }

    public BLE_Callback(Context context){
        this.context = context;
    }
    public static boolean isBothConnected(){
        return rpadConnected && lpadConnected;
    }
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
        String intentAction;
        String TAG = "HELLO";
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            //mConnectionState = STATE_CONNECTED;

            Log.i(TAG, "Connected to GATT server.");
            gatt.discoverServices();
            String name = gatt.getDevice().getName();
            if(name != null){
                if(name.contains("_L") && MenuActivity.leftpad == null) {
                    MenuActivity.leftpad = gatt;
                    padOrientation = LEFTPAD;
                }
                else if(name.contains("_R") && MenuActivity.rightpad == null) {
                    MenuActivity.rightpad = gatt;
                    padOrientation = RIGHTPAD;
                }
            }
        }

        else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                mConnectionState = STATE_DISCONNECTED;
            Log.i(TAG, "Disconnected from GATT server.");
            isCalibrated = false; //reset calibration if it's needed
            if(padOrientation == LEFTPAD) {
                lpadConnected = false;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!(lpadConnected && rpadConnected))
                            Toast.makeText(context, "Left Pad Disconnected!", Toast.LENGTH_SHORT).show();
                        TextView tv = (TextView) ((Activity) context).findViewById(R.id.menu_pad_status);
                        tv.setText("Disconnected"); //can't run on this layout menu
                }
                });


                if(!gatt.connect()) {
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Connection Reattempt failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
            else if(padOrientation == RIGHTPAD) {
                rpadConnected = false;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!(lpadConnected && rpadConnected)){
                            Toast.makeText(context, "Right Pad Disconnected!", Toast.LENGTH_SHORT).show();
                        TextView tv = (TextView) ((Activity) context).findViewById(R.id.menu_pad_status);
                        tv.setText("Disconnected");}
                    }
                });
            }
//                broadcastUpdate(intentAction);
        }
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(lpadConnected && rpadConnected)
                    Toast.makeText(context, "Both pads connected!", Toast.LENGTH_SHORT).show();
            }
        });

    }
    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status){
        if(status == BluetoothGatt.GATT_SUCCESS){
            Log.d("DEBUG", "ON SERVICES DISCOVERED FOR " + gatt.getDevice().getName() + "\n\n");
            BluetoothGattService mserv = gatt.getService(UUID.fromString("713d0000-503e-4c75-ba94-3148f18d941e"));
            if(mserv != null)
            {
                BluetoothGattCharacteristic charserve = mserv.getCharacteristic(UUID.fromString("713d0002-503e-4c75-ba94-3148f18d941e"));
                gatt.setCharacteristicNotification(charserve, true);
                BluetoothGattDescriptor descriptor = charserve.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                gatt.writeDescriptor(descriptor);
                Log.d("DEBUG", "ENABLED NOTIFICATIONS FOR " + gatt.getDevice().getName());
                if(padOrientation == LEFTPAD) {
                    lpadConnected = true;
//                    TextView tv = (TextView) ((Activity) context).findViewById(R.id.menu_pad_status);
//                    tv.setText("Left Pad is Connected!");
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Left Pad connected!", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                else if(padOrientation == RIGHTPAD) {
                    rpadConnected = true;
//                    TextView tv = (TextView) ((Activity) context).findViewById(R.id.menu_pad_status);
//                    tv.setText("Right Pad is Connected!");
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Right Pad connected!", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
                if(lpadConnected && rpadConnected){
                    final TextView tv = (TextView) ((Activity) context).findViewById(R.id.menu_pad_status);
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Left Pad connected!", Toast.LENGTH_SHORT).show();
                            tv.setText("Both Pads are Connected!");
                            tv.setTextColor(Color.parseColor("#008F00"));
                        }
                    });

                }

            }
        }
    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status){
        Log.d("DEBUG", "GOT SOMETHING");
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
        //Log.d("DEBUG", "DO SOMETHING");
        byte[] input = characteristic.getValue();
        if(padOrientation == LEFTPAD){
            Log.d("DEBUG",  gatt.getDevice().getName() + "  GOT THIS: " + Arrays.toString(input) + " which is " + new String(input));
            ldata = input;
            lpad_gotdata = true;
            //check if correct somewhere else!

//            ((Activity)context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    TextView txt = (TextView)  ((Activity)context).findViewById(R.id.ble_angle_left);
//                    txt.setText(String.valueOf(input[0]));
//                }
//            });
        }
        else if(padOrientation == RIGHTPAD){
            rdata = input;
            rpad_gotdata = true;
            //if(new String(input).equalsIgnoreCase("Correct"))

//            ((Activity)context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //TextView txt = (TextView)  ((Activity)context).findViewById(R.id.ble_angle_right);
//                    //txt.setText(String.valueOf(input[0]));
//                }
//            });
        }

    }

    public synchronized void readByte(){
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitforpads() throws IOException{
        Log.d("DEBUG", "Waiting for pads!");
        if(!lpadConnected) {
            Log.d("DEBUG", "Pad isn't connected!");
            throw new IOException("PAD isn't connected!");
        }
        if(!rpadConnected)
            Log.d("DEBUG", "Pad isn't connected!");
        //throw new IOException("PAD isn't connected!");
        while(!lpad_gotdata && !rpad_gotdata ){
            //Log.d("DEBUG", "blah blah");
            ;} //stall here for a while

        lpad_gotdata = false;
        rpad_gotdata = false;

    }
}
