package com.example.a12_bt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        }
        catch (Exception e) {
            Toast.makeText(BluetoothConfigActivity.this, "Error interacting with remote device [" + e.getMessage() + "]", Toast.LENGTH_LONG).show();
        }
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
}