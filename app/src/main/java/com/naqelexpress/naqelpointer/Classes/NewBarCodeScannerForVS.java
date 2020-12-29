package com.naqelexpress.naqelpointer.Classes;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;

public class NewBarCodeScannerForVS extends AppCompatActivity
        implements ZBarScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZBarScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;

    public static ArrayList<String> scannedBarCode = new ArrayList<>();
    public static ArrayList<String> ScanbyDevice = new ArrayList<>();
    public static ArrayList<String> ConflictBarcode = new ArrayList<>();


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.newbarcodescanner);

//        scannedBarCode.clear();
//        ScanbyDevice.clear();
//        ConflictBarcode.clear();

        if (state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
            mCameraId = state.getInt(CAMERA_ID, -1);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
            mCameraId = -1;

            if (scannedBarCode.size() == 0) {
                Bundle extras = getIntent().getExtras();
                scannedBarCode = (ArrayList<String>) extras.getSerializable("scannedBarCode");
                ScanbyDevice = (ArrayList<String>) extras.getSerializable("ScanbyDevice");
                ConflictBarcode = (ArrayList<String>) extras.getSerializable("ConflictBarcode");
            }
        }

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new ZBarScannerView(this);
        setupFormats();
        contentFrame.addView(mScannerView);
        Button btnFlash = (Button) findViewById(R.id.btnFlash);
        btnFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFlash = !mFlash;
                mScannerView.setFlash(mFlash);
            }
        });
        setRequestedOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera(mCameraId);
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
        outState.putInt("EmployID", GlobalVar.GV().EmployID);
        outState.putInt("UserID", GlobalVar.GV().UserID);
        outState.putInt("StationID", GlobalVar.GV().StationID);
        outState.putString("EmployMobileNo", GlobalVar.GV().EmployMobileNo);
        outState.putString("EmployName", GlobalVar.GV().EmployName);
        outState.putString("EmployStation", GlobalVar.GV().EmployStation);
        outState.putParcelable("currentSettings", GlobalVar.GV().currentSettings);
        outState.putInt("currentSettingsID", GlobalVar.GV().currentSettings.ID);
        outState.putStringArrayList("scannedBarCode", scannedBarCode);
        outState.putStringArrayList("ScanbyDevice", ScanbyDevice);
        outState.putStringArrayList("ConflictBarcode", ConflictBarcode);
    }

    MediaPlayer mediaPlayer;

//    public void react(View view) {
//        mediaPlayer = MediaPlayer.create(ToBeOrNot.this, R.raw.achord);
//        mediaPlayer.start();
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        mediaPlayer.release();
//        mediaPlayer = null;
//    }
//

    @Override
    public void handleResult(Result rawResult) {

        String result = rawResult.getContents();
        if (scannedBarCode.contains(result)) {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.barcodescanned);
            if (!ScanbyDevice.contains(result))
                ScanbyDevice.add(result);

            mScannerView.resumeCameraPreview(this);
        } else {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            if (!ConflictBarcode.contains(result))
                ConflictBarcode.add(result);
            conflict(result);

        }

    }

    public void conflict(String Barcode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewBarCodeScannerForVS.this);
        builder.setTitle("Warning " + Barcode)
                .setMessage("This Piece is not belongs to this Employee")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        // finish();
                        // startActivity(getIntent());
                        mScannerView.resumeCameraPreview(NewBarCodeScannerForVS.this);
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<>();
        if (mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<>();
            for (int i = 0; i < BarcodeFormat.ALL_FORMATS.size(); i++)
                mSelectedIndices.add(i);
        }

        for (int index : mSelectedIndices)
            formats.add(BarcodeFormat.ALL_FORMATS.get(index));
        if (mScannerView != null)
            mScannerView.setFormats(formats);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {

            GlobalVar.GV().EmployID = savedInstanceState.getInt("EmployID");
            GlobalVar.GV().UserID = savedInstanceState.getInt("UserID");
            GlobalVar.GV().StationID = savedInstanceState.getInt("StationID");
            GlobalVar.GV().EmployMobileNo = savedInstanceState.getString("EmployMobileNo");
            GlobalVar.GV().EmployName = savedInstanceState.getString("EmployName");
            GlobalVar.GV().EmployStation = savedInstanceState.getString("EmployStation");
            GlobalVar.GV().currentSettings = savedInstanceState.getParcelable("currentSettings");
            GlobalVar.GV().currentSettings.ID = savedInstanceState.getInt("currentSettingsID");
            scannedBarCode = savedInstanceState.getStringArrayList("scannedBarCode");
            ScanbyDevice = savedInstanceState.getStringArrayList("ScanbyDevice");
            ConflictBarcode = savedInstanceState.getStringArrayList("ConflictBarcode");

        }
    }

    @Override
    public void onBackPressed() {
//        Bundle bundle = new Bundle();
        Intent intent = new Intent();
        intent.putExtra("test", "test");
        intent.putExtra("scannedBarCode", scannedBarCode);
        //bundle.putSerializable("scannedBarCode", scannedBarCode);
        //bundle.putSerializable("ScanbyDevice", ScanbyDevice);
        intent.putExtra("ScanbyDevice", ScanbyDevice);
//        bundle.putSerializable("ConflictBarcode", ConflictBarcode);
        intent.putExtra("ConflictBarcode", ConflictBarcode);
        //intent.putExtras(bundle);
        setResult(RESULT_OK, intent);

//        finish();//finishing activity
        super.onBackPressed();
    }
}