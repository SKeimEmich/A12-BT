package com.example.a12_bt;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener{
    // To store the speed
    private int firstSeekBarProgress;
    private int secondSeekBarProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Seekbars
        SeekBar firstSeek = findViewById(R.id.firstSeekBar);
        SeekBar secondSeek = findViewById(R.id.secondSeekBar);
        firstSeek.setOnSeekBarChangeListener(this);
        secondSeek.setOnSeekBarChangeListener(this);

        //set bluetooth connection status
        TextView connectionStatus = (TextView) findViewById(R.id.nxtConnectionText);
        if(BluetoothConfigActivity.connectionStatus == true){
            connectionStatus.setText("NXT Device is connected!");
            connectionStatus.setTextColor(Color.parseColor("#FF9800"));
        }
        else{
            connectionStatus.setText("NXT Device is not connected!");
            connectionStatus.setTextColor(Color.parseColor("#000000"));
        }

        // Drive Buttons
        ImageButton driveUp = findViewById(R.id.driveUp);
        driveUp.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //set the direction as forward
                BluetoothConfigActivity.direction = 1;
                //call the move method
                BluetoothConfigActivity.moveForward();
            }
        }));

        ImageButton driveDown = findViewById(R.id.driveDown);
        driveDown.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothConfigActivity.direction = -1;
                BluetoothConfigActivity.moveForward();
            }
        }));
        ImageButton driveRight = findViewById(R.id.driveRight);
        driveRight.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothConfigActivity.direction = 1;
                BluetoothConfigActivity.turnRight();
            }
        }));
        ImageButton driveLeft = findViewById(R.id.driveLeft);
        driveLeft.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothConfigActivity.direction = 1;
                BluetoothConfigActivity.turnLeft();
            }
        }));
        ImageButton otherDriveDown = findViewById(R.id.otherDriveDown);
        otherDriveDown.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothConfigActivity.direction = -1;
                BluetoothConfigActivity.moveForward();
            }
        }));
        ImageButton otherDriveUp = findViewById(R.id.otherDriveUp);
        otherDriveUp.setOnTouchListener(new RepeatListener(400, 100, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothConfigActivity.direction = 1;
                BluetoothConfigActivity.moveForward();
            }
        }));

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int progress = seekBar.getProgress();
        int currentSeekId = seekBar.getId();

        if (currentSeekId == R.id.firstSeekBar){
            firstSeekBarProgress = progress;
            TextView text = findViewById(R.id.firstSeekBarText);
            text.setText("Power " + progress + ":");
            BluetoothConfigActivity.speed = progress;
        }
        else{
            secondSeekBarProgress = progress;
            TextView text = findViewById(R.id.secondSeekBarText);
            text.setText("Power " + progress + ":");
            BluetoothConfigActivity.speed = progress;
        }

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onClick(View view) {
        int imageIdClicked = view.getId();
        switch(imageIdClicked){
            case R.id.driveRight:
                break;
            case R.id.driveLeft:
                break;
            case R.id.driveUp:
                break;
            case R.id.driveDown:
                break;
            case R.id.otherDriveDown:
                break;
            case R.id.otherDriveUp:
                break;
        }
    }
}