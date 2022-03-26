
package com.example.a12_bt;

import static com.example.a12_bt.R.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.util.UUID;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.a12_bt.databinding.ActivityMainBinding;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;

/**
 * Authors Giantte, Jean, Sam
 */
public class MainActivity extends AppCompatActivity {
    private static InputStream cv_is = null;
    private static OutputStream cv_os = null;
    private ActivityMainBinding binding;
    // BT Variables
    private final String CV_ROBOTNAME = "EV3A";
    private BluetoothAdapter cv_btInterface = null;
    private Set<BluetoothDevice> cv_pairedDevices = null;
    private BluetoothDevice cv_btDevice = null;
    private BluetoothSocket cv_btSocket = null;
    //initialize speed at 50
    static int speed = 50;
    //initialize direction as forward (1)
    static int direction = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        

        // Need grant permission once per install
        cpf_checkBTPermissions();
        binding.floatingActionButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        Context ctx = MainActivity.this;
                        Toast.makeText(ctx, string.FABToast, Toast.LENGTH_LONG).show();

                    }
                });
        binding.button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        binding.vvTvOut1.setText(string.COSC426Greeting);
                    }
                });

    }

    // Overriding onCreateoptionMenu() to make Option menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflating menu by overriding inflate() method of MenuInflater class.
        //Inflating here means parsing layout XML to views.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    //Overriding onOptionsItemSelected to perform event on menu items
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case id.firstMenuOption:
                cpf_requestBTPermissions();
                return true;
            case id.secondMenuOption:
                cv_btDevice = cpf_locateInPairedBTList(CV_ROBOTNAME);
                return true;
            case id.thirdMenuOption:
                cpf_connectToEV3(cv_btDevice);
                return true;
            case id.fourthMenuOption: moveForward();
                return true;
            case id.fifthMenuOption: cpf_EV3PlayTone();
                return true;
            case id.sixthMenuOption: cpf_disconnFromEV3(cv_btDevice);
                return true;
        }
        binding.vvTvOut1.setText(menuItem.getTitle() + " Selected");
        return true;
    }
    private void cpf_checkBTPermissions() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            binding.vvTvOut1.setText("BLUETOOTH_SCAN already granted.\n");
        }
        else {
            binding.vvTvOut1.setText("BLUETOOTH_SCAN NOT granted.\n");
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            binding.vvTvOut2.setText("BLUETOOTH_CONNECT NOT granted.\n");
        }
        else {
            binding.vvTvOut2.setText("BLUETOOTH_CONNECT already granted.\n");
        }
    }

    // https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
    private void cpf_requestBTPermissions() {
        // We can give any value but unique for each permission.
        final int BLUETOOTH_SCAN_CODE = 100;
        final int BLUETOOTH_CONNECT_CODE = 101;

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    BLUETOOTH_SCAN_CODE);
        }
        else {
            Toast.makeText(MainActivity.this,
                    "BLUETOOTH_SCAN already granted", Toast.LENGTH_SHORT) .show();
        }

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    BLUETOOTH_CONNECT_CODE);
        }
        else {
            Toast.makeText(MainActivity.this,
                    "BLUETOOTH_CONNECT already granted", Toast.LENGTH_SHORT) .show();
        }
    }

    // Modify from chap14, pp390 findRobot()
    @SuppressLint("MissingPermission")
    private BluetoothDevice cpf_locateInPairedBTList(String name) {
        BluetoothDevice lv_bd = null;
        try {
            cv_btInterface = BluetoothAdapter.getDefaultAdapter();
            cv_pairedDevices = cv_btInterface.getBondedDevices();
            Iterator<BluetoothDevice> lv_it = cv_pairedDevices.iterator();
            while (lv_it.hasNext())  {
                lv_bd = lv_it.next();
                if (lv_bd.getName().equalsIgnoreCase(name)) {
                    binding.vvTvOut1.setText(name + " is in paired list");
                    return lv_bd;
                }
            }
            binding.vvTvOut1.setText(name + " is NOT in paired list");
        }
        catch (Exception e) {
            binding.vvTvOut1.setText("Failed in findRobot() " + e.getMessage());
        }
        return null;
    }

    // Modify frmo chap14, pp391 connectToRobot()
    @SuppressLint("MissingPermission")
    private void cpf_connectToEV3(BluetoothDevice bd) {
        try  {
            cv_btSocket = bd.createRfcommSocketToServiceRecord
                    (UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            cv_btSocket.connect();
            binding.vvTvOut2.setText("Connect to " + bd.getName() + " at " + bd.getAddress());
            cv_is = cv_btSocket.getInputStream();
            cv_os = cv_btSocket.getOutputStream();
        }
        catch (Exception e) {
            binding.vvTvOut2.setText("Error interacting with remote device [" +
                    e.getMessage() + "]");
        }
    }

    //disconnect from EV3
    @SuppressLint("MissingPermission")
    private void cpf_disconnFromEV3(BluetoothDevice bd) {
        try {
            cv_btSocket.close();
            cv_is.close();
            cv_os.close();
            binding.vvTvOut2.setText(bd.getName() + " is disconnect " );
        } catch (Exception e) {
            binding.vvTvOut2.setText("Error in disconnect -> " + e.getMessage());
        }
    }

    //set the motor direction forward/backward
    private static void setDirection() {
        try {
            byte[] buffer = new byte[11];       // 0x12 command length

            buffer[0] = (byte) (11-2);
            buffer[1] = 0;
            buffer[2] = 34;
            buffer[3] = 12;
            buffer[4] = (byte) 0x80;
            buffer[5] = 0;
            buffer[6] = 0;
            buffer[7] = (byte) 0xA7;
            buffer[8] = 0;
            buffer[9] = 0x06;
            buffer[10] = (byte) direction;

            cv_os.write(buffer);
            cv_os.flush();
        }
        catch (Exception e) {
            //binding.vvTvOut1.setText("Error in Back(" + e.getMessage() + ")");
        }
    }

    //move both wheels
    static void moveForward() {
        setDirection();
        try {
            byte[] buffer = new byte[20];       // 0x12 command length
            buffer[0] = (byte) (20-2);
            buffer[1] = 0;
            buffer[2] = 34;
            buffer[3] = 12;
            buffer[4] = (byte) 0x80;
            buffer[5] = 0;
            buffer[6] = 0;
            buffer[7] = (byte) 0xae;
            buffer[8] = 0;
            buffer[9] = (byte) 0x06;
            buffer[10] = (byte) 0x81;
            buffer[11] = (byte) speed;
            buffer[12] = 0;
            buffer[13] = (byte) 0x82;
            buffer[14] = (byte) 0xB4;
            buffer[15] = (byte) 0x00;
            buffer[16] = (byte) 0x82;
            buffer[17] = (byte) 0xB4;
            buffer[18] = (byte) 0x00;

            cv_os.write(buffer);
            cv_os.flush();
        }
        catch (Exception e) {
            //binding.vvTvOut1.setText("Error in MoveForward(" + e.getMessage() + ")");
        }
    }


    //move the right wheel only
    static void turnLeft() {
        setDirection();
        try {
            byte[] buffer = new byte[20];       // 0x12 command length

            buffer[0] = (byte) (20-2);
            buffer[1] = 0;
            buffer[2] = 34;
            buffer[3] = 12;
            buffer[4] = (byte) 0x80;
            buffer[5] = 0;
            buffer[6] = 0;
            buffer[7] = (byte) 0xae;
            buffer[8] = 0;
            buffer[9] = (byte) 0x02;
            buffer[10] = (byte) 0x81;
            buffer[11] = (byte) speed;
            buffer[12] = 0;
            buffer[13] = (byte) 0x82;
            buffer[14] = (byte) 0x5A;
            buffer[15] = (byte) 0x00;
            buffer[16] = (byte) 0x82;
            buffer[17] = (byte) 0x5A;
            buffer[18] = (byte) 0x00;

            cv_os.write(buffer);
            cv_os.flush();
        }
        catch (Exception e) {
            //binding.vvTvOut1.setText("Error in TurnLeft(" + e.getMessage() + ")");
        }
    }

    //move the left wheel only
    static void turnRight() {
        setDirection();
        try {
            byte[] buffer = new byte[20];       // 0x12 command length
            buffer[0] = (byte) (20-2);
            buffer[1] = 0;
            buffer[2] = 34;
            buffer[3] = 12;
            buffer[4] = (byte) 0x80;
            buffer[5] = 0;
            buffer[6] = 0;
            buffer[7] = (byte) 0xae;
            buffer[8] = 0;
            buffer[9] = 0x04;
            buffer[10] = (byte) 0x81;
            buffer[11] = (byte) speed;
            buffer[12] = 0;
            buffer[13] = (byte) 0x82;
            buffer[14] = (byte) 0x5A;
            buffer[15] = (byte) 0x00;
            buffer[16] = (byte) 0x82;
            buffer[17] = (byte) 0x5A;
            buffer[18] = (byte) 0x00;

            cv_os.write(buffer);
            cv_os.flush();
        }
        catch (Exception e) {
            //binding.vvTvOut1.setText("Error in TurnRight(" + e.getMessage() + ")");
        }
    }


    //play tone on EV3
    private void cpf_EV3PlayTone(){
        try {
            byte[] buffer = new byte[17];       // 0x12  command length

            buffer[0] = (byte) (17-2);
            buffer[1] = 0;

            buffer[2] = 34;
            buffer[3] = 12;

            buffer[4] = (byte) 0x80;

            buffer[5] = 0;
            buffer[6] = 0;
            buffer[7] = (byte) 0x94;
            buffer[8] = (byte) 0x01;
            buffer[9] = (byte) 0x81;
            buffer[10] = (byte) 0x02;
            buffer[11] = (byte) 0x82;
            buffer[12] = (byte) 0xE8;
            buffer[13] = (byte) 0x03;
            buffer[14] = (byte) 0x82;
            buffer[15] = (byte) 0xE8;
            buffer[16] = 3;

            cv_os.write(buffer);
            cv_os.flush();
        }
        catch (Exception e) {
            binding.vvTvOut1.setText("Error in PlayTone(" + e.getMessage() + ")");
        }
    }

}