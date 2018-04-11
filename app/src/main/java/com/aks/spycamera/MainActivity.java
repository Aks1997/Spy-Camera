package com.aks.spycamera;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private Intent intent;
    private Button recorder;
    private SurfaceView mSurface;
    MyService.MyBinder myBinder;
    private Button release;
    private Button cam,newTab;
    private int camUsed=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findIds();
        checkPermmision();

        intent=new Intent(this,MyService.class);
        bindService(intent,serviceConnection, Service.BIND_AUTO_CREATE);
    }

    private void findIds() {
        recorder=(Button)findViewById(R.id.rec_id);
        newTab=(Button)findViewById(R.id.newTab_id);
        mSurface=(SurfaceView)findViewById(R.id.surface_id);
        release=(Button)findViewById(R.id.release_id);
        cam=(Button)findViewById(R.id.cam_id);

        newTab.setOnClickListener(this);
        recorder.setOnClickListener(this);
        release.setOnClickListener(this);
        cam.setOnClickListener(this);
    }

    ServiceConnection serviceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            myBinder=(MyService.MyBinder)service;
            Log.e("Main","Connected");
            myBinder.initialize(mSurface,recorder,cam);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("Main","DisConnected");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
    }

    private void checkPermmision(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            return;
        }
        else
            ActivityCompat.requestPermissions(this, new String[]{CAMERA,RECORD_AUDIO,WRITE_EXTERNAL_STORAGE}, 200);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rec_id:
                if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA)== PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)
                    myBinder.cam(camUsed);
                else
                    ActivityCompat.requestPermissions(this, new String[]{CAMERA,RECORD_AUDIO,WRITE_EXTERNAL_STORAGE}, 200);
                break;
            case R.id.release_id:
                if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA)== PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                    myBinder.releaseCam();
                    Toast.makeText(this, "Camera Released", Toast.LENGTH_SHORT).show();
                }
                else
                    ActivityCompat.requestPermissions(this, new String[]{CAMERA,RECORD_AUDIO,WRITE_EXTERNAL_STORAGE}, 200);
                break;
            case R.id.cam_id:
                if(camUsed==0) {
                    camUsed = 1;
                    cam.setText("Set Back Camera\n\n*Front Camera is active");
                }
                else {
                    camUsed = 0;
                    cam.setText("Set Front Camera\n\n*Back Camera is active");
                }
                if(ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA)== PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                    myBinder.releaseCam();
                    Toast.makeText(this, "Camera Released", Toast.LENGTH_SHORT).show();
                }
                else
                    ActivityCompat.requestPermissions(this, new String[]{CAMERA,RECORD_AUDIO,WRITE_EXTERNAL_STORAGE}, 200);
                break;
            case R.id.newTab_id:
                startActivity(new Intent(MainActivity.this,Videos.class));
                break;
        }
    }
}
