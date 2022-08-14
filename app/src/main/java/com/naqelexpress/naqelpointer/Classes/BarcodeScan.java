package com.naqelexpress.naqelpointer.Classes;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.naqelexpress.naqelpointer.R;

public class BarcodeScan
        extends AppCompatActivity
{
    SurfaceView cameraView;
    BarcodeDetector barcode;
    CameraSource cameraSource;
    SurfaceHolder holder;
    Intent intent;
    //android.hardware.camera2.Camera camera;
    //private CameraManager mCameraManager;
    //private String mCameraId;
    //private ImageButton mTorchOnOffButton;
    //private Boolean isTorchOn;
    //private MediaPlayer mp;

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        cameraSource = null;
        holder = null;
        cameraView = null;
        barcode = null;
        intent = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
//        if(Created)
//            return;
        super.onCreate(savedInstanceState);
//        Created = true;
        setContentView(R.layout.barcodescan);
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        cameraView.setZOrderMediaOverlay(true);
        holder = cameraView.getHolder();
        barcode = new BarcodeDetector.Builder(this)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();
        if (!barcode.isOperational())
        {
            Toast.makeText(getApplicationContext(),"Sorry, We need a Camera Permission to Scan Barcode.", Toast.LENGTH_LONG).show();
            this.finish();
        }

        cameraSource = new CameraSource.Builder(this,barcode)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
//                .setRequestedFps(200)
                .setAutoFocusEnabled(true)
//                .setRequestedPreviewSize(1000,1000)
                .build();
//        .setRequestedPreviewSize(1920,1024)

//        cameraSource = new CameraSource.Builder(GlobalVar.GV().context,barcode)
//                .setFacing(CameraSource.CAMERA_FACING_BACK)
//                .setRequestedFps(200)
//                .setAutoFocusEnabled(true)
//                .setRequestedPreviewSize(1000,1000)
//                .build();

        cameraView.getHolder().addCallback(new SurfaceHolder.Callback()
        {
            @Override
            public void surfaceCreated(SurfaceHolder holder)
            {
                try
                {
                    if(ContextCompat.checkSelfPermission(BarcodeScan.this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                    {
                        try
                        {
                            cameraSource.start(cameraView.getHolder());
                        }
                        catch (Exception exception)
                        {
                            exception.printStackTrace();
                        }
                    }
                }
                catch (Exception ignored){}
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {}
        });

        barcode.setProcessor(new Detector.Processor<Barcode>()
        {
            @Override
            public void release(){}

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections)
            {
//                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
//                if (barcodes.size() > 0 )
//                {
//                    intent = new Intent();
//                    intent.putExtra("barcode", barcodes.valueAt(0));
//                    setResult(RESULT_OK,intent);
//                    finish();
//                }
            }
        });

        setRequestedOrientation(getResources().getConfiguration().orientation);

//        CameraDevice
//        CameraManager manager = (CameraManager) this.getSystemService(Context.CAMERA_SERVICE);
//        String[] cameraIds = new String[0];
//        try {
//            cameraIds = manager.getCameraIdList();
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//        CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);

//        camera = Camera.open();
//        Camera.Parameters parameters = camera.getParameters();
//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
//        camera.setParameters(parameters);
//        camera.startPreview();

//        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        try
//        {
//            mCameraId = mCameraManager.getCameraIdList()[0];
//        }
//        catch (CameraAccessException e)
//        {
//            e.printStackTrace();
//        }
//
//        turnOnFlashLight();

//        mTorchOnOffButton.setOnClickListener(new View.OnClickListener()
//        {
//            @Override
//            public void onClick(View v) {
//                try {
//                    if (isTorchOn) {
//                        turnOffFlashLight();
//                        isTorchOn = false;
//                    } else {
//                        turnOnFlashLight();
//                        isTorchOn = true;
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }

//    public void turnOnFlashLight()
//    {
//        try
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            {
//                mCameraManager.setTorchMode(mCameraId, true);
//                playOnOffSound();
////                mTorchOnOffButton.setImageResource(R.drawable.on);
//            }
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

//    public void turnOffFlashLight()
//    {
//        try
//        {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//            {
//                mCameraManager.setTorchMode(mCameraId, false);
//                playOnOffSound();
////                mTorchOnOffButton.setImageResource(R.drawable.off);
//            }
//
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }

//    private void playOnOffSound()
//    {
////        mp = MediaPlayer.create(FlashLightActivity.this, R.raw.flash_sound);
////        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
////        {
////            @Override
////            public void onCompletion(MediaPlayer mp)
////            {
////                mp.release();
////            }
////        });
////        mp.start();
//    }
}
