package com.naqelexpress.naqelpointer.Classes;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.MLBarcode.BarcodeCapture;
import com.naqelexpress.naqelpointer.MLBarcode.BarcodeGraphic;
import com.naqelexpress.naqelpointer.MLBarcode.MobilevisionBarcode.BarcodeRetriever;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.net.Uri;

public class NewBarCodeScannerForVS extends AppCompatActivity
        implements BarcodeRetriever {
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
    private DBConnections dbConnections;
    private List<OnLineValidation> onLineValidationList;
    private String division;

    BarcodeCapture barcodeCapture;
    String barcode = "";

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.barcode_google);


        barcodeCapture = (BarcodeCapture) getSupportFragmentManager().findFragmentById(R.id.barcode);
//        barcodeCapture = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);

        barcodeCapture.setRetrieval(this);

        try {
            division = GlobalVar.GV().getDivisionID(getApplicationContext(), GlobalVar.GV().EmployID);
            dbConnections = new DBConnections(getApplicationContext(), null);
            onLineValidationList = new ArrayList<>();
        } catch (Exception e) {
            Log.d("test", "Scanner " + e.toString());
        }

//        if (state != null) {
//            mFlash = state.getBoolean(FLASH_STATE, false);
//            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
//            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
//            mCameraId = state.getInt(CAMERA_ID, -1);
//        } else {
//            mFlash = false;
//            mAutoFocus = true;
//            mSelectedIndices = null;
//            mCameraId = -1;
//
//            if (scannedBarCode.size() == 0) {
//                Bundle extras = getIntent().getExtras();
//                scannedBarCode = (ArrayList<String>) extras.getSerializable("scannedBarCode");
//                ScanbyDevice = (ArrayList<String>) extras.getSerializable("ScanbyDevice");
//                ConflictBarcode = (ArrayList<String>) extras.getSerializable("ConflictBarcode");
//            }
//        }

        if (scannedBarCode.size() == 0) {
            Bundle extras = getIntent().getExtras();
            scannedBarCode = (ArrayList<String>) extras.getSerializable("scannedBarCode");
            ScanbyDevice = (ArrayList<String>) extras.getSerializable("ScanbyDevice");
            ConflictBarcode = (ArrayList<String>) extras.getSerializable("ConflictBarcode");
        }

        //ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
//        mScannerView = new ZBarScannerView(this);
//        setupFormats();
//        contentFrame.addView(mScannerView);
//        Button btnFlash = (Button) findViewById(R.id.btnFlash);
//        btnFlash.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mFlash = !mFlash;
//                mScannerView.setFlash(mFlash);
//            }
//        });
        setRequestedOrientation(getResources().getConfiguration().orientation);
    }

    @Override
    public void onResume() {
        super.onResume();
//        mScannerView.setResultHandler(this);
//        mScannerView.startCamera(mCameraId);
//        mScannerView.setFlash(mFlash);
//        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onRetrieved(final Barcode barcode) {


        barcodeCapture.stopScanning();
        this.barcode = barcode.displayValue;
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
//        Intent intent = new Intent();
//        // String result = rawResult.getContents();
//        //Log.d("test", "result " + result);
//        intent.putExtra("barcode", rawResult);
//        setResult(RESULT_OK, intent);
//        finish();


        String result = rawResult;

        // To show onlineValidation warning if any
//        boolean isConflict = !scannedBarCode.contains(result);
//        if (division.equals("Courier")) {
//            onlineValidation(result, isConflict);
//        }
//
//        if (scannedBarCode.contains(result)) {
//            GlobalVar.MakeSound(getApplicationContext(), R.raw.barcodescanned);
//            if (!ScanbyDevice.contains(result)) {
//                ScanbyDevice.add(result);
//                onBackPressed();
//            }
//            // mScannerView.resumeCameraPreview(this);
//
//        } else {
//            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
//            if (!ConflictBarcode.contains(result))
//                ConflictBarcode.add(result);
//
//            if (!division.equals("Courier")) //For courier popup will be shown in onlineValidation
//                conflict(result);
//
//        }

        // mScannerView.resumeCameraPreview(this);
        onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    /*@Override
    public void handleResult(Result rawResult) {

        String result = rawResult.getContents();

        // To show onlineValidation warning if any
        boolean isConflict = !scannedBarCode.contains(result);
        if (division.equals("Courier")) {
            onlineValidation(result, isConflict);
        }

        if (scannedBarCode.contains(result)) {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.barcodescanned);
            if (!ScanbyDevice.contains(result))
                ScanbyDevice.add(result);

            mScannerView.resumeCameraPreview(this);
        } else {
            GlobalVar.MakeSound(getApplicationContext(), R.raw.wrongbarcodescan);
            if (!ConflictBarcode.contains(result))
                ConflictBarcode.add(result);

            if (!division.equals("Courier")) //For courier popup will be shown in onlineValidation
                conflict(result);

        }

        mScannerView.resumeCameraPreview(this);


    }*/

    public void conflict(String Barcode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(NewBarCodeScannerForVS.this);
        builder.setTitle("Warning " + Barcode)
                .setMessage("This Piece is not belongs to this Employee")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        finish();
                        // startActivity(getIntent());
//                        mScannerView.resumeCameraPreview(NewBarCodeScannerForVS.this);
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
//        mScannerView.stopCamera();
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

        barcodeCapture.stopScanning();
        Intent intent = new Intent();
        intent.putExtra("barcode", barcode);
        intent.putExtra("scannedBarCode", scannedBarCode);
        intent.putExtra("ScanbyDevice", ScanbyDevice);
        intent.putExtra("ConflictBarcode", ConflictBarcode);
        setResult(RESULT_OK, intent);
        finish();

//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Exit")
//                .setMessage("Are you sure you want to exit ?")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int which) {
//
//                    }
//                }).setNegativeButton("Cancel", null).setCancelable(false);
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();


//        finish();//finishing activity
        super.onBackPressed();
    }

    private void onlineValidation(String barcode, boolean isConflict) {
        boolean isShowWarning = false;
        try {
            OnLineValidation onLineValidationLocal = dbConnections.getPieceInformationByWaybillNo(GlobalVar.getWaybillFromBarcode(barcode)
                    , barcode, getApplicationContext(), false);
            OnLineValidation onLineValidation = new OnLineValidation();

            if (onLineValidationLocal != null) {


                if (onLineValidationLocal.getIsMultiPiece() == 1) {
                    onLineValidation.setIsMultiPiece(1);
                    isShowWarning = true;
                }

                if (onLineValidationLocal.getIsStopped() == 1) {
                    onLineValidation.setIsStopped(1);
                    isShowWarning = true;
                }
            }

            if (isConflict) {
                onLineValidation.setIsConflict(1);
                isShowWarning = true;
            }

            if (isShowWarning) {
                onLineValidation.setBarcode(barcode);
                onLineValidationList.add(onLineValidation);
                showDialog(getOnLineValidationPiece(barcode));
            }

        } catch (Exception e) {
            Log.d("test", "isValidPieceBarcode " + e.toString());
        }
    }

    private OnLineValidation getOnLineValidationPiece(String barcode) {
        try {
            for (OnLineValidation pieceDetail : onLineValidationList) {
                if (pieceDetail.getBarcode().equals(barcode))
                    return pieceDetail;
            }

        } catch (Exception e) {
            Log.d("test", "getOnLineValidationPiece " + e.toString());
        }
        return null;
    }

    public void showDialog(OnLineValidation pieceDetails) {
        try {
            if (pieceDetails != null) {
                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(NewBarCodeScannerForVS.this);
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
                tvBarcode.setText("Piece #" + pieceDetails.getBarcode());


                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setText("OK");


                if (pieceDetails.getIsMultiPiece() == 1) {
                    LinearLayout llMultiPiece = dialogView.findViewById(R.id.ll_is_multi_piece);
                    llMultiPiece.setVisibility(View.VISIBLE);

                    TextView tvMultiPieceHeader = dialogView.findViewById(R.id.tv_multiPiece_header);
                    tvMultiPieceHeader.setText("Multi Piece");

                    TextView tvMultiPieceBody = dialogView.findViewById(R.id.tv_multiPiece_body);
                    tvMultiPieceBody.setText("Please check pieces.");
                }

                if (pieceDetails.getIsStopped() == 1) {
                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                    tvStopShipmentHeader.setText("Stop Shipment");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                    tvStopShipmentBody.setText("Stop shipment.Please Hold.");
                }

                if (pieceDetails.getIsConflict() == 1) {
                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_ds_validation);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_ds_validation_header);
                    tvStopShipmentHeader.setText("DS Validation");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_ds_validation_body);
                    tvStopShipmentBody.setText("Shipment is not belong to employee.");
                }


                final android.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To avoid leaked window
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                        }
                    }
                });


            }
        } catch (Exception e) {
            Log.d("test", "showDialog " + e.toString());
        }
    }
}