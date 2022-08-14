package com.naqelexpress.naqelpointer.Classes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.util.Log;
import android.util.SparseArray;
import android.widget.CheckBox;

import com.google.android.gms.vision.barcode.Barcode;
import com.naqelexpress.naqelpointer.MLBarcode.BarcodeCapture;
import com.naqelexpress.naqelpointer.MLBarcode.BarcodeGraphic;
import com.naqelexpress.naqelpointer.MLBarcode.MobilevisionBarcode.BarcodeRetriever;
import com.naqelexpress.naqelpointer.R;

import java.util.List;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;

public class NewBarCodeScanner extends AppCompatActivity implements BarcodeRetriever {

    private static final String TAG = "BarcodeMain";

    CheckBox fromXMl, pause;
    SwitchCompat drawRect, autoFocus, supportMultiple, touchBack, drawText, flash, frontCam;

    BarcodeCapture barcodeCapture;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        // Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());

        setContentView(R.layout.barcode_google);


        barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
//        barcodeCapture = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);

        barcodeCapture.setRetrieval(this);
//
//        fromXMl = (CheckBox) findViewById(R.id.from_xml);
//        pause = (CheckBox) findViewById(R.id.pause);
//        drawRect = (SwitchCompat) findViewById(R.id.draw_rect);
//        autoFocus = (SwitchCompat) findViewById(R.id.focus);
//        supportMultiple = (SwitchCompat) findViewById(R.id.support_multiple);
//        touchBack = (SwitchCompat) findViewById(R.id.touch_callback);
//        drawText = (SwitchCompat) findViewById(R.id.draw_text);
//        flash = (SwitchCompat) findViewById(R.id.on_flash);
//        frontCam = (SwitchCompat) findViewById(R.id.front_cam);
//
//        findViewById(R.id.stop).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                barcodeCapture.stopScanning();
//            }
//        });
//
//        findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (fromXMl.isChecked()) {
//
//                } else {
//                    barcodeCapture.setShowDrawRect(drawRect.isChecked())
//                            .setSupportMultipleScan(supportMultiple.isChecked())
//                            .setTouchAsCallback(touchBack.isChecked())
//                            .shouldAutoFocus(autoFocus.isChecked())
//                            .setShowFlash(flash.isChecked())
//                            .setShowFlash(flash.isChecked())
//                            .setBarcodeFormat(Barcode.ALL_FORMATS)
//                            .setCameraFacing(frontCam.isChecked() ? CameraSource.CAMERA_FACING_FRONT : CameraSource.CAMERA_FACING_BACK)
//                            .setShouldShowText(drawText.isChecked());
//                    if (pause.isChecked())
//                        barcodeCapture.pause();
//                    else
//                        barcodeCapture.resume();
//                    barcodeCapture.refresh(true);
//                }
//            }
//        });

    }


    @Override
    public void onRetrieved(final Barcode barcode) {
        Log.d(TAG, "Barcode read: " + barcode.displayValue);
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
////                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
////                        .setTitle("code retrieved")
////                        .setMessage(barcode.displayValue);
////                builder.show();
//                barcodeCapture.stopScanning();
//                handleResult(barcode.displayValue);
//            }
//        });

        barcodeCapture.stopScanning();
        handleResult(barcode.displayValue);
    }

    @Override
    public void onRetrievedMultiple(final Barcode closetToClick, final List<BarcodeGraphic> barcodeGraphics) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                String message = "Code selected : " + closetToClick.displayValue + "\n\nother " +
//                        "codes in frame include : \n";
//                for (int index = 0; index < barcodeGraphics.size(); index++) {
//                    Barcode barcode = barcodeGraphics.get(index).getBarcode();
//                    message += (index + 1) + ". " + barcode.displayValue + "\n";
//                }
//                AlertDialog.Builder builder = new AlertDialog.Builder(NewBarCodeScanner.this)
//                        .setTitle("code retrieved")
//                        .setMessage(message);
//                builder.show();
//            }
//        });

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            Barcode barcode = sparseArray.valueAt(i);
            Log.e("value", barcode.displayValue);
        }

    }

    @Override
    public void onRetrievedFailed(String reason) {

    }

    @Override
    public void onPermissionRequestDenied() {

    }


    public void handleResult(String rawResult) {
        Intent intent = new Intent();
        // String result = rawResult.getContents();
        //Log.d("test", "result " + result);
        intent.putExtra("barcode", rawResult);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit")
                .setMessage("Are you sure you want to exit ?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        barcodeCapture.stopScanning();
                        finish();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}