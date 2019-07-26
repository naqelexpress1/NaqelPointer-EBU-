package com.naqelexpress.naqelpointer.Activity.ArrivedatDestNoValidation;


import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.naqelexpress.naqelpointer.DB.DBConnections;
import com.naqelexpress.naqelpointer.GlobalVar;
import com.naqelexpress.naqelpointer.R;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class Waybill extends Fragment {

    View rootView;
    WaybillAdapter adapter;
    private GridView waybilgrid;
    private EditText txtBarCode;
    static ArrayList<String> validatewaybillist = new ArrayList<>();
    TextView wbScanned, bpScanned, slScanned, wbTotal, bpTotal, slTotal;
    public static TextView tripcode, tripname;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.singleitem_atdest, container, false);

            wbTotal = (TextView) rootView.findViewById(R.id.wbtotal);
            tripcode = (TextView) rootView.findViewById(R.id.tripcode);
            tripname = (TextView) rootView.findViewById(R.id.tripname);
            wbScanned = (TextView) rootView.findViewById(R.id.wbscanned);

            bpTotal = (TextView) rootView.findViewById(R.id.bptotal);
            bpTotal.setText("Count : 0");
            bpScanned = (TextView) rootView.findViewById(R.id.bpscanned);


            slTotal = (TextView) rootView.findViewById(R.id.sltotal);
            slScanned = (TextView) rootView.findViewById(R.id.slscanned);


            txtBarCode = (EditText) rootView.findViewById(R.id.waybillno);

            waybilgrid = (GridView) rootView.findViewById(R.id.waybills);
            adapter = new WaybillAdapter(validatewaybillist, getContext(), getActivity());
            waybilgrid.setAdapter(adapter);
            adapter.notifyDataSetChanged();

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
                        ValidateWayBill(txtBarCode.getText().toString().substring(0, 8));
                }
            });

            ReadFromLocal();

        }

        return rootView;
    }

    private void ReadFromLocal() {
        DBConnections dbConnections = new DBConnections(getContext(), null);
        Cursor result = dbConnections.Fill("select * from AtDestWaybill where TrailerNo = '" + ArrivedatDestination.tripPlanID
                + "'", getContext());
        if (result.getCount() > 0) {
            bpTotal.setText("Count : " + String.valueOf(result.getCount()));
            result.moveToFirst();
            do {
                String waybillno = String.valueOf(result.getInt(result.getColumnIndex("WayBillNo")));
                if (!validatewaybillist.contains(waybillno))
                    validatewaybillist.add(waybillno);


            }
            while (result.moveToNext());
            adapter.notifyDataSetChanged();
        }

        result.close();
        dbConnections.close();
    }

    private void ValidateWayBill(String barcode) {
        if (!validatewaybillist.contains(barcode)) {
            validatewaybillist.add(barcode);
            adapter.notifyDataSetChanged();
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.barcodescanned);
            txtBarCode.setText("");
            SaveWaybilltoLocal(Integer.parseInt(barcode));
            bpTotal.setText("Count : " + String.valueOf(validatewaybillist.size()));

        } else {
            GlobalVar.MakeSound(getActivity().getApplicationContext(), R.raw.wrongbarcodescan);
            txtBarCode.setText("");
        }

    }


    private void SaveWaybilltoLocal(int waybill) {
        DBConnections dbConnections = new DBConnections(getContext(), null);
        dbConnections.InsertAtDestWaybill(waybill, ArrivedatDestination.tripPlanID, getContext());
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

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            txtBarCode.setText(savedInstanceState.getString("txtBarCode"));
            ReadFromLocal();
        }
    }
}