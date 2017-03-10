package ncsu.ece463.project24.dptworkout;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;

public class BLEActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean connected;

    private class mBluetoothCallBack extends BluetoothGattCallback{
        @Override
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
//                broadcastUpdate(intentAction);
            }
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
            byte[] input = characteristic.getValue();
            Log.d("DEBUG",  gatt.getDevice().getName() + "  GOT THIS: " + Arrays.toString(input));
        }
    }
    private BluetoothGattCallback mgattcallback = new mBluetoothCallBack();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble);

        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported!", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();


        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "BLE not supported!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        BluetoothDevice bd = mBluetoothAdapter.getRemoteDevice("D5:6B:4F:85:08:2E");
        bd.connectGatt(this, false, new mBluetoothCallBack());
        BluetoothDevice bd2 = mBluetoothAdapter.getRemoteDevice("DE:9F:F9:F2:2C:80");
        bd2.connectGatt(this, false, new mBluetoothCallBack());

    }

    @Override
    protected void onResume(){
        super.onResume();

    }


}
