
package com.example.a12_bt;

import static com.example.a12_bt.R.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import java.util.UUID;

import android.content.Intent;
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
        
        binding.configBTButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lv_it = new Intent(MainActivity.this, BluetoothConfigActivity.class);
                startActivity(lv_it);
            }
        });
        binding.robotControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lv_it = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(lv_it);
            }
        });
        // Need grant permission once per install
//        cpf_checkBTPermissions(); // MOVED TO BluetoothConfigActivity
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
//                cpf_requestBTPermissions();
                return true;
            case id.secondMenuOption:
//                cv_btDevice = cpf_locateInPairedBTList(CV_ROBOTNAME);
                return true;
            case id.thirdMenuOption:
//                cpf_connectToEV3(cv_btDevice);
                return true;
            case id.fourthMenuOption:
                moveForward();
                return true;
            case id.fifthMenuOption:
                cpf_EV3PlayTone();
                return true;
            case id.sixthMenuOption:
//                cpf_disconnFromEV3(cv_btDevice);
                return true;
        }
        binding.vvTvOut1.setText(menuItem.getTitle() + " Selected");
        return true;
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