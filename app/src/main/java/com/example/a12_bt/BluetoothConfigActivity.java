package com.example.a12_bt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.Image;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.a12_bt.databinding.ActivityBluetoothConfigBinding;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

/**
 * Authors Giantte, Jean, Sam
 */
public class BluetoothConfigActivity extends AppCompatActivity {
    private static InputStream cv_is = null;
    private static OutputStream cv_os = null;
    private ActivityBluetoothConfigBinding binding;
    // BT Variables
    private final String CV_ROBOTNAME = "EV3A";
    private BluetoothAdapter cv_btInterface = null;
    private Set<BluetoothDevice> cv_pairedDevices = null;
    private BluetoothDevice cv_btDevice = null;
    private BluetoothSocket cv_btSocket = null;
    static boolean connectionStatus = false;
    //initialize speed at 0
    static int speed = 0;
    //initialize direction as forward (1)
    static int direction = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBluetoothConfigBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        // Need grant permission once per install
        cpf_checkBTPermissions();
        binding.grantPermissionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpf_requestBTPermissions();
            }
        });
        binding.btListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cv_btDevice = cpf_locateInPairedBTList(CV_ROBOTNAME);
            }
        });
        binding.connectButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpf_connectToEV3(cv_btDevice);
            }
        }));
        binding.disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cpf_disconnFromEV3(cv_btDevice);
            }
        });
    }
    private void cpf_checkBTPermissions() {
        if (ContextCompat.checkSelfPermission(BluetoothConfigActivity.this,
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(BluetoothConfigActivity.this, "BLUETOOTH_SCAN already granted.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(BluetoothConfigActivity.this, "BLUETOOTH_SCAN not granted.", Toast.LENGTH_LONG).show();
        }
        if (ContextCompat.checkSelfPermission(BluetoothConfigActivity.this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(BluetoothConfigActivity.this, "BLUETOOTH_CONNECT NOT granted.", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(BluetoothConfigActivity.this, "BLUETOOTH_CONNECT already granted.", Toast.LENGTH_LONG).show();
        }
    }

    // https://www.geeksforgeeks.org/android-how-to-request-permissions-in-android-application/
    private void cpf_requestBTPermissions() {
        // We can give any value but unique for each permission.
        final int BLUETOOTH_SCAN_CODE = 100;
        final int BLUETOOTH_CONNECT_CODE = 101;

        if (ContextCompat.checkSelfPermission(BluetoothConfigActivity.this,
                Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(BluetoothConfigActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_SCAN},
                    BLUETOOTH_SCAN_CODE);
        }
        else {
            Toast.makeText(BluetoothConfigActivity.this,
                    "BLUETOOTH_SCAN already granted", Toast.LENGTH_SHORT) .show();
        }

        if (ContextCompat.checkSelfPermission(BluetoothConfigActivity.this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(BluetoothConfigActivity.this,
                    new String[]{Manifest.permission.BLUETOOTH_CONNECT},
                    BLUETOOTH_CONNECT_CODE);
        }
        else {
            Toast.makeText(BluetoothConfigActivity.this,
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
                    Toast.makeText(BluetoothConfigActivity.this, name + " is in paired list.", Toast.LENGTH_LONG).show();
                    return lv_bd;
                }
            }
            Toast.makeText(BluetoothConfigActivity.this, name + " is NOT in paired list.", Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(BluetoothConfigActivity.this, "Failed in findRobot() " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }

    // Modify from chap14, pp391 connectToRobot()
    @SuppressLint("MissingPermission")
    private void cpf_connectToEV3(BluetoothDevice bd) {
        try  {
            cv_btSocket = bd.createRfcommSocketToServiceRecord
                    (UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            cv_btSocket.connect();
            Toast.makeText(BluetoothConfigActivity.this, "Connect to " + bd.getName() + " at " + bd.getAddress(), Toast.LENGTH_LONG).show();
            cv_is = cv_btSocket.getInputStream();
            cv_os = cv_btSocket.getOutputStream();
            connectionStatus = true;
            setStatus();
        }
        catch (Exception e) {
            Toast.makeText(BluetoothConfigActivity.this, "Error interacting with remote device [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
        }
    }


    private void setStatus(){
        TextView deviceNametv = (TextView) findViewById(R.id.tvDeviceName);
        TextView connectionStatustv = (TextView) findViewById(R.id.tvConnectionStatus);
        ImageView bluetoothSymbol = (ImageView) findViewById(R.id.bluetoothSymbolImage);
        connectionStatustv.setText("Connected");
        deviceNametv.setText(CV_ROBOTNAME);
        connectionStatustv.setTextColor(Color.parseColor("#ff850a"));
        deviceNametv.setTextColor(Color.parseColor("#ff850a"));
        bluetoothSymbol.setImageResource(R.drawable.bluetooth_connected);
    }

    //disconnect from EV3
    @SuppressLint("MissingPermission")
    private void cpf_disconnFromEV3(BluetoothDevice bd) {
        try {
            cv_btSocket.close();
            cv_is.close();
            cv_os.close();
            Toast.makeText(BluetoothConfigActivity.this, bd.getName() + " is disconnected.", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Toast.makeText(BluetoothConfigActivity.this, "Error in disconnect -> " + e.getMessage(), Toast.LENGTH_LONG).show();
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
}