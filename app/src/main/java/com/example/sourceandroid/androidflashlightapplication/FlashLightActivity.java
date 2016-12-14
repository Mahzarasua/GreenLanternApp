package com.example.sourceandroid.androidflashlightapplication;

/**
 * Created by PC on 7/1/2016.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ToggleButton;

import static android.R.id.toggle;


public class FlashLightActivity extends AppCompatActivity {
    //Comments
    private CameraManager mCameraManager;
    private String mCameraId;
    private String mText;
    private EditText mEditText;
    private ImageButton mTorchOnOffButton;
    private Boolean isTorchOn;
    private MediaPlayer mp;
    private Boolean isToggleOn;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FlashLightActivity", "onCreate()");
        setContentView(R.layout.activity_main);
        ToggleButton toggle = (ToggleButton) findViewById(R.id.toggleButton);
        mTorchOnOffButton = (ImageButton) findViewById(R.id.button_on_off);
        mTorchOnOffButton.setBackgroundResource(R.drawable.img_off);
        isTorchOn = false;
        isToggleOn = false;
        Boolean isFlashAvailable = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!isFlashAvailable) {

            AlertDialog alert = new AlertDialog.Builder(FlashLightActivity.this)
                    .create();
            alert.setTitle("Error !!");
            alert.setMessage("Your device doesn't support flash light!");
            alert.setButton(DialogInterface.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // closing the application
                    finish();
                    System.exit(0);
                }
            });
            alert.show();
            return;
        }

        /*
        Toggle code begins
         */
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                // The toggle is enabled
                isToggleOn = true;
                blink(getThreshold());
            } else {
                // The toggle is disabled
                isToggleOn = false;
                turnOffFlashLight();
            }
        }
        });
        /*
        Toggle code ends
         */


        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            mCameraId = mCameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        mTorchOnOffButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (isTorchOn) {
                        mTorchOnOffButton.setBackgroundResource(R.drawable.img_off);
                        turnOffFlashLight();
                        isTorchOn = false;
                    } else {
                        mTorchOnOffButton.setBackgroundResource(R.drawable.img_on);
                        turnOnFlashLight();
                        isTorchOn = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public int getThreshold(){
        mEditText = (EditText) findViewById(R.id.editText);
        mText = mEditText.getText().toString();
        int foo = Integer.parseInt(mText) * 100;
        return foo;
    }

    private void blink(final int delay2) {
        Thread t = new Thread() {
            public void run() {
                int delay = delay2;
                try {
                    while (isToggleOn){
                        if (isToggleOn) {
                            delay = getThreshold();
                            if (isTorchOn) {
                                turnOffFlashLight();
                                isTorchOn = false;
                            } else {
                                turnOnFlashLight();
                                isTorchOn = true;
                            }
                            sleep(delay);
                        }
                        else {
                            turnOffFlashLight();
                            isTorchOn = false;
                        }
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    public void turnOnFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, true);
                playOnOffSound();
                mTorchOnOffButton.setImageResource(R.drawable.on2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void turnOffFlashLight() {

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mCameraManager.setTorchMode(mCameraId, false);
                playOnOffSound();
                mTorchOnOffButton.setImageResource(R.drawable.on2);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playOnOffSound(){

      //mp = MediaPlayer.create(FlashLightActivity.this, R.raw.flash_sound);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if(isTorchOn){
            turnOffFlashLight();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isTorchOn){
            turnOffFlashLight();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isTorchOn){
            turnOnFlashLight();
        }
    }
    }