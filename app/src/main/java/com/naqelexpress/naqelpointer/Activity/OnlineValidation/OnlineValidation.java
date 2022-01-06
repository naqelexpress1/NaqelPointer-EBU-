package com.naqelexpress.naqelpointer.Activity.OnlineValidation;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.DB.SelectData;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.Retrofit.Models.OnLineValidation;
import com.naqelexpress.naqelpointer.utils.StaticClass;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;

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

    public void showFlagsPopup(final OnLineValidation onLineValidation, final Context context, Activity activity) {
        try {
            this.activity = activity;
            boolean isshowPopup = false;

            GlobalVar.MakeSound(context, R.raw.wrongbarcodescan);

            final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(context);
            LayoutInflater inflater = activity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.custom_alert_dialog_new, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setCancelable(false);

            final TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
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

            if (onLineValidation.getWaybillDestID() != GlobalVar.GV().StationID) {
                isshowPopup = true;
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

            if (onLineValidation.getIsManifested() == 0) {
                isshowPopup = true;
                LinearLayout llNoManifest = dialogView.findViewById(R.id.ll_not_manifested);
                llNoManifest.setVisibility(View.VISIBLE);

                TextView tvManifestHeader = dialogView.findViewById(R.id.tv_not_manifested_header);
                tvManifestHeader.setText("Manifest");

                TextView tvManifestBody = dialogView.findViewById(R.id.tv_not_manifested_body);
                tvManifestBody.setText("Waybill " + onLineValidation.getWaybillNo() + " is not manifested yet.");
            }

            if (onLineValidation.getIsStopped() == 1) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                tvStopShipmentHeader.setText("Stop Shipment");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                tvStopShipmentBody.setText("Stop shipment , Please hold.");

            }


            if (onLineValidation.getIsCITCComplaint() == 1) {
                isshowPopup = true;
                LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_citc_complaint);
                llStopShipment.setVisibility(View.VISIBLE);

                TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_citc_header);
                tvStopShipmentHeader.setText("CITC Complaint");

                TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_citc_body);
                tvStopShipmentBody.setText("The Shipment has a CITC Complaint.");

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

            final android.app.AlertDialog alertDialog = dialogBuilder.create();
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
    }

    View rootView;

    private void isactivebtn_Miscode_Missort(View view) {

        LinearLayout llDifDest = view.findViewById(R.id.ll_wrongdest_miscode_misort);
        llDifDest.setVisibility(View.VISIBLE);
        Button btn_miscode = view.findViewById(R.id.btn_miscode);
        btn_miscode.setOnClickListener(this);
        Button btn_missort = view.findViewById(R.id.btn_missort);
        btn_missort.setOnClickListener(this);
        rootView = view;

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_miscode:
                radioGroupCheckListener();
                Toast.makeText(v.getContext(), "Miscode", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_missort:

                break;

        }
    }

    private void radioGroupCheckListener() { //, final Button btnConfirm
        try {

            final RadioButton rbDiscard = rootView.findViewById(R.id.rb_change_discard);
            RadioGroup radioGroup = rootView.findViewById(R.id.rg_change_dest);
            radioGroup.setVisibility(View.VISIBLE);
            final ProgressBar progressBar = rootView.findViewById(R.id.progressbar);

            radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    switch (checkedId) {
                        case R.id.rb_change_dest:
//                        btnConfirm.setText("OK & Save");
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

                            break;
                        case R.id.rb_change_discard:
//                        btnConfirm.setText("OK & Quit");
//                        isDestChanged = false;
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

}