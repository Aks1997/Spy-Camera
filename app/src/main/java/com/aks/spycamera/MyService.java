package com.aks.spycamera;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

public class MyService extends Service {

    MyBinder binder = new MyBinder();
    private boolean isRecording = false;
    private SurfaceView msurfaceView;
    public Camera mCamera;
    public MediaRecorder mMediaRecorder;
    public String TAG = "Rec..";
    private Button recorder;
    private static int mCam;
    private Button mcamUse;

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("Myservice", "onBind");
        // TODO: Return the communication channel to the service.
        return binder;
    }

    public class MyBinder extends Binder {

        public void initialize(SurfaceView surfaceView, Button rec,Button camUse) {
            Log.e("Myservice", "binder");
            msurfaceView = surfaceView;
            recorder = rec;
            mcamUse=camUse;
        }

        public void releaseCam() {
//            releaseMediaRecorder();       // if you are using MediaRecorder, release it first
//            releaseCamera();
            if (isRecording) {
                // stop recording and release camera
                try {
                    mMediaRecorder.stop();// stop the recording
                }catch(RuntimeException stopException){
                    //handle cleanup here
                    Log.e("MediaRecorder","Stop failed "+stopException.getMessage());
                }
                releaseMediaRecorder(); // release the MediaRecorder object
                releaseCamera();

                recorder.setText("Start Recording...");
                // inform the user that recording has stopped
                isRecording = false;
            }
            else {
                releaseMediaRecorder(); // release the MediaRecorder object
                releaseCamera();
            }
            mcamUse.setClickable(true);
        }

        public void cam(int camUsed) {
            mCam=camUsed;
            if (isRecording) {
                // stop recording and release camera
                try {
                    mMediaRecorder.stop();// stop the recording
                }catch(RuntimeException stopException){
                    //handle cleanup here
                    Log.e("MediaRecorder","Stop failed "+stopException.getMessage());
                }
                releaseMediaRecorder(); // release the MediaRecorder object
                mCamera.lock();         // take camera access back from MediaRecorder

                recorder.setText("Start Recording...");
                mcamUse.setClickable(true);
                // inform the user that recording has stopped
                isRecording = false;
            } else {
                // initialize video camera
                if (prepareVideoRecorder()) {
                    // Camera is available and unlocked, MediaRecorder is prepared,
                    // now you can start recording
                    mMediaRecorder.start();
                    recorder.setText("Stop");
                    mcamUse.setClickable(false);
                    // inform the user that recording has started
                    isRecording = true;
                } else {
                    // prepare didn't work, release the camera
                    releaseMediaRecorder();
                    // inform user
                    recorder.setText("Start Recording...");
                    mcamUse.setClickable(true);
                    isRecording = false;
                }
            }
        }
    }

    public void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    public boolean prepareVideoRecorder() {

        mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        mCamera.unlock();
        mMediaRecorder.setCamera(mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
        mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(msurfaceView.getHolder().getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    public void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "spyCamera");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    /**
     * A safe way to get an instance of the Camera object.
     */
    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(mCam); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e("Myservice", "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("Myservice", "onDestroy");
    }
}
