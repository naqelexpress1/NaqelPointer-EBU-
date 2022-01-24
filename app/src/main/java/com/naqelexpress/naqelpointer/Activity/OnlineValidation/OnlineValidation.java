package com.naqelexpress.naqelpointer.Activity.OnlineValidation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Activity.OFDPieceLevel.DeliverySheetActivity;
import com.naqelexpress.naqelpointer.Activity.OFDPieceLevel.DeliverySheetThirdFragment;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.CheckPointBarCodeDetails;
import com.naqelexpress.naqelpointer.DB.DBObjects.PieceDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.DB.DBObjects.TerminalHandling;
import com.naqelexpress.naqelpointer.DB.DBObjects.UpdateData;
import com.naqelexpress.naqelpointer.DB.SelectData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.UpdateWaybillRequest;
import com.naqelexpress.naqelpointer.Models.CommonResult;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;
import com.naqelexpress.naqelpointer.callback.AlertCallbackOnlineValidation;
import com.naqelexpress.naqelpointer.callback.Callback;
import com.naqelexpress.naqelpointer.utils.StaticClass;
import com.naqelexpress.naqelpointer.utils.UpdatingWaybillDestinationApi;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Objects;

public class OnlineValidation implements View.OnClickListener {
    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Thread.setDefaultUncaughtExceptionHandler(new ErrorReporter());
//
//
//
//    }
    private SearchableSpinner searchableSpinnerDest;
    private Activity activity;
    private ProgressBar progressBar;
    private android.app.AlertDialog alertDialog;
    private OnLineValidation GlobalonLineValidation;
    //    private int mis_sort = 0;
    private boolean isAdd = false;

    AlertCallbackOnlineValidation alertCallback;

    public boolean showFlagsPopup(final OnLineValidation onLineValidation, final Context context, Activity activity) {

        boolean isshowPopup = false, isAddPiece = true, ismispop = false;
        try {
            this.activity = activity;


            GlobalonLineValidation = onLineValidation;
            GlobalVar.MakeSound(context, R.raw.wrongbarcodescan);

            android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_alert_dialog_new, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);

            TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
            tvBarcode.setText("Piece #" + onLineValidation.getBarcode());
            Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
            btnConfirm.setVisibility(View.VISIBLE);
            btnConfirm.setText("OK & Quit");


//

//                if (onLineValidation.getIsDestNotBelongToNcl() == 1) {
//
//                    String stationName = "";
//                    try {
//                        Station station = null;
//                        station = dbConnections.getStationByID(onLineValidation.getWaybillDestID(), getContext());
//
//                        if (station != null)
//                            stationName = station.Name;
//                        else
//                            Log.d("test", TAG + " Station is null");
//
//                    } catch (Exception e) {
//                        Log.d("test", TAG + "" + e.toString());
//                    }
//
//                    LinearLayout llDifDest = dialogView.findViewById(R.id.ll_ncl_wrong_dest);
//                    llDifDest.setVisibility(View.VISIBLE);
//
//                    TextView tvNclHeader = dialogView.findViewById(R.id.tv_ncl_header);
//                    tvNclHeader.setText("NCL Destination");
//
//                    TextView tvNclBody = dialogView.findViewById(R.id.tv_ncl_body);
//                    tvNclBody.setText("Piece destination (" + stationName + ") doesn’t belong to NCL.");
//
//                    // add on click listener
//                    radioGroupCheckListener(dialogView, btnConfirm);
//
//                }


            if (onLineValidation.getIsManifested() == 0 && onLineValidation.getisManifestedalert()) {
                isshowPopup = true;
                LinearLayout llNoManifest = dialogView.findViewById(R.id.ll_not_manifested);
                llNoManifest.setVisibility(View.VISIBLE);

                TextView tvManifestHeader = dialogView.findViewById(R.id.tv_not_manifested_header);
                tvManifestHeader.setText("Manifest");

                TextView tvManifestBody = dialogView.findViewById(R.id.tv_not_manifested_body);
                tvManifestBody.setText("Waybill " + onLineValidation.getWaybillNo() + " is not manifested yet.");
            }

            if (onLineValidation.getIsStopped() == 1 && onLineValidation.getisStoppedalert()) {
                isshowPopup = true;
                isAddPiece = false;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                tvStopShipmentHeader.setText("Stop Shipment");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                tvStopShipmentBody.setText("Stop shipment , Please hold.");

            }

            if (onLineValidation.getIsDeliveryRequest() == 1 && onLineValidation.isDLalert()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_delivery_req);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_delivery_req_header);
                tvStopShipmentHeader.setText("DL Request");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_delivery_req_body);
                tvStopShipmentBody.setText("This Shipment has DL Request.");

            }


            if (onLineValidation.getIsCITCComplaint() == 1 && onLineValidation.getisCITCalert()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_citc_complaint);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_citc_header);
                tvStopShipmentHeader.setText("CITC Complaint");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_citc_body);
                tvStopShipmentBody.setText("The Shipment has a CITC Complaint.");

            }

            if (onLineValidation.getIsCAFRequest() == 1 && onLineValidation.isCAFlert()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_caf_complaint);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_caf_header);
                tvStopShipmentHeader.setText("CAF Request");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_caf_body);
                tvStopShipmentBody.setText("The Shipment has a CAF Request.");

            }
            if (onLineValidation.getIsConflict() == 1 && onLineValidation.isConflictalert()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_ds_validation);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_ds_validation_header);
                tvStopShipmentHeader.setText("DS Validation");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_ds_validation_body);
                tvStopShipmentBody.setText("Shipment is not belong to employee.");
            }
            if (onLineValidation.getIsRTORequest() == 1 && onLineValidation.isRTOalert()) {
                isshowPopup = true;
//                isAddPiece = false;
                isAddPiece = isPieceAddintoList(onLineValidation);
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_rto);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_rto_header);
                tvStopShipmentHeader.setText("RTO Request");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_rto_body);
                tvStopShipmentBody.setText("This Shipment has a RTO Request.");

            }


            if (onLineValidation.getIsMultiPiece() == 1 && onLineValidation.getisMultiPiecealert()) {
                isshowPopup = true;
                LinearLayout llMultiPiece = dialogView.findViewById(R.id.ll_is_multi_piece);
                llMultiPiece.setVisibility(View.VISIBLE);

                TextView tvMultiPieceHeader = dialogView.findViewById(R.id.tv_multiPiece_header);
                tvMultiPieceHeader.setText("Multi Piece");

                TextView tvMultiPieceBody = dialogView.findViewById(R.id.tv_multiPiece_body);
                tvMultiPieceBody.setText("Please check pieces.");
            }

            if (onLineValidation.getNoOfAttempts() > 1 && onLineValidation.getisNoofAttemptsalert()) {
                isshowPopup = true;
                LinearLayout ll_no_attempts = dialogView.findViewById(R.id.ll_no_attempts);
                ll_no_attempts.setVisibility(View.VISIBLE);

                TextView tvMultiPieceHeader = dialogView.findViewById(R.id.tv_no_attempts_header);
                tvMultiPieceHeader.setText("Number of Attempts");

                TextView tvMultiPieceBody = dialogView.findViewById(R.id.tv_no_of_attempts_body);
                tvMultiPieceBody.setText(String.valueOf(onLineValidation.getNoOfAttempts()));
            }


            if (onLineValidation.getisHV()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_hv);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_hv_header);
                tvStopShipmentHeader.setText("HV Shipment");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_hv_body);
                tvStopShipmentBody.setText("This is HV Shipment ");

            }

            if (onLineValidation.getWaybillDestID() > 0 && onLineValidation.getWaybillDestID() != GlobalVar.GV().StationID && onLineValidation.getisWrongDestalert()) {
                isAddPiece = isPieceAddintoList(onLineValidation);
                isshowPopup = true;
                ismispop = true;
                LinearLayout llDifDest = dialogView.findViewById(R.id.ll_ncl_wrong_dest);
                llDifDest.setVisibility(View.VISIBLE);

                TextView tvNclHeader = dialogView.findViewById(R.id.tv_ncl_header);
                tvNclHeader.setText("Wrong Destination");

                TextView tvNclBody = dialogView.findViewById(R.id.tv_ncl_body);
                tvNclBody.setText("Please choose exception:");

                isactivebtn_Miscode_Missort(dialogView);
                btnConfirm.setVisibility(View.GONE);
                // add on click listener

            }


            if (onLineValidation.isMultipiecePopup())
                setCallback(onLineValidation);

            if (!ismispop && isAddPiece && isshowPopup && onLineValidation.getIsMultiPiece() > 0)
                isEnableMultipiecepopup(dialogView);
//                callback(0);

            alertDialog = dialogBuilder.create();
            if (isshowPopup)
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
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return isAddPiece;
    }

    public boolean showFlagsPopupCount(final OnLineValidation onLineValidation, final Context context, Activity activity) {

        boolean isshowPopup = false, isAddPiece = true, ismispop = false;
        try {
            this.activity = activity;


            GlobalonLineValidation = onLineValidation;
//            GlobalVar.MakeSound(context, R.raw.wrongbarcodescan);

            android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(activity);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_alert_dialog_new, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);

            TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
            tvBarcode.setText("NCLNo #" + onLineValidation.getBarcode() + "\n" + " WaybillCount " + onLineValidation.getWaybillNo());
            Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
            btnConfirm.setVisibility(View.VISIBLE);
            btnConfirm.setText("OK & Quit");


            if (onLineValidation.getIsStopped() > 0 && onLineValidation.getisStoppedalert()) {
                isshowPopup = true;
                isAddPiece = false;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                tvStopShipmentHeader.setText("Stop Shipment");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                tvStopShipmentBody.setText("Counts - " + String.valueOf(onLineValidation.getIsStopped()));

            }

            if (onLineValidation.getIsDeliveryRequest() > 0 && onLineValidation.isDLalert()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_delivery_req);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_delivery_req_header);
                tvStopShipmentHeader.setText("DL Request");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_delivery_req_body);
                tvStopShipmentBody.setText("Counts - " + String.valueOf(onLineValidation.getIsDeliveryRequest()));

            }


            if (onLineValidation.getIsCITCComplaint() > 0 && onLineValidation.getisCITCalert()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_citc_complaint);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_citc_header);
                tvStopShipmentHeader.setText("CITC Complaint");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_citc_body);
                tvStopShipmentBody.setText("Counts - " + String.valueOf(onLineValidation.getIsCITCComplaint()));

            }

            if (onLineValidation.getIsCAFRequest() > 0 && onLineValidation.isCAFlert()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_caf_complaint);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_caf_header);
                tvStopShipmentHeader.setText("CAF Request");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_caf_body);
                tvStopShipmentBody.setText("Counts - " + String.valueOf(onLineValidation.getIsCAFRequest()));

            }


            if (onLineValidation.getIsRTORequest() > 0 && onLineValidation.isRTOalert()) {
                isshowPopup = true;
//                isAddPiece = false;
                isAddPiece = isPieceAddintoList(onLineValidation);
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_rto);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_rto_header);
                tvStopShipmentHeader.setText("RTO Request");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_rto_body);
                tvStopShipmentBody.setText("Counts - " + String.valueOf(onLineValidation.getIsRTORequest()));

            }


           /*

            if (onLineValidation.getIsManifested() == 0 && onLineValidation.getisManifestedalert()) {
                isshowPopup = true;
                LinearLayout llNoManifest = dialogView.findViewById(R.id.ll_not_manifested);
                llNoManifest.setVisibility(View.VISIBLE);

                TextView tvManifestHeader = dialogView.findViewById(R.id.tv_not_manifested_header);
                tvManifestHeader.setText("Manifest");

                TextView tvManifestBody = dialogView.findViewById(R.id.tv_not_manifested_body);
                tvManifestBody.setText("Waybill " + onLineValidation.getWaybillNo() + " is not manifested yet.");
            }

          if (onLineValidation.getIsConflict() == 1 && onLineValidation.isConflictalert()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_ds_validation);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_ds_validation_header);
                tvStopShipmentHeader.setText("DS Validation");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_ds_validation_body);
                tvStopShipmentBody.setText("Shipment is not belong to employee.");
            }
            if (onLineValidation.getIsMultiPiece() == 1 && onLineValidation.getisMultiPiecealert()) {
                isshowPopup = true;
                LinearLayout llMultiPiece = dialogView.findViewById(R.id.ll_is_multi_piece);
                llMultiPiece.setVisibility(View.VISIBLE);

                TextView tvMultiPieceHeader = dialogView.findViewById(R.id.tv_multiPiece_header);
                tvMultiPieceHeader.setText("Multi Piece");

                TextView tvMultiPieceBody = dialogView.findViewById(R.id.tv_multiPiece_body);
                tvMultiPieceBody.setText("Please check pieces.");
            }

            if (onLineValidation.getNoOfAttempts() > 1 && onLineValidation.getisNoofAttemptsalert()) {
                isshowPopup = true;
                LinearLayout ll_no_attempts = dialogView.findViewById(R.id.ll_no_attempts);
                ll_no_attempts.setVisibility(View.VISIBLE);

                TextView tvMultiPieceHeader = dialogView.findViewById(R.id.tv_no_attempts_header);
                tvMultiPieceHeader.setText("Number of Attempts");

                TextView tvMultiPieceBody = dialogView.findViewById(R.id.tv_no_of_attempts_body);
                tvMultiPieceBody.setText(String.valueOf(onLineValidation.getNoOfAttempts()));
            }


            if (onLineValidation.getisHV()) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_hv);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_hv_header);
                tvStopShipmentHeader.setText("HV Shipment");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_hv_body);
                tvStopShipmentBody.setText("This is HV Shipment ");

            }

            if (onLineValidation.getWaybillDestID() > 0 && onLineValidation.getWaybillDestID() != GlobalVar.GV().StationID && onLineValidation.getisWrongDestalert()) {
                isAddPiece = isPieceAddintoList(onLineValidation);
                isshowPopup = true;
                ismispop = true;
                LinearLayout llDifDest = dialogView.findViewById(R.id.ll_ncl_wrong_dest);
                llDifDest.setVisibility(View.VISIBLE);

                TextView tvNclHeader = dialogView.findViewById(R.id.tv_ncl_header);
                tvNclHeader.setText("Wrong Destination");

                TextView tvNclBody = dialogView.findViewById(R.id.tv_ncl_body);
                tvNclBody.setText("Please choose exception:");

                isactivebtn_Miscode_Missort(dialogView);
                btnConfirm.setVisibility(View.GONE);
                // add on click listener

            }
*/

           /* if (onLineValidation.isMultipiecePopup())
                setCallback(onLineValidation);
*/
            /*if (!ismispop && isAddPiece && isshowPopup && onLineValidation.getIsMultiPiece() > 0)
                isEnableMultipiecepopup(dialogView);*/
//                callback(0);

            alertDialog = dialogBuilder.create();
            if (isshowPopup)
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
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return isAddPiece;
    }

    private boolean isPieceAddintoList(OnLineValidation onLineValidation) {
        boolean isAddPiece = true;
        if (onLineValidation.getClassName().equals("DeliverySheet"))
            isAddPiece = false;


        return isAddPiece;

    }

    View rootView;

    private void setCallback(OnLineValidation onLineValidation) {
        if (onLineValidation.getClassName().equals("DeliverySheet"))
            alertCallback = ((DeliverySheetActivity) activity).thirdFragment;
        ;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_miscode:
//                mis_sort = 0;
                disablebtn();
                radioGroupCheckListener();
//                Toast.makeText(v.getContext(), "Miscode", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_missort:
//                mis_sort = 1;
                //disablebtn();
                updateWaybillDestination(GlobalonLineValidation, 1);
                break;
            case R.id.btn_confirm:
                updateWaybillDestination(GlobalonLineValidation, 0);
                break;
            case R.id.btn_ok:
//                callback(1);
                alertCallback.returnOk(1, activity, GlobalonLineValidation);
                dismissCustomAlertDialog();
                break;
            case R.id.btn_no:
                alertCallback.returnOk(0, activity, GlobalonLineValidation);
                dismissCustomAlertDialog();
//                callback(0);

                break;


        }
    }


    private void callback(int i) {
        DeliverySheetThirdFragment deliverySheetThirdFragment = ((DeliverySheetActivity) activity).thirdFragment;
        deliverySheetThirdFragment.isAddPiece(i, activity, GlobalonLineValidation);
        dismissCustomAlertDialog();
    }

    private void radioGroupCheckListener() { //, final Button btnConfirm
        try {

            final RadioButton rbDiscard = rootView.findViewById(R.id.rb_change_discard);
            RadioGroup radioGroup = rootView.findViewById(R.id.rg_change_dest);
            radioGroup.setVisibility(View.VISIBLE);
            progressBar = rootView.findViewById(R.id.progressbar);
            //final Button btnConfirm = rootView.findViewById(R.id.btn_confirm);
            final Button btnConfirm = EnableOkbtn();

//            btnConfirm.setText("OK & Quit");

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_change_dest:
                            btnConfirm.setText("OK & Save");
//                        isDestChanged = true;

                            showprogressbar(progressBar);
                            searchableSpinnerDest = (SearchableSpinner) rootView.findViewById(R.id.spinner_ncl_destinations);
                            searchableSpinnerDest.setVisibility(View.VISIBLE);
                            ArrayAdapter<Station> addressArrayAdapter = new ArrayAdapter<>(rootView.getContext(),
                                    R.layout.support_simple_spinner_dropdown_item, getAllowedDestCode());
                            addressArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            searchableSpinnerDest.setAdapter(addressArrayAdapter);
                            searchableSpinnerDest.setTitle("Change Piece Destination");
                            rbDiscard.setVisibility(View.VISIBLE);
                            hideprogressbar(progressBar);
                            disablebtn(btnConfirm);

                            break;
                        case R.id.rb_change_discard:
//                        btnConfirm.setText("OK & Quit");
//                        isDestChanged = false;
                            Enablebtn(btnConfirm);
                            searchableSpinnerDest.setVisibility(View.GONE);
                            rbDiscard.setVisibility(View.GONE);
                            break;

                    }
                }
            });
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private ArrayList<Station> getAllowedDestCode() {
//        SweetAlertDialog sweetAlertDialog = sweetAlertDialogprogessbar();

        SelectData selectData = new SelectData();
        StaticClass.stationArrayList.addAll(selectData.getStationsData(rootView.getContext()));

//        if (StaticClass.allowedDestStationIDs.size() == 0)
//            StaticClass.allowedDestStationIDs = AllowedFacilityStations(rootView.getContext(), 0);
//
//        if (StaticClass.stationArrayList.size() == 0) {
//
//            DBConnections dbConnections = new DBConnections(rootView.getContext(), null);
//            for (int i = 0; i < StaticClass.allowedDestStationIDs.size(); i++) {
//                Station tempStation = dbConnections.getStationByID(StaticClass.allowedDestStationIDs.get(i), rootView.getContext());
//                if (tempStation != null)
//                    StaticClass.stationArrayList.add(tempStation);
//            }
//    }

//        dismissDialog(sweetAlertDialog);

        return StaticClass.stationArrayList;
    }


    private void showprogressbar(ProgressBar progressBar) {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideprogressbar(ProgressBar progressBar) {
        progressBar.setVisibility(View.GONE);
    }
 /*  private OnLineValidation isValid(OnLineValidation onLineValidationLocal) {

        OnLineValidation onLineValidation = new OnLineValidation();

        if (onLineValidationLocal.getWaybillDestID() != GlobalVar.GV().StationID) {
            onLineValidation.setIsWrongDest(1);
//            onLineValidation.setWaybillDestID(onLineValidationLocal.getWaybillDestID());
//            isValid = false;
        }

        if (onLineValidationLocal.getIsMultiPiece() == 1) {
            onLineValidation.setIsMultiPiece(1);
//            isValid = false;
        }

        if (onLineValidationLocal.getIsStopped() == 1) {
            onLineValidation.setIsStopped(1);
//            isValid = false;
        }

        if (onLineValidationLocal.getIsRelabel() == 1) {
            onLineValidation.setIsRelabel(1);
//            isValid = false;
        }


//        if (!isValid) {
//            onLineValidation.setBarcode(barcode);
//            onLineValidationList.add(onLineValidation);
//        }
        return onLineValidation;
    }*/
   /* private SweetAlertDialog sweetAlertDialogprogessbar() {

        SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(activity, SweetAlertDialog.PROGRESS_TYPE);
        sweetAlertDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        sweetAlertDialog.setTitleText("Fetching Destination Staions, Please wait...");
        sweetAlertDialog.setCancelable(false);
        sweetAlertDialog.show();

        return sweetAlertDialog;
    }

    private void dismissDialog(SweetAlertDialog sweetAlertDialog) {
        if (sweetAlertDialog != null && sweetAlertDialog.isShowing()) {
            sweetAlertDialog.dismissWithAnimation();
        }

    }*/

    private void updateWaybillDestination(OnLineValidation onLineValidation, int miscode_misort) {

        if (GlobalVar.GV().EmployID == 0)
            GlobalVar.GV().EmployID = GlobalVar.getlastlogin(rootView.getContext());

        try {
            Station station = null;
            if (miscode_misort == 0) //0 - MisCode
                station = (Station) searchableSpinnerDest.getSelectedItem();
            ArrayList<String> locaArrayList = isLocationEnabled();
            String Lat = "0";
            String Long = "0";
            if (locaArrayList.size() > 1) {
                Lat = locaArrayList.get(0);
                Long = locaArrayList.get(1);
            }

            UpdateWaybillRequest request = new UpdateWaybillRequest();
            request.setWaybillNo(onLineValidation.getWaybillNo());
            request.setEmployeeID(GlobalVar.GV().EmployID);
            request.setBarcode(onLineValidation.getBarcode());
            request.setLatitude(Lat);
            request.setLongitude(Long);
            request.setAppVersion(GlobalVar.GV().AppVersion);
            request.setMultiPiecePopup(onLineValidation.isMultipiecePopup());

            if (miscode_misort == 0) {
                request.setNewWaybillDestID(station.ID);
                request.setStationName(station.Name);
            }

//            String jsonData = JsonSerializerDeserializer.serialize(request, true);
            if (miscode_misort == 0)
                updateDestintoServer(request);
            else if (miscode_misort == 1)
                insertMisscodeCheckpoint(request, 1);


//            new UpdateWaybill().execute(jsonData);

        } catch (Exception e) {

        }
    }

    private ArrayList<String> isLocationEnabled() {
        ArrayList<String> location = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(rootView.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            location = requestLocation();
        } else {
            location.add("0");
            location.add("0");
        }

        return location;

    }

    private ArrayList<String> requestLocation() {
        Location location = GlobalVar.getLastKnownLocation(rootView.getContext());
        ArrayList<String> locationList = new ArrayList<>();

        if (location != null) {
            locationList.add(String.valueOf(location.getLatitude()));
            locationList.add(String.valueOf(location.getLongitude()));
        } else {
            locationList.add("0");
            locationList.add("0");
        }
        return locationList;
    }

    private void updateDestintoServer(final UpdateWaybillRequest updateWaybillRequest) {
        showprogressbar(progressBar);

        UpdatingWaybillDestinationApi.UpdatingWaybillDestinationApi(new Callback<CommonResult>() {
            @Override
            public void returnResult(CommonResult result) {
                if (!result.getHasError()) {
                    updateLocalDest(updateWaybillRequest);
                    insertMisscodeCheckpoint(updateWaybillRequest, 0);
                    GlobalVar.ShowDialog(activity, "Info", "Your request has been updated sucessfully.", true);
                }
                //finish();
                else
                    GlobalVar.ShowDialog(activity, "Error", result.getErrorMessage(), true);


            }

            @Override
            public void returnError(String message) {
                GlobalVar.ShowDialog(activity, "Error", message.toString(), true);
                dismissCustomAlertDialog();
            }
        }, updateWaybillRequest);

    }

    private void dismissCustomAlertDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
            alertDialog = null;
        }
    }

    private void updateLocalDest(UpdateWaybillRequest updateWaybillRequest) {
        String waybillNo = String.valueOf(updateWaybillRequest.getWaybillNo());
        String barcode = String.valueOf(updateWaybillRequest.getBarcode());

        PieceDetail pieceDetail = new PieceDetail();
        pieceDetail.setBarcode(barcode);
        pieceDetail.setWaybill(waybillNo);
        pieceDetail.setWeight(0);

        UpdateData updateData = new UpdateData();
        updateData.updateWaybillDestID_offset(rootView.getContext(), waybillNo, updateWaybillRequest.getNewWaybillDestID());

    }

    private void disablebtn() {

        LinearLayout llDifDest = rootView.findViewById(R.id.ll_wrongdest_miscode_misort);
        llDifDest.setVisibility(View.GONE);

    }

    private void disablebtn(Button btn_confirm) {

        LinearLayout llDifDest = rootView.findViewById(R.id.ll_wrongdest_miscode_misort);
        llDifDest.setVisibility(View.GONE);
        btn_confirm.setVisibility(View.VISIBLE);

    }

    private void Enablebtn(Button btn_confirm) {
        LinearLayout llDifDest = rootView.findViewById(R.id.ll_wrongdest_miscode_misort);
        llDifDest.setVisibility(View.VISIBLE);
        btn_confirm.setVisibility(View.GONE);

    }

    private void insertMisscodeCheckpoint(UpdateWaybillRequest updateWaybillRequest, int miscode_misort) {
        DBConnections dbConnections = new DBConnections(rootView.getContext(), null);
        ArrayList<String> location = isLocationEnabled();
        String Lat = "0";
        String Long = "0";
        if (location.size() > 1) {
            Lat = location.get(0);
            Long = location.get(1);
        }

        String stationname = "";

        if (miscode_misort == 0)
            stationname = updateWaybillRequest.getStationName();

        TerminalHandling checkPoint = new TerminalHandling
                (GlobalonLineValidation.getReasonID(), Lat,
                        Long, GetReasonDID(GlobalonLineValidation.getReasonID(), miscode_misort), stationname, stationname, 1);

        int ID = 0;
        if (dbConnections.InsertTerminalHandling(checkPoint, rootView.getContext())) {
            ID = dbConnections.getMaxID("CheckPoint", rootView.getContext());

            if (ID > 0) {
                CheckPointBarCodeDetails checkPointBarCodeDetails = new CheckPointBarCodeDetails(updateWaybillRequest.getBarcode(), ID);
                dbConnections.InsertCheckPointBarCodeDetails(checkPointBarCodeDetails, rootView.getContext());

            }
        }

        restartService();
        if (miscode_misort == 1)
            GlobalVar.ShowDialog(activity, "Info", "Your request has been updated sucessfully.", true);

        if (updateWaybillRequest.isMultiPiecePopup())
            isEnableMultipiecepopup(rootView);
        else
            dismissCustomAlertDialog();
    }

    private Integer GetReasonDID(int reasonID, int subReason) {
        if (reasonID == 7 && subReason == 0) //0 Miscode 1 // Missort
            return activity.getResources().getInteger(R.integer.ArrivedAtMisscode);
        else if (reasonID == 7 && subReason == 1)
            return activity.getResources().getInteger(R.integer.ArrivedAtMissort);

        return 0;
    }

    private void restartService() {
        if (!GlobalVar.isMyServiceRunning(com.naqelexpress.naqelpointer.service.TerminalHandling.class, rootView.getContext())) {
            activity.startService(
                    new Intent(activity,
                            com.naqelexpress.naqelpointer.service.TerminalHandling.class));
        }
    }

    private Button EnableOkbtn() {
        final Button btnConfirm = rootView.findViewById(R.id.btn_confirm);

        btnConfirm.setOnClickListener(this);

        return btnConfirm;
    }


    public static boolean isValidPieceBarcode(String barcode, Activity activity, Context context, String KeyName, boolean isConflict,
                                              boolean isCount) {
        boolean isAddPiece = true;
        try {
            DBConnections dbConnections = new DBConnections(context, null);
            SetOnlineValidationAlert setOnlineValidationAlert = new SetOnlineValidationAlert();
            OnLineValidation onLineValidationLocal = null;
            if (!isCount)
                onLineValidationLocal = dbConnections.getPieceInformationByWaybillNo(GlobalVar.getWaybillFromBarcode(barcode)
                        , barcode, context, isCount);
            else
                onLineValidationLocal = dbConnections.getPieceCountByNCL(barcode
                        , barcode, context, isCount);

            if (KeyName.equals("ArrivedAt"))
                onLineValidationLocal.setReasonID(Objects.requireNonNull(activity).getResources().getInteger(R.integer.ArrivedAt));
            else if (KeyName.equals("DeliverySheet")) {
                onLineValidationLocal.setReasonID(Objects.requireNonNull(activity).getResources().getInteger(R.integer.DeliverySheet));
                DeliverySheetThirdFragment.isMulitPiecePop = onLineValidationLocal.getIsMultiPiece();
            } else if (KeyName.equals("INV"))
                onLineValidationLocal.setReasonID(Objects.requireNonNull(activity).getResources().getInteger(R.integer.INV));
            else if (KeyName.equals("VDS")) {
                onLineValidationLocal.setReasonID(Objects.requireNonNull(activity).getResources().getInteger(R.integer.VDS));
                onLineValidationLocal.setIsConflict(isConflict ? 1 : 0);
            } else if (KeyName.equals("INV_byNCL"))
                onLineValidationLocal.setReasonID(Objects.requireNonNull(activity).getResources().getInteger(R.integer.INV_byNCL));
            else if (KeyName.equals("INV_byPiece"))
                onLineValidationLocal.setReasonID(Objects.requireNonNull(activity).getResources().getInteger(R.integer.INV_byPiece));


            onLineValidationLocal = setOnlineValidationAlert.setOnlineValidationalert(onLineValidationLocal, activity, KeyName);

            OnlineValidation onlineValidation = new OnlineValidation();
            if (!isCount)
                isAddPiece = onlineValidation.showFlagsPopup(onLineValidationLocal, context, activity);

            else if (isCount)
                isAddPiece = onlineValidation.showFlagsPopupCount(onLineValidationLocal, context, activity);

        } catch (Exception e) {

        }
        return isAddPiece;
    }

    public boolean ShowMultiPieceAlertMessage(String Message, Activity activity, String title) {

//        final boolean isAdd = false;
        isAdd = false;
        AlertDialog alertDialog = new AlertDialog.Builder(activity).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(Message);
        alertDialog.setCancelable(false);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        isAdd = true;
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        alertDialog.show();

        return isAdd;
    }

    private void isEnableMultipiecepopup(View rootView) {

        LinearLayout llmultipopup = rootView.findViewById(R.id.ll_multipiececonfirm);
        llmultipopup.setVisibility(View.VISIBLE);
        LinearLayout llDifDest = rootView.findViewById(R.id.ll_wrongdest_miscode_misort);
        llDifDest.setVisibility(View.GONE);
        Button btnConfirm = rootView.findViewById(R.id.btn_confirm);
        btnConfirm.setVisibility(View.GONE);

        Button ok = rootView.findViewById(R.id.btn_ok);
        Button no = rootView.findViewById(R.id.btn_no);
        ok.setOnClickListener(this);
        no.setOnClickListener(this);
    }

    private boolean isAddpiece(boolean isAdd) {
        return isAdd;
    }

    private void isactivebtn_Miscode_Missort(View view) {

        LinearLayout llDifDest = view.findViewById(R.id.ll_wrongdest_miscode_misort);
        llDifDest.setVisibility(View.VISIBLE);
        Button btn_miscode = view.findViewById(R.id.btn_miscode);
        btn_miscode.setOnClickListener(this);
        Button btn_missort = view.findViewById(R.id.btn_missort);
        btn_missort.setOnClickListener(this);
        rootView = view;

    }

}