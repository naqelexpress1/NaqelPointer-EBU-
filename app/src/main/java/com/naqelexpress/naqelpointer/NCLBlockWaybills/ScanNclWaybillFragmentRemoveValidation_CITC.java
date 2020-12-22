package com.naqelexpress.naqelpointer.NCLBlockWaybills;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.BarcodeValidation;
import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.Ncl;
import com.naqelexpress.naqelpointer.DB.DBObjects.NclDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Request.UpdateWaybillRequest;
import com.naqelexpress.naqelpointer.OnlineValidation.OnLineValidation;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.NclServiceBulk;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

//Used By GWT (IRS)
public class ScanNclWaybillFragmentRemoveValidation_CITC extends Fragment {
    View rootView;
    public EditText txtBarcode;
    TextView lbTotal, lbNclNo, txtCitcCount, inserteddate, validupto;
    public static ArrayList<String> WaybillList = new ArrayList<>();

    private RecyclerView recyclerView;
    public NclAdapterRemoveValidation_CITC adapter;
    private SearchableSpinner searchableSpinnerDest;


    public static ArrayList<PieceDetail> PieceCodeList = new ArrayList<PieceDetail>();
    public static double waybillweight = 0.0;
    int WaybillCount = 0, PiecesCount = 0;
    ArrayList<String> isduplicate = new ArrayList<>();
    public ArrayList<String> isrtoReq = new ArrayList<>();
    public ArrayList<String> isdeliveryReq = new ArrayList<>();
    private List<Integer> allowedDestStationIDs = new ArrayList<>();

    private boolean isNCLDestChosen , isDestChanged;
    private int NCLDestStationID , newDestID;
    private DBConnections dbConnections = new DBConnections(getContext(), null);
    private List<OnLineValidation> onLineValidationList = new ArrayList<>();

    private final static String TAG = "ScanNclWaybillFragmentRemoveValidation_CITC";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (rootView == null) {
            rootView = inflater.inflate(R.layout.scannclwaybill, container, false);


            txtBarcode = (EditText) rootView.findViewById(R.id.txtBarcode);

            if (!isNCLDestChosen) {
                txtBarcode.setEnabled(false);
                txtBarcode.setHint("Generate NCL No first");
            }


            txtCitcCount = (TextView) rootView.findViewById(R.id.citccount);
            txtCitcCount.setVisibility(View.GONE);
            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ll);
            ll.setVisibility(View.GONE);
            inserteddate = (TextView) rootView.findViewById(R.id.inserteddate);
            validupto = (TextView) rootView.findViewById(R.id.validupto);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);
            lbNclNo = (TextView) rootView.findViewById(R.id.lbNclNo);
            WaybillCount = 0;
            PiecesCount = 0;
            PieceCodeList.clear();

            Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);
            btnOpenCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!GlobalVar.GV().checkPermission(getActivity(), GlobalVar.PermissionType.Camera)) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else {
                        Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        startActivityForResult(intent, GlobalVar.GV().CAMERA_PERMISSION_REQUEST);
                    }
                }
            });

            txtBarcode.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    // If the event is a key-down event on the "enter" button
                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return true;
                    else if (keyCode == KeyEvent.KEYCODE_BACK) {
                        onBackpressed();
                        return true;
                    } else if (keyCode == KeyEvent.KEYCODE_ENTER) {

                        String barcode = txtBarcode.getText().toString();
                        PieceDetail pieceDetail = new PieceDetail(barcode,"0" , 0);

                        BarcodeValidation barcodeValidation = validateBarcode(barcode);


                        if (barcodeValidation.isValid) {
                            boolean hasFlag = checkWaybillFlags(barcode);
                            if (hasFlag)
                                showFlagsPopup(getOnLineValidationPiece(barcode));
                            else
                                AddNewPieceTest(pieceDetail);
                        } else {
                            GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                            GlobalVar.GV().ShowSnackbar(rootView, "Wrong Barcode", GlobalVar.AlertType.Error);
                        }

                        return true;
                    }
                    return false;
                }
            });


//            txtBarcode.addTextChangedListener(new TextWatcher() {
//                @Override
//                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                }
//
//                @Override
//                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                }
//
//                @Override
//                public void afterTextChanged(Editable s) {
//                    if (txtBarcode != null && txtBarcode.getText().toString().length() == 13) {
//
//                        //AddNewWaybill(String.valueOf(barcodeInfoResult.WayBillNo));
//                        AddNewPiece(txtBarcode.getText().toString(), "0", 0);
//
//                    }
//
//                }
//            });
//
        }

        ReadFromLocal();
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        GlobalVar.GV().MakeSound(getContext(), R.raw.barcodescanned);
                        txtBarcode.setText(barcode);
                    }
                }
            }
        }
    }



    private void AddNewPieceTest(PieceDetail pieceDetail) {
        GlobalVar.hideKeyboardFrom(getContext(), rootView);
        AddPieceTest(pieceDetail);
    }


    //Check valid barcode
    //check not duplicate
    //check length 13
    //check bayan

      //GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
      //GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
      //txtBarcode.setText("");

    private void AddPieceTest(PieceDetail pieceDetail) {

                isduplicate.add(pieceDetail.Barcode);
                PiecesCount = PiecesCount + 1;
                PieceCodeList.add(0, new PieceDetail(pieceDetail.Barcode, pieceDetail.Waybill, pieceDetail.Weight));
                waybillweight = waybillweight + pieceDetail.Weight;
                lbTotal.setText(getString(R.string.lbCount) + String.valueOf(isduplicate.size()));
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);

                txtBarcode.setText("");
                txtBarcode.requestFocus();
                initViews();

        if (PieceCodeList.size() == 20) {
            SaveData(PieceCodeList, WaybillList);
        }
    }



    private void AddNewPiece(String PieceCode, String WaybillNo, double Weight) {

        //Riyam
      /*  if (!GlobalVar.GV().isValidBarcodeCons(PieceCode)) {
            GlobalVar.GV().ShowSnackbar(rootView, "Wrong Barcode", GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarcode.setText("");
            return;
        }*/

        if (!ScanNclNoFragment.pieceDenied.contains(txtBarcode.getText().toString())) {

            if (GlobalVar.GV().ValidateAutomacticDate(getContext())) {
//            if (!GlobalVar.GV().IsAllowtoScan(validupto.getText().toString().replace("Upto : ", ""))) { //validupto.getText().toString()
//                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
//                ErrorAlert("Info", "Data is Expired kindly Load today Data , (Press Bring Data)", PieceCode, WaybillNo, Weight);
//                return;
//            }

            } else {
                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                GlobalVar.RedirectSettings(getActivity());
                return;
            }

            //Commented by ismail  09-09-2020 by ashik no need this function any more
            //  GetNCLDatafromDB(txtBarcode.getText().toString());

            boolean rtoreq = false;
            boolean ismatch = false;


            if (iscitcshipments.contains(txtBarcode.getText().toString())) {

                ismatch = true;
                GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
                ErrorAlert("CITC Complaint", "This Waybill Number(" + txtBarcode.getText().toString() + ") has CITC Complaint ", PieceCode, WaybillNo, Weight);
                return;

            }

            if (!ismatch) {
                if (isdeliveryReq.contains(txtBarcode.getText().toString())) {
                    if (isrtoReq.contains(txtBarcode.getText().toString())) {
                        ismatch = true;
                        rtoreq = true;

                        GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
                        ErrorAlert("Delivery/RTO Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For Delivery & RTO ", PieceCode, WaybillNo, Weight);

                        return;
                    } else {

                        GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
                        ErrorAlert("Delivery Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For Delivery ", PieceCode, WaybillNo, Weight);
                        return;
                    }


                }
            }
            if (!rtoreq) {
                if (isrtoReq.contains(txtBarcode.getText().toString())) {

                    GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
                    ErrorAlert("RTO Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For RTO ", PieceCode, WaybillNo, Weight);
                    return;

                }
            }

//        if (iscitcshipments.contains(txtBarcode.getText().toString())) {
//
//
//            GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
//            ErrorAlert("CITC Complaint", "This Waybill Number(" + txtBarcode.getText().toString() + ") has CITC Complaint ");
//            //  return;
//
//        }
        } else {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this.getContext());
            builder.setTitle("No Bayan No");
            builder.setMessage("This piece(" + txtBarcode.getText().toString() + ") no Bayan No.kindly contact GateWay team ")

                    .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
//                                lbTotal.setText(getString(R.string.lbCount) + DeliveryBarCodeList.size());
                            txtBarcode.setText("");
                        }
                    })
                    .setCancelable(true);
            android.support.v7.app.AlertDialog alertDialog = builder.create();
            alertDialog.show();
            ;
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarcode.setText("");
        }
        GlobalVar.hideKeyboardFrom(getContext(), rootView);

        AddPiece(PieceCode, WaybillNo, Weight);
        //if (!IsDuplicate(PieceCode)) {


    }

    private void AddPiece(String PieceCode, String WaybillNo, double Weight) {
        if (!isduplicate.contains(PieceCode)) {
            if (PieceCode.length() == 13) {
                //SaveData(WaybillNo, PieceCode);
                isduplicate.add(PieceCode);
                PiecesCount = PiecesCount + 1;
                PieceCodeList.add(0, new PieceDetail(PieceCode, WaybillNo, Weight));
                waybillweight = waybillweight + Weight;
                lbTotal.setText(getString(R.string.lbCount) + String.valueOf(isduplicate.size()));
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);

                txtBarcode.setText("");
                txtBarcode.requestFocus();
                //if (PieceCodeList.size() > 5)
                //     PieceCodeList.remove(5);
                initViews();
            }


        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarcode.setText("");
        }

        if (PieceCodeList.size() == 20) {
            SaveData(PieceCodeList, WaybillList);
            //PieceCodeList.clear();
            //WaybillList.clear();
        }
    }


    private BarcodeValidation validateBarcode (String barcode) {

        BarcodeValidation barcodeValidation = new BarcodeValidation();

        if (!GlobalVar.GV().isValidBarcodeCons(barcode)) {
            barcodeValidation.isValid = false;
            barcodeValidation.ErrorMessage = "Wrong Barcode";
            txtBarcode.setText("");
            return barcodeValidation;
        }

        if (isduplicate.contains(barcode)) {
            barcodeValidation.isValid = false;
            barcodeValidation.ErrorMessage = "Duplicate Barcode";
            txtBarcode.setText("");
            return barcodeValidation;
        }

        if (barcode.length() != 13) {
            barcodeValidation.isValid = false;
            barcodeValidation.ErrorMessage = "Wrong Barcode Length";
            txtBarcode.setText("");
            return barcodeValidation;
        }

        return barcodeValidation;

    }


    private void ErrorAlert(final String title, String message, final String PieceCode, final String Waybillno, final double Weight) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "ADD",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AddPiece(PieceCode, Waybillno, Weight);
                        txtBarcode.setText("");
                        txtBarcode.requestFocus();


                    }
                });

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

//                        isduplicate.remove(0);
//                        PiecesCount = PiecesCount - 1;
//                        double tw = PieceCodeList.get(0).Weight;
//                        PieceCodeList.remove(0);
//                        waybillweight = waybillweight - tw;
//                        lbTotal.setText(getString(R.string.lbCount) + String.valueOf(isduplicate.size()));

                        txtBarcode.setText("");
                        txtBarcode.requestFocus();


                    }
                });

        alertDialog.show();
    }

    public ArrayList<String> iscitcshipments = new ArrayList<>();


    private void initViews() {
        recyclerView = (RecyclerView) rootView.findViewById(R.id.shipmentBarCodes);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NclAdapterRemoveValidation_CITC(PieceCodeList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        initSwipe();
    }


    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT)//| ItemTouchHelper.RIGHT)
        {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirm Deleting")
                            .setMessage("Are you sure you want to delete?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int which) {

                                    isduplicate.remove(PieceCodeList.get(position).Barcode);
                                    adapter.removeItem(position);
                                    lbTotal.setText(getString(R.string.lbCount) + adapter.getItemCount());
                                    PiecesCount = PiecesCount - 1;
                                }
                            })
                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    initViews();
                                }
                            })
                            .setCancelable(false);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }


        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }


    public class PieceDetail {
        public String Barcode;
        public String Waybill;
        public double Weight;

        public PieceDetail () {}

        public PieceDetail(String barcode, String waybill, double weight) {
            Barcode = barcode;
            Waybill = waybill;
            Weight = weight;
        }

    }

    public void clearList() {
        initViews();
        PiecesCount = 0;
        isduplicate.clear();
        adapter.clearAll();
        PieceCodeList.clear();
        WaybillList.clear();
        lbTotal.setText(getString(R.string.lbCount) + String.valueOf(PiecesCount));
        txtBarcode.setText("");
    }


    private boolean IsValid() {
        boolean isValid = true;
        if (NclShipmentActivity.NclNo == "0") {
            GlobalVar.GV().ShowSnackbar(rootView, "Please generate Ncl No", GlobalVar.AlertType.Error);
            isValid = false;
        }

        return isValid;
    }

    private void SaveData(ArrayList<PieceDetail> PieceCodeList, ArrayList<String> WaybillList) {

        DBConnections dbConnections = new DBConnections(getContext(), null);
        if (IsValid()) {

            DateTime TimeIn = DateTime.now();
            // Ncl ncl = new Ncl(WaybillList.size(), PieceCodeList.size(), TimeIn, NclShipmentActivity.NclNo);
            ArrayList<String> waybill = new ArrayList<String>();

            Ncl ncl = new Ncl();
            ncl.NclNo = NclShipmentActivity.NclNo;
            ncl.Date = TimeIn;
            ncl.UserID = GlobalVar.GV().UserID;
            ncl.PieceCount = PieceCodeList.size();
            ncl.WaybillCount = waybill.size();
            ncl.IsSync = false;
            ncl.EmployID = GlobalVar.GV().EmployID;
            ncl.StationID = GlobalVar.GV().StationID;

            String Origin[] = ScanNclNoFragment.txtOrgin.getText().toString().split(":");
            String Dest[] = ScanNclNoFragment.txtDestination.getText().toString().split(":");
            if (Origin.length > 1)
                ncl.OrgDest = Origin[0];
            if (Dest.length > 1)
                ncl.OrgDest = ncl.OrgDest + " / " + Dest[0];


            for (int i = 0; i < PieceCodeList.size(); i++) {

                ncl.ncldetails.add(i,
                        new NclDetail(PieceCodeList.get(i).Barcode, 0));
            }

            String jsonData = JsonSerializerDeserializer.serialize(ncl, true);
            jsonData = jsonData.replace("Date(-", "Date(");

            dbConnections.InsertNclBulk(jsonData, getContext(), PieceCodeList.size());

            if (!isMyServiceRunning(NclServiceBulk.class)) {
                getActivity().startService(
                        new Intent(getActivity(), NclServiceBulk.class));

            }

            PieceCodeList.clear();
            WaybillList.clear();
            initViews();
        }
        dbConnections.close();


    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void ReadFromLocal() {

        try {
            DBConnections dbConnections = new DBConnections(getContext(), null);
            Cursor cursor = dbConnections.Fill("select count(*) total from DeliverReq where ReqType = 3 ", getContext());

            Cursor delreq = dbConnections.Fill("select * from DeliverReq where ReqType = 1 Limit 1", getContext());
            if (delreq.getCount() > 0) {
                delreq.moveToFirst();
                try {
                    validupto.setText("Upto : " + delreq.getString(delreq.getColumnIndex("ValidDate")) + " 16:30");
                    inserteddate.setText("DLD : " + delreq.getString(delreq.getColumnIndex("InsertedDate")));
                } catch (Exception e) {
                    System.out.println(e);
                }

            }


            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                txtCitcCount.setText("CITC Count : " + String.valueOf(cursor.getString(cursor.getColumnIndex("total"))));


            }

            cursor.close();
            dbConnections.close();

        } catch (
                Exception e) {
            e.printStackTrace();
        }

    }

    private void onBackpressed() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle("Exit NCL ")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        getActivity().finish();
                    }
                }).setNegativeButton("Cancel", null).setCancelable(false);
        android.support.v7.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    public void onNCLGenerated(String NCLNo , int NCLDestStationID , List<Integer> allowedDestStations) {
        try {
            isNCLDestChosen = true;
            this.NCLDestStationID = NCLDestStationID;
            allowedDestStationIDs = allowedDestStations;
            txtBarcode.setHint("Scan barcode");
            txtBarcode.setEnabled(true);
        } catch (Exception ex) {
            Log.d("test" , TAG + "" + ex.toString());
        }
    }

    private ArrayList<Station> getAllowedDestCode () {
        ArrayList<Station> stationArrayList = new ArrayList<>();
        for (int i = 0 ; i < allowedDestStationIDs.size(); i++) {
            Station tempStation = dbConnections.getStationByID(allowedDestStationIDs.get(i) , getContext());
            if (tempStation != null )
                stationArrayList.add(tempStation);
        }
        return stationArrayList;
    }


    //Check if waybill has RTO Req , Delivery Req ..
    private boolean checkWaybillFlags (String barcode) {
        boolean hasFlag = false;
        try {

            //Read from local online validation table
            //TODO Add CITC
            OnLineValidation onLineValidationLocal = dbConnections.getPieceInformationByBarcode(barcode, getContext());
            // To set flags
            OnLineValidation onLineValidation = new OnLineValidation();

            if (onLineValidationLocal != null) {

                onLineValidation.setWaybillDestID(onLineValidationLocal.getWaybillDestID());
                onLineValidation.setWaybillNo(onLineValidationLocal.getWaybillNo());

                if (NCLDestStationID != 0 && NCLDestStationID != onLineValidationLocal.getWaybillDestID()) {
                    onLineValidation.setIsDestNotBelongToNcl(1);
                    hasFlag = true;
                }
                if (onLineValidationLocal.getIsStopped() == 1) {
                    onLineValidation.setIsStopped(1);
                    hasFlag = true;
                }

                //TODO Riyam to be added.
               /* if (onLineValidationLocal.getIsCITCComplaint() == 1) {
                    onLineValidation.setIsCITCComplaint(1);
                    hasFlag = true;
                }*/

            }
            // Uncomment once scrip updated
            /*else {
                onLineValidation.setNotInFile(true);
                hasFlag = true;
            }*/



            if (ScanNclNoFragment.pieceDenied.contains(barcode)) {
                Log.d("test" , "No Byan");
                onLineValidation.setIsNoBayanNo(1);
                hasFlag = true;
            }

            onLineValidation.setBarcode(barcode);
            onLineValidationList.add(onLineValidation);

        } catch (Exception e) {
            Log.d("test" , TAG + "" + e.toString());
        }

        return hasFlag;
    }

    private OnLineValidation getOnLineValidationPiece (String barcode) {
        try {
            for (OnLineValidation pieceDetail : onLineValidationList) {
                if (pieceDetail.getBarcode().equals(barcode))
                    return pieceDetail;
            }

        } catch (Exception e) {
            Log.d("test" , TAG + " " + e.toString());
        }
        return null;
    }


    public void showFlagsPopup (final OnLineValidation onLineValidation) {
        try {

            try {
                GlobalVar.hideKeyboardFrom(getContext(), rootView);
            } catch (Exception e) {Log.d("test" , TAG + "" + e.toString());}


            if (onLineValidation != null) {

                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);

                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                final TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
                tvBarcode.setText("Piece #" + onLineValidation.getBarcode());
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setText("OK & Quit");

                //If barcode is not in onlineValidation table
                //Uncomment once script is updated
                 /*if (onLineValidation.isNotInFile()) {
                    LinearLayout llDifDest = dialogView.findViewById(R.id.ll_not_manifested);
                    llDifDest.setVisibility(View.VISIBLE);

                    TextView tvNclHeader = dialogView.findViewById(R.id.tv_not_manifested_header);
                    tvNclHeader.setText("Manifest");

                    TextView tvNclBody = dialogView.findViewById(R.id.tv_not_manifested_body);
                    tvNclBody.setText("Shipment is not manifested yet.Online Validation flags won't be available");
                }*/

                 if (onLineValidation.getIsDestNotBelongToNcl() == 1) {

                        String stationName = "";
                        try {
                            Station station = null;
                            station =  dbConnections.getStationByID(onLineValidation.getWaybillDestID() , getContext());

                            if (station != null)
                                stationName = station.Name;
                            else
                                Log.d("test" , TAG + " Station is null");

                        } catch (Exception e) { Log.d("test" , TAG + "" + e.toString());}

                        LinearLayout llDifDest = dialogView.findViewById(R.id.ll_ncl_wrong_dest);
                        llDifDest.setVisibility(View.VISIBLE);

                        TextView tvNclHeader = dialogView.findViewById(R.id.tv_ncl_header);
                        tvNclHeader.setText("NCL Destination");

                        TextView tvNclBody = dialogView.findViewById(R.id.tv_ncl_body);
                        tvNclBody.setText("Piece destination (" + stationName +") doesn’t belong to NCL.");

                        // add on click listener
                        radioGroupCheckListener(dialogView , btnConfirm);

                    }


                 if (onLineValidation.getIsStopped() == 1) {

                        LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                        llStopShipment.setVisibility(View.VISIBLE);

                        TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                        tvStopShipmentHeader.setText("Stop Shipment");

                        TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                        tvStopShipmentBody.setText("Stop shipment , Please hold.");

                    }


                 if (onLineValidation.getIsCITCComplaint() == 1) {

                        LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_citc_complaint);
                        llStopShipment.setVisibility(View.VISIBLE);

                        TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_citc_header);
                        tvStopShipmentHeader.setText("CITC Complaint");

                        TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_citc_body);
                        tvStopShipmentBody.setText("The Shipment has a CITC Complaint.");

                    }


                 if (onLineValidation.getIsNoBayanNo() == 1) {

                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_no_bayan);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_no_bayan_header);
                    tvStopShipmentHeader.setText("Bayan Number");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_no_bayan_body);
                    tvStopShipmentBody.setText("The Shipment has no Bayan number.Kindly contact GateWay team");

                }


                final android.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To avoid leaked window
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();

                            //Don't record if it's high value with no Bayan
                            if (onLineValidation.getIsNoBayanNo() == 1 ){
                                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                                GlobalVar.GV().ShowSnackbar(rootView, "Shipment has no Bayan number.Scan won't be recorded", GlobalVar.AlertType.Warning);                                txtBarcode.getText().clear();
                            }

                            //don't record if dest not belong to NCL & user didn't change the dest
                            //TODO Riyam Uncomment once (Update Waybill Dest) is implemented.
                            else if (onLineValidation.getIsDestNotBelongToNcl() == 1 && !isDestChanged){
                                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                                GlobalVar.GV().ShowSnackbar(rootView, "Shipment destination not belong to NCL.Scan won't be recorded", GlobalVar.AlertType.Warning);                                txtBarcode.getText().clear();
                            }

                            //Dest not belong to NCL & user changed it. Call update waybill Dest API
                            else if (onLineValidation.getIsDestNotBelongToNcl() == 1 && isDestChanged){

                                PieceDetail pieceDetail = new PieceDetail();
                                pieceDetail.Barcode = onLineValidation.getBarcode();
                                pieceDetail.Waybill = "0";
                                pieceDetail.Weight = 0;


                                if (isDestChanged) {
                                    Station station = (Station) searchableSpinnerDest.getSelectedItem();
                                    newDestID = station.ID;
                                    updateWaybillDestination(onLineValidation);
                                    isDestChanged = false; //For next scan
                                }

                            } else { //All valid
                                PieceDetail pieceDetail = new PieceDetail();
                                pieceDetail.Barcode = onLineValidation.getBarcode();
                                pieceDetail.Waybill = "0";
                                pieceDetail.Weight = 0;
                                AddNewPieceTest(pieceDetail);

                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.d("test" , TAG + " " + e.toString());
        }
    }

    private void radioGroupCheckListener(final View v , final Button btnConfirm) {
        final RadioButton rbDiscard = v.findViewById(R.id.rb_change_discard);
        RadioGroup radioGroup = v.findViewById(R.id.rg_change_dest);
        radioGroup.setVisibility(View.VISIBLE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener(){
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.rb_change_dest:
                        btnConfirm.setText("OK & Save");
                        isDestChanged = true;
                        searchableSpinnerDest = (SearchableSpinner) v.findViewById(R.id.spinner_ncl_destinations);
                        searchableSpinnerDest.setVisibility(View.VISIBLE);
                        ArrayAdapter<Station> addressArrayAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, getAllowedDestCode());
                        addressArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        searchableSpinnerDest.setAdapter(addressArrayAdapter);
                        searchableSpinnerDest.setTitle("Change Piece Destination");
                        rbDiscard.setVisibility(View.VISIBLE);

                        break;
                    case R.id.rb_change_discard:
                        btnConfirm.setText("OK & Quit");
                        isDestChanged = false;
                        searchableSpinnerDest.setVisibility(View.GONE);
                        RadioButton rbDiscard = v.findViewById(R.id.rb_change_discard);
                        rbDiscard.setVisibility(View.GONE);
                        break;

                }
            }
        });
    }


    private void updateWaybillDestination (OnLineValidation onLineValidation) {

        //Comment once approved
       /* Toast.makeText(getContext() , "Changing destination is under development" , Toast.LENGTH_LONG).show();
        PieceDetail pieceDetail = new PieceDetail();
        pieceDetail.Barcode = onLineValidation.getBarcode();
        pieceDetail.Waybill = "0";
        pieceDetail.Weight = 0;
        AddNewPieceTest(pieceDetail);*/

        //Uncomment once approved.
       try {
            UpdateWaybillRequest request = new UpdateWaybillRequest();
            request.WaybillNo = onLineValidation.getWaybillNo();
            request.EmployeeID = GlobalVar.GV().EmployID;
            request.Barcode = onLineValidation.getBarcode();
            request.Latitude = "0";
            request.Longitude = "0";
            request.NewWaybillDestID = newDestID;
            request.AppVersion = GlobalVar.GV().AppVersion;

            String jsonData = JsonSerializerDeserializer.serialize(request, true);
            new UpdateWaybill().execute(jsonData);

        } catch (Exception e) {
            Log.d("test" , TAG + "" + e.toString());
        }
    }


    private class UpdateWaybill extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;
        JSONObject requestJsonObject;

        @Override
        protected void onPreExecute() {
            try {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage("Please wait.");
                progressDialog.setTitle("Updating Waybill Destination.");
                progressDialog.show();
            } catch (Exception e ) {
                Log.d("test" , TAG + "" + e.toString());
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];

            try {
                requestJsonObject =  new JSONObject(jsonData);
            } catch (JSONException e) {
                Log.d("test" , TAG + "" + e.toString());
                e.printStackTrace();
            }

            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {

                //TODO RIYAM
                URL url = new URL(GlobalVar.NaqelAPIUAT + "UpdateWaybillDestination");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return String.valueOf(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            try {
                progressDialog.dismiss();
                super.onPostExecute(String.valueOf(finalJson));
                if (finalJson != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(finalJson);
                        if (!jsonObject.getBoolean("HasError")) {

                            String waybillNo = requestJsonObject.getString("WaybillNo");
                            String barcode = requestJsonObject.getString("Barcode");

                            PieceDetail pieceDetail = new PieceDetail();
                            pieceDetail.Barcode = barcode;
                            pieceDetail.Waybill = "0";
                            pieceDetail.Weight = 0;

                            DBConnections dbConnections = new DBConnections(getContext() , null);
                            dbConnections.updateWaybillDestID(getContext() , waybillNo , newDestID);
                            AddNewPieceTest(pieceDetail);
                        } else {
                            GlobalVar.GV().ShowSnackbar(rootView, jsonObject.getString("ErrorMessage"), GlobalVar.AlertType.Error);
                        }

                    } catch (Exception e) {
                        Log.d("test" , TAG + "" + e.toString());
                    }

                } else
                    GlobalVar.GV().ShowSnackbar(rootView, "Something went wrong", GlobalVar.AlertType.Error);
            } catch (Exception e) {
                Log.d("test" , TAG + "" + e.toString());
            }
        }
    }



    /*

        private void AddNewWaybill(String WaybillNo) {
        if (WaybillNo.length() >= 8)
            WaybillNo = WaybillNo.substring(0, 8);
        if (WaybillNo.toString().length() == 8) {
            if (!WaybillList.contains(WaybillNo.toString())) {
                WaybillCount = WaybillCount + 1;
                WaybillList.add(0, WaybillNo.toString());
            }
        }
    }



        private boolean IsValid(BarcodeInfoResult barcodeInfoResult) {

        boolean Result = true;

        NclShipmentActivity nclShipmentActivity = (NclShipmentActivity) getActivity();

        if (nclShipmentActivity.NclNo == "0") {
            Result = false;
            GlobalVar.GV().ShowSnackbar(rootView, "Please Generate Ncl No", GlobalVar.AlertType.Info);
        }
        if (!nclShipmentActivity.destList.contains(barcodeInfoResult.DestId) && !nclShipmentActivity.IsMixed) {
            Result = false;
            GlobalVar.GV().ShowSnackbar(rootView, "Barcode is not belong to this destination", GlobalVar.AlertType.Info);
            GlobalVar.GV().MakeSound(getActivity(), R.raw.wrongbarcodescan);
            txtBarcode.setText("");

        }
        txtBarcode.requestFocus();
        return Result;
    }


    private class BringBarcodeInfo extends AsyncTask<String, Void, String> {
        private ProgressDialog progressDialog;
        String result = "";
        StringBuffer buffer;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {
            String jsonData = params[0];
            HttpURLConnection httpURLConnection = null;
            OutputStream dos = null;
            InputStream ist = null;

            try {
                URL url = new URL(GlobalVar.GV().NaqelPointerAPILink + "GetBarcodeInfo");
                httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.connect();

                dos = httpURLConnection.getOutputStream();
                httpURLConnection.getOutputStream();
                dos.write(jsonData.getBytes());

                ist = httpURLConnection.getInputStream();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(ist));
                buffer = new StringBuffer();

                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return String.valueOf(buffer);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (ist != null)
                        ist.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (httpURLConnection != null)
                    httpURLConnection.disconnect();
                result = String.valueOf(buffer);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String finalJson) {
            //progressDialog.dismiss();
            super.onPostExecute(String.valueOf(finalJson));
            if (finalJson != null) {
                BarcodeInfoResult barcodeInfoResult = new BarcodeInfoResult(finalJson);


                if (IsValid(barcodeInfoResult) && barcodeInfoResult.WayBillNo != 0) {
                    AddNewWaybill(String.valueOf(barcodeInfoResult.WayBillNo));
                    AddNewPiece(String.valueOf(barcodeInfoResult.BarCode), String.valueOf(barcodeInfoResult.WayBillNo), barcodeInfoResult.weight);
                } else {
                    GlobalVar.GV().ShowSnackbar(rootView, "No data with these Barcode", GlobalVar.AlertType.Error);
                    GlobalVar.GV().MakeSound(getActivity(), R.raw.wrongbarcodescan);
                    txtBarcode.setText("");
                    txtBarcode.requestFocus();
                }

            } else {
                GlobalVar.GV().ShowSnackbar(rootView, "No data with these Barcode", GlobalVar.AlertType.Error);
                GlobalVar.GV().MakeSound(getActivity(), R.raw.wrongbarcodescan);
                txtBarcode.setText("");
                txtBarcode.requestFocus();
            }
        }
    }

    private void GetNCLDatafromDB(String Barcode) {

        DBConnections dbConnections = new DBConnections(getContext(), null);
        try {
            Cursor cursor = dbConnections.Fill("select * from DeliverReq where ReqType = 1 and BarCode='" + Barcode + "'", getContext());
            if (cursor.getCount() > 0) {
                isdeliveryReq.clear();
                cursor.moveToFirst();
                do {

                    isdeliveryReq.add(cursor.getString(cursor.getColumnIndex("BarCode")));

                } while (cursor.moveToNext());
            }


            cursor = dbConnections.Fill("select * from DeliverReq where ReqType = 3 and BarCode='" + Barcode + "'", getContext());
            if (cursor.getCount() > 0) {
                iscitcshipments.clear();

                cursor.moveToFirst();
                do {

                    iscitcshipments.add(cursor.getString(cursor.getColumnIndex("BarCode")));
//                    if (cursor.getString(cursor.getColumnIndex("NCLNO")).length() > 0)
//                        isNclCitc.add(cursor.getString(cursor.getColumnIndex("NCLNO")));

                } while (cursor.moveToNext());
            }

            //cursor.close();


            isrtoReq.clear();

            cursor = dbConnections.Fill("select * from RtoReq where BarCode ='" + Barcode + "' ", getContext()); //and BarCode='" + Barcode + "'"
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                do {
                    isrtoReq.add(cursor.getString(cursor.getColumnIndex("BarCode")));
                } while (cursor.moveToNext());
            }

            cursor.close();
            dbConnections.close();


        } catch (
                Exception e) {
            GlobalVar.hideKeyboardFrom(getContext(), rootView);
            GlobalVar.GV().ShowSnackbar(rootView, "Somthing went wrong, kindly scan again",
                    GlobalVar.AlertType.Error);
            GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
            txtBarcode.setText("");
            txtBarcode.requestFocus();
            e.printStackTrace();
        }

    }


    private void removeWaybill(String waybillNo) {
        int index = WaybillList.indexOf(waybillNo);
        WaybillList.remove(index);
    }

    public boolean IsDuplicate(String pieceCode) {
        boolean result = false;
        for (int i = 0; i < PieceCodeList.size(); i++) {
            if (PieceCodeList.get(i).Barcode.equals(pieceCode)) {
                result = true;
                break;
            }
        }
        return result;
    }*/

}
