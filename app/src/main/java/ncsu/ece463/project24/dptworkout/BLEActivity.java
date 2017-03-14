package ncsu.ece463.project24.dptworkout;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class BLEActivity extends AppCompatActivity {
    private BluetoothAdapter mBluetoothAdapter;
    private boolean connected;


    //private BluetoothGattCallback mgattcallback = new BLE_Callback();
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
        BluetoothGatt dd = bd.connectGatt(this, false, new BLE_Callback(this, BLE_Callback.RIGHTPAD));
        BluetoothDevice bd2 = mBluetoothAdapter.getRemoteDevice("DE:9F:F9:F2:2C:80");
        bd2.connectGatt(this, false, new BLE_Callback(this, BLE_Callback.LEFTPAD));


    }

    @Override
    protected void onResume(){
        super.onResume();


    }


}
