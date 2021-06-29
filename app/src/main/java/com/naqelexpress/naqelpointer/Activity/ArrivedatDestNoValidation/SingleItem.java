package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNoValidation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;


public class SingleItem extends Fragment {

    EditText palletbarcode;
    View rootView;
    public SingleItemAdapter adapter;
    static ArrayList<String> ValidateBarCodeList = new ArrayList<>();
    private GridView waybilgrid;
    TextView bpTotal;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        {
            if (rootView == null) {
                rootView = inflater.inflate(R.layout.arivedatdestpallet, container, false);

                waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
                bpTotal = (TextView) rootView.findViewById(R.id.bptotal);
                bpTotal.setText("Count : 0");
                adapter = new SingleItemAdapter(ValidateBarCodeList, getContext(), getActivity());
                waybilgrid.setAdapter(adapter);

                palletbarcode = (EditText) rootView.findViewById(R.id.palletbarcode);
                palletbarcode.setFilters(new InputFilter[]{new InputFilter.LengthFilter(GlobalVar.ScanBarcodeLength)});

                palletbarcode.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (palletbarcode != null && palletbarcode.getText().length() >= 13)
//                            ValidatePallet(palletbarcode.getText().toString());
                            setBarcode();
                    }
                });
            }

            ReadFromLocal();
            return rootView;
        }
    }

    private void setBarcode() {
        if (palletbarcode.getText().length() >= 13) {
            //txtBarCode.setText(barcode.substring(0, 8));
            ValidatePallet(palletbarcode.getText().toString());

        }

        //ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));


    }


    private void ValidatePallet(String barcode) {
        if (!ValidateBarCodeList.contains(barcode)) {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            palletbarcode.setText("");
            adapter.notifyDataSetChanged();
            ValidateBarCodeList.add(barcode);
            SaveBarcodetoLocal(barcode);
            bpTotal.setText("Count : " + String.valueOf(ValidateBarCodeList.size()));

        } else {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
            palletbarcode.setText("");
        }
    }

    private void SaveBarcodetoLocal(String barcode) {
        DBConnections dbConnections = new DBConnections(getContext(), null);
        dbConnections.InsertAtDestPieces(barcode, ArrivedatDestination.tripPlanID, getContext());
        dbConnections.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GlobalVar.GV().CAMERA_PERMISSION_REQUEST && resultCode == RESULT_OK) {
            if (data != null) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    if (extras.containsKey("barcode")) {
                        String barcode = extras.getString("barcode");
                        palletbarcode.setText(barcode);
                    }
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("palletbarcode", palletbarcode.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            ReadFromLocal();
        }
    }

    private void ReadFromLocal() {
        DBConnections dbConnections = new DBConnections(getContext(), null);
        // dbConnections.deleteArrivedatDestPieces(getContext());
        Cursor result = dbConnections.Fill("select * from AtDestPiece where TrailerNo = '" + ArrivedatDestination.tripPlanID
                + "'", getContext());
        if (result.getCount() > 0) {
            bpTotal.setText("Count : " + String.valueOf(result.getCount()));
            result.moveToFirst();
            do {
                String waybillno = String.valueOf(result.getString(result.getColumnIndex("BarCode")));
                if (!ValidateBarCodeList.contains(waybillno))
                    ValidateBarCodeList.add(waybillno);


            }
            while (result.moveToNext());
            adapter.notifyDataSetChanged();
        }

        result.close();
        dbConnections.close();
    }

}