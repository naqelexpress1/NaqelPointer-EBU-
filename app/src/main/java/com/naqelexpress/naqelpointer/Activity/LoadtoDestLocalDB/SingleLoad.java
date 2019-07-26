package com.naqelexpress.naqelpointer.Activity.LoadtoDestLocalDB;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
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
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class SingleLoad extends Fragment {
    View rootView;
    private EditText txtBarCode;
    static TextView lbTotal;
    static DataAdapterForThird adapter;
    private GridView recyclerView;
    static ArrayList<String> ValidateBarCodeList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.loadtodestbpallet, container, false);

            lbTotal = (TextView) rootView.findViewById(R.id.count);


            txtBarCode = (EditText) rootView.findViewById(R.id.txtWaybilll);

            txtBarCode.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (txtBarCode != null && txtBarCode.getText().length() == 13)
                        ValidateWayBill(txtBarCode.getText().toString());
                }
            });

            recyclerView = (GridView) rootView.findViewById(R.id.barcode);
            adapter = new DataAdapterForThird(ValidateBarCodeList, getContext(), getActivity());
            recyclerView.setAdapter(adapter);
        }

        ReadFromLocal();
        return rootView;
    }

    private void ValidateWayBill(String barcode) {

        if (!ValidateBarCodeList.contains(barcode)) {

            DBConnections dbConnections = new DBConnections(getContext(), null);
            dbConnections.InsertLoadtoDestPiece(barcode, LoadtoDestination.triplanID, getContext());
            dbConnections.close();

            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            adapter.notifyDataSetChanged();
            ValidateBarCodeList.add(barcode);
            lbTotal.setText(String.valueOf(ValidateBarCodeList.size()));

        } else
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
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
            lbTotal.setText( String.valueOf(ValidateBarCodeList.size()));
            adapter.notifyDataSetChanged();
        }

        result.close();
        dbConnections.close();
    }
}