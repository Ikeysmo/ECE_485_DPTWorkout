package ncsu.ece463.project24.dptworkout;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
    //private boolean bothPadsOnline = false;
    private Context context;
    public BLE_Callback(Context context, boolean isRight){
        padOrientation = isRight;
        this.context = context;
    }

    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState){
        String intentAction;
        String TAG = "HELLO";
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            //mConnectionState = STATE_CONNECTED;
            if(padOrientation == LEFTPAD) {
                lpadConnected = true;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TextView tv = (TextView) ((Activity) context).findViewById(R.id.lpadstatus);
                        tv.setText("Connected");
                    }
                });
            }
            else if(padOrientation == RIGHTPAD) {
                rpadConnected = true;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        TextView tv = (TextView) ((Activity) context).findViewById(R.id.rpadstatus);
                        tv.setText("Connected");
                    }
                });
            }
            Log.i(TAG, "Connected to GATT server.");
            gatt.discoverServices();
        }
        else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                mConnectionState = STATE_DISCONNECTED;
            Log.i(TAG, "Disconnected from GATT server.");
            if(padOrientation == LEFTPAD) {
                lpadConnected = false;
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(!(lpadConnected && rpadConnected))
                            Toast.makeText(context, "Left Pad Disconnected!", Toast.LENGTH_SHORT).show();
                        TextView tv = (TextView) ((Activity) context).findViewById(R.id.lpadstatus);
                        tv.setText("Disconnected");
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
                        if(!(lpadConnected && rpadConnected))
                            Toast.makeText(context, "Right Pad Disconnected!", Toast.LENGTH_SHORT).show();
                        TextView tv = (TextView) ((Activity) context).findViewById(R.id.rpadstatus);
                        tv.setText("Disconnected");
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
        Log.d("DEBUG",  gatt.getDevice().getName() + "  GOT THIS: " + Arrays.toString(input));
        if(padOrientation == LEFTPAD){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView txt = (TextView)  ((Activity)context).findViewById(R.id.ble_angle_left);
                    txt.setText(String.valueOf(input[0]));
                }
            });
        }
        else if(padOrientation == RIGHTPAD){
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView txt = (TextView)  ((Activity)context).findViewById(R.id.ble_angle_right);
                    txt.setText(String.valueOf(input[0]));
                }
            });
        }


    }
}
