package com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.naqelexpress.naqelpointer.Classes.NewBarCodeScanner;
import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

public class SingleLoad extends Fragment {
    View rootView;
    private EditText txtBarCode;
    static TextView lbTotal;
    static DataAdapterForThird adapter;
    private GridView recyclerView;
    static ArrayList<String> ValidateBarCodeList = new ArrayList<>();
    private Intent intent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.loadtodestbpallet, container, false);

            lbTotal = (TextView) rootView.findViewById(R.id.count);


            txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);
//            txtBarCode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanBarcodeLength)});

            txtBarCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtBarCode != null && txtBarCode.getText().length() >= 8)
                        setBarcode();
                }
            });

            recyclerView = (GridView) rootView.findViewById(R.id.barcode);
            adapter = new DataAdapterForThird(ValidateBarCodeList, getContext(), getActivity());
            recyclerView.setAdapter(adapter);
        }
        Button btnOpenCamera = (Button) rootView.findViewById(R.id.btnOpenCamera);

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    GlobalVar.GV().ShowSnackbar(rootView, getString(R.string.NeedCameraPermission), GlobalVar.AlertType.Error);
                    GlobalVar.GV().askPermission(getActivity(), GlobalVar.PermissionType.Camera);
                } else{
                    Intent intent = new Intent(getContext().getApplicationContext(), NewBarCodeScanner.class);
                    getActivity().startActivityForResult(intent, 2);
                }

            }
        });
        ReadFromLocal();
        return rootView;
    }

    private void setBarcode() {

        String barcode = txtBarCode.getText().toString();
//        utilities utilities = new utilities();
//        ValidateWayBill(utilities.findwaybillno(barcode));
        ValidateWayBill(barcode);

//        txtBarCode.setText(txtBarCode.getText().toString().substring(0, 8));
//        ValidateWayBill(txtBarCode.getText().toString());
    }

    private void ValidateWayBill(String barcode) {

        if (!ValidateBarCodeList.contains(barcode)) {

            DBConnections dbConnections = new DBConnections(getContext(), null);
            dbConnections.InsertLoadtoDestPiece(barcode, LoadtoDestination.triplanID, getContext());
            dbConnections.close();

            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
//            txtBarCode.setText("");
            adapter.notifyDataSetChanged();
            ValidateBarCodeList.add(barcode);
            lbTotal.setText(String.valueOf(ValidateBarCodeList.size()));

        } else {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
//            txtBarCode.setText("");
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2 && resultCode == RESULT_OK) {
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

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("txtBarCode", txtBarCode.getText().toString());
        outState.putString("waybillcount", lbTotal.getText().toString());
        outState.putStringArrayList("thisValidateBarCodeList", ValidateBarCodeList);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            lbTotal.setText(savedInstanceState.getString("waybillcount"));
            ValidateBarCodeList = savedInstanceState.getStringArrayList("thisValidateBarCodeList");
        }
    }

    private void ReadFromLocal() {
        ValidateBarCodeList.clear();
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from LoadtoDestLastPiece where TrailerNo = '" + LoadtoDestination.triplanID
                + "'", getContext());
        if (result.getCount() > 0) {
            result.moveToFirst();
            do {
                String waybillno = String.valueOf(result.getString(result.getColumnIndex("BarCode")));
                if (!ValidateBarCodeList.contains(waybillno))
                    ValidateBarCodeList.add(waybillno);


            }
            while (result.moveToNext());
            lbTotal.setText(String.valueOf(ValidateBarCodeList.size()));
            adapter.notifyDataSetChanged();
        }

        result.close();
        dbConnections.close();
    }
}