package com.naqelexpress.naqelpointer.NCLBulk;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import android.widget.Toast;

import com.naqelexpress.naqelpointer.Classes.JsonSerializerDeserializer;
import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.DB.DBObjects.Ncl;
import com.naqelexpress.naqelpointer.DB.DBObjects.NclDetail;
import com.naqelexpress.naqelpointer.DB.DBObjects.Station;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.JSON.Results.BarcodeInfoResult;
import com.naqelexpress.naqelpointer.OnlineValidation.OnLineValidation;
import com.naqelexpress.naqelpointer.R;
import com.naqelexpress.naqelpointer.service.NclServiceBulk;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.joda.time.DateTime;

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


public class ScanNclWaybillFragmentRemoveValidation_CITC extends Fragment  {

    public EditText txtBarcode;
    public TextView lbNclNo;
    private View rootView;
    private TextView lbTotal, txtCitcCount, inserteddate, validupto;
    private RecyclerView recyclerView;
    private SearchableSpinner searchableSpinnerDest;

    public NclAdapterRemoveValidation_CITC adapter;
    public static double waybillweight = 0.0;

    private int WaybillCount = 0, PiecesCount = 0;
    private boolean isNCLDestChosen , isDestChanged;
    private int NCLDestStationID , newDestID;

    public static ArrayList<String> WaybillList = new ArrayList<>();
    public static ArrayList<PieceDetail> PieceCodeList = new ArrayList<PieceDetail>();
    public ArrayList<String> isrtoReq = new ArrayList<>();
    public ArrayList<String> isdeliveryReq = new ArrayList<>();
    private List<Integer> allowedDestStationIDs = new ArrayList<>();
    private ArrayList<String> isduplicate = new ArrayList<>();


    private DBConnections dbConnections = new DBConnections(getContext(), null);
    private List<OnLineValidation> onLineValidationList = new ArrayList<>();


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
            txtCitcCount.setVisibility(View.VISIBLE);

            inserteddate = (TextView) rootView.findViewById(R.id.inserteddate);
            validupto = (TextView) rootView.findViewById(R.id.validupto);
            lbTotal = (TextView) rootView.findViewById(R.id.lbTotal);
            lbNclNo = (TextView) rootView.findViewById(R.id.lbNclNo);

            LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ll);
            ll.setVisibility(View.VISIBLE);


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

            txtBarcode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtBarcode != null && txtBarcode.getText().toString().length() == 13) {
                        try {
                            if (txtBarcode != null && txtBarcode.getText().toString().length() == 13) {
                                String barcode = txtBarcode.getText().toString();
                                //Todo riyam only if courier || th
                                if (isValidPieceBarcode(barcode)) {
                                    PieceDetail pieceDetail = new PieceDetail();
                                    pieceDetail.Barcode = barcode;
                                    pieceDetail.Waybill = "0";
                                    pieceDetail.Weight = 0;
                                    AddNewPiece(pieceDetail);
                                } else {
                                    showDialog(getOnLineValidationPiece(barcode));
                                }
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            Log.d("test", "ScanNclWaybill" + e.toString());
                        }
                    }

                }
            });

        }

        ReadFromLocal();
        return rootView;
    }


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

//    private void AddNewPiece(String PieceCode, String WaybillNo, double Weight) {
//
//        if (GlobalVar.GV().ValidateAutomacticDate(getContext())) {
//            if (!GlobalVar.GV().IsAllowtoScan(validupto.getText().toString().replace("Upto : ", ""))) { //validupto.getText().toString()
//                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
//                ErrorAlert("Info", "Data is Expired kindly Load today Data , (Press Bring Data)", PieceCode, WaybillNo, Weight);
//                return;
//            }
//        } else {
//            GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
//            GlobalVar.RedirectSettings(getActivity());
//            return;
//        }
//
//        GetNCLDatafromDB(txtBarcode.getText().toString());
//
//        boolean rtoreq = false;
//        boolean ismatch = false;
//
//
//        if (iscitcshipments.contains(txtBarcode.getText().toString())) {
//
//            ismatch = true;
//            GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
//            ErrorAlert("CITC Complaint", "This Waybill Number(" + txtBarcode.getText().toString() + ") has CITC Complaint ", PieceCode, WaybillNo, Weight);
//            return;
//
//        }
//
//        if (!ismatch) {
//            if (isdeliveryReq.contains(txtBarcode.getText().toString())) {
//                if (isrtoReq.contains(txtBarcode.getText().toString())) {
//                    ismatch = true;
//                    rtoreq = true;
//
//                    GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
//                    ErrorAlert("Delivery/RTO Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For Delivery & RTO ", PieceCode, WaybillNo, Weight);
//
//                    return;
//                } else {
//
//                    GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
//                    ErrorAlert("Delivery Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For Delivery ", PieceCode, WaybillNo, Weight);
//                    return;
//                }
//
//
//            }
//        }
//        if (!rtoreq) {
//            if (isrtoReq.contains(txtBarcode.getText().toString())) {
//
//                GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
//                ErrorAlert("RTO Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For RTO ", PieceCode, WaybillNo, Weight);
//                return;
//
//            }
//        }
//
//        GlobalVar.hideKeyboardFrom(getContext(), rootView);
//        AddPiece(PieceCode, WaybillNo, Weight,false , 0);
//    }

    private void AddNewPiece(PieceDetail pieceDetail) {

        if (GlobalVar.GV().ValidateAutomacticDate(getContext())) {
            if (!GlobalVar.GV().IsAllowtoScan(validupto.getText().toString().replace("Upto : ", ""))) { //validupto.getText().toString()
                GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
                ErrorAlert("Info", "Data is Expired kindly Load today Data , (Press Bring Data)", pieceDetail);
                return;
            }
        } else {
            GlobalVar.GV().MakeSound(getContext(), R.raw.wrongbarcodescan);
            GlobalVar.RedirectSettings(getActivity());
            return;
        }

        GetNCLDatafromDB(txtBarcode.getText().toString());

        boolean rtoreq = false;
        boolean ismatch = false;


        if (iscitcshipments.contains(txtBarcode.getText().toString())) {

            ismatch = true;
            GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
            ErrorAlert("CITC Complaint", "This Waybill Number(" + txtBarcode.getText().toString() + ") has CITC Complaint ", pieceDetail);
            return;

        }

        if (!ismatch) {
            if (isdeliveryReq.contains(txtBarcode.getText().toString())) {
                if (isrtoReq.contains(txtBarcode.getText().toString())) {
                    ismatch = true;
                    rtoreq = true;

                    GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
                    ErrorAlert("Delivery/RTO Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For Delivery & RTO ", pieceDetail);

                    return;
                } else {

                    GlobalVar.GV().MakeSound(getContext(), R.raw.delivery);
                    ErrorAlert("Delivery Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For Delivery ", pieceDetail);
                    return;
                }


            }
        }
        if (!rtoreq) {
            if (isrtoReq.contains(txtBarcode.getText().toString())) {

                GlobalVar.GV().MakeSound(getContext(), R.raw.rto);
                ErrorAlert("RTO Request", "This Waybill Number(" + txtBarcode.getText().toString() + ") is Request For RTO ", pieceDetail);
                return;

            }
        }

        GlobalVar.hideKeyboardFrom(getContext(), rootView);
        AddPiece(pieceDetail);
    }

    private void AddPiece(PieceDetail pieceDetail) {
        if (!isduplicate.contains(pieceDetail.Barcode)) {
            if (pieceDetail.Barcode.length() == 13) {

                isduplicate.add(pieceDetail.Barcode);
                PiecesCount = PiecesCount + 1;

               if (pieceDetail.IsDestChanged)
                PieceCodeList.add(0, pieceDetail);
               else
                PieceCodeList.add(0, pieceDetail);

                waybillweight = waybillweight + pieceDetail.Weight;
                lbTotal.setText(getString(R.string.lbCount) + String.valueOf(isduplicate.size()));
                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);

                txtBarcode.setText("");
                txtBarcode.requestFocus();

                initViews();
            }
        } else {
            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
            txtBarcode.setText("");
        }

        if (PieceCodeList.size() >= 20) {
            SaveData(PieceCodeList, WaybillList);
            PieceCodeList.clear();
            WaybillList.clear();
        }
    }

//    private void AddPiece(String PieceCode, String WaybillNo, double Weight , boolean isDestChanged , int newDestID) {
//        if (!isduplicate.contains(PieceCode)) {
//            if (PieceCode.length() == 13) {
//
//                isduplicate.add(PieceCode);
//                PiecesCount = PiecesCount + 1;
//
//                if (isDestChanged)
//                    PieceCodeList.add(0, new PieceDetail(PieceCode, WaybillNo, Weight , isDestChanged , newDestID ));
//                else
//                    PieceCodeList.add(0, new PieceDetail(PieceCode, WaybillNo, Weight , isDestChanged , 0));
//
//                waybillweight = waybillweight + Weight;
//                lbTotal.setText(getString(R.string.lbCount) + String.valueOf(isduplicate.size()));
//                GlobalVar.GV().MakeSound(this.getContext(), R.raw.barcodescanned);
//
//                txtBarcode.setText("");
//                txtBarcode.requestFocus();
//
//                initViews();
//            }
//        } else {
//            GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.AlreadyExists), GlobalVar.AlertType.Warning);
//            GlobalVar.GV().MakeSound(this.getContext(), R.raw.wrongbarcodescan);
//            txtBarcode.setText("");
//        }
//
//        if (PieceCodeList.size() >= 20) {
//            SaveData(PieceCodeList, WaybillList);
//            PieceCodeList.clear();
//            WaybillList.clear();
//        }
//    }


    private void ErrorAlert(final String title, String message, final PieceDetail pieceDetail) {
        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
        alertDialog.setCancelable(false);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "ADD",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        AddPiece(pieceDetail);
                        txtBarcode.setText("");
                        txtBarcode.requestFocus();
                    }
                });

        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        txtBarcode.setText("");
                        txtBarcode.requestFocus();


                    }
                });

        alertDialog.show();
    }
//    private void ErrorAlert(final String title, String message, final String PieceCode, final String Waybillno, final double Weight) {
//        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(getActivity()).create();
//        alertDialog.setCancelable(false);
//        alertDialog.setTitle(title);
//        alertDialog.setMessage(message);
//        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE, "ADD",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        AddPiece(PieceCode, Waybillno, Weight,false , 0);
//                        txtBarcode.setText("");
//                        txtBarcode.requestFocus();
//                    }
//                });
//
//        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEGATIVE, "No",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        txtBarcode.setText("");
//                        txtBarcode.requestFocus();
//
//
//                    }
//                });
//
//        alertDialog.show();
//    }

    public ArrayList<String> iscitcshipments = new ArrayList<>();

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

                } while (cursor.moveToNext());
            }

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

            boolean isinsert = dbConnections.InsertNclBulk(jsonData, getContext(), PieceCodeList.size());

            if (isinsert) {
                if (!isMyServiceRunning(NclServiceBulk.class)) {
                    getActivity().startService(
                            new Intent(getActivity(), NclServiceBulk.class));

                }

                PieceCodeList.clear();
                WaybillList.clear();
                initViews();
            }
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

    public void onNCLGenerated(String NCLNo , int NCLDestStationID ) {
      try {
          isNCLDestChosen = true;
          this.NCLDestStationID = NCLDestStationID;
          txtBarcode.setHint("Scan barcode");
          txtBarcode.setEnabled(true);
      } catch (Exception ex) {}
    }


  //  public void onNCLGenerated(String NCLNo , int NCLDestStationID , List<Integer> allowedDestStations) {
     /* try {
          isNCLDestChosen = true;
          this.NCLDestStationID = NCLDestStationID;
          allowedDestStationIDs = allowedDestStations;
          txtBarcode.setHint("Scan barcode");
          txtBarcode.setEnabled(true);
      } catch (Exception ex) {} */
  //  }

   /* private ArrayList<Station> getAllowedDestCode () {
        ArrayList<Station> stationArrayList = new ArrayList<>();
        for (int i = 0 ; i < allowedDestStationIDs.size(); i++) {
            Station tempStation = dbConnections.getStationByID(allowedDestStationIDs.get(i) , getContext());
             if (tempStation != null )
                 stationArrayList.add(tempStation);
        }
        return stationArrayList;
    }*/


    private boolean isValidPieceBarcode(String pieceBarcode) {
        boolean isValid = true;
        try {
            OnLineValidation onLineValidationLocal = dbConnections.getPieceInformationByBarcode(pieceBarcode, getContext());
            OnLineValidation onLineValidation = new OnLineValidation();

            if (onLineValidationLocal != null) {

                if (NCLDestStationID != 0 && NCLDestStationID != onLineValidationLocal.getDestID()) {
                    Log.d("test" , "isValidPieceBarcode() Dest not belongs to NCL");
                    onLineValidation.setIsDestNotBelongToNcl(1);
                    isValid = false;
                }
                if (onLineValidationLocal.getIsStopShipment() == 1) {
                    Log.d("test" , "isValidPieceBarcode() Stop shipment");
                    onLineValidation.setIsStopShipment(1);
                    isValid = false;
                }

                if (!isValid) {
                    onLineValidation.setPieceBarcode(pieceBarcode);
                    onLineValidationList.add(onLineValidation);
                }
                return isValid;
            }

        } catch (Exception e) {
            Log.d("test" , "isValidPieceBarcode " + e.toString());
        }
        return isValid;
    }


    public void showDialog(final OnLineValidation pieceDetails) {
        try {
            if (pieceDetails != null) {
                final android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getContext());
                LayoutInflater inflater = this.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.custom_alert_dialog, null);
                dialogBuilder.setView(dialogView);
                dialogBuilder.setCancelable(false);

                final TextView tvBarcode = dialogView.findViewById(R.id.tv_barcode);
                tvBarcode.setText("Piece #" + pieceDetails.getPieceBarcode());
                Button btnConfirm = dialogView.findViewById(R.id.btn_confirm);
                btnConfirm.setVisibility(View.VISIBLE);
                btnConfirm.setText("OK & Quit");

                if (pieceDetails.getIsDestNotBelongToNcl() == 1) {

                    LinearLayout llDifDest = dialogView.findViewById(R.id.ll_ncl_wrong_dest);
                    llDifDest.setVisibility(View.VISIBLE);

                    TextView tvNclHeader = dialogView.findViewById(R.id.tv_ncl_header);
                    tvNclHeader.setText("NCL Destination");

                    TextView tvNclBody = dialogView.findViewById(R.id.tv_ncl_body);
                    tvNclBody.setText("Piece destination doesn’t belong to NCL.");

                    // add on click listener
                   // radioGroupCheckListener(dialogView , btnConfirm);

                }


                if (pieceDetails.getIsStopShipment() == 1) {

                    LinearLayout llStopShipment = dialogView.findViewById(R.id.ll_is_stop_shipment);
                    llStopShipment.setVisibility(View.VISIBLE);

                    TextView tvStopShipmentHeader = dialogView.findViewById(R.id.tv_stop_shipment_header);
                    tvStopShipmentHeader.setText("Stop Shipment");

                    TextView tvStopShipmentBody = dialogView.findViewById(R.id.tv_stop_shipment_body);
                    tvStopShipmentBody.setText("Stop shipment , Please hold.");

                }



                final android.app.AlertDialog alertDialog = dialogBuilder.create();
                alertDialog.show();

                btnConfirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // To avoid leaked window
                        if (alertDialog != null && alertDialog.isShowing()) {
                            alertDialog.dismiss();
                            int DestID = 0;
                            if (pieceDetails.getIsDestNotBelongToNcl() == 1 && isDestChanged) {
                                Station station = (Station) searchableSpinnerDest.getSelectedItem();
                                DestID = station.ID; // New ID
                                PieceDetail pieceDetail = new PieceDetail();
                                pieceDetail.Barcode = pieceDetails.getPieceBarcode();
                                pieceDetail.Waybill = "0";
                                pieceDetail.Weight = 0;
                                pieceDetail.IsDestChanged = true;
                                pieceDetail.DestinationStationID = DestID;
                                AddNewPiece(pieceDetail);
                                isDestChanged = false;

                            } else if (pieceDetails.getIsDestNotBelongToNcl() == 0) {
                                PieceDetail pieceDetail = new PieceDetail();
                                pieceDetail.Barcode = pieceDetails.getPieceBarcode();
                                pieceDetail.Waybill = "0";
                                pieceDetail.Weight = 0;
                                pieceDetail.IsDestChanged = false;
                                pieceDetail.DestinationStationID = 0;
                                AddNewPiece(pieceDetail);
                            } else {
                                PieceDetail pieceDetail = new PieceDetail();
                                pieceDetail.Barcode = pieceDetails.getPieceBarcode();
                                pieceDetail.Waybill = "0";
                                pieceDetail.Weight = 0;
                                pieceDetail.IsDestChanged = false;
                                pieceDetail.DestinationStationID = 0;
                                AddNewPiece(pieceDetail);
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.d("test" , "showDialog " + e.toString());
        }
    }

    private OnLineValidation getOnLineValidationPiece (String barcode) {
        try {
            for (OnLineValidation pieceDetail : onLineValidationList) {
                if (pieceDetail.getPieceBarcode().equals(barcode))
                    return pieceDetail;
            }

        } catch (Exception e) {
            Log.d("test" , "getOnLineValidationPiece " + e.toString());
        }
        return null;
    }


 /*   private void radioGroupCheckListener(final View v , final Button btnConfirm) {
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
    }*/


    public class PieceDetail {
        public String Barcode;
        public String Waybill;
        public double Weight;
        public boolean IsDestChanged;
        public int DestinationStationID;

        public PieceDetail () {}

        public PieceDetail(String barcode, String waybill, double weight) {
            Barcode = barcode;
            Waybill = waybill;
            Weight = weight;
        }
        public PieceDetail(String barcode, String waybill, double weight , boolean isDestChanged , int destID) {
            Barcode = barcode;
            Waybill = waybill;
            Weight = weight;
            IsDestChanged = isDestChanged;
            DestinationStationID = destID;
        }
    }


/*
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
    }
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
    private void updateAllowedDesList(int nclDestStationID) {

        if (allowedDestStationIDs != null && allowedDestStationIDs.size() > 0)
            allowedDestStationIDs.clear();

        allowedDestStationIDs = dbConnections.getAllowedNclStationDestIDs(nclDestStationID, getContext());
    } */

}
