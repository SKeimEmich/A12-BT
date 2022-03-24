package com.example.a12_bt;

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

        // Drive Buttons
        ImageButton driveUp = findViewById(R.id.driveUp);
        driveUp.setOnClickListener(this);
        ImageButton driveDown = findViewById(R.id.driveDown);
        driveDown.setOnClickListener(this);
        ImageButton driveRight = findViewById(R.id.driveRight);
        driveUp.setOnClickListener(this);
        ImageButton driveLeft = findViewById(R.id.driveLeft);
        driveUp.setOnClickListener(this);
        ImageButton otherDriveDown = findViewById(R.id.otherDriveDown);
        driveUp.setOnClickListener(this);
        ImageButton otherDriveUp = findViewById(R.id.otherDriveUp);
        driveUp.setOnClickListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int progress = seekBar.getProgress();
        int currentSeekId = seekBar.getId();

        if (currentSeekId == R.id.firstSeekBar){
            firstSeekBarProgress = progress;
            TextView text = findViewById(R.id.firstSeekBarText);
            text.setText("Power " + progress + ":");
        }
        else{
            secondSeekBarProgress = progress;
            TextView text = findViewById(R.id.secondSeekBarText);
            text.setText("Power " + progress + ":");
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