package com.naqelexpress.naqelpointer.Classes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;

public class NewBarCodeScanner extends AppCompatActivity
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

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.newbarcodescannerlands);

        try {
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
        } catch (Exception e) {
            System.out.println(e.toString());
        }
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
    }


    @Override
    public void handleResult(Result rawResult) {
        Intent intent = new Intent();
        String result = rawResult.getContents();
        Log.d("test" , "result " + result);
        intent.putExtra("barcode", result);
        setResult(RESULT_OK, intent);
        finish();
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
}