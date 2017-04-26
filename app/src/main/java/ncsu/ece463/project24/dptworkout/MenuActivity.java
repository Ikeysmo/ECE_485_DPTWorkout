package ncsu.ece463.project24.dptworkout;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Date;
import java.util.UUID;

/*
Description: This activity provides interface to reaching other activities
Author: Isaiah Smoak
 */
public class MenuActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    public static String tempHolder; //here for debugging purposes only
    public static BluetoothGatt leftpad;
    public static BluetoothGatt rightpad;
    private BluetoothAdapter mBluetoothAdapter;
    public int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setTitle("DPT Workout");
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 4);
        try{     //try parsing the Options/IP address if available
            Config.IP_ADDRESS = getIPFromConfig();
        }
       catch (Exception d){Log.d("DEBUG", "Options not found!");}
        //create workout w/ random date/time stamp
        try{
            Workout.loadCustomWorkouts(this);
        }
        catch (IOException fe){
            //if can't load custom, make custom and save
            Workout ws = new Workout("Intro 101", "Workout to test things", new Exercise[]{
                    new Exercise("Squats", "Lower body until legs are parallel to ground", 1, 3),
                    new Exercise("Pushups", "Lay flat on ground. Push until arms are straight, and lower body until arms make right angle. Repeat", 2, 4, true)
            }, new Date().getTime());

            Workout wx = new Workout("Pushup Extreme", "Test Pushups", new Exercise[]{
                    new Exercise("Pushups", "Lay flat on ground. Push until arms are straight, and lower body until arms make right angle. Repeat", 1, 30, true)
            }, new Date().getTime());

            Workout.addCustomWorkout(ws);
            Workout.addCustomWorkout(wx);

        }
        Log.d("DEBUG", "WONDER WONDER");
        //used for Workout Activity testing reading JSON from string that would be stored in a file,

        //get all the names of all the workouts that exist and add to spinner!
        Spinner sp = (Spinner) findViewById(R.id.selectWorkout);
        sp.setAdapter(new SpinAdapter(this, R.layout.support_simple_spinner_dropdown_item, Workout.getCustomWorkouts()));
        sp.setOnItemSelectedListener(this);


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
        Log.d("DEBUG", "THIS WORK?");
        BluetoothDevice bd = mBluetoothAdapter.getRemoteDevice("D5:6B:4F:85:08:2E"); //default left?
        BluetoothDevice bd2 = mBluetoothAdapter.getRemoteDevice("DE:9F:F9:F2:2C:80");
//
        if(MenuActivity.leftpad == null && MenuActivity.rightpad == null){
            bd.connectGatt(this, false, new BLE_Callback(this));
            bd2.connectGatt(this,false, new BLE_Callback(this));
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_instructions, menu);
        return true;
    }

    public void workoutLog(View view){ //goes to LOG_ACTIVITY
        Intent intent = new Intent(this, LogActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        Intent intent;
        switch(item.getItemId()) {
            case R.id.action_settings:
                intent = new Intent(this, OptionsActivity.class);
                startActivity(intent);
                break;
            case R.id.aaction_ble:
                if(leftpad != null)
                    leftpad.disconnect();
                if(rightpad != null)
                    rightpad.disconnect();
                intent = new Intent(this, BLEActivity.class);
                startActivity(intent);
                break;
            case R.id.aaction_about:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void editWorkout(View view){
        Intent intent = new Intent(this, CustomizeActivity.class);
        startActivity(intent);
    }
    public void openContinueWorkout(View view){ //goes to WORKOUT_ACTVITY
        final Spinner sp = (Spinner) findViewById(R.id.selectWorkout);
        Workout.currentWorkout = (Workout) sp.getSelectedItem();
        selectedPosition = sp.getSelectedItemPosition();
        if(padExerciseFound(Workout.currentWorkout) && !BLE_Callback.isBothConnected()) //if pads are needed in this workout
        {
            AlertDialog.Builder build = new AlertDialog.Builder(this).setTitle("Continue?");
            build.setMessage("BLE Pads are needed for this workout. However they are not connected, do you want to continue without them (Skip pad enabled exercises)?");
            build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d("Error", "he said yeah");
                    Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class); //was workout of course
                    startActivity(intent);
                }
            });
            build.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.d("Error", "no he didn't");
                }
            });
            AlertDialog alert = build.create();
            alert.show();
        }
        else{
            Intent intent = new Intent(getApplicationContext(), WorkoutActivity.class);
            startActivity(intent);
        }


    }

    private boolean padExerciseFound(Workout currentWorkout) {
        for (Exercise wk : currentWorkout.exercises){
            if(wk.padEnable)
                return true;
        }
        return  false;
    }


    public void gotoInstructions(View view){
        Intent intent = new Intent(this, InstructionsActivity.class);
        startActivity(intent);
    }

    /* Reads IP address from Config file... assumes only IP address is there for now */
    public String getIPFromConfig() throws IOException, ClassNotFoundException {
        Log.d("DEBUG", "Trying to read the file!");
        Config.loadSettings(this);
        Log.d("DEBUG", "RESTORED THE SETTINGS!");
        //return rcv;
        return Config.IP_ADDRESS;
    }

    @Override
    protected void onResume(){
        super.onResume();
        Spinner sp = (Spinner) findViewById(R.id.selectWorkout);
        sp.setAdapter(new SpinAdapter(this, R.layout.support_simple_spinner_dropdown_item, Workout.getCustomWorkouts()));
        sp.setSelection(selectedPosition);
        Log.d("ERROR", "HEY");

        /*if(rightpad == null) {
            //bd.getName().toString() != null &&
            if( bd.getName().toString().contains("_R"))
                rightpad = bd.connectGatt(this, false, new BLE_Callback(this, BLE_Callback.RIGHTPAD));
            else if(bd2 != null && bd2.getName().toString().contains("_R"))
                rightpad = bd2.connectGatt(this, false, new BLE_Callback(this, BLE_Callback.RIGHTPAD));
        }
        //the real OG left pad
        if(leftpad == null) {
            // MenuActivity.leftpad = bd2.connectGatt(this, false, new BLE_Simple_Callback(this, BLE_Callback.LEFTPAD));
            if(bd.getName() != null && bd.getName().toString().contains("_L"))
                leftpad = bd.connectGatt(this, false, new BLE_Callback(this, BLE_Callback.LEFTPAD));
            else if(bd2.getName() != null && bd2.getName().toString().contains("_L"))
                leftpad = bd2.connectGatt(this, false, new BLE_Callback(this, BLE_Callback.LEFTPAD));
        }
        */

//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                BluetoothGattService bgs = MenuActivity.leftpad.getService(UUID.fromString("713d0000-503e-4c75-ba94-3148f18d941e"));
//                if(bgs == null)
//                {
//                    handler.postDelayed(this,1000);
//                    return;
//                }
//                BluetoothGattCharacteristic bgc = bgs.getCharacteristic(UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e"));
//                bgc.setValue("min_angle");
//                //bgc.setValue(new byte[]{(byte) 0xFE}); //if this is to calibrate min angle
//                MenuActivity.leftpad.writeCharacteristic(bgc);
//                handler.postDelayed(this, 1000);
//            }
//        }, 3000);
    }
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //do stuff here
        Workout myman = (Workout) adapterView.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private class SpinAdapter extends ArrayAdapter<Workout>{
        private Context context;
        private Workout[] list;

        public SpinAdapter(Context context, int textViewResourseId, Workout[] values){
            super(context, textViewResourseId, values);
            this.context = context;
            this.list = values;
        }

        public int getCount(){
            return list.length;
        }

        public Workout getItem(int position){
            return list[position];
        }

        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            TextView label = new TextView(context);
            label.setTextColor(Color.LTGRAY);
            label.setTextSize(20);
            label.setText(list[position].title);
            return label;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent){
            TextView label = new TextView(context);
            label.setTextColor(Color.BLACK);
            label.setTextSize(20);
            label.setText(list[position].title);
            return label;
        }
    }

//    public void sendHello(View view){
//        //BluetoothGattCharacteristic bgc = new BluetoothGattCharacteristic(UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e"), BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,BluetoothGattCharacteristic.PERMISSION_WRITE);
//        //bgc = BluetoothGattService.getCharacteristic();
//        BluetoothGattService bgs = leftpad.getService(UUID.fromString("713d0000-503e-4c75-ba94-3148f18d941e"));
//        BluetoothGattCharacteristic bgc = bgs.getCharacteristic(UUID.fromString("713d0003-503e-4c75-ba94-3148f18d941e"));
//        bgc.setValue("Hey there");
//        leftpad.writeCharacteristic(bgc);
//        Log.d("DEBUG", "Sent it!");
//    }
}
