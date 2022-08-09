package com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class WayBillDetails extends Fragment // implements ResultInterface
{
    View rootView;
    private EditText txtBarCode;
    static TextView waybillcount;
    static CourierAdapterNew adapter;
    private GridView waybilgrid;
    static TextView tripname, tripcode;
    private Intent intent;

    //static ArrayList<HashMap<String, String>> waybilldetails = new ArrayList<>();
    public static ArrayList<String> validatewaybilldetails = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {

                rootView = inflater.inflate(R.layout.loadtodest, container, false);

                waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
                adapter = new CourierAdapterNew(validatewaybilldetails, getContext(), getActivity());
                waybilgrid.setAdapter(adapter);

                tripname = (TextView) rootView.findViewById(R.id.tripname);
                tripcode = (TextView) rootView.findViewById(R.id.tripcode);

                waybillcount = (TextView) rootView.findViewById(R.id.waybillcount);
                txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);

//                txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanWaybillLength)});

                txtBarCode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }


                    @Override
                    public void afterTextChanged(Editable s) {
                        if (txtBarCode != null && txtBarCode.getText().length() >= 8){
                            String code = txtBarCode.getText().toString();
                            setTxtWaybillNo(code);
                        }
                    }
                });
            }

            Button cmrabtn = (Button) rootView.findViewById(R.id.btnOpenCamera);
            //cmrabtn.setVisibility(View.GONE);



            cmrabtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                        GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                    } else
                    {
                        Intent newIntent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                        getActivity().startActivityForResult(newIntent, 1);
                    }
                }
            });
            ReadFromLocal();
            return rootView;
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("SECFRAG", "AtOriginUsingLocalDB/SecondFragment: ");
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        txtBarCode.setText(barcode);

                    }
                }
            }
        }
    }

    private void setTxtWaybillNo(String barcode) {
//        String barcode = txtBarCode.getText().toString();
//        utilities utilities = new utilities();
//        ValidateWayBill(utilities.findwaybillno(barcode));
        ValidateWayBill(barcode);

    }

    private void ValidateWayBill(String waybillno) {
        if (!validatewaybilldetails.contains(waybillno)) {
            DBConnections dbConnections = new DBConnections(getContext(), null);
            dbConnections.InsertLoadtoDestWaybill(waybillno, LoadtoDestination.triplanID, getContext());
            dbConnections.close();

            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
//            txtBarCode.setText("");
            adapter.notifyDataSetChanged();
            validatewaybilldetails.add(waybillno);
            waybillcount.setText(getString(R.string.lbCount) + validatewaybilldetails.size());

        } else {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
//            txtBarCode.setText("");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("validatewaybilldetails", validatewaybilldetails);
        outState.putString("waybillcount", waybillcount.getText().toString());
        outState.putString("tripname", tripname.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {

            validatewaybilldetails = savedInstanceState.getStringArrayList("validatewaybilldetails");
            waybillcount.setText(savedInstanceState.getString("waybillcount"));
            tripname.setText(savedInstanceState.getString("tripname"));
        }
    }

    private void ReadFromLocal() {
        validatewaybilldetails.clear();
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from LoadtoDestLastWayBill where TrailerNo = '" + LoadtoDestination.triplanID
                + "'", getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                String waybillno = String.valueOf(result.getString(result.getColumnIndex("WaybillNo")));
                if (!validatewaybilldetails.contains(waybillno))
                    validatewaybilldetails.add(waybillno);


            }
            while (result.moveToNext());
            adapter.notifyDataSetChanged();
            waybillcount.setText("Count : " + String.valueOf(result.getCount()));
        }

        result.close();
        dbConnections.close();
    }
}