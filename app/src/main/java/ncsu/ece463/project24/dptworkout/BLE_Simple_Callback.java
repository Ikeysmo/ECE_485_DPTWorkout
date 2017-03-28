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

public class BLE_Simple_Callback extends BluetoothGattCallback {
    public final static boolean LEFTPAD = false; //constant
    public final static boolean RIGHTPAD = true; //constant
    public  boolean padOrientation = LEFTPAD; //ID of current pad
    private static boolean lpadConnected = false;
    private static boolean rpadConnected = false;
    public static boolean isCalibrated = false;
    //private boolean bothPadsOnline = false;
    private Context context;


    public BLE_Simple_Callback(Context context, boolean isRight){
        padOrientation = isRight;
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
                        TextView tv = (TextView) ((Activity) context).findViewById(R.id.lpadstatus);
                        tv.setTextColor(Color.RED);
                        tv.setText("Not connected!");
                        if(!(lpadConnected && rpadConnected))
                            Toast.makeText(context, "Left Pad Disconnected!", Toast.LENGTH_SHORT).show();
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
                        if (!(lpadConnected && rpadConnected)) {
                            TextView tv = (TextView) ((Activity) context).findViewById(R.id.rpadstatus);
                            tv.setTextColor(Color.RED);
                            tv.setText("Not connected!");
                            Toast.makeText(context, "Right Pad Disconnected!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            if(!gatt.connect()) {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Connection Reattempt failed!", Toast.LENGTH_SHORT).show();
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

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Left Pad connected!", Toast.LENGTH_SHORT).show();
                            TextView tv = (TextView) ((Activity) context).findViewById(R.id.lpadstatus);
                            tv.setTextColor(Color.GREEN);
                            tv.setText("Connected!");
                        }
                    });


                }
                else if(padOrientation == RIGHTPAD) {
                    rpadConnected = true;
                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Right Pad connected!", Toast.LENGTH_SHORT).show();
                            TextView tv = (TextView) ((Activity) context).findViewById(R.id.rpadstatus);
                            tv.setTextColor(Color.GREEN);
                            tv.setText("Connected!");
                        }
                    });


                }
                if(lpadConnected && rpadConnected){

                    ((Activity)context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "Both are connected!", Toast.LENGTH_SHORT).show();

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
        final byte[] input = characteristic.getValue();
        if(padOrientation == LEFTPAD){
            Log.d("DEBUG",  gatt.getDevice().getName() + "  GOT THIS: " + Arrays.toString(input) + " which is " + new String(input));
            //check if correct somewhere else!

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView txt = (TextView)  ((Activity)context).findViewById(R.id.ble_angle_left);
                    txt.setText(String.valueOf(input[0]));
                }
            });
        }
        else if(padOrientation == RIGHTPAD){
            //if(new String(input).equalsIgnoreCase("Correct"))

            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView txt = (TextView)  ((Activity)context).findViewById(R.id.ble_angle_right);
                    txt.setText(String.valueOf(input[0]));
                }
            });
        }

    }

    public synchronized void readByte(){
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
